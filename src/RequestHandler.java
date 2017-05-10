import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
                case "USER_REGISTRATION": {

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
                }

                case "USER_ENTER": {
                    statement = connection.prepareStatement(
                            "SELECT * FROM users WHERE login = ?"
                    );

                    statement.setString(1, request[1]);

                    ResultSet user = statement.executeQuery();

                    if (user.next() && user.getString("password").equals(request[2])) {
                        outputStream.writeBoolean(true);
                        outputStream.writeObject(user.getString("nickname"));
                    } else {
                        outputStream.writeBoolean(false);
                    }

                    break;
                }

                case "POST_MESSAGE": {

                    statement = connection.prepareStatement(
                            "INSERT INTO messages VALUES (?, ?, ?, '0')"
                    );

                    statement.setString(1, request[1]);
                    statement.setString(2, request[2]);
                    statement.setString(3, request[3]);

                    statement.executeUpdate();

                    break;
                }

                case "GET_MESSAGES": {

                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE messages " +
                                    "SET received = '1' " +
                                    "WHERE recipient_login = ? and received = '0'");
                    updateStatement.setString(1, request[1]);

                    PreparedStatement selectStatement = connection.prepareStatement(
                            "SELECT * FROM messages WHERE recipient_login = ? and received = '1'"
                    );
                    selectStatement.setString(1, request[1]);

                    ResultSet messages;

                    do {
                        updateStatement.executeUpdate();

                        messages = selectStatement.executeQuery();

                    } while (!messages.next());

                    ArrayList<String>[] response = new ArrayList[2];
                    response[0] = new ArrayList<>();
                    response[1] = new ArrayList<>();

                    do {
                        response[0].add(messages.getString("content"));
                        response[1].add(messages.getString("sender_login"));
                    } while (messages.next());

                    outputStream.writeObject(response);

                    statement = connection.prepareStatement(
                            "DELETE FROM messages WHERE recipient_login = ? and received = '1'"
                    );
                    statement.setString(1, request[1]);

                    statement.execute();

                    break;
                }
            }

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
