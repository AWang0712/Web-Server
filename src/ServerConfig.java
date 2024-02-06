/**
 * @author: Allan Wang
 * @description: This class is used to parse command-line arguments and store the server configuration.
*/
public class ServerConfig {

    // Default port and document root, can be overridden by command-line arguments
    private int port = 8080;
    private String documentRoot = "D:\\SCU\\4_Winter2024\\CSEN317\\hw\\hw3\\webserver_files";

    // Constructor, getters, and setters
    public ServerConfig() {
    }
    public ServerConfig(int port, String documentRoot) {
        this.port = port;
        this.documentRoot = documentRoot;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public void setDocumentRoot(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    // Parse command-line arguments
    public static ServerConfig parseArgs(String[] args) {
        ServerConfig config = new ServerConfig();
        for (int i = 0; i < args.length; i++) {
            if ("-document_root".equals(args[i])) {
                config.setDocumentRoot(args[++i]);
            } else if ("-port".equals(args[i])) {
                config.setPort(Integer.parseInt(args[++i]));
            }
        }
        return config;
    }

}

