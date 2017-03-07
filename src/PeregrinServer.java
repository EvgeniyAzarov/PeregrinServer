import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PeregrinServer {

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        final int port = 9875;
        ServerSocket server = new ServerSocket(port);

        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        ArrayList<String> dataStorage = new ArrayList<>();

        while (true) {
            Socket socket = server.accept();

            inputStream = new ObjectInputStream(socket.getInputStream());
            String request = (String) inputStream.readObject();

            try {
                if (request.substring(0, 4).equals("POST")) {
                    dataStorage.add(request.substring(5, request.length()));
                } else if (request.substring(0, 3).equals("GET")) {

                    int lastGetMessageIndex = Integer.valueOf(request.substring(4, request.length()));

                    outputStream = new ObjectOutputStream(socket.getOutputStream());

                    for (int i = lastGetMessageIndex + 1; i < dataStorage.size(); i++) {
                        outputStream.writeObject(dataStorage.get(i));
                    }
                    outputStream.writeObject("END");

                    outputStream.close();
                }
            } catch (Exception e) {
                System.out.println("Ignored incorrect request");
            }

            inputStream.close();
            socket.close();
        }
    }
}
