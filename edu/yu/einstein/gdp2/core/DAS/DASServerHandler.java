/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parse a DASServer XML file and extract the list of {@link DASServer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASServerHandler extends DefaultHandler {

	private final List<DASServer> 	dasServerList;			// list of DASServer


	/**
	 * Creates an instance of {@link DASServerHandler}
	 */
	public DASServerHandler() {
		super();
		dasServerList = new ArrayList<DASServer>();
	}


	/**
	 * @return the List of {@link DASServer}
	 */
	public final List<DASServer> getDasTypeList() {
		return dasServerList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("SERVER")) {
			DASServer currentDasServer = new DASServer();
			if(attributes.getLength() == 2) {
				currentDasServer.setName(attributes.getValue("name"));
				currentDasServer.setURL(attributes.getValue("href"));
			}
			dasServerList.add(currentDasServer);
		} 
	}	
}
