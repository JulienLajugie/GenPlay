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

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.GenomeSynchronizer;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.SNPSynchroniser;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripes;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
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
	
	private Chromosome						newChromosome;
	private MultiGenomeStripes				previousSetting;
	private MultiGenomeStripes				newSetting;
	

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
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		
		ProjectManager projectManager = ProjectManager.getInstance();
		GenomeSynchronizer genomeSynchronizer = projectManager.getGenomeSynchronizer();
		SNPSynchroniser snpSynchronizer = genomeSynchronizer.getSnpSynchroniser();
		
		// Checks if the project is multi-genome and if SNPs have been requested
		if (projectManager.isMultiGenomeProject()) {
			
			// Notifies the action
			notifyActionStart(ACTION_NAME, 1, false);
			
			boolean readyToCompute = false;
			
			if (newChromosome != null) {
				readyToCompute = true;
				snpSynchronizer.removeChromosomeSNPs(newChromosome);
			} else if (newSetting != null) {
				readyToCompute = true;
				snpSynchronizer.updateCounters(previousSetting, newSetting);
			}
			
			if (readyToCompute) {
				snpSynchronizer.compute();
				genomeSynchronizer.SNPhaveBeenComputed();
			} else {
				System.err.println("SNPs synchronization cannot be performed because parameters have not been set.");
			}
			
		} else {
			// Generates error if the project is not multi-genome
			System.err.println("SNPs synchronization cannot be performed because the project does not seem to be multi-genome.");
		}
		
		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		refreshTracks();
	}


	/**
	 * @param newChromosome the newChromosome to set
	 */
	public void setNewChromosome(Chromosome newChromosome) {
		this.newChromosome = newChromosome;
	}


	/**
	 * @param previousSetting the previousSetting to set
	 */
	public void setPreviousSetting(MultiGenomeStripes previousSetting) {
		this.previousSetting = previousSetting;
	}


	/**
	 * @param newSetting the newSetting to set
	 */
	public void setNewSetting(MultiGenomeStripes newSetting) {
		this.newSetting = newSetting;
	}	

	
	/**
	 * Initializes attributes used for multi genome project.
	 */
	private void refreshTracks () {
		Track<?>[] tracks = MainFrame.getInstance().getTrackList().getTrackList();
		for (Track<?> track: tracks) {
			track.refreshDisplayableVariantList();
		}
	}
	
}