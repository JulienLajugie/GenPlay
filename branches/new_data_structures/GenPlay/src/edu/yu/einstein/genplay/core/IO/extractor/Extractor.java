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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.extractor.Options.ChromosomesSelector;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataEventsGenerator;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;


/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Extractor implements InvalidDataEventsGenerator {

	protected File 						dataFile = null;			// file containing the data
	protected List<InvalidDataListener> invalidDataListenersList;	// List of invalid data listeners
	protected ChromosomesSelector		chromosomeSelector = null; 	// object that defines which chromosomes need to be extracted
	private Long						startTime = null;			// time when the extraction started
	private String						extractionLog = "";			// log of the extraction
	private	String						genomeName;					// name of the genome used for the mapping of the data
	private AlleleType					alleleType;					// type of allele to load the data (multi genome)


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 */
	public Extractor(File dataFile) {
		this.dataFile = dataFile;
		invalidDataListenersList = new ArrayList<InvalidDataListener>();
	}


	@Override
	public void addInvalidDataListener(InvalidDataListener invalidDataListener) {
		if (!invalidDataListenersList.contains(invalidDataListener)) {
			invalidDataListenersList.add(invalidDataListener);
		}
	}


	/**
	 * Extract the header of the a data text file
	 */
	private final void extractHeader() {
		startTime = System.currentTimeMillis();
		parseTrackLine();
		parse
	}


	/**
	 * @return the log about the extraction
	 */
	public String getExtractionLog() {
		return extractionLog;
	}


	@Override
	public InvalidDataListener[] getInvalidDataListeners() {
		return (InvalidDataListener[]) invalidDataListenersList.toArray();
	}


	/**
	 * @param chromosome	current chromosome
	 * @param position		current position
	 * @return				the associated associated meta genome position
	 */
	protected int getMultiGenomePosition (Chromosome chromosome, int position) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
		} else {
			return position;
		}
	}


	/**
	 * Writes basic information about the extraction
	 * in the log file if the log file is specified
	 */
	protected void logBasicInfo() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		extractionLog += "-------------------------------------------------------------------\n";
		extractionLog += "Extraction - " + dateFormat.format(date) + "\n";
		extractionLog += "File: " + dataFile.getAbsolutePath() + "\n";
	}


	/**
	 * Writes information about the execution of the extraction
	 * in the log file if the log file is specified
	 */
	protected void logExecutionInfo() {
		long timeEnd = (System.currentTimeMillis() - startTime) / 1000l;
		extractionLog += "Extraction done. Time elapsed: " + timeEnd + " seconds\n";
	}


	/**
	 * Writes the specified message in the log file
	 * @param message message to write
	 */
	protected void logMessage(String message) {
		extractionLog += message + "\n";
	}


	private parseTrackLine() {



	}


	@Override
	public void removeInvalidDataListener(InvalidDataListener invalidDataListener) {
		invalidDataListenersList.remove(invalidDataListener);
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * Sets the chromosomes selected for the extraction
	 * @param selectedChromosomes array of booleans. The indexes set to true correspond to the index of the selected chromosomes in the {@link ProjectChromosome}
	 */
	public void setSelectedChromosomes(boolean[] selectedChromosomes) {
		chromosomeSelector = new ChromosomesSelector(selectedChromosomes);
	}
}
