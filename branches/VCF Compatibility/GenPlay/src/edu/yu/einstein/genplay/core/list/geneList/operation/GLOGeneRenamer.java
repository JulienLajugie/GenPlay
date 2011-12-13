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
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.operation.Operation;


/**
 * Renames the genes of a list with new names specified in a file with the following format:
 * oldName \t newName
 * @author Chirag Gorasia
 * @version 0.1
 */

public class GLOGeneRenamer implements Operation<GeneList> {

	private GeneList 	geneList;			// input geneList
	private File 		fileName;			// fileName in which the gene needs to be renamed
	private boolean		stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOGeneRenamer}
	 * @param geneList list of genes
	 * @param fileName file name contain
	 */
	public GLOGeneRenamer(GeneList geneList, File fileName) {
		this.geneList = geneList;
		this.fileName = fileName;
	}


	@Override
	public GeneList compute() throws Exception {
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader(fileName));
			GeneList renamedList = geneList.deepClone();
			String lineRead;
			String geneNames[];
			while ((lineRead = bufReader.readLine()) != null && !stopped) {
				geneNames = lineRead.split("\t");
				for (int i = 0; i < renamedList.size() && !stopped; i++) {
					for (int j = 0; j < renamedList.size(i) && !stopped; j++) {
						if (geneNames.length > 1) {
							if (geneNames[0].trim().equalsIgnoreCase(renamedList.get(i,j).getName())) {
								renamedList.get(i,j).setName(geneNames[1].trim());			
							}
						}
					}
				}
			}
			return renamedList;
		} finally {
			if (bufReader != null) {
				bufReader.close();
			}
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Rename Genes, input file: " + fileName.getName();
	}


	@Override
	public String getProcessingDescription() {
		return "Renaming Genes";
	}


	@Override
	public int getStepCount() {
		return 1;
	}	


	@Override
	public void stop() {
		this.stopped = true;
	}
}
