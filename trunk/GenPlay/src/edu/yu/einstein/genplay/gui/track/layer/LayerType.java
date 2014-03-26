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
package edu.yu.einstein.genplay.gui.track.layer;

/**
 * Enumeration of all the different track layers available in GenPlay
 * @author Julien Lajugie
 */
public enum LayerType {

	/**
	 * Layer displayed on the background of a track
	 */
	BACKGROUND_LAYER ("Background Layer"),

	/**
	 * BinList layer
	 */
	BIN_LAYER ("Fixed Window Layer"),

	/**
	 * Layer displayed on the foreground of a track
	 */
	FOREGROUND_LAYER ("Foreground Layer"),

	/**
	 * GeneList layer
	 */
	GENE_LAYER ("Gene Annotation Layer"),

	/**
	 * Mask layer
	 */
	MASK_LAYER ("Mask Layer"),

	/**
	 * NucleotideList layer
	 */
	NUCLEOTIDE_LAYER ("DNA Sequence Layer"),

	/**
	 * RepeatFamilyList layer
	 */
	REPEAT_FAMILY_LAYER ("Repeat Family Layer"),

	/**
	 * Generic ScoredChromosomeWindowList layer
	 */
	SCW_LAYER ("Window Layer (Sequencing/Microarray Data)"),

	/**
	 * Simple SCW layer
	 */
	SIMPLE_SCW_LAYER ("Variable Window Layer"),

	/**
	 * Variant layer displaying the multigenome information
	 */
	VARIANT_LAYER ("Variant Layer");


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


	/**
	 * @param layerTypes an array of {@link LayerType}
	 * @return true if the current object is part of the specified layer types
	 */
	public boolean isContainedIn(LayerType[] layerTypes) {
		for (LayerType currentType: layerTypes) {
			if (currentType == this) {
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return getName();
	}
}
