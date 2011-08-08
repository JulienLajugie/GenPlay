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

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;


/**
 * Parse a DNS XML file and extract the list of {@link Gene}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneHandler extends DefaultHandler {

	private final	List<Gene>	geneList;	// list of genes
	private String 	currentMarkup = null;	// current XML markup
	private	Gene	currentGene = null;		// current gene

	private String previousGroupID = null;

	private	String 	groupID;				// a group define a gene for the different exons
	private	int 	start;
	private	int 	end;
	private	double 	score;
	private Strand 	orientation;
	private String  name;
	private final Chromosome chromosome;	// chromosome being extracted
	private String genomeName;				// for multi-genome project only.  Name of the genome on which the data were mapped
	

	/**
	 * Creates an instance of {@link GeneHandler}
	 */
	public GeneHandler(Chromosome chromosome) {
		super();
		geneList = new ArrayList<Gene>();
		this.chromosome = chromosome;
	}


	/**
	 * @return the List of {@link Gene}
	 */
	public final List<Gene> getGeneList() {
		return geneList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			if(attributes.getLength() > 0) {
				name = attributes.getValue("label");
			}
		} else if (qName.equalsIgnoreCase("START")) {
			currentMarkup = "START";
		} else 	if (qName.equalsIgnoreCase("END")) {
			currentMarkup = "END";
		} else 	if (qName.equalsIgnoreCase("SCORE")) {
			currentMarkup = "SCORE";
		} else 	if (qName.equalsIgnoreCase("ORIENTATION")) {
			currentMarkup = "ORIENTATION";
		} else 	if (qName.equalsIgnoreCase("GROUP")) {
			currentMarkup = "GROUP";
			if(attributes.getLength() > 0) {
				groupID = attributes.getValue("id");
			}
		} else {
			currentMarkup = null;
		}
	}


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			// case where it's the first gene of the document
			if (previousGroupID == null) {
				currentGene = new Gene();
				previousGroupID = groupID;
			} else if (!groupID.equalsIgnoreCase(previousGroupID)) {	// if we have a new group we add the previous gene to the list
				// set the gene start
				currentGene.setStart(getMultiGenomePosition(currentGene.getExonStarts()[0]));
				// set the gene stop
				currentGene.setStop(getMultiGenomePosition(currentGene.getExonStops()[currentGene.getExonStops().length - 1]));
				currentGene.setName(name);
				currentGene.setStrand(orientation);
				currentGene.setChromo(chromosome);
				geneList.add(currentGene);
				previousGroupID = groupID;
				currentGene = new Gene();
			} 
			currentGene.addExon(getMultiGenomePosition(start), getMultiGenomePosition(end), score);			
		}
	}


	@Override
	public void characters(char[] ch, int start, int length) {
		if (currentMarkup != null) {
			String elementValue = new String(ch, start, length);
			if (currentMarkup.equals("START")) {
				this.start = Integer.parseInt(elementValue);
			} else if (currentMarkup.equals("END")) {
				end = Integer.parseInt(elementValue);
			} else if (currentMarkup.equals("ORIENTATION")) {
				orientation = Strand.get(elementValue.charAt(0));				
			} else if (currentMarkup.equals("SCORE")) {
				// if the score is not specified we set a 0 score value
				if (elementValue.trim().equals("-")) {
					score = 0;
				} else {
					score = Double.parseDouble(elementValue);
				}
			}
		}
	}
	
	
	/**
	 * @param position		current position
	 * @return				the associated associated meta genome position
	 */
	private int getMultiGenomePosition (int position) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.computeShift(genomeName, chromosome, position);
		} else {
			return position;
		}
	}	
	
	
	/**
	 * @param genomeName for multi-genome project only.  Name of the genome on which the data were mapped
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * @return the name of the genome on which the data were mapped.  For multi-genome project only
	 */
	public String getGenomeName() {
		return genomeName;
	}
}
