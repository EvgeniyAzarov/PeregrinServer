import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    public static void main(String args[]) throws IOException, SQLException {

        final int port = 9875;
        ServerSocket server = new ServerSocket(port);

        Connection connection = connect();

        while (true) {
            Socket socket = server.accept();

            while (connection == null) {
                connection = connect();
            }

            RequestHandler requestHandler = new RequestHandler(socket, connection);

            requestHandler.start();
        }
    }

    private static Connection connect () {
        Connection connection = null;

        try {
            Driver driver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(driver);

            Properties connectInfo = new Properties();

            connectInfo.put("user", "root");
            connectInfo.put("password", "toor");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/peregrin?" +
                            "autoReconnect=true&" +
                            "useSSL=false&" +
                            "useUnicode=true&" +
                            "characterEncoding=utf8",
                    connectInfo
            );

            System.out.println("Подключено к базе данных peregrin");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
