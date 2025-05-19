package lexico;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.JOptionPane;
import utils.Token;
import utils.TokenType;

public class AnalizadorLexico {
	private final ArrayList<Token> tablaTokens = new ArrayList<>();
	private final ArrayList<Error> errores = new ArrayList<>();
	private boolean errorLexico = false;

	// Mapa de expresiones regulares ordenadas por prioridad
	private static final LinkedHashMap<Pattern, TokenType> REGEX_MAP = new LinkedHashMap<>() {{
		// Comentarios (los ignoramos)
		put(Pattern.compile("^//.*|^/\\*.*?\\*/"), null);

		// Palabras reservadas
		put(Pattern.compile("^program$"), TokenType.PALABRAS_RESERVADAS_PROGRAM);
		put(Pattern.compile("^begin$"), TokenType.PALABRAS_RESERVADAS_BEGIN);
		put(Pattern.compile("^end$"), TokenType.PALABRAS_RESERVADAS_END);
		put(Pattern.compile("^read$"), TokenType.PALABRAS_RESERVADAS_READ);
		put(Pattern.compile("^write$"), TokenType.PALABRAS_RESERVADAS_WRITE);
		put(Pattern.compile("^if$"), TokenType.PALABRAS_RESERVADAS_IF);
		put(Pattern.compile("^else$"), TokenType.PALABRAS_RESERVADAS_ELSE);
		put(Pattern.compile("^while$"), TokenType.PALABRAS_RESERVADAS_WHILE);
		put(Pattern.compile("^repeat$"), TokenType.PALABRAS_RESERVADAS_REPEAT);
		put(Pattern.compile("^until$"), TokenType.PALABRAS_RESERVADAS_UNTIL);
		put(Pattern.compile("^int$"), TokenType.PALABRAS_RESERVADAS_INT);
		put(Pattern.compile("^real$"), TokenType.PALABRAS_RESERVADAS_REAL);
		put(Pattern.compile("^string$"), TokenType.PALABRAS_RESERVADAS_STRING);
		put(Pattern.compile("^bool$"), TokenType.PALABRAS_RESERVADAS_BOOL);
		put(Pattern.compile("^var$"), TokenType.PALABRAS_RESERVADAS_VAR);
		put(Pattern.compile("^then$"), TokenType.PALABRAS_RESERVADAS_THEN);
		put(Pattern.compile("^do$"), TokenType.PALABRAS_RESERVADAS_DO);

		// Constantes
		put(Pattern.compile("^\\d+$"), TokenType.CONSTANTES_ENTERO);
		put(Pattern.compile("^-?\\d+\\.\\d+$"), TokenType.CONSTANTES_REAL);
		put(Pattern.compile("^\".*\"$"), TokenType.CONSTANTES_CADENA);
		put(Pattern.compile("^true$"), TokenType.CONSTANTES_VERDADERO);
		put(Pattern.compile("^false$"), TokenType.CONSTANTES_FALSO);

		// Operadores y símbolos
		put(Pattern.compile("^\\+$"), TokenType.OPERADORES_SUMA);
		put(Pattern.compile("^-$"), TokenType.OPERADORES_RESTA);
		put(Pattern.compile("^\\*$"), TokenType.OPERADORES_MULTIPLICACION);
		put(Pattern.compile("^/$"), TokenType.OPERADORES_DIVISION);
		put(Pattern.compile("^:=$"), TokenType.OPERADORES_ASIGNACION);
		put(Pattern.compile("^%$"), TokenType.OPERADORES_MODULO);
		put(Pattern.compile("^<$"), TokenType.OPERADORES_MENOR_QUE);
		put(Pattern.compile("^>$"), TokenType.OPERADORES_MAYOR_QUE);
		put(Pattern.compile("^<=$"), TokenType.OPERADORES_MENOR_IGUAL_QUE);
		put(Pattern.compile("^>=$"), TokenType.OPERADORES_MAYOR_IGUAL_QUE);
		put(Pattern.compile("^==$"), TokenType.OPERADORES_COMPARACION);
		put(Pattern.compile("^!=$"), TokenType.OPERADORES_DIFERENTE);
		put(Pattern.compile("^\\|\\|$"), TokenType.OPERADORES_O);
		put(Pattern.compile("^&&$"), TokenType.OPERADORES_Y);
		put(Pattern.compile("^!$"), TokenType.OPERADORES_NO);
		put(Pattern.compile("^\\($"), TokenType.CARACTERES_PARENTESIS_IZQUIERDO);
		put(Pattern.compile("^\\)$"), TokenType.CARACTERES_PARENTESIS_DERECHO);
		put(Pattern.compile("^,$"), TokenType.CARACTERES_COMA);
		put(Pattern.compile("^;$"), TokenType.CARACTERES_PUNTO_COMA);
		put(Pattern.compile("^:$"), TokenType.CARACTERES_DOS_PUNTOS);

		// Identificadores (debe ser la última regla)
		put(Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*[#$%&?]$"), null);
	}};

	public void analizarArchivo(String nombreArchivo) {
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			String linea;
			int numLinea = 0;

			while ((linea = br.readLine()) != null) {
				numLinea++;
				procesarLinea(linea, numLinea);
			}

			generarArchivosSalida();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al leer el archivo");
		}
	}

	public void procesarLinea(String linea, int numLinea) {
		List<Token> tokensLinea = extraerTokens(linea, numLinea);
		clasificarTokens(tokensLinea, numLinea);
	}

	public List<Token> extraerTokens(String linea, int numLinea) {
		List<Token> tokens = new ArrayList<>();
		String regex = "\".*?\"|//.*?$|/\\*.*?\\*/|:=|&&|<=|>=|==|!=|\\|\\||\\||-?\\d+\\.\\d*|[-+*/;=,<>():!]|\\d+!|(\"[^\"]+\")|\\b[a-zA-Z\\d_]+\\b[#@%&$?]*|\\.[^ \\t\\n\\r\\f\\v]+|=";
				//"\"(?:\\\\.|[^\"])*\"|:=|<=|>=|==|!=|\\|\\|?|&&?|[()*/%+\\-;,:]|\\w+[#$%&?]?|\\S";

		Matcher matcher = Pattern.compile(regex).matcher(linea);
		while (matcher.find()) {
			String lexema = matcher.group();
			if (!lexema.trim().isEmpty() && !lexema.startsWith("//")) {
				tokens.add(new Token(lexema, numLinea));
			}
		}
		return tokens;
	}

	private void clasificarTokens(List<Token> tokens, int numLinea) {
		for (Token token : tokens) {
			boolean coincidencia = false;
			String lexema = token.getLexema();

			for (Map.Entry<Pattern, TokenType> entry : REGEX_MAP.entrySet()) {
				Matcher matcher = entry.getKey().matcher(lexema);
				if (matcher.matches()) {
					TokenType tipo = entry.getValue();

					if (tipo == null) {
						tipo = clasificarIdentificador(lexema);
					}

					if (tipo != null) {
						token.setToken(tipo);
						tablaTokens.add(token);
						coincidencia = true;
						break;
					}
				}
			}

			if (!coincidencia) {
				errorLexico = true;
				errores.add(new Error(numLinea, lexema, "Token no reconocido"));
				System.err.println("Error léxico en línea " + numLinea + ": " + lexema);
			}
		}
	}

	private TokenType clasificarIdentificador(String lexema) {
		char sufijo = lexema.charAt(lexema.length() - 1);
		switch (sufijo) {
			case '#': return TokenType.IDENTIFICADORES_TIPO_CADENA;
			case '%': return TokenType.IDENTIFICADORES_TIPO_REAL;
			case '&': return TokenType.IDENTIFICADORES_TIPO_ENTERO;
			case '$': return TokenType.IDENTIFICADORES_TIPO_LOGICO;
			case '?': return TokenType.IDENTIFICADORES_ID_GENERAL;
			default: return null;
		}
	}

	public void analizar(List<Token> listaTokens) throws IOException {
		for (Token token : listaTokens) {
			String lexema = token.getLexema();
			boolean tokenReconocido = false;

			// 1. Verificar constantes y palabras reservadas
			for (Map.Entry<Pattern, TokenType> entry : REGEX_MAP.entrySet()) {
				Matcher matcher = entry.getKey().matcher(lexema);
				if (matcher.matches()) {
					TokenType tipo = entry.getValue();

					// Clasificación especial para identificadores
					if (tipo == null && lexema.matches("^[a-zA-Z][a-zA-Z0-9_]*[#$%&?]$")) {
						tipo = clasificarIdentificador(lexema);
					}

					if (tipo != null) {
						token.setToken(tipo);
						tablaTokens.add(token);
						tokenReconocido = true;
						break;
					}
				}
			}

			// 2. Si no se reconoció, marcar error
			if (!tokenReconocido) {
				errorLexico = true;
				errores.add(new Error(token.getNumeroLinea(), lexema, "Token no válido"));
				System.err.println("Error léxico en línea " + token.getNumeroLinea() + ": " + lexema);
			}
		}

		// 3. Generar archivos de salida
		generarArchivosSalida();
	}


	private void generarArchivosSalida() throws IOException {
		// Generar tabla de tokens
		try (FileWriter writer = new FileWriter("TablaTokens.txt")) {
			for (Token token : tablaTokens) {
				writer.write(token.toString() + "\n");
			}
		}

		// Generar tabla de errores
		if (!errores.isEmpty()) {
			try (FileWriter writer = new FileWriter("TablaErrores.txt")) {
				writer.write("Línea\tLexema\tError\n");
				for (Error error : errores) {
					writer.write(error.numLinea + "\t" + error.lexema + "\t" + error.msg + "\n");
				}
			}
		}
	}

	// Getters
	public ArrayList<Token> getTokens() {
		return tablaTokens;
	}

	public boolean notErrorLexico() {
		return !errorLexico;
	}

	// Clase interna para manejo de errores
	private static class Error {
		int numLinea;
		String lexema;
		String msg;

		public Error(int numLinea, String lexema, String msg) {
			this.numLinea = numLinea;
			this.lexema = lexema;
			this.msg = msg;
		}
	}
}