package testresource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TestRealWorldIssue3 {

	public void sendUserFile(Socket sock, String user) throws IOException {
		BufferedReader filenameReader = new BufferedReader(
				new InputStreamReader(sock.getInputStream(), "UTF-8"));
		String filename = filenameReader.readLine();
		// BAD: read from a file using a path controlled by the user
		BufferedReader fileReader = new BufferedReader(
				new FileReader("/home/" + user + "/" + filename));
		String fileLine = fileReader.readLine();
		while (fileLine != null) {
			sock.getOutputStream().write(fileLine.getBytes());
			fileLine = fileReader.readLine();
		}
	}
}
