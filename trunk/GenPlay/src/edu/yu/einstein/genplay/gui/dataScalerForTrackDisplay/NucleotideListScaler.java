/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Scales a {@link NucleotideList} to be displayed on a track.
 * @author Julien Lajugie
 */
class NucleotideListScaler implements DataScalerForTrackDisplay<NucleotideList, Nucleotide[]> {

	/** Chromosome window of the scaled nucleotide array */
	private ChromosomeWindow scaledWindow;

	/** Array of {@link Nucleotide} scaled for a chromosome window */
	private Nucleotide[] scaledNucleotides;

	/** Data to be scaled for track display */
	private final NucleotideList dataToScale;


	/**
	 * Creates an instance of {@link NucleotideListScaler}
	 * @param dataToScale data to be scaled for track display
	 */
	NucleotideListScaler(NucleotideList dataToScale) {
		this.dataToScale = dataToScale;
	}


	@Override
	public Nucleotide[] getDataScaledForTrackDisplay() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		// if the scaled array of nucleotide doesn't contain the data
		// for the genome window currently displayed we refresh it
		if (!projectWindow.getGenomeWindow().equals(scaledWindow)) {
			scaleCurrentWindow();
		}
		return scaledNucleotides;
	}


	@Override
	public NucleotideList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Populates the scaled array for the {@link GenomeWindow} currently displayed
	 */
	private void scaleCurrentWindow() {
		GenomeWindow displayedWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		int start = displayedWindow.getStart();
		int stop = displayedWindow.getStop();
		Chromosome chromosome = displayedWindow.getChromosome();
		scaledNucleotides = new Nucleotide[(stop - start) + 1];
		try {
			ListView<Nucleotide> currentList = dataToScale.get(chromosome);
			int j = 0;
			for (int i = start; i <= stop; i++) {
				scaledNucleotides[j] = currentList.get(i);
				j++;
			}
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			scaledNucleotides = null;
		}
	}
}
