
public class ProcesadorParalelo {

    private ProcesadorParalelo() {
    }

    public static Resultado procesar(String texto, String palabra, String secuencia, int numHilos)
            throws InterruptedException {
        int[] limites = dividir(texto, numHilos);

        HiloAnalizador[] hilos = new HiloAnalizador[numHilos];
        for (int k = 0; k < numHilos; k++) {
            hilos[k] = new HiloAnalizador(texto, limites[k], limites[k + 1], palabra, secuencia);
        }

        for (HiloAnalizador h : hilos) {
            h.start();
        }
        for (HiloAnalizador h : hilos) {
            h.join();
        }

        Resultado total = new Resultado(0, 0, 0);
        for (HiloAnalizador h : hilos) {
            total.combinar(h.getResultado());
        }
        return total;
    }

    static int[] dividir(String texto, int numHilos) {
        int n = texto.length();
        int[] limites = new int[numHilos + 1];
        limites[0] = 0;
        limites[numHilos] = n;

        for (int k = 1; k < numHilos; k++) {
            int f = (int) ((long) n * k / numHilos);
            f = Math.max(f, limites[k - 1]);
            while (f < n && f > 0
                    && Character.isLetter(texto.charAt(f))
                    && Character.isLetter(texto.charAt(f - 1))) {
                f++;
            }
            limites[k] = f;
        }
        return limites;
    }
}
