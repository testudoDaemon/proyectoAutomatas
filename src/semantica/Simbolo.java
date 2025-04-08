package semantica;

public class Simbolo {

    String id;
    int token;
    String valor;
    int d1;
    int d2;
    String ptr;
    String ambito;

    public Simbolo(String id, int token, String valor) {
        this.id = id;
        this.token = token;
        this.valor = valor;
        this.d1 = 0;
        this.d2 = 0;
        this.ptr = null;
        this.ambito = null;
    }

    public String getID() {
        return id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getAmbito() {
        return ambito;
    }

    @Override
    public String toString() {
        return id + "\t" + token + "\t" + valor + "\t" + d1 + "\t" + d2 + "\t" + ptr + "\t" + ambito;
    }

}
