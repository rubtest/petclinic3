package testresource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestRealWorldIssue9 {

	public void weakhash() {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// handle error
		}
	}
}
