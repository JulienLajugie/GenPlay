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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSNPSynchronizer;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * This class performs the multi-genome synchronization algorithm for SNPs.
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PAMultiGenomeSNP extends TrackListActionWorker<Track<?>[]> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION = 
		"Performs the multi genome algorithm for SNPs"; 					// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 		// mnemonic key
	private static		 String 			ACTION_NAME = "SNPs loading";	// action name

	private Map<String, List<AlleleType>> genomeNames;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome for SNP";


	/**
	 * Creates an instance of {@link PAMultiGenomeSNP}.
	 */
	public PAMultiGenomeSNP() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		genomeNames = null;
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();

		// Checks if the project is multi-genome and if SNPs have been requested
		if (projectManager.isMultiGenomeProject()) {
			
			MGSNPSynchronizer snpSynchronizer = projectManager.getMultiGenome().getMultiGenomeSynchronizerForSNP();

			// Notifies the action
			notifyActionStart(ACTION_NAME, 1, false);

			snpSynchronizer.compute(genomeNames);
		}

		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		MGDisplaySettings settings = MGDisplaySettings.getInstance();
		Track<?>[] tracks = getTrackList().getTrackList();
		for (Track<?> track: tracks) {
			List<StripesData> stripesList = settings.getStripeSettings().getStripesForTrack(track);
			boolean hasToReset = false;
			for (StripesData data: stripesList) {
				if (data.getVariationTypeList().contains(VariantType.SNPS)) {
					hasToReset = true;
				}
			}
			if (hasToReset) {
				track.resetVariantListMaker();
			}
		}
	}


	/**
	 * @param genomeNames the genomeNames to set
	 */
	protected void setGenomeNames(Map<String, List<AlleleType>> genomeNames) {
		this.genomeNames = genomeNames;
	}

}