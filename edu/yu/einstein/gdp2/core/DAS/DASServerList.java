/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	 * @param DASServerFile	XML file containing information about the DAS server available
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DASServerList(File DASServerFile) throws ParserConfigurationException, SAXException, IOException {
		super();
		InputStream is = new FileInputStream(DASServerFile);
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DASServerHandler dsh = new DASServerHandler();
		parser.parse(is, dsh);
		addAll(dsh.getDasTypeList());
	}

}
