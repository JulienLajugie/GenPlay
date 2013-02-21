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
package edu.yu.einstein.genplay.core.list.ScoredWindowList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.list.GenomicDataArrayList;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.list.arrayList.DoubleListFactory;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * A {@link GenomicDataList} of doubles where the scores are put into bins with a fixed size
 * that is specified during the creating of an instance.
 * @author Julien Lajugie
 */
public class BinList extends GenomicDataArrayList<Double>  implements Serializable, GenomicDataList<Double> {

	/** Generated serial ID */
	private static final long serialVersionUID = -3768849978109172398L;

	/** Saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** Size of the bins */
	private int	binSize;

	/** Precision of the data */
	private DataPrecision dataPrecision;


	/**
	 * Creates a new instance of {@link BinList} with a data precision of 32-bit
	 * @param binSize size of the bins
	 */
	public BinList(int binSize) {
		this(binSize, DataPrecision.PRECISION_32BIT);
	}


	/**
	 * Creates a new instance of {@link BinList}
	 * @param binSize size of the bins
	 * @param dataPrecision {@link DataPrecision} of the scores of the BinList
	 */
	public BinList(int binSize, DataPrecision dataPrecision) {
		this.binSize = binSize;
		this.dataPrecision = dataPrecision;
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			// count the number of element for the current chromosome
			int binCount = (int) Math.ceil(projectChromosome.get(i).getLength() / binSize);
			add(DoubleListFactory.createDoubleList(dataPrecision, binCount));
		}
	}


	/**
	 * Performs a deep clone of the current BinList
	 * @return a new BinList that is a deep copy of this one
	 */
	@Override
	public BinList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((BinList) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @return the size of the bins
	 */
	public int getBinSize() {
		return binSize;
	}


	/**
	 * @return {@link DataPrecision} indication the precision of the scores
	 */
	public DataPrecision getDataPrecision() {
		return dataPrecision;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		binSize = in.readInt();
		dataPrecision = (DataPrecision) in.readObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(binSize);
		out.writeObject(dataPrecision);
	}
}
