/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMin;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Shows the minimum score of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAMin extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = 3523404731226850786L;	// generated ID
	private static final String 	ACTION_NAME = "Minimum";			// action name
	private static final String 	DESCRIPTION = 
		"Show the minimum score of the selected track";					// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAMin";


	/**
	 * Creates an instance of {@link BLAMin}
	 */
	public BLAMin() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				BinList binList = selectedTrack.getData();
				Operation<Double> operation = new BLOMin(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), actionResult, "Minimum of \"" + selectedTrack.getName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}