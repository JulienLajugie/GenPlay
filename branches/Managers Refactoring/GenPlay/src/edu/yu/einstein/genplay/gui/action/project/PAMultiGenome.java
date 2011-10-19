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
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.GenomeSynchronizer;
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
		"Performs the multi genome algorithm"; 									// tooltip
	private static final int 			MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 		ACTION_NAME = "Multi-genome loading";	// action name
	private GenomeSynchronizer 			genomeSynchroniser;
	private Map<File, VCFReader> 		fileReaders;							// Mapping between files and their readers.
	private Map<String, List<File>> 	genomeFileAssociation;					// Mapping between genome names and their files.
	private	List<Chromosome> 			chromosomeList;							// List of chromosome


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
		genomeSynchroniser = ProjectManager.getInstance().getGenomeSynchronizer();
		chromosomeList = ProjectManager.getInstance().getCurrentMultiGenomeChromosomeList();
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
				
				// Computes the synchronization
				genomeSynchroniser.compute(projectManager.getAssembly().getDisplayName(), chromosomeList);
				
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
		ProjectManager.getInstance().updateChromosomeList();
		
		MainFrame.getInstance().getControlPanel().reinitChromosomePanel();
	}	
	
	
	/**
	 * Checks if file readers and genome file association parameters are null,
	 * if they are not, it sets the genome synchronizer using them.
	 * Then, it checks if genome synchronizer has been initialized.
	 * @return true if genome synchronizer has been initialized, false if not.
	 */
	private boolean hasBeenInitialized () {
		boolean valid = false;
		
		// If parameters have been given,
		// sets the genome synchronizer.
		if (fileReaders != null &&
				genomeFileAssociation != null) {
			genomeSynchroniser.setFileReaders(fileReaders);
			genomeSynchroniser.setGenomeFileAssociation(genomeFileAssociation);
		}
		
		// Checks if genome synchronizer has been initialized
		if (genomeSynchroniser.getFileReaders() != null &&
				genomeSynchroniser.getGenomeFileAssociation() != null) {
			valid = true;
		}
		
		return valid;
	}
	
	
	/**
	 * This method must be used when multi-genome synchronization is performed for the first time in a project.
	 * @param fileReaders the fileReaders to set
	 */
	public void setFileReaders(Map<File, VCFReader> fileReaders) {
		this.fileReaders = fileReaders;
	}


	/**
	 * This method must be used when multi-genome synchronization is performed for the first time in a project.
	 * @param genomeFileAssociation the genomeFileAssociation to set
	 */
	public void setGenomeFileAssociation(
			Map<String, List<File>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
	}


	/**
	 * The action will be performed on that list of chromosomes.
	 * By default, that chromosome list is set according to the low memory mode option (full list or current chromosome only).
	 * @param chromosomeList the chromosomeList to set
	 */
	public void setChromosomeList(List<Chromosome> chromosomeList) {
		this.chromosomeList = chromosomeList;
	}
	
}