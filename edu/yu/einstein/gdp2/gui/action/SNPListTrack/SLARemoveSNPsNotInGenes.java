/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.gui.action.SNPListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.SNPList.operation.SLORemoveSNPsNotInGenes;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLARemoveSNPsNotInGenes extends TrackListActionOperationWorker<SNPList> {

	private static final long serialVersionUID = -2654849686971854092L;			// generated ID
	private static final String 	ACTION_NAME = "Remove SNPs Not In Genes";	// action name
	private static final String 	DESCRIPTION = 
		"Removes the SNPs that are not in the genes of a selected track";		// tooltip
	private Track<?> 				selectedTrack;								// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SLARemoveSNPsNotInGenes";


	/**
	 * Creates an instance of {@link SLARemoveSNPsNotInGenes}
	 */
	public SLARemoveSNPsNotInGenes() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<SNPList> initializeOperation() throws Exception {
		if (getTrackList().getGeneListTracks() == null) {
			return null;
		}
		selectedTrack = getTrackList().getSelectedTrack();
		if ((selectedTrack != null) && (selectedTrack.getData() instanceof SNPList)) {
			SNPList snpList = (SNPList) selectedTrack.getData();
			GeneListTrack geneTrack = (GeneListTrack) TrackChooser.getTracks(getRootPane(), "Gene Track", "Select A Gene Track", getTrackList().getGeneListTracks());
			if (geneTrack != null) {
				String[] options = {"the SNPs that are not in the genes", "the SNPs that are not in the exons of the genes"};
				String selectedOption = (String) JOptionPane.showInputDialog(getRootPane(), "Remove", "Remove", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (selectedOption != null) {
					int removalType = 0;
					if (selectedOption == options[0]) {
						removalType = SLORemoveSNPsNotInGenes.REMOVE_SNPs_NOT_IN_GENES;
					} else {
						removalType = SLORemoveSNPsNotInGenes.REMOVE_SNPs_NOT_IN_EXONS;
					}
					Operation<SNPList> operation = new SLORemoveSNPsNotInGenes(snpList, geneTrack.getData(), removalType);
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(SNPList actionResult) {
		if (actionResult != null) {
			int index = selectedTrack.getTrackNumber() - 1;
			Track<?> newTrack = new SNPListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " filtered", selectedTrack.getStripes());
		}		
	}
}
