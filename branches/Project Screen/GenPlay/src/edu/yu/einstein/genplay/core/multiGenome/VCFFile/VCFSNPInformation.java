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
package edu.yu.einstein.genplay.core.multiGenome.VCFFile;

import java.util.Map;

import edu.yu.einstein.genplay.core.enums.Nucleotide;

/**
 * This class manages the SNPs information.
 * @author Nicolas Fourel
 */
public class VCFSNPInformation {
	
	
	private Nucleotide 	nReference;
	private Nucleotide 	nAlternative;
	
	private int			genomePosition;		// The genome position
	private int			metaGenomePosition;		// The meta genome position
	
	private Map<String, String> info;					// The genome information (ex: GT:GQ -> X/X:Y)
	private boolean 	isOnFirstAllele;
	private boolean 	isOnSecondAllele;
	
	
	public VCFSNPInformation () {}
	
	
	public VCFSNPInformation (int genomePosition, int metaGenomePosition, Nucleotide nReference, Nucleotide nAlternative) {
		this.genomePosition = genomePosition;
		this.metaGenomePosition = metaGenomePosition;
		this.nReference = nReference;
		this.nAlternative = nAlternative;
	}


	/**
	 * @return the nReference
	 */
	public Nucleotide getnReference() {
		return nReference;
	}


	/**
	 * @param nReference the nReference to set
	 */
	public void setnReference(Nucleotide nReference) {
		this.nReference = nReference;
	}


	/**
	 * @return the nAlternative
	 */
	public Nucleotide getnAlternative() {
		return nAlternative;
	}


	/**
	 * @param nAlternative the nAlternative to set
	 */
	public void setnAlternative(Nucleotide nAlternative) {
		this.nAlternative = nAlternative;
	}


	/**
	 * @return the genomePosition
	 */
	public int getGenomePosition() {
		return genomePosition;
	}


	/**
	 * @param genomePosition the genomePosition to set
	 */
	public void setGenomePosition(int genomePosition) {
		this.genomePosition = genomePosition;
	}


	/**
	 * @return the metaGenomePosition
	 */
	public int getMetaGenomePosition() {
		return metaGenomePosition;
	}


	/**
	 * @param metaGenomePosition the metaGenomePosition to set
	 */
	public void setMetaGenomePosition(int metaGenomePosition) {
		this.metaGenomePosition = metaGenomePosition;
	}
	
	
	public String getInfoValue (String title) {
		if (info != null) {
			if (info.get(title) != null) {
				return info.get(title);
			}
		}
		return null;
	}


	public boolean isPhased () {
		String value = getInfoValue("GT").substring(1, 2);
		if (value.equals("/")) {
			return false;
		} else {
			return true;
		}
	}


	public int[] getGT () {
		int[] values = new int[2];
		values[0] = Integer.parseInt(getInfoValue("GT").substring(0, 1));
		values[1] = Integer.parseInt(getInfoValue("GT").substring(2));
		return values;
	}


	public double getQuality () {
		return Double.parseDouble(getInfoValue("GQ"));
	}
	
	
	/**
	 * @return the isOnFirstAllele
	 */
	public boolean isOnFirstAllele() {
		return isOnFirstAllele;
	}


	/**
	 * @param isOnFirstAllele the isOnFirstAllele to set
	 */
	public void setOnFirstAllele(boolean isOnFirstAllele) {
		this.isOnFirstAllele = isOnFirstAllele;
	}


	/**
	 * @return the isOnSecondAllele
	 */
	public boolean isOnSecondAllele() {
		return isOnSecondAllele;
	}


	/**
	 * @param isOnSecondAllele the isOnSecondAllele to set
	 */
	public void setOnSecondAllele(boolean isOnSecondAllele) {
		this.isOnSecondAllele = isOnSecondAllele;
	}


	/**
	 * @param info the info to set
	 */
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	
	
	
}
