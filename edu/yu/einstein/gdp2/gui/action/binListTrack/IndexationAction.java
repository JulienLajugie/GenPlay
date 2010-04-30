/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOIndex;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Indexes the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class IndexationAction extends TrackListAction {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Indexation";			// action name
	private static final String 	DESCRIPTION = 
		"Index the selected track";		 								// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "index";


	/**
	 * Creates an instance of {@link IndexationAction}
	 * @param trackList a {@link TrackList}
	 */
	public IndexationAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Indexes the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {			
			if (selectedTrack.getBinList().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, indexation is not available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			final Number indexMin = NumberOptionPane.getValue(getRootPane(), "Minimum", "New minimum score:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
			if (indexMin != null) {
				final Number indexMax = NumberOptionPane.getValue(getRootPane(), "Maximum", "New maximum score:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
				if(indexMax != null) {
					final BinList binList = selectedTrack.getBinList();
					final BinListOperation<BinList> operation = new BLOIndex(binList, indexMin.doubleValue(), indexMax.doubleValue());
					// thread for the action
					new ActionWorker<BinList>(trackList, "Indexing") {
						@Override
						protected BinList doAction() throws Exception {
							return operation.compute();
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							selectedTrack.setBinList(actionResult, operation.getDescription());								
						}
					}.execute();
				}
			}
		}		
	}
}
