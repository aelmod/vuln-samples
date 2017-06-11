package com.vuln;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet("/sql-vuln")
public class SqlVulnController extends HttpServlet {

    private Connection connection;

    private Connection getConnection() throws SQLException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        return DriverManager.getConnection("jdbc:mysql://localhost/vuln_samples", "root", "root");
    }

    public SqlVulnController() {
        try {
            connection = getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.nonNull(req.getParameter("message"))) {
            String message = req.getParameter("message");
            try {
                connection.createStatement().execute("INSERT INTO messages(text) VALUES('" + message + "')");
                resp.sendRedirect("/sql-vuln");
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (Objects.nonNull(id)) {
            try {
                String sql = "SELECT * FROM messages WHERE id=" + id;
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                resultSet.first();
                List<Message> message = new ArrayList<>();
                message.add(new Message(resultSet.getString("text")));
                resp.getWriter().write(getHtml(message));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        } else {
            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM messages");
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    messages.add(new Message(resultSet.getString("text")));
                }
                resp.getWriter().write(getHtml(messages));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private String getHtml(List<Message> messages) {
        StringBuilder stringBuilder = new StringBuilder();
        String first =
                "<html>\n" +
                        "<head>\n" +
                        "    <title>Title</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<form method=\"post\" action=\"/sql-vuln\">\n" +
                        "    <input id=\"message\" type=\"text\" required=\"required\" name=\"message\">\n" +
                        "    <div class=\"form-actions\">\n" +
                        "        <button type=\"submit\">Save</button>\n" +
                        "    </div>\n" +
                        "</form>\n" +
                        "<table>\n" +
                        "<tbody>\n";
        stringBuilder.append(first);

        for (Message message : messages) {
            String middle = "<tr> <td>" + message.getText() + "</td> </tr>";
            stringBuilder.append(middle);
        }

        String last =
                "</tbody>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";
        stringBuilder.append(last);
        return stringBuilder.toString();
    }

}
