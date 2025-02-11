import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static String tokensTabla = "./TablaTokens.txt";

    public static void main(String[] args) {
        ArrayList<Token> tokens = new ArrayList<>();
        leerTablaTokens(tokens);
        if (tokens.isEmpty()) {
            System.out.println("ARCHIVO VACÍO");
            return;
        }
        AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(tokens);
        analizadorSintactico.analizar();
    }

    // metodo para leer la tabla de tokens
    public static void leerTablaTokens(ArrayList<Token> tokens) {
        File file = new File(tokensTabla);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Archivo creado: " + tokensTabla);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Usar expresión regular para dividir la línea respetando comillas
                String regex = "\"([^\"]*)\"|(\\S+)";
                ArrayList<String> parts = new ArrayList<>();
                Matcher matcher = Pattern.compile(regex).matcher(line);
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        parts.add(matcher.group(1)); // Cadena entre comillas
                    } else if (matcher.group(2) != null) {
                        parts.add(matcher.group(2)); // Tokens normales
                    }
                }

                if (parts.size() >= 4) {
                    try {
                        // Crear un objeto Token
                        String lexema = parts.get(0);
                        String valorTablaTokens = parts.get(1).trim();
                        int numeroLinea = Integer.parseInt(parts.get(3).trim());
                        Token token = new Token(lexema, valorTablaTokens, numeroLinea);
                        tokens.add(token);
                        System.out.println("Token cargado: " + token);
                    } catch (NumberFormatException e) {
                        System.out.println("Error al interpretar la línea: " + line);
                    }
                } else {
                    System.out.println("Línea no válida en el archivo: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
