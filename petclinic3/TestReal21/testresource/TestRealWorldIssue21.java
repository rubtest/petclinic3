package testresource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URLDecoder;

class TestRealWorldIssue21 {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String nextUrl = request.getParameter("next");
		URI url = URI.create(URLDecoder.decode(nextUrl, "UTF-8"));
		String redirectUrl = url.getPath();

		response.sendRedirect(redirectUrl);
	}
}
