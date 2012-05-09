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
package edu.yu.einstein.genplay.core.multiGenome.VCF.filtering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.utils.FilterUtility;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.utils.NumberUtility;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class QualFilter implements NumberIDFilterInterface {

	/** Generated default serial ID*/
	private static final long serialVersionUID = 3099777763400649421L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private FilterUtility			utility;
	private VCFHeaderType			header;
	private InequalityOperators		inequation01;
	private InequalityOperators 	inequation02;
	private Float 					value01;
	private Float 					value02;
	private boolean					cumulative;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(header);
		out.writeObject(inequation01);
		out.writeObject(inequation02);
		out.writeFloat(value01);
		out.writeFloat(value02);
		out.writeBoolean(cumulative);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		header = (VCFHeaderType) in.readObject();
		inequation01 = (InequalityOperators) in.readObject();
		inequation02 = (InequalityOperators) in.readObject();
		value01 = in.readFloat();
		value02 = in.readFloat();
		cumulative = in.readBoolean();
		utility = new NumberUtility();
	}


	/**
	 * Constructor of {@link QualFilter}
	 */
	public QualFilter () {
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


	@Override
	public InequalityOperators getInequation01() {
		return inequation01;
	}


	@Override
	public void setInequation01(InequalityOperators inequation01) {
		this.inequation01 = inequation01;
	}


	@Override
	public InequalityOperators getInequation02() {
		return inequation02;
	}


	@Override
	public void setInequation02(InequalityOperators inequation02) {
		this.inequation02 = inequation02;
	}


	@Override
	public Float getValue01() {
		return value01;
	}


	@Override
	public void setValue01(Float value01) {
		this.value01 = value01;
	}


	@Override
	public Float getValue02() {
		return value02;
	}


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
	public boolean equals(Object obj) {
		return utility.equals(this, obj);
	}
	

	@Override
	public boolean isValid(VariantInterface variant) {
		return false;
	}


	@Override
	public boolean isValid(Map<String, Object> o) {
		return utility.isValid(this, o);
	}


	@Override
	public VCFColumnName getColumnName() {
		return VCFColumnName.QUAL;
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
	public void setGenomeNames(List<String> genomeNames) {}


	@Override
	public List<String> getGenomeNames() {
		return null;
	}


	@Override
	public void setOperator(FormatFilterOperatorType operator) {}


	@Override
	public FormatFilterOperatorType getOperator() {
		return null;
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
		return duplicate;
	}
}
