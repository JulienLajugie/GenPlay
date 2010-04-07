/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Changes the size of the bins of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChangeBinSizeAction extends TrackListAction {

	private static final long serialVersionUID = 4743270937529673599L;			// generated ID
	private static final String 	ACTION_NAME = "Change Bin Size";		// action name
	private static final String 	DESCRIPTION = 
		"Change the size of the bins of the selected track ";				// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ChangeBinSizeAction";


	/**
	 * Creates an instance of {@link ChangeBinSizeAction}
	 * @param trackList a {@link TrackList}
	 */
	public ChangeBinSizeAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Changes the size of the bins of a {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		final BinList binList = selectedTrack.getBinList();
		if (selectedTrack != null) {
			final Number binSize = NumberOptionPane.getValue(trackList.getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				final ScoreCalculationMethod method = Utils.chooseScoreCalculation(trackList.getRootPane());
				if (method != null) {
					// thread for the action
					new ActionWorker<BinList>(trackList, "Changing Window Size") {
						@Override
						protected BinList doAction() {
							return BinListOperations.changeBinSize(binList, binSize.intValue(), method);
						}
						@Override
						protected void doAtTheEnd(BinList resultList) {
							if (resultList != null) {
								selectedTrack.setBinList(resultList, "Bin Size Changed to " + binSize.intValue() + "bp, Method of Calculation = " + method);
							}
						}
					}.execute();
				}
			}
		}
	}
}
