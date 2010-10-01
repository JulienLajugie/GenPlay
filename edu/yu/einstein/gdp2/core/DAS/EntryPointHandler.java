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

import yu.einstein.gdp2.core.enums.Strand;

public class EntryPointHandler extends DefaultHandler {

	private final List<EntryPoint> 	entryPointList;	// list of EntryPoint


	/**
	 * Creates an instance of {@link EntryPointHandler}
	 */
	public EntryPointHandler() {
		super();
		entryPointList = new ArrayList<EntryPoint>();
	}


	/**
	 * @return the List of {@link EntryPoint}
	 */
	public final List<EntryPoint> getEntryPointList() {
		return entryPointList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("SEGMENT")) {
			EntryPoint currentEntryPoint = new EntryPoint();
			if(attributes.getLength() > 0) {
				for(int i = 0 ; i < attributes.getLength() ; i++) {
					if (attributes.getQName(i).equalsIgnoreCase("ID")) {
						currentEntryPoint.setID(attributes.getValue(i));
					} else if (attributes.getQName(i).equalsIgnoreCase("START")) {
						currentEntryPoint.setStart(Integer.parseInt(attributes.getValue(i)));
					} else if (attributes.getQName(i).equalsIgnoreCase("STOP")) {
						currentEntryPoint.setStop(Integer.parseInt(attributes.getValue(i)));
					} else if (attributes.getQName(i).equalsIgnoreCase("ORIENTATION")) {
						currentEntryPoint.setOrientation(Strand.get(attributes.getValue(i).charAt(0)));
					}
				}
			}
			entryPointList.add(currentEntryPoint);
		} 
	}	
}
