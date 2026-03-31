import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;

public class TodoApp {
    static List<String> tasks = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);

        server.createContext("/tasks", exchange -> {
            String method = exchange.getRequestMethod();

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                String response = tasks.toString().replace("=", "");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

            else if (method.equals("POST")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String body = br.readLine();
                String task = body.split(":")[1].replace("\"", "").replace("}", "").trim();
                tasks.add(task);

                String response = "Task Added";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

            else if (method.equals("PUT")) {
                int id = Integer.parseInt(path.split("/")[2]);

                BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String body = br.readLine();
                String task = body.split(":")[1].replace("\"", "").replace("}", "").trim();

                tasks.set(id, task);

                String response = "Task Updated";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

            else if (method.equals("DELETE")) {
                int id = Integer.parseInt(path.split("/")[2]);
                tasks.remove(id);

                String response = "Task Deleted";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.start();
        System.out.println("Server started at http://localhost:4567");
    }
}