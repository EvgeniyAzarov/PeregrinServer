import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        InetAddress host = InetAddress.getByAddress(new byte[]{93, 72 , 119 , 9});

        System.out.println(host.getHostName());
        System.out.println(host.getHostAddress());

        Socket socket;
        OutputStream oos;
        InputStream ois;

        socket = new Socket(host.getHostName(), 9875);
        oos = socket.getOutputStream();

        String str = scanner.nextLine();

        System.out.println("Sending request to Socket Server");

        oos.write(str.getBytes());

        ois = socket.getInputStream();
        byte[] buffer = new byte[1024];
        ois.read(buffer);
        System.out.println("Message: " + new String(clearEmptyCell(buffer)));

        ois.close();
        oos.close();
        Thread.sleep(100);
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
