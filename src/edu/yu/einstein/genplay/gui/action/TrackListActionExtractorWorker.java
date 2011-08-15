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
package edu.yu.einstein.genplay.gui.action;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.extractor.Extractor;
import edu.yu.einstein.genplay.core.extractor.ExtractorFactory;
import edu.yu.einstein.genplay.core.generator.Generator;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.exception.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Action that starts an extractor in a thread so the GUI doesn't freeze
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionExtractorWorker<T> extends TrackListActionWorker<T> {

	private static final long serialVersionUID = -1626148358656459751L; // generated ID
	private final Class<? extends Generator>	extractorClass;			// desired class of extractor
	private File	 							logFile;				// a file we extracts
	protected File								fileToExtract;  		// file to extract
	protected String							name;					// a name 
	protected Extractor							extractor;				// an extractor
	protected boolean[]							selectedChromo = null;	// selected chromo
	protected String							genomeName = null;		// genome name for a multi genome project

	
	/**
	 * Public constructor 
	 * @param extractorClass {@link Class} of the {@link Extractor}
	 */
	public TrackListActionExtractorWorker(Class<? extends Generator> extractorClass) {
		super();
		this.extractorClass = extractorClass;
		retrieveLogFile();
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
	protected final T processAction() throws Exception {
		fileToExtract = retrieveFileToExtract();		
		if (fileToExtract != null) {
			extractor = ExtractorFactory.getExtractor(fileToExtract, logFile);
			if ((extractor != null) && (extractorClass.isAssignableFrom(extractor.getClass()))) {
				name = extractor.getName();
				doBeforeExtraction();
				if (ProjectManager.getInstance().isMultiGenomeProject()) {
					extractor.setGenomeName(genomeName);
				}
				notifyActionStart("Loading File", 1, extractor instanceof Stoppable);
				extractor.extract();
				notifyActionStop();
				System.gc();
				notifyActionStart("Generating Track", 1, true);
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
	 * Retrieves the log file from the configuration manager and check if the file is valid / accessible
	 */
	private void retrieveLogFile() {
		logFile = new File(ConfigurationManager.getInstance().getLogFile());
		if (logFile != null) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				logFile = null;
			}
			// check if the user has the permission to write the log
			if (!logFile.canWrite()) {
				logFile = null;
				JOptionPane.showMessageDialog(getTrackList(), "Impossible to access or create the log file \"" + 
						logFile + "\"", "Invalid Log File", JOptionPane.WARNING_MESSAGE, null);
			}
		}
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
