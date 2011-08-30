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
package edu.yu.einstein.genplay.core.writer.binListWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.core.list.binList.BinList;




/**
 * Allows to write a BinList as a compressed serialized object.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SerializedBinListWriter extends BinListWriter {

	
	/**
	 * Creates an instance of {@link SerializedBinListWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public SerializedBinListWriter(File outputFile, BinList data, String name) {
		super(outputFile, data, name);
	}

	@Override
	public void write() throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		GZIPOutputStream gz = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(data);
		oos.flush();
		oos.close();
		fos.close();
	}
}
