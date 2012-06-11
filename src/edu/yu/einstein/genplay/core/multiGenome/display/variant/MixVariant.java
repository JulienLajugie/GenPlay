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
package edu.yu.einstein.genplay.core.multiGenome.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MixVariant implements Serializable, VariantInterface {
	
	/** Generated serial version ID */
	private static final long serialVersionUID = 4873498320038629297L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private int 	start;
	private int 	stop;
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(start);
		out.writeInt(stop);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		start = in.readInt();
		stop = in.readInt();
	}
	
	
	/**
	 * Constructor of {@link MixVariant}
	 * @param start 
	 * @param stop
	 */
	public MixVariant(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}

	
	@Override
	public MGVariantListForDisplay getVariantListForDisplay() {
		return null;
	}
	
	
	@Override
	public int getReferenceGenomePosition() {
		return -1;
	}


	@Override
	public int getLength() {
		return stop - start;
	}


	@Override
	public float getScore() {
		return 1000;
	}


	@Override
	public int phasedWithPos() {
		return -1;
	}


	@Override
	public VariantType getType() {
		return VariantType.MIX;
	}
	
	
	@Override
	public void show() {
		String info = "T: " + getType() + "; ";
		info += "St: " + start + "; ";
		info += "Sp': " + stop + "]";
		System.out.println(info);
	}


	@Override
	public int getStart() {
		return start;
	}


	@Override
	public int getStop() {
		return stop;
	}
	
	
	@Override
	public MGPosition getVariantInformation() {
		return null;
	}
	
	
	@Override
	public MGPosition getFullVariantInformation() {
		return null;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		if (this.hashCode() != obj.hashCode()) {
			return false;
		}
		
		// object must be Test at this point
		MixVariant test = (MixVariant)obj;
		return start == test.getStart() &&
		stop == test.getStop();
	}
	
	
	@Override
	public AlleleType getAlleleType() {
		return null;
	}
}
