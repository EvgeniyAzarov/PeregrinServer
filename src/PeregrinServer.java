import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeregrinServer {

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        final int port = 9875;
        ServerSocket server = new ServerSocket(port);

        String buffer = "";

        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            Socket socket = server.accept();

            InputStream inputStream = socket.getInputStream();

            byte[] message = new byte[1024];

            if (inputStream.read(message) != -1) {
                System.out.println("Message Received: " + new String(clearEmptyCell(message)));
                buffer = new String(clearEmptyCell(message));
            }

            OutputStream outputStream = socket.getOutputStream();

            if (!buffer.equals(""))
                outputStream.write(buffer.getBytes());

            outputStream.close();
            inputStream.close();
            socket.close();

            if (new String(clearEmptyCell(message)).equals("exit")) break;
        }

        System.out.println("Shutting down Socket server!!");

        server.close();
    }

    private static byte[] clearEmptyCell(byte[] arr) {
        int length = 0;
        for (byte ignored : arr) {
            if (arr[length] == 0) break;
            else length++;
        }

        byte[] result = new byte[length];

        System.arraycopy(arr, 0, result, 0, length);

        return result;
    }
}
