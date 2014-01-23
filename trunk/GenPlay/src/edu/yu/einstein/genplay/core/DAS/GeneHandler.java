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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.gene.SimpleGene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Parse a DNS XML file and extract the list of {@link Gene}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 */
public class GeneHandler extends DefaultHandler {

	private final ListViewBuilder<Gene>				geneLVBuilder;			// list of genes
	private String 									currentMarkup = null;	// current XML markup
	private String 									previousGroupID = null;
	private	String 									groupID;				// a group define a gene for the different exons
	private	int 									start;					// start position
	private	int 									end;					// stop position
	private	float 									score;					// score
	private Strand 									orientation;			// strand
	private String  								name;					// name
	private final Chromosome 						chromosome;				// chromosome being extracted
	private SCWListViewBuilder 						exonLVBuilder;			// builders for the list view of exon
	private String 									genomeName;				// for multi-genome project only.  Name of the genome on which the data were mapped
	private AlleleType 								alleleType;				// for multi-genome project only.  Type of allele for synchronization


	/**
	 * Creates an instance of {@link GeneHandler}
	 * @param chromosome current {@link Chromosome}
	 */
	public GeneHandler(Chromosome chromosome) {
		super();
		geneLVBuilder = new GeneListViewBuilder();
		this.chromosome = chromosome;
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
				// if the score is not specified we set a NaN score value
				if (elementValue.trim().equals("-")) {
					score = Float.NaN;
				} else {
					score = Float.parseFloat(elementValue);
				}
			}
		}
	}


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			// case where it's the first gene of the document
			if (previousGroupID == null) {
				previousGroupID = groupID;
				exonLVBuilder = new GenericSCWListViewBuilder();
			} else if (!groupID.equalsIgnoreCase(previousGroupID)) {	// if we have a new group we add the previous gene to the list
				ListView<ScoredChromosomeWindow> exonList = exonLVBuilder.getListView();
				int start = exonList.get(0).getStart();
				int stop = exonList.get(exonList.size() - 1).getStop();
				// TODO check if we can retrieve the overall score of the gene
				Gene gene = new SimpleGene(name, orientation, start, stop, Float.NaN, exonList);
				geneLVBuilder.addElementToBuild(gene);
				previousGroupID = groupID;
				exonLVBuilder = new GenericSCWListViewBuilder();
			}
			exonLVBuilder.addElementToBuild(getMultiGenomePosition(start), getMultiGenomePosition(end), score);
		}
	}


	/**
	 * @return the alleleType
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}


	/**
	 * @return the List of {@link Gene}
	 */
	public final ListView<Gene> getGeneList() {
		return geneLVBuilder.getListView();
	}


	/**
	 * @return the name of the genome on which the data were mapped.  For multi-genome project only
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @param position		current position
	 * @return				the associated associated meta genome position
	 */
	private int getMultiGenomePosition (int position) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
		} else {
			return position;
		}
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param genomeName for multi-genome project only.  Name of the genome on which the data were mapped
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
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

}
