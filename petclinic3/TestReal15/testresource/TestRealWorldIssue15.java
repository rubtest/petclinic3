package testresource;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestRealWorldIssue15 extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// BAD: setting a cookie with an unvalidated parameter
		Cookie cookie = new Cookie("name", request.getParameter("name"));
		response.addCookie(cookie);
	}
}
