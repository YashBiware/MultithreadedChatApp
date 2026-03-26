import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    // port the server listens on
    private static final int PORT = 1234;

    // shared list of all connected clients
    // synchronized so multiple threads can safely add/remove
    static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        System.out.println("Waiting for clients...\n");

        // keep accepting new client connections forever
        while (true) {
            Socket clientSocket = serverSocket.accept();

            // create a handler for this client and run it in a new thread
            ClientHandler handler = new ClientHandler(clientSocket);
            clients.add(handler);

            Thread thread = new Thread(handler);
            thread.start();
        }
    }

    // sends a message to every connected client except the sender
    static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }
}
