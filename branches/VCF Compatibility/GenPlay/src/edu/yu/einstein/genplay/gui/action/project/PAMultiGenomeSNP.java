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

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.GenomeSynchronizer;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.SNPSynchroniser;
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

	private Chromosome			newChromosome;					// current chromosome (to apply changes)
	private List<String>		previousRequiredGenomes;		// the list of previous requested genomes (SNPs for these genomes can be either kept or released)
	private List<String>		newRequiredGenomes;				// the list of the new requested genomes (SNPs for these genomes can be either already in memory or have to be loaded)
	private boolean 			hasBeenPerformed = false;		// control whether the process has been performed or not (in order to execute the doAtTheEnd method only when the process went until the end)

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

		// Checks if the project is multi-genome and if SNPs have been requested
		if (projectManager.isMultiGenomeProject()) {

			GenomeSynchronizer genomeSynchronizer = projectManager.getMultiGenome();
			SNPSynchroniser snpSynchronizer = genomeSynchronizer.getSnpSynchroniser();

			boolean readyToCompute = false;
			boolean hasToRemoveSNP = false;

			if (newChromosome != null) {
				hasToRemoveSNP = snpSynchronizer.hasToRemoveSNPs(newChromosome);
				if (hasToRemoveSNP) {
					readyToCompute = true;
				}
			} else if (newRequiredGenomes != null) {
				readyToCompute = snpSynchronizer.updateCounters(previousRequiredGenomes, newRequiredGenomes);
			}

			if (readyToCompute) {
				// Notifies the action
				notifyActionStart(ACTION_NAME, 1, false);

				if (hasToRemoveSNP) {
					snpSynchronizer.removeChromosomeSNPs(newChromosome);
				}

				snpSynchronizer.compute();
				genomeSynchronizer.SNPhaveBeenComputed();
				hasBeenPerformed = true;
			}
		}

		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		if (ProjectManager.getInstance().isMultiGenomeProject() && hasBeenPerformed) {
			hasBeenPerformed = false;
			ProjectManager.getInstance().getMultiGenome().getGenomesInformation().resetListIndexes();
			refreshTracks();
		}
	}


	/**
	 * @param newChromosome the newChromosome to set
	 */
	public void setNewChromosome(Chromosome newChromosome) {
		this.newChromosome = newChromosome;
	}


	/**
	 * @param requiredGenomes the previous list of required genomes
	 */
	public void setPreviousSetting(List<String> requiredGenomes) {
		this.previousRequiredGenomes = requiredGenomes;
	}


	/**
	 * @param requiredGenomes the new list of required genomes
	 */
	public void setNewSetting(List<String> requiredGenomes) {
		this.newRequiredGenomes = requiredGenomes;
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