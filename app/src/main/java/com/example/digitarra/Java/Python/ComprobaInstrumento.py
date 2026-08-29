import os
from music21 import converter, instrument

def identificar_instrumentos(nombre_o_ruta_archivo):
    # Si le pasas solo el nombre del archivo, lo busca en la misma carpeta que este script .py
    directorio_script = os.path.dirname(os.path.abspath(__file__))
    
    if not os.path.isabs(nombre_o_ruta_archivo):
        ruta_archivo = os.path.join(directorio_script, nombre_o_ruta_archivo).replace("\\", "/")
    else:
        ruta_archivo = nombre_o_ruta_archivo

    # 1. Verificar si el archivo existe
    if not os.path.exists(ruta_archivo):
        print(f"Error: No se encontró el archivo en:\n{ruta_archivo}")
        return

    print(f"Analizando archivo: {os.path.basename(ruta_archivo)}...\n")

    try:
        # 2. Cargar el MIDI o MusicXML
        score = converter.parse(ruta_archivo)
        
        # 3. Buscar todos los objetos de tipo Instrument en la obra
        instrumentos_encontrados = score.recurse().getElementsByClass(instrument.Instrument)
        
        instrumentos_lista = []
        for inst in instrumentos_encontrados:
            nombre = inst.instrumentName or inst.bestName() or "Instrumento desconocido"
            programa_midi = inst.midiProgram
            
            info = {
                "nombre": nombre,
                "programa_midi": programa_midi if programa_midi is not None else "No especificado",
                "clase": inst.__class__.__name__
            }
            
            if info not in instrumentos_lista:
                instrumentos_lista.append(info)

        # 4. Mostrar los resultados
        if not instrumentos_lista:
            print("No se encontraron etiquetas de instrumentos explícitas en el archivo.")
            print("*(Es posible que use el canal de piano por defecto)*")
            return

        print(f"=== INSTRUMENTOS DETECTADOS ({len(instrumentos_lista)}) ===")
        for i, inst in enumerate(instrumentos_lista, 1):
            print(f"{i}. Nombre: {inst['nombre']}")
            print(f"   - Programa MIDI: {inst['programa_midi']}")
            print(f"   - Tipo interno: {inst['clase']}")
            print("-" * 35)

    except Exception as e:
        print(f"Ocurrió un error al procesar el archivo: {e}")

if __name__ == "__main__":
    # Pon aquí el nombre de tu archivo si está en la misma carpeta del script
    archivo_a_analizar = "pages.mid"
    
    identificar_instrumentos(archivo_a_analizar)