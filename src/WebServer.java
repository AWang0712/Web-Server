import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class WebServer {
    public static void main(String[] args) throws IOException {
        int port = 8080; // You can change this to any port number above 1024 and below 65535

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (true) {
            try (Socket socket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("New connection made");

                String inputLine;
                StringBuilder request = new StringBuilder();

                // Read HTTP request from the client and print it
                while ((inputLine = in.readLine()) != null) {
                    request.append(inputLine).append("\n");
                    if (inputLine.isEmpty()) { // HTTP request ends with a blank line
                        break;
                    }
                }

                // Parse the request
                String[] requestLines = request.toString().split("\n");
                String[] requestLine = requestLines[0].split(" ");
                String httpMethod = requestLine[0];
                String httpPath = requestLine[1];

                System.out.println("HTTP Method: " + httpMethod);
                System.out.println("HTTP Path: " + httpPath);

                // Prepare an HTTP response
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\nHello World!";

                // Send the HTTP response
                try (OutputStream out = socket.getOutputStream()) {
                    out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                System.out.println(request.toString()); // Print the complete HTTP request
            } catch (IOException e) {
                e.printStackTrace();
                break; // Exit the loop in case of an IO error
            }
        }
    }
}

