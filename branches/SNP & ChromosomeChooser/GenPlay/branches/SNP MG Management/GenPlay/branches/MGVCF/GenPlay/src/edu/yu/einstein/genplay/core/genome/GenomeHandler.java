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
package edu.yu.einstein.genplay.core.genome;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import edu.yu.einstein.genplay.core.Chromosome;

/**
 * This class picks up genome information from xml files.
 * @author Nicolas Fourel
 */
public class GenomeHandler extends DefaultHandler {
	
	private Clade 					clade;			// The clade
	private Genome 					genome;			// The genome
	private Assembly 				assembly;		// The assembly
	private Map<String, Chromosome> chromosomeList;	// The chromosome list
	
	
	/**
	 * Constructor of {@link GenomeHandler}
	 */
	protected GenomeHandler() {
		super();
		chromosomeList = new HashMap<String, Chromosome>();
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("assembly")) {
			clade = new Clade(attributes.getValue("clade"));
			genome = new Genome(attributes.getValue("genome"));
			assembly = new Assembly(attributes.getValue("name"), attributes.getValue("date"));
		} else if (qName.equalsIgnoreCase("chromosome")) {
			chromosomeList.put(attributes.getValue("name"), new Chromosome(attributes.getValue("name"), (int)Integer.parseInt(attributes.getValue("length").trim())));
		}
	}
	
	
	/**
	 * This method associates clade information to make a full clade object.
	 */
	protected void computeClade () {
		assembly.setChromosomeList(chromosomeList);
		genome.getAssemblyList().put(assembly.getIndexName(), assembly);
		clade.getGenomeList().put(genome.getName(), genome);
	}
	
	
	/**
	 * @return the clade
	 */
	protected Clade getClade() {
		return clade;
	}
	
}