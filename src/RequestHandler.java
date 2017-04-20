import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestHandler extends Thread {

    private final Socket socket;

    private final Connection connection;

    RequestHandler(Socket socket, Connection connection) {
        this.socket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            String[] request = (String[]) inputStream.readObject();

            PreparedStatement statement;

            switch (request[0]) {
                case "USER_REGISTRATION":

                    statement = connection.prepareStatement(
                            "SELECT * FROM users WHERE login = ?"
                    );

                    statement.setString(1, request[1]);

                    ResultSet users = statement.executeQuery();

                    if (!users.next()) {
                        outputStream.writeBoolean(true);

                        statement = connection.prepareStatement(
                                "INSERT INTO users VALUES (?, ?, ?)"
                        );

                        statement.setString(1, request[1]);
                        statement.setString(2, request[2]);
                        statement.setString(3, request[3]);

                        statement.executeUpdate();
                    } else {
                        outputStream.writeBoolean(false);
                    }

                    break;

                case "POST_MESSAGE":

                    statement = connection.prepareStatement(
                            "INSERT INTO messages VALUES (?, ?, ?)"
                    );

                    statement.setString(1, request[1]);
                    statement.setString(2, request[2]);
                    statement.setString(3, request[3]);

                    statement.executeUpdate();

                    break;

                case "GET_MESSAGES":
                    statement = connection.prepareStatement(
                            "SELECT * FROM messages WHERE recipient_login = ?"
                    );

                    //TODO "GET_MESSAGES" request handler
                    
                    break;
            }

        } catch (IOException | ClassNotFoundException | SQLException ignored) {
            ignored.printStackTrace();
        }
    }
}
