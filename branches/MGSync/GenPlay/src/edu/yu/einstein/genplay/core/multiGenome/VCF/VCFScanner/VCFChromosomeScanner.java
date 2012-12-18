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
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFChromosomeScanner extends VCFScanner {

	private List<String> result;
	private VCFLine line;
	private int index;


	/**
	 * Constructor of {@link VCFChromosomeScanner}
	 * @param receiver
	 * @param vcfFile
	 * @throws IOException
	 */
	public VCFChromosomeScanner (VCFScannerReceiver receiver, VCFFile vcfFile) throws IOException {
		super(receiver, vcfFile);
		result = null;
		index = 0;
	}


	@Override
	protected VCFLine getFirstLine() {
		Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		try {
			result = vcfFile.getReader().query(chromosome.getName(), 0, chromosome.getLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getCurrentLine();
	}


	@Override
	protected VCFLine getNextLine() {
		index++;
		return getCurrentLine();
	}


	private VCFLine getCurrentLine () {
		if (index < result.size()) {
			line = new VCFLine(result.get(index), vcfFile.getHeader());
		} else {
			line = null;
		}
		return line;
	}


	@Override
	protected void endScan() {
		result = null;
		line = null;
		index = 0;
	}

}