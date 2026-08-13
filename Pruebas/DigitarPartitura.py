import json
import sys
import music21

MAPA_ROMANOS = { # Mapa global de enteros a numeros romanos (del 0 al 36), para la digitacion de los trastes
    0: "0",
    1: "I",
    2: "II",
    3: "III",
    4: "IV",
    5: "V",
    6: "VI",
    7: "VII",
    8: "VIII",
    9: "IX",
    10: "X",
    11: "XI",
    12: "XII",
    13: "XIII",
    14: "XIV",
    15: "XV",
    16: "XVI",
    17: "XVII",
    18: "XVIII",
    19: "XIX",
    20: "XX",
    21: "XXI",
    22: "XXII",
    23: "XXIII",
    24: "XXIV",
    25: "XXV",
    26: "XXVI",
    27: "XXVII",
    28: "XXVIII",
    29: "XXIX",
    30: "XXX",
    31: "XXXI",
    32: "XXXII",
    33: "XXXIII",
    34: "XXXIV",
    35: "XXXV",
    36: "XXXVI",
}


def entero_a_romano(num):
    """Devuelve la representación en romano usando un mapa directo del 0 al 36."""
    try:
        val = int(num)
        return MAPA_ROMANOS.get(val, str(num))
    except (ValueError, TypeError):
        return str(num)

def digitarPartitura():
    # 1. Leer el JSON desde stdin enviada por Java
    json_raw = sys.stdin.read()

    if not json_raw.strip():
        print("Error: No se recibio ningun JSON", file=sys.stderr)
        sys.exit(1)

    try:
        datosjson = json.loads(json_raw)
    except json.JSONDecodeError as e:
        print(f"Error al parsear el JSON: {e}", file=sys.stderr)
        sys.exit(1)

    # 2. Extraer los datos del JSON
    digitaciones = datosjson.get("digitaciones", []) # array de digitaciones de cada nota
    archivo_in = datosjson.get("archivo_in", "") # ruta xml sin digitar
    archivo_out = datosjson.get("archivo_out", "") # ruta xml digitado ya

    if not archivo_in or not archivo_out or not digitaciones: # puede no haber digitacion especificada
        print(
            "Error: Las rutas archivo_in y archivo_out son obligatorias y no pueden ser nulas.",
            file=sys.stderr,
        )
        sys.exit(1)

    try:
        # 3. Cargar la partitura/archivo desde la ruta indicada en archivo_in
        score = music21.converter.parse(archivo_in)

        # 4. Recorrer las notas y acordes en orden secuencial
        notas_y_acordes = score.recurse().notes # .notes recupera objetos Note y Chord, ignorando los silencios (Rest)

        idx_digitacion = 0
        total_digitaciones = len(digitaciones)

        for el in notas_y_acordes:
            if idx_digitacion >= total_digitaciones:
                break  # Finaliza si no quedan mas digitaciones en el array

            val_digitacion = str(digitaciones[idx_digitacion])

            # Separar el string "cuerda,traste,dedo_izq,dedo_der"
            partes = [p.strip() for p in val_digitacion.split(",")]

            cuerda = partes[0] if len(partes) > 0 else ""
            traste = partes[1] if len(partes) > 1 else ""
            dedo_izq = partes[2] if len(partes) > 2 else ""
            dedo_der = partes[3] if len(partes) > 3 else ""

            # 4.1. Cuerda: Numero rodeado con circulo (debajo de la nota)
            if cuerda:
                try:
                    str_ind = music21.expressions.StringIndication(int(cuerda))
                    str_ind.placement = "below"
                    el.expressions.append(str_ind)
                except ValueError:
                    pass

            # 4.2. Traste: Numero romano segun el mapa (encima de la nota)
            if traste and traste.isdigit() and 0 <= int(traste) <= 36:
                traste_romano = entero_a_romano(traste)
                fret_ind = music21.expressions.FretIndication(traste_romano)
                fret_ind.placement = "above"
                el.expressions.append(fret_ind)

            # 4.3. Dedo Mano Izquierda: Numero (encima a la derecha de la nota)
            if dedo_izq:
                fing_lh = music21.articulations.Fingering(dedo_izq)
                fing_lh.placement = "above"
                el.articulations.append(fing_lh)

            # 4.4. Dedo Mano Derecha: Numero (debajo a la derecha de la nota)
            if dedo_der:
                fing_rh = music21.articulations.RightHandFingering(dedo_der)
                fing_rh.placement = "below"
                el.articulations.append(fing_rh)

            idx_digitacion += 1
        # 5. Guardar la partitura digitada en la ruta archivo_out
        score.write(fp=archivo_out) # music21 detecta automaticamente el formato por la extension (.xml, .mxl, .mid, etc.)
        print(f"Exito: Archivo procesado y guardado en {archivo_out}")

    except Exception as e:
        print(
            f"Error al procesar el archivo musical: {e}",
            file=sys.stderr,
        )
        sys.exit(1)


if __name__ == "__main__":
    digitarPartitura()