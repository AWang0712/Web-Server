import java.io.*;
import java.net.*;

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

                System.out.println(request.toString()); // Print the complete HTTP request
            } catch (IOException e) {
                e.printStackTrace();
                break; // Exit the loop in case of an IO error
            }
        }
    }
}

