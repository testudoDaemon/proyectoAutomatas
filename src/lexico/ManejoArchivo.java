package lexico;

import java.io.*;
import java.util.ArrayList;

import utils.Token;

public class ManejoArchivo {
    private final File tablaErrores = new File("src/build/TablaErrores.txt"); // Nombre del archivo de la tabla de errores
    private final FileWriter writerErrores = new FileWriter(tablaErrores);

    public ManejoArchivo() throws IOException {
    }
    /*
       /*
   Metodo para leer, devuelve un arraylist con un objeto en el cual se guarda la linea y el numero de linea
   Estos parametros pueden ser accesados mediante sus metodos get de la clase
   @param nombre del archivo a leer
    */
    public ArrayList<Linea> leer(String ruta) throws IOException {
        ArrayList<Linea> lineas = new ArrayList<>();
        File file = new File(ruta);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            Linea linea;
            int numeroLinea = 1;
            String line;
            boolean comentarioAbierto = false; // Variable para ver si estamos dentro de un comentario
            while ((line = br.readLine()) != null) {
                // Verificar si la línea es un comentario
                if (esComentario(line, comentarioAbierto)) {
                    numeroLinea++;
                    continue; // Saltar esta linea y pasar a la siguiente
                }
                linea = new Linea(line, numeroLinea);
                numeroLinea++;
                lineas.add(linea);
            }
            br.close();
            return lineas;
        } catch(FileNotFoundException e){
            throw e;

        } catch (IOException e) {
            System.out.println("Ocurrió un error");
        }
        return lineas;
    }

    // Método para verificar si una línea es un comentario no vacío
    private boolean esComentario(String line, boolean comentarioAbierto) {
        line = line.trim();
        if (line.startsWith("//")) {
            return !comentarioAbierto || !line.endsWith("//");
        } else return line.endsWith("//");
    }

    public void  imprimirTablaTokens(ArrayList<Token> Tokens) {

    }

    public void imprimirTablaErrores(int contador, String lexema, int numeroLinea) throws IOException {
        try {
            writerErrores.write(contador + ", " + lexema + ", " + numeroLinea);
            writerErrores.close();
        }
        catch (IOException e){
            System.out.println("No se pudo imprimir la tabla de errores");
        }
    }

    public void cerrarTablaErrores(){
        try{
            writerErrores.close();
        }catch(IOException e){
            System.out.printf(String.valueOf(e));
        }
    }
}