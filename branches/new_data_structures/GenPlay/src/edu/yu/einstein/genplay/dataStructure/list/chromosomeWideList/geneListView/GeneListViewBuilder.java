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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView;

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ByteArrayAsBooleanList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfByteArraysAsByteList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfFloatArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfHalfArraysAsFloatList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Implementation of the {@link ListViewBuilder} interface vending
 * {@link GeneListView} objects.
 * @author Julien Lajugie
 */
public final class GeneListViewBuilder implements ListViewBuilder<Gene> {

	/** List of the names of the genes */
	private List<Byte> geneNames;

	/** List of the offsets of the gene names inside the byte list containing the gene names */
	private List<Integer> geneNameOffsets;

	/** List of the strands of the genes. A true value means plus strand, false means minus strand  */
	private List<Boolean> geneStrands;

	/** List of the start positions of the genes */
	private List<Integer> geneStarts;

	/** List of the stop positions of the genes */
	private List<Integer> geneStops;

	/** List of the scores of the genes */
	private List<Float> geneScores;

	/** List of the UTR 5 bounds of the genes */
	private List<Integer> geneUTR5Bounds;

	/** List of the UTR 3 bounds of the genes */
	private List<Integer> geneUTR3Bounds;

	/**  List of the exon start positions */
	private List<Integer> exonStarts;

	/** List of the exon stop positions */
	private List<Integer> exonStops;

	/** List of the exon scores */
	private List<Float> exonScores;

	/** List of the offsets of the exons inside the exon start, stop and score lists */
	private List<Integer> exonOffsets;


	/**
	 * Creates an instance of {@link GeneListViewBuilder}
	 * @param scorePrecision precision of the score of the genes of the {@link ListView} to build
	 */
	public GeneListViewBuilder(ScorePrecision scorePrecision) {
		geneNames = new ListOfByteArraysAsByteList();
		geneNameOffsets = new ListOfIntArraysAsIntegerList();
		geneStrands = new ByteArrayAsBooleanList();
		geneStarts = new ListOfIntArraysAsIntegerList();
		geneStops = new ListOfIntArraysAsIntegerList();
		geneUTR5Bounds = new ListOfIntArraysAsIntegerList();
		geneUTR3Bounds = new ListOfIntArraysAsIntegerList();
		exonStarts = new ListOfIntArraysAsIntegerList();
		exonStops = new ListOfIntArraysAsIntegerList();
		exonOffsets = new ListOfIntArraysAsIntegerList();
		switch (scorePrecision) {
		case PRECISION_16BIT:
			geneScores = new ListOfHalfArraysAsFloatList();
			exonScores = new ListOfHalfArraysAsFloatList();
			break;
		case PRECISION_32BIT:
			geneScores = new ListOfFloatArraysAsFloatList();
			exonScores = new ListOfFloatArraysAsFloatList();
			break;
		}
	}


	@Override
	public void addElementToBuild(Gene gene) throws ObjectAlreadyBuiltException {
		if (geneStarts == null) {
			throw new ObjectAlreadyBuiltException();
		} else {
			// add gene name offset
			geneNameOffsets.add(geneNames.size());
			// add the gene name
			byte[] geneName = gene.getName().getBytes();
			for (int i = 0; i < geneName.length; i++) {
				geneNames.add(geneName[i]);
			}
			// add strand
			geneStrands.add(gene.getStrand() == Strand.FIVE);
			// add gene start, stop, score, UTR5 and UTR3
			geneStarts.add(gene.getStart());
			geneStops.add(gene.getStop());
			geneScores.add(gene.getScore());
			geneUTR5Bounds.add(gene.getUTR5Bound());
			geneUTR3Bounds.add(gene.getUTR3Bound());
			// add exon offset
			exonOffsets.add(exonStarts.size());
			// add exon
			for (ScoredChromosomeWindow currentExon: gene.getExons()) {
				exonStarts.add(currentExon.getStart());
				exonStops.add(currentExon.getStop());
				exonScores.add(currentExon.getScore());
			}
		}
	}


	@Override
	public ListView<Gene> getListView() {
		ListView<Gene> listView = new GeneListView(
				geneNames,
				geneNameOffsets,
				geneStrands,
				geneStarts,
				geneStops,
				geneScores,
				geneUTR5Bounds,
				geneUTR3Bounds,
				exonStarts,
				exonStops,
				exonScores,
				exonOffsets
				);
		geneNames = null;
		geneNameOffsets = null;
		geneStrands = null;
		geneStarts = null;
		geneStops = null;
		geneScores = null;
		geneUTR5Bounds = null;
		geneUTR3Bounds = null;
		exonStarts = null;
		exonStops = null;
		exonScores = null;
		exonOffsets = null;
		return listView;
	}
}
