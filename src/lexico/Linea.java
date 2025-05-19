package lexico;

public class Linea {
    private String linea;
    private int numeroLinea;

    public Linea(String linea, int numeroLinea){
        this.linea = linea;
        this.numeroLinea = numeroLinea;
    }

    public String getLinea() {
        return linea;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public void setNumeroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }

    @Override
    public String toString(){
        return linea + " " + numeroLinea;
    }
}
