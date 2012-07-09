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
package edu.yu.einstein.genplay.core.multiGenome.export.VCFExport;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.export.FileAlgorithmInterface;
import edu.yu.einstein.genplay.core.multiGenome.export.SingleFileAlgorithm;
import edu.yu.einstein.genplay.core.multiGenome.export.utils.BGZIPReader;
import edu.yu.einstein.genplay.core.multiGenome.export.utils.ManualVCFReader;
import edu.yu.einstein.genplay.core.multiGenome.export.utils.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFExportEngineSingleFile extends ExportVCFEngine {


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			fileHandler = new SingleFileAlgorithm(this);
			return true;
		}
		System.err.println("VCFExportEngineSingleFile.canStart() number of files invalid: " + fileList.size());
		return false;
	}


	@Override
	protected void processLine(FileAlgorithmInterface fileAlgorithm) throws IOException {
		ManualVCFReader vcfReader = fileAlgorithm.getCurrentVCFReader();
		VCFLine currentLine = fileAlgorithm.getCurrentLine();
		data.writeObject(buildLine(vcfReader.getReader(), vcfReader.getAllValidIndex(), fileAlgorithm.getGenomeList(), vcfReader.getAllValidGenome()) + "\n");			// We have to add the line
		headerHandler.processLine(fileAlgorithm.getCurrentVCFFile(), currentLine.getALT(), currentLine.getFILTER(), currentLine.getINFO(), currentLine.getFORMAT());			// Checks the line in order to update IDs for the header
	}


	@Override
	protected void createHeader() throws IOException {
		// Initialize the reader
		BGZIPReader reader = new BGZIPReader(getFileList().get(0));

		// Initialize the list of genomes
		List<String> genomeList = getGenomeList();

		// Gets the meta data
		header = reader.getMetaDataHeader() + "\n";

		// Gets the fields data
		header += headerHandler.getFieldHeader() + "\n";

		// Gets the column names line
		header += reader.getFixedColumns();
		for (int i = 0; i < genomeList.size(); i++) {
			header += FormattedMultiGenomeName.getRawName(genomeList.get(i));
			if (i < (genomeList.size() - 1)) {
				header += "\t";
			}
		}
	}


	/**
	 * Builds the line to insert in the output.
	 * The ALT field contains information only about the required variations.
	 * The genome fields are native fields. Genomes that don't define any required variation will have a empty field: ./.
	 * All other fields are the ones from the native line.
	 * @param reader			the file reader
	 * @param indexes			the indexes referring to the correct alternatives
	 * @param fullGenomesList	the list of all required genomes
	 * @param genomes			the list of genomes that have information matching requirements
	 * @return					the line to insert
	 */
	private String buildLine (BGZIPReader reader, List<Integer> indexes, List<String> fullGenomesList, List<String> genomes) {
		String result = "";
		VCFLine line = reader.getCurrentLine();
		result += line.getCHROM() + "\t";
		result += line.getPOS() + "\t";
		result += line.getID() + "\t";
		result += line.getREF() + "\t";
		result += buildALTField(indexes, line.getALT()) + "\t";
		result += line.getQUAL() + "\t";
		result += line.getFILTER() + "\t";
		result += line.getINFO() + "\t";
		result += line.getFORMAT() + "\t";
		for (int i = 0; i < fullGenomesList.size(); i++) {
			String currentGenome = fullGenomesList.get(i);

			if (genomes.contains(currentGenome)) {
				result += line.getField(reader.getIndexFromGenome(genomes.get(i)));
			} else {
				result += "./.";
			}
			if (i < (fullGenomesList.size() - 1)) {
				result += "\t";
			}
		}
		return result;
	}


	/**
	 * Build the ALT field.
	 * It contains only alternatives matching the requirements
	 * @param indexes	the list of indexes referring to the alternatives
	 * @param alt		the native ALT field
	 * @return			the ALT field to insert
	 */
	private String buildALTField (List<Integer> indexes, String alt) {
		Collections.sort(indexes);
		String[] alternatives = Utils.split(alt, ',');
		String result = "";
		for (int i = 0; i < indexes.size(); i++) {
			result += alternatives[indexes.get(i)];
			if (i < (indexes.size() - 1)) {
				result += ",";
			}
		}
		return result;
	}

}
