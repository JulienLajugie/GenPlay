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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.DAS;

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
