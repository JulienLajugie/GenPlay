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
package edu.yu.einstein.genplay.gui.track.drawer.multiGenome;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.ListComparator;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayList;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayMultiListScanner;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation.VariantInformationDialog;
import edu.yu.einstein.genplay.gui.track.TrackGraphics;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeDrawer {

	private List<VariantData> variantDataList;
	private List<MGFilter> filtersList;
	private List<VariantDisplayList> variantDisplayList;
	private boolean showReference;
	private boolean showFilter;
	private boolean forceFitToScreen;
	private boolean locked;
	private VCFFileStatistics statistics;

	private final MultiGenomeVariantDrawer variantDrawer;
	private final MultiGenomeListHandler handler;
	private List<VariantInformationDialog> variantDialogs;
	private Variant variantUnderMouse;
	private Chromosome chromosome;


	/**
	 * Constructor of {@link MultiGenomeDrawer}
	 */
	public MultiGenomeDrawer () {
		variantDataList = new ArrayList<VariantData>();
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
	 * Update the variant lists
	 * @param variantDataList	the {@link VariantData} settings
	 * @param filtersList		the {@link FiltersData} settings
	 */
	public void updateMultiGenomeInformation(List<VariantData> variantDataList, List<MGFilter> filtersList) {
		boolean generateLists = false;
		boolean filtersHaveChanged = false;

		if (hasToReset(variantDataList)) {
			this.variantDataList = new ArrayList<VariantData>();
			variantDisplayList = new ArrayList<VariantDisplayList>();
			statistics = null;
			variantUnderMouse = null;
			handler.initialize(variantDisplayList);
		} else if (hasChromosomeChanged()) {
			this.chromosome = getCurrentChromosome();
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
		if (haveOptionsChanged()) {
			forceFitToScreen = true;
			for (VariantDisplayList currentList: variantDisplayList) {
				currentList.updateDisplayForOption(showReference, showFilter);
			}
		}
	}


	private boolean hasToReset (List<VariantData> variantDataList) {
		if ((this.variantDataList.size() > 0) && (variantDataList.size() == 0)) {
			return true;
		}
		return false;
	}


	private boolean updateVariant (List<VariantData> variantDataList) {
		boolean generateLists = false;

		if ((variantDataList != null) && (variantDataList.size() > 0)) {
			List<VariantData> newVariantDataList = new ArrayList<VariantData>();
			List<VariantDisplayList> newVariantDisplayList = new ArrayList<VariantDisplayList>();

			for (int i = 0; i < variantDataList.size(); i++) {
				VariantData newData = variantDataList.get(i);
				newVariantDataList.add(newData);
				int dataIndex = this.variantDataList.indexOf(newData);
				if (dataIndex > -1) {
					newVariantDisplayList.add(this.variantDisplayList.get(dataIndex));
				} else {
					generateLists = true;
					VariantDisplayList newList = new VariantDisplayList();
					newList.initialize(newData.getGenome(), newData.getVariationTypeList());
					newList.generateLists();
					newVariantDisplayList.add(newList);
				}
			}

			this.variantDataList = newVariantDataList;
			this.variantDisplayList = newVariantDisplayList;
		}

		return generateLists;
	}



	private boolean updateFilter (List<MGFilter> filtersList) {
		boolean filtersHaveChanged = false;
		if (haveFiltersChanged(filtersList)) {
			filtersHaveChanged = true;
			this.filtersList = filtersList;
		}
		return filtersHaveChanged;
	}


	///////////////////////////////////////////////////////////////////// Information comparison
	/**
	 * Compare given multi genome information with the current ones.
	 * @param stripesList the new stripes list
	 * @param filtersList the new filters list
	 * @return true if new information are different than the current ones
	 */
	public boolean hasMultiGenomeInformationChanged(List<VariantData> stripesList, List<MGFilter> filtersList) {
		if (haveVariantsChanged(stripesList) || haveFiltersChanged(filtersList)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compare given variant information with the current one.
	 * @param variantDataList the new stripes list
	 * @return true if new information are different than the current ones
	 */
	private boolean haveVariantsChanged(List<VariantData> variantDataList) {
		ListComparator<VariantData> stripesComparator = new ListComparator<VariantData>();
		return stripesComparator.areDifferent(this.variantDataList, variantDataList);
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
	 * Check if display major options have changed
	 * @return true if display major options have changed, false otherwise
	 */
	private boolean haveOptionsChanged() {
		boolean showReference = MGDisplaySettings.getInstance().includeReferences();
		boolean showFiltered = MGDisplaySettings.DRAW_FILTERED_VARIANT == MGDisplaySettings.YES_MG_OPTION;

		if ((this.showReference != showReference) || (this.showFilter != showFiltered)) {
			this.showReference = showReference;
			this.showFilter = showFiltered;
			return true;
		}

		return false;
	}

	/**
	 * @return the current chromosome
	 */
	private Chromosome getCurrentChromosome() {
		return ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
	}
	/////////////////////////////////////////////////////////////////////



	public void resetVariantListMaker() {
	}

	public List<VariantData> getStripesList() {
		return variantDataList;
	}

	public List<MGFilter> getFiltersList() {
		return filtersList;
	}


	/**
	 * Draws stripes showing information for multi genome. The method checks if the track must show both allele or only one, in order to split the track or not.
	 * @param g graphics object
	 * @param genomeWindow the genome window
	 * @param xFactor the x factor
	 */
	public void drawMultiGenomeInformation(Graphics g, GenomeWindow genomeWindow, double xFactor) {
		if ((variantDataList != null) && (variantDataList.size() > 0)) {
			variantDrawer.initializeStripesOpacity();
			/*
			 * if (genomeWindow.getSize() > 1000000) { stripesDrawer.drawMultiGenomeMask(g, "Multi genome information cannot be displayed at this zoom level."); } else {
			 */
			if (!locked) { // if there are stripes
				int halfHeight = g.getClipBounds().height / 2; // calculates the half of the height track
				Graphics allele01Graphic = g.create(0, 0, g.getClipBounds().width, halfHeight); // create a graphics for the first allele that correspond to the upper half of the track
				Graphics2D allele02Graphic = (Graphics2D) g.create(0, halfHeight, g.getClipBounds().width, halfHeight); // create a 2D graphics for the second allele that correspond to the lower
				// half of the track
				allele02Graphic.scale(1, -1); // all Y axis (vertical) coordinates must be reversed for the second allele
				allele02Graphic.translate(0, -allele02Graphic.getClipBounds().height - 1); // translates all coordinates of the graphic for the second allele
				if (forceFitToScreen) {
					handler.forceFitToScreen(xFactor);
					forceFitToScreen = false;
				}
				variantDrawer.setCurrentAllele(AlleleType.ALLELE01);
				variantDrawer.drawGenome(allele01Graphic, genomeWindow, handler.getFittedData(genomeWindow, xFactor, 0)); // draw the stripes for the first allele
				variantDrawer.setCurrentAllele(AlleleType.ALLELE02);
				variantDrawer.drawGenome(allele02Graphic, genomeWindow, handler.getFittedData(genomeWindow, xFactor, 1)); // draw the stripes for the second allele
				variantDrawer.drawMultiGenomeLine(g); // draw a line in the middle of the track to distinguish upper and lower half.
				//}
			} else {
				variantDrawer.drawMultiGenomeMask(g, "Multi genome display interupted while loading information.");
			}
			// }
		}
	}

	public void toolTipStripe(int height, MouseEvent e) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) { // we must be in a multi genome project
			if (isOverVariant(height, e)) {
				VariantInformationDialog toolTip = new VariantInformationDialog(this); // we create the information dialog
				variantDialogs.add(toolTip);
				int pos = (int) Math.round(ProjectManager.getInstance().getProjectWindow().screenXPosToGenomePos(TrackGraphics.getTrackGraphicsWidth(), e.getX())); // we translate the position on the screen into a position on the genome
				VariantDisplayMultiListScanner iterator = new VariantDisplayMultiListScanner(variantDisplayList);
				iterator.initializeDiploide();
				iterator.setPosition(pos);
				iterator.setDisplayDependancy(true);
				toolTip.show(iterator, e.getXOnScreen(), e.getYOnScreen()); // we show it
			}
		}
	}

	public boolean hasToBeRepaintAfterExit() {
		// TODO Auto-generated method stub
		return false;
	}

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



	private Variant getVariantUnderMouse (int trackHeight, int x, int y) {
		// Get the allele index
		int alleleIndex = 0;
		if (y > (trackHeight / 2)) {
			alleleIndex = 1;
		}

		// Get the meta genome position
		int pos = (int) Math.round(ProjectManager.getInstance().getProjectWindow().screenXPosToGenomePos(TrackGraphics.getTrackGraphicsWidth(), x)); // we translate the position on the screen into a position on the genome

		List<Variant> results = new ArrayList<Variant>();
		for (VariantDisplayList list: variantDisplayList) {
			Variant result = list.getVariant(alleleIndex, pos);
			if (result != null) {
				results.add(result);
			}
		}

		if (results.size() > 0) {
			return results.get(0);
		}
		return null;
	}


	public Variant getVariantUnderMouse () {
		return variantUnderMouse;
	}


	public void setVariantDataList(List<VariantData> stripeList) {
		// TODO Auto-generated method stub

	}

	public void setFiltersList(List<MGFilter> filterList) {
		// TODO Auto-generated method stub

	}

	public void lockPainting() {
		locked = true;
	}

	public void unlockPainting() {
		locked = false;
	}

	public VCFFileStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(VCFFileStatistics statistics) {
		this.statistics = statistics;
	}



	public boolean isVariantShown(Variant variant) {
		// TODO Auto-generated method stub
		return false;
	}


	private void killStripesDialogs() {
		for (VariantInformationDialog dialog : variantDialogs) {
			dialog.dispose();
		}
		variantDialogs = new ArrayList<VariantInformationDialog>();
	}

}
