package semantica;

import utils.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AnalizadorSemantico {

    public ArrayList<Token> tokens;
    public ArrayList<Simbolo> simbolos;
    public Direccion ejecucion;
    public boolean salirDeclaratoria = false, errorSemantico = false, enLinea = false;
    public int indice;
    public String ambito;

    public AnalizadorSemantico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.simbolos = new ArrayList<>();
        this.indice = 0;
        this.ejecucion = null;
        this.ambito = null;
    }

    public boolean getErrorSemantico() {
        return errorSemantico;
    }
    public ArrayList<Simbolo> getSimbolos() {
        return simbolos;
    }

    public void analizar() {
        Token tokenActual = tokens.get(indice);
        boolean entero = false, real = false, string = false, bool = false;

        // Aqui se guardan todas las variables declaradas en la tabla de simbolos
        while (!salirDeclaratoria) {
            tokenActual = tokens.get(indice);
            Simbolo simbolo;
            if (tokenActual.getValorTablaTokens() == -55) {
                ejecucion = new Direccion(tokenActual.getLexema(), -55, tokenActual.getNumeroLinea(), 0);
                ambito = tokenActual.getLexema();
            }

            if (tipoDato(tokenActual.getValorTablaTokens())) {
                switch (tokenActual.getValorTablaTokens()) {
                    case -11 -> entero = true;
                    case -12 -> real = true;
                    case -13 -> string = true;
                    case -14 -> bool = true;
                }
            }

            // checar que los identificadores sean de tipo entero antes de agregarlos a la tabla de simbolos
            if (tokenActual.getEsIdentificador() == -2 && entero)
                if (tokenActual.getValorTablaTokens() == -51) {
                    simbolo = new Simbolo(tokenActual.getLexema(), tokenActual.getValorTablaTokens(), "0", 0 , 0," ",ambito);
                    simbolos.add(simbolo);
                } else
                    error("usar un identificador de enteros para tipo int (debe terminar con &) en linea:" + tokenActual.getNumeroLinea());

            // checar que los identificadores sean de tipo real antes de agregarlos a la tabla de simbolos
            if (tokenActual.getEsIdentificador() == -2 && real)
                if (tokenActual.getValorTablaTokens() == -52) {
                    simbolo = new Simbolo(tokenActual.getLexema(), tokenActual.getValorTablaTokens(), "0", 0, 0, "", ambito);
                    simbolos.add(simbolo);
                } else
                    error("usar un identificador de reales para tipo real (debe terminar con %) en linea:" + tokenActual.getNumeroLinea());

            // checar que los identificadores sean de tipo string antes de agregarlos a la tabla de simbolos
            if (tokenActual.getEsIdentificador() == -2 && string)
                if (tokenActual.getValorTablaTokens() == -53) {
                    simbolo = new Simbolo(tokenActual.getLexema(), tokenActual.getValorTablaTokens(), "null", 0, 0, "", ambito);
                    simbolos.add(simbolo);
                } else
                    error("usar un identificador de cadenas para tipo string (debe terminar con %) en linea:" + tokenActual.getNumeroLinea());

            // checar que los identificadores sean de tipo boolean antes de agregarlos a la tabla de simbolos
            if (tokenActual.getEsIdentificador() == -2 && bool)
                if (tokenActual.getValorTablaTokens() == -54) {
                    simbolo = new Simbolo(tokenActual.getLexema(), tokenActual.getValorTablaTokens(), "null", 0, 0, "", ambito);
                    simbolos.add(simbolo);
                } else
                    error("usar un identificador boolean para tipo bool (debe terminar con $) en linea:" + tokenActual.getNumeroLinea());

            if (tokenActual.getValorTablaTokens() == -75) {
                entero = false;
                real = false;
                string = false;
                bool = false;
            }

            // Move to the next token
            indice++;
            if (indice >= tokens.size())
                break; // Avoid IndexOutOfBoundsException

            if (tokens.get(indice).getValorTablaTokens() == -2)
                salirDeclaratoria = true;
        }

        indice = 0;
        salirDeclaratoria = false;
        for (Token token : tokens) {
            if (token.getValorTablaTokens() == -2)
                salirDeclaratoria = true;
            if (salirDeclaratoria) {
                if (token.getEsIdentificador() == -2)
                    if (token.getValorTablaTokens() != -55)
                        if (!isTokenInSimbolos(token.getLexema()))
                            error("variable no declarada: " + token.getLexema() + " en linea:" + token.getNumeroLinea());

                if (enLinea) {
                    if (token.getEsIdentificador() == -2) {
                        if (token.getValorTablaTokens() != -51 && entero)
                            error("identificador: " + token.getLexema() + " no compatible para tipo en int (debe terminar con &) en linea:" + token.getNumeroLinea());
                        if (token.getValorTablaTokens() != -52 && real)
                            error("identificador: " + token.getLexema() + " no compatible para tipo real (debe terminar con %) en linea:" + token.getNumeroLinea());
                        if (token.getValorTablaTokens() != -53 && string)
                            error("identificador: " + token.getLexema() + " no compatible para tipo string (debe terminar con %) en linea:" + token.getNumeroLinea());
                        if (token.getValorTablaTokens() != -54 && bool)
                            error("identificador: " + token.getLexema() + " no compatible para tipo bool (debe terminar con $) en linea:" + token.getNumeroLinea());
                    }
                    if (esConstante(token.getValorTablaTokens())) {
                        if (token.getValorTablaTokens() != -61 && entero)
                            error("dato: " + token.getLexema() + " incompatible con enteros en linea:" + token.getNumeroLinea());
                        if (token.getValorTablaTokens() != -62 && real)
                            error("dato: " + token.getLexema() + " incompatible con reales en linea:" + token.getNumeroLinea());
                        if (token.getValorTablaTokens() != -63 && string)
                            error("dato: " + token.getLexema() + " incompatible con cadenas en linea:" + token.getNumeroLinea());
                        if ((token.getValorTablaTokens() == -64 || token.getValorTablaTokens() == -65) && bool) {
                        } else if (bool)
                            error("dato: " + token.getLexema() + " incompatible con booleanos en linea:" + token.getNumeroLinea());
                    }
                }
                if (token.getEsIdentificador() == -2 || esConstante(token.getValorTablaTokens())) {
                    switch (token.getValorTablaTokens()) {
                        case -51 -> entero = true;
                        case -52 -> real = true;
                        case -53 -> string = true;
                        case -54 -> bool = true;
                        case -61 -> entero = true;
                        case -62 -> real = true;
                        case -63 -> string = true;
                        case -64 -> bool = true;
                        case -65 -> bool = true;
                    }
                    enLinea = true;
                }
                if (token.getValorTablaTokens() == -75 || token.getValorTablaTokens() == -2) {
                    entero = false;
                    real = false;
                    string = false;
                    bool = false;
                    enLinea = false;
                }
                indice++;
            }
        }

        if (!errorSemantico) {
            escribirTablaSimbolos("src/build/TablaSimbolos.dat", simbolos);
            escribirTablaDirecciones("src/build/TablaDirecciones.dat", ejecucion);
        } else
            System.out.println((char) 27 + "[31m" + "No se puede compilar el programa por errores semanticos");
    }

    public boolean isTokenInSimbolos(String lexema) {
        for (Simbolo simbolo : simbolos)
            if (simbolo.getID().equals(lexema))
                return true;
        return false;
    }

    private boolean tipoDato(int token) {
        return token == -11 || token == -12 || token == -13 || token == -14;
    }

    private boolean esConstante(int token) {
        return token == -61 || token == -62 || token == -63 || token == -64 || token == -65;
    }

    private void error(String mensaje) {
        System.out.println((char) 27 + "[31m" + "ERROR SEMANTICO! " + mensaje + (char) 27 + "[0m");
        errorSemantico = true;
    }

    public static void escribirTablaSimbolos(String archivoSalida, ArrayList<Simbolo> tablaSimbolos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            for (Simbolo simbolo : tablaSimbolos)
                bw.write(simbolo.toString() + "\n");
            System.out.println("Tabla de simbolos guardada en 'TablaSimbolos.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void escribirTablaDirecciones(String archivoSalida, Direccion direccion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            bw.write(direccion.toString() + "\n");
            System.out.println("Tabla de direcciones guardada en 'TablaDirecciones.txt'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

