/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Adds a constant to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class AddConstantAction extends TrackListAction {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Add Constant";		// action name
	private static final String 	DESCRIPTION = 
		"Add a constant to the scores of the selected track";			// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "addConstant";


	/**
	 * Creates an instance of {@link AddConstantAction}
	 * @param trackList a {@link TrackList}
	 */
	public AddConstantAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Adds a constant to the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to add: f(x)=x + C", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				final BinList binList = ((BinListTrack)selectedTrack).getBinList();
				final DataPrecision precision = Utils.choosePrecision(getRootPane());
				if (precision != null) {
					final String description;
					if (precision != binList.getPrecision()) {
						description = "add constant C = " + constant + ", precision changed: New Precision = " + precision;
					} else {
						description = "add constant C = " + constant;
					}
					// thread for the action
					new ActionWorker<BinList>(trackList) {
						@Override
						protected BinList doAction() {
							return BinListOperations.addConstant(binList, constant.doubleValue(), precision);
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							selectedTrack.setBinList(actionResult, description);
						}
					}.execute();
				}
			}
		}
	}
}