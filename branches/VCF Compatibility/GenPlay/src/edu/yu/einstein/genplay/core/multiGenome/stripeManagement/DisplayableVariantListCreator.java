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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosomeOld;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * This class adapts VCF variant information to displayable variant.
 * It mostly creates list of variants displayable by the GUI layer.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DisplayableVariantListCreator implements DisplayableDataList<List<DisplayableVariant>>, Serializable {

	private static final long serialVersionUID = 7895054388386894571L;		// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version

	// Graphic variables
	private GenomeWindow					currentGenomeWindow;			// Chromosome with the adapted data
	private double							currentXRatio;					// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )

	// Filter variables
	private Map<String, List<VariantType>>	genomes;						// Genome names list
	private List<IDFilterInterface>			filters;						// List of filters

	// Lists
	private List<Variant> 					fittedVariantList;				// Complete list of variant (gains a lot of time and increase the fluidity)
	private List<DisplayableVariant> 		fittedDisplayableVariantList;	// Complete list of the displayable variant

	// Changes indicators
	private boolean							hasBeenChanged;					// Is true if any information has been modified
	private boolean 						genomeWindowHasChanged;			// Is true if the current chromosome has changed
	private boolean 						xRatioHasChanged;				// Is true if the xRatio has changed

	private int passFilter = 0;
	private int notPassFilter = 0;

	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(currentGenomeWindow);
		out.writeDouble(currentXRatio);
		out.writeObject(genomes);
		out.writeObject(fittedVariantList);
		out.writeObject(fittedDisplayableVariantList);
		out.writeBoolean(hasBeenChanged);
		out.writeBoolean(genomeWindowHasChanged);
		out.writeBoolean(xRatioHasChanged);
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
		currentGenomeWindow = (GenomeWindow) in.readObject();
		currentXRatio = in.readDouble();
		genomes = (Map<String, List<VariantType>>) in.readObject();
		fittedVariantList = (List<Variant>) in.readObject();
		fittedDisplayableVariantList = (List<DisplayableVariant>) in.readObject();
		hasBeenChanged = in.readBoolean();
		genomeWindowHasChanged = in.readBoolean();
		xRatioHasChanged = in.readBoolean();
	}


	/**
	 * Constructor of {@link DisplayableVariantListCreator}
	 */
	public DisplayableVariantListCreator () {
		currentGenomeWindow = null;
		currentXRatio = 0d;
		genomes = new HashMap<String, List<VariantType>>();
		hasBeenChanged = false;
		genomeWindowHasChanged = false;
		xRatioHasChanged = false;
	}


	@Override
	public List<DisplayableVariant> getFittedData(GenomeWindow genomeWindow, double xRatio) {
		if ((currentGenomeWindow == null) ||
				(!currentGenomeWindow.getChromosome().equals(genomeWindow.getChromosome()))) {
			genomeWindowHasChanged = true;
		}
		currentGenomeWindow = genomeWindow;

		if (currentXRatio != xRatio) {
			currentXRatio = xRatio;
			xRatioHasChanged = true;
		}

		// If filters or the current chromosome have changed
		if (genomeWindowHasChanged || hasBeenChanged) {
			fittedVariantList = getFittedVariantList();			// creates the list of variants
		}

		//System.out.println("fittedVariantList: " + fittedVariantList.size());

		// If filters or the xRatio have changed
		if (xRatioHasChanged || hasBeenChanged) {
			createDisplayableVariantList(fittedVariantList);	// creates the list of displayable variants
		}

		//System.out.println("fittedDisplayableVariantList: " + fittedDisplayableVariantList.size());

		// Changes indicators come back to false
		genomeWindowHasChanged = false;
		xRatioHasChanged = false;
		hasBeenChanged = false;

		// Return the list of fitted displayable variants
		return getFittedDisplayableVariantList();
	}


	/**
	 * Builds the list of fitted variants.
	 * They are selected according to:
	 * - the requested genomes
	 * - the requested variant type
	 * - the quality
	 * @return the list of fitted variant
	 */
	private List<Variant> getFittedVariantList () {
		// Creates the list of involved variants
		List<Variant> fittedVariantList = new ArrayList<Variant>();

		passFilter = 0;
		notPassFilter = 0;

		// Scan for every required genomes
		for (String genomeFullName: genomes.keySet()) {

			// Gets parameters
			MGChromosomeOld chromosomeInformation = null; //ProjectManager.getInstance().getGenomeSynchronizer().getChromosomeInformation(genomeFullName, currentGenomeWindow.getChromosome());
			Map<Integer, Variant> variants = chromosomeInformation.getPositionInformationList();
			chromosomeInformation.resetIndexList();
			int[] indexes = chromosomeInformation.getPositionIndex();

			// Scan the full variant list with the right indexes
			for (int i = 0; i < indexes.length; i++) {
				Variant current = variants.get(indexes[i]);

				// The variant is added if pertinent
				if (passFilter(genomeFullName, current)) {
					fittedVariantList.add(current);
				}
			}
		}

		//System.out.println(passFilter + " : " + notPassFilter);

		// Sorts the list using the start position on the meta genome coordinates
		Collections.sort(fittedVariantList, new VariantMGPositionComparator());

		return fittedVariantList;
	}


	/**
	 * Creates a complete list of displayable variant according to the X ratio.
	 * @param fittedVariantList list of fitted variant
	 */
	private void createDisplayableVariantList (List<Variant> fittedVariantList) {
		fittedDisplayableVariantList = new ArrayList<DisplayableVariant>();

		if (fittedVariantList.size() > 0) {

			// if it is not necessary to merge stripes
			if (currentXRatio > 1) {
				for (int i = 0; i < fittedVariantList.size(); i++) {
					Variant current = fittedVariantList.get(i);
					DisplayableVariant displayableVariant;
					if (current instanceof VCFSNP) {
						displayableVariant = new SNPDisplayableVariant(current, current.getMetaGenomePosition());
					} else {
						displayableVariant = new RegularDisplayableVariant(fittedVariantList.get(i), current.getMetaGenomePosition(), current.getNextMetaGenomePosition());
					}
					fittedDisplayableVariantList.add(displayableVariant);
				}
			} else {

				boolean isValid = true;
				int index = 0;
				int stopIndex = fittedVariantList.size() - 1;

				while (isValid) {

					// Gets the current variant
					Variant current = fittedVariantList.get(index);

					// Gets the start
					int start = current.getMetaGenomePosition();

					// Gets the stop
					int stop = current.getNextMetaGenomePosition() - 1;
					int nextIndex = index + 1;

					// Checks if it is necessary to merge variants
					boolean hasBeenMerged = false;
					if (nextIndex <= stopIndex) {
						boolean merging = false;												// By default the merging is not necessary.
						double distance = (fittedVariantList.get(nextIndex).getMetaGenomePosition() - stop) * currentXRatio; // Distance between the start position of the next variant and the current stop.

						if (distance < 1) {														// If the distance is smaller than 1 pixel,
							merging = true;														// the merging is required.
							hasBeenMerged = true;
						}

						while (merging) {														// While the merging is required,
							stop = fittedVariantList.get(nextIndex).getNextMetaGenomePosition();	// the new stop becomes the stop of the next variant,
							nextIndex++;
							if (nextIndex <= stopIndex) {										// if a next variant exists,
								distance = (fittedVariantList.get(nextIndex).getMetaGenomePosition() - stop) * currentXRatio; // the new distance is calculated,
								if (distance >= 1) {											// if the distance is greater/equal than 1 pixel,
									merging = false;											// the merging is not necessary,
								}
							} else {															// if there is no next variant,
								isValid = false;												// the loop is not valid anymore,
								merging = false;												// and the merging cannot be performed.
							}
						}

					} else {
						isValid = false;
					}

					// Creates the displayable variant
					DisplayableVariant displayableVariant;
					if (hasBeenMerged) {
						displayableVariant = new MIXDisplayableVariant(start, stop);
					} else {
						if (current instanceof VCFSNP) {
							displayableVariant = new SNPDisplayableVariant(current, current.getMetaGenomePosition());
						} else {
							displayableVariant = new RegularDisplayableVariant(current, start, stop);
						}
					}

					// Adds the displayable variant
					fittedDisplayableVariantList.add(displayableVariant);

					// Goes to the next index if it is valid
					if (nextIndex > stopIndex) {
						isValid = false;
					} else {
						index = nextIndex;
					}
				}
			}
		}
	}


	/**
	 * @return the fitted list of displayable variant
	 */
	private List<DisplayableVariant> getFittedDisplayableVariantList () {
		List<DisplayableVariant> variantList = new ArrayList<DisplayableVariant>();

		if (fittedDisplayableVariantList.size() > 0) {

			// Start and stop meta genome position on the screen
			final int screenStart = currentGenomeWindow.getStart();
			final int screenStop = currentGenomeWindow.getStop();

			// Gets variant boundaries indexes according to the screen position
			int startIndex = findStart();
			int stopIndex = findStop();

			// Loops to add displayable variants from the full list to the fitted list
			for (int i = startIndex; i <= stopIndex; i++) {
				DisplayableVariant current = fittedDisplayableVariantList.get(i);

				// Gets the start
				int currentStart = current.getStart();
				int currentStop = current.getStop();

				// Solve bug for displaying stripes at the edge of the screen
				boolean edgeBug = false;
				if (currentStart < screenStart && currentStop > screenStart) {
					edgeBug = true;
					currentStart = screenStart;
					if (currentStop > screenStop) {
						currentStop = screenStop;
					}
				} else if (currentStart < screenStop && currentStop > screenStop) {
					edgeBug = true;
					currentStop = screenStop;
					if (currentStart < screenStart) {
						currentStart = screenStart;
					}
				}

				// If bug appeared
				if (edgeBug) {
					DisplayableVariant newDisplayableVariant = null;
					if (current instanceof RegularDisplayableVariant) {
						newDisplayableVariant = new RegularDisplayableVariant(current.getNativeVariant(), currentStart, currentStop);
					} else if (current instanceof MIXDisplayableVariant) {
						newDisplayableVariant = new MIXDisplayableVariant(currentStart, currentStop);
					} else if (current instanceof SNPDisplayableVariant) {
						newDisplayableVariant = new SNPDisplayableVariant(current.getNativeVariant(), currentStart);
					}
					variantList.add(newDisplayableVariant);	// new start/stop have to be taken in account
				} else { // if not
					variantList.add(current); //the current displayable variant is used
				}
			}
		}

		return variantList;
	}


	/**
	 * @return the start index
	 */
	private int findStart () {
		int start = getIndex(currentGenomeWindow.getStart(), 0, fittedDisplayableVariantList.size()); 
		start--;
		if (start < 0) {
			start = 0;
		}
		return start;
	}


	/**
	 * @return the stop index
	 */
	private int findStop () {
		int stop = getIndex(currentGenomeWindow.getStop(), 0, fittedDisplayableVariantList.size());
		if (stop >= fittedDisplayableVariantList.size()) {
			stop = fittedDisplayableVariantList.size() - 1;
		}
		return stop;
	}


	/**
	 * @param value meta genome position
	 * @param indexStart index to start
	 * @param indexStop index to stop
	 * @return the index
	 */
	private int getIndex(int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == fittedDisplayableVariantList.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > fittedDisplayableVariantList.get(indexStart + middle).getStart()) {
			return getIndex(value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Checks if the variant passes filters:
	 * - variant type
	 * - quality
	 * @param variant the variant
	 * @return	true if the variant is correct, false if not
	 */
	private boolean passFilter (String genomeFullName, Variant variant) {
		boolean result = true;

		if (!genomes.get(genomeFullName).contains(variant.getType())) {
			result = false;
		}

		if (result) {
			for (IDFilterInterface data: filters) {
				if (!data.passFilter(genomeFullName, variant)) {
					result = false;
					break;
				}
			}
		}

		if (result) {
			passFilter++;
		} else {
			notPassFilter++;
		}

		return result;
	}


	/**
	 * Sets the full genome names and checks if they are different.
	 * @param genomes the full genome names to set
	 */
	public void setGenomeNames(Map<String, List<VariantType>> genomes) {
		/*String info = "";
		for (String name: genomes.keySet()) {
			info += name + ":";
			for (VariantType type: genomes.get(name)) {
				info += " " + type;
			}
			info += "\n";
		}
		System.out.println(info);*/

		if (setsAreDifferents(this.genomes.keySet(), genomes.keySet())) {
			hasBeenChanged = true;
		} else {
			for (String genomeName: this.genomes.keySet()) {
				if (listAreDifferents(this.genomes.get(genomeName), genomes.get(genomeName))) {
					hasBeenChanged = true;
					break;
				}
			}
		}
		this.genomes = genomes;
	}


	/**
	 * Checks if two sets of String are strictly similar or not 
	 * @param set1	the first set of String
	 * @param set2	the second set of String
	 * @return		true if they are not similar, false if they are
	 */
	private boolean setsAreDifferents (Set<String> set1, Set<String> set2) {
		if (set1.size() != set2.size()) {
			return true;
		} else {
			for (String name: set1) {
				if (!set2.contains(name)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Checks if two lists of VCFType are strictly similar or not 
	 * @param list1
	 * @param list2
	 * @return true if the specified list are different
	 */
	private boolean listAreDifferents (List<VariantType> list1, List<VariantType> list2) {
		if (list1.size() != list2.size()) {
			return true;
		} else {
			for (VariantType type: list1) {
				if (!list2.contains(type)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * When SNPs are added/removed, the list must be re-created.
	 */
	public void SNPUpdate () {
		hasBeenChanged = true;
	}


	/**
	 * @return the full fitted list of displayable variant
	 */
	public List<DisplayableVariant> getFullDisplayableVariantList () {
		return fittedDisplayableVariantList;
	}


	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<IDFilterInterface> filters) {
		this.filters = filters;
		hasBeenChanged = true;
	}


}
