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
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.BGZIPReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.operation.UpdateEngine;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class UpdateFileScanner implements FileScannerInterface {

	private final UpdateEngine		engine;		// The export update engine.
	private final ManualVCFReader 	src;		// The reader for the file to use as a model.
	private final BGZIPReader 		dest;		// The reader for the file to update


	/**
	 * Constructor of {@link UpdateFileScanner}
	 * @param engine the phasing engine
	 * @param src the VCF file to use as reference for phasing
	 * @param dest the VCF file to apply the phasing
	 * @throws Exception
	 */
	public UpdateFileScanner (UpdateEngine engine, VCFFile src, VCFFile dest) throws Exception {
		this.engine = engine;
		this.src = new ManualVCFReader(src, engine.getGenomeList(), engine.getVariationMap(), engine.getFilterList(), engine.isIncludeReferences(), engine.isIncludeNoCall());
		this.dest = new BGZIPReader(dest);
	}


	/**
	 * Compares positions of two lines according to:
	 * - CHROM
	 * - POS
	 * @param line01 the first line
	 * @param line02 the second line
	 * @return -1 if the first line is located before the second line, 0 if they are at the same location, 1 if the first line is after the second one
	 */
	private int compareLines (VCFLine line01, VCFLine line02) {
		int chromIndex01 = ProjectManager.getInstance().getProjectChromosomes().getIndex(line01.getCHROM());
		int chromIndex02 = ProjectManager.getInstance().getProjectChromosomes().getIndex(line02.getCHROM());

		if (chromIndex01 == chromIndex02) {
			int	position01 = line01.getReferencePosition();
			int position02 = line02.getReferencePosition();

			if (position01 == position02) {
				return 0;
			} else if (position01 < position02) {
				return -1;
			} else {	// position01 > position02
				return 1;
			}

		} else if ((chromIndex01 == 0) &&  line02.getCHROM().equals("chrM")) {
			return 1;
		} else if ((chromIndex02 == 0) &&  line01.getCHROM().equals("chrM")) {
			return -1;
		} else if (chromIndex01 < chromIndex02) {
			return -1;
		} else {		// chromIndex01 > chromIndex02
			return 1;
		}
	}


	@Override
	public void compute() throws Exception {
		boolean valid = true;
		//while ((lineNumber < lineLimit) && valid) {
		while (valid) {
			List<VCFLine> currentDestinationLines = getCurrentListOfDestinationLine();
			if (currentDestinationLines.size() > 0) {
				List<VCFLine> currentSourceLines = getCurrentListOfSourceLines(currentDestinationLines.get(0));
				if (currentSourceLines.size() > 0) {
					List<VCFLine> validCouplesLines = getValidCouplesLines(currentSourceLines, currentDestinationLines);
					int index = 0;
					while (index < validCouplesLines.size()) {
						engine.processLine(validCouplesLines.get(index), validCouplesLines.get(index + 1));
						index += 2;
					}
				}
			} else {
				valid = false;
			}
		}
	}


	private boolean defineSameVariation (VCFLine line01, VCFLine line02) {
		boolean result = false;

		if (line01.getREF().equals(line02.getREF())) {
			String[] alternatives01 = line01.getAlternatives();
			String[] alternatives02 = line02.getAlternatives();

			for (String currentAlternative: alternatives01) {
				if (hasAlternative(alternatives02, currentAlternative)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}


	@Override
	public VCFLine getCurrentLine() {
		return null;
	}


	private List<VCFLine> getCurrentListOfDestinationLine () throws IOException {
		List<VCFLine> result = new ArrayList<VCFLine>();

		if (!dest.getCurrentLine().isLastLine()) {
			VCFLine firstLine = dest.getCurrentLine();
			firstLine.processForAnalyse();
			result.add(firstLine);

			boolean valid = true;
			while (valid) {
				dest.goNextLine();
				VCFLine currentLine = dest.getCurrentLine();
				if (!currentLine.isLastLine() && (compareLines(firstLine, currentLine) == 0)) {
					result.add(currentLine);
					currentLine.processForAnalyse();
				} else {
					valid = false;
				}
			}
		}

		return result;
	}


	private List<VCFLine> getCurrentListOfSourceLines (VCFLine line) throws IOException {
		List<VCFLine> result = new ArrayList<VCFLine>();

		boolean valid = true;
		while (valid) {
			VCFLine currentSrcLine = src.getCurrentValidLine();
			if (currentSrcLine.isLastLine()) {
				valid = false;
			} else {
				//int compare = compareLines(line, currentSrcLine);
				int compare = compareLines(currentSrcLine, line);
				if (compare == 0) {
					if (currentSrcLine.hasData()) {
						result.add(currentSrcLine);
						currentSrcLine.processForAnalyse();
					}
					src.goNextLine();
				} else if (compare < 0) {
					src.goNextLine();
				} else {
					valid = false;
				}
			}
		}

		return result;
	}


	@Override
	public VCFFile getCurrentVCFFile() {
		return null;
	}


	@Override
	public ManualVCFReader getCurrentVCFReader() {
		return null;
	}


	/**
	 * @return the dest
	 */
	public BGZIPReader getDestinationReader() {
		return dest;
	}


	@Override
	public List<String> getGenomeList() {
		return null;
	}


	/**
	 * @return the src
	 */
	public BGZIPReader getSourceReader() {
		return src.getReader();
	}


	private List<VCFLine> getValidCouplesLines (List<VCFLine> currentSourceLines, List<VCFLine> currentDestinationLines) {
		List<VCFLine> result = new ArrayList<VCFLine>();

		for (VCFLine currentSourceLine: currentSourceLines) {
			for (VCFLine currentDestinationLine: currentDestinationLines) {
				if (defineSameVariation(currentSourceLine, currentDestinationLine)) {
					result.add(currentSourceLine);
					result.add(currentDestinationLine);
					currentSourceLine.processForAnalyse();
					currentDestinationLine.processForAnalyse();
					break;
				}
			}
		}

		return result;
	}


	private boolean hasAlternative (String[] alternatives, String alternative) {
		for (String currentAlternative: alternatives) {
			if (currentAlternative.equals(alternative)) {
				return true;
			}
		}
		return false;
	}

}
