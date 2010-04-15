/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

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
 * Shows the maximum score of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MaximumAction extends TrackListAction {

	private static final long serialVersionUID = -3864460354387970028L;	// generated ID
	private static final String 	ACTION_NAME = "Maximum";			// action name
	private static final String 	DESCRIPTION = 
		"Show the maximum score of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "maximum";


	/**
	 * Creates an instance of {@link MaximumAction}
	 * @param trackList a {@link TrackList}
	 */
	public MaximumAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Shows the maximum score of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), trackList.getChromosomeManager());
			if (selectedChromo != null) {
				final BinList binList = selectedTrack.getBinList();
				// thread for the action
				new ActionWorker<Double>(trackList, "Searching Maximum") {
					@Override
					protected Double doAction() {
						return BinListOperations.max(binList, selectedChromo);
					}
					protected void doAtTheEnd(Double actionResult) {
						JOptionPane.showMessageDialog(getRootPane(), actionResult, "Maximum of \"" + selectedTrack.getName() +"\":", JOptionPane.INFORMATION_MESSAGE);
					};
				}.execute();
			}		
		}
	}
}