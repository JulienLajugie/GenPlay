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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType;

import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPositionInformation;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.PositionCalculation;

/**
 * This class represent the VCF SV file type.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFSV implements MGPosition {

	private	MGPositionInformation 	positionInformation;		// The common genome position information
	private String 					fullGenomeName;				// The genome name
	private int 					genomePosition;				// The genome position
	private int 					initialReferenceOffset;		// The offset between the genome position and the reference genome position
	private int 					initialMetaGenomeOffset;	// The offset between the genome position and the meta genome position
	private int 					extraOffset;				// Offset when multiple insertions happen at the same reference position


	/**
	 * Constructor of {@link VCFSV}
	 */
	public VCFSV (String fullGenomeName, Chromosome chromosome, Map<String, Object> VCFLine, MGPositionInformation positionInformation) {
		this.fullGenomeName = fullGenomeName;
		initialReferenceOffset = 0;
		initialMetaGenomeOffset = 0;
		extraOffset = 0;
		this.positionInformation = positionInformation;
	}

	@Override
	public String getFullGenomeName() {
		return fullGenomeName;
	}

	@Override
	public void setFullGenomeName(String name) {
		fullGenomeName = name;
	}

	@Override
	public String getRawGenomeName() {
		return FormattedMultiGenomeName.getRawName(fullGenomeName);
	}

	@Override
	public String getUsualGenomeName() {
		return FormattedMultiGenomeName.getUsualName(fullGenomeName);
	}

	@Override
	public String getChromosomeName() {
		return positionInformation.getChromosomeName();
	}

	@Override
	public VariantType getType() {
		VariantType type = null;
		try {
			String svtype = getInfoValue("SVTYPE").toString();
			if (svtype.equals("DEL")) {
				type = VariantType.DEL;
			} else if (svtype.equals("INS")) {
				type = VariantType.INS;
			} else if (svtype.equals("DUP")) {
				type = VariantType.DUP;
			} else if (svtype.equals("INV")) {
				type = VariantType.INV;
			} else if (svtype.equals("CNV")) {
				type = VariantType.CNV;
			}
		} catch (Exception e) {
			String alt = getAlternative();
			if (alt.charAt(0) == '<') {
				alt = alt.substring(1, 3);
				if (alt.equals("DEL")) {
					type = VariantType.DEL;
				} else if (alt.equals("INS")) {
					type = VariantType.INS;
				} else if (alt.equals("DUP")) {
					type = VariantType.DUP;
				} else if (alt.equals("INV")) {
					type = VariantType.INV;
				} else if (alt.equals("CNV")) {
					type = VariantType.CNV;
				}
			} else {
				int length = (getAlternative().length() - getReference().length());
				if (length > 0) {
					type = VariantType.DEL;
				} else if (length < 0) {
					type = VariantType.INS;
				} else {
					type = VariantType.SVSNPS;
				}
			}
		}
		return type;
	}

	@Override
	public int getLength() {
		return Math.abs(Integer.parseInt(getInfoValue("SVLEN").toString()));
	}

	@Override
	public boolean isPhased() {
		if (getFormatValue("GT").toString().charAt(1) == '|') {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOnFirstAllele() {
		if (getFormatValue("GT").toString().charAt(0) != '0') {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOnSecondAllele() {
		if (getFormatValue("GT").toString().charAt(2) != '0') {
			return true;
		}
		return false;
	}

	@Override
	public int getGenomePosition() {
		return genomePosition;
	}

	@Override
	public int getNextGenomePosition() {
		return PositionCalculation.getNextGenomePosition(this);
	}

	@Override
	public int getReferenceGenomePosition() {
		return PositionCalculation.getReferenceGenomePosition(this);
	}

	@Override
	public int getNextReferenceGenomePosition() {
		return PositionCalculation.getNextReferenceGenomePosition(this);
	}

	@Override
	public int getNextReferenceGenomePosition(int position) {
		return PositionCalculation.getNextReferenceGenomePosition(this, position);
	}

	@Override
	public int getMetaGenomePosition() {
		return PositionCalculation.getMetaGenomePosition(this);
	}

	@Override
	public int getNextMetaGenomePosition() {
		return PositionCalculation.getNextMetaGenomePosition(this);
	}

	@Override
	public void setGenomePosition(int position) {
		genomePosition = position;
	}

	@Override
	public int getNextMetaGenomePosition(int position) {
		return PositionCalculation.getNextMetaGenomePosition(this, position);
	}

	@Override
	public int getExtraOffset() {
		return extraOffset;
	}
	
	@Override
	public int getInitialReferenceOffset() {
		return initialReferenceOffset;
	}

	@Override
	public int getNextReferencePositionOffset() {
		return PositionCalculation.getNextReferencePositionOffset(this);
	}

	@Override
	public int getInitialMetaGenomeOffset() {
		return initialMetaGenomeOffset;
	}

	@Override
	public int getNextMetaGenomePositionOffset() {
		return PositionCalculation.getNextMetaGenomePositionOffset(this);
	}

	@Override
	public void addExtraOffset(int offset) {
		this.extraOffset += offset;
	}

	@Override
	public void setInitialReferenceOffset(int offset) {
		this.initialReferenceOffset = offset;
	}

	@Override
	public void setInitialMetaGenomeOffset(int offset) {
		this.initialMetaGenomeOffset = offset;
	}

	@Override
	public MGPositionInformation getPositionInformation() {
		return positionInformation;
	}

	@Override
	public String getId() {
		return positionInformation.getId();
	}

	@Override
	public String getReference() {
		return positionInformation.getReference();
	}

	@Override
	public String getAlternative() {
		return positionInformation.getAlternative();
	}

	@Override
	public Double getQuality() {
		return positionInformation.getQuality();
	}

	@Override
	public boolean getFilter() {
		return positionInformation.getFilter();
	}

	@Override
	public String getInfo() {
		return positionInformation.getInfo();
	}

	@Override
	public Object getInfoValue(String field) {
		return positionInformation.getInfoValue(field);
	}

	@Override
	public String getFormat() {
		return positionInformation.getFormat();
	}

	@Override
	public String getFormatValues () {
		return positionInformation.getFormatValues(getRawGenomeName());
	}

	@Override
	public Object getFormatValue(String field) {
		return positionInformation.getFormatValue(getRawGenomeName(), field);
	}
	
}
