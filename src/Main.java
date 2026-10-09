import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;

public class Main {

    private static final int[] HILOS = {1, 2, 4};
    private static final int CALENTAMIENTO = 3;
    private static final Locale ES = Locale.forLanguageTag("es");

    public static void main(String[] args) throws IOException, InterruptedException {
        String archivo = args.length > 0 ? args[0] : "libro.txt";
        String palabra = (args.length > 1 ? args[1] : "quijote").toLowerCase(ES);
        String secuencia = (args.length > 2 ? args[2] : "ción").toLowerCase(ES);
        int repeticiones = args.length > 3 ? Integer.parseInt(args[3]) : 5;
        int factor = args.length > 4 ? Integer.parseInt(args[4]) : 100;

        String original = Files.readString(Paths.get(archivo), StandardCharsets.UTF_8);
        original = quitarLicenciaGutenberg(original).toLowerCase(ES);
        String ampliado = String.join("\n", Collections.nCopies(factor, original));

        System.out.println("Archivo: " + archivo);
        System.out.println("Palabra buscada: \"" + palabra + "\" | Secuencia: \"" + secuencia + "\"");
        System.out.println("Procesadores disponibles para la JVM: " + Runtime.getRuntime().availableProcessors());
        System.out.printf("Tamaño original: %,d caracteres | ampliado (x%d): %,d caracteres%n%n",
                original.length(), factor, ampliado.length());

        String[] nombres = {"Original", "Ampliado"};
        String[] textos = {original, ampliado};
        double[][] promedios = new double[textos.length][HILOS.length];
        double[][][] tiempos = new double[textos.length][HILOS.length][repeticiones];

        for (int d = 0; d < textos.length; d++) {
            String texto = textos[d];

            Resultado referencia = ProcesadorSecuencial.procesar(texto, palabra, secuencia);
            System.out.println("[" + nombres[d] + "] Secuencial: " + referencia);
            for (int h : HILOS) {
                if (h == 1) continue;
                Resultado r = ProcesadorParalelo.procesar(texto, palabra, secuencia, h);
                System.out.println("[" + nombres[d] + "] " + h + " hilos:   " + r
                        + (r.equals(referencia) ? "  -> OK, coincide" : "  -> ERROR, no coincide"));
            }
            for (int k = 0; k < HILOS.length; k++) {
                int h = HILOS[k];
                for (int w = 0; w < CALENTAMIENTO; w++) {
                    ejecutar(texto, palabra, secuencia, h);
                }
                double suma = 0;
                for (int rep = 0; rep < repeticiones; rep++) {
                    System.gc(); 
                    long inicio = System.nanoTime();
                    ejecutar(texto, palabra, secuencia, h);
                    long fin = System.nanoTime();
                    double ms = (fin - inicio) / 1_000_000.0;
                    tiempos[d][k][rep] = ms;
                    suma += ms;
                }
                promedios[d][k] = suma / repeticiones;
            }
            System.out.println();
        }

        imprimirTabla(nombres, promedios, tiempos);
    }

  
    private static Resultado ejecutar(String texto, String palabra, String secuencia, int hilos)
            throws InterruptedException {
        if (hilos == 1) {
            return ProcesadorSecuencial.procesar(texto, palabra, secuencia);
        }
        return ProcesadorParalelo.procesar(texto, palabra, secuencia, hilos);
    }

     private static String quitarLicenciaGutenberg(String texto) {
        int inicio = texto.indexOf("*** START OF");
        int fin = texto.indexOf("*** END OF");
        if (inicio < 0 || fin < 0 || fin <= inicio) {
            return texto;
        }
        inicio = texto.indexOf('\n', inicio) + 1;
        return texto.substring(inicio, fin);
    }

    private static void imprimirTabla(String[] nombres, double[][] promedios, double[][][] tiempos) {
        System.out.println(" ");
        System.out.println("| Conjunto de datos | Hilos          | Tiempo promedio (ms) | Aceleración |");
        System.out.println(" ");
        for (int d = 0; d < nombres.length; d++) {
            double ts = promedios[d][0];
            for (int k = 0; k < HILOS.length; k++) {
                String hilos = HILOS[k] == 1 ? "1 (secuencial)" : String.valueOf(HILOS[k]);
                System.out.printf(Locale.US, "| %-17s | %-14s | %20.3f | %11.2f |%n",
                        nombres[d], hilos, promedios[d][k], ts / promedios[d][k]);
            }
        }
        System.out.println(" ");

        System.out.println("\nTiempos individuales (ms):");
        for (int d = 0; d < nombres.length; d++) {
            for (int k = 0; k < HILOS.length; k++) {
                StringBuilder sb = new StringBuilder();
                for (double t : tiempos[d][k]) {
                    sb.append(String.format(Locale.US, "%.3f  ", t));
                }
                System.out.printf("  %-8s %d hilo(s): %s%n", nombres[d], HILOS[k], sb);
            }
        }
    }

}
