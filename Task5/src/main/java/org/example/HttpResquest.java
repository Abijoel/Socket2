package org.example;

import java.io.*;
import java.net.Socket;

class HttpRequestHandler extends Thread {
    private final Socket clientSocket;

    public HttpRequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestLine = in.readLine();
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            if (method.equals("GET")) {
                if (path.equals("/")) {
                    // Handle '/' path
                    String htmlContent = readFile("/Users/decagon/IdeaProjects/Task5/src/main/resources/index.html");
                    String cssContent = readFile("");
                    String response = htmlContent.replace("</head>", "<style>" + cssContent + "</style></head>");
                    sendResponse(out, "200 OK", "text/html", response);
                } else if (path.equals("/json")) {
                    // Handle '/json' path
                    String jsonResponse = readFile("/Users/decagon/IdeaProjects/Task5/src/main/resources/package.json");
                    sendResponse(out, "200 OK", "application/json", jsonResponse);
                } else {
                    // Handle unknown path
                    sendResponse(out, "404 Not Found", "text/plain", "404 Not Found");
                }
            } else {
                // Handle unsupported HTTP methods
                sendResponse(out, "405 Method Not Allowed", "text/plain", "405 Method Not Allowed");
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(PrintWriter out, String status, String contentType, String content) {
        out.println("HTTP/1.1 " + status);
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + content.length());
        out.println();
        out.println(content);
    }

    private String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
