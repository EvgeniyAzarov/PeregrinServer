import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeregrinServer {

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        final int port = 9875;
        ServerSocket server = new ServerSocket(port);

        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        String buffer = "";

        while (true) {
            Socket socket = server.accept();

            inputStream = new ObjectInputStream(socket.getInputStream());
            String message = (String) inputStream.readObject();
            System.out.println("Message Received: " + message);
            buffer = message;

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(buffer);

            inputStream.close();
            outputStream.close();
            socket.close();

            if (message.equals("exit")) break;
        }

        System.out.println("Shutting down Socket server!!");

        server.close();
    }
}
