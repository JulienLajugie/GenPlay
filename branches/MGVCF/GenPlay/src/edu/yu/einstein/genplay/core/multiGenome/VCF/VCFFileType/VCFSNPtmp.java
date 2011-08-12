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

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPositionInformation;

/**
 * This class represent the VCF SNPs file type.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFSNPtmp implements Variant {
	
	private int 					genomePosition;				// The genome position
	private int 					metaGenomePosition;			// The meta genome position
	
	
	
	/**
	 * Constructor of {@link VCFSNPtmp}
	 * @param genomePosition 
	 * @param metaGenomePosition 
	 */
	public VCFSNPtmp (int genomePosition, int metaGenomePosition) {
		this.genomePosition = genomePosition;
		this.metaGenomePosition = metaGenomePosition;
	}


	@Override
	public String getFullGenomeName() {
		return null;
	}

	@Override
	public void setFullGenomeName(String name) {}

	@Override
	public String getRawGenomeName() {
		return null;
	}

	@Override
	public String getUsualGenomeName() {
		return null;
	}

	@Override
	public String getChromosomeName() {
		return null;
	}

	@Override
	public VariantType getType() {
		return VariantType.SNPS;
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public boolean isPhased() {
		return false;
	}

	@Override
	public boolean isOnFirstAllele() {
			return true;
	}

	@Override
	public boolean isOnSecondAllele() {
			return true;
	}

	@Override
	public int getGenomePosition() {
		return genomePosition;
	}

	@Override
	public int getNextGenomePosition() {
		return getGenomePosition() + 1;
	}

	@Override
	public int getReferenceGenomePosition() {
		return -1;
	}

	@Override
	public int getNextReferenceGenomePosition() {
		return -1;
	}

	@Override
	public int getNextReferenceGenomePosition(int position) {
		return -1;
	}

	@Override
	public int getMetaGenomePosition() {
		return metaGenomePosition;
	}

	@Override
	public int getNextMetaGenomePosition() {
		return getMetaGenomePosition() + 1;
	}

	@Override
	public void setGenomePosition(int position) {}

	@Override
	public int getNextMetaGenomePosition(int position) {
		return getNextMetaGenomePosition();
	}

	@Override
	public int getExtraOffset() {
		return -1;
	}

	@Override
	public int getInitialReferenceOffset() {
		return -1;
	}

	@Override
	public int getNextReferencePositionOffset() {
		return -1;
	}

	@Override
	public int getInitialMetaGenomeOffset() {
		return getMetaGenomePosition() - getGenomePosition();
	}

	@Override
	public int getNextMetaGenomePositionOffset() {
		return getInitialMetaGenomeOffset() + 1;
	}

	@Override
	public void addExtraOffset(int offset) {}

	@Override
	public void setInitialReferenceOffset(int offset) {}

	@Override
	public void setInitialMetaGenomeOffset(int offset) {}
	
	@Override
	public MGPositionInformation getPositionInformation() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getReference() {
		return null;
	}

	@Override
	public String getAlternative() {
		return null;
	}

	@Override
	public Double getQuality() {
		return null;
	}

	@Override
	public boolean getFilter() {
		return true;
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public Object getInfoValue(String field) {
		return null;
	}

	@Override
	public String getFormat() {
		return null;
	}

	@Override
	public String getFormatValues () {
		return null;
	}

	@Override
	public Object getFormatValue(String field) {
		return null;
	}
	
	
	
	
}