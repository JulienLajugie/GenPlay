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
package edu.yu.einstein.genplay.core.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataEventsGenerator;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Extractor implements Serializable, InvalidDataEventsGenerator {

	private static final long serialVersionUID = 374481155831573347L;	// generated ID
	protected static final int NEED_TO_BE_EXTRACTED = 0;			// the chromsome needs to be extracted
	protected static final int NEED_TO_BE_SKIPPED = 1;				// the chromosome needs to be skipped
	protected static final int AFTER_LAST_SELECTED = 2;				// the chromosome is after the last selected chromosome
	protected File 						dataFile = null;			// file containing the data
	protected File 						logFile = null;				// log file
	protected long 						startTime = 0;				// time at the beginning of the extraction
	protected String					name = null;				// name
	protected final ProjectChromosome 	projectChromosome;			// ChromosomeManager
	protected List<InvalidDataListener> invalidDataListenersList;	// List of invalid data listeners
	private boolean[] 					selectedChromo = null;		// array of booleans. The indexes set to true correspond to the index of the selected chromosomes in the ChromosomeManager
	private boolean						isFileSorted = true;		// boolean indicating if the data file is sorted
	private int		 					lastSelectedChromoIndex;	// index of the last chromosome to extract
	private	String						genomeName;					// name of the genome used for the mapping of the data
	private AlleleType					alleleType;					// type of allele to load the data (multi genome)


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 * @param logFile file for the log (no log if null)
	 */
	public Extractor(File dataFile, File logFile) {
		this.dataFile = dataFile;
		this.logFile = logFile;
		this.projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.name = Utils.getFileNameWithoutExtension(dataFile);
		invalidDataListenersList = new ArrayList<InvalidDataListener>();
	}


	/**
	 * @param chromosome a chromosome
	 * @return if the specified chromosome needs to be extracted, needs to be skipped or is after the last selected chromosome
	 */
	protected int checkChromosomeStatus(Chromosome chromosome) {
		if (selectedChromo == null) {
			return NEED_TO_BE_EXTRACTED;
		} else {
			int index = projectChromosome.getIndex(chromosome);
			if ((index > lastSelectedChromoIndex) && isFileSorted) {
				return AFTER_LAST_SELECTED;
			} else {
				if (selectedChromo[index]) {
					return NEED_TO_BE_EXTRACTED;
				} else {
					return NEED_TO_BE_SKIPPED;
				}
			}
		}
	}


	/**
	 * Extracts the data from a file.
	 * @throws Exception
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public abstract void extract() throws Exception;


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Writes basic information about the extraction
	 * in the log file if the log file is specified
	 */
	protected void logBasicInfo() {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				writer.write("-------------------------------------------------------------------");
				writer.newLine();
				writer.write("Extraction - " + dateFormat.format(date));
				writer.newLine();
				writer.write("File: " + dataFile.getAbsolutePath());
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * Writes information about the execution of the extraction
	 * in the log file if the log file is specified
	 */
	protected void logExecutionInfo() {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				long timeEnd = (System.currentTimeMillis() - startTime) / 1000l;
				writer.write("Extraction done. Time elapsed: " + timeEnd + " seconds");
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * Writes the specified message in the log file
	 * @param message message to write
	 */
	protected void logMessage(String message) {
		if(logFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write(message);
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * Sets the chromosomes selected for the extraction
	 * @param selectedChromosomes array of booleans. The indexes set to true correspond to the index of the selected chromosomes in the {@link ProjectChromosome}
	 */
	public void setSelectedChromosomes(boolean[] selectedChromosomes) {
		this.selectedChromo = selectedChromosomes;
		// look for the index of the last selected chromosome
		if (selectedChromo == null) {
			lastSelectedChromoIndex = projectChromosome.size() -1;
		} else {
			int lastIndex = 0;
			for (int i = 0; i < projectChromosome.size(); i++) {
				if (selectedChromo[i]) {
					lastIndex = i;
				}
			}
			lastSelectedChromoIndex = lastIndex;
		}
	}


	/**
	 * @param isFileSorted the isFileSorted to set
	 */
	public void setFileSorted(boolean isFileSorted) {
		this.isFileSorted = isFileSorted;
	}


	/**
	 * @return the isFileSorted
	 */
	public boolean isFileSorted() {
		return isFileSorted;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param chromosome	current chromosome
	 * @param position		current position
	 * @return				the associated associated meta genome position
	 */
	protected int getMultiGenomePosition (Chromosome chromosome, int position) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
			//return ShiftCompute.computeShift(genomeName, chromosome, alleleType, position);
		} else {
			return position;
		}
	}


	@Override
	public void addInvalidDataListener(InvalidDataListener invalidDataListener) {
		if (!invalidDataListenersList.contains(invalidDataListener)) {
			invalidDataListenersList.add(invalidDataListener);
		}
	}


	@Override
	public InvalidDataListener[] getInvalidDataListeners() {
		return (InvalidDataListener[]) invalidDataListenersList.toArray();
	}


	@Override
	public void removeInvalidDataListener(InvalidDataListener invalidDataListener) {
		invalidDataListenersList.remove(invalidDataListener);
	}
}
