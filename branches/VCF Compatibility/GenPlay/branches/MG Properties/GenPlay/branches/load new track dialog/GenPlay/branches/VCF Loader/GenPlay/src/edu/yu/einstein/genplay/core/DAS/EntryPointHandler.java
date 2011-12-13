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

import edu.yu.einstein.genplay.core.enums.Strand;


/**
 * Parse a DNS XML file and extract the list of {@link EntryPoint}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
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
