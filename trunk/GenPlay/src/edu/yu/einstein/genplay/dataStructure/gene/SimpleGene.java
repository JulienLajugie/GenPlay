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
package edu.yu.einstein.genplay.dataStructure.gene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Simple implementation of the {@link Gene} interface.
 * {@link SimpleGene} objects are immutable.
 * @author Julien Lajugie
 */
public final class SimpleGene extends AbstractGene implements Gene {

	/** Generated serial ID */
	private static final long serialVersionUID = -9086602517817950291L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Name of the gene */
	private final String name;

	/** Strand of the gene */
	private final Strand strand;

	/** Start position of the gene */
	private final int start;

	/** End position of the gene */
	private final int stop;

	/** Score of the gene */
	private final float score;

	/**  5' UTR boundary */
	private final int UTR5Bound;

	/** 3' UTR bondary */
	private final int UTR3Bound;

	/** {@link ListView} of the exons of the gene*/
	private final ListView<ScoredChromosomeWindow> exons;


	/**
	 * Creates an instance of Gene.
	 * @param name name of the gene
	 * @param strand strand of the gene
	 * @param start start position of the gene
	 * @param stop stop position of the gene
	 * @param score score of the gene
	 * @param UTR5Bound transcription 5' bound
	 * @param UTR3Bound transcription 3' bound
	 * @param exons exons of the gene
	 */
	public SimpleGene(String name, Strand strand, int start, int stop, float score, int UTR5Bound,int UTR3Bound, ListView<ScoredChromosomeWindow> exons) {
		super();
		this.name = name;
		this.strand = strand;
		this.start = start;
		this.stop = stop;
		this.score = score;
		this.UTR5Bound = UTR5Bound;
		this.UTR3Bound = UTR3Bound;
		this.exons = exons;
	}


	/**
	 * Creates an instance of Gene.
	 * @param name name of the gene
	 * @param strand strand of the gene
	 * @param start start position of the gene
	 * @param stop stop position of the gene
	 * @param score score of the gene
	 * @param exons exons of the gene
	 */
	public SimpleGene(String name, Strand strand, int start, int stop, float score, ListView<ScoredChromosomeWindow> exons) {
		this(name, strand, start, stop, score, start, stop, exons);
	}


	@Override
	public ListView<ScoredChromosomeWindow> getExons() {
		return exons;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public float getScore() {
		return score;
	}


	@Override
	public int getStart() {
		return start;
	}


	@Override
	public int getStop() {
		return stop;
	}


	@Override
	public Strand getStrand() {
		return strand;
	}


	@Override
	public int getUTR3Bound() {
		return UTR3Bound;
	}


	@Override
	public int getUTR5Bound() {
		return UTR5Bound;
	}


	/**
	 * Prints the state of the gene
	 */
	public void print() {
		System.out.println("Name:\t" + name);
		System.out.println("Strand:\t" + strand);
		System.out.println("Start:\t" + start);
		System.out.println("Stop:\t" + stop);
		System.out.println("Score:\t" + score);
		System.out.println("UTR5:\t" + UTR5Bound);
		System.out.println("UTR3:\t" + UTR3Bound);
		if (exons != null) {
			for (int i = 0; i < exons.size(); i++) {
				System.out.println("Exon " + (i + 1) + ":");
				System.out.println("\tStart:\t" + exons.get(i).getStart());
				System.out.println("\tStop:\t" + exons.get(i).getStop());
				System.out.println("\tScore:\t" + exons.get(i).getScore());
			}
		}
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the class version number
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
