import java.util.ArrayList;

public class Token {
    private final String lexema;
    private final int numeroLinea;
    private int valorTablaTokens;
    private ArrayList<Token> tokens;

    public Token(String lexema, String valorTablaTokens, int numeroLinea) {
        this.lexema = lexema;
        this.numeroLinea = numeroLinea;
        try {
            this.valorTablaTokens = Integer.parseInt(valorTablaTokens);
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido para utils.Token: " + valorTablaTokens);
            this.valorTablaTokens = -404; // Valor de error
        }
        this.tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        tokens.add(token);
    }
    public String getLexema() {
        return lexema;
    }
    public int getNumeroLinea() {
        return numeroLinea;
    }

    public int getValorTablaTokens() {
        return valorTablaTokens;
    }


    @Override
    public String toString() {
        return String.valueOf(valorTablaTokens);
    }


}
