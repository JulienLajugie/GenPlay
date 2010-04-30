/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import yu.einstein.gdp2.core.extractor.Extractor;
import yu.einstein.gdp2.core.extractor.ExtractorFactory;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEventsGenerator;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.Utils;


/**
 * A worker thread that extracts the data from a file 
 * and generates and returns a list of data.
 * The first generic parameter EC is the subtype of {@link Extractor}.
 * The second generic parameter is the type of list of data.
 * The generateList() method has to be implemented to specify how to generate
 * the data list from the {@link Extractor}
 * The doAtTheEnd() method has to be implemented to specify what to do at the
 * end of the loading.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ExtractorWorker<EC, LC> extends SwingWorker<LC, Void> implements TrackListActionEventsGenerator {

	protected final TrackList 		trackList;			// TrackList 
	private final Class<EC>			extractorClass;		// desired class of extractor
	private final ChromosomeManager chromosomeManager;	// a ChromosomeManager
	private final List<TrackListActionListener> tlalListenerList;	// list of GenomeWindowListener
	private final String actionStartDescription;
	protected final File			fileToExtract;  	// file to extract
	protected File	 				logFile;			// a file we extracts
	protected String				name;				// a name 
	protected Extractor				extractor;	


	/**
	 * Creates an instance of an {@link ExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param extractorClass desired class of extractor
	 */
	public ExtractorWorker(TrackList trackList, String logFile, File fileToExtract, ChromosomeManager chromosomeManager, Class<EC> extractorClass, String actionStartDescription) {
		this.chromosomeManager = chromosomeManager;
		this.trackList = trackList;
		this.extractorClass = extractorClass;
		if (logFile != null) {
			this.logFile = new File(logFile);
			try {
				this.logFile.createNewFile();
			} catch (IOException e) {
				this.logFile = null;
			}
			// check if the user has the permission to write the log
			if (!this.logFile.canWrite()) {
				this.logFile = null;
				JOptionPane.showMessageDialog(trackList, "Impossible to access or create the log file \"" + 
						logFile + "\"", "Invalid Log File", JOptionPane.WARNING_MESSAGE, null);
			}
		} else {
			this.logFile = null;
		}		
		this.fileToExtract = fileToExtract;
		this.tlalListenerList = new ArrayList<TrackListActionListener>();
		addTrackListActionListener(trackList);
		this.actionStartDescription = actionStartDescription;
	}


	/**
	 * Extracts the data and return an {@link Extractor} containing the data
	 */
	@Override
	final protected LC doInBackground() throws Exception, ManagerDataNotLoadedException, InvalidChromosomeException, InvalidFileTypeException {
		if (fileToExtract != null) {
			extractor = ExtractorFactory.getExtractor(fileToExtract, logFile, chromosomeManager);
			if ((extractor != null) && (extractorClass.isAssignableFrom(extractor.getClass()))) {
				notifyActionStarted(actionStartDescription);
				extractor.extract();
				if (extractor.getName() != null) {
					name = extractor.getName();
				} else {
					name = Utils.getFileNameWithoutExtension(fileToExtract);
				}
				System.gc();
				return generateList();
			} else {
				throw new InvalidFileTypeException();
			}
		} else {
			return null;
		}
	};


	/**
	 * This method can be overload  to specify what to do at the end of the loading
	 */
	@Override
	final protected void done() {
		doAtTheEnd();
	}


	/**
	 * This method has to be implemented to specify how to generate
	 * the data list from the {@link Extractor}
	 * @return a list from whose type depends on the generic parameter LC of this class
	 * @throws ManagerDataNotLoadedException
	 * @throws InvalidChromosomeException
	 */
	abstract public LC generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException;


	/**
	 * This method has to be implemented to specify what to do at the
	 * end of the loading.
	 */
	abstract public void doAtTheEnd();
	
	
	@Override
	public void addTrackListActionListener(TrackListActionListener trackListActionListener) {
		tlalListenerList.add(trackListActionListener);		
	}


	@Override
	public TrackListActionListener[] getOperationOnTrackListener() {
		TrackListActionListener[] operationOnTrackListeners = new TrackListActionListener[tlalListenerList.size()];
		return tlalListenerList.toArray(operationOnTrackListeners);
	}


	@Override
	public void removeTrackListActionListener(TrackListActionListener trackListActionListener) {
		tlalListenerList.remove(trackListActionListener);		
	}
	
	
	/**
	 * Notifies all the {@link TrackListActionListener} that an action started
	 * @param actionDescription
	 */
	protected void notifyActionStarted(String actionDescription) {
		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
		for (TrackListActionListener tal: tlalListenerList) {
			tal.actionStarts(evt);
		}
	}
	
	
	/**
	 * Notifies all the {@link TrackListActionListener} that an action ended
	 * @param actionDescription
	 */
	protected void notifyActionEnded(String actionDescription) {
		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
		for (TrackListActionListener tal: tlalListenerList) {
			tal.actionEnds(evt);
		}
	}
}
