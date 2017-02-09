import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeregrinServer {
    private static ServerSocket server;

    private static int port = 9875;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            System.out.println("Waiting for client request");

            Socket socket = server.accept();

            InputStream ois = socket.getInputStream();

            byte[] message = new byte[1024];

            ois.read(message);
            System.out.println("Message Received: " + new String(clearEmptyCell(message)));

            OutputStream oos = socket.getOutputStream();

            oos.write(message);

            ois.close();
//            oos.close();
            socket.close();

            if (new String(clearEmptyCell(message)).equals("exit")) break;
        }

        System.out.println("Shutting down Socket server!!");

        server.close();
    }

    private static byte[] clearEmptyCell(byte[] arr) {
        int length = 0;
        for (byte arrE : arr) {
            if (arr[length] == 0) break;
            else length++;
        }

        byte[] result = new byte[length];

        System.arraycopy(arr, 0, result, 0, length);

        return result;
    }
}
