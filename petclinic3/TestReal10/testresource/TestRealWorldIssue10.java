package testresource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class TestRealWorldIssue10 {

	public void issue10() {
		try {
			URLConnection openConnection = new URL("http://missing_ssl_url/").openConnection();
		} catch (IOException e) {
			// handle error
		}
	}

	public void issue10b() {
		try {
			URLConnection openConnection = new URL("http", "missing_ssl_url", "/").openConnection();
		} catch (IOException e) {
			// handle error
		}
	}
}
