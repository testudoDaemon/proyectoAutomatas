import utils.Token;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        while (true) {
            // Crear menú interactivo
            String[] opciones = {"Análisis Léxico", "Análisis Sintáctico", "Salir"};
            int opcionSeleccionada = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una acción a realizar:",
                    "Menú Principal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            switch (opcionSeleccionada) {
                case 0 -> ejecutarAnalisisLexico(); // Análisis Léxico
                case 1 -> ejecutarAnalisisSintactico(); // Análisis Sintáctico
                case 2 -> {
                    JOptionPane.showMessageDialog(null, "Saliendo del programa.");
                    System.exit(0); // Salir del programa
                }
                default -> JOptionPane.showMessageDialog(null, "Opción inválida, inténtelo nuevamente.");
            }
        }
    }

    private static void ejecutarAnalisisLexico() {
        try {
            analizadorLexico.leerArchivo("./codigofuente.txt");
            JOptionPane.showMessageDialog(null, "Análisis léxico completado. Tabla de tokens generada.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error durante el análisis léxico: " + e.getMessage());
        }
    }

    private static void ejecutarAnalisisSintactico() {
        try {
            // Cargar tokens del archivo generado
            ArrayList<Token> tokens = cargarTokensDesdeArchivo("./TablaTokens.txt");

            // Ejecutar el análisis sintáctico
            AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(tokens);
            analizadorSintactico.analizar();

            JOptionPane.showMessageDialog(null, "Análisis sintáctico completado.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error durante el análisis sintáctico: " + e.getMessage());
        }
    }

    private static ArrayList<Token> cargarTokensDesdeArchivo(String archivoTokens) throws Exception {
        return analizadorLexico.cargarTokens(archivoTokens);
    }
}
