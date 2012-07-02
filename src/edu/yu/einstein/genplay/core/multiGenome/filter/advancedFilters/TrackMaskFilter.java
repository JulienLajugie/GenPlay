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
package edu.yu.einstein.genplay.core.multiGenome.filter.advancedFilters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.IndelVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackMaskFilter implements FilterInterface, Serializable {

	/** Generated default serial ID*/
	private static final long serialVersionUID = 2892057204781001181L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private Track<?> track;
	private List<VariantInterface> 	variantList;		// The list of variant


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(track);
		out.writeObject(variantList);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		track = (Track<?>) in.readObject();
		variantList = (List<VariantInterface>) in.readObject();
	}


	/**
	 * Constructor of {@link TrackMaskFilter}
	 */
	public TrackMaskFilter () {}


	/**
	 * @return the variantList
	 */
	public List<VariantInterface> getVariantList() {
		return variantList;
	}


	/**
	 * @param variantList the variantList to set
	 */
	public void setVariantList(List<VariantInterface> variantList) {
		this.variantList = variantList;
	}


	/**
	 * Generate the list of variant
	 */
	public void generateFilter () {
		variantList = track.getMultiGenomeDrawer().getFullVariantList();
	}


	/**
	 * @return the track
	 */
	public Track<?> getTrack() {
		return track;
	}


	/**
	 * @param track the track to set
	 */
	public void setTrack(Track<?> track) {
		this.track = track;
	}


	@Override
	public String getName() {
		return "Track Mask Filter";
	}


	@Override
	public String getDescription() {
		return "Use a stripes from a track as a mask in order to filter stripes from other selected tracks.";
	}


	@Override
	public String toStringForDisplay() {
		if (track != null) {
			return "Use the track " + track.getName() + " as a mask.";
		}
		return "Nothing defined.";
	}


	@Override
	public String getErrors() {
		String errors = "";

		if (track == null) {
			errors += "The track to use as a mask has not been chosen.";
		}

		if (variantList == null) {
			errors += "No list of variant found.";
		}

		return errors;
	}


	private boolean hasStripes () {
		if ((variantList != null) && (variantList.size() > 0)) {
			return true;
		}
		return false;
	}


	@Override
	public boolean isValid(Map<String, Object> value) {
		if (hasStripes()) {
			Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
			int refPosition = Integer.parseInt(value.get(VCFColumnName.POS).toString());
			int startPosition = ShiftCompute.computeShiftForReferenceGenome(chromosome, refPosition);
			List<VariantInterface> trackVariants = Utils.searchVariantInterval(variantList, startPosition, startPosition + 1);
			return areValid(trackVariants, value);
		}
		return false;
	}


	@Override
	public boolean isValid(VariantInterface variant) {
		if (hasStripes()) {
			List<VariantInterface> trackVariants = Utils.searchVariantInterval(variantList, variant.getStart(), variant.getStop());
			Map<String, Object> variantPosition = variant.getVariantInformation().getMappedVCFLine();
			return areValid(trackVariants, variantPosition);
		}
		return false;
	}


	/**
	 * Checks if a variant has a equal variant in a list of variant
	 * @param trackVariants	the list of variants
	 * @param variant		the variant to process
	 * @return				true if one variant from the list is equal to the variant to process
	 */
	private boolean areValid (List<VariantInterface> trackVariants, Map<String, Object> variant) {
		for (VariantInterface current: trackVariants) {
			if ((current instanceof IndelVariant) || (current instanceof SNPVariant)) {
				MGPosition position = current.getVariantInformation();
				if (position != null) {
					Map<String, Object> currentPosition = position.getMappedVCFLine();
					if (areEqual(variant, currentPosition)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * Checks if two variants are equal according to the fields POS and INFO
	 * @param variant	the variant to process
	 * @param current	the current variant
	 * @return			true if variants are equals
	 */
	private boolean areEqual (Map<String, Object> variant, Map<String, Object> current) {
		// FIXME the variant comparison will work most of the time since the INFO field is extremly specific.
		// However, if the INFO fields are similar, it will need to compare the genomes related to the variations.
		// The problem is that we cannot always know it...
		if ((variant.get(VCFColumnName.POS) == current.get(VCFColumnName.POS)) &&
				(variant.get(VCFColumnName.REF) == current.get(VCFColumnName.REF)) &&
				(variant.get(VCFColumnName.INFO) == current.get(VCFColumnName.INFO))) {
			return true;
		}
		return false;
	}


	@Override
	public FilterInterface getDuplicate() {
		TrackMaskFilter duplicate = new TrackMaskFilter();
		duplicate.setTrack(getTrack());
		duplicate.setVariantList(getListDuplicate());
		return duplicate;
	}


	/**
	 * @return a duplicate of the list of variants
	 */
	private List<VariantInterface> getListDuplicate () {
		List<VariantInterface> duplicate = new ArrayList<VariantInterface>();

		if (variantList != null) {
			for (VariantInterface variant: variantList) {
				duplicate.add(variant);
			}
		}

		return duplicate;
	}


	@Override
	public String toString () {
		return getName();
	}


}
