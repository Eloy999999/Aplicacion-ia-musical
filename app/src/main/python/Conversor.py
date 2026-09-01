import music21
import os

midi_min = 40
midi_max = 83

def reoctavar_midi(val_midi):
        while val_midi < midi_min:
            val_midi += 12  # Sube octavas (ej. C2/36 -> C3/48)
        while val_midi > midi_max:
            val_midi -= 12  # Baja octavas (ej. E6/88 -> E5/76)
        return val_midi

def preparar_partitura_guitarra(score):
    parte_guitarra = None
    if len(score.parts) > 1:
        for part in score.parts:
            nombre = str(part.partName or part.getInstrument().instrumentName or "").lower()
            if "guitar" in nombre or "guitarra" in nombre:
                parte_guitarra = part
                break
        if not parte_guitarra:
            parte_guitarra = score.parts[0]  # Si no hay etiquet    a clara, toma la primera parte
    else:
        parte_guitarra = score
    for elemento in parte_guitarra.flat.notes:

            # Caso A: Nota individual
        if isinstance(elemento, music21.note.Note):
            elemento.pitch.midi = reoctavar_midi(elemento.pitch.midi)

        # Caso B: Acorde
        elif isinstance(elemento, music21.chord.Chord):
            # Modificar la propiedad midi de cada Pitch dentro del acorde
            nuevos_pitches = []
            for p in elemento.pitches:
                p.midi = reoctavar_midi(p.midi)
                nuevos_pitches.append(p)
            elemento.pitches = nuevos_pitches

    return parte_guitarra

def convierteAMusicXML(archivo_in, archivo_out):
    score = music21.converter.parse(archivo_in)
    scoreBuena = preparar_partitura_guitarra(score)
    scoreBuena.write('musicxml', fp=archivo_out)
    os.sync()