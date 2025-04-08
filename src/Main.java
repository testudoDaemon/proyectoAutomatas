import lexico.AnalizadorLexico;
import lexico.Linea;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import lexico.ManejoArchivo;
import utils.Token;


public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        AnalizadorLexico analizadorLexico = new AnalizadorLexico();
        ManejoArchivo mArch = new ManejoArchivo();
        ArrayList<Linea> lineas;
        ArrayList<Token> lexemas;

        while (true) {
            // Crear menú interactivo
            String[] opciones = {"Análisis Léxico", "Análisis Sintáctico", "Salir"};

            /*int opcionSeleccionada = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una acción a realizar:",
                    "Menú Principal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );*/
            int opcionSeleccionada = Integer.parseInt(bf.readLine());

            switch (opcionSeleccionada) {
                //case 0 -> ejecutarAnalisisLexico(); // Análisis Léxico
                //case 1 -> ejecutarAnalisisSintactico(); // Análisis Sintáctico
                case 0 -> {
                    try{
                        java.io.File archivoTokens = new java.io.File("./TablaTokens.txt");
                        if (archivoTokens.exists()) {
                            archivoTokens.delete(); // Elimina el archivo para iniciar desde cero
                        }
                        analizadorLexico.leerArchivo("./codigofuente.txt");
                        System.out.println("Análisis léxico completado. Tabla de tokens generada.");
                        File tablatokens = new File("src/build/TablaTokens.dat"); // Nombre del archivo de la tabla de tokens
                        FileWriter writerTokens = new FileWriter(tablatokens);
                        lineas = mArch.leer("./codigofuente.txt");
                        for (Linea linea : lineas) {
                            lexemas = analizadorLexico.analizarLinea(linea);
                            analizadorLexico.analizarLexemas(lexemas);
                        }

                    }catch (Exception e){
                        System.out.println("Error durante el analisis" + e.getMessage());
                    }
                }
                case 1 -> {
                    JOptionPane.showMessageDialog(null, "Saliendo del programa.");
                    System.exit(0); // Salir del programa
                }
                default -> JOptionPane.showMessageDialog(null, "Opción inválida, inténtelo nuevamente.");
            }
        }
    }
    /*
    private static void ejecutarAnalisisLexico() {
        try {
            // Eliminar el archivo TablaTokens.txt si existe
            java.io.File archivoTokens = new java.io.File("./TablaTokens.txt");
            if (archivoTokens.exists()) {
                archivoTokens.delete(); // Elimina el archivo para iniciar desde cero
            }

            // Ejecutar el análisis léxico
            lexico.AnalizadorLexico.leerArchivo("./codigofuente.txt");
            JOptionPane.showMessageDialog(null, "Análisis léxico completado. Tabla de tokens generada.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error durante el análisis léxico: " + e.getMessage());
        }
    }*/


    /*private static void ejecutarAnalisisSintactico() {
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
        return lexico.AnalizadorLexico.cargarTokens(archivoTokens);
    }*/
}
