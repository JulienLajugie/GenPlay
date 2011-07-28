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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;


/**
 * This class symbolizes a variation information.
 * All variation are specific to a type and a start/stop couple of data.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Variant {

	private MGPosition 			variantPosition;		// The real variant position
	private VariantType 		type;					// Type of variation
	private ChromosomeWindow 	position; 				// Start and stop of the variation
	private ChromosomeWindow 	deadZone; 				// Start and stop of the dead zone
	private Double 				qualityScore;			// The quality of the variant


	/**
	 * Constructor of {@link Variant}
	 * @param type		variation type
	 * @param position	chromosome window
	 */
	public Variant (VariantType type, ChromosomeWindow position, MGPosition positionInformation) {
		this.type = type;
		this.position = position;
		this.variantPosition = positionInformation;
	}

	
	/**
	 * @return the variantPosition
	 */
	public MGPosition getVariantPosition() {
		return variantPosition;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(VariantType type) {
		this.type = type;
	}


	/**
	 * @return the type
	 */
	public VariantType getType() {
		return type;
	}


	/**
	 * @return the position
	 */
	public ChromosomeWindow getPosition() {
		return position;
	}


	/**
	 * @param start the new start position
	 */
	public void setStart(int start) {
		position.setStart(start);
	}


	/**
	 * @param stop the new stop position
	 */
	public void setStop(int stop) {
		position.setStop(stop);
	}


	/**
	 * @return the start position
	 */
	public int getStart() {
		return position.getStart();
	}


	/**
	 * @return the stop position
	 */
	public int getStop() {
		return position.getStop();
	}


	/**
	 * @return the deadZone
	 */
	public ChromosomeWindow getDeadZone() {
		return deadZone;
	}


	/**
	 * @param deadZone the deadZone to set
	 */
	public void setDeadZone(ChromosomeWindow deadZone) {
		this.deadZone = deadZone;
	}


	/**
	 * @return true if a dead zone exist
	 */
	public boolean deadZoneExists () {
		if (this.deadZone == null) {
			return false;
		}
		return true;
	}


	/**
	 * @return the isOnFirstAllele
	 */
	public boolean isOnFirstAllele() {
		return variantPosition.isOnFirstAllele();
	}


	/**
	 * @return the isOnSecondAllele
	 */
	public boolean isOnSecondAllele() {
		return variantPosition.isOnSecondAllele();
	}


	/**
	 * @return the qualityScore
	 */
	public Double getQualityScore() {
		//return qualityScore;
		return variantPosition.getQuality();
	}


	/**
	 * @param qualityScore the qualityScore to set
	 */
	/*public void setQualityScore(Double qualityScore) {
		this.qualityScore = qualityScore;
	}*/


	public int compareTo (Variant variant) {
		int result;
		if (this.getStart() < variant.getStart()) {
			result = -1;
		} else if (this.getStart() > variant.getStart()) {
			result = 1;
		} else {
			result = -1;
			if (this.getStop() == variant.getStop() &&
					//this.getDeadZone().getStart() == variant.getDeadZone().getStart() &&
					//this.getDeadZone().getStop() == variant.getDeadZone().getStop() &&
					this.getQualityScore() == variant.getQualityScore()) {
				result = 0;
			}
		}
		return result;
	}
	
	
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