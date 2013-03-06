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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;



/**
 * Parse a DNS XML file and extract the list of {@link SimpleScoredChromosomeWindow}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWHandler extends DefaultHandler {

	private final List<ScoredChromosomeWindow>	SCWList;				// list of SCW
	private final Chromosome 					chromosome;				// chromosome being extracted
	private String 								currentMarkup = null;	// current XML markup
	private	ScoredChromosomeWindow				currentSCW = null;		// current SCW
	private String 								genomeName;				// for multi-genome project only.  Name of the genome on which the data were mapped
	private AlleleType 							alleleType;				// for multi-genome project only.  Type of allele for synchronization


	/**
	 * Creates an instance of {@link SCWHandler}
	 * @param chromosome current {@link Chromosome}
	 */
	public SCWHandler(Chromosome chromosome) {
		super();
		SCWList = new ArrayList<ScoredChromosomeWindow>();
		this.chromosome = chromosome;
	}


	/**
	 * @return the List of {@link ScoredChromosomeWindow}
	 */
	public final List<ScoredChromosomeWindow> getScoreChromosomeWindowList() {
		return SCWList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			currentSCW = new SimpleScoredChromosomeWindow();
		} else 	if (qName.equalsIgnoreCase("START")) {
			currentMarkup = "START";
		} else 	if (qName.equalsIgnoreCase("END")) {
			currentMarkup = "END";
		} else 	if (qName.equalsIgnoreCase("SCORE")) {
			currentMarkup = "SCORE";
		} else {
			currentMarkup = null;
		}
	}


	/**
	 * Adds a SCW to the list of SCW at the end of a FEATURE element
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			SCWList.add(currentSCW);
		}
	}


	@Override
	public void characters(char[] ch, int start, int length) {
		if (currentMarkup != null) {
			String elementValue = new String(ch, start, length);
			if (currentMarkup.equals("START")) {
				currentSCW.setStart(getMultiGenomePosition(Integer.parseInt(elementValue)));
			} else if (currentMarkup.equals("END")) {
				currentSCW.setStop(getMultiGenomePosition(Integer.parseInt(elementValue)));
			} else if (currentMarkup.equals("SCORE")) {
				// if the score is not specified we set a 0 score value
				if (elementValue.trim().equals("-")) {
					currentSCW.setScore(0);
				} else {
					currentSCW.setScore(Double.parseDouble(elementValue));
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
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
			//return ShiftCompute.computeShift(genomeName, chromosome, alleleType, position);
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


	/**
	 * @return the alleleType
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}
}
