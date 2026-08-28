import music21

def convierteAMusicXML(archivo_in, archivo_out):
    score = music21.converter.parse(archivo_in)
    score.write('musicxml', fp=archivo_out)