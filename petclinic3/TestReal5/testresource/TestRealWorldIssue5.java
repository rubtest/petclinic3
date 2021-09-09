package testresource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
@EnableAutoConfiguration
public class TestRealWorldIssue5 {
	@RequestMapping(value = "/xxe1", method = RequestMethod.POST)
	public String xxe1(Model model, @RequestBody String xml) throws ParserConfigurationException, SAXException, IOException {//Welcome page, non-rest
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		MyHandler handler = new MyHandler();
		parser.parse(stream, handler);
		return "xxe1";
	}
}

class MyHandler extends DefaultHandler {
}
