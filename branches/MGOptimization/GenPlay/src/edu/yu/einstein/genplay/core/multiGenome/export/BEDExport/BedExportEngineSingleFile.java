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
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.export.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.export.FileAlgorithmInterface;
import edu.yu.einstein.genplay.core.multiGenome.export.SingleFileAlgorithm;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BedExportEngineSingleFile extends ExportEngine {

	private final MGSynchronizer 	synchronizer;

	private final String 					fullGenomeName;
	private final boolean					isReferenceGenome;
	private final AlleleType				allele;
	private final VCFHeaderType 			header;
	private List<AlleleSettingsBedExport> 	fullAlleleList;
	private List<AlleleSettingsBedExport> 	alleleListToExport;

	private int genomeIndex;


	/**
	 * Constructor of {@link BedExportEngineSingleFile}
	 * @param fullGenomeName the full genome name of the genome to export
	 * @param allele the allele type to export
	 * @param header the header to use as a score
	 * @param isReferenceGenome true if the export has to be in the coordinate system of the reference genome, false if on the current genome
	 */
	public BedExportEngineSingleFile (String fullGenomeName, AlleleType allele, VCFHeaderType header, boolean isReferenceGenome) {
		this.synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();
		this.fullGenomeName = fullGenomeName;
		this.allele = allele;
		this.header = header;
		this.isReferenceGenome = isReferenceGenome;
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
		fullAlleleList = new ArrayList<AlleleSettingsBedExport>();
		fullAlleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE01));
		fullAlleleList.add(new AlleleSettingsBedExport(path, AlleleType.ALLELE02));

		alleleListToExport = new ArrayList<AlleleSettingsBedExport>();
		if (allele == AlleleType.BOTH) {
			alleleListToExport.add(fullAlleleList.get(0));
			alleleListToExport.add(fullAlleleList.get(1));
		} else if (allele == AlleleType.ALLELE01) {
			alleleListToExport.add(fullAlleleList.get(0));
		} else if (allele == AlleleType.ALLELE02) {
			alleleListToExport.add(fullAlleleList.get(1));
		}
	}


	@Override
	protected void process() throws Exception {
		// Retrieve the index of the column of the genome in the VCF
		genomeIndex = fileHandler.getCurrentVCFReader().getReader().getIndexFromGenome(fullGenomeName);

		// Open the file streams
		for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
			alleleExport.openStreams();
			alleleExport.write(getFileHeader(alleleExport));
		}

		// Compute the file scan algorithm
		fileHandler.compute();

		// Close the file streams
		for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
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

			for (AlleleSettingsBedExport alleleExport: fullAlleleList) {
				int altIndex = synchronizer.getAlleleIndex(gt.charAt(alleleExport.getCharIndex()));
				if (isReferenceGenome) {
					alleleExport.initializeCurrentInformationForReferenceGenome(lengths, currentLine, altIndex);
				} else {
					alleleExport.initializeCurrentInformation(lengths, currentLine, altIndex);
				}
			}

			AlleleSettingsBedExport firstAllele = fullAlleleList.get(0);
			AlleleSettingsBedExport secondAllele = fullAlleleList.get(1);
			if (isReferenceGenome) {
				firstAllele.updateCurrentInformationForReferenceGenome(secondAllele);
				secondAllele.updateCurrentInformationForReferenceGenome(firstAllele);
			} else {
				firstAllele.updateCurrentInformation(secondAllele);
				secondAllele.updateCurrentInformation(firstAllele);
			}


			for (AlleleSettingsBedExport alleleExport: alleleListToExport) {
				Object score = getScore(currentLine, alleleExport);
				if (score != null) {
					String name = alleleExport.getName(currentLine);
					String line = buildLine(chromosome, alleleExport.getCurrentStart(), alleleExport.getCurrentStop(), name, score);
					alleleExport.write(line);
				} else {
					System.err.println("The line could not be exported. It seems the ID '" + header.getId() + "' has not been found in the line: " + currentLine.toString());
				}
			}
		}
	}


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

}
