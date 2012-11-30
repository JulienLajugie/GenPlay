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
package edu.yu.einstein.genplay.gui.track.layer;

/**
 * Enumeration of all the different track layers available in GenPlay
 * @author Julien Lajugie
 */
public enum LayerType {

	/**
	 * Layer displayed on the foreground of a track
	 */
	FOREGROUND_LAYER ("Foreground Layer"),

	/**
	 * Layer displayed on the background of a track
	 */
	BACKGROUND_LAYER ("Background Layer"),

	/**
	 * BinList layer
	 */
	BIN_LAYER ("Fixed Window Layer"),

	/**
	 * GeneList layer
	 */
	GENE_LAYER ("Gene Layer"),

	/**
	 * NucleotideList layer
	 */
	NUCLEOTIDE_LAYER ("Sequence Layer"),

	/**
	 * RepeatFamilyList layer
	 */
	REPEAT_FAMILY_LAYER ("Repeat Family Layer"),

	/**
	 * ScoredChromosomeWindowList layer
	 */
	SCW_LAYER ("Variable Window Layer");


	private final String name; // name


	/**
	 * Creates an instance of {@link LayerType}
	 * @param name name of the layer type
	 */
	private LayerType(String name) {
		this.name = name;
	}


	/**
	 * @return the name of the layer type
	 */
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return getName();
	}
}
