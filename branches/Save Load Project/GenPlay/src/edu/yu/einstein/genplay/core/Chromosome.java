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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * The Chromosome class represents a chromosome with a name and a length.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Chromosome implements Cloneable, Serializable {

	private static final long serialVersionUID = -8339402742378578413L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private String name;	// Name of the chromosome
	private int length;	// Length of the chromosome
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(name);
		out.writeInt(length);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		name = (String) in.readObject();
		length = in.readInt();
	}

	
	/**
	 * Constructor. Creates an instance of a Chromosome.
	 * @param name Name of the chromosome.
	 * @param length Length of the chromosome.
	 */
	public Chromosome(String name, int length) {
		this.setName(name);
		this.length = length;
	}


	/**
	 * @param length the length of a chromosome to set
	 */
	public void setLength(int length) {
		this.length = length;
	}


	/**
	 * @return the length of a chromosome
	 */
	public int getLength() {
		return length;
	}


	/**
	 * @param name the name of a chromosome to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the name of a chromosome
	 */
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return name;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Chromosome other = (Chromosome) obj;
		if (length != other.length) {
			return false;
		}
		if (name == null) { 
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Returns true if the name of the current chromosome is equal to the specified string.
	 * Removes "chr" and "chromosome" before comparing if the string in parameter or if the 
	 * chromosome name start this way (ex: "chr1" becomes "1")
	 * @param otherChromoName
	 * @return true if equal, false otherwise
	 */
	public boolean hasSameNameAs(String otherChromoName) {
		// we remove "chr" or "chromosome" if the name of the current chromosome starts this way
		String chromoName = getName().trim();
		if ((chromoName.length() >= 10) && (chromoName.substring(0, 10).equalsIgnoreCase("chromosome"))) {
			chromoName = chromoName.substring(10);
		} else if ((chromoName.length() >= 3) && (chromoName.substring(0, 3).equalsIgnoreCase("chr"))) {
			chromoName = chromoName.substring(3);
		}
		// we remove "chr" or "chromosome" if the name of the other chromosome starts this way
		otherChromoName = otherChromoName.trim();
		if ((otherChromoName.length() >= 10) && (otherChromoName.substring(0, 10).equalsIgnoreCase("chromosome"))) {
			otherChromoName = otherChromoName.substring(10);
		} else if ((otherChromoName.length() >= 3) && (otherChromoName.substring(0, 3).equalsIgnoreCase("chr"))) {
			otherChromoName = otherChromoName.substring(3);
		}
		return chromoName.equalsIgnoreCase(otherChromoName);
	}
}
