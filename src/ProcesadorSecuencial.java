
public class ProcesadorSecuencial {

    private ProcesadorSecuencial() {
    }

    public static Resultado procesar(String texto, String palabra, String secuencia) {
        return Analizador.analizar(texto, 0, texto.length(), palabra, secuencia);
    }
}
