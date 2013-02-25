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
package edu.yu.einstein.genplay.core.multiGenome.filter.VCFID;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FilterUtility;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.StringUtility;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StringIDFilter implements StringIDFilterInterface, Serializable {

	/** Generated default serial ID*/
	private static final long serialVersionUID = -4601435986037527188L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private FilterUtility				utility;
	private VCFHeaderType 				header;				// ID of the filter
	private String 						value;			// value of the filter
	private boolean 					required;		// true if the value is required to pass the the filter
	private List<String>				genomeNames;	// the list of genomes to apply the filter (if required, null otherwise)
	private FormatFilterOperatorType 	operator;		// the operator to use to filter the genomes (if required, null otherwise)


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		if (header != null) {
			out.writeObject(header);
		}
		out.writeObject(value);
		out.writeBoolean(required);
		out.writeObject(genomeNames);
		out.writeObject(operator);
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
		try {
			header = (VCFHeaderType) in.readObject();
		} catch (Exception e) {
			header = null;
		}
		value = (String) in.readObject();
		required = in.readBoolean();
		genomeNames = (List<String>) in.readObject();
		operator = (FormatFilterOperatorType) in.readObject();
		utility = new StringUtility();
	}


	/**
	 * Constructor of {@link StringIDFilter}
	 */
	public StringIDFilter () {
		utility = new StringUtility();
	}


	@Override
	public VCFHeaderType getHeaderType() {
		return header;
	}


	@Override
	public void setHeaderType(VCFHeaderType id) {
		this.header = id;
	}


	@Override
	public String getValue() {
		return value;
	}


	@Override
	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public boolean isRequired() {
		return required;
	}


	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}


	@Override
	public String toStringForDisplay() {
		return utility.toStringForDisplay(this);
	}


	@Override
	public String getErrors() {
		return utility.getErrors(this);
	}


	@Override
	public boolean isValid(VCFLine line) {
		return utility.isValid(this, line);
	}


	@Override
	public boolean isValid(Variant variant) {
		return false;
	}


	@Override
	public boolean equals(Object obj) {
		return utility.equals(this, obj);
	}


	@Override
	public VCFColumnName getColumnName() {
		return header.getColumnCategory();
	}


	@Override
	public void setGenomeNames(List<String> genomeNames) {
		this.genomeNames = genomeNames;
	}


	@Override
	public List<String> getGenomeNames() {
		return genomeNames;
	}


	@Override
	public void setOperator(FormatFilterOperatorType operator) {
		this.operator = operator;
	}


	@Override
	public FormatFilterOperatorType getOperator() {
		return operator;
	}


	@Override
	public IDFilterInterface getDuplicate() {
		StringIDFilterInterface duplicate = new StringIDFilter();
		duplicate.setHeaderType(getHeaderType());
		duplicate.setValue(getValue());
		duplicate.setRequired(isRequired());
		duplicate.setGenomeNames(getGenomeNames());
		duplicate.setOperator(getOperator());
		return duplicate;
	}


	@Override
	public String getName() {
		return "String filter";
	}


	@Override
	public String getDescription() {
		return "Filter for strings.";
	}
}
