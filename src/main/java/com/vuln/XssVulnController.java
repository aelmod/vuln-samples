package com.vuln;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet("/vuln")
public class XssVulnController extends HttpServlet {

    private List<Message> messages = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(getHtml());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.nonNull(req.getParameter("message"))) {
            String message = req.getParameter("message");
            messages.add(new Message(message));
            resp.sendRedirect("/vuln");
        }
    }

    private String getHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        String first =
                "<html>\n" +
                "<head>\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form method=\"post\" action=\"/vuln\">\n" +
                "    <input id=\"message\" type=\"text\" required=\"required\" name=\"message\">\n" +
                "    <input id=\"message2\" type=\"text\" name=\"test2\">\n" +
                "    <input id=\"message1\" type=\"text\" name=\"test1\">\n" +
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