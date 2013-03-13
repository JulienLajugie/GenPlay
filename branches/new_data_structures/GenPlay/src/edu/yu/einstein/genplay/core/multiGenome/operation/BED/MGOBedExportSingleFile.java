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
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.FileScannerInterface;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.SingleFileScanner;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Operation to export a VCF track as a BED file.
 * This will create new file(s).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOBedExportSingleFile extends ExportEngine {

	private final String 					fullGenomeName;			// The genome to export.
	private final AlleleType				allele;					// The allele(s) to export.
	private final VCFHeaderType 			header;					// The header field to use as a score.
	private final CoordinateSystemType 		coordinateSystem;		// The coordinate system to export the positions.
	private List<AlleleSettingsBedExport> 	fullAlleleList;			// The full list of allele settings helper.
	private List<AlleleSettingsBedExport> 	alleleListToExport;		// The list of allele settings helper to use.

	private int genomeIndex;										// The index of the genome in the file to export.


	/**
	 * Constructor of {@link MGOBedExportSingleFile}
	 * @param fullGenomeName the full genome name of the genome to export
	 * @param allele the allele type to export
	 * @param header the header to use as a score
	 * @param coordinateSystem the coordinate system of the position to export the data
	 */
	public MGOBedExportSingleFile (String fullGenomeName, AlleleType allele, VCFHeaderType header, CoordinateSystemType coordinateSystem) {
		this.fullGenomeName = fullGenomeName;
		this.allele = allele;
		this.header = header;
		this.coordinateSystem = coordinateSystem;
	}


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			fileScanner = new SingleFileScanner(this);
			initializeAlleleList();
			return true;
		} else {
			System.err.println("BedExportEngineSingleFile.canStart() number of files invalid: " + fileList.size());
		}
		return false;
	}


	/**
	 * Initialize the list of allele
	 */
	private void initializeAlleleList () {
		fullAlleleList = new ArrayList<AlleleSettingsBedExport>();
		fullAlleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE01, coordinateSystem));
		fullAlleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE02, coordinateSystem));

		alleleListToExport = new ArrayList<AlleleSettingsBedExport>();
		//if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {
		if (allele == AlleleType.BOTH) {
			alleleListToExport.add(fullAlleleList.get(0));
			alleleListToExport.add(fullAlleleList.get(1));
		} else if (allele == AlleleType.ALLELE01) {
			alleleListToExport.add(fullAlleleList.get(0));
		} else if (allele == AlleleType.ALLELE02) {
			alleleListToExport.add(fullAlleleList.get(1));
		}
		/*} else {
			alleleListToExport.add(fullAlleleList.get(0));
		}*/
	}


	@Override
	protected void process() throws Exception {
		// Retrieve the index of the column of the genome in the VCF
		genomeIndex = fileScanner.getCurrentVCFReader().getReader().getIndexFromGenome(fullGenomeName);

		// Open the file streams
		for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
			alleleExport.openStreams();
			alleleExport.write(getFileHeader(alleleExport));
		}

		// Compute the file scan algorithm
		fileScanner.compute();

		// Close the file streams
		for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
			alleleExport.closeStreams();
		}
	}


	/**
	 * @param alleleExport
	 * @return the header for the bed
	 */
	private String getFileHeader (AlleleSettingsBed alleleExport) {
		String header = "";

		String track = fullGenomeName.replace(" ", "_") + "_";
		if (alleleExport.getCoordinateSystem() == CoordinateSystemType.CURRENT_GENOME) {
			track +=  alleleExport.getAllele().toString().toLowerCase();
		} else if (alleleExport.getCoordinateSystem() == CoordinateSystemType.METAGENOME) {
			track += "meta_genome";
		} else if (alleleExport.getCoordinateSystem() == CoordinateSystemType.REFERENCE) {
			track += "reference_genome";
		}
		track += "_genplay_export";
		String id = this.header.getId();
		if (id == null) {
			id = this.header.getColumnCategory().toString();
		}
		String description = id + " is used as score to extract positions of: " + getVariantDescription() + " from the file " + getFileList().get(0).getFile().getName();

		header += "track name=" + track + " ";
		header += "description=\"" + description + "\" ";
		header += "useScore=1";

		return header;
	}


	@Override
	public void processLine(FileScannerInterface fileAlgorithm) throws IOException {
		VCFLine currentLine = fileAlgorithm.getCurrentLine();
		currentLine.processForAnalyse();

		String gt = currentLine.getFormatField(genomeIndex, 0).toString();
		if (gt.length() == 3) {
			Chromosome chromosome = currentLine.getChromosome();
			int[] lengths = VCFLineUtility.getVariantLengths(currentLine.getREF(), Utils.split(currentLine.getALT(), ','), currentLine.getINFO());

			for (AlleleSettingsBedExport alleleExport: fullAlleleList) {
				int altIndex = VCFLineUtility.getAlleleIndex(gt.charAt(alleleExport.getCharIndex()));
				alleleExport.initializeCurrentInformation(lengths, currentLine, altIndex);
			}

			AlleleSettingsBedExport firstAllele = fullAlleleList.get(0);
			AlleleSettingsBedExport secondAllele = fullAlleleList.get(1);
			firstAllele.updateCurrentInformation(secondAllele, chromosome);
			secondAllele.updateCurrentInformation(firstAllele, chromosome);

			firstAllele.finalizePosition();
			secondAllele.finalizePosition();

			for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
				if (alleleExport.isWritable()) {
					Object score = getScore(currentLine, alleleExport);
					if (score != null) {
						String name = alleleExport.getName(currentLine);
						String line;
						line = buildLine(chromosome, alleleExport.getCurrentStart(), alleleExport.getCurrentStop(), name, score);

						alleleExport.write(line);
					} else {
						System.err.println("The line could not be exported. It seems the ID '" + header.getId() + "' has not been found in the line: " + currentLine.toString());
					}
				}
			}
		}
	}


	/**
	 * @param currentLine	the current line in process
	 * @param alleleExport	the allele setting helper to use
	 * @return the score to use, null otherwise
	 */
	private Object getScore (VCFLine currentLine, AlleleSettingsBedExport alleleExport) {
		Object value = currentLine.getHeaderField(header, genomeIndex);
		Object result = null;
		if (value != null) {
			Object[] values = Utils.split(value.toString(), ',');
			int valueIndex = 0;

			if (values.length > 1) {
				if (header instanceof VCFHeaderAdvancedType) {
					VCFHeaderAdvancedType advanced = (VCFHeaderAdvancedType) header;
					if (!advanced.getNumber().equals("1")) {
						valueIndex = alleleExport.getCurrentAltIndex();
						if (advanced.getColumnCategory() == VCFColumnName.FORMAT) {
							if (header.getId().equals("AD")) {
								valueIndex++;
							}
						}
					}
				}
			}

			if ((valueIndex > -1) && (valueIndex < values.length)) {
				result = values[valueIndex];
			} else {
				result = null;
			}
		}

		return result;
	}


	/**
	 * Builds the BED line
	 * @param chromosome	the chromosome of the line
	 * @param position		the start and stop positions
	 * @param value			the score
	 * @return				the BED line
	 */
	private String buildLine (Chromosome chromosome, int start, int stop, String name, Object value) {
		String result = "";
		result += chromosome.getName() + "\t";
		result += start + "\t";
		result += stop + "\t";
		result += name + "\t";
		result += value;
		return result;
	}


	/**
	 * @return the list of generated files
	 */
	public List<File> getExportedFiles () {
		List<File> list = new ArrayList<File>();

		for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
			list.add(alleleExport.getBedFile());
		}

		return list;
	}


	@Override
	public void processLine(VCFLine src, VCFLine dest) throws IOException {}

}
