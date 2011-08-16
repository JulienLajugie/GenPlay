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
package edu.yu.einstein.genplay.core.list.binList.operation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.operation.Operation;



/**
 * Serializes and zips a BinList into a {@link ByteArrayOutputStream}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSerializeAndZip implements Operation<ByteArrayOutputStream> {

	private final BinList 	binList;		// input BinList
	

	/**
	 * Serializes and zips a BinList into a {@link ByteArrayOutputStream}
	 * @param binList input BinList
	 */
	public BLOSerializeAndZip(BinList binList) {
		this.binList = binList;
	}


	@Override
	public ByteArrayOutputStream compute() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gz = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(binList);
		oos.flush();
		oos.close();
		gz.flush();
		gz.close();
		return baos;
	}
	

	@Override
	public String getDescription() {
		return "Operation: Serialize and Zip";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Serializing";
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
