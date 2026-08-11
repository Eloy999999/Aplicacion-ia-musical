import os
import subprocess
import re
from music21 import converter, common, instrument, note, chord

def obtener_pista_guitarra(score):
    partes = score.parts # obtenemos todas las pistas (cada una corresponde a un instrumento diferente)
    for parte in partes:
        inst = parte.getInstruments()[0] if parte.getInstruments() else None
        if inst: # si la pista tiene instrumento principal y este es guitarra, la devolvemos. Se comprueba por clase de libreria, numero de programa midi y nombre de instrumento por si no viene de alguna forma el instrumento, siempre se pueda identificar de otra
            if isinstance(inst, (instrument.AcousticGuitar, instrument.Guitar)) or \
                (inst.midiProgram in [24, 25]) or \
                ("guitar" in (inst.instrumentName or "").lower()):
                return parte
    return partes[0] if partes else score

def construir_lilypond_directo(pista, lista_anotaciones):
    """Genera código LilyPond nativo asignando anotaciones directamente nota por nota."""
    elementos = pista.flatten().notesAndRests
    lineas_notas = []
    idx_anotacion = 0

    for el in elementos:
        if isinstance(el, note.Rest): # Silencios
            dur = el.duration.components[0] if el.duration.components else None
            tipo_dur = el.duration.type # tipo de silencio (cuarta, octava,...)
            mapa_dur = {'quarter': '4', 'half': '2', 'whole': '1', 'eighth': '8', '16th': '16', '32nd': '32'}
            dur_str = mapa_dur.get(tipo_dur, '4') # tipo de silencio mapeado a su valor
            if el.duration.dots > 0: # añadimos tantos puntillos como tenga la nota (si tiene) [cada punto aniade 0,5 veces la duracion  de la nota y despues el punto anterior]
                dur_str += "." * el.duration.dots
            lineas_notas.append(f"r{dur_str}") # mete el silencio en lineas_notas

        elif isinstance(el, (note.Note, chord.Chord)): # Acordes o notas
            target_note = el.notes[0] if isinstance(el, chord.Chord) else el # nota directa o primera nota si es acorde
            nombre_pitch = target_note.pitch.name.lower().replace('#', 'is').replace('-', 'es') # sostenido (#) es is y bemol (-) es es en lilypond
            octava = target_note.pitch.octave
            
            if octava >= 4: # Lilypond aniade ' cada octava mas alta de la 3 y , cada una debajo de la 3
                pitch_str = nombre_pitch + ("'" * (octava - 3))
            else:
                pitch_str = nombre_pitch + ("," * (3 - octava))

            tipo_dur = el.duration.type # Tipo de nota
            mapa_dur = {'quarter': '4', 'half': '2', 'whole': '1', 'eighth': '8', '16th': '16', '32nd': '32'}
            dur_str = mapa_dur.get(tipo_dur, '4')
            if el.duration.dots > 0:
                dur_str += "." * el.duration.dots

            nota_ly = f"{pitch_str}{dur_str}" # Ensamblar nota (pitch + duracion)

            if idx_anotacion < len(lista_anotaciones): # Aniadir digitacion a la nota
                anot = lista_anotaciones[idx_anotacion]
                if anot and len(anot) >= 2:
                    cuerda, traste = int(anot[0]), int(anot[1])
                    traste_romano = "0" if traste == 0 else common.toRoman(traste)
                    
                    nota_ly += f'^\markup{{\\bold "{traste_romano}"}} _\markup{{\\circle "{cuerda}"}}' # ^ = colocar encima de nota | _ = colocar debajo de nota [la digitacion]
                idx_anotacion += 1

            lineas_notas.append(nota_ly)

    return " ".join(lineas_notas)

def exportar_a_pdf_directo(codigo_musica, ruta_pdf, titulo):
    user_home = os.path.expanduser("~")
    lilypond_exe = os.path.join(user_home, "Downloads", "lilypond-2.26.0", "bin", "lilypond.exe").replace("\\", "/") # Localizacion de lilypong.exe para ejecutarlo

    if not os.path.exists(lilypond_exe):
        print(f"Error: No se encontró LilyPond en:\n{lilypond_exe}")
        return False

    directorio_actual = os.path.dirname(os.path.abspath(__file__))
    archivo_ly = os.path.join(directorio_actual, "temp_score.ly").replace("\\", "/")
    nombre_base_salida = os.path.splitext(ruta_pdf)[0] # sin .pdf

    plantilla_ly = f"""\\version "2.26.0"

\\header {{
    title = \\markup \\fontsize #3 \\bold "{titulo}"
    tagline = ##f
}}

#(set-global-staff-size 17)

\\paper {{
    paper-width = 210\\mm
    paper-height = 297\\mm
    top-margin = 10\\mm
    bottom-margin = 10\\mm
    left-margin = 10\\mm
    right-margin = 10\\mm
    indent = 0\\mm
    
    page-breaking = #ly:minimal-breaking
    system-system-spacing.basic-distance = #10
    system-system-spacing.minimum-distance = #6
    system-system-spacing.padding = #2
    
    ragged-bottom = ##t
    ragged-last-bottom = ##t
}}

\\layout {{
    \\context {{
    \\Score
    \\override NonMusicalPaperColumn.page-break-permission = ##f
    \\override NonMusicalPaperColumn.line-break-permission = #'allow
    }}
}}

\\absolute {{
    \\clef treble
    \\key c \\major
    \\time 4/4
    {codigo_musica}
}}
"""

    with open(archivo_ly, "w", encoding="utf-8") as f:
        f.write(plantilla_ly)

    cmd_pdf = [lilypond_exe, "-fpdf", "-o", nombre_base_salida, archivo_ly]
    subprocess.run(cmd_pdf, capture_output=True, text=True, encoding='utf-8', errors='ignore')

    if os.path.exists(archivo_ly):
        try:
            os.remove(archivo_ly)
        except Exception:
            pass

    return os.path.exists(ruta_pdf) and os.path.getsize(ruta_pdf) > 0

def procesar_digitacion(nombre_archivo_midi, lista_anotaciones):
    directorio_actual = os.path.dirname(os.path.abspath(__file__))
    ruta_entrada = os.path.join(directorio_actual, nombre_archivo_midi).replace("\\", "/")

    if not os.path.exists(ruta_entrada):
        print(f"Error: No se encontró el archivo '{nombre_archivo_midi}'")
        return

    print(f"1. Leyendo MIDI '{nombre_archivo_midi}'...")
    score_original = converter.parse(ruta_entrada)
    pista_guitarra = obtener_pista_guitarra(score_original) # obtiene la pista de guitarra del MIDI (puede contener otras de otros instrumentos)

    print("2. Construyendo partitura LilyPond nativa con anotaciones...")
    codigo_musica = construir_lilypond_directo(pista_guitarra, lista_anotaciones) # Obtiene el código para generar la partitura entendible por lilypond

    nombre_base = os.path.splitext(nombre_archivo_midi)[0] # Anotar la salida
    ruta_pdf_digitado = os.path.join(directorio_actual, f"{nombre_base}_digitado.pdf").replace("\\", "/")

    print("3. Generando PDF...")
    exito = exportar_a_pdf_directo(codigo_musica, ruta_pdf_digitado, titulo=nombre_base)

    if exito:
        print(f"\nPartitura PDF generada correctamente en:\n{ruta_pdf_digitado}")
    else:
        print("\nError al generar el PDF.")

if __name__ == "__main__":
    anotaciones_ejemplo = [
        (1, 3), (1, 1), (2, 3), (2, 1), (3, 2), (3, 0)
    ]
    procesar_digitacion("pruebamidi.mid", anotaciones_ejemplo)