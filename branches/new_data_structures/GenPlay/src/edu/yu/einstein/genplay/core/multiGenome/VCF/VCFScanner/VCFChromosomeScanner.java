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

import net.sf.jannot.tabix.TabixReader.Iterator;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;

/**
 * See the {@link VCFScanner} description for further information on scanners.
 * The {@link VCFChromosomeScanner} will go through the lines of a {@link VCFFile} for the current {@link Chromosome} only.
 * Only the current {@link Chromosome} will be scanned.
 * 
 * This uses the Tabix API and we'll retrieve all information about a {@link Chromosome} at once, it may generate memory peaks.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFChromosomeScanner extends VCFScanner {

	private Iterator 		result;		// The full list of lines returned by the Tabix API.
	private VCFLine 		line;		// The current line in process.


	/**
	 * Constructor of {@link VCFChromosomeScanner}
	 * @param receiver
	 * @param vcfFile
	 * @throws IOException
	 */
	public VCFChromosomeScanner (VCFScannerReceiver receiver, VCFFile vcfFile) throws IOException {
		super(receiver, vcfFile);
		result = null;
	}


	@Override
	protected void endScan() {
		result = null;
		line = null;
	}


	/**
	 * @return the current {@link VCFLine} being processed
	 */
	private VCFLine getCurrentLine () {
		try {
			String nextLine = result.next();
			line = new VCFLine(nextLine, vcfFile.getHeader());
		} catch (IOException e) {
			line = null;
		}
		return line;
	}


	@Override
	protected VCFLine getFirstLine() {
		Chromosome chromosome = ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getChromosome();
		String query = chromosome.getName() + ":0-" + chromosome.getLength();
		result = vcfFile.getReader().query(query);
		return getCurrentLine();
	}


	@Override
	protected VCFLine getNextLine() {
		return getCurrentLine();
	}
}
