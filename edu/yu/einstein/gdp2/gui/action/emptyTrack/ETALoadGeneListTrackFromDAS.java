/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.dialog.DASDialog;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Loads a GeneList track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ETALoadGeneListTrackFromDAS extends TrackListActionWorker<GeneList> {
	
	private static final long serialVersionUID = 142539909587173492L; // generated ID
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
	public ETALoadGeneListTrackFromDAS(DataSource dataSource, DASConnector dasConnector, DASType dasType, int dataRange, 
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
	protected GeneList processAction() throws Exception {
		notifyActionStart("Loading From DAS Server", 1, true);
		if(dataRange == DASDialog.GENERATE_GENOMEWIDE_LIST)
			return dasConnector.getGeneList(dataSource, dasType);
		else if(dataRange == DASDialog.GENERATE_USER_SPECIFIED_LIST) {
			if(genomeWindow.getStop() < genomeWindow.getStart()) {
				throw new Exception("Invalid Start Stop Range");
			}
			return dasConnector.getGeneList(dataSource, dasType, genomeWindow);
		}
		else if(dataRange == DASDialog.GENERATE_CURRENT_LIST) {
			return dasConnector.getGeneList(dataSource, dasType, currentWindow);
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
			Track newTrack = new GeneListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), dasType.getID(), stripes);
		}								
	}
}
