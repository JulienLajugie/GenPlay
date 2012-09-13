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

import edu.yu.einstein.genplay.core.multiGenome.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BasicFilter extends MGFilter implements Serializable {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -3012257557822225729L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filter);
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
	}


	@Override
	public boolean isVariantValid(Variant variant) {
		return filter.isValid(variant);
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
		BasicFilter test = (BasicFilter)obj;
		return	filter.equals(test.getFilter());
	}


	@Override
	public BasicFilter getDuplicate() {
		BasicFilter duplicate = new BasicFilter();
		duplicate.setFilter(getFilter().getDuplicate());
		return duplicate;
	}


	@Override
	public void show() {
		String info = "";

		info += filter.getName();

		System.out.println(info);
	}

}
