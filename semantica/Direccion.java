package semantica;

public class Direccion {
    String id;
    int token;
    int linea;
    int vci;

    public Direccion(String id, int token, int linea, int vci) {
        this.id = id;
        this.token = token;
        this.linea = linea;
        this.vci = vci;
    }

    @Override
    public String toString() {
        return id + "\t" + token + "\t" + linea + "\t" + vci;
    }
}
