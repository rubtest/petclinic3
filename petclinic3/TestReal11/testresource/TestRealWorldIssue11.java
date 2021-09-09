package testresource;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

public class TestRealWorldIssue11 {

	public void rsanopadding() {
		try {
			Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			// handle error
		} catch (NoSuchPaddingException e) {
			// handle error
		}
	}
}
