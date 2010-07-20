/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.geneListTrack;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.GLOGeneRenamer;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.GeneListTrack;

/**
 * Class to Rename Genes 
 * @author Chirag Gorasia
 * @version 0.1
 */

public class GLAGeneRenamer extends TrackListActionOperationWorker<GeneList>{

	private static final long serialVersionUID = -2210215854202609520L;
	private static final String 	ACTION_NAME = "Rename Genes"; // action name
	private static final String 	DESCRIPTION = "Rename Genes";
	private GeneListTrack selectedTrack;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAGeneRenamer";
	
	/**
	 * Creates an instance of {@link GLAGeneRenamer}
	 */
	public GLAGeneRenamer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<GeneList> initializeOperation() throws Exception {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			GeneList geneList = selectedTrack.getData();
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = jfc.showOpenDialog(getRootPane());
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File fileName = jfc.getSelectedFile();
				Operation<GeneList> operation = new GLOGeneRenamer(geneList, fileName); 
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			int selectedIndex = getTrackList().getSelectedTrackIndex();
			GeneListTrack glt = new GeneListTrack(getTrackList().getGenomeWindow(), selectedIndex + 1, actionResult);
			getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getStripes());
		}
	}
}
