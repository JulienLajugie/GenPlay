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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;


/**
 * This class symbolizes a variation information.
 * All variation are specific to a type and a start/stop couple of data.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MIXDisplayableVariant implements DisplayableVariant {

	private static final long serialVersionUID = 874763751086685243L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private ChromosomeWindow 	position; 		// Start and stop of the variation
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(position);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		position = (ChromosomeWindow) in.readObject();
	}

	
	/**
	 * Constructor of {@link MIXDisplayableVariant}
	 * @param start		start position of the displayable variant
	 * @param stop		stop position of the displayable variant
	 */
	public MIXDisplayableVariant (int start, int stop) {
		this.position = new ChromosomeWindow(start, stop);
	}


	@Override
	public Variant getNativeVariant() {
		return null;
	}


	@Override
	public VariantType getType() {
		return VariantType.MIX;
	}
	

	@Override
	public int getStart() {
		return position.getStart();
	}


	@Override
	public int getStop() {
		return position.getStop();
	}


	@Override
	public ChromosomeWindow getDeadZone() {
		return null;
	}


	@Override
	public boolean deadZoneExists () {
		return false;
	}


	@Override
	public boolean isOnFirstAllele() {
		return true;
	}


	@Override
	public boolean isOnSecondAllele() {
		return true;
	}


	@Override
	public Double getQualityScore() {
		return 100.0;
	}
	

	@Override
	public int compareTo (DisplayableVariant regularDisplayableVariant) {
		int result;
		if (this.getStart() < regularDisplayableVariant.getStart()) {
			result = -1;
		} else if (this.getStart() > regularDisplayableVariant.getStart()) {
			result = 1;
		} else {
			result = -1;
			if (this.getStop() == regularDisplayableVariant.getStop() &&
					//this.getDeadZone().getStart() == variant.getDeadZone().getStart() &&
					//this.getDeadZone().getStop() == variant.getDeadZone().getStop() &&
					this.getQualityScore() == regularDisplayableVariant.getQualityScore()) {
				result = 0;
			}
		}
		return result;
	}


	@Override
	public void show () {
		String info = "";
		info += "start: " + getStart() + "\n";
		info += "stop: " + getStop() + "\n";
		info += "isOnFirstAllele: " + isOnFirstAllele() + "\n";
		info += "isOnSecondAllele: " + isOnSecondAllele() + "\n";
		info += "quality: " + getQualityScore() + "\n";
		System.out.println(info);
	}

}
