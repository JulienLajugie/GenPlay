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
 * Parse a DASTYPE XML file and extract the list of {@link DASType}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASTypeHandler extends DefaultHandler {

	private final List<DASType> 	dasTypeList;			// list of DASType


	/**
	 * Creates an instance of {@link DASTypeHandler}
	 */
	public DASTypeHandler() {
		super();
		dasTypeList = new ArrayList<DASType>();
	}


	/**
	 * @return the List of {@link DASType}
	 */
	public final List<DASType> getDasTypeList() {
		return dasTypeList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("TYPE")) {
			DASType currentDasType = new DASType();
			if(attributes.getLength() > 0) {
				currentDasType.setID(attributes.getValue("id"));
				currentDasType.setMethod(attributes.getValue("method"));
				currentDasType.setCategory(attributes.getValue("category"));
				currentDasType.setPreferredFormat(attributes.getValue("preferred_format"));
			}
			dasTypeList.add(currentDasType);
		}
	}
}
