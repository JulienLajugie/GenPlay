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

import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.ByteArrayAsBooleanList;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.FloatListFactory;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.ListOfByteArraysAsByteList;
import edu.yu.einstein.genplay.dataStructure.list.primitiveList.ListOfIntArraysAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
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

	/**  Build the list of exons*/
	private GenericSCWListViewBuilder exonLVBuilder;

	/** Index of the last exon added */
	private int lastExonAddedIndex = 0;

	/** List of the offsets of the exons inside the exon start, stop and score lists */
	private List<Integer> exonOffsets;


	/**
	 * Creates an instance of {@link GeneListViewBuilder}
	 */
	public GeneListViewBuilder() {
		geneNames = new ListOfByteArraysAsByteList();
		geneNameOffsets = new ListOfIntArraysAsIntegerList();
		geneStrands = new ByteArrayAsBooleanList();
		geneStarts = new ListOfIntArraysAsIntegerList();
		geneStops = new ListOfIntArraysAsIntegerList();
		geneUTR5Bounds = new ListOfIntArraysAsIntegerList();
		geneUTR3Bounds = new ListOfIntArraysAsIntegerList();
		exonLVBuilder = new GenericSCWListViewBuilder();
		exonOffsets = new ListOfIntArraysAsIntegerList();
		geneScores = FloatListFactory.createFloatList();
	}


	@Override
	public void addElementToBuild(Gene gene) throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		addElementToBuild(
				gene.getName(),
				gene.getStrand(),
				gene.getStart(),
				gene.getStop(),
				gene.getScore(),
				gene.getUTR5Bound(),
				gene.getUTR3Bound(),
				gene.getExons()
				);
	}


	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable,
	 * this method will throw an exception if called after
	 * the getListView() has been called.
	 * Checks that the elements are added in start position order.
	 * @param geneName name of the gene to add
	 * @param geneStrand strand of the gene to add
	 * @param geneStart start position of the gene to add
	 * @param geneStop stop position of the gene to add
	 * @param geneScore score value of the gene to add
	 * @param geneUTR5Bound UTR 5' boundary of the gene to add
	 * @param geneUTR3Bound UTR 3' boundary of the gene to add
	 * @param geneExons exons of the gene to add
	 * @throws ObjectAlreadyBuiltException
	 * @throws ElementAddedNotSortedException If elements are not added in sorted order
	 */
	public void addElementToBuild(
			String geneName,
			Strand geneStrand,
			int geneStart,
			int geneStop,
			float geneScore,
			int geneUTR5Bound,
			int geneUTR3Bound,
			ListView<ScoredChromosomeWindow> geneExons
			) throws ObjectAlreadyBuiltException, ElementAddedNotSortedException {
		if (geneStarts == null) {
			throw new ObjectAlreadyBuiltException();
		}
		if (!geneStarts.isEmpty()) {
			int lastElementIndex = geneStarts.size() -1;
			int lastStart = geneStarts.get(lastElementIndex);
			if (geneStart < lastStart) {
				// case where the element added are not sorted
				throw new ElementAddedNotSortedException();
			}
		}
		// add gene name offset
		geneNameOffsets.add(geneNames.size());
		// add the gene name
		byte[] geneNameBytes = geneName.getBytes();
		for (int i = 0; i < geneNameBytes.length; i++) {
			geneNames.add(geneNameBytes[i]);
		}
		// add strand
		geneStrands.add(geneStrand == Strand.FIVE);
		// add gene start, stop, score, UTR5 and UTR3
		geneStarts.add(geneStart);
		geneStops.add(geneStop);
		geneScores.add(geneScore);
		geneUTR5Bounds.add(geneUTR5Bound);
		geneUTR3Bounds.add(geneUTR3Bound);
		// add exon offset
		exonOffsets.add(lastExonAddedIndex);
		// add exon
		if (geneExons != null) {
			for (ScoredChromosomeWindow currentExon: geneExons) {
				exonLVBuilder.addUnsortedElementToBuild(currentExon);
				lastExonAddedIndex++;
			}
		}
	}


	@Override
	public GeneListViewBuilder clone() {
		GeneListViewBuilder clone = new GeneListViewBuilder();
		return clone;
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
				exonLVBuilder.getListView(),
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
		exonLVBuilder = null;
		exonOffsets = null;
		return listView;
	}
}
