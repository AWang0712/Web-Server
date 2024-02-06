import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AllanWang
 * @description: This class is used to start the web server and listen for incoming client connections.
 */
public class WebServer {

    // Server configuration
    private final ServerConfig config;
    // Keep track of active connections
    private static final AtomicInteger activeConnections = new AtomicInteger(0);

    public WebServer(ServerConfig config) {
        this.config = config;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            System.out.println("Server is listening on port " + config.getPort());
            // Create a thread pool to handle incoming client connections
            ExecutorService threadPool = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                activeConnections.incrementAndGet(); // Increment active connections
                threadPool.execute(() -> {
                    try {
                        new ClientHandler(clientSocket, config).run();
                    } finally {
                        activeConnections.decrementAndGet(); // Decrement active connections
                    }
                });
            }
        }
    }

    public static int getActiveConnections() {
        return activeConnections.get();
    }

    public static void main(String[] args) {
        ServerConfig config = ServerConfig.parseArgs(args);
        WebServer server = new WebServer(config);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
