/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DASconnector;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parse a DNS XML file and extract the list of data source
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DataSourceHandler extends DefaultHandler {

	private final 	List<DataSource> 	dataSourceList;					// list of DataSource
	private 		String 				currentElement = null;			// current XML markup
	private 		DataSource 			currentDataSource = null;		// current data source
	

	/**
	 * Creates an instance of {@link DataSourceHandler}
	 */
	public DataSourceHandler() {
		super();
		dataSourceList = new ArrayList<DataSource>();
	}
	

	/**
	 * @return the List of {@link DataSource}
	 */
	public final List<DataSource> getDataSourceList() {
		return dataSourceList;
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("DSN")) {
			currentDataSource = new DataSource();
		} else if (qName.equalsIgnoreCase("SOURCE")) {
			currentElement = "SOURCE";
			if(attributes.getLength() > 0) {
				for(int i = 0 ; i < attributes.getLength() ; i++) {
					if (attributes.getQName(i).equalsIgnoreCase("ID")) {
						currentDataSource.setID(attributes.getValue(i));
					} else if (attributes.getQName(i).equalsIgnoreCase("VERSION")) {
						currentDataSource.setVersion(attributes.getValue(i));
					}
				}
			}
		} else if (qName.equalsIgnoreCase("MAPMASTER")) {
			currentElement = "MAPMASTER";
		} else if (qName.equalsIgnoreCase("DESCRIPTION")) {
			currentElement = "DESCRIPTION";
			if(attributes.getLength() > 0) {
				for(int i = 0 ; i < attributes.getLength() ; i++) {
					if (attributes.getQName(i).equalsIgnoreCase("HREF")) {
						currentDataSource.setHref(attributes.getValue(i));
					}
				}
			}
		}
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("DSN")) {
			dataSourceList.add(currentDataSource);
		}
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length) {
		String elementValue = new String(ch, start, length);
		if (currentElement.equals("SOURCE")) {
			currentDataSource.setName(elementValue);
		} else if (currentElement.equals("MAPMASTER")) {
			currentDataSource.setMapMaster(elementValue);
		} else if (currentElement.equals("DESCRIPTION")) {
			currentDataSource.setDescription(elementValue);
		}
	}
}
