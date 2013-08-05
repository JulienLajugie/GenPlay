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
package edu.yu.einstein.genplay.core.multiGenome.operation.convert;

import java.io.File;

import net.sf.jannot.source.Locator;
import net.sf.jannot.tabix.TabixWriter;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 */
public class MGOTBIIndex {

	private final File bgzFile;	// the bgzip file
	private File tbiFile;		// the tbi file


	/**
	 * Constructor of {@link MGOTBIIndex}
	 * @param file the BGZIP file
	 */
	public MGOTBIIndex(File file) {
		bgzFile = file;
	}


	/**
	 * Index a BGZIP file using Tabix
	 * @return true if the operation has been done correctly. False otherwise.
	 * @throws Exception
	 */
	public Boolean compute() throws Exception {
		if (Utils.getExtension(bgzFile).equals("gz")) {
			tbiFile = new File(bgzFile.getPath() + ".tbi");
			tbiFile.createNewFile();
			Locator tbiLocator = new Locator(tbiFile.getPath());
			TabixWriter writer = new TabixWriter(tbiLocator, TabixWriter.VCF_CONF);
			writer.createIndex(tbiLocator);
			return true;
		} else {
			return false;
		}
	}


	/**
	 * @return the tbiFile
	 */
	public File getTbiFile() {
		return tbiFile;
	}

}
