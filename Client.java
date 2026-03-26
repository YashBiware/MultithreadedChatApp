import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to chat server!");
            System.out.println("Type 'exit' to leave.\n");

            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner    = new Scanner(System.in);

            // separate thread to listen for incoming messages from server
            // this runs in the background while the main thread handles user input
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            listenerThread.setDaemon(true); // stops automatically when main thread exits
            listenerThread.start();

            // main thread: read user input and send to server
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                out.println(userInput);

                if (userInput.equalsIgnoreCase("exit")) {
                    System.out.println("You left the chat. Goodbye!");
                    break;
                }
            }

            socket.close();
            scanner.close();

        } catch (ConnectException e) {
            System.out.println("Could not connect to server. Make sure the server is running first!");
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
