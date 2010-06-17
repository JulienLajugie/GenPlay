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
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.DASDialog;


/**
 * Loads a track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ETALoadFromDAS extends TrackListAction {

	private static final long serialVersionUID = -4045220235804063954L;	// generated ID
	private static final String ACTION_NAME = "Load from DAS Server"; 	// action name
	private static final String DESCRIPTION = "Load a track from " +
			"data retrieve from a DAS server"; 							// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadFromDAS";


	/**
	 * Creates an instance of {@link ETALoadFromDAS}
	 */
	public ETALoadFromDAS() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			final int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
			if (selectedTrackIndex != -1) {
				DASDialog dasDialog = new DASDialog();
				int res = dasDialog.showDASDialog(getRootPane());
				if (res == DASDialog.APPROVE_OPTION) {
					DataSource dataSource = dasDialog.getSelectedDataSource();
					DASConnector dasConnector = dasDialog.getSelectedDasConnector();
					DASType dasType = dasDialog.getSelectedDasType();
					int resType = dasDialog.getGenerateType();
					int dataRange = dasDialog.getDataRange();
					GenomeWindow genomeWindow = new GenomeWindow();
					GenomeWindow currentWindow = getTrackList().getGenomeWindow();
					genomeWindow.setChromosome(dasDialog.getSelectedChromosome());
					genomeWindow.setStart((int)dasDialog.getUserSpecifiedStart());
					genomeWindow.setStop((int)dasDialog.getUserSpecifiedStop());
					if (resType == DASDialog.GENERATE_GENE_LIST) {
						// case where the result type is a GeneList
						new ETALoadGeneListTrackFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrackIndex).actionPerformed(arg0);
					} else if (resType == DASDialog.GENERATE_SCW_LIST) {
						// case where the result type is a SCWList 
						new ETALoadSCWListTrackFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrackIndex).actionPerformed(arg0);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error While Loading the Server List");
		}
	}
}
