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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.AbstractGene;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * {@link ListView} of objects implementing the {@link Gene} interface.
 * This {@link ListView} is optimized to be memory efficient.
 * {@link GeneListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class GeneListView extends AbstractListView<Gene> implements Serializable, ListView<Gene> {

	/**
	 * Implementation of the {@link Gene} interface for genes retrieved from a {@link GeneListView}
	 * @author Julien Lajugie
	 */
	private class GeneFromListView extends AbstractGene implements Gene {

		/** Generated serial ID */
		private static final long serialVersionUID = -800372393932775792L;

		/**  Index of the gene in the parent {@link ListView}*/
		private final int geneIndex;


		/**
		 * Creates an instance of {@link GeneFromListView}
		 * @param geneIndex index of the gene
		 */
		private GeneFromListView(int geneIndex) {
			this.geneIndex = geneIndex;
		}


		@Override
		public ListView<ScoredChromosomeWindow> getExons() {
			return retrieveGeneExons(geneIndex);
		}


		@Override
		public String getName() {
			return retrieveGeneName(geneIndex);
		}


		@Override
		public float getScore() {
			return geneScores.get(geneIndex);
		}


		@Override
		public int getSize() {
			return getStop() - getStart();
		}


		@Override
		public int getStart() {
			return geneStarts.get(geneIndex);
		}


		@Override
		public int getStop() {
			return geneStops.get(geneIndex);
		}


		@Override
		public Strand getStrand() {
			return geneStrands.get(geneIndex) ? Strand.FIVE : Strand.THREE;
		}


		@Override
		public int getUTR3Bound() {
			return geneUTR3Bounds.get(geneIndex);
		}


		@Override
		public int getUTR5Bound() {
			return geneUTR5Bounds.get(geneIndex);
		}
	}


	/** generated ID */
	private static final long serialVersionUID = 2250815008426652561L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** List of the names of the genes */
	private final List<Byte> geneNames;

	/** List of the offsets of the gene names inside the byte list containing the gene names */
	private final List<Integer> geneNameOffsets;

	/** List of the strands of the genes. A true value means plus strand, false means minus strand  */
	private final List<Boolean> geneStrands;

	/** List of the start positions of the genes */
	private final List<Integer> geneStarts;

	/** List of the stop positions of the genes */
	private final List<Integer> geneStops;

	/** List of the scores of the genes */
	private final List<Float> geneScores;

	/** List of the UTR 5 bounds of the genes */
	private final List<Integer> geneUTR5Bounds;

	/** List of the UTR 3 bounds of the genes */
	private final List<Integer> geneUTR3Bounds;

	/**  List of the exon start positions */
	private final ListView<ScoredChromosomeWindow> exons;

	/** List of the offsets of the exons inside the exon start, stop and score lists */
	private final List<Integer> exonOffsets;


	/**
	 * Creates an instance of {@link GeneListView}
	 * @param geneNames list of the names of the genes
	 * @param geneNameOffsets list of the offsets of the gene names inside the byte list containing the gene names
	 * @param geneStrands list of the strands of the genes. A true value means plus strand, false means minus strand
	 * @param geneStarts list of the start positions of the genes
	 * @param geneStops list of the stop positions of the genes
	 * @param geneScores list of the scores of the genes
	 * @param geneUTR5Bounds list of the UTR 5 bounds of the genes
	 * @param geneUTR3Bounds list of the UTR 3 bounds of the genes
	 * @param exons list of exons
	 * @param exonOffsets list of the offsets of the exons inside the exon start, stop and score lists
	 */
	GeneListView(
			List<Byte> geneNames,
			List<Integer> geneNameOffsets,
			List<Boolean> geneStrands,
			List<Integer> geneStarts,
			List<Integer> geneStops,
			List<Float> geneScores,
			List<Integer> geneUTR5Bounds,
			List<Integer> geneUTR3Bounds,
			ListView<ScoredChromosomeWindow> exons,
			List<Integer> exonOffsets
			) {
		this.geneNames = geneNames;
		this.geneNameOffsets = geneNameOffsets;
		this.geneStrands = geneStrands;
		this.geneStarts = geneStarts;
		this.geneStops = geneStops;
		this.geneScores = geneScores;
		this.geneUTR5Bounds = geneUTR5Bounds;
		this.geneUTR3Bounds = geneUTR3Bounds;
		this.exons = exons;
		this.exonOffsets = exonOffsets;
	}


	@Override
	public Gene get(int geneIndex) {
		return new GeneFromListView(geneIndex);
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the version number of the object
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	/**
	 * @param geneIndex the index of a gene
	 * @return the {@link ListView} of exons of the gene
	 */
	private ListView<ScoredChromosomeWindow> retrieveGeneExons(int geneIndex) {
		int firstExonIndex = exonOffsets.get(geneIndex);
		int lastExonIndex;
		if (geneIndex < (exonOffsets.size() - 1)) {
			lastExonIndex = exonOffsets.get(geneIndex + 1);
		} else {
			lastExonIndex = exons.size() - 1;
		}
		return exons.subList(firstExonIndex, lastExonIndex);
	}


	/**
	 * @param geneIndex the index of a gene
	 * @return the name of the gene at the specified index
	 */
	private String retrieveGeneName(int geneIndex) {
		int geneNameOffset = geneNameOffsets.get(geneIndex);
		int geneNameLength;
		if (geneIndex < (geneNameOffsets.size() - 1)) {
			geneNameLength = geneNameOffsets.get(geneIndex + 1) - geneNameOffset;
		} else {
			geneNameLength = geneNames.size() - geneNameOffset;
		}
		byte[] geneName = new byte[geneNameLength];
		for (int i = 0; i < geneNameLength; i++) {
			geneName[i] = geneNames.get(i + geneNameOffset);
		}
		return new String(geneName);
	}


	@Override
	public int size() {
		return geneStarts.size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
