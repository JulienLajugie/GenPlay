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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFGenomeIndexer;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLine implements Serializable {

	/** Generated default serial version ID */
	private static final long serialVersionUID = 4610867949459433256L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private VCFGenomeIndexer genomeIndexer;

	private String[] elements;
	private final static HashMap<String, Chromosome> chromosomeNamesMap = new HashMap<String, Chromosome>();
	private boolean hasData;			// Used during the export only. Some line can be qualified according to different constraints (variations, filters...). If the line passes those constraints, it has then the significant data.
	private boolean readyForAnalyse;

	// Alternatives attributes
	private String[] alternatives;
	private int[] alternativesLength;
	private VariantType[] alternativesTypes;

	// Format attributes
	private String[] format;
	private List<String[]> formats;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeIndexer);
		out.writeObject(elements);
		out.writeBoolean(hasData);
		out.writeBoolean(readyForAnalyse);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		Object o = in.readObject();
		genomeIndexer = null;
		if (o != null) {
			genomeIndexer = (VCFGenomeIndexer) o;
		}
		elements = (String[]) in.readObject();
		hasData = in.readBoolean();
		readyForAnalyse = in.readBoolean();
		if (readyForAnalyse) {
			processForAnalyse();
		}
	}


	/**
	 * @return true if all elelements are present and not empty, false otherwise
	 */
	public boolean isIntegrityValid () {
		boolean valid = true;
		for (String element: elements) {
			if ((element == null) || element.isEmpty()) {
				valid = false;
			}
		}
		return valid;
	}


	/**
	 * Constructor of {@link VCFLine}
	 * @param line		the line from the VCF
	 * @param genomeIndexer the genome indexer
	 */
	public VCFLine (String line, VCFGenomeIndexer genomeIndexer) {
		initialize(line, genomeIndexer);
	}


	/**
	 * Initialize the {@link VCFLine}
	 * @param line line from the VCF
	 * @param genomeIndexer the genome indexer
	 */
	public void initialize (String line, VCFGenomeIndexer genomeIndexer) {
		this.genomeIndexer = genomeIndexer;
		if (line == null) {							// if null
			elements = null;						// there is no element and it is the last line
		} else if (line.isEmpty()) {				// if empty: bad reading behavior
			elements = new String[0];				// we filter setting the elements as "empty" in order to be skipped in the process
		} else {									// the line matches the requirements
			elements = Utils.splitWithTab(line);	// we split with tabulations
		}
		hasData = false;
		readyForAnalyse = false;
	}


	/**
	 * Process line information for further analysis.
	 * Retrieves and organizes alternatives and formats.
	 */
	public void processForAnalyse () {
		if (!readyForAnalyse) {
			readyForAnalyse = true;
			processAlternatives();
			processAlternativeLengths();
			processAlternativeTypes();

			processFormat();
			processFormats();
		}
	}


	///////////////////////////////////////////////////// ALT Process
	private void processAlternatives () {
		alternatives = Utils.split(getALT(), ',');
	}

	/**
	 * Retrieves the length of all defined alternatives
	 * If an alternative is SV coded, the info field is used
	 */
	private void processAlternativeLengths() {
		alternativesLength = new int[alternatives.length];

		for (int i = 0; i < alternatives.length; i++) {
			alternativesLength[i] = retrieveVariantLength(alternatives[i]);
		}
	}

	/**
	 * Retrieves the length of a variation using the reference and the alternative.
	 * If the alternative is a structural variant, the length is given by the SVLEN INFO attributes.
	 * @param alternative	ALT field
	 * @return	the length of the variation
	 */
	private int retrieveVariantLength (String alternative) {
		int length = 0;

		if (isStructuralVariant(alternative)) {
			String lengthPattern = "SVLEN=";
			String info = getINFO();
			int lengthPatternIndex = info.indexOf(lengthPattern) + lengthPattern.length();
			int nextCommaIndex = info.indexOf(";", lengthPatternIndex);
			if (nextCommaIndex == -1) {
				length = Integer.parseInt(info.substring(lengthPatternIndex));
			} else {
				length = Integer.parseInt(info.substring(lengthPatternIndex, nextCommaIndex));
			}
		} else {
			length = alternative.length() - getREF().length();
		}

		return length;
	}

	/**
	 * @param alternative ALT field (or part of it)
	 * @return true if the given alternative is coded as an SV
	 */
	private boolean isStructuralVariant (String alternative) {
		if (alternative.charAt(0) == '<') {
			return true;
		}
		return false;
	}

	/**
	 * Defines the variant type according to several lengths
	 */
	private void processAlternativeTypes () {
		alternativesTypes = new VariantType[alternativesLength.length];

		for (int i = 0; i < alternativesTypes.length; i++) {
			alternativesTypes[i] = getVariantType(alternativesLength[i]);
		}
	}

	/**
	 * Tests the length of a variation to find its type out.
	 * @param variationLength 	length of the variation
	 * @return					the variation type {@link VariantType}
	 */
	public VariantType getVariantType (int variationLength) {
		if (variationLength < 0) {
			return VariantType.DELETION;
		} else if (variationLength > 0) {
			return VariantType.INSERTION;
		} else if (variationLength == 0) {
			return VariantType.SNPS;
		} else {
			return null;
		}
	}
	/////////////////////////////////////////////////////


	///////////////////////////////////////////////////// FORMAT Process
	private void processFormat () {
		format = Utils.split(getFORMAT(), ':');
	}

	private void processFormats () {
		formats = new ArrayList<String[]>();

		for (int i = 9; i < elements.length; i++) {
			formats.add(Utils.split(elements[i], ':'));
		}
	}

	/**
	 * @param genomeRawName
	 * @return true if the genotype if heterozygote
	 */
	public boolean isHeterozygote (String genomeRawName) {
		String gt = getGenotype(genomeRawName);
		if (gt.length() > 2) {
			char c1 = gt.charAt(0);
			char c2 = gt.charAt(2);
			return c1 != c2;
		}
		return false;
	}

	/**
	 * @param genomeRawName
	 * @return true if the genotype if homozygote
	 */
	public boolean isHomozygote (String genomeRawName) {
		String gt = getGenotype(genomeRawName);
		if (gt.length() > 2) {
			char c1 = gt.charAt(0);
			char c2 = gt.charAt(2);
			return c1 == c2;
		}
		return false;
	}

	/**
	 * @param genomeRawName
	 * @return true if the genotype contains a no call (a dot: '.')
	 */
	public boolean genomeHasNoCall (String genomeRawName) {
		String gt = getGenotype(genomeRawName);
		return genotypeHasNoCall(gt);
	}

	/**
	 * @param genotype
	 * @return true if at least one no call is defined in the genotype
	 */
	private boolean genotypeHasNoCall (String genotype) {
		if (genotype.length() > 2) {
			char c1 = genotype.charAt(0);
			char c2 = genotype.charAt(2);
			return ((c1 == '.') || (c2 == '.'));
		}
		return false;
	}

	/**
	 * @return true if at least one no call is defined in the line
	 */
	public boolean lineHasNoCall () {
		for (String[] format: formats) {
			String genotype = format[0];
			if (genotypeHasNoCall(genotype)) {
				return true;
			}
		}
		return false;
	}
	/////////////////////////////////////////////////////


	///////////////////////////////////////////////////// Advanced field getters
	/**
	 * The reference genome position is not always the position where the variation starts.
	 * The given position may refer to the last nucleotide before the variation starts, which means the start position is one nucleotide further.
	 * @param altIndex	the alternative index
	 * @return the start position on the reference genome of the given alternative index
	 */
	public int getStartPosition (int altIndex) {
		int pos = getReferencePosition();
		if (altIndex >= 0) {
			//String alternative = alternatives[altIndex];
			if (alternativesTypes[altIndex] != VariantType.SNPS) {
				pos++;
			}
		}
		return pos;
	}


	/**
	 * @return the chromosome related to the VCF line
	 */
	public Chromosome getChromosome () {
		Chromosome chromosome = null;
		if (chromosomeNamesMap.containsKey(getCHROM())) {
			chromosome = chromosomeNamesMap.get(getCHROM());
		} else {
			String shortLineName = getShortChromosomeName(getCHROM());
			List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();
			for (Chromosome current: chromosomeList) {
				String shortProjectChromosomeName = getShortChromosomeName(current.getName());
				if (shortLineName.equals(shortProjectChromosomeName)) {
					chromosome = current;
				}
			}
		}
		if (chromosome != null) {
			chromosomeNamesMap.put(getCHROM(), chromosome);
		}
		return chromosome;
	}

	/**
	 * @return the POS field as an integer
	 */
	public Integer getReferencePosition () {
		return Integer.parseInt(getPOS());
	}

	/**
	 * @return the QUAL field as a float
	 */
	public Float getQuality () {
		float quality = -1;
		try {
			quality = Float.parseFloat(getQUAL());
		} catch (Exception e) {}
		return quality;
	}

	/**
	 * @param index index of the field
	 * @return the field associated to the index
	 */
	public String getField (int index) {
		if (index < elements.length) {
			return elements[index];
		}
		return null;
	}

	/**
	 * @return the format
	 */
	public String[] getFormat() {
		return format;
	}

	/**
	 * @param genomeIndex	the index of the genome
	 * @param fieldIndex	the index of the field (starts from 0)
	 * @return	the object contained in the format field of a genome at the specified index.
	 */
	public Object getFormatField (int genomeIndex, int fieldIndex) {
		Object[] fields = getFormatValues(genomeIndex);
		if (fieldIndex < fields.length) {
			return fields[fieldIndex];
		}
		return null;
	}

	/**
	 * @param genomeRawName the genome raw name
	 * @return the format values of a genome
	 */
	public String[] getFormatValues (String genomeRawName) {
		int index = genomeIndexer.getIndexFromRawGenomeName(genomeRawName);
		return getFormatValues(index);
	}

	/**
	 * @param genomeIndex the index of the genome
	 * @return the format values of a genome
	 */
	public String[] getFormatValues (int genomeIndex) {
		genomeIndex -= 9;
		if (genomeIndex > -1) {
			return formats.get(genomeIndex);
		}
		return null;
	}

	/**
	 * @param genomeRawName a genome raw name
	 * @return the genotype of the given genome
	 */
	public String getGenotype (String genomeRawName) {
		String[] format = getFormatValues(genomeRawName);
		if (format != null) {
			return format[0];
		}
		return "";
	}

	/**
	 * Get the value associated to the header.
	 * The genome index is used only if the header is related to a FORMAT fields.
	 * @param header		the header
	 * @param genomeIndex	the index of the genome in the line
	 * @return				the value
	 */
	public Object getHeaderField (VCFHeaderType header, int genomeIndex) {
		Object result = null;
		if (header.getColumnCategory() == VCFColumnName.QUAL) {
			result = getQUAL();
		} else if (header.getColumnCategory() == VCFColumnName.INFO) {
			result = VCFLineUtility.getInfoValue(getINFO(), header);
		} else if (header.getColumnCategory() == VCFColumnName.FORMAT) {
			result = VCFLineUtility.getFormatValue(getFORMAT(), getField(genomeIndex), header);
		}
		return result;
	}


	/**
	 * @param fullGenomeName a full genome name
	 * @return	the alternative of the genome
	 */
	public int[] getAlternativeIndexesFromFullName (String fullGenomeName) {
		String rawName = FormattedMultiGenomeName.getRawName(fullGenomeName);
		if (rawName != null) {
			String genotype = getGenotype(rawName);
			String[] indexes = genotype.split("/");
			int[] array = new int[indexes.length];
			for (int i = 0; i < indexes.length; i++) {
				array[i] = VCFLineUtility.getAlleleIndex(indexes[i]);
			}
			return array;
		}
		return null;
	}


	/**
	 * @param fullGenomeName a full genome name
	 * @param allele an allele type
	 * @return	the alternative of the genome
	 */
	public String getAlternativeFromFullName (String fullGenomeName, AlleleType allele) {
		String rawName = FormattedMultiGenomeName.getRawName(fullGenomeName);
		if (rawName != null) {
			return getAlternativeFromRawName(rawName, allele);
		}
		return null;
	}


	/**
	 * @param rawName a raw genome name
	 * @param allele an allele type
	 * @return	the alternative of the genome
	 */
	public String getAlternativeFromRawName (String rawName, AlleleType allele) {
		String genotype = getGenotype(rawName);
		if (genotype != null) {
			int index = -1;
			if (allele == AlleleType.ALLELE01) {
				index = VCFLineUtility.getAlleleIndex(genotype.charAt(0) + "");
			} else if (allele == AlleleType.ALLELE02) {
				index = VCFLineUtility.getAlleleIndex(genotype.charAt(2) + "");
			}
			if (index > -1) {
				return alternatives[index];
			}
		}
		return null;
	}

	/**
	 * @return the alternatives
	 */
	public String[] getAlternatives() {
		return alternatives;
	}

	/**
	 * @return all the alternatives as a string
	 */
	public String getStringAlternatives() {
		String alt = "";
		for (int i = 0; i < (alternatives.length - 1); i++) {
			alt += alternatives[i] + ",";
		}
		alt += alternatives[alternatives.length - 1];
		return alt;
	}

	/**
	 * @return the alternativesLength
	 */
	public int[] getAlternativesLength() {
		return alternativesLength;
	}

	/**
	 * @return the alternativesTypes
	 */
	public VariantType[] getAlternativesTypes() {
		return alternativesTypes;
	}
	/////////////////////////////////////////////////////


	///////////////////////////////////////////////////// Field getters
	/**
	 * @return the CHROM field
	 */
	public String getCHROM () {
		return elements[0];
	}
	/**
	 * @return the POS field
	 */
	public String getPOS () {
		return elements[1];
	}
	/**
	 * @return the ID field
	 */
	public String getID () {
		return elements[2];
	}
	/**
	 * @return the REF field
	 */
	public String getREF () {
		return elements[3];
	}
	/**
	 * @return the ALT field
	 */
	public String getALT () {
		return elements[4];
	}
	/**
	 * @return the QUAL field
	 */
	public String getQUAL () {
		return elements[5];
	}
	/**
	 * @return the FILTER field
	 */
	public String getFILTER () {
		return elements[6];
	}
	/**
	 * @return the INFO field
	 */
	public String getINFO () {
		return elements[7];
	}
	/**
	 * @return the FORMAT field
	 */
	public String getFORMAT () {
		return elements[8];
	}
	/////////////////////////////////////////////////////


	///////////////////////////////////////////////////// Class utilities
	/**
	 * Reduce the chromosome name removing any knid of prefix.
	 * Keeps everything after the first integer
	 * @param chromosomeName the chromosome name
	 * @return	the shorted name
	 */
	private String getShortChromosomeName (String chromosomeName) {
		String shortName;
		int intOffset = Utils.getFirstIntegerOffset(chromosomeName, 0);

		if (intOffset != -1) {
			shortName = chromosomeName.substring(intOffset);
		} else {
			char lastChar = chromosomeName.charAt(chromosomeName.length() - 1);
			if ((lastChar == 'X') || (lastChar == 'Y') || (lastChar == 'M')) {
				shortName = "" + lastChar;
			} else {
				shortName = chromosomeName;
			}
		}
		return shortName;
	}
	/////////////////////////////////////////////////////


	///////////////////////////////////////////////////// Line utilities
	/**
	 * A valid line contains data and at least 10 columns: 8 (CHROM to INFO) + 1 (FORMAT) + 1 (sample)
	 * @return true if the line is valid, false otherwise
	 */
	public boolean isValid () {
		if ((elements != null) && (elements.length > 9)) {
			return true;
		}
		return false;
	}

	/**
	 * @return true if it is the last line (the line has no data), false otherwise (the line has data)
	 */
	public boolean isLastLine () {
		if (elements == null) {
			return true;
		}
		return false;
	}

	/**
	 * @return the hasData
	 */
	public boolean hasData() {
		return hasData;
	}

	/**
	 * @param hasData the hasData to set
	 */
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	/**
	 * @return the elements
	 */
	public String[] getElements() {
		return elements;
	}

	/**
	 * @return the elements merged with a tab
	 */
	public String getMergedElements() {
		String result = "";
		for (int i = 0; i < elements.length; i++) {
			result += elements[i];
			if (i < (elements.length - 1)) {
				result += "\t";
			}
		}
		return result;
	}


	/**
	 * @return the genomeIndexer
	 */
	public VCFGenomeIndexer getGenomeIndexer() {
		return genomeIndexer;
	}


	/**
	 * @param columnName a {@link VCFColumnName}
	 * @return the related value
	 */
	public String getValueFromColumn (VCFColumnName columnName) {
		if (columnName == VCFColumnName.CHROM) {
			return getCHROM();
		} else if (columnName == VCFColumnName.POS) {
			return getPOS();
		} else if (columnName == VCFColumnName.ID) {
			return getID();
		} else if (columnName == VCFColumnName.REF) {
			return getREF();
		} else if (columnName == VCFColumnName.ALT) {
			return getALT();
		} else if (columnName == VCFColumnName.QUAL) {
			return getQUAL();
		} else if (columnName == VCFColumnName.FILTER) {
			return getFILTER();
		} else if (columnName == VCFColumnName.INFO) {
			return getINFO();
		} else if (columnName == VCFColumnName.FORMAT) {
			return getFORMAT();
		}
		return null;
	}


	/**
	 * @return the longest alternative length. Negative if deletion, positive if insertion, 0 if line has not been set for analysis
	 */
	public int getLongestAlternativeLength () {
		int max = 0;
		if (readyForAnalyse) {
			for (int current: alternativesLength) {		// We need to compare with the absolute value
				max = Math.max(max, Math.abs(current));
			}
			for (int current: alternativesLength) {		// but we still want the negative if it exists
				if (max == Math.abs(current)) {
					max = current;
				}
			}
		}
		return max;
	}


	/**
	 * Shows the elements of line
	 */
	public void showElements () {
		String info = "";
		for (int i = 0; i < elements.length; i++) {
			info += i + ": " + elements[i];
			if (i < (elements.length - 1)) {
				info += "\n";
			}
		}
		System.out.println(info);
	}

	/**
	 * @return the whole line as map (keys are column names)
	 */
	public Map<String, Object> toFullMap () {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(VCFColumnName.CHROM.toString(), getCHROM());
		map.put(VCFColumnName.POS.toString(), getPOS());
		map.put(VCFColumnName.ID.toString(), getID());
		map.put(VCFColumnName.REF.toString(), getREF());
		map.put(VCFColumnName.ALT.toString(), getALT());
		map.put(VCFColumnName.QUAL.toString(), getQUAL());
		map.put(VCFColumnName.FILTER.toString(), getFILTER());
		map.put(VCFColumnName.INFO.toString(), getINFO());
		map.put(VCFColumnName.FORMAT.toString(), getFORMAT());

		for (int i = 9; i < elements.length; i++) {
			map.put(genomeIndexer.getGenomeRawName(i), elements[i]);
		}

		return map;
	}

	@Override
	public String toString () {
		String s = "";
		for (int i = 0; i < (elements.length - 1); i++) {
			s += elements[i] + "\t";
		}
		s += elements[elements.length - 1];
		return s;
	}


	/**
	 * @return the formats
	 */
	public List<String[]> getFormats() {
		return formats;
	}


	/////////////////////////////////////////////////////
}
