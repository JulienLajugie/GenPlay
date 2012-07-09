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
package edu.yu.einstein.genplay.core.multiGenome.export.BEDExport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.export.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.export.FileAlgorithmInterface;
import edu.yu.einstein.genplay.core.multiGenome.export.SingleFileAlgorithm;
import edu.yu.einstein.genplay.core.multiGenome.export.utils.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BedExportEngineSingleFile extends ExportEngine {

	private final MGSynchronizer 	synchronizer;

	private final String 					fullGenomeName;
	private final AlleleType				allele;
	private final VCFHeaderType 			header;
	private List<AlleleSettingsBedExport> 	alleleList;

	private int genomeIndex;


	/**
	 * Constructor of {@link BedExportEngineSingleFile}
	 * @param fullGenomeName the full genome name of the genome to export
	 * @param allele the allele type to export
	 * @param header the header to use as a score
	 */
	public BedExportEngineSingleFile (String fullGenomeName, AlleleType allele, VCFHeaderType header) {
		this.synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();
		this.fullGenomeName = fullGenomeName;
		this.allele = allele;
		this.header = header;
	}


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			fileHandler = new SingleFileAlgorithm(this);
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
		alleleList = new ArrayList<AlleleSettingsBedExport>();
		if (allele == AlleleType.BOTH) {
			alleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE01));
			alleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE02));
		} else {
			alleleList.add(new AlleleSettingsBedExport(path, allele));
		}
	}


	@Override
	protected void process() throws Exception {
		// Retrieve the index of the column of the genome in the VCF
		genomeIndex = fileHandler.getCurrentVCFReader().getReader().getIndexFromGenome(fullGenomeName);

		// Open the file streams
		for (AlleleSettingsBedExport alleleExport: alleleList) {
			alleleExport.openStreams();
			alleleExport.write(getFileHeader(alleleExport));
		}

		// Compute the file scan algorithm
		fileHandler.compute();

		// Close the file streams
		for (AlleleSettingsBedExport alleleExport: alleleList) {
			alleleExport.closeStreams();
		}
	}


	/**
	 * @param alleleExport
	 * @return the header for the bed
	 */
	private String getFileHeader (AlleleSettingsBedExport alleleExport) {
		String header = "";

		String track = fullGenomeName.replace(" ", "_") + "_" + alleleExport.getAllele().toString().toLowerCase() + "_genplay_export";
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
	protected void processLine(FileAlgorithmInterface fileAlgorithm) throws IOException {
		//ManualVCFReader vcfReader = fileAlgorithm.getCurrentVCFReader();
		VCFLine currentLine = fileAlgorithm.getCurrentLine();

		String gt = currentLine.getFormatField(genomeIndex, 0).toString();
		if (gt.length() == 3) {
			Chromosome chromosome = currentLine.getChromosome();
			int[] lengths = synchronizer.getVariantLengths(currentLine.getREF(), Utils.splitWithTab(currentLine.getALT()), currentLine.getINFO());

			for (AlleleSettingsBedExport alleleExport: alleleList) {
				int charIndex = 0;
				if (alleleExport.getAllele().equals(AlleleType.ALLELE02)) {
					charIndex = 2;
				}
				int altIndex = synchronizer.getAlleleIndex(gt.charAt(charIndex));

				String line = getLine(currentLine, chromosome, lengths, altIndex, alleleExport);
				alleleExport.write(line);
			}
		}
	}


	/**
	 * Writes the line into the file
	 * @param currentLine
	 * @param chromosome
	 * @param lengths
	 * @param allele
	 * @param offset
	 */
	private String getLine (VCFLine currentLine, Chromosome chromosome, int[] lengths, int altIndex, AlleleSettingsBedExport alleleExport) {
		int[] position = getPositions(lengths, currentLine, altIndex, alleleExport);
		String line = null;

		if (position[0] > -1) {
			Object score = getScore(currentLine, alleleExport, altIndex);
			if (score != null) {
				line = buildLine(currentLine, chromosome, position, score);
			} else {
				System.err.println("The line could not be exported. It seems the ID '" + header.getId() + "' has not been found in the line: " + currentLine.toString());
			}
		}

		return line;
	}


	/**
	 * Gets the start and stop positions of the variation
	 * @param lengths		lengths of alternatives of the line
	 * @param currentLine	the current line
	 * @param allele		the variant allele index (to pickup the alternative)
	 * @param offset		the position offset
	 * @return				an array of two positions: start and stop. Both at -1 if positions not valid.
	 */
	private int[] getPositions (int[] lengths, VCFLine currentLine, int altIndex, AlleleSettingsBedExport alleleExport) {
		int[] position = {-1, -1};
		int length = -1000;
		if (altIndex >= 0) {
			length = lengths[altIndex];
			position[0] = Integer.parseInt(currentLine.getPOS()) + alleleExport.getOffset();
			position[1] = position[0] + 1;
			alleleExport.addOffset(length);
			if (length > 0) {
				position[1] += length;
			}
		}
		//System.out.println("start: " + position[0] + "; stop: " + position[1] + "; length: " + length + "; offset: " + alleleExport.getOffset());
		return position;
	}



	private Object getScore (VCFLine currentLine, AlleleSettingsBedExport alleleExport, int altIndex) {
		Object value = currentLine.getHeaderField(header, genomeIndex);
		Object result = null;
		if (value != null) {
			Object[] values = Utils.split(value.toString(), ',');
			int valueIndex = 0;

			if (values.length > 1) {
				if (header instanceof VCFHeaderAdvancedType) {
					VCFHeaderAdvancedType advanced = (VCFHeaderAdvancedType) header;
					if (!advanced.getNumber().equals("1")) {
						valueIndex = altIndex;
						if (advanced.getColumnCategory() == VCFColumnName.FORMAT) {
							if (header.getId().equals("AD")) {
								valueIndex++;
							}
						}
					}
				}
			}

			if (valueIndex < values.length) {
				result = values[valueIndex];
			} else {
				result = null;
			}
		}

		return result;
	}



	/**
	 * Builds the BED line
	 * @param currentLine	the current VCF line
	 * @param chromosome	the chromosome of the line
	 * @param position		the start and stop positions
	 * @param value			the score
	 * @return				the BED line
	 */
	private String buildLine (VCFLine currentLine, Chromosome chromosome, int[] position, Object value) {
		String result = "";
		result += chromosome.getName() + "\t";
		result += position[0] + "\t";
		result += position[1] + "\t";
		result += currentLine.getID() + "\t";
		result += value;
		return result;
	}


	/**
	 * @return the list of generated files
	 */
	public List<File> getExportedFiles () {
		List<File> list = new ArrayList<File>();

		for (AlleleSettingsBedExport alleleExport: alleleList) {
			list.add(alleleExport.getBedFile());
		}

		return list;
	}

}
