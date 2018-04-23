/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Arrays;
import javafx.util.Pair;
;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author c.saldarriaga
 */


public class Nodo {

    public static List<Integer> nodos;

    public static BlockChain blockChain;

    public static int port;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        nodos = new ArrayList<>();

        System.out.println("Ingrese un puerto");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        port = Integer.parseInt(br.readLine());

        // TODO code application logic here
        System.out.println("Bienvenido...");
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/addnode", new AddNode());
        server.createContext("/latestchain", new LatestChain());
        server.createContext("/newchain", new NewChain());
        server.setExecutor(null);
        server.start();

        System.out.println("Es el nodo principal? (Y/N)");
        br = new BufferedReader(new InputStreamReader(System.in));
        String answer = br.readLine();

        if (answer.equalsIgnoreCase("y")) {
            // Nodo inicial
            blockChain = new BlockChain(0);

        } else if (answer.equalsIgnoreCase("n")) {
            blockChain = new BlockChain(0);
            System.out.println("Ingrese los puertos de los nodos en la red( con comas entre ellos)");
            answer = br.readLine();
            String[] tokens = answer.split(",");

            for (String token : tokens) {
                nodos.add(Integer.parseInt(token));
            }
            tools.Request.addselfToNetwork(nodos, server.getAddress().getPort());
            JSONArray chain = tools.Request.getChainFromNodes(nodos);

            // Omite el bloque génesis
      
            for (int i = 1; i < chain.length(); i++) {

                JSONObject j = chain.getJSONObject(i);

                System.out.println("Objeto " + j.toString());

                List<String> data = new ArrayList<>();
                JSONArray ja = j.getJSONArray("data");
                for (int k = 0; k < ja.length(); k++) {
                    data.add(ja.getString(i));
                }
                blockChain.addBlock(j.getInt("index"), j.get("previousHash").toString(), j.get("hash").toString(), j.get("nonce").toString(), data);

            }

            System.out.println("TAMAÑO CADENA: " + blockChain.getNumOfBlocks());

        } else {
            return;
        }

        boolean close = false;

        while (!close) {

            System.out.println("--- Menú de opciones ---");
            System.out.println();
            System.out.println("(1) Buscar bloque");
            System.out.println("(2) Añadir bloque");
            System.out.println("(3) Salir");

            br = new BufferedReader(new InputStreamReader(System.in));
            answer = br.readLine();

            switch (answer) {

                case "1":
                    System.out.println("Qué bloque desea buscar");
                    br = new BufferedReader(new InputStreamReader(System.in));
                    answer = br.readLine();

                    try {
                        System.out.println("Petición: " + blockChain.getBlock(Integer.parseInt(answer)).toString());
                    } catch (Exception e) {
                        System.out.println("Error en la petición " + e);
                    }

                    break;
                case "2":

                    String[] txs = new String[]{
                        "ID:1019090511 Serial:0000000001 Clase:Notaria Tipo:EstadoCivil Datos:Casado",
                        "ID:1019090512 Serial:0000000002 Clase:Impuestos Tipo:RegistroRUT Datos:8299",
                        "ID:1019090513 Serial:0000000003 Clase:Gobierno Tipo:Subsidio Datos:Vivienda",
                        "ID:1019090514 Serial:0000000005 Clase:Registraduria Tipo:Duplicado Datos:0000000004",
                        "ID:1019090514 Serial:0000000005 Clase:Impuestos Tipo:RegistroRUT Datos:0000005543"
                    };

                    List<String> data = new ArrayList<>();

                    for (int i = 0; i < txs.length; i++) {
                        data.add(txs[i]);
                    }

                    Pair<String, String> pair = tools.StringUtil.findHash(blockChain.getNumOfBlocks(), blockChain.getLatestBlockhash(), data);
                    blockChain.addBlock(blockChain.getNumOfBlocks(), blockChain.getLatestBlockhash(), pair.getKey(), pair.getValue(), data);

                    System.out.println("SIZE " + blockChain.getNumOfBlocks());
                    
                    // Enviar blockchain a la red
                    tools.Request.sendNewChain(nodos, blockChain.toJSON());

                    

                    try {
                        if (blockChain.getNumOfBlocks() == 0) {
                            System.out.println("-----------------------------");
                            System.out.println("Por favor conectese a la red... Su blockchain  no posee ningún bloque ");
                        }
                    } catch (Exception e) {
                        System.out.println("Error en la petición " + e);
                    }

                    break;
                case "3":
                    close = true;
                    break;
                default:
                    break;
            }

        }

        return;
    }

    static class AddNode implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {

            //JSONObject jsonObject = messageExchange.getJSONMessage(httpExchange.getRequestBody());
            System.out.println("POST /addnode --- Nuevo nodo conectandose a la red...");
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            JSONObject request = new JSONObject(query);
            nodos.add(request.getInt("port"));
            System.out.println("---Agregando nodo a la lista de nodos, " + request.getInt("port"));

            String response = "agregado";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();

        }

    }

    static class LatestChain implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {

            System.out.println("GET /latestchain --- Enviando BlockChain...");
            JSONArray chain = blockChain.toJSON();
            System.out.println("CADENA: " + chain.toString());

            byte[] response = chain.toString().getBytes();

            t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
            t.getResponseBody().write(response);
            t.close();

        }
    }

    static class NewChain implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            System.out.println("POST /newchain --- Nodo en la red envió una nueva cadena...");
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            
           try {
               JSONArray array = new JSONArray(query);
                blockChain.replaceChain(array);
           } catch(Exception e) {
               System.err.println("Error: " + e);
           }
           
            
            
            String s = "OK";
            byte[] response = s.getBytes();
            t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
            t.getResponseBody().write(response);
            t.close();
        }
    }

}
