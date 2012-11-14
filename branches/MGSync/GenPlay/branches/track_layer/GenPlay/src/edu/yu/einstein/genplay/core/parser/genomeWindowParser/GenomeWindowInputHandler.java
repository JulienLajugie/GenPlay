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
package edu.yu.einstein.genplay.core.parser.genomeWindowParser;

import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;

/**
 * The {@link GenomeWindowInputHandler} handles the parsing of a text input.
 * It uses the {@link PositionParser} to parse the text and provides advanced methods to retrieve information.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeWindowInputHandler {

	private final PositionParser parser;	// The string parser.


	/**
	 * Constructor of {@link GenomeWindowInputHandler}
	 * @param s the string to parse
	 */
	public GenomeWindowInputHandler (String s) {
		parser = new PositionParser();
		parser.parse(s);
	}


	/**
	 * @return the genome window, null if wrong input
	 */
	public GenomeWindow getGenomeWindow () {
		GenomeWindow genomeWindow = null;			// The genome window to return
		Integer start = getStart();					// The start position.
		Integer stop = getStop();					// The stop position.
		Chromosome chromosome = getChromosome();	// The chromosome.

		if ((start != null) && (stop != null)) {								// If both position have been given.
			genomeWindow = new GenomeWindow(chromosome, start, stop);			// The genome window is easily created.
		} else if ((start != null) && (stop == null)) {							// If only one position has been given (and it can only be the start position in this parser).
			GenomeWindow currentGenomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();	// Get the current genome window.
			int width = currentGenomeWindow.getSize() / 2;						// Get half of the window size.
			int newStart = start - width;										// The new start will be half size less than the given position.
			int newStop = start + width;										// The new stop will be half size more than the given position.
			genomeWindow = new GenomeWindow(chromosome, newStart, newStop);		// The new genome window can be created
		}

		return genomeWindow;		// Returns the new result.
	}


	/**
	 * The start position is considered as the smallest value found in the input.
	 * @return the start position, null if not found
	 */
	public Integer getStart () {
		if (parser.getNumberElements().size() > 0) {
			return parser.getNumberElements().get(0);
		}
		return null;
	}


	/**
	 * The stop position is considered as the highest value found in the input.
	 * @return the stop position, null if not found
	 */
	public Integer getStop () {
		if (parser.getNumberElements().size() > 1) {
			return parser.getNumberElements().get(parser.getNumberElements().size() - 1);
		}
		return null;
	}


	/**
	 * Looks for the right chromosome, every text elements from the input will be faced to the chromosomes project list.
	 * If no chromosome is found, the current chromosome is returned.
	 * @return	the given chromosome, the current chromosome if not found.
	 */
	public Chromosome getChromosome () {
		Chromosome chromosome = null;
		List<String> eventualChromosome = parser.getTextElements();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();

		boolean endOfLists = false;
		while (!endOfLists && (chromosome == null)) {
			for (String currentEventualChromosome: eventualChromosome) {
				for (Chromosome currentChromosome: projectChromosome) {
					if (currentChromosome.getName().equals(currentEventualChromosome)) {
						chromosome = currentChromosome;
					}
				}
			}
			endOfLists = true;
		}

		if (chromosome == null) {
			chromosome = projectChromosome.getCurrentChromosome();
		}

		return chromosome;
	}

}
