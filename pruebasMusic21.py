from music21 import converter, note, chord

RUTA_ARCHIVO_PRUEBA = './partituras/HotelCalifornia.mid'

partitura = converter.parse(RUTA_ARCHIVO_PRUEBA)

for e in partitura.flatten().getElementsByClass(['Note', 'Chord']):
    if e.isNote:
        print(e.name)
    else:
        print(e)