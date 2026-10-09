
public class Resultado {

    private long totalPalabras;
    private long apariciones;          // veces que aparece la palabra buscada
    private long contienenSecuencia;   // palabras que contienen la secuencia buscada

    public Resultado(long totalPalabras, long apariciones, long contienenSecuencia) {
        this.totalPalabras = totalPalabras;
        this.apariciones = apariciones;
        this.contienenSecuencia = contienenSecuencia;
    }

    public void combinar(Resultado otro) {
        totalPalabras += otro.totalPalabras;
        apariciones += otro.apariciones;
        contienenSecuencia += otro.contienenSecuencia;
    }

    public long getTotalPalabras() {
        return totalPalabras;
    }

    public long getApariciones() {
        return apariciones;
    }

    public long getContienenSecuencia() {
        return contienenSecuencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resultado)) return false;
        Resultado r = (Resultado) o;
        return totalPalabras == r.totalPalabras
                && apariciones == r.apariciones
                && contienenSecuencia == r.contienenSecuencia;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(totalPalabras) * 31 * 31
                + Long.hashCode(apariciones) * 31
                + Long.hashCode(contienenSecuencia);
    }

    @Override
    public String toString() {
        return "palabras=" + totalPalabras
                + ", apariciones=" + apariciones
                + ", conSecuencia=" + contienenSecuencia;
    }
}
