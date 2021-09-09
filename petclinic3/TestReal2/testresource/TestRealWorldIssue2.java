package testresource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class TestRealWorldIssue2 extends HttpServlet {

	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response) throws ServletException, IOException {
		String file = request.getParameter("file");

		if (foobar(file)) {
			getServletContext().getRequestDispatcher("/WEB-INF/success.jsp").forward(
					request, response);
		}
	}

	private boolean foobar(String file) {
		if (file) {
			String s = getServletContext().getRealPath("/");
			new File(s + file);
			return true;
		}
		return false;
	}
}