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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFScanner;

import java.io.IOException;

import edu.yu.einstein.genplay.core.multiGenome.VCF.BGZIPReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * See the {@link VCFScanner} description for further information on scanners.
 * The {@link VCFGenomeScanner} will go through all the lines of a {@link VCFFile}.
 * All chromosomes will be scanned.
 * 
 * It is technically optimized to avoid memory peaks.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFGenomeScanner extends VCFScanner {

	private final BGZIPReader reader;			// The gz reader


	/**
	 * Constructor of {@link VCFGenomeScanner}
	 * @param receiver
	 * @param vcfFile
	 * @throws IOException
	 */
	public VCFGenomeScanner (VCFScannerReceiver receiver, VCFFile vcfFile) throws IOException {
		super(receiver, vcfFile);
		this.reader = new BGZIPReader(vcfFile);
	}


	@Override
	protected VCFLine getFirstLine() {
		return reader.getCurrentLine();
	}


	@Override
	protected VCFLine getNextLine() {
		try {
			reader.goNextLine();
		} catch (IOException e) {
			ExceptionManager.getInstance().handleException(e);
		}
		return reader.getCurrentLine();
	}


	@Override
	protected void endScan() {
		reader.closeStreams();
	}


}
