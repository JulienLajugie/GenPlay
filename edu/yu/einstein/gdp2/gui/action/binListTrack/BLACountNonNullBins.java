/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOCountNonNullBins;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Returns the number of non-null bins.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACountNonNullBins extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = 384900915791989265L;	// generated ID
	private static final String 	ACTION_NAME = "Bin Count";			// action name
	private static final String 	DESCRIPTION = 
		"Return the number of non-null bins on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACountNonNullBins";


	/**
	 * Creates an instance of {@link BLACountNonNullBins}
	 */
	public BLACountNonNullBins() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Long> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				BinList binList = selectedTrack.getData();
				Operation<Long> operation = new BLOCountNonNullBins(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Number of non-null bins: \n" + new DecimalFormat("###,###,###,###").format(actionResult), "Number of Bins", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
