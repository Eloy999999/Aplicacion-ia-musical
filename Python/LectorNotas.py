import music21
import json
import sys

NOTAS_MAP = { # Mapeo de notación anglófona a latina
    'C': 'do',
    'D': 're',
    'E': 'mi',
    'F': 'fa',
    'G': 'sol',
    'A': 'la',
    'B': 'si'
}

def convertir_a_solfeggio(pitch: music21.pitch.Pitch) -> str:
    """
    Convierte un objeto Pitch de music21 a notación latina
    """
    nombre_ingles = pitch.step
    nombre_latino = NOTAS_MAP.get(nombre_ingles, nombre_ingles)
    
    alteracion = pitch.accidental.modifier if pitch.accidental else ""
    octava = str(pitch.octave) if pitch.octave is not None else ""
    
    return f"{nombre_latino}{alteracion}{octava}"

def archivo_a_notas(nombre_archivo: str) -> list[str]:
    """
    Lee un archivo de música (MIDI o MusicXML) y extrae los nombres de las notas en notacion latina (Do, Re, Mi...).
    """
    notas: list[str] = []

    score = music21.converter.parse(nombre_archivo)
    elementos = score.flatten().notes

    for elemento in elementos:
        if isinstance(elemento, music21.note.Note):
            notas.append(convertir_a_solfeggio(elemento.pitch))
            
        elif isinstance(elemento, music21.chord.Chord): # Para acordes, convertimos cada nota del acorde
            notas_acorde = ".".join(convertir_a_solfeggio(p) for p in elemento.pitches)
            notas.append(notas_acorde)

    return notas

if __name__ == "__main__": # Pasamos ruta del midi / musicxml recibido, sus notas/acordes y cuantos tiene en un JSON
    archivo_midi = sys.argv[1] if len(sys.argv) > 1 else print("Error con el archivo")
    
    lista_notas = archivo_a_notas(archivo_midi)
    
    datos = { # Encapsulamos la informacion dentro de un diccionario para un JSON bien estructurado
        "archivo": archivo_midi,
        "total_notas": len(lista_notas),
        "notas": lista_notas
    }
    
    print(json.dumps(datos))
    sys.stdout.flush()