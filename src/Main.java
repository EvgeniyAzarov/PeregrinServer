import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int port = 9875;
    private static ServerSocket server;
    private static ExecutorService pool;

    public static void main(String args[]) throws SQLException, IOException {

        server = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();

        runServer();
    }

    private static void runServer() {
        Connection connection = connect();

        while (true) {
            while (connection == null) {
                connection = connect();
            }

            try {
                pool.execute(new RequestHandler(server.accept(), connection));
            } catch (IOException e) {
                pool.shutdown();
            }
        }
    }

    private static Connection connect () {
        Connection connection = null;

        System.out.print("Connect to DB...  ");

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

            System.out.println("Done.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
