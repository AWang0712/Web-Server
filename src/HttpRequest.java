import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlanWang
 * @description: This class is used to parse HTTP requests and store the request information.
 */
public class HttpRequest {
    private String method;
    private URI uri;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>(); // Store headers in a map

    public HttpRequest() {
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();
        String line = reader.readLine();
        if (line == null || line.isEmpty()) {
            return null;
        }

        String[] requestLine = line.split(" ");
        if (requestLine.length < 3) {
            return null; // Invalid request line
        }

        request.method = requestLine[0];
        request.uri = URI.create(requestLine[1]);
        request.httpVersion = requestLine[2];

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonPos = line.indexOf(":");
            if (colonPos >= 0) {
                String headerName = line.substring(0, colonPos).trim();
                String headerValue = line.substring(colonPos + 1).trim();
                request.headers.put(headerName, headerValue);
            }
        }

        return request;
    }

    // Getters for method, uri, httpVersion, headers
    public String getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
