/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.extractor.Extractor;
import yu.einstein.gdp2.core.extractor.ExtractorFactory;
import yu.einstein.gdp2.core.generator.Generator;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.util.Utils;


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


	/**
	 * Public constructor 
	 */
	public TrackListActionExtractorWorker(Class<? extends Generator> extractorClass) {
		super();
		this.extractorClass = extractorClass;
		retrieveLogFile();
	}


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


	@Override
	protected final T processAction() throws Exception {
		fileToExtract = retrieveFileToExtract();		
		if (fileToExtract != null) {
			extractor = ExtractorFactory.getExtractor(fileToExtract, logFile);
			if ((extractor != null) && (extractorClass.isAssignableFrom(extractor.getClass()))) {
				notifyLoadingStart();
				extractor.extract();
				notifyActionStop();
				if (extractor.getName() != null) {
					name = extractor.getName();
				} else {
					name = Utils.getFileNameWithoutExtension(fileToExtract);
				}
				System.gc();
				notifyActionStart("Generating Track", 1);
				return generateList();
			} else {
				throw new InvalidFileTypeException();
			}
		} else {
			throw new InterruptedException();
		}
	}

	
	/**
	 * Notifies that the file loading starts.
	 * This steps can't be stopped.
	 */
	private void notifyLoadingStart() {
		MainFrame.getInstance().getStatusBar().actionStart("Loading File", 1, null);
	}
	

	/**
	 * This method has to be implemented to specify how to generate
	 * the data list from the {@link Extractor}
	 * @return a list from whose type depends on the generic parameter T of this class
	 * @throws Exception
	 */
	abstract protected T generateList() throws Exception;


	/**
	 * Asks the user a file to load
	 * @return the file to load. Null if canceled
	 */
	abstract protected File retrieveFileToExtract();
}
