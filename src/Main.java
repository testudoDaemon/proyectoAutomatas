import lexico.AnalizadorLexico;
import lexico.Linea;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import lexico.ManejoArchivo;
import utils.Token;
import semantico.AnalizadorSemantico;
import vci.VCIGen;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        AnalizadorLexico analizadorLexico = new AnalizadorLexico();
        AnalizadorSemantico analizadorSemantico;
        ManejoArchivo mArch = new ManejoArchivo();
        VCIGen vciGen = new VCIGen();
        ArrayList<Linea> lineas;
        ArrayList<Token> lexemas;

        while (true) {
            // Crear menú interactivo
            System.out.println("1. Análisis \n"+ "2. Salir\n");

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


                        analizadorSemantico = new AnalizadorSemantico(tokens);
                        analizadorSemantico.analizar();

                        vciGen = new VCIGen();
                        vciGen.generarVCI(tokens);

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

}
