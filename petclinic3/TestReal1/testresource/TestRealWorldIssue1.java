package testresource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.net.URL;

public class TestRealWorldIssue1 {
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/client/import/endpoint/json/{type}")
	public void importJsonUrl(@PathVariable("type") String type, @Validated @RequestBody Import importRequest) {
		try {
			Resource resource = new UrlResource(new URL(importRequest.getUrl()));
		} catch (IOException e) {
			System.out.println("Malformed URL");
		}
	}
}

// self-made Import class since we don't have the original source code
class Import {

	private String url;

	public String getUrl() {
		return this.url;
	}
}
