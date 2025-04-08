package lexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import utils.Token;

public class AnalizadorLexico {

    public static final HashMap<String, Integer> identificadores = new HashMap<>();
    public static final HashMap<String, Integer> palabrasReservadas = new HashMap<>();
    public static final HashMap<String, Integer> operadoresAritmeticos = new HashMap<>();
    public static final HashMap<String, Integer> operadoresRelacionales = new HashMap<>();
    public static final HashMap<String, Integer> operadoresLogicos = new HashMap<>();
    public static final HashMap<String, Integer> caracteres = new HashMap<>();
    public static final HashMap<String, Integer> numEnteros = new HashMap<>();
    public static final HashMap<String, Integer> numReales = new HashMap<>();
    public static final HashMap<String, Integer> cadenaString = new HashMap<>();
    public static final HashMap<String, ArrayList<Integer>> palabrasDelArchivo = new HashMap<>();

    //public static final ArrayList<Integer> valorTokens = new ArrayList<Integer>();
    public static final ArrayList<Token> tokens = new ArrayList<>();
    public static final String tablaTokensSalida = "./TablaTokens.txt";
   //public static final String tablaTokensSalidaErrores = "./TablaErrores.txt";

    public static final HashMap<Integer, Integer> posTablaSalida = new HashMap<>();

    public static void inicializarMapas() {
        identificadores.put("[a-zA-Z]+[\\$]", -53);
        identificadores.put("[a-zA-Z]+[\\%]", -52);
        identificadores.put("[a-zA-Z]+[\\&]", -51);
        identificadores.put("[a-zA-Z]+[\\#]", -54);
        identificadores.put("[a-zA-Z]+[\\@]", -55);

        palabrasReservadas.put("^programa$", -1);
        palabrasReservadas.put("^inicio$", -2);
        palabrasReservadas.put("^fin$", -3);
        palabrasReservadas.put("^leer$", -4);
        palabrasReservadas.put("^escribir$", -5);
        palabrasReservadas.put("^si$", -6);
        palabrasReservadas.put("^sino$", -7);
        palabrasReservadas.put("^mientras$", -8);
        palabrasReservadas.put("^repetir$", -9);
        palabrasReservadas.put("^hasta$", -10);
        palabrasReservadas.put("^entero$", -11);
        palabrasReservadas.put("^real$", -12);
        palabrasReservadas.put("^cadena$", -13);
        palabrasReservadas.put("^logico$", -14);
        palabrasReservadas.put("^variables$", -15);
        palabrasReservadas.put("^entonces$", -16);
        palabrasReservadas.put("^hacer$", -17);

        operadoresAritmeticos.put("\\+", -24);
        operadoresAritmeticos.put("\\-", -25);
        operadoresAritmeticos.put("\\*", -21);
        operadoresAritmeticos.put("\\/", -22);
        operadoresAritmeticos.put("\\=", -26);

        operadoresRelacionales.put("\\<", -31);
        operadoresRelacionales.put("\\>", -33);
        operadoresRelacionales.put("\\<=", -32);
        operadoresRelacionales.put("\\>=", -34);
        operadoresRelacionales.put("\\==", -35);
        operadoresRelacionales.put("\\!=", -36);

        operadoresLogicos.put("\\&&", -41);
        operadoresLogicos.put("\\|\\|?", -42);
        operadoresLogicos.put("\\!", -43);

        cadenaString.put("\"[^\"]+\"", -63);

        operadoresLogicos.put("true", -64);
        operadoresLogicos.put("false", -65);

        caracteres.put("\\(", -73);
        caracteres.put("\\)", -74);
        caracteres.put(";", -75);
        caracteres.put(",", -76);

        numEnteros.put("^\\d+$", -61);
        numReales.put("^-?\\d+\\.\\d+$", -62);

        posTablaSalida.put(-51, -2);
        posTablaSalida.put(-52, -2);
        posTablaSalida.put(-53, -2);
        posTablaSalida.put(-54, -2);
        posTablaSalida.put(-55, -1);
    }
    public static ArrayList<Error> errores = new ArrayList<Error>();
    public void leerArchivo(String nombreArchivo) {
        inicializarMapas();
        boolean classLexema = false;
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            ArrayList<Token> linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.replaceAll("/\\.?\\*/", "").trim();
                classLexema = clasificacionToken(linea, numLinea);
            }
            tablaErrores(errores);
            if (errores.isEmpty()) {
                System.out.println((char) 27 + "[32m" + "El archivo se leyó correctamente y no tiene errores. Analizador Léxico completado." + (char) 27 + "[0m");
            } else {
                System.out.println("El archivo se leyó correctamente pero tiene errores.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo");
        }
    }

    public boolean clasificacionToken(ArrayList<Token> lexema, int numLinea) {
        boolean noClasificadoLexeme = false;
        String[] tokens = analizarLexemas(lexema);

        for (String token : tokens) {
            // Excluimos comentarios y vacíos
            if (token.startsWith("//") || token.matches("^/\\*.*?\\*/$") || token.isEmpty()) {
                continue; // Saltamos comentarios y tokens vacíos
            }

            // Clasificación del token
            int valorToken = getValorToken(token);
            if (valorToken != 0) {
                escribirEnArchivo(token, valorToken, numLinea);
            } else {
                // Validación final antes de considerar el token como error
                if (!esTokenValido(token)) {
                    noClasificadoLexeme = true;

                    // Evita duplicados en la lista de errores
                    if (errores.stream().noneMatch(e -> e.lexema.equals(token) && e.numLinea == numLinea)) {
                        errores.add(new Error(numLinea, token, "Lexema no clasificado"));
                    }
                }
            }
        }
        return noClasificadoLexeme;
    }

    private boolean esTokenValido(String token) {
        HashMap<String, Integer> combinedMap = new HashMap<>();
        combinedMap.putAll(identificadores);
        combinedMap.putAll(palabrasReservadas);
        combinedMap.putAll(operadoresAritmeticos);
        combinedMap.putAll(operadoresRelacionales);
        combinedMap.putAll(operadoresLogicos);
        combinedMap.putAll(cadenaString);
        combinedMap.putAll(caracteres);
        combinedMap.putAll(numEnteros);
        combinedMap.putAll(numReales);

        for (String regex : combinedMap.keySet()) {
            if (Pattern.matches(regex, token)) {
                return true; // El token es válido
            }
        }
        return false; // El token no pertenece a ninguna categoría
    }


    public int getValorToken(String key) {
        HashMap<String, Integer> combinedMap = new HashMap<>();
        combinedMap.putAll(identificadores);
        combinedMap.putAll(palabrasReservadas);
        combinedMap.putAll(operadoresAritmeticos);
        combinedMap.putAll(operadoresRelacionales);
        combinedMap.putAll(operadoresLogicos);
        combinedMap.putAll(cadenaString);
        combinedMap.putAll(caracteres);
        combinedMap.putAll(numEnteros);
        combinedMap.putAll(numReales);

        for (String regex : combinedMap.keySet()) {
            if (Pattern.matches(regex, key)) {
                return combinedMap.get(regex); // Retorna el valor del token si coincide
            }
        }
        return 0; // Retorna 0 si no hay coincidencia
    }


    public String[] analizarLexemas(ArrayList<Token> lexema) {
        // La expresión regular ahora incluye / correctamente antes de identificadores con '%'
        Pattern pattern = Pattern.compile(
                "\".*?\"|//.*?$|/\\*.*?\\*/|:=|&&|<=|>=|==|!=|\\|\\||\\||-?\\d+\\.\\d*|[-+*/;=,<>():!]|\\d+!|(\"[^\"]+\")|\\b[a-zA-Z\\d_]+\\b[#@%&$?]*|\\.[^ \\t\\n\\r\\f\\v]+|="
        );
        Matcher matcher = pattern.matcher((CharSequence) lexema);
        ArrayList<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            String cadena = matcher.group();
            tokens.add(cadena);
        }
        return tokens.toArray(new String[0]);
    }


    public void escribirEnArchivo(String token, int valorToken, int numLinea) {
        int posicionTabla = posTablaSalida.getOrDefault(valorToken, -1);

        try (FileWriter fw = new FileWriter(tablaTokensSalida, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.printf("%s %d %d %d%n", token, valorToken, posicionTabla, numLinea);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo" + e.getMessage());
        }
    }
    private static class Error {
        public int numLinea;
        public String lexema;
        public String msg;

        public Error(int numLinea, String lexema, String msg) {
            this.numLinea = numLinea;
            this.lexema = lexema;
            this.msg = msg;
        }
    }

    public static void tablaErrores(ArrayList<Error> errores) {
        Path path = Paths.get("TablaErrores.txt");

        try {
            // Si ya existe el archivo, lo borra para evitar datos duplicados
            Files.deleteIfExists(path);
            Files.createFile(path);

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                for (Error error : errores) {
                    String errorStr = String.format("%d        %s        %s%n", error.numLinea, error.lexema, error.msg);

                    // Evita escribir errores duplicados en el archivo
                    if (!Files.readAllLines(path).contains(errorStr.trim())) {
                        writer.write(errorStr);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al manejar el archivo de errores: " + e.getMessage());
        }
    }

    public static ArrayList<Token> cargarTokens(String archivoTokens) throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoTokens))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // Usar expresión regular para dividir la línea respetando comillas
                String regex = "\"([^\"]*)\"|(\\S+)";
                ArrayList<String> partes = new ArrayList<>();
                Matcher matcher = Pattern.compile(regex).matcher(linea);
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        partes.add(matcher.group(1)); // Cadena entre comillas
                    } else if (matcher.group(2) != null) {
                        partes.add(matcher.group(2)); // Tokens normales
                    }
                }

                if (partes.size() >= 4) {
                    try {
                        String lexema = partes.get(0);
                        int valorTablaTokens = Integer.parseInt(partes.get(1).trim());
                        int posicionTabla = Integer.parseInt(partes.get(2).trim());
                        int numeroLinea = Integer.parseInt(partes.get(3).trim());

                        Token token = new Token(lexema, valorTablaTokens, posicionTabla, numeroLinea);
                        tokens.add(token);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al interpretar la línea: " + linea);
                    }
                } else {
                    System.err.println("Línea no válida en el archivo de tokens: " + linea);
                }
            }
        }
        return tokens;
    }

    /**
     * Método que analiza una línea de código y genera una lista de objetos Token.
     * Se utiliza la misma lógica de separación por expresión regular que en cargarTokens.
     *
     * @param linea Objeto Linea que contiene el contenido y el número de la línea.
     * @return ArrayList<Token> con los tokens extraídos de la línea.
     */
    public ArrayList<Token> analizarLinea(Linea linea) {
        ArrayList<Token> tokens = new ArrayList<>();

        // Obtener el contenido de la línea y su número
        String contenido = linea.getLinea();
        int numeroLinea = linea.getNumeroLinea();

        // Definir la expresión regular para extraer tokens:
        // Se captura cualquier cadena entre comillas o cualquier secuencia no vacía
        String regex = "\"([^\"]*)\"|(\\S+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contenido);

        // Lista para almacenar las partes extraídas
        ArrayList<String> partes = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                partes.add(matcher.group(1)); // Cadena entre comillas
            } else if (matcher.group(2) != null) {
                partes.add(matcher.group(2)); // Token normal
            }
        }

        // Se asume que cada token debería tener, al menos, la información necesaria para su creación.
        // Aquí, por ejemplo, se puede decidir que cada token se cree con un valor temporal de 0 para el tokenType
        // y posición, ya que estos valores se pueden asignar en una fase posterior.
        for (String lexema : partes) {
            // Puedes agregar validaciones para omitir tokens vacíos o comentarios, si es necesario.
            if (lexema.trim().isEmpty() || lexema.startsWith("//")) {
                continue;
            }

            // Crear el objeto Token
            Token token = new Token(lexema, 0, 0, numeroLinea);
            tokens.add(token);
        }

        return tokens;
    }


    public ArrayList<Token> getTokens() {
        return tokens;
    }

}
