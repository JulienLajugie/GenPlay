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
package edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner;

import java.io.IOException;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;

/**
 * The file scanner interface provides methods to scan file(s).
 * A file scanner can scan one or several file according to the purpose of the scan.
 * A scan between several files and one file or to update a file using another one is not the same.
 * Once a line has to be processed, the scanner calls the process method of the export engine.
 * This way, the export engine does not have to check if it is the right line, the scanner already did.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface FileScannerInterface {


	/**
	 * Compute the file algorithm
	 * @throws IOException
	 * @throws Exception
	 */
	public void compute () throws IOException, Exception;


	/**
	 * @return the current VCF line
	 */
	public VCFLine getCurrentLine ();


	/**
	 * @return the current VCF file
	 */
	public VCFFile getCurrentVCFFile ();


	/**
	 * @return the current VCF reader
	 */
	public ManualVCFReader getCurrentVCFReader ();


	/**
	 * @return the required list of genome names
	 */
	public List<String> getGenomeList ();

}
