/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLORepartition;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Generates a file showing the repartition of the score values of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ShowRepartitionAction extends TrackListAction {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a csv file showing the repartition of the scores of the selected track";	// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "showRepartition";


	/**
	 * Creates an instance of {@link ShowRepartitionAction}
	 * @param trackList a {@link TrackList}
	 */
	public ShowRepartitionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Generates a file showing the repartition of the score values of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0"), 0, 1000, 1);
			if (scoreBin != null) {
				final JFileChooser saveFC = new JFileChooser(trackList.getConfigurationManager().getDefaultDirectory());
				saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
				saveFC.setDialogTitle("Bin repartition " + selectedTrack.getName());
				saveFC.setSelectedFile(new File(".csv"));
				int returnVal = saveFC.showSaveDialog(getRootPane());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (!Utils.cancelBecauseFileExist(getRootPane(), saveFC.getSelectedFile())) {
						final BinList binList = ((BinListTrack)selectedTrack).getBinList();
						final BinListOperation<Void> operation = new BLORepartition(binList, scoreBin.doubleValue(), saveFC.getSelectedFile());
						// thread for the action
						new ActionWorker<Void>(trackList, "Calculating Repartition") {
							@Override
							protected Void doAction() throws Exception {
								return operation.compute();
							}
							@Override
							protected void doAtTheEnd(Void actionResult) {}							
						}.execute();	
					}
				}
			}
		}		
	}
}