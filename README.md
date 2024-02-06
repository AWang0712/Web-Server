This project is a functional web server built in Java. The server is designed to listen for incoming HTTP
requests on a specified port and serve files from a designated document root directory. It supports basic
HTTP/1.0 and HTTP/1.1 protocols and handles various file types including HTML, JPG, GIF, and TXT.
The server appropriately responds to different requests, handling scenarios such as 200, 400, 403 and 404.

The server operates on a continual loop, persistently listening for incoming connections. Upon accepting
a new connection from a client, it initiates a multi-threaded approach, where each connection
is handled by a separate thread. This design choice enhances the server’s ability to manage multiple
concurrent client requests.

The core functionality of the server includes:
• Parsing the HTTP request from the client.
• Validating the form of the request, with error responses for malformed requests.
• Determining the existence and access permissions of the requested file, responding with appropriate
HTTP error codes (such as 404 for Not Found or 403 for Forbidden) when necessary.
• Transmitting the contents of the file through socket writing operations if the file exists and is
accessible.

The server has its implementation of a heuristic approach to manage persistent connections for
HTTP/1.1 protocol. This heuristic determines the duration for which the server keeps a connection open
before timing out, based on the current server load. It allows the server to dynamically adjust the timeout
period, keeping connections open longer during periods of low activity and closing them sooner under high
load to optimize resource usage.
