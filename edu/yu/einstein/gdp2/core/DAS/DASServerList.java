/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


/**
 * A list of DAS servers
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASServerList extends ArrayList<DASServer> {

	private static final long serialVersionUID = 9132952222755375888L;	// generated ID
		
	
	/**
	 * Creates an instance of {@link DASServerList}
	 * @param DASServerFile	URL of the XML file containing information about the DAS servers available
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DASServerList(URL DASServerFile) throws ParserConfigurationException, SAXException, IOException {
		super();
		URLConnection connection = DASServerFile.openConnection();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DASServerHandler dsh = new DASServerHandler();
		parser.parse(connection.getInputStream(), dsh);
		addAll(dsh.getDasTypeList());
	}
}
