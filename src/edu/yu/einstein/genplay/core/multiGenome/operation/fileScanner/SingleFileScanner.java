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
import edu.yu.einstein.genplay.core.multiGenome.operation.BasicEngine;

/**
 * Scanner for single file.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SingleFileScanner implements FileScannerInterface {

	private final 	BasicEngine 		engine;			// The export engine.
	private final 	VCFFile 			vcfFile;		// The VCF file to scan.
	private final 	List<String> 		genomeList;		// The list of genome names.
	private final 	ManualVCFReader 	vcfReader;		// The actual VCF reader.
	private 		VCFLine 			currentLine;	// The current line of the scan.


	/**
	 * Constructor of {@link SingleFileScanner}
	 * @param engine		the related export engine
	 * @throws Exception
	 */
	public SingleFileScanner (BasicEngine engine) throws Exception {
		this.engine = engine;
		vcfFile = engine.getFileList().get(0);
		genomeList = engine.getGenomeList();
		vcfReader = new ManualVCFReader(vcfFile, genomeList, engine.getVariationMap(), engine.getFilterList(), engine.isIncludeReferences(), engine.isIncludeNoCall());
	}


	@Override
	public void compute() throws IOException {
		// Gets the first line of data
		currentLine = vcfReader.getCurrentValidLine();

		// Scan the file line by line
		while (!currentLine.isLastLine()) {
			if (currentLine.hasData()) {
				engine.processLine(this);
			}
			currentLine = vcfReader.getNextValidLine();
		}

		// Closes the streams
		vcfReader.getReader().closeStreams();
	}


	@Override
	public VCFLine getCurrentLine() {
		return currentLine;
	}


	@Override
	public VCFFile getCurrentVCFFile() {
		return vcfFile;
	}


	@Override
	public ManualVCFReader getCurrentVCFReader() {
		return vcfReader;
	}


	@Override
	public List<String> getGenomeList() {
		return genomeList;
	}

}
