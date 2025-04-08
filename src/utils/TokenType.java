package utils;

public enum TokenType {
    // Palabras Reservadas
    PALABRAS_RESERVADAS_PROGRAMA(-1, -1),
    PALABRAS_RESERVADAS_INICIO(-2, -1),
    PALABRAS_RESERVADAS_FIN(-3, -1),
    PALABRAS_RESERVADAS_LEER(-4, -1),
    PALABRAS_RESERVADAS_ESCRIBIR(-5, -1),
    PALABRAS_RESERVADAS_SI(-6, -1),
    PALABRAS_RESERVADAS_SINO(-7, -1),
    PALABRAS_RESERVADAS_MIENTRAS(-8, -1),
    PALABRAS_RESERVADAS_REPETIR(-9, -1),
    PALABRAS_RESERVADAS_HASTA(-10, -1),
    PALABRAS_RESERVADAS_ENTERO(-11, -1),
    PALABRAS_RESERVADAS_REAL(-12, -1),
    PALABRAS_RESERVADAS_CADENA(-13, -1),
    PALABRAS_RESERVADAS_BOOLEANO(-14, -1),
    PALABRAS_RESERVADAS_VARIABLE(-15, -1),
    PALABRAS_RESERVADAS_ENTONCES(-16, -1),
    PALABRAS_RESERVADAS_HACER(-17, -1),
    // Operadores
    OPERADORES_MULTIPLICACION(-21, -1),
    OPERADORES_DIVISION(-22, -1),
    OPERADORES_SUMA(-24, -1),
    OPERADORES_RESTA(-25, -1),
    OPERADORES_ASIGNACION(-26, -1),
    OPERADORES_MODULO(-27, -1),

    OPERADORES_MENOR_QUE(-31, -1),
    OPERADORES_MENOR_IGUAL_QUE(-32, -1),
    OPERADORES_MAYOR_QUE(-33, -1),
    OPERADORES_MAYOR_IGUAL_QUE(-34, -1),
    OPERADORES_COMPARACION(-35, -1),
    OPERADORES_DIFERENTE(-36, -1),

    OPERADORES_Y(-41, -1),
    OPERADORES_O(-42, -1),
    OPERADORES_NO(-43, -1),
    // Identificadores
    IDENTIFICADORES_TIPO_ENTERO(-51, -2),
    IDENTIFICADORES_TIPO_REAL(-52, -2),
    IDENTIFICADORES_TIPO_CADENA(-53, -2),
    IDENTIFICADORES_TIPO_LOGICO(-54, -2),
    IDENTIFICADORES_ID_GENERAL(-55, -2),
    // Constantes
    CONSTANTES_ENTERO(-61, -1),
    CONSTANTES_REAL(-62, -1),
    CONSTANTES_CADENA(-63, -1),
    CONSTANTES_VERDADERO(-64, -1),
    CONSTANTES_FALSO(-65, -1),
    // Caracteres
    CARACTERES_PARENTESIS_IZQUIERDO(-73, -1),
    CARACTERES_PARENTESIS_DERECHO(-74, -1),
    CARACTERES_PUNTO_COMA(-75, -1),
    CARACTERES_COMA(-76, -1),
    CARACTERES_DOS_PUNTOS(-77, -1);

    private final int valorTablaTokens;
    private final int esIdentificador;

    TokenType(int valorTablaTokens, int esIdentificador) {
        this.valorTablaTokens = valorTablaTokens;
        this.esIdentificador = esIdentificador;
    }

    public int getValorTablaTokens() {
        return valorTablaTokens;
    }

    public int getEsIdentificador() {
        return esIdentificador;
    }

}
