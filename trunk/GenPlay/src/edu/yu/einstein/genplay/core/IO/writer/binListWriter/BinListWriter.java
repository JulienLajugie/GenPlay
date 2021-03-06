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
package edu.yu.einstein.genplay.core.IO.writer.binListWriter;

import java.io.File;

import edu.yu.einstein.genplay.core.IO.writer.Writer;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;



/**
 * Abstract class that must be extended by the classes able to write a {@link BinList} into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class BinListWriter implements Writer {

	protected final ProjectChromosomes	projectChromosomes;	// ChromosomeManager
	protected final File 				outputFile;			// output file
	protected final BinList				data;				// data to print
	protected final String				name;				// name of the BinList

	protected String					fullGenomeName;		// the genome name (multi genome project only)
	protected AlleleType				allele;				// the allele type (multi genome project only)


	/**
	 * Creates an instance of {@link BinListWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public BinListWriter(File outputFile, BinList data, String name) {
		this.projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		this.outputFile = outputFile;
		this.data = data;
		this.name = name;
		this.fullGenomeName = null;
		this.allele = null;
	}


	@Override
	public void setMultiGenomeCoordinateSystem (String genome, AlleleType allele) {
		this.fullGenomeName = genome;
		this.allele = allele;
	}

}
