package vci;

import utils.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class VCIGen {
    private ArrayList<Token> vci;
    private Stack<Token> operadores;
    private Stack<Integer> direcciones;
    private Stack<Token> estatutos;

    public VCIGen() {
        this.vci = new ArrayList<>();
        this.operadores = new Stack<>();
        this.direcciones = new Stack<>();
        this.estatutos = new Stack<>();
    }

    public ArrayList<Token> getVci() {
        return vci;
    }

    public void generarVCI(ArrayList<Token> tokens) {
        boolean IOFlag = false, inCode = false, conditionFlag = false, elseFlag = false, repeatFlag = false;
        Token tokenVacio = new Token("", -0, -1, -1);
        int valor, indice = 0;
        for (Token token : tokens){
            valor = token.getValorTablaTokens();
            //el metodo no empieza hasta que encuentre el primer begin para que no haga
            //checks innecearios
            if(inCode) {
                //------------ Tokens constantes y variables ------------
                //si es una constante o un identificador se agrega al VCI de inmediato
                if(isConstante(valor) || identificador(valor)){
                    vci.add(token);
                }

                //------------ Tokens de operadores ------------
                //si es un operador se agrega a la pila de operadores hasta que tenga alguna salida
                if(opAritmeticos(valor) || opRelacionales(valor) || opLogicos(valor) || valor == -26){
                    //si la pila esta vacia se agrega el operador
                    if(operadores.isEmpty()){
                        operadores.push(token);
                    } else {
                        //si el operador es de mayor prioridad que el que esta en la cima de la pila
                        //se agrega a la pila
                        if(getPrioridad(valor) > getPrioridad(operadores.peek().getValorTablaTokens())){
                            operadores.push(token);
                        } else {
                            //si no se vacia la pila hasta que sea menor o igual a la prioridad del operador
                            while(getPrioridad(valor) <= getPrioridad(operadores.peek().getValorTablaTokens())){
                                vci.add(operadores.pop());
                            }
                            //se agrega el operador a la pila
                            operadores.push(token);
                        }
                    }
                }

                // ------------ Tokens end finaliza los estatutos ------------
                //si es un end se manejan los estatutos
                if(valor == -3){
                    Token temp;
                    //si estatutos esta vacia significa que es el ultimo end y no se hace nada
                    if(estatutos.isEmpty()){

                    } else {
                        if(!repeatFlag){
                            //se guarda el estatuto en un temporal
                            temp = estatutos.pop();
                            //si el estatuto es un if
                            if(temp.getValorTablaTokens() == -6)
                                //si hay un else despues del if se activa una bandera
                                if(tokens.get(indice+1).getValorTablaTokens() == -7){
                                    elseFlag = true;
                                } else {
                                    //si no hay un else se agrega el if al VCI
                                    actualizarVCI(direcciones.pop(), vci.size() + 1);
                                }
                            //si es un else se actualiza la direccion en la cima de la pila de direcciones
                            if(temp.getValorTablaTokens() == -7){
                                //las direcciones se guardan con un numero de token 0
                                actualizarVCI(direcciones.pop(), vci.size() + 1);
                                elseFlag = false;
                            }
                            //si es un while se retiran 2 direcciones a la primera se le suma 2 y la segunda se guarda
                            //normal y se agregan al vci
                            if(temp.getValorTablaTokens() == -8){
                                actualizarVCI(direcciones.pop(), vci.size() + 3);
                                vci.add(new Token(Integer.toString(direcciones.pop()), 0, -1, -1));
                                //el token enWhile es un salto incondicional esos llevan un numero de token 1
                                vci.add(new Token("enWhile", 3, -1, -1));
                            }
                        }
                    }
                }

                //------------ Tokens de estatutos ------------
                // Si el token es read or write se activa una flag de entrada y salida que nos dice que hay que agregar la siguiete
                // instruccion al VCI
                if(valor == -4 || valor == -5){
                    estatutos.push(token);
                    IOFlag = true;
                }

                //si es un if se agrega a la pila de estatutos y la bandera de condicion se activa
                //que nos dice que cuando sea el proximo parentesis derecho se debe agregar un token vacio y el if al vci
                if(valor == -6){
                    conditionFlag = true;
                    estatutos.push(token);
                }

                //si llego un if con un else
                //checar que si es un else por que todo lo demas ya se maneja
                //si es un else se agrega a la pila de estatutos
                //se actualiza la direccion en la cima de la pila de direcciones para que se salte el else
                //se guarda la direccion del vacio en la pila de direcciones
                //se agrega el else al VCI
                if(valor == -7 && elseFlag){
                    estatutos.push(token);
                    vci.add(tokenVacio);
                    //las direcciones se guardan con un numero de token 0
                    actualizarVCI(direcciones.pop(), vci.size() + 2);
                    direcciones.push(vci.size()-1);
                    vci.add(token);
                }

                //si es un while se agrega a la pila de estatutos y se guarda la direccion en la pila de direcciones
                if(valor == -8){
                    estatutos.push(token);
                    direcciones.push(vci.size()+1);
                    conditionFlag = true;
                }

                //si es un repeat se agrega a la pila de estatutos y se guarda la direccion en la pila de direcciones
                if(valor == -9){
                    estatutos.push(token);
                    direcciones.push(vci.size()+1);
                }

                //si es un until se activa una vandera para la condicion del repeat
                if(valor == -10){
                    repeatFlag = true;
                }

                //------------ Tokens de control de flujo ------------
                //si es un parentesis izquierdo se agrega a la pila
                if(valor == -73){
                    operadores.push(token);
                }
                //si es un parentesis derecho se sacan los operadores de la pila hasta encontrar el parentesis izquierdo
                if(valor == -74){
                    while(operadores.peek().getValorTablaTokens() != -73){
                        vci.add(operadores.pop());
                    }
                    //se saca el parentesis izquierdo
                    operadores.pop();
                    //si la condicion es verdadera se agrega un token vacio y se guarda la direccion cuando termine la condicion
                    if(conditionFlag && !repeatFlag){
                        vci.add(tokenVacio);
                        direcciones.push(vci.size()-1);
                        vci.add(estatutos.peek());
                        conditionFlag = false;
                    }
                    if(repeatFlag){
                        vci.add(new Token(Integer.toString(direcciones.pop()), 0, -1, -1));
                        vci.add(new Token("endRepeat", 4, -1, -1));
                        repeatFlag = false;
                    }

                }

                //si es un punto y coma se sacan todos los operadores de la pila
                if(valor == -75) {
                    while (!operadores.isEmpty()) {
                        vci.add(operadores.pop());
                    }
                }
                //si la bandera de entrada y salida esta activa se agrega la siguiente instruccion al VCI que van a ser write o read
                if(IOFlag){
                    if(isPrintable(valor)){
                        vci.add(estatutos.pop());
                        IOFlag = false;
                    }
                }
            }
            //si el token es un begin se activa la bandera de inicio de codigo
            if(valor == -2){
                inCode = true;
            }
            //incrementar el indice
            indice++;
        }

        //se guarda el VCI en un archivo
        guardarVCI("./build/salida.vci", vci);
    }

    private int getPrioridad(int token){
        switch (token){
            case -21, -22, -27 -> {
                return 60;
            }
            case -24, -25 -> {
                return 50;
            }
            case -31, -32, -33, -34, -35, -36 -> {
                return 40;
            }
            case -43 -> {
                return 30;
            }
            case -41 -> {
                return 20;
            }
            case -42 -> {
                return 10;
            }
            case -26, -73 -> {
                return 0;
            }
            default -> {
                return -1;
            }
        }
    }

    private boolean opAritmeticos(int token) {
        return token == -21 || token == -22 || token == -23 || token == -24 || token == -25 || token == -27;
    }

    private boolean opRelacionales(int token) {
        return token == -31 || token == -32 || token == -33 || token == -34 || token == -35 || token == -36;
    }

    private boolean opLogicos(int token) {
        return token == -41 || token == -42 || token == -43;
    }

    private boolean isPrintable(int token) {
        return identificador(token) || isConstante(token);
    }

    private boolean isConstante(int token) {
        return token >= -65 && token <= -61;
    }

    private boolean identificador(int token) {
        return token <= -51 && token >= -54;
    }

    //actualiza la direccion en la posicion de la pila de direcciones
    private void actualizarVCI(int direccion, int nuevaDireccion) {
        //las direcciones se guardan con un numero de token 0
        vci.set(direccion, new Token(Integer.toString(nuevaDireccion), 0, -1, -1));
    }


    private static void guardarVCI(String archivoSalida, ArrayList<Token> vci) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
            for (Token token : vci)
                if(token.getValorTablaTokens() == 0){
                    bw.write("0x" + token.getLexema() + "\n");
                }else {
                    bw.write(token.getLexema() + "\n");
                }
            System.out.println("VCI guardado en 'salida.vci'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
