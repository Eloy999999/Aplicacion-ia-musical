import json
import sys
import music21

MAPA_ROMANOS = {
    0: "0", 1: "I", 2: "II", 3: "III", 4: "IV", 5: "V", 6: "VI",
    7: "VII", 8: "VIII", 9: "IX", 10: "X", 11: "XI", 12: "XII"
}

# Mapeo para números en círculo
MAPA_CUERDA_CIRCULO = {
    1: "①", 2: "②", 3: "③", 4: "④", 5: "⑤", 6: "⑥"
}

def entero_a_romano(num): # Pasa el valor del numero dado a su numero romano correspondiente
    try:
        val = int(num)
        return MAPA_ROMANOS.get(val, str(num))
    except (ValueError, TypeError):
        return str(num)

def digitarPartitura(): # Parsea el JSON recibido y digita la partitura y la exporta a otro xml
    json_raw = sys.stdin.readline() # Saca el JSON
    if not json_raw.strip():
        sys.exit(1)

    datosjson = json.loads(json_raw) # Saca los datos del JSON
    digitaciones = datosjson.get("digitaciones", [])
    archivo_in = datosjson.get("archivo_in", "")
    archivo_out = datosjson.get("archivo_out", "")

    try:
        score = music21.converter.parse(archivo_in) # Parsea la partitura para obtener sus datos y poder modificarlos

        for p in score.getElementsByClass('Part'): # Cada instrumento de cada pista debe ser guitarra, asi se asegura que no hay fallos de incompatibilidad de instrumentos en digitacion
            p.partName = "Guitarra"
            p.partAbbreviation = "Gtr."
            insts = list(p.getElementsByClass('Instrument'))
            for inst in insts:
                p.remove(inst)
            p.insert(0, music21.instrument.AcousticGuitar())

        notas_y_acordes = [el for el in score.recurse().notes if el.isNote or el.isChord] # Notas y acordes de la partitura
        total_digitaciones = len(digitaciones)

        for i, el in enumerate(notas_y_acordes):
            if i >= total_digitaciones:
                break

            val_digitacion = str(digitaciones[i]) # Obtener valores de la digitacion actual
            partes = [p.strip() for p in val_digitacion.split(",")]

            cuerda = partes[0] if len(partes) > 0 else ""
            traste = partes[1] if len(partes) > 1 else ""
            dedo_izq = partes[2] if len(partes) > 2 else ""
            dedo_der = partes[3] if len(partes) > 3 else ""

            target_note = el if isinstance(el, music21.note.Note) else el.notes[0]

            # ---  DEDO IZQUIERDO (Arriba, numero normal) ---
            if dedo_izq and dedo_izq != "0":
                f_lh = music21.articulations.Fingering(dedo_izq) # Crear y aniadir digitacion
                f_lh.placement = "above"
                target_note.articulations.append(f_lh)

            # --- TRASTE (Arriba, numero romano) ---
            if traste and traste.isdigit() and int(traste) > 0:
                s_ind = music21.articulations.StringIndication(int(traste)) # Crear y aniadir digitacion
                s_ind.placement = "above"
                target_note.articulations.append(s_ind)
#                texto_traste = music21.expressions.TextExpression(entero_a_romano(traste)) # O simplemente '5'
#                texto_traste.style.placement = 'above' # Posición: encima del pentagrama

                # Asignarlo a las expresiones de la nota
#                target_note.expressions.append(texto_traste)

            # --- CUERDA EN CIRCULO Y MANO DERECHA (Abajo) ---
            elementos_abajo = []
            
            if cuerda and cuerda.isdigit(): # Convierte el numero de cuerda al simbolo en circulo
                num_c = int(cuerda)
                if num_c in MAPA_CUERDA_CIRCULO:
                    elementos_abajo.append(MAPA_CUERDA_CIRCULO[num_c])
            
            if dedo_der: # Aniadir cuerda y despues dedo derecho
                elementos_abajo.append(dedo_der)

            if elementos_abajo: # Que aparezca en partitura cuerda y dedo derecho
                texto_abajo = " ".join(elementos_abajo)
                lyr = music21.note.Lyric(text=texto_abajo)
                # lyr.style.fontSize = 8
                target_note.lyrics.append(lyr)

        score.write('musicxml', fp=archivo_out)
        print(f"Exito: Guardado en {archivo_out}")

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    digitarPartitura()