/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.DAS;

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
