import utils.Token;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class analizadorLexico {

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

    public static final ArrayList<Integer> valorTokens = new ArrayList<Integer>();
    public static final ArrayList<String> tokens = new ArrayList<>();
    public static final String tablaTokensSalida = "./TablaTokens.txt";
    //public static final String tablaTokensSalidaErrores = "./TablaErrores.txt"; comentado pq no se usa

    public static final HashMap<Integer, Integer> posTablaSalida = new HashMap<>();

    // este metodo no lo esta usando ningun otro metodo, el importante que se debe ejecutar es
    // incializarMapas(); lo hare estatico
    public analizadorLexico() {
        inicializarMapas();
        try {
            FileWriter fw = new FileWriter(tablaTokensSalida);
            fw.write("Tabla de Token\n");
            fw.write("Tabla de Errores\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public static void leerArchivo(String nombreArchivo) {
        boolean classLexema = false;
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
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

    public static boolean clasificacionToken(String lexema, int numLinea) {
        boolean noClasificadoLexeme = false;
        String[] tokens = analizarLexemas(lexema);

        for (String token : tokens) {
            // Excluimos comentarios y vacíos
            if (token.startsWith("//") || token.matches("^/\\*.*?\\*/$") || token.isEmpty()) {
                continue; // Saltamos comentarios y tokens vacíos
            }

            // Clasificación del token
            int valorToken = getValorToken(token);
            System.out.println("valor token " + valorToken);
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

    private static boolean esTokenValido(String token) {
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


    public static int getValorToken(String key) {
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


    public static String[] analizarLexemas(String lexema) {
        // La expresión regular ahora incluye / correctamente antes de identificadores con '%'
        Pattern pattern = Pattern.compile(
                "\".*?\"|//.*?$|/\\*.*?\\*/|:=|&&|<=|>=|==|!=|\\|\\||\\||-?\\d+\\.\\d*|[-+*/;=,<>():!]|\\d+!|(\"[^\"]+\")|\\b[a-zA-Z\\d_]+\\b[#@%&$?]*|\\.[^ \\t\\n\\r\\f\\v]+|="
        );
        Matcher matcher = pattern.matcher(lexema);
        ArrayList<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            String cadena = matcher.group();
            tokens.add(cadena);
        }
        return tokens.toArray(new String[0]);
    }



    public static void escribirEnArchivo(String token, int valorToken, int numLinea) {
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
        Path path = Paths.get("tabla_errores.txt");

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
                String[] partes = linea.split("\\s+");
                if (partes.length >= 4) {
                    tokens.add(new Token(partes[0], Integer.parseInt(partes[1]), Integer.parseInt(partes[2]), Integer.parseInt(partes[3])));
                } else {
                    System.err.println("Línea no válida en el archivo de tokens: " + linea);
                }
            }
        }
        return tokens;
    }

}