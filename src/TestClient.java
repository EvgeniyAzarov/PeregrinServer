import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException {
        InetAddress local = InetAddress.getLocalHost();

        String[] request = new String[4];

        request[0] = "POST_MESSAGE";
        request[1] = "098-301-40-54";
        request[2] = "098-301-44-95";

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message;

            do {
                System.out.print("> ");
                message = scanner.nextLine();
            } while(message.equals(""));

            Socket socket = new Socket(local, 9875);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            request[3] = message;

            outputStream.writeObject(request);

            outputStream.close();
            socket.close();
        }
    }
}
