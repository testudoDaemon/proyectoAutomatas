
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class AnalizadorSintactico {
    private Set<String> errorMessages = new HashSet<>();
    private final ArrayList<Token> numTokens;
    private int indice;
    private Token siguienteToken;

    public AnalizadorSintactico(ArrayList<Token> numTokens) {
        this.numTokens = numTokens;
        this.indice = 0;
        this.siguienteToken = null;
    }

    public void analizar() {
        try {
            encabezado();
            declaraciones(false);
            estructuraPrograma();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hayTokensRestantes() {
        return indice < numTokens.size();
    }

    private void encabezado() {

        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -1) {
            error("Se esperaba la palabra clave 'programa'");
        }
        avanza();

        if (!hayTokensRestantes()) {
            error("Se esperaba un identificador después de la palabra clave 'program'");
        }
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -55) {
            error("Se esperaba un identificador de tipo general (?)");
        }
        avanza();

        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -75) {
            error("Se esperaba un punto y coma ';' después del identificador ");
        }
        avanza();
    }

    private void declaraciones(boolean varEncontrada) {
        if (!hayTokensRestantes() || numTokens.get(indice).getValorTablaTokens() == -2) {
            return;
        }
        Token tokenActual = numTokens.get(indice);

        if (tokenActual.getValorTablaTokens() == -15) {
            if (!varEncontrada) {
                avanza();
                varEncontrada = true;
            } else {
                error("La palabra clave 'variables' ya fue declarada anteriormente en la línea " + tokenActual.getNumeroLinea());
            }
        } else {
            if (!varEncontrada) {
                error("Se esperaba la palabra clave 'variables' en la línea " + tokenActual.getNumeroLinea());
            } else {
                if (!declaracionesTipo(tokenActual.getValorTablaTokens()))
                    error("Se esperaba un tipo de dato valido o la palabra ´inicio', en la linea: " + tokenActual.getNumeroLinea());
                avanza();
                tokenActual = numTokens.get(indice);
                declaracionVariable();
            }
        }

        declaraciones(varEncontrada);
    }
    private void declaracionVariable() {
        // Manejo de las declaraciones de variables
        Token tokenActual = numTokens.get(indice);

        // Verificar y consumir identificadores
        tokenActual = numTokens.get(indice);
        if (identificador(tokenActual.getValorTablaTokens())) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -76) {
                avanza();
            } else if (tokenActual.getValorTablaTokens() == -75) {
                avanza();
                return;
            } else {
                error("Se esperaba ',' o ';' después del identificador en la línea " + tokenActual.getNumeroLinea());
            }
        } else {
            error("Se esperaba un identificador válido en la línea " + tokenActual.getNumeroLinea());
        }
        declaracionVariable();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void estructuraPrograma() {
        // Se verifica que comienze con 'inicio'
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -2)
            error("Se esperaba la palabra clave 'inicio' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        estructuraSentencias();
        tokenActual = numTokens.get(indice);
        // Se verifica que termine con un 'fin'
        if (indice == numTokens.size() - 1 && tokenActual.getValorTablaTokens() != -3)
            error("Se esperaba la palabra clave 'fin' en la línea " + tokenActual.getNumeroLinea());
        // Si termina con un end se termina el programa sin errores
        if (tokenActual.getValorTablaTokens() == -3 && indice == numTokens.size() - 1) {
            aceptar();
            return;
        }
    }

    private boolean esPrintable(int token) {
        return identificador(token) || esConstante(token);
    }
    private boolean identificador(int token) {
        return token <= -51 && token >= -54;
    }
    private boolean opLogicos(int token) {
        return token == -41 || token == -42;
    }
    private boolean opAritmeticos(int token) {
        return token == -21 || token == -22 || token == -23 || token == -24 || token == -25 || token == -27;
    }

    private boolean opRelacionales(int token) {
        return token == -31 || token == -32 || token == -33 || token == -34 || token == -35 || token == -36;
    }
    private boolean declaracionesTipo(int token) {
        return tipoDato(token);
    }
    private boolean tipoDato(int token) {
        return token == -11 || token == -12 || token == -13 || token == -14;
    }
    private boolean esConstante(int token) {
        return token >= -65 && token <= -61;
    }
    private boolean esOperando(int token) {
        return token <= -21 && token >= -25 || token == -27;
    }
    private boolean esLogico(int token) {
        return token <= -31 && token >= -42;
    }
    private boolean operandos(int token) {
        return identificador(token) || token == -61 || token == -62 || token == -63 || token == -64 || token == -65;
    }

    private void avanza() {
        if (indice < numTokens.size()) {
            indice++;
            if (indice < numTokens.size() - 1)
                siguienteToken = numTokens.get(indice + 1);
            else
                siguienteToken = null;
        } else {
            siguienteToken = null; // Ya no hay más tokens para analizar
        }
    }
    private void estructuraSentencias() {
        Token tokenActual = numTokens.get(indice);
        comprobarSentencias(tokenActual);
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -3 || !hayTokensRestantes())
            return;
        else
            estructuraSentencias();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // bloque E/S y bloque Sentencia
    private void comprobarSentencias(Token tokenActual) {
        switch (tokenActual.getValorTablaTokens()) {
            case -4 -> lectura(); // Esto verifica la estructura de un 'leer'
            case -5 -> escribirEstructura(); // Esto verifica la estructura de un 'escribir'
            case -6 -> siEstructura(); // Esto verifica la estructura de un 'si'
            case -7 -> error("Se esperaba un 'if' en la línea " + tokenActual.getNumeroLinea()); // Esto verifica la estructura
            // de un 'sino'
            case -8, -9 -> repetitivas(); // Esto verifica la estructura de un 'mientras', 'repetir'
            case -51, -52, -53, -54 -> asignacion(); // Esto verifica la estructura de una asignacion
            default -> error("Se esperaba una sentencia válida en la línea " + tokenActual.getNumeroLinea());
        }
    }
    private void lectura() {
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -4) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -73) {
                avanza();
                tokenActual = numTokens.get(indice);
                if (esPrintable(tokenActual.getValorTablaTokens())) {
                    avanza();
                    tokenActual = numTokens.get(indice);
                    if (tokenActual.getValorTablaTokens() == -74) {
                        avanza();
                        tokenActual = numTokens.get(indice);
                        if (tokenActual.getValorTablaTokens() == -75) {
                            avanza();
                            // Aqui se regresa a la condicion
                        } else {
                            error("Se esperaba la palabra ';' en la linea " + tokenActual.getNumeroLinea());
                        }
                    } else {
                        error("Se esperaba la palabra ')' en la linea " + tokenActual.getNumeroLinea());
                    }
                }
            } else {
                error("Se esperaba la palabra '(' en la linea " + tokenActual.getNumeroLinea());
            }
        } else {
            error("Se esperaba la palabra 'leer' en la linea " + tokenActual.getNumeroLinea());
        }
    }
    private void escribirEstructura() {
        avanza();
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -73)
            error("Se esperaba '(' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (!esPrintable(tokenActual.getValorTablaTokens()))
            error("Se esperaba un identificador o un literal en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -74)
            error("Se esperaba ')' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -75)
            error("Se esperaba ';' en la línea " + tokenActual.getNumeroLinea());
        avanza();
    }
    private void siEstructura() {
        avanza();
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -73)
            error("Se esperaba '(' en la línea " + tokenActual.getNumeroLinea());
        condicion();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -74)
            error("Se esperaba ')' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -16)
            error("Se esperaba la palabra clave 'then' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -2)
            error("Se esperaba la palabra clave 'begin' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        estructuraSentencias();
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -7)
            sinoEstructura();
    }
    private void sinoEstructura() {
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -7)
            error("Se esperaba la palabra clave 'sino' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -2)
            error("Se esperaba la palabra clave 'inicio' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        estructuraSentencias();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() != -3)
            error("Se esperaba la palabra clave 'fin' en la línea " + tokenActual.getNumeroLinea());
        avanza();
    }
    private void condicion() {
        avanza();
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -43 && identificador(siguienteToken.getValorTablaTokens())) {
            avanza();
            tokenActual = numTokens.get(indice);
        } else if (tokenActual.getValorTablaTokens() == -43 && !identificador(siguienteToken.getValorTablaTokens())) {
            error("Se esperaba un identificador en la línea " + tokenActual.getNumeroLinea());
        }
        if (identificador(tokenActual.getValorTablaTokens()) || esConstante(tokenActual.getValorTablaTokens())) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (esOperando(tokenActual.getValorTablaTokens()))
                aritmetica();
            if (esLogico(tokenActual.getValorTablaTokens()))
                logica();
        } else if (tokenActual.getValorTablaTokens() == -73) {
            condicion();
            avanza();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -74)
                return;
            if (tokenActual.getValorTablaTokens() == -73) {
                condicion();
                avanza();
                tokenActual = numTokens.get(indice);
            }
        } else {
            error("Se esperaba un identificador o una constante en la línea " + tokenActual.getNumeroLinea());
        }
        if (tokenActual.getValorTablaTokens() == -74)
            avanza();
    }
    private void repetitivas() {
        Token tokenActual = numTokens.get(indice);
        switch (tokenActual.getValorTablaTokens()) {
            case -8:
                mientrasEstructura();
                break;
            case -9:
                repetirEstructura();
                break;
            default:
                error("Se esperaba una estructura repetitiva en la linea " + tokenActual.getNumeroLinea());
        }
    }
    public void mientrasEstructura() {
        avanza();
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -73) {
            condicion();
            avanza();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -17) {
                avanza();
                tokenActual = numTokens.get(indice);
                if (tokenActual.getValorTablaTokens() == -2) {
                    avanza();
                    estructuraSentencias();
                    tokenActual = numTokens.get(indice);
                    if (tokenActual.getValorTablaTokens() == -3) {
                        avanza();
                        // Aqui se regresa a la condicion
                    } else
                        error("Se esperaba la palabra clave 'fin' en la linea " + tokenActual.getNumeroLinea());
                } else {
                    error("Se esperaba la palabra clave 'inicio' en la linea " + tokenActual.getNumeroLinea());
                }
            } else {
                error("Se esperaba la palabra clave 'hacer' en la linea " + tokenActual.getNumeroLinea());
            }

        }
    }
    public void repetirEstructura() {
        avanza();
        Token tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -2) {
            avanza();
            estructuraSentencias();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -3) {
                avanza();
                tokenActual = numTokens.get(indice);
                if (tokenActual.getValorTablaTokens() == -10) {
                    avanza();
                    tokenActual = numTokens.get(indice);
                    if (tokenActual.getValorTablaTokens() == -73) {
                        condicion();
                        tokenActual = numTokens.get(indice);
                        if (tokenActual.getValorTablaTokens() == -74) {
                            avanza();
                            tokenActual = numTokens.get(indice);
                            if (tokenActual.getValorTablaTokens() == -75) {
                                avanza();
                            } else {
                                error("Se esperaba la palabra clave ';' en la linea " + tokenActual.getNumeroLinea());
                            }
                        } else {
                            error("Se esperaba la palabra clave ')' en la linea " + tokenActual.getNumeroLinea());
                        }
                    } else {
                        error("Se esperaba la palabra clave '(' en la linea " + tokenActual.getNumeroLinea());
                    }
                } else {
                    error("Se esperaba la palabra clave 'hasta' en la linea " + tokenActual.getNumeroLinea());
                }
            } else {
                error("Se esperaba la palabra clave 'fin' en la linea " + tokenActual.getNumeroLinea());
            }
        } else {
            error("Se esperaba la palabra clave 'inicio' en la linea " + tokenActual.getNumeroLinea());
        }
    }
    private void asignacion() {
        Token tokenActual = numTokens.get(indice);
        if (identificador(tokenActual.getValorTablaTokens())) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (tokenActual.getValorTablaTokens() == -26 || tokenActual.getValorTablaTokens() == -73) { // utils.Token de asignación :=
                avanza();
                if (expresion()) {
                    tokenActual = numTokens.get(indice);
                    if (tokenActual.getValorTablaTokens() == -75) { // utils.Token de ;
                        avanza();
                    } else {
                        error("Se esperaba ';' después de la expresión. Lexema: " + tokenActual.getLexema()
                                + ", Línea: " + tokenActual.getNumeroLinea());
                    }
                } else {
                    error("Error en la expresión. Lexema: " + tokenActual.getLexema() + ", Línea: "
                            + tokenActual.getNumeroLinea());
                }
            } else {
                error("Se esperaba '=' después del identificador. Lexema: " + tokenActual.getLexema() + ", Línea: "
                        + tokenActual.getNumeroLinea());
            }
        } else {
            error("Se esperaba un identificador válido. Lexema: " + tokenActual.getLexema() + ", Línea: "
                    + tokenActual.getNumeroLinea());
        }
    }
    private boolean expresion() {
        if (termino()) {
            while (opRelacionales(numTokens.get(indice).getValorTablaTokens()) || opAritmeticos(numTokens.get(indice).getValorTablaTokens())
                    || opLogicos(numTokens.get(indice).getValorTablaTokens())) { // Verificar cualquier operador relacional
                avanza();
                if (!termino()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    private void aritmetica() {
        Token tokenActual;

        avanza();
        tokenActual = numTokens.get(indice);
        if (!identificador(tokenActual.getValorTablaTokens()) && !esConstante(tokenActual.getValorTablaTokens()))
            error("Se esperaba un identificador o una constante en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (esOperando(tokenActual.getValorTablaTokens())) {
            aritmetica();
            return;
        }
        if (tokenActual.getValorTablaTokens() != -35)
            error("Se esperaba un '==' en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (!identificador(tokenActual.getValorTablaTokens()) && !esConstante(tokenActual.getValorTablaTokens()))
            error("Se esperaba un identificador o una constante en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (esOperando(tokenActual.getValorTablaTokens()))
            aritmetica();
        if (esLogico(tokenActual.getValorTablaTokens()))
            logica();
    }
    public void logica() {
        Token tokenActual;
        avanza();
        tokenActual = numTokens.get(indice);
        if (tokenActual.getValorTablaTokens() == -43) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (!identificador(tokenActual.getValorTablaTokens()))
                error("Se esperaba un identificador en la línea " + tokenActual.getNumeroLinea());
        }
        if (tokenActual.getValorTablaTokens() == -73) {
            condicion();
            avanza();
            return;
        }
        if (!identificador(tokenActual.getValorTablaTokens()) && !esConstante(tokenActual.getValorTablaTokens()))
            error("Se esperaba un identificador o una constante en la línea " + tokenActual.getNumeroLinea());
        avanza();
        tokenActual = numTokens.get(indice);
        if (esLogico(tokenActual.getValorTablaTokens()))
            logica();
        if (esOperando(tokenActual.getValorTablaTokens()))
            aritmetica();
    }
    private boolean termino() {
        Token tokenActual; // Obtener el token actual
        if (factor()) {
            tokenActual = numTokens.get(indice);
            while (opRelacionales(tokenActual.getValorTablaTokens()) || opAritmeticos(tokenActual.getValorTablaTokens())
                    || opLogicos(tokenActual.getValorTablaTokens())) {
                avanza();
                if (!factor()) {
                    return false;
                }
                tokenActual = numTokens.get(indice);
            }
            return true;
        }
        return false;
    }
    private boolean factor() {
        Token tokenActual = numTokens.get(indice);
        if (operandos(tokenActual.getValorTablaTokens())) { // Número o identificador
            avanza();
            return true;
        } else if (tokenActual.getValorTablaTokens() == -73) { // Paréntesis (
            avanza();
            if (expresion()) {
                tokenActual = numTokens.get(indice);
                if (tokenActual.getValorTablaTokens() == -74) { // Paréntesis )
                    avanza();
                    return true;
                } else {
                    error("Se esperaba ')' después de la expresión. Lexema: " + tokenActual.getLexema() + ", Línea: "
                            + tokenActual.getNumeroLinea());
                    return false;
                }
            } else {
                error("Error en la expresión dentro del paréntesis. Lexema: " + tokenActual.getLexema() + ", Línea: "
                        + tokenActual.getNumeroLinea());
                return false;
            }
        } else if (tokenActual.getValorTablaTokens() == -43) {
            avanza();
            tokenActual = numTokens.get(indice);
            if (identificador(tokenActual.getValorTablaTokens())) {
                avanza();
                return true;
            } else {
                error("Se esperaba un identificador '." + ", Línea: " + tokenActual.getNumeroLinea());
                return false;
            }
        } else {
            error("Se esperaba un factor válido. Lexema: " + tokenActual.getLexema() + ", Línea: "
                    + tokenActual.getNumeroLinea());
            return false;
        }
    }

    private void aceptar() {
        System.out.println((char) 27 + "[32m" + "El análisis sintáctico ha finalizado sin errores." + (char) 27 + "[0m");
        //System.out.println("El análisis sintáctico ha finalizado sin errores.");
        System.out.printf((char) 27 + "[32m" + "El programa es correcto\n" + (char) 27 + "[0m");
    }
    private void error(String mensaje) {
        if (!errorMessages.contains(mensaje)) {
            errorMessages.add(mensaje);
            System.out.println((char) 27 + "[31m" + "ERROR SINTACTICO! " + mensaje + (char) 27 + "[0m");
        }
    }
}
