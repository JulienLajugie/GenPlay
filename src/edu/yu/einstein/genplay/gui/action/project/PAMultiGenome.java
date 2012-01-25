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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.project.MultiGenome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * This class performs the multi-genome synchronization algorithm.
 * There is two ways to perform the synchronization:
 * - At the creation of the project when the synchronization is initiated for the first time (must provide parameters).
 * - To update the synchronization in case of low memory mode (every time user changes current chromosome) (DO NOT provide parameters).
 * 
 * In order to initiate the synchronization for the first time, please set parameters:
 * - fileReaders
 * - genomeFileAssociation
 * 
 * In a project, before using the {@link PAMultiGenome} action for the first time,
 * please set the multi-genome project boolean presents in {@link ProjectManager} to true.
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PAMultiGenome extends TrackListActionWorker<Track<?>[]> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Performs the multi genome algorithm"; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Multi-genome loading";	// action name
	private MultiGenome 					multiGenome;							// instance of the multi genome
	private Map<String, List<VCFReader>> 	genomeFileAssociation;					// Mapping between genome names and their readers.

	
	private List<Long> times = new ArrayList<Long>();
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome";


	/**
	 * Creates an instance of {@link PAMultiGenome}.
	 */
	public PAMultiGenome() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		multiGenome = ProjectManager.getInstance().getMultiGenome();
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();
		
		// Checks if the project is multi-genome
		if (projectManager.isMultiGenomeProject()) {
			
			// Checks if parameters have been set
			if (hasBeenInitialized()) {
				
				// Notifies the action
				notifyActionStart(ACTION_NAME, 1, false);
				
				// Initializes the genome synchronization
				times.add(System.currentTimeMillis());
				multiGenome.initializeSynchronization (genomeFileAssociation);
				
				// Insert the variation into the data structure
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenomeSynchronizer().insertVariantposition();
				
				// Sort lists of position for every chromosome of every genome
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenome().sort();
				
				//genomeSynchronizer.getMultiGenome().show();
				
				// Remove the duplicate from the reference genome lists of position
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenome().getReferenceGenome().removeDuplicate();
				
				// Creates the variation for the reference genome.
				// This is the only place where this method must be called!! After removing duplicates and before position synchronization.
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenomeForDisplay().getReferenceGenome().getAllele().initialize();
				
				// Performs the synchronization in order to get all genome positions and their offset with the meta genome
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenomeSynchronizer().performPositionSynchronization();
				
				// Compacts the offset lists in order to optimize the memory usage
				times.add(System.currentTimeMillis());
				multiGenome.getMultiGenome().compactLists();
				
				// End
				times.add(System.currentTimeMillis());
			} else {
				// Generates error when parameters have not been set
				System.err.println("Multi-genome synchronization cannot be performed because the file readers and/or the genome file association parameters have not been set.");
			}
			
		} else {
			// Generates error if the project is not multi-genome
			System.err.println("Multi-genome synchronization cannot be performed because the project does not seem to be multi-genome.");
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
	
		//multiGenome.show();
		
		initializesTrackListForMultiGenomeProject();
		
		/*for (int i = 1; i < times.size(); i++) {
			System.out.println("[" + i + "]: " + (times.get(i) - times.get(i - 1)));
		}
		System.out.println("[total]: " + (times.get(times.size() - 1) - times.get(0)));*/
	}
	
	
	/**
	 * Checks if file readers and genome file association parameters are null,
	 * if they are not, it sets the genome synchronizer using them.
	 * Then, it checks if genome synchronizer has been initialized.
	 * @return true if genome synchronizer has been initialized, false if not.
	 */
	private boolean hasBeenInitialized () {
		boolean valid = false;
		
		// If parameter have been given,
		// sets the genome synchronizer.
		if (genomeFileAssociation != null) {
			multiGenome.setGenomeFileAssociation(genomeFileAssociation);
		}
		
		// Checks if genome synchronizer has been initialized
		if (multiGenome.getGenomeFileAssociation() != null) {
			valid = true;
		}
		
		return valid;
	}


	/**
	 * This method must be used when multi-genome synchronization is performed for the first time in a project.
	 * @param genomeFileAssociation the genomeFileAssociation to set
	 */
	public void setGenomeFileAssociation(
			Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
	}
	
	
	/**
	 * Initializes attributes used for multi genome project.
	 */
	private void initializesTrackListForMultiGenomeProject () {
		Track<?>[] tracks = MainFrame.getInstance().getTrackList().getTrackList();
		for (Track<?> track: tracks) {
			if (track.getStripesList() == null) {
				track.multiGenomeInitializing();
			}
		}
	}
	
}