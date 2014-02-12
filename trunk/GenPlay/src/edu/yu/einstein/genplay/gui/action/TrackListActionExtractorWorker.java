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
package edu.yu.einstein.genplay.gui.action;

import java.io.File;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.IO.extractor.Extractor;
import edu.yu.einstein.genplay.core.IO.extractor.ExtractorFactory;
import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.core.IO.fileSorter.ExternalSortAdapter;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.WarningReportDialog;
import edu.yu.einstein.genplay.gui.event.invalidDataEvent.InvalidDataListener;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Action that starts an extractor in a thread so the GUI doesn't freeze
 * @author Julien Lajugie
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionExtractorWorker<T> extends TrackListActionWorker<T> implements InvalidDataListener {

	private static final long serialVersionUID = -1626148358656459751L; // generated ID
	protected String							name;					// a name
	protected Extractor							extractor;				// an extractor
	protected boolean[]							selectedChromo = null;	// selected chromo


	/**
	 * Public constructor
	 * @param extractor the extractor that will extract the data
	 */
	public TrackListActionExtractorWorker(Extractor	extractor) {
		super();
		this.extractor = extractor;
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
		e.printStackTrace();
		if (e instanceof ElementAddedNotSortedException) {
			String message = "File not sorted:  " + extractor.getDataFile().getName()
					+ "\nThis file cannot be loaded because it is not sorted by chromosome and start position.";
			showWarningMessage(message);
			throw new InterruptedException();
		} else if (e instanceof InvalidFileTypeException) {
			String message = "Invalid file: " + extractor.getDataFile().getName()
					+"\nThe file type is not compatible with the selected layer type.";
			showWarningMessage(message);
			throw new InterruptedException();
		} else if (!(e instanceof InterruptedException)) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e);
		}
	}


	/**
	 * Asks the user if he wants to sort the input file.
	 * @return the result from {@link #generateList()} if the file can be sorted, null otherwise
	 * @throws Exception
	 */
	private T handleUnsortedFile() throws Exception {
		File sortedFile = ExternalSortAdapter.generateOutputFile(extractor.getDataFile());
		int answer = JOptionPane.showConfirmDialog(getRootPane(), "GenPlay cannot load the selected file because it is not sorted.\n"
				+ "Do you want GenPlay to sort the file?\n"
				+ "(This will generate a new file with a .sorted prefix)", "Sort File", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			if (!Utils.cancelBecauseFileExist(getRootPane(), sortedFile)) {
				notifyActionStart("Sorting File", 1, false);
				ExternalSortAdapter.externalSortGenomicFile(extractor.getDataFile());
				Extractor newExtractor = ExtractorFactory.getExtractor(sortedFile);
				newExtractor.addInvalidDataListener(this);
				newExtractor.addOperationProgressListener(this);
				newExtractor.setGenomeName(genomeName);
				newExtractor.setAlleleType(alleleType);
				newExtractor.setChromosomeSelector(extractor.getChromosomeSelector());
				newExtractor.setFirstBasePosition(extractor.getFirstBasePosition());
				if (extractor instanceof StrandedExtractor) {
					((StrandedExtractor) newExtractor).setStrandedExtractorOptions(((StrandedExtractor) extractor).getStrandedExtractorOptions());
				}
				extractor = newExtractor;
				return generateList();
			}
		}
		stop();
		return null;
	}


	@Override
	public void invalidDataExtracted(String invalidDataMessage) {
		showWarningMessage(invalidDataMessage);
	}


	@Override
	protected final T processAction() throws Exception {
		try {
			name = extractor.getDataName();
			extractor.addInvalidDataListener(this);
			extractor.addOperationProgressListener(this);
			doBeforeExtraction();
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				extractor.setGenomeName(genomeName);
				extractor.setAlleleType(alleleType);
			}
			try {
				return generateList();
			} catch (ElementAddedNotSortedException e) {
				return handleUnsortedFile();
			}

		} catch (Exception e) {
			if (extractor != null) {
				extractor.stop();
			}
			handleProcessActionException(e);
		}
		throw new InterruptedException();
	}


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
