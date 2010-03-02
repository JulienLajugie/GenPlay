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
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Indexes the selected {@link BinListTrack} by chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public final class IndexationByChromosomeAction extends TrackListAction {

	private static final long serialVersionUID = -2043891820249510406L; 		// generated ID
	private static final String 	ACTION_NAME = "Indexation by Chromosome";	// action name
	private static final String 	DESCRIPTION = 
		"Index separately each chromosome of the selected track";				// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "indexByChromo";


	/**
	 * Creates an instance of {@link IndexationByChromosomeAction}
	 * @param trackList a {@link TrackList}
	 */
	public IndexationByChromosomeAction(TrackList trackList) {
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
			final Number saturation = NumberOptionPane.getValue(getRootPane(), "Saturation:", "Enter a value for the saturation:", new DecimalFormat("###.###%"), 0, 1, 0.01);
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
								return BinListOperations.indexByChromo(binList, saturation.doubleValue(), indexMin.doubleValue(), indexMax.doubleValue(), binList.getPrecision());
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
