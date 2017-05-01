import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InetAddress local = InetAddress.getLocalHost();

        String[] request = new String[4];

        request[0] = "POST_MESSAGE";
        request[1] = "login1";
        request[2] = "login2";

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message;

            do {
                System.out.print("> ");
                message = scanner.nextLine();
            } while(message.equals(""));

            Socket socket = new Socket(local, 9875);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            if (message.equals("get")) {
                String[] getRequest = new String[2];
                getRequest[0] = "GET_MESSAGES";
                getRequest[1] = "login2";

                outputStream.writeObject(getRequest);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                ArrayList<String>[] response = (ArrayList[]) inputStream.readObject();

                for (int i = 0; i < response[0].size(); i++) {
                    System.out.print(response[0].get(i));
                    System.out.println("  -  "+response[1].get(i));
                }
            } else {

                request[3] = message;

                outputStream.writeObject(request);
            }

            outputStream.close();
            socket.close();
        }
    }
}
