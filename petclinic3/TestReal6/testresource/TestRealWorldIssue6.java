package testresource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
@EnableAutoConfiguration
public class TestRealWorldIssue6 {
	@RequestMapping(value = "/xxe2", method = RequestMethod.POST)
	public void xxe2(Model model, @RequestBody String xml) throws ParserConfigurationException, SAXException, IOException {//Welcome page, non-rest
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		XMLReader reader = XMLReaderFactory.createXMLReader();
		MyHandler handler = new MyHandler();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(stream));
	}
}
