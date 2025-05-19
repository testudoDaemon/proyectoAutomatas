package utils;

public class Token {
    private final String lexema;
    private final int numeroLinea;
    private int valorTablaTokens;
    private int esIdentificador;

    public Token(String lexema, int numeroLinea){
        this.lexema = lexema;
        this.numeroLinea = numeroLinea;
    }

    public Token(String lexema, int valorTablaTokens, int esIdentificador, int numeroLinea){
        this.lexema = lexema;
        this.valorTablaTokens = valorTablaTokens;
        this.esIdentificador = esIdentificador;
        this.numeroLinea = numeroLinea;
    }

    public void setToken(TokenType token) {
        valorTablaTokens = token.getValorTablaTokens();
        esIdentificador = token.getEsIdentificador();
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public String getLexema() {
        return lexema;
    }

    public int getEsIdentificador() {
        return esIdentificador;
    }

    public int getValorTablaTokens() {
        return valorTablaTokens;
    }

    @Override
    public String toString(){
        return lexema + " " + valorTablaTokens + " " + esIdentificador + " " + numeroLinea;
    }
}
