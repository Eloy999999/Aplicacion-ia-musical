import json
import music21

NOTAS_MAP = {
    'C': 'do',
    'D': 're',
    'E': 'mi',
    'F': 'fa',
    'G': 'sol',
    'A': 'la',
    'B': 'si',
}

def circulo_a_cuerda(texto):
    mapa = {
        "①": "1",
        "②": "2",
        "③": "3",
        "④": "4",
        "⑤": "5",
        "⑥": "6"
    }

    return mapa.get(texto, texto)


def romano_a_entero(texto):
    mapa = {
        "0": "0",
        "I": "1",
        "II": "2",
        "III": "3",
        "IV": "4",
        "V": "5",
        "VI": "6",
        "VII": "7",
        "VIII": "8",
        "IX": "9",
        "X": "10",
        "XI": "11",
        "XII": "12"
    }

    return mapa.get(texto.upper(), texto)


def convertir_a_solfeggio(pitch: music21.pitch.Pitch) -> str:
    """Convierte un Pitch de music21 a notación latina.

    Ejemplos:
        C4  -> do4
        F#4 -> fa#4
        Bb3 -> sib3
    """
    nombre_ingles = pitch.step
    nombre_latino = NOTAS_MAP.get(nombre_ingles, nombre_ingles)

    alteracion = ''
    if pitch.accidental is not None:
        alteracion = pitch.accidental.modifier or ''

    octava = str(pitch.octave) if pitch.octave is not None else ''

    return f'{nombre_latino}{alteracion}{octava}'


def obtener_digitacion_existente(elemento):
    """
    Recupera la digitación que está guardada en el MusicXML.

    Devuelve:
        dedoIzquierdo
        traste
        cuerda
        manoDerecha
    """

    dedo_izquierdo = ""
    traste = ""
    cuerda = ""
    mano_derecha = ""

    # -----------------------------------------
    # Dedo izquierdo + traste
    # -----------------------------------------

    for art in elemento.articulations:

        if isinstance(
                art,
                music21.articulations.Fingering):

            texto = str(art.fingerNumber).strip()

            # Ejemplo:
            # "2 V"
            partes = texto.split()

            if len(partes) >= 1:
                dedo_izquierdo = partes[0]

            if len(partes) >= 2:
                traste = partes[1]

    # -----------------------------------------
    # Cuerda + mano derecha
    # -----------------------------------------

    if elemento.lyrics:

        texto = str(elemento.lyrics[0].text).strip()

        partes = texto.split()

        if len(partes) >= 1:
            cuerda = partes[0]

        if len(partes) >= 2:
            mano_derecha = partes[1]

    return (
        dedo_izquierdo,
        traste,
        cuerda,
        mano_derecha
    )


def obtener_nombre_elemento(elemento) -> str:
    """Devuelve el nombre musical de un Note o Chord.

    Note:
        mi4

    Chord:
        do4,mi4,sol4
    """
    if isinstance(elemento, music21.note.Note):
        return convertir_a_solfeggio(elemento.pitch)

    if isinstance(elemento, music21.chord.Chord):
        return ','.join(
            convertir_a_solfeggio(p) for p in elemento.pitches
        )

    return ''


def archivo_a_notas_legacy(nombre_archivo: str) -> list[str]:
    """
    Formato utilizado por Digitador.java.

    Cada elemento de 'notas' es un String:
        "do4"
        "mi4"
        "do4,mi4,sol4"

    En los acordes se usa el mismo orden que DigitarPartitura.py:
    nota más aguda -> nota más grave.
    """

    score = music21.converter.parse(nombre_archivo)

    resultado = []

    elementos = score.recurse().getElementsByClass(
        [music21.note.Note, music21.chord.Chord]
    )

    for elemento in elementos:

        # No duplicar notas que formen parte de un Chord.
        if isinstance(elemento, music21.note.Note):
            if elemento.activeSite and isinstance(
                elemento.activeSite,
                music21.chord.Chord
            ):
                continue

            resultado.append(
                convertir_a_solfeggio(elemento.pitch)
            )

        elif isinstance(elemento, music21.chord.Chord):

            # IMPORTANTE:
            # mismo orden que DigitarPartitura.py
            notas_ordenadas = sorted(
                elemento.pitches,
                reverse=True
            )

            nombre_acorde = ",".join(
                convertir_a_solfeggio(p)
                for p in notas_ordenadas
            )

            resultado.append(nombre_acorde)

    return resultado


def archivo_a_notas_detalladas(nombre_archivo: str) -> list[dict]:
    """Devuelve información detallada para la aplicación Android.

    Este formato NO lo utiliza Digitador.java directamente.

    Ejemplo:

    {
        "id": "0",
        "nombre": "mi4",
        "compas": 1,
        "dedoIzquierdo": "",
        "cuerda": "",
        "manoDerecha": ""
    }
    """

    score = music21.converter.parse(nombre_archivo)

    resultado = []

    elementos = score.recurse().getElementsByClass(
        [music21.note.Note, music21.chord.Chord]
    )

    for idx, elemento in enumerate(elementos):
        # Evitamos duplicar las notas que están dentro de un Chord.
        if isinstance(elemento, music21.note.Note):
            if elemento.activeSite and isinstance(
                elemento.activeSite, music21.chord.Chord
            ):
                continue

        nombre = obtener_nombre_elemento(elemento)

        if not nombre:
            continue

        compas = (
            elemento.measureNumber
            if elemento.measureNumber is not None
            else 1
        )

        dedo, cuerda = obtener_digitacion_existente(elemento)

        resultado.append({
            'id': str(idx),
            'nombre': nombre,
            'compas': compas,
            'dedoIzquierdo': dedo,
            'cuerda': cuerda,
            'manoDerecha': '',
        })

    return resultado


def json_notas_legacy(ruta_archivo: str) -> str:
    """JSON compatible con Digitador.java.

    NO cambiar este formato.
    """

    notas = archivo_a_notas_legacy(ruta_archivo)

    datos = {
        'archivo': ruta_archivo,
        'total_notas': len(notas),
        'notas': notas,
    }

    return json.dumps(datos, ensure_ascii=False)


def json_notas_detalladas(nombre_archivo: str) -> str:

    score = music21.converter.parse(
        nombre_archivo
    )

    resultado = []

    elementos = [
        el
        for el in score.recurse().notes
        if el.isNote or el.isChord
    ]

    for indice_elemento, elemento in enumerate(elementos):

        compas = (
            elemento.measureNumber
            if elemento.measureNumber
            else 1
        )

        # ==========================================
        # NOTA SIMPLE
        # ==========================================

        if elemento.isNote:

            dedo, traste, cuerda, mano = \
                leer_digitacion_elemento(elemento)

            resultado.append({

                "id": f"E{indice_elemento}N0",

                "nombre":
                    convertir_a_solfeggio(
                        elemento.pitch
                    ),

                "compas": compas,

                "dedoIzquierdo": dedo,

                "traste": traste,

                "cuerda": cuerda,

                "manoDerecha": mano
            })

        # ==========================================
        # ACORDE
        # ==========================================

        elif elemento.isChord:

            # EXACTAMENTE EL MISMO ORDEN QUE
            # DigitarPartitura.py
            notas = sorted(
                elemento.notes,
                key=lambda n: n.pitch,
                reverse=True
            )

            # Fingering del acorde
            lineas_fingering = []

            for art in elemento.articulations:

                if isinstance(
                        art,
                        music21.articulations.Fingering):

                    lineas_fingering = (
                        str(art.fingerNumber)
                        .splitlines()
                    )

                    break

            # Lyrics del acorde
            lineas_lyrics = []

            for lyric in elemento.lyrics:
                lineas_lyrics.append(
                    str(lyric.text)
                )

            # Una DigitacionNota POR nota
            for i, nota in enumerate(notas):

                dedo = ""
                traste = ""
                cuerda = ""
                mano = ""

                # ------------------------------
                # Fingering
                # ------------------------------

                if i < len(lineas_fingering):

                    partes = (
                        lineas_fingering[i]
                        .split()
                    )

                    if len(partes) >= 1:
                        dedo = partes[0]

                    if len(partes) >= 2:
                        traste = romano_a_entero(
                            partes[1]
                        )

                # ------------------------------
                # Lyric
                # ------------------------------

                if i < len(lineas_lyrics):

                    partes = (
                        lineas_lyrics[i]
                        .split()
                    )

                    if len(partes) >= 1:
                        cuerda = circulo_a_cuerda(
                            partes[0]
                        )

                    if len(partes) >= 2:
                        mano = partes[1]

                resultado.append({

                    # MUY IMPORTANTE:
                    # E5N0, E5N1, E5N2 pertenecen
                    # al mismo acorde.
                    "id": (
                        f"E{indice_elemento}N{i}"
                    ),

                    "nombre":
                        convertir_a_solfeggio(
                            nota.pitch
                        ),

                    "compas": compas,

                    "dedoIzquierdo": dedo,

                    "traste": traste,

                    "cuerda": cuerda,

                    "manoDerecha": mano
                })

    datos = {
        "archivo": nombre_archivo,
        "total_notas": len(resultado),
        "notas": resultado
    }

    return json.dumps(
        datos,
        ensure_ascii=False
    )


# Mantener esta función por compatibilidad con código existente.
def json_notas(ruta_archivo: str) -> str:
    return json_notas_legacy(ruta_archivo)

def json_notas_edicion(nombre_archivo: str) -> str:
   """
   Lee las notas para la pantalla de edición.
   Los acordes se separan en notas individuales.
   """

   resultado = []

   score = music21.converter.parse(nombre_archivo)

   elementos = score.recurse().getElementsByClass(
       [music21.note.Note, music21.chord.Chord]
   )

   id_nota = 0

   for elemento in elementos:

       compas = elemento.measureNumber if elemento.measureNumber else 1

       # Nota normal
       if isinstance(elemento, music21.note.Note):

           dedo, cuerda = obtener_digitacion_existente(elemento)

           resultado.append({
               "id": str(id_nota),
               "nombre": convertir_a_solfeggio(elemento.pitch),
               "compas": compas,
               "dedoIzquierdo": dedo,
               "cuerda": cuerda,
               "traste": "",
               "manoDerecha": ""
           })

           id_nota += 1

       # Acorde
       elif isinstance(elemento, music21.chord.Chord):

           for pitch in elemento.pitches:

               resultado.append({
                   "id": str(id_nota),
                   "nombre": convertir_a_solfeggio(pitch),
                   "compas": compas,
                   "dedoIzquierdo": "",
                   "cuerda": "",
                   "traste": "",
                   "manoDerecha": ""
               })

               id_nota += 1

   return json.dumps({
       "archivo": nombre_archivo,
       "total_notas": len(resultado),
       "notas": resultado
   })

def leer_digitacion_elemento(elemento):
   """
   Lee la digitación que DigitarPartitura.py escribió en el XML.

   Devuelve:
       dedoIzquierdo
       traste
       cuerda
       manoDerecha
   """

   dedo = ""
   traste = ""
   cuerda = ""
   mano_derecha = ""

   # -----------------------------------------
   # Fingering
   # -----------------------------------------

   for art in elemento.articulations:

       if isinstance(
               art,
               music21.articulations.Fingering):

           texto = str(art.fingerNumber).strip()

           # Puede contener varias líneas si es un acorde.
           lineas = texto.splitlines()

           if lineas:

               partes = lineas[0].split()

               if len(partes) >= 1:
                   dedo = partes[0]

               if len(partes) >= 2:
                   traste = romano_a_entero(partes[1])

   # -----------------------------------------
   # Lyric
   # -----------------------------------------

   if elemento.lyrics:

       texto = str(
           elemento.lyrics[0].text
       ).strip()

       partes = texto.split()

       if len(partes) >= 1:
           cuerda = circulo_a_cuerda(
               partes[0]
           )

       if len(partes) >= 2:
           mano_derecha = partes[1]

   return (
       dedo,
       traste,
       cuerda,
       mano_derecha
   )
