/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Returns the number of non-null bins.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinCountAction extends TrackListAction {

	private static final long serialVersionUID = 384900915791989265L;	// generated ID
	private static final String 	ACTION_NAME = "Bin Count";			// action name
	private static final String 	DESCRIPTION = 
		"Return the number of non-null bins on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BinListBinCount";


	/**
	 * Creates an instance of {@link BinCountAction}
	 * @param trackList a {@link TrackList}
	 */
	public BinCountAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Returns the number of non-null bins
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), trackList.getChromosomeManager());
			if (selectedChromo != null) {
				final BinList binList = selectedTrack.getBinList();
				// thread for the action
				new ActionWorker<Long>(trackList, "Counting Bins") {
					@Override
					protected Long doAction() {
						return BinListOperations.binCount(binList, selectedChromo);
					}
					@Override
					protected void doAtTheEnd(Long actionResult) {
						if (actionResult != null) {
							JOptionPane.showMessageDialog(getRootPane(), "Number of non-null bins: \n" + new DecimalFormat("###,###,###,###").format(actionResult), "Number of Bins", JOptionPane.INFORMATION_MESSAGE);
						}
					}							
				}.execute();
			}
		}	
	}		
}
