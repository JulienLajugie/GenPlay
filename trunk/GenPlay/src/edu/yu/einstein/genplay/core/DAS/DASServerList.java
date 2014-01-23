/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.DAS;

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
