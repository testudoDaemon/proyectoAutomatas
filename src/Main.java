import lexico.AnalizadorLexico;
import lexico.Linea;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import lexico.ManejoArchivo;
import utils.Token;
import semantico.AnalizadorSemantico;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        AnalizadorLexico analizadorLexico = new AnalizadorLexico();
        AnalizadorSemantico analizadorSemantico;
        ManejoArchivo mArch = new ManejoArchivo();
        ArrayList<Linea> lineas;
        ArrayList<Token> lexemas;

        while (true) {
            // Crear menú interactivo
            //String[] opciones = {"Análisis Léxico", "Análisis Sintáctico", "Salir"};
            System.out.println("1. Análisis \n"+ "2. Salir\n");

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
                case 1 -> {
                    try{
                        // supongo que esta variable archivoTokens solo es para checar si ya existe
                        // tal archivo, no para escribir en este archivo si es que existe
                        java.io.File archivoTokens = new java.io.File("../TablaTokens.txt");
                        if (archivoTokens.exists()) {
                            archivoTokens.delete(); // Elimina el archivo para iniciar desde cero
                        }

                        analizadorLexico.analizarArchivo("./codigofuente.txt");
                        System.out.println("Análisis léxico completado. Tabla de tokens generada.");
                        ArrayList<Token> tokens = analizadorLexico.getTokens();
                        // Solamente muestra lo que se estuvo almacenando en el array list de tokens
                        // System.out.println(tokens);
                        // supongo que en tablaTokens se va a escribir
                        //File tablatokens = new File("/TablaTokens.txt"); // Nombre del archivo de la tabla de tokens
                        //FileWriter writerTokens = new FileWriter(tablatokens);
                        //lineas = mArch.leer("./codigofuente.txt");
                        //for (Linea linea : lineas) {
                          //  lexemas = analizadorLexico.analizarArchivo;
                           // analizadorLexico.analizar(lexemas);
                        //}

                        analizadorSemantico = new AnalizadorSemantico(tokens);
                        analizadorSemantico.analizar();


                    }catch (Exception e){
                        System.out.println("Error durante el analisis" + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Saliendo del programa.");
                    System.exit(0); // Salir del programa
                }
                default -> System.out.println("Opción inválida, inténtelo nuevamente.");
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
