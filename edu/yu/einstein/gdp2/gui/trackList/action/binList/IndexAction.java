/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;


/**
 * Indexes the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class IndexAction extends TrackListAction {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Index";				// action name
	private static final String 	DESCRIPTION = 
		"Index the selected track";		 								// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "index";


	/**
	 * Creates an instance of {@link IndexAction}
	 * @param trackList a {@link TrackList}
	 */
	public IndexAction(TrackList trackList) {
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
			final Number saturation = NumberOptionPane.getValue(getRootPane(), "Saturation:", "Enter a value for the saturation:", new DecimalFormat("0.0#####"), 0, 100, 1);
			if(saturation != null) {
				final Number indexMin = NumberOptionPane.getValue(getRootPane(), "Minimum", "Enter minimum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
				if (indexMin != null) {
					final Number indexMax = NumberOptionPane.getValue(getRootPane(), "Maximum", "Enter the maximum indexed value:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
					if(indexMax != null) {
						final BinList binList = selectedTrack.getBinList();
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								return BinListOperations.index(binList, saturation.doubleValue(), indexMin.doubleValue(), indexMax.doubleValue(), binList.getPrecision());
							}
							@Override
							protected void doAtTheEnd(BinList actionResult) {
								String description = "index track between " +  indexMin + " and " + indexMax + " with a saturation of " + saturation;
								selectedTrack.setBinList(actionResult, description);								
							}
						}.execute();
					}
				}
			}
		}		
	}
}
