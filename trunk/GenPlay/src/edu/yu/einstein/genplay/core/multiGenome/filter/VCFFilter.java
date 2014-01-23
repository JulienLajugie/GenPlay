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
package edu.yu.einstein.genplay.core.multiGenome.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.PrimitiveList;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFilter extends MGFilter implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 8207888574897530618L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private VCFFile					vcfFile;			// the file reader
	private List<Boolean> 			booleanList;		// list of boolean meaning whether variants pass the filter or not


	/**
	 * Constructor of {@link VCFFilter}
	 */
	public VCFFilter () {
		filter = null;
		vcfFile = null;
		booleanList = null;
	}


	/**
	 * Constructor of {@link VCFFilter}
	 * @param filter the filter
	 * @param vcfFile the reader
	 */
	public VCFFilter (FilterInterface filter, VCFFile vcfFile) {
		this.filter = filter;
		this.vcfFile = vcfFile;
		booleanList = null;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		VCFFilter test = (VCFFilter)obj;
		return vcfFile.equals(test.getVCFFile()) &&
				filter.equals(test.getFilter());
	}


	/**
	 * Analyzes lines from VCF file in order to determine if variation pass the filter.
	 * @param results list of VCF lines delimited by columns (must contains the column of the filter)
	 */
	public void generateFilter (List<VCFLine> results) {
		vcfFile.initializePositionList(ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getChromosome(), results);
		if (results != null) {
			booleanList = new PrimitiveList<Boolean>(Boolean.class, vcfFile.getPositionList().size());
			for (VCFLine currentVcfLine: results) {
				boolean valid = filter.isValid(currentVcfLine);
				booleanList.add(valid);
			}
		}
	}


	/**
	 * @return the booleanList
	 */
	public List<Boolean> getBooleanList() {
		return booleanList;
	}


	@Override
	public VCFFilter getDuplicate () {
		VCFFilter duplicate = new VCFFilter();
		duplicate.setFilter(getFilter().getDuplicate());
		duplicate.setVCFFile(getVCFFile());
		duplicate.setBooleanList(getBooleanList());
		return duplicate;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @return the index where the start value of the window is found or -1 if the value is not found
	 */
	public int getIndex (int value) {
		List<Integer> data = vcfFile.getPositionList();
		int index = getIndex(value, 0, data.size() - 1);

		if (data.get(index) == value) {
			return index;
		} else {
			int lengthCurrentIndex = Math.abs(data.get(index) - value);
			int lengthSecondIndex = -1;
			int secondIndex = -1;
			if (value > data.get(index)) {
				if ((index + 1) < data.size()) {
					secondIndex = index + 1;
				}
			} else {
				if ((index - 1) > 0) {
					secondIndex = index -1;
				}
			}

			if (secondIndex >= 0) {
				lengthSecondIndex = Math.abs(data.get(secondIndex) - value);
				if (lengthSecondIndex < lengthCurrentIndex) {
					index = secondIndex;
				}
			}
		}
		return index;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @param indexStart	start index (in the data array)
	 * @param indexStop		stop index (in the data array)
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int getIndex (int value, int indexStart, int indexStop) {
		List<Integer> data = vcfFile.getPositionList();
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == data.get(indexStart + middle)) {
			return indexStart + middle;
		} else if (value > data.get(indexStart + middle)) {
			return getIndex(value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(value, indexStart, indexStart + middle);
		}
	}


	@Override
	public int getVariantIndex(Variant variant) {
		int position = variant.getReferenceGenomePosition();
		return getIndex(position);
	}


	/**
	 * @return the reader
	 */
	public VCFFile getVCFFile() {
		return vcfFile;
	}


	@Override
	public boolean isVariantValid(int variantIndex) {
		boolean result = false;
		if (variantIndex != -1) {
			result = booleanList.get(variantIndex);
		}
		return result;
	}


	@Override
	public boolean isVariantValid (Variant variant) {
		int index = getVariantIndex(variant);
		return isVariantValid(index);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		filter = (FilterInterface) in.readObject();
		vcfFile = (VCFFile) in.readObject();
		booleanList = (List<Boolean>) in.readObject();
	}


	/**
	 * @param booleanList the booleanList to set
	 */
	public void setBooleanList(List<Boolean> booleanList) {
		this.booleanList = booleanList;
	}


	/**
	 * @param reader the reader to set
	 */
	public void setVCFFile(VCFFile reader) {
		vcfFile = reader;
	}


	@Override
	public void show () {
		String info = "";
		info += "VCF File: " + vcfFile.getFile().getName() + "\n";
		info += "Filter display: " + filter.toStringForDisplay() + "\n";
		System.out.println(info);
	}


	/**
	 * Shows information about the MG filter
	 */
	public void showBooleanList () {
		String info = "";

		if (booleanList == null) {
			info += "The boolean list is null";
		} else {
			if (booleanList.size() == 0) {
				info += "The boolean list is empty";
			} else {
				info += "Size of the boolean list: " + booleanList.size() + "\n";
				for (int i = 0; i < booleanList.size(); i++) {
					info += booleanList.get(i) + "; ";
					if ((i > 0) && ((i % 5) == 0)) {
						info += "\n";
					}
				}
			}
		}

		System.out.println(info);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filter);
		out.writeObject(vcfFile);
		out.writeObject(booleanList);
	}

}
