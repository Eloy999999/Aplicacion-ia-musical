import music21
import os

def convierteAMusicXML(archivo_in, archivo_out):
    score = music21.converter.parse(archivo_in)
    score.write('musicxml', fp=archivo_out)
    os.sync()