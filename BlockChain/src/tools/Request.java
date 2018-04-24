/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author c.saldarriaga
 */
public class Request {

    public static void addselfToNetwork(List<Pair<String, String>> listOfNodes, Pair <String, String> p) throws IOException {

        System.out.println("Enviando puerto a todos los nodos");

        JSONObject j = new JSONObject();
        j.put("port", p.getValue());
        j.put("ip", p.getKey());

        for (int i = 0; i < listOfNodes.size(); i++) {

            Pair<String, String> pair = listOfNodes.get(i);

            String ip = pair.getKey();
            String portN = pair.getValue();

            System.out.println("---enviando puerto al nodo " + portN);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            try {
                HttpPost request = new HttpPost("http://" + ip + ":" + portN + "/addnode");
                StringEntity params = new StringEntity(j.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    System.out.println("Puerto agregado al nodo " + portN);
                }

            } catch (Exception ex) {
                System.out.println("Error de la petición del cliente");
            } finally {
                httpClient.close();
            }

        }

    }

    public static void sendNewChain(List<Pair<String, String>> listOfNodes, JSONArray j) throws IOException {
        System.out.println("Enviando nueva cadena a todos los nodos");

        for (int i = 0; i < listOfNodes.size(); i++) {
            Pair<String, String> pair = listOfNodes.get(i);

            String ip = pair.getKey();
            String portN = pair.getValue();
            
          
            System.out.println("---enviando cadena al puerto " +ip + ":"+portN);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            try {
                HttpPost request = new HttpPost("http://" + ip + ":" + portN + "/newchain");
                StringEntity params = new StringEntity(j.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                httpClient.execute(request);
            } catch (Exception ex) {
                System.out.println("Error de la petición del cliente " + ex);
            } finally {
                httpClient.close();
            }
        }
    }

    public static JSONArray getChainFromNodes(List<Pair<String, String>> listOfNodes) throws IOException {
        System.out.println("Pidiendo cadena a nodos");

        List<String> list = new ArrayList<>();

        for (int i = 0; i < listOfNodes.size(); i++) {
             Pair<String, String> pair = listOfNodes.get(i);
            String ip = pair.getKey();
            String portN = pair.getValue();
            System.out.println("Pidiendo cadena a nodo " + portN);
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("http://" + ip + ":" + portN + "/latestchain");
            HttpResponse response;

            try {
                response = httpClient.execute(request);
                String result = EntityUtils.toString(response.getEntity());
                list.add(result);

                System.out.println("Resultado: " + result);
            } catch (Exception e) {
                System.out.println("Error en la petición de la cadena " + e);
            } finally {
                httpClient.close();
            }

        }

        int max = 0;
        int biggest = 0;
        for (int i = 0; i < list.size(); i++) {

            if (max < list.get(i).length()) {
                max = list.get(i).length();
                biggest = i;
            }
        }

        return new JSONArray(list.get(biggest));

    }

}
