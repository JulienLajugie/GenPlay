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
package edu.yu.einstein.genplay.core.writer;

import edu.yu.einstein.genplay.core.enums.AlleleType;


/**
 * Interface implemented by the data writers (BinListWriter, GeneListWriter...)
 * @author Julien Lajugie
 * @version 0.1
 */
public interface Writer {

	/**
	 * Writes data in an output file
	 * @throws Exception
	 */
	public void write() throws Exception;


	/**
	 * In Multi Genome projects, tracks can be saved according to three different categories of coordinate system:
	 * - the meta genome
	 * - the reference genome
	 * - a genome (among the ones from the current project)
	 * 
	 * The user can select which coordinate system he wants to use to save a track
	 * 
	 * @param genome a full genome name (meta genome, reference genome or one of the genome within the project)
	 * @param allele an allele, will not be considered if the genome is the meta genome or the reference genome (therefore null)
	 */
	public void setMultiGenomeCoordinateSystem (String genome, AlleleType allele);
}
