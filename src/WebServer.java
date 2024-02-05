import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.lang.System.out;

/**
 * @author Allan Wang
 */
public class WebServer {

    // default port and document root
    private static int port = 8080;
    private static String documentRoot = "D:\\SCU\\4_Winter2024\\CSEN317\\hw\\hw3\\webserver_files";


    public static void main(String[] args) throws IOException {
        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if ("-document_root".equals(args[i])) {
                documentRoot = args[++i];
            } else if ("-port".equals(args[i])) {
                port = Integer.parseInt(args[++i]);
            }
        }

        ServerSocket serverSocket = new ServerSocket(port);
        out.println("Server is listening on port " + port);

        while (true) {

            try (Socket socket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream out = socket.getOutputStream()) {  // Use socket's output stream

                String inputLine;
                StringBuilder request = new StringBuilder();

                // Read HTTP request from the client
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

                File file = new File(documentRoot, httpPath.replaceFirst("/", "")); // Correct file path handling
                if (httpPath.equals("/")) {
                    file = new File(documentRoot, "index.html"); // Default to index.html if root path is requested
                }

                if (file.exists() && !file.isDirectory()) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String contentType = getContentType(file.getName()); // Determine the content type

                    // Build the HTTP response with Content-Type header
                    String httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileContent.length + "\r\n\r\n";
                    out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    out.write(fileContent);
                } else {
                    String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                    out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                break; // Exit the loop in case of an IO error
            }
        }
    }

    private static String getContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain"; // Default content type
        }
    }
}

