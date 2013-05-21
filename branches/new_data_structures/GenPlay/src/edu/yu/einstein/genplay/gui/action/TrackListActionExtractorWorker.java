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
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.WarningReportDialog;
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


	/**
	 * Handles an exception occurring in the {@link #processAction()} method
	 * @param e exception caught in {@link #processAction()}
	 * @throws Exception If the exception cannot be handled
	 */
	private void handleProcessActionException(Exception e) throws Exception {
		if (e instanceof ElementAddedNotSortedException) {
			String message = "File not sorted:  " + fileToExtract.getName()
					+ "\nThis file cannot be loaded because it is not sorted by chromosome and start position.";
			showWarningMessage(message);
			throw new InterruptedException();
		} else if (e instanceof InvalidFileTypeException) {
			String message = "Invalid file: " + fileToExtract.getName()
					+"\nThe file type is not compatible with the selected layer type.";
			showWarningMessage(message);
			throw new InterruptedException();
		} else {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e);
		}
		/*else if (e.getMessage() != null) {
			String message = "Error in file: " + fileToExtract.getName()
					+ "\nThe following error occurred: \"" + e.getMessage() + "\""
					+ "\nPlease check that the file is sorted and that there is no formatting errors.";
			showWarningMessage(message);
			throw new InterruptedException();
		} else if (!(e instanceof InterruptedException)) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e);
		} */
	}


	@Override
	public void invalidDataExtracted(String invalidDataMessage) {
		showWarningMessage(invalidDataMessage);
	}


	@Override
	protected final T processAction() throws Exception {
		fileToExtract = retrieveFileToExtract();
		if (fileToExtract != null) {
			try {
				extractor = ExtractorFactory.getExtractor(fileToExtract);
				if (extractor != null) {
					name = extractor.getDataName();
					extractor.addInvalidDataListener(this);
					extractor.addOperationProgressListener(this);
					doBeforeExtraction();
					if (ProjectManager.getInstance().isMultiGenomeProject()) {
						extractor.setGenomeName(genomeName);
						extractor.setAlleleType(alleleType);
					}
					return generateList();
				}
			} catch (Exception e) {
				handleProcessActionException(e);
			}
		}
		throw new InterruptedException();
	}


	/**
	 * Asks the user a file to load
	 * @return the file to load. Null if canceled
	 */
	abstract protected File retrieveFileToExtract();


	/**
	 * Shows the warning report dialog displaying a specified message
	 * @param message message to print in the warning report dialog
	 */
	protected void showWarningMessage(String message) {
		WarningReportDialog.getInstance().addMessage(message);
		WarningReportDialog.getInstance().showDialog(getRootPane());
	}


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
