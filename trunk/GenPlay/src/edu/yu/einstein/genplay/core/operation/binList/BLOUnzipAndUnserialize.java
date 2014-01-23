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
package edu.yu.einstein.genplay.core.operation.binList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;



/**
 * Unzips and unserializes a {@link ByteArrayOutputStream} and returns a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOUnzipAndUnserialize implements Operation<BinList> {

	private final ByteArrayOutputStream baos;	// input BinList


	/**
	 * Unzips and unserializes a {@link ByteArrayOutputStream} and returns a {@link BinList}
	 * @param baos a {@link ByteArrayOutputStream} to unzip and unserialize
	 */
	public BLOUnzipAndUnserialize(ByteArrayOutputStream baos) {
		this.baos = baos;
	}


	@Override
	public BinList compute() throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		GZIPInputStream gz = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gz);
		BinList binList = (BinList)ois.readObject();
		ois.close();
		gz.close();
		return binList;
	}


	@Override
	public String getDescription() {
		return "Operation: Unzip and Unserialize";
	}


	@Override
	public String getProcessingDescription() {
		return "Unserializing";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
