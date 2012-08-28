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
package edu.yu.einstein.genplay.core.multiGenome.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsBooleanList;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFilter extends MGFilter implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 8207888574897530618L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private VCFFile					vcfFile;			// the file reader
	private ByteArrayAsBooleanList 	booleanList;		// list of boolean meaning whether variants pass the filter or not


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


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		filter = (FilterInterface) in.readObject();
		vcfFile = (VCFFile) in.readObject();
		booleanList = (ByteArrayAsBooleanList) in.readObject();
	}


	/**
	 * Constructor of {@link VCFFilter}
	 */
	public VCFFilter () {
		this.filter = null;
		this.vcfFile = null;
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
	public boolean isVariantValid (VariantInterface variant) {
		VCFLine line = variant.getVCFLine();
		if (line != null) {
			//if (vcfFile.equals(information.getReader())) {
			int index = vcfFile.getPositionList().getIndex(line.getReferencePosition());
			if (index != -1) {
				return booleanList.get(index);
			}
			//}
		}
		return false;
	}


	/**
	 * @return the booleanList
	 */
	public ByteArrayAsBooleanList getBooleanList() {
		return booleanList;
	}


	/**
	 * @return the reader
	 */
	public VCFFile getVCFFile() {
		return vcfFile;
	}


	/**
	 * @param reader the reader to set
	 */
	public void setVCFFile(VCFFile reader) {
		this.vcfFile = reader;
	}


	/**
	 * @param booleanList the booleanList to set
	 */
	public void setBooleanList(ByteArrayAsBooleanList booleanList) {
		this.booleanList = booleanList;
	}


	/**
	 * Analyzes lines from VCF file in order to determine if variation pass the filter.
	 * @param results list of VCF lines delimited by columns (must contains the column of the filter)
	 */
	public void generateFilter (List<String> results) {
		vcfFile.initializesPositionList();

		if (results != null) {
			booleanList = new ByteArrayAsBooleanList(vcfFile.getPositionList().size());
			VCFLine line = new VCFLine(null, null);
			for (int i = 0; i < results.size(); i++) {
				line.initialize(results.get(i), vcfFile.getHeader());
				line.processForAnalyse();
				boolean valid = filter.isValid(line);
				booleanList.set(i, valid);
			}
		}
	}


	@Override
	public void show () {
		String info = "";
		info += "VCF File: " + vcfFile.getFile().getName() + "\n";
		info += "Filter display: " + filter.toStringForDisplay() + "\n";
		System.out.println(info);
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


	@Override
	public VCFFilter getDuplicate () {
		VCFFilter duplicate = new VCFFilter();
		duplicate.setFilter(getFilter().getDuplicate());
		duplicate.setVCFFile(getVCFFile());
		duplicate.setBooleanList(getBooleanList());
		return duplicate;
	}

}
