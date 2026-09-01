import json
import sys
import xml.etree.ElementTree as ET
import music21

MAPA_ROMANOS = {
    0: "0", 1: "I", 2: "II", 3: "III", 4: "IV", 5: "V", 6: "VI",
    7: "VII", 8: "VIII", 9: "IX", 10: "X", 11: "XI", 12: "XII", 13: "XIII",
    14: "XIV", 15: "XV", 16: "XVI", 17: "XVII", 18: "XVIII", 19: "XIX"
}

MAPA_CUERDA_CIRCULO = {
    1: "①", 2: "②", 3: "③", 4: "④", 5: "⑤", 6: "⑥"
}

MAPA_DEDO_DCHO = {
    1: "p", 2: "i", 3: "c", 4: "a", 5: "m"
}

def entero_a_romano(num):
    try:
        val = int(num)
        return MAPA_ROMANOS.get(val, str(num))
    except (ValueError, TypeError):
        return str(num)

def limpiar_prefijo(texto):
    texto = texto.strip()
    if not texto:
        return ""
    for i, caracter in enumerate(texto):
        if caracter.isdigit():

            return texto[i:]
    return texto

def obtener_textos_digitacion(val_digitacion_nota):
    partes = [p.strip() for p in str(val_digitacion_nota).split(",")]

    cuerda_raw   = partes[0] if len(partes) > 0 else ""
    traste_raw   = partes[1] if len(partes) > 1 else ""
    dedo_izq_raw = partes[2] if len(partes) > 2 else ""
    dedo_der_raw = partes[3] if len(partes) > 3 else ""

    cuerda   = limpiar_prefijo(cuerda_raw)
    traste   = limpiar_prefijo(traste_raw)
    dedo_izq = limpiar_prefijo(dedo_izq_raw)
    dedo_der = limpiar_prefijo(dedo_der_raw)

    elem_arriba = []
    if dedo_izq != "":
        if traste == "0":
            elem_arriba.append("~")
        else:
            elem_arriba.append(dedo_izq)
    if traste != "":
        elem_arriba.append(entero_a_romano(traste))
    texto_arriba = " ".join(elem_arriba)

    elem_abajo = []
    if cuerda != "":
        if cuerda.isdigit():
            num_c = int(cuerda)
            elem_abajo.append(MAPA_CUERDA_CIRCULO.get(num_c, cuerda))
        else:
            elem_abajo.append(cuerda)

    if dedo_der != "":
        if dedo_der.isdigit():
            num_dd = int(dedo_der)
            elem_abajo.append(MAPA_DEDO_DCHO.get(num_dd, dedo_der))
        else:
            elem_abajo.append(dedo_der)
        
    texto_abajo = " ".join(elem_abajo)

    return texto_arriba, texto_abajo

def remover_nombres_e_instrucciones_pantalla(ruta_xml):
    """
    Desactiva explícitamente la impresión de nombres de instrumentos en MusicXML 
    y limpia los elementos <part-name> e <instrument-name>.
    """
    tree = ET.parse(ruta_xml)
    root = tree.getroot()

    # 1. Configurar <part-name> para que tenga el atributo print-object="no"
    for part_name in root.iter('part-name'):
        part_name.text = ""
        part_name.set('print-object', 'no')

    for part_abbrev in root.iter('part-abbreviation'):
        part_abbrev.text = ""
        part_abbrev.set('print-object', 'no')

    # 2. Vaciar nombres de instrumentos
    for elem_tag in ['instrument-name', 'instrument-abbreviation']:
        for elem in root.iter(elem_tag):
            elem.text = ""

    tree.write(ruta_xml, encoding="UTF-8", xml_declaration=True)

def digitarPartitura(json_raw: str) -> str:
    #json_raw = sys.stdin.readline()
    if not json_raw.strip():
        sys.exit(1)

    datosjson = json.loads(json_raw)
    digitaciones = datosjson.get("digitaciones", [])
    archivo_in = datosjson.get("archivo_in", "")
    archivo_out = datosjson.get("archivo_out", "")

    try:
        score = music21.converter.parse(archivo_in)

        # Aplicación de las digitaciones
        elementos = [el for el in score.recurse().notes if el.isNote or el.isChord]
        idx_digitacion = 0
        total_digitaciones = len(digitaciones)
        cont = 0
        for el in elementos:
            if idx_digitacion >= total_digitaciones:
                break

            cadena_digitacion = digitaciones[idx_digitacion]
            idx_digitacion += 1

            if isinstance(el, music21.chord.Chord):
                sub_digitaciones = cadena_digitacion.split("+")
                notas_ordenadas = sorted(el.notes, key=lambda n: n.pitch, reverse=True)

                arriba_partes = []
                abajo_partes = []

                for i in range(len(notas_ordenadas)):
                    if i < len(sub_digitaciones):
                        t_arr, t_aba = obtener_textos_digitacion(sub_digitaciones[i])
                        if t_arr:
                            arriba_partes.append(t_arr)
                        if t_aba:
                            abajo_partes.append(t_aba)

                if arriba_partes:
                    texto_arr = "\n".join(arriba_partes)
                    f_above = music21.articulations.Fingering(texto_arr)
                    f_above.placement = "above"
                    el.articulations.append(f_above)

                if abajo_partes:
                    for num_linea, texto in enumerate(abajo_partes, start=1):
                        lyr = music21.note.Lyric(text=texto, number=num_linea)
                        el.lyrics.append(lyr)

            else:
                t_arr, t_aba = obtener_textos_digitacion(cadena_digitacion)

                if t_arr:
                    f_above = music21.articulations.Fingering(t_arr)
                    f_above.placement = "above"
                    el.articulations.append(f_above)

                if t_aba:
                    lyr = music21.note.Lyric(text=t_aba, number=1)
                    el.lyrics.append(lyr)
            if cont % 50 == 0:
                print(cont)
            cont += 1

        # 1. Exportar MusicXML con music21
        score.write('musicxml', fp=archivo_out)

        # 2. Inyectar atributo print-object="no" para forzar la ocultación visual
        remover_nombres_e_instrucciones_pantalla(archivo_out)

        print(f"Exito: Guardado en {archivo_out}")

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

#if __name__ == "__main__":
#    digitarPartitura()
