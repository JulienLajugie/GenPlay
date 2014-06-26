/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer.variantLayer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.yu.einstein.genplay.core.comparator.ListComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayList;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayMultiListScanner;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.FiltersData;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.VariantLayerDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation.VariantInformationDialog;


/**
 * @author Nicolas Fourel
 */
public class MultiGenomeDrawer implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -6660557171751127841L;
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0; // saved format version

	private MultiGenomeVariantDrawer 		variantDrawer;
	private MultiGenomeListHandler			handler;

	private List<VariantInformationDialog> 	variantDialogs;
	private Chromosome 						chromosome;
	private VCFFileStatistics 				statistics;

	private List<VariantLayerDisplaySettings> 				variantDataList;
	private List<MGFilter> 					filtersList;
	private List<VariantDisplayList> 		variantDisplayList;

	private boolean 						showReference;
	private boolean 						showFilter;
	private boolean 						forceFitToScreen;
	private boolean 						locked;
	private Variant 						variantUnderMouse;


	/**
	 * Constructor of {@link MultiGenomeDrawer}
	 */
	public MultiGenomeDrawer () {
		variantDataList = new ArrayList<VariantLayerDisplaySettings>();
		variantDisplayList = new ArrayList<VariantDisplayList>();
		forceFitToScreen = false;
		locked = false;
		statistics = null;
		variantDrawer = new MultiGenomeVariantDrawer(this);
		handler = new MultiGenomeListHandler();
		variantDialogs = new ArrayList<VariantInformationDialog>();
		variantUnderMouse = null;
		chromosome = getCurrentChromosome();
		MGDisplaySettings displaySettings = MGDisplaySettings.getInstance();
		showFilter = displaySettings.includeFilteredVariants();
		showReference = displaySettings.includeReferences();
	}


	/**
	 * Draws stripes showing information for multi genome. The method checks if the track must show both allele or only one, in order to split the track or not.
	 * @param g graphics object
	 * @param width
	 * @param height
	 */
	public void drawMultiGenomeInformation(Graphics g, int width, int height) {
		if ((variantDataList != null) && (variantDataList.size() > 0)) {
			variantDrawer.initializeStripesOpacity();
			if (!locked) { // if there are stripes
				GenomeWindow genomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
				double xRatio = ProjectManager.getInstance().getProjectWindow().getXRatio();
				int halfHeight = height / 2; 																	// calculates the half of the height track
				Graphics allele01Graphic = g.create(0, 0, width, halfHeight); 									// create a graphics for the first allele that correspond to the upper half of the track
				Graphics2D allele02Graphic = (Graphics2D) g.create(0, halfHeight, width, halfHeight); 			// create a 2D graphics for the second allele that correspond to the lower
				// half of the track
				allele02Graphic.scale(1, -1); 																	// all Y axis (vertical) coordinates must be reversed for the second allele
				allele02Graphic.translate(0, -halfHeight); 														// translates all coordinates of the graphic for the second allele
				if (forceFitToScreen) {
					handler.forceFitToScreen(xRatio);
					forceFitToScreen = false;
				}
				variantDrawer.setCurrentAllele(AlleleType.ALLELE01);
				variantDrawer.drawGenome(allele01Graphic, width, halfHeight, genomeWindow, handler.getFittedData(genomeWindow, xRatio, 0)); // draw the stripes for the first allele
				variantDrawer.setCurrentAllele(AlleleType.ALLELE02);
				variantDrawer.drawGenome(allele02Graphic, width, halfHeight, genomeWindow, handler.getFittedData(genomeWindow, xRatio, 1)); // draw the stripes for the second allele
				variantDrawer.drawMultiGenomeLine(g, width, height); // draw a line in the middle of the track to distinguish upper and lower half.
			} else {
				variantDrawer.drawMultiGenomeMask(g, height, "Multi genome display interrupted while loading information");
			}
		}
	}


	/**
	 * @return the current chromosome
	 */
	private Chromosome getCurrentChromosome() {
		return ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getChromosome();
	}


	/**
	 * @return the list of {@link MGFilter}
	 */
	public List<MGFilter> getFiltersList() {
		return filtersList;
	}


	/**
	 * @return the {@link VCFFileStatistics} of the track
	 */
	public VCFFileStatistics getStatistics() {
		return statistics;
	}


	/**
	 * @return the list of {@link VariantLayerDisplaySettings}
	 */
	public List<VariantLayerDisplaySettings> getVariantDataList() {
		return variantDataList;
	}



	/**
	 * This method does not process anything, only return the {@link Variant} that has been found earlier!
	 * @return the {@link Variant} under the mouse.
	 */
	public Variant getVariantUnderMouse () {
		return variantUnderMouse;
	}


	/**
	 * Scans lists and processes data in order to retrieve the {@link Variant} that is under the pointer of the mouse.
	 * @param height height of the track
	 * @param x x position of the pointer
	 * @param y y position of the pointer
	 * @return the {@link Variant} under the mouse, null if there is no {@link Variant}.
	 */
	private Variant getVariantUnderMouse (int trackHeight, int x, int y) {
		// Get the allele index
		int alleleIndex = 0;
		if (y > (trackHeight / 2)) {
			alleleIndex = 1;
		}

		// Get the meta genome position
		int pos = ProjectManager.getInstance().getProjectWindow().screenToGenomePosition(x); // we translate the position on the screen into a position on the genome

		// Get the right list of variants in the area of the actual position
		List<Variant> results = new ArrayList<Variant>();
		for (VariantDisplayList list: variantDisplayList) {
			List<Variant> result = list.getVariantsInArea(alleleIndex, pos);
			if ((result != null) && !result.isEmpty()) {
				results.addAll(result);
			}
		}

		// If at least one variant has been found
		if (results.size() > 0) {
			// Sort the list by score
			Collections.sort(results, new Comparator<Variant>() {
				@Override
				public int compare(Variant o1, Variant o2) {
					if (o1.getScore() < o2.getScore()) {
						return -1;
					} else if (o1.getScore() > o2.getScore()) {
						return 1;
					}
					return 0;
				}
			});

			int clipHeight = trackHeight / 2;
			int index = 0;
			int size = results.size();
			boolean found = false;
			while (!found && (index < size)) {
				Variant variant = results.get(index);
				int height = variantDrawer.getVariantHeight(variant, clipHeight);
				int yVariant = clipHeight - height;
				if (alleleIndex == 0) {
					if (y >= yVariant) {
						found = true;
					}
				} else {
					y -= clipHeight;
					if (y <= height) {
						found = true;
					}
				}
				if (!found) {
					index++;
				}
			}
			if (found) {
				return results.get(index);
			} else {
				return results.get(0);
			}
		}
		return null;
	}


	/**
	 * Compare the current chromosome and the chromosome used for the stripes/filter
	 * @return true if the chromosome has changed, false otherwise
	 */
	private boolean hasChromosomeChanged() {
		if (chromosome == null) {
			return true;
		}
		return !chromosome.equals(getCurrentChromosome());
	}


	/**
	 * Compare given multi genome information with the current ones.
	 * @param stripesList the new stripes list
	 * @param filtersList the new filters list
	 * @return true if new information are different than the current ones
	 */
	public boolean hasMultiGenomeInformationChanged(List<VariantLayerDisplaySettings> stripesList, List<MGFilter> filtersList) {
		if (haveVariantsChanged(stripesList) || haveFiltersChanged(filtersList)) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Checks if the track has to be repaint when the mouse exit from it. Basically, it has to be repaint if a variant was under the mouse in order to not highlight it.
	 * @return true if the track has to be repaint, false otherwise
	 */
	public boolean hasToBeRepaintAfterExit() {
		if (variantUnderMouse != null) {
			variantUnderMouse = null;
			return true;
		}
		return false;
	}


	private boolean hasToReset (List<VariantLayerDisplaySettings> variantDataList) {
		if ((this.variantDataList.size() > 0) && (variantDataList.size() == 0)) {
			return true;
		}
		return false;
	}


	/**
	 * Compare given filters information with the current one.
	 * @param filtersList the new filters list
	 * @return true if new information are different than the current ones
	 */
	private boolean haveFiltersChanged(List<MGFilter> filtersList) {
		ListComparator<MGFilter> filtersComparator = new ListComparator<MGFilter>();
		return filtersComparator.areDifferent(this.filtersList, filtersList);
	}


	/**
	 * Check if display major options have changed
	 * @return true if display major options have changed, false otherwise
	 */
	private boolean haveOptionsChanged() {
		boolean showReference = MGDisplaySettings.getInstance().includeReferences();
		boolean showFiltered = MGDisplaySettings.DRAW_FILTERED_VARIANT == MGDisplaySettings.YES_MG_OPTION;

		if ((this.showReference != showReference) || (showFilter != showFiltered)) {
			this.showReference = showReference;
			showFilter = showFiltered;
			return true;
		}

		return false;
	}


	/**
	 * Compare given variant information with the current one.
	 * @param variantDataList the new stripes list
	 * @return true if new information are different than the current ones
	 */
	private boolean haveVariantsChanged(List<VariantLayerDisplaySettings> variantDataList) {
		ListComparator<VariantLayerDisplaySettings> stripesComparator = new ListComparator<VariantLayerDisplaySettings>();
		return stripesComparator.areDifferent(this.variantDataList, variantDataList);
	}


	/**
	 * Look if the mouse is on top of a {@link Variant}, set the related attribute and return the result.
	 * @param height height of the track
	 * @param e the {@link MouseEvent}
	 * @return	true if the mouse is on top of a {@link Variant}, false otherwise
	 */
	public boolean isOverVariant(int height, MouseEvent e) {
		if (ProjectManager.getInstance().isMultiGenomeProject() && !locked) { // if we are in multi genome project
			Variant variant = getVariantUnderMouse(height, e.getX(), e.getY()); // we get the variant (Y is needed to know if the variant is on the upper or lower half of the track)
			if (variant != null) { // if a variant has been found
				variantUnderMouse = variant; // the mouse is on this variant (we save it)
				return true; // we return true
			} else if (variantUnderMouse != null) { // no variant has been found but one was already defined (the mouse is just getting out of the stripe)
				variantUnderMouse = null; // there is no variant under the mouse anymore
				return true;
			}
		}
		return false;
	}


	/**
	 * Disposes all {@link VariantInformationDialog} related to this track
	 */
	private void killStripesDialogs() {
		for (VariantInformationDialog dialog : variantDialogs) {
			dialog.dispose();
		}
		variantDialogs = new ArrayList<VariantInformationDialog>();
	}


	/**
	 * Locks the painting in all methods (used when information is changing)
	 */
	public void lockPainting() {
		locked = true;
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

		variantDrawer = (MultiGenomeVariantDrawer) in.readObject();
		handler = (MultiGenomeListHandler) in.readObject();

		variantDialogs = new ArrayList<VariantInformationDialog>();
		chromosome = (Chromosome) in.readObject();
		statistics = (VCFFileStatistics) in.readObject();


		variantDataList = (List<VariantLayerDisplaySettings>) in.readObject();
		filtersList = (List<MGFilter>) in.readObject();
		variantDisplayList = (List<VariantDisplayList>) in.readObject();

		showReference = in.readBoolean();
		showFilter = in.readBoolean();
		forceFitToScreen = in.readBoolean();
		locked = in.readBoolean();
		variantUnderMouse = (Variant) in.readObject();

		variantDrawer.setDrawer(this);
	}


	/**
	 * Sets the {@link MGFilter} list
	 * @param filterList the new {@link MGFilter} list to use
	 */
	public void setFiltersList(List<MGFilter> filterList) {
		filtersList = filterList;
	}


	/**
	 * Sets the {@link VCFFileStatistics} to use
	 * @param statistics the {@link VCFFileStatistics} to use
	 */
	public void setStatistics(VCFFileStatistics statistics) {
		this.statistics = statistics;
	}


	/**
	 * Sets the {@link VariantLayerDisplaySettings} list
	 * @param variantDataList the new {@link VariantLayerDisplaySettings} list to use
	 */
	public void setVariantDataList(List<VariantLayerDisplaySettings> variantDataList) {
		this.variantDataList = variantDataList;
	}


	/**
	 * Shows the {@link VariantInformationDialog} is the mouse is on top of a {@link Variant}
	 * @param height height of the track
	 * @param e the {@link MouseEvent}
	 */
	public void showVariantInformationDialog(int height, MouseEvent e) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) { // we must be in a multi genome project
			if (variantUnderMouse != null) {
				VariantInformationDialog toolTip = new VariantInformationDialog(this); // we create the information dialog
				variantDialogs.add(toolTip);
				int pos = Math.round(ProjectManager.getInstance().getProjectWindow().screenToGenomePosition(e.getX())) - 1; // we translate the position on the screen into a position on the genome
				VariantDisplayMultiListScanner iterator = new VariantDisplayMultiListScanner(variantDisplayList);
				iterator.initializeDiploide();
				iterator.setPosition(pos);
				iterator.setDisplayDependancy(true);
				toolTip.show(iterator, e.getXOnScreen(), e.getYOnScreen()); // we show it
			}
		}
	}


	/**
	 * Unlocks the painting in all methods (used once information changed)
	 */
	public void unlockPainting() {
		locked = false;
	}


	private boolean updateFilter (List<MGFilter> filtersList) {
		boolean filtersHaveChanged = false;
		if (haveFiltersChanged(filtersList)) {
			filtersHaveChanged = true;
			this.filtersList = filtersList;
		}
		return filtersHaveChanged;
	}


	/**
	 * Update the variant lists
	 * @param variantDataList	the {@link VariantLayerDisplaySettings} settings
	 * @param filtersList		the {@link FiltersData} settings
	 */
	public void updateMultiGenomeInformation(List<VariantLayerDisplaySettings> variantDataList, List<MGFilter> filtersList) {
		boolean generateLists = false;
		boolean filtersHaveChanged = false;

		if (hasToReset(variantDataList)) {
			this.variantDataList = new ArrayList<VariantLayerDisplaySettings>();
			variantDisplayList = new ArrayList<VariantDisplayList>();
			statistics = null;
			variantUnderMouse = null;
			handler.initialize(variantDisplayList);
		} else if (hasChromosomeChanged()) {
			chromosome = getCurrentChromosome();
			generateLists = true;
			if (variantDisplayList.size() > 0) {
				for (VariantDisplayList variantDisplay: variantDisplayList) {
					variantDisplay.generateLists();
				}
			}
		} else {
			// Update variants
			generateLists = updateVariant(variantDataList);

			// Update filters
			filtersHaveChanged = updateFilter(filtersList);
		}

		if (generateLists || filtersHaveChanged) {
			statistics = null;
			for (VariantDisplayList currentList: variantDisplayList) {
				currentList.updateDisplay(filtersList, showFilter);
			}
			handler.initialize(variantDisplayList);
			forceFitToScreen = true;
		}

		boolean haveOptionsChanged = haveOptionsChanged();
		if (haveOptionsChanged) {
			forceFitToScreen = true;
			for (VariantDisplayList currentList: variantDisplayList) {
				currentList.updateDisplayForOption(showReference, showFilter);
			}
		}

		if (generateLists || filtersHaveChanged || haveOptionsChanged) {
			killStripesDialogs();
		}
	}


	private boolean updateVariant (List<VariantLayerDisplaySettings> variantDataList) {
		boolean generateLists = false;

		if ((variantDataList != null) && (variantDataList.size() > 0)) {
			List<VariantLayerDisplaySettings> newVariantDataList = new ArrayList<VariantLayerDisplaySettings>();
			List<VariantDisplayList> newVariantDisplayList = new ArrayList<VariantDisplayList>();

			for (int i = 0; i < variantDataList.size(); i++) {
				VariantLayerDisplaySettings newData = variantDataList.get(i);
				if (newData != null) {
					newVariantDataList.add(newData);
					int dataIndex = this.variantDataList.indexOf(newData);
					if ((dataIndex > -1) && !newData.hasChanged()) {
						newVariantDisplayList.add(variantDisplayList.get(dataIndex));
					} else {
						generateLists = true;
						newData.setHasChanged(false);
						VariantDisplayList newList = new VariantDisplayList();
						newList.initialize(newData.getGenome(), newData.getVariationTypeList());
						newList.generateLists();
						newVariantDisplayList.add(newList);
					}
				}
			}

			this.variantDataList = newVariantDataList;
			variantDisplayList = newVariantDisplayList;
		}

		return generateLists;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);

		out.writeObject(variantDrawer);
		out.writeObject(handler);

		out.writeObject(chromosome);
		out.writeObject(statistics);

		out.writeObject(variantDataList);
		out.writeObject(filtersList);
		out.writeObject(variantDisplayList);

		out.writeBoolean(showReference);
		out.writeBoolean(showFilter);
		out.writeBoolean(forceFitToScreen);
		out.writeBoolean(locked);
		out.writeObject(variantUnderMouse);
	}

}
