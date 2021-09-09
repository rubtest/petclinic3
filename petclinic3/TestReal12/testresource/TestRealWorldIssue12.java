package testresource;

import java.security.SecureRandom;
import java.util.Random;

public class TestRealWorldIssue12 {
	/* Output 1st, 2nd and 3rd Lotto numbers to stdout.*/
	public void issue12() {
		Random r = new Random();
		System.out.println(r.nextInt());
		System.out.println(r.nextInt());
		System.out.println(r.nextInt());
	}

	public void issue12b() {
		Random r = new Random(123L);
		System.out.println(r.nextInt());
		System.out.println(r.nextInt());
		System.out.println(r.nextInt());
	}

	public void issue12c() {
		System.out.println(Math.random());
	}

	public void safe() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
	}
}
