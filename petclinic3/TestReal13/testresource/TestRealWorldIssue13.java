package testresource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestRealWorldIssue13 extends HttpServlet {

	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response) throws IOException {
		String url = request.getParameter("url");
		response.sendRedirect(url);
	}
}
