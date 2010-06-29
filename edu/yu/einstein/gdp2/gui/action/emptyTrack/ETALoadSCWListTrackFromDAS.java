/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.Color;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.dialog.DASDialog;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Loads a SCWList track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ETALoadSCWListTrackFromDAS extends TrackListActionWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 8520156015849830140L; // generated ID
	private final DataSource 	dataSource;			// DAS data source
	private final DASConnector 	dasConnector;		// DAS connector
	private final DASType 		dasType;			// DAS type
	private final int 			dataRange;			// enum representing the type of range (genome wide / current range / user defined) 
	private final GenomeWindow 	genomeWindow;		// genome window defined by the user
	private final GenomeWindow 	currentWindow;		// current genome window 
	private final int 			selectedTrackIndex;	// index of the selected track


	/**
	 * Creates an instance of {@link ETALoadGeneListTrackFromDAS}
	 * @param dataSource DAS data source
	 * @param dasConnector DAS connector
	 * @param dasType DAS type
	 * @param dataRange enum representing the type of range (genome wide / current range / user defined)
	 * @param genomeWindow genome window defined by the user
	 * @param currentWindow current genome window
	 * @param selectedTrackIndex index of the selected track
	 */
	public ETALoadSCWListTrackFromDAS(DataSource dataSource, DASConnector dasConnector, DASType dasType, int dataRange, 
			GenomeWindow genomeWindow, GenomeWindow currentWindow, int selectedTrackIndex) {
		this.dataSource = dataSource;
		this.dasConnector = dasConnector;
		this.dasType = dasType;
		this.dataRange	= 	dataRange;
		this.genomeWindow = genomeWindow;
		this.currentWindow = currentWindow;
		this.selectedTrackIndex = selectedTrackIndex;
	}


	@Override
	protected ScoredChromosomeWindowList processAction() throws Exception {
		notifyActionStart("Loading From DAS Server", 1, false);
		if(dataRange == DASDialog.GENERATE_GENOMEWIDE_LIST) {
			return dasConnector.getSCWList(dataSource, dasType);
		}
		else if(dataRange == DASDialog.GENERATE_USER_SPECIFIED_LIST) {
			if(genomeWindow.getStop() < genomeWindow.getStart()) {
				throw new Exception("Invalid Start Stop Range");
			}
			return dasConnector.getSCWList(dataSource, dasType, genomeWindow);
		}
		else if(dataRange == DASDialog.GENERATE_CURRENT_LIST) {
			return dasConnector.getSCWList(dataSource, dasType, currentWindow);
		}
		return null;									
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
			SCWListTrack newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			newTrack.getHistory().add("Load From DAS Server", Color.GRAY);
			getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), dasType.getID(), stripes);
		}							
	}
}
