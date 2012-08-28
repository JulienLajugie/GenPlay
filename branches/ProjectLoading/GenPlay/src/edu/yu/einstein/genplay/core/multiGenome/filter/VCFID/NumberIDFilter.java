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

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FilterUtility;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.NumberUtility;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NumberIDFilter implements NumberIDFilterInterface, Serializable {

	/** Generated default serial ID*/
	private static final long serialVersionUID = -4281690761382110840L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private FilterUtility				utility;
	private VCFHeaderType 				header;				// ID of the filter
	private InequalityOperators			inequation01;
	private InequalityOperators 		inequation02;
	private Float 						value01;
	private Float 						value02;
	private boolean						cumulative;
	private List<String>				genomeNames;	// the list of genomes to apply the filter (if required, null otherwise)
	private FormatFilterOperatorType 	operator;		// the operator to use to filter the genomes (if required, null otherwise)


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(inequation01);
		out.writeObject(inequation02);
		out.writeFloat(value01);
		out.writeFloat(value02);
		out.writeBoolean(cumulative);
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
		inequation01 = (InequalityOperators) in.readObject();
		inequation02 = (InequalityOperators) in.readObject();
		value01 = in.readFloat();
		value02 = in.readFloat();
		cumulative = in.readBoolean();
		genomeNames = (List<String>) in.readObject();
		operator = (FormatFilterOperatorType) in.readObject();
		utility = new NumberUtility();
	}


	/**
	 * Constructor of {@link NumberIDFilter}
	 */
	public NumberIDFilter () {
		utility = new NumberUtility();
	}


	@Override
	public VCFHeaderType getHeaderType() {
		return header;
	}


	@Override
	public void setHeaderType(VCFHeaderType id) {
		this.header = id;
	}


	/**
	 * @return the inequation01
	 */
	@Override
	public InequalityOperators getInequation01() {
		return inequation01;
	}


	/**
	 * @param inequation01 the inequation01 to set
	 */
	@Override
	public void setInequation01(InequalityOperators inequation01) {
		this.inequation01 = inequation01;
	}


	/**
	 * @return the inequation02
	 */
	@Override
	public InequalityOperators getInequation02() {
		return inequation02;
	}


	/**
	 * @param inequation02 the inequation02 to set
	 */
	@Override
	public void setInequation02(InequalityOperators inequation02) {
		this.inequation02 = inequation02;
	}


	/**
	 * @return the value01
	 */
	@Override
	public Float getValue01() {
		return value01;
	}


	/**
	 * @param value01 the value01 to set
	 */
	@Override
	public void setValue01(Float value01) {
		this.value01 = value01;
	}


	/**
	 * @return the value02
	 */
	@Override
	public Float getValue02() {
		return value02;
	}


	/**
	 * @param value02 the value02 to set
	 */
	@Override
	public void setValue02(Float value02) {
		this.value02 = value02;
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
	public boolean isValid(VariantInterface variant) {
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
	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}


	@Override
	public boolean isCumulative() {
		return cumulative;
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
		NumberIDFilterInterface duplicate = new NumberIDFilter();
		duplicate.setHeaderType(getHeaderType());
		duplicate.setInequation01(getInequation01());
		duplicate.setInequation02(getInequation02());
		duplicate.setValue01(getValue01());
		duplicate.setValue02(getValue02());
		duplicate.setCumulative(isCumulative());
		duplicate.setGenomeNames(getGenomeNames());
		duplicate.setOperator(getOperator());
		return duplicate;
	}


	@Override
	public String getName() {
		return "Number filter";
	}


	@Override
	public String getDescription() {
		return "Filter for numbers.";
	}
}
