/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.DAS.DASDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;


/**
 * Loads a track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @version 0.1
 */
public class LoadFromDASAction extends TrackListAction {

	private static final long serialVersionUID = -4045220235804063954L;	// generated ID
	private static final String ACTION_NAME = "Load from DAS Server"; // action name
	private static final String DESCRIPTION = "Load a track from data retrieve from a DAS server"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "LoadFromDASAction";


	/**
	 * Creates an instance of {@link LoadFromDASAction}
	 * @param trackList a {@link TrackList}
	 */
	public LoadFromDASAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link BinListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final int selectedTrackIndex = trackList.getSelectedTrackIndex();
		if (selectedTrackIndex != -1) {
			try {
				DASDialog dasDialog = new DASDialog();
				int res = dasDialog.showDASDialog(getRootPane());
				if (res == DASDialog.APPROVE_OPTION) {
					final DataSource dataSource = dasDialog.getSelectedDataSource();
					final DASConnector dasConnector = dasDialog.getSelectedDasConnector();
					final DASType dasType = dasDialog.getSelectedDasType();
					final int resType = dasDialog.getGenerateType();
					final ChromosomeManager cm = trackList.getChromosomeManager();
					if (resType == DASDialog.GENERATE_GENE_LIST) {
						new ActionWorker<GeneList>(trackList) {
							@Override
							protected GeneList doAction() {
								try {
									return dasConnector.getGeneList(cm, dataSource, dasType);
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while retrieving the data from the DAS server");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(GeneList actionResult) {
								if (actionResult != null) {
									ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
									Track newTrack = new GeneListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
									trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), dasType.getID(), stripes);
								}								
							}
						}.execute();
					} else if (resType == DASDialog.GENERATE_SCW_LIST) {
						new ActionWorker<ScoredChromosomeWindowList>(trackList) {
							@Override
							protected ScoredChromosomeWindowList doAction() {
								try {
									return dasConnector.getSCWList(cm, dataSource, dasType);
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while retrieving the data from the DAS server");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
								if (actionResult != null) {
									ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
									Track newTrack = new SCWListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
									trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), dasType.getID(), stripes);
								}
							}					
						}.execute();
					}
				}
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "DAS Error");
			}			
		}
	}
}
