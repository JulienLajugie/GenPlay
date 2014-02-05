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
package edu.yu.einstein.genplay.dataStructure.gwBookmark;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;

/**
 * A bookmark for a position on the genome.
 * @author Julien Lajugie
 */
public class GWBookmark implements Serializable {

	/**  Generated serial ID */
	private static final long serialVersionUID = 5462392735006277687L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Description of the bookmark */
	private final String description;

	/** Bookmarked position on the genome */
	private final GenomeWindow 	genomeWindow;


	/**
	 * Creates an instance of {@link GWBookmark}
	 * @param description description of the bookmark
	 * @param genomeWindow the genome window to bookmark
	 */
	public GWBookmark(String description, GenomeWindow genomeWindow) {
		this.description = description;
		this.genomeWindow = genomeWindow;
	}


	/**
	 * @return the description of the bookmark
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the bookmarked position
	 */
	public GenomeWindow getGenomeWindow() {
		return genomeWindow;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the class version number
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
