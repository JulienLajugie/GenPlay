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
package edu.yu.einstein.genplay.core.list.geneList;

import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;


/**
 * A list of {@link Gene} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListTmp extends ChromosomeArrayListOfLists<Gene> implements Serializable, ChromosomeListOfLists<Gene>, DisplayableDataList<List<Gene>> {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1567605708127718216L;


	/**
	 * The name of the genes are printed if the horizontal ratio is above this value
	 */
	public  static final double	MIN_X_RATIO_PRINT_NAME = 0.0005d;


	@Override
	public List<Gene> getFittedData(GenomeWindow genomeWindow, double xRatio) {
		// TODO Auto-generated method stub
		return null;
	}

}
