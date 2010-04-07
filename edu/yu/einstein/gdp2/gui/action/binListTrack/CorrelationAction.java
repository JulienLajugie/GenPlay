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
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;


/**
 * Computes the coefficient of correlation between the selected
 * {@link BinListTrack} and another {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class CorrelationAction extends TrackListAction {

	private static final long serialVersionUID = -3513622153829181945L; // generated ID
	private static final String 	ACTION_NAME = "Correlation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the coefficient of correlation between " +
		"the selected track and another track";							// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "correlation";


	/**
	 * Creates an instance of {@link CorrelationAction}
	 * @param trackList a {@link TrackList}
	 */
	public CorrelationAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Computes the coefficient of correlation between the selected
	 * {@link BinListTrack} and another {@link BinListTrack}.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinListTrack otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the correlation with:", trackList.getBinListTracks());
			if (otherTrack != null) {
				final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), trackList.getChromosomeManager());
				if (selectedChromo != null) {
					final BinList binList1 = selectedTrack.getBinList();
					final BinList binList2 = otherTrack.getBinList();
					// thread for the action
					new ActionWorker<Double>(trackList, "Computing Correlation") {
						@Override
						protected Double doAction() {
							try {
								return BinListOperations.correlation(binList1, binList2, selectedChromo);
							} catch (BinListDifferentWindowSizeException e) {
								ExceptionManager.handleException(getRootPane(), e, "Calculating the correlation between tracks with different window sizes is not allowed");
								return null;
							} catch (Exception e) {
								ExceptionManager.handleException(getRootPane(), e, "Error while calculating the correlation");
								return null;
							}
						}
						@Override
						protected void doAtTheEnd(Double actionResult) {
							if (actionResult != null) {
								JOptionPane.showMessageDialog(getRootPane(), "Correlation coefficient: \n" + new DecimalFormat("0.000").format(actionResult), "Correlation", JOptionPane.INFORMATION_MESSAGE);
							}
						}							
					}.execute();
				}
			}	
		}		
	}
}