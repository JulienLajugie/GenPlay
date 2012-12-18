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
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeDrawer {

	private List<VariantData> variantDataList;
	private List<MGFilter> filtersList;
	private List<VariantDisplayList> variantDisplayList;
	private boolean showReference;
	private boolean showFiltered;
	private boolean locked;
	private VCFFileStatistics statistics;

	private final MultiGenomeVariantDrawer variantDrawer;

	private Chromosome chromosome;


	public MultiGenomeDrawer () {
		variantDataList = new ArrayList<VariantData>();
		variantDisplayList = new ArrayList<VariantDisplayList>();
		locked = false;
		statistics = null;
		variantDrawer = new MultiGenomeVariantDrawer(this);
	}



	public void updateMultiGenomeInformation(List<VariantData> variantDataList, List<MGFilter> filtersList) {
		boolean generateLists = false;
		boolean filtersHaveChanged = false;


		if (hasChromosomeChanged()) {
			this.chromosome = getCurrentChromosome();
			generateLists = true;
			if (variantDisplayList.size() > 0) {
				for (VariantDisplayList variantDisplay: variantDisplayList) {
					variantDisplay.generateLists();
				}
			} else {
				// Update variants
				generateLists = updateVariant(variantDataList);
			}
		} else {
			// Update variants
			generateLists = updateVariant(variantDataList);

			// Update filters
			filtersHaveChanged = updateFilter(filtersList);
		}


		if (generateLists || filtersHaveChanged || haveOptionsChanged()) {
			statistics = null;
			for (VariantDisplayList currentList: variantDisplayList) {
				currentList.updateDisplay(filtersList);
			}
		}
	}



	private boolean updateVariant (List<VariantData> variantDataList) {
		boolean generateLists = false;
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

		if ((this.showReference != showReference) || (this.showFiltered != showFiltered)) {
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
				for (VariantDisplayList displayList: variantDisplayList) {
					variantDrawer.setCurrentAllele(AlleleType.ALLELE01);
					variantDrawer.drawGenome(allele01Graphic, genomeWindow, displayList.getIterator(0), displayList.getGenomeName()); // draw the stripes for the first allele
					variantDrawer.setCurrentAllele(AlleleType.ALLELE02);
					variantDrawer.drawGenome(allele02Graphic, genomeWindow, displayList.getIterator(1), displayList.getGenomeName()); // draw the stripes for the second allele
					variantDrawer.drawMultiGenomeLine(g); // draw a line in the middle of the track to distinguish upper and lower half.
				}
			} else {
				variantDrawer.drawMultiGenomeMask(g, "Multi genome display interupted while loading information.");
			}
			// }
		}
	}

	public void toolTipStripe(int height, MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean hasToBeRepaintAfterExit() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOverVariant(int height, MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
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

}
