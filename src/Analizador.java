
public class Analizador {

    private Analizador() {
    }

   
    public static Resultado analizar(String texto, int inicio, int fin,
                                     String palabra, String secuencia) {
        long total = 0;
        long apariciones = 0;
        long conSecuencia = 0;

        int i = inicio;
        while (i < fin) {
            // Saltar separadores
            while (i < fin && !Character.isLetter(texto.charAt(i))) {
                i++;
            }
            if (i >= fin) {
                break;
            }

            int ini = i;
            while (i < fin && Character.isLetter(texto.charAt(i))) {
                i++;
            }
            int largo = i - ini;
            total++;

            if (largo == palabra.length() && texto.regionMatches(ini, palabra, 0, largo)) {
                apariciones++;
            }
            if (contiene(texto, ini, i, secuencia)) {
                conSecuencia++;
            }
        }
        return new Resultado(total, apariciones, conSecuencia);
    }

    private static boolean contiene(String texto, int ini, int fin, String secuencia) {
        int ultimo = fin - secuencia.length();
        for (int j = ini; j <= ultimo; j++) {
            if (texto.regionMatches(j, secuencia, 0, secuencia.length())) {
                return true;
            }
        }
        return false;
    }
}
