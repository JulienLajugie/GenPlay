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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.utils.ChromosomesSelector;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataEventsGenerator;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.event.operationProgressEvent.OperationProgressEvent;
import edu.yu.einstein.genplay.gui.event.operationProgressEvent.OperationProgressEventsGenerator;
import edu.yu.einstein.genplay.gui.event.operationProgressEvent.OperationProgressListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class must be extended by the file extractors
 * @author Julien Lajugie
 */
public abstract class Extractor implements InvalidDataEventsGenerator, Stoppable, OperationProgressEventsGenerator {

	/** Maximum number of warning that will be reported */
	private final static int WARNING_LIMIT = 100;

	private final List<OperationProgressListener>	progressListeners; 			// list of progress listeners
	private final ProjectChromosomes 				projectChromosomes;			// Reference to projectChromosomes saved for fast retrieval
	private final File								dataFile;					// file containing the data
	private final List<InvalidDataListener> 		invalidDataListenersList;	// List of invalid data listeners
	private final String							dataName;					// name of the data
	private boolean									isStopped = false;			// set to true if the execution of the extractor needs to be stopped
	private ChromosomesSelector						chromosomeSelector = null; 	// object that defines which chromosomes need to be extracted
	private long									startTime;					// time when the extraction started
	private long 									extractionDuration;			// duration of the extraction in seconds
	private	String									genomeName;					// name of the genome used for the mapping of the data
	private AlleleType								alleleType;					// type of allele to load the data (multi genome)
	private int										warningCount;				// number of warning sent to the listeners
	protected int									itemExtractedCount;			// number of item extracted


	/**
	 * Constructor
	 * @param dataFile file containing the data
	 */
	public Extractor(File dataFile) {
		this.dataFile = dataFile;
		projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		invalidDataListenersList = new ArrayList<InvalidDataListener>();
		warningCount = 0;
		itemExtractedCount = 0;
		dataName = retrieveDataName(dataFile);
		progressListeners = new ArrayList<OperationProgressListener>();
	}



	@Override
	public final void addInvalidDataListener(InvalidDataListener invalidDataListener) {
		if (!invalidDataListenersList.contains(invalidDataListener)) {
			invalidDataListenersList.add(invalidDataListener);
		}
	}


	@Override
	public void addOperationProgressListener(OperationProgressListener operationProgressListener) {
		progressListeners.add(operationProgressListener);
	}


	/**
	 * Finalized the extraction (retrieves the duration of the extraction).
	 */
	protected void finalizeExtraction() {
		extractionDuration = (System.currentTimeMillis() - startTime) / 1000l;
		notifyProgressListeners(OperationProgressEvent.COMPLETE, 100d);
	}


	/**
	 * @return the {@link ChromosomesSelector} object that specifies which chromosomes are selected to be extracted
	 */
	public ChromosomesSelector getChromosomeSelector() {
		return chromosomeSelector;
	}


	/**
	 * @return the file containing the data to extract
	 */
	public File getDataFile() {
		return dataFile;
	}


	/**
	 * @return the name of the data
	 */
	public String getDataName() {
		return dataName;
	}


	/**
	 * @return the duration of the extraction in seconds
	 */
	public long getExtractionDuration() {
		return extractionDuration;
	}


	/**
	 * This method returns the position of the first base of the extractor.
	 * Most files are either 0-based (the first position on a chromosome is 0)
	 * or 1-based (the first position on a chromosome is 1).
	 * @return The position of the first base
	 */
	public abstract int getFirstBasePosition();


	@Override
	public InvalidDataListener[] getInvalidDataListeners() {
		return (InvalidDataListener[]) invalidDataListenersList.toArray();
	}


	/**
	 * @return the number of item extracted
	 */
	public int getItemExtractedCount() {
		return itemExtractedCount;
	}


	@Override
	public OperationProgressListener[] getOperationProgressListeners() {
		OperationProgressListener[] listeners = new OperationProgressListener[progressListeners.size()];
		return progressListeners.toArray(listeners);
	}


	/**
	 * @return a reference to the {@link ProjectChromosomes}.
	 */
	public ProjectChromosomes getProjectChromosome() {
		return projectChromosomes;
	}


	/**
	 * @param chromosome	current chromosome
	 * @param position		current position
	 * @return				the position shifted of 1 bp if the extractor is 0 based
	 * and in the case of multi-genome project, the associated meta genome position
	 */
	protected int getRealGenomePosition(Chromosome chromosome, int position) {
		if (getFirstBasePosition() != 1) {
			position += 1 - getFirstBasePosition();
		}
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			return ShiftCompute.getPosition(genomeName, alleleType, position, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
		} else {
			return position;
		}
	}


	/**
	 * Initializes the extraction (retrieve the time at the begining of the extractio).
	 * @throws IOException
	 */
	protected void initializeExtraction() throws IOException {
		startTime = System.currentTimeMillis();
	}


	/**
	 * @return true if the extraction was stopped
	 */
	public boolean isStopped() {
		return isStopped;
	}


	/**
	 * Notifies the listener that a data event occurred
	 * @param e exception to send to the listeners
	 */
	protected void notifyDataEventListeners(DataLineException e) {
		if (warningCount < WARNING_LIMIT) {
			for (InvalidDataListener listeners: invalidDataListenersList) {
				listeners.invalidDataExtracted(e.getFormattedMessage());
			}
		} else if (warningCount == WARNING_LIMIT) {
			for (InvalidDataListener listeners: invalidDataListenersList) {
				listeners.invalidDataExtracted("And more ...");
			}
		}
		warningCount++;
	}


	/**
	 * Notifies the listener that a data event occurred
	 * @param e exception to send to the listeners
	 * @param lineNumber line number with a problem
	 * @param line line with a problem
	 */
	protected void notifyDataEventListeners(DataLineException e, int lineNumber, String line) {
		e.setFile(dataFile);
		e.setLineNumber(lineNumber);
		e.setLine(line);
		notifyDataEventListeners(e);
	}


	/**
	 * Notifies all the listeners that the progression of an operation changed
	 * @param progressState state of the progression
	 * @param completion completion if the state is IN_PROGRESS
	 */
	private void notifyProgressListeners(int progressState, double completion) {
		OperationProgressEvent evt = new OperationProgressEvent(progressState, completion);
		for (OperationProgressListener listener: progressListeners) {
			listener.operationProgressChanged(evt);
		}
	}


	@Override
	public void removeInvalidDataListener(InvalidDataListener invalidDataListener) {
		invalidDataListenersList.remove(invalidDataListener);
	}


	@Override
	public void removeOperationProgressListener(OperationProgressListener operationProgressListener) {
		progressListeners.remove(operationProgressListener);
	}


	/**
	 * @return the name of the data.  The name of the data is
	 */
	protected abstract String retrieveDataName(File dataFile);


	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(AlleleType alleleType) {
		this.alleleType = alleleType;
	}


	/**
	 * @param chromosomeSelector set the {@link ChromosomesSelector} that specifies which chromosome will be extracted
	 */
	public void setChromosomeSelector(ChromosomesSelector chromosomeSelector) {
		this.chromosomeSelector = chromosomeSelector;
	}


	/**
	 * Sets the position of the first base of the extractor.
	 * Most files are either 0-based (the first position on a chromosome is 0)
	 * or 1-based (the first position on a chromosome is 1).
	 * @param firstBasePosition position of the first base
	 */
	public abstract void setFirstBasePosition(int firstBasePosition);


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	@Override
	public void stop() {
		isStopped = true;
	}
}
