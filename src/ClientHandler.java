import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author AllanWang
 * @description: This class is used to handle client connections and process HTTP requests.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerConfig config;

    // Constructor
    public ClientHandler(Socket clientSocket, ServerConfig config) {
        this.clientSocket = clientSocket;
        this.config = config;
    }


    // The run method is called when the thread is started
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            // Initialize keepAlive to true for HTTP/1.1, false for HTTP/1.0
            boolean keepAlive = true;

            // The server keeps the connection open by default for a period of time for HTTP/1.1
            int timeout = calculateTimeout();

            // Assume HTTP/1.1 at the beginning
            String httpVersion = "HTTP/1.1";

            do {
                // Read the request and parse it
                HttpRequest request = HttpRequest.parse(in);
                if (request == null) {
                    sendError(out, 400, "Bad Request");
                    break; // Exit if the request cannot be parsed
                }

                // Get the HTTP version from the request to handle keep-alive correctly
                httpVersion = request.getHttpVersion();
                keepAlive = "HTTP/1.1".equals(httpVersion);

                // Get the file from the document root based on the request URI
                String filePath = request.getUri().getPath();
                if ("/".equals(filePath)) {
                    filePath = "/index.html"; // Default to index.html if root path is requested
                }
                File file = new File(config.getDocumentRoot(), filePath);

                // Check if the file exists and is not a directory
                if (!file.exists()) {
                    sendError(out, 404, "Not Found");
                } else if (!file.isFile()) {
                    sendError(out, 403, "Forbidden");
                } else {
                    sendResponse(out, file, request.getHttpVersion());
                }

                // If it's HTTP/1.0 or an error occurred, close the connection
                if ("HTTP/1.0".equals(httpVersion) || !keepAlive) {
                    break; // Close the connection after sending the response
                }

                // For HTTP/1.1, set the timeout for the socket and wait for another request
                if (keepAlive) {
                    clientSocket.setSoTimeout(timeout);
                }

            } while (keepAlive);

        } catch (SocketTimeoutException e) {
            System.err.println("Connection timed out, closing connection.");
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            closeSocket();
        }
    }

    // Close the client socket
    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    // Heuristic: adjust the timeout based on the ratio of active connections
    private int calculateTimeout() {

        int maxConnections = 100; // maximum threshold, set as 100 for example
        int minTimeout = 1000; // Minimum timeout in ms
        int maxTimeout = 5000; // Maximum timeout in ms

        int activeConnections = WebServer.getActiveConnections();

        // If active ratio is high, use a shorter timeout
        // If active ratio is low, use a longer timeout
        double ratio = (double) activeConnections / maxConnections;
        int timeout = (int) (maxTimeout - (ratio * (maxTimeout - minTimeout)));

        return Math.max(timeout, minTimeout); // Ensure timeout >= minimumTimeout
    }

    // Send the response to the client
    private void sendResponse(OutputStream out, File file, String httpVersion) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(file.getPath()));
        String contentType = getContentType(file.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateStr = formatter.format(new Date());

        String httpResponse = httpVersion + " 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Date: " + dateStr + "\r\n\r\n";
        out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.flush();
    }

    // Send an error response to the client
    private void sendError(OutputStream out, int statusCode, String statusText) throws IOException {
        String httpResponse = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n\r\n";
        out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    // Get the content type based on the file extension
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        // Default to binary type if unknown
        return "application/octet-stream";
    }

}
