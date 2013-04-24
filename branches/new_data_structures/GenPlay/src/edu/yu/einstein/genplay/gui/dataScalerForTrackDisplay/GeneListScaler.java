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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ListView.ChromosomeWindowListViews;


/**
 * This class scales a {@link GeneList} to be displayed on a track.
 * @author Julien Lajugie
 */
public class GeneListScaler implements DataScalerForTrackDisplay<GeneList, List<ListView<Gene>>> {

	/** The name of the genes are printed if the ratio is higher than this value */
	public static final double MIN_X_RATIO_PRINT_NAME = 0.0005d;

	/** Minimum distance in pixel between two genes */
	private static final int MIN_DISTANCE_BETWEEN_2_GENES = 5;

	/** Scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** The gene list organized in lines scaled for a specified chromosome and xRatio */
	private List<ListView<Gene>> scaledGeneList;

	/** Data to be scaled for track display */
	private final GeneList dataToScale;

	/** Dimension of the font used to print the name of the genes */
	private final FontMetrics fontMetrics;


	/**
	 * Creates an instance of {@link GeneListScaler}
	 * @param geneList gene list to scale
	 * @param fontMetrics font metrics of the track
	 */
	public GeneListScaler(GeneList geneList, FontMetrics fontMetrics) {
		scaledChromosome = null;
		scaledXRatio = -1;
		scaledGeneList = null;
		dataToScale = geneList;
		this.fontMetrics = fontMetrics;
	}


	@Override
	public List<ListView<Gene>> getDataScaledForTrackDisplay() {
		GenomeWindow projectWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		double projectXRatio = ProjectManager.getInstance().getProjectWindow().getXRatio();
		// if the chromosome or the xRatio of the project window changed we need to rescale the data
		if (!projectWindow.getChromosome().equals(scaledChromosome) || (projectXRatio != scaledXRatio)) {
			scaledChromosome = projectWindow.getChromosome();
			scaledXRatio = projectXRatio;
			scaleChromosome();
		}
		if (scaledGeneList == null) {
			return null;
		}
		List<ListView<Gene>> resultList = new ArrayList<ListView<Gene>>();
		// search genes for each line
		for (ListView<Gene> currentLine : scaledGeneList) {
			// retrieve the sublist of genes that are located between the start and stop displayed positions
			ListView<Gene> lineToAdd = ChromosomeWindowListViews.sublist(currentLine, projectWindow.getStart(), projectWindow.getStop());
			resultList.add(lineToAdd);
		}
		return resultList;
	}


	@Override
	public GeneList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Organizes the list of genes by line so two genes don't overlap on the screen.
	 */
	protected void scaleChromosome() {
		ListView<Gene> currentList;
		scaledGeneList = null;
		try {
			currentList = dataToScale.get(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			scaledChromosome = null;
			return;
		}

		if ((currentList == null) || currentList.isEmpty()) {
			return;
		}
		scaledGeneList = new ArrayList<ListView<Gene>>();
		// how many genes have been organized
		int organizedGeneCount = 0;
		// which genes have already been selected and organized
		boolean[] organizedGenes = new boolean[currentList.size()];
		Arrays.fill(organizedGenes, false);
		// check if we need to print the gene names at the current scale
		boolean isGeneNamePrinted = (scaledXRatio > MIN_X_RATIO_PRINT_NAME) && (fontMetrics != null);
		ProjectWindow pw = ProjectManager.getInstance().getProjectWindow();
		// loop until every gene has been organized
		while (organizedGeneCount < currentList.size()) {
			GeneListViewBuilder geneLVBuilder = new GeneListViewBuilder();
			Gene previousGene = null;
			// we loop on the gene list
			for (int i = 0; i < currentList.size(); i++) {
				// if the current gene has not been organized yet
				if (!organizedGenes[i]) {
					// if the current line is empty we add the current gene
					if (previousGene == null) {
						previousGene = currentList.get(i);
						geneLVBuilder.addElementToBuild(currentList.get(i));
						organizedGenes[i] = true;
						organizedGeneCount++;
					} else {
						long currentStart = pw.genomeToAbsoluteScreenPosition(currentList.get(i).getStart());
						long previousStop;
						// if we don't print the gene names the previous stop is the stop position of the gene + the minimum length between two genes
						if (!isGeneNamePrinted) {
							previousStop = pw.genomeToAbsoluteScreenPosition(previousGene.getStop()) + MIN_DISTANCE_BETWEEN_2_GENES;
						} else { // if we print the name the previous stop is the max between the stop of the gene and the end position of the name of the gene (+ MIN_DISTANCE_BETWEEN_2_GENES in both case)
							long previousNameStop = fontMetrics.stringWidth(previousGene.getName()) + pw.genomeToAbsoluteScreenPosition(previousGene.getStart());
							long previousGeneStop = pw.genomeToAbsoluteScreenPosition(previousGene.getStop());
							previousStop = Math.max(previousNameStop, previousGeneStop);
							previousStop += MIN_DISTANCE_BETWEEN_2_GENES;
						}
						// if the current gene won't overlap with the previous one we add it to the current line of the list of organized genes
						if (currentStart > previousStop) {
							previousGene = currentList.get(i);
							geneLVBuilder.addElementToBuild(previousGene);
							organizedGenes[i] = true;
							organizedGeneCount++;
						}
					}
				}
			}
			scaledGeneList.add(geneLVBuilder.getListView());
		}
	}
}
