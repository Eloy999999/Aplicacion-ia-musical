import json
import re
import xml.etree.ElementTree as ET
import music21

# ============================================================
# MAPAS
# ============================================================

MAPA_ROMANOS = {
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
}

MAPA_CUERDA_CIRCULO = {
    1: "①",
    2: "②",
    3: "③",
    4: "④",
    5: "⑤",
    6: "⑥",
}

MAPA_DEDO_DCHO = {
    1: "p",
    2: "i",
    3: "c",
    4: "a",
    5: "m",
}

MAPA_SOLFEGO_A_INGLES = {
    "do": "C",
    "re": "D",
    "mi": "E",
    "fa": "F",
    "sol": "G",
    "la": "A",
    "si": "B",
}


# ============================================================
# FUNCIONES AUXILIARES
# ============================================================


def entero_a_romano(num):
    try:
        val = int(num)
        return MAPA_ROMANOS.get(val, str(num))
    except (ValueError, TypeError):
        return str(num)


def limpiar_prefijo(texto):
    texto = str(texto).strip()

    if not texto:
        return ""

    for i, caracter in enumerate(texto):
        if caracter.isdigit():
            return texto[i:]

    return texto


def construir_textos_digitacion(
    dedo_izq_raw, traste_raw, cuerda_raw, dedo_der_raw
):
    cuerda = limpiar_prefijo(cuerda_raw)
    traste = limpiar_prefijo(traste_raw)
    dedo_izq = limpiar_prefijo(dedo_izq_raw)
    dedo_der = limpiar_prefijo(dedo_der_raw)

    arriba = []

    if dedo_izq:
        arriba.append(dedo_izq)

    if traste:
        arriba.append(entero_a_romano(traste))

    texto_arriba = " ".join(arriba)

    abajo = []

    if cuerda:
        if cuerda.isdigit():
            abajo.append(MAPA_CUERDA_CIRCULO.get(int(cuerda), cuerda))
        else:
            abajo.append(cuerda)

    if dedo_der:
        if dedo_der.isdigit():
            abajo.append(MAPA_DEDO_DCHO.get(int(dedo_der), dedo_der))
        else:
            abajo.append(dedo_der)

    texto_abajo = " ".join(abajo)

    return texto_arriba, texto_abajo


def parsear_nombre_solfeggio(nombre_solfeo):
    nombre_solfeo = nombre_solfeo.strip().lower()

    for nota_lat, nota_ing in MAPA_SOLFEGO_A_INGLES.items():
        if nombre_solfeo.startswith(nota_lat):
            resto = nombre_solfeo[len(nota_lat) :]

            resto = resto.replace("♭", "-")
            resto = resto.replace("♯", "#")
            resto = resto.replace("b", "-")

            pitch_str = f"{nota_ing}{resto}"

            try:
                return music21.pitch.Pitch(pitch_str)
            except Exception:
                pass

    return music21.pitch.Pitch(nombre_solfeo)


def remover_nombres_e_instrucciones_pantalla(ruta_xml):
    tree = ET.parse(ruta_xml)
    root = tree.getroot()

    for part_name in root.iter("part-name"):
        part_name.text = ""
        part_name.set("print-object", "no")

    for part_abbrev in root.iter("part-abbreviation"):
        part_abbrev.text = ""
        part_abbrev.set("print-object", "no")

    for elem_tag in ["instrument-name", "instrument-abbreviation"]:
        for elem in root.iter(elem_tag):
            elem.text = ""

    tree.write(ruta_xml, encoding="UTF-8", xml_declaration=True)


# ============================================================
# IDENTIFICAR LOS IDs
# ============================================================


def obtener_id_elemento(id_nota):
    """Convierte:

        E8N0 -> 8
        E8N1 -> 8
        E8N2 -> 8

    Para una nota normal:

        E7N0 -> 7
    """

    match = re.match(r"^E(\d+)N(\d+)$", str(id_nota))

    if not match:
        return None

    return int(match.group(1))


def obtener_indice_nota(id_nota):
    """E8N0 -> 0

    E8N1 -> 1
    E8N2 -> 2
    """

    match = re.match(r"^E(\d+)N(\d+)$", str(id_nota))

    if not match:
        return None

    return int(match.group(2))


# ============================================================
# EDITAR UNA NOTA SIMPLE
# ============================================================


def aplicar_digitacion_nota(nota, datos):
    # --------------------------------------------------------
    # Altura
    # --------------------------------------------------------

    nombre = datos.get("nombreNota", "").strip()

    if nombre:
        try:
            nota.pitch = parsear_nombre_solfeggio(nombre)

        except Exception as e:
            print(f"Error cambiando nota: {e}")

    # --------------------------------------------------------
    # Limpiar digitación anterior
    # --------------------------------------------------------

    nota.articulations = [
        a
        for a in nota.articulations
        if not isinstance(a, music21.articulations.Fingering)
    ]

    nota.lyrics = []

    # --------------------------------------------------------
    # Nueva digitación
    # --------------------------------------------------------

    dedo = datos.get("dedoIzquierdo", "")

    traste = datos.get("traste", "")

    cuerda = datos.get("cuerda", "")

    mano = datos.get("manoDerecha", "")

    t_arr, t_aba = construir_textos_digitacion(dedo, traste, cuerda, mano)

    if t_arr:
        fingering = music21.articulations.Fingering(t_arr)

        fingering.placement = "above"

        nota.articulations.append(fingering)

    if t_aba:
        lyric = music21.note.Lyric(text=t_aba, number=1)

        nota.lyrics.append(lyric)


# ============================================================
# EDITAR PARTITURA
# ============================================================


def editar_partitura(ruta_xml: str, cambios_json_str: str) -> bool:
    try:
        score = music21.converter.parse(ruta_xml)

        cambios = json.loads(cambios_json_str)

        lista_digitaciones = cambios.get("digitaciones", [])

        # ----------------------------------------------------
        # Los elementos reales de la partitura.
        #
        # Un acorde cuenta como UN elemento.
        # ----------------------------------------------------

        elementos = [
            el for el in score.recurse().notes if el.isNote or el.isChord
        ]

        # ----------------------------------------------------
        # Agrupar los datos recibidos por elemento.
        #
        # Ejemplo:
        #
        # E8N0
        # E8N1
        # E8N2
        #
        # pasan al mismo acorde E8.
        # ----------------------------------------------------

        grupos = {}

        for datos in lista_digitaciones:
            id_nota = datos.get("idNota", "")

            id_elemento = obtener_id_elemento(id_nota)

            indice_nota = obtener_indice_nota(id_nota)

            if id_elemento is None:
                continue

            if id_elemento not in grupos:
                grupos[id_elemento] = []

            grupos[id_elemento].append((indice_nota, datos))

        # Ordenamos las notas de cada elemento.
        for clave in grupos:
            grupos[clave].sort(key=lambda x: x[0])

        # ====================================================
        # RECORRER ELEMENTOS REALES
        # ====================================================

        for indice_elemento, elemento in enumerate(elementos):
            datos_elemento = grupos.get(indice_elemento, [])

            if not datos_elemento:
                continue

            # =================================================
            # NOTA SIMPLE
            # =================================================

            if elemento.isNote:
                datos = datos_elemento[0][1]

                aplicar_digitacion_nota(elemento, datos)

            # =================================================
            # ACORDE
            # =================================================

            elif elemento.isChord:
                # ------------------------------------------------
                # Orden de las notas:
                #
                # igual que en LectorNotas:
                # aguda -> grave
                # ------------------------------------------------

                notas_acorde = sorted(
                    elemento.notes, key=lambda n: n.pitch, reverse=True
                )

                # ------------------------------------------------
                # Datos recibidos:
                #
                # E8N0
                # E8N1
                # E8N2
                #
                # también están:
                # aguda -> grave
                # ------------------------------------------------

                datos_acorde = [datos for _, datos in datos_elemento]

                # ------------------------------------------------
                # 1. CAMBIAR ALTURAS
                #
                # Modificamos TODOS los pitches del acorde
                # de una vez.
                # ------------------------------------------------

                nuevos_pitches = list(elemento.pitches)

                # El acorde de music21 normalmente está
                # de grave -> aguda.
                #
                # Nuestros datos están de aguda -> grave.
                nombres_grave_aguda = list(
                    reversed([
                        datos.get("nombreNota", "").strip()
                        for datos in datos_acorde
                    ])
                )

                for i, nombre in enumerate(nombres_grave_aguda):
                    if nombre and i < len(nuevos_pitches):
                        try:
                            nuevos_pitches[i] = parsear_nombre_solfeggio(nombre)

                        except Exception as e:
                            print(f"Error cambiando pitch del acorde: {e}")

                # ------------------------------------------------
                # MUY IMPORTANTE:
                #
                # Asignamos todos los pitches al Chord junto.
                # ------------------------------------------------

                elemento.pitches = tuple(nuevos_pitches)

                # ------------------------------------------------
                # 2. BORRAR DIGITACIÓN ANTERIOR
                # ------------------------------------------------

                elemento.articulations = [
                    a
                    for a in elemento.articulations
                    if not isinstance(a, music21.articulations.Fingering)
                ]

                elemento.lyrics = []

                # ------------------------------------------------
                # 3. RECONSTRUIR EL FORMATO ORIGINAL
                #
                # Es EXACTAMENTE el formato que utiliza
                # DigitarPartitura.py.
                # ------------------------------------------------

                arriba_partes = []
                abajo_partes = []

                for datos in datos_acorde:
                    dedo = datos.get("dedoIzquierdo", "")

                    traste = datos.get("traste", "")

                    cuerda = datos.get("cuerda", "")

                    mano = datos.get("manoDerecha", "")

                    t_arr, t_aba = construir_textos_digitacion(
                        dedo, traste, cuerda, mano
                    )

                    arriba_partes.append(t_arr)

                    abajo_partes.append(t_aba)

                # ------------------------------------------------
                # Fingering
                # ------------------------------------------------

                if any(arriba_partes):
                    texto_arriba = "\n".join(arriba_partes)

                    fingering = music21.articulations.Fingering(texto_arriba)

                    fingering.placement = "above"

                    elemento.articulations.append(fingering)

                # ------------------------------------------------
                # Lyrics
                # ------------------------------------------------

                for numero_linea, texto in enumerate(abajo_partes, start=1):
                    if texto:
                        lyric = music21.note.Lyric(
                            text=texto, number=numero_linea
                        )

                        elemento.lyrics.append(lyric)

        # ====================================================
        # GUARDAR
        # ====================================================

        score.write("musicxml", fp=ruta_xml)

        remover_nombres_e_instrucciones_pantalla(ruta_xml)

        print(f"Partitura guardada correctamente: {ruta_xml}")

        return True

    except Exception as e:
        print(f"Error editando partitura: {e}")

        return False