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
package edu.yu.einstein.genplay.gui.action;

import java.io.File;

import edu.yu.einstein.genplay.core.IO.extractor.Extractor;
import edu.yu.einstein.genplay.core.IO.extractor.ExtractorFactory;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Action that starts an extractor in a thread so the GUI doesn't freeze
 * @author Julien Lajugie
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionExtractorWorker<T> extends TrackListActionWorker<T> implements InvalidDataListener {

	private static final long serialVersionUID = -1626148358656459751L; // generated ID
	protected File								fileToExtract;  		// file to extract
	protected String							name;					// a name
	protected Extractor							extractor;				// an extractor
	protected boolean[]							selectedChromo = null;	// selected chromo


	/**
	 * Public constructor
	 */
	public TrackListActionExtractorWorker() {
		super();
	}


	/**
	 * Can be overridden to define actions to do before the extraction
	 */
	protected void doBeforeExtraction() throws InterruptedException {}


	/**
	 * This method has to be implemented to specify how to generate
	 * the data list from the {@link Extractor}
	 * @return a list from whose type depends on the generic parameter T of this class
	 * @throws Exception
	 */
	abstract protected T generateList() throws Exception;


	@Override
	public void handleDataError(DataLineException e) {
		handleError(e);
	}


	@Override
	protected final T processAction() throws Exception {
		fileToExtract = retrieveFileToExtract();
		if (fileToExtract != null) {
			extractor = ExtractorFactory.getExtractor(fileToExtract);
			if (extractor != null) {
				name = extractor.getDataName();
				extractor.addInvalidDataListener(this);
				doBeforeExtraction();
				if (ProjectManager.getInstance().isMultiGenomeProject()) {
					extractor.setGenomeName(genomeName);
					extractor.setAlleleType(alleleType);
				}
				notifyActionStart("Generating Layer", 1, true);
				return generateList();
			} else {
				throw new InvalidFileTypeException();
			}
		} else {
			throw new InterruptedException();
		}
	}


	/**
	 * Asks the user a file to load
	 * @return the file to load. Null if canceled
	 */
	abstract protected File retrieveFileToExtract();


	/**
	 * Override that stops the extractor
	 */
	@Override
	public void stop() {
		if (extractor instanceof Stoppable) {
			((Stoppable) extractor).stop();
		}
		super.stop();
	}
}
