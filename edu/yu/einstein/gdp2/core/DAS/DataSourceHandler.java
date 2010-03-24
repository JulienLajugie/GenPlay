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
 * Parse a DNS XML file and extract the list of data source
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DataSourceHandler extends DefaultHandler {

	private final List<DataSource> 	dataSourceList;					// list of DataSource
	private String 					currentMarkup = null;			// current XML markup
	private DataSource 				currentDataSource = null;		// current data source


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
			currentMarkup = "SOURCE";
			if(attributes.getLength() > 0) {
				currentDataSource.setID(attributes.getValue("id"));
				currentDataSource.setVersion(attributes.getValue("version"));
			}
		} else if (qName.equalsIgnoreCase("MAPMASTER")) {
			currentMarkup = "MAPMASTER";
		} else if (qName.equalsIgnoreCase("DESCRIPTION")) {
			currentMarkup = "DESCRIPTION";
			if(attributes.getLength() > 0) {
				currentDataSource.setHref(attributes.getValue("href"));
			}
		} else {
			currentMarkup = null;
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
		if (currentMarkup != null) {
			String elementValue = new String(ch, start, length);
			if (currentMarkup.equals("SOURCE")) {
				currentDataSource.setName(elementValue);
			} else if (currentMarkup.equals("MAPMASTER")) {
				currentDataSource.setMapMaster(elementValue);
			} else if (currentMarkup.equals("DESCRIPTION")) {
				currentDataSource.setDescription(elementValue);
			}
		}
	}
}
