import java.io.*;
import java.net.*;

// Each connected client gets its own ClientHandler running in a separate thread
public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // set up input and output streams for this client
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // first message from client is always their username
            out.println("Enter your username: ");
            username = in.readLine();

            System.out.println(username + " has joined the chat.");
            Server.broadcast("[" + username + " has joined the chat!]", this);
            sendMessage("Welcome " + username + "! Type your message and press Enter.");

            // keep reading messages from this client
            String message;
            while ((message = in.readLine()) != null) {

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.println("[" + username + "]: " + message);
                // broadcast to everyone else
                Server.broadcast("[" + username + "]: " + message, this);
            }

        } catch (IOException e) {
            System.out.println(username + " lost connection.");
        } finally {
            disconnect();
        }
    }

    // sends a message to this specific client
    public void sendMessage(String message) {
        out.println(message);
    }

    // clean up when client leaves
    private void disconnect() {
        try {
            Server.clients.remove(this);
            Server.broadcast("[" + username + " has left the chat.]", this);
            System.out.println(username + " has left the chat.");
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing socket for " + username);
        }
    }
}
