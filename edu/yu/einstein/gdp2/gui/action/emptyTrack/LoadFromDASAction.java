/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.DASDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


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
	 */
	public LoadFromDASAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link BinListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
		if (selectedTrackIndex != -1) {
			try {
				DASDialog dasDialog = new DASDialog();
				int res = dasDialog.showDASDialog(getRootPane());
				if (res == DASDialog.APPROVE_OPTION) {
					final DataSource dataSource = dasDialog.getSelectedDataSource();
					final DASConnector dasConnector = dasDialog.getSelectedDasConnector();
					final DASType dasType = dasDialog.getSelectedDasType();
					final int resType = dasDialog.getGenerateType();
					final int dataRange = dasDialog.getDataRange();
					final GenomeWindow genomeWindow = new GenomeWindow();
					final GenomeWindow currentWindow = getTrackList().getGenomeWindow();
					genomeWindow.setChromosome(dasDialog.getSelectedChromosome());
					genomeWindow.setStart((int)dasDialog.getUserSpecifiedStart());
					genomeWindow.setStop((int)dasDialog.getUserSpecifiedStop());
					//System.out.println("Chromosome: " + dasDialog.getSelectedChromosome() + "\nStart: " + dasDialog.getUserSpecifiedStart() + "\nStop: " + dasDialog.getUserSpecifiedStop());
					if (resType == DASDialog.GENERATE_GENE_LIST) {
						new ActionWorker<GeneList>(getTrackList(), "Retrieving Data from DAS Server") {
							@Override
							protected GeneList doAction() {
								try {
									if(dataRange == DASDialog.GENERATE_GENOMEWIDE_LIST)
										return dasConnector.getGeneList(dataSource, dasType);
									else if(dataRange == DASDialog.GENERATE_USER_SPECIFIED_LIST)
									{
										if(genomeWindow.getStop() < genomeWindow.getStart())
											throw new Exception("Invalid Start Stop Range");
										return dasConnector.getGeneList(dataSource, dasType, genomeWindow);
									}
									else if(dataRange == DASDialog.GENERATE_CURRENT_LIST)
									{
										return dasConnector.getGeneList(dataSource, dasType, currentWindow);
									}
									return null;
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while retrieving the data from the DAS server");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(GeneList actionResult) {
								if (actionResult != null) {
									ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
									Track newTrack = new GeneListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
									getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), dasType.getID(), stripes);
								}								
							}
						}.execute();
					} else if (resType == DASDialog.GENERATE_SCW_LIST) {
						new ActionWorker<ScoredChromosomeWindowList>(getTrackList(), "Retrieving Data from DAS server") {
							@Override
							protected ScoredChromosomeWindowList doAction() {
								try {
									if(dataRange == DASDialog.GENERATE_GENOMEWIDE_LIST)
										return dasConnector.getSCWList(dataSource, dasType);
									else if(dataRange == DASDialog.GENERATE_USER_SPECIFIED_LIST)
									{
										if(genomeWindow.getStop() < genomeWindow.getStart())
											throw new Exception("Invalid Start Stop Range");
										return dasConnector.getSCWList(dataSource, dasType, genomeWindow);
									}
									else if(dataRange == DASDialog.GENERATE_CURRENT_LIST)
									{
										return dasConnector.getSCWList(dataSource, dasType, currentWindow);
									}
									return null;									
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while retrieving the data from the DAS server");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
								if (actionResult != null) {
									ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
									Track newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
									getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), dasType.getID(), stripes);
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
