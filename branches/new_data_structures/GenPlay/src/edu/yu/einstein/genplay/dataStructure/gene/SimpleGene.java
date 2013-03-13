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
package edu.yu.einstein.genplay.dataStructure.gene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.HashCodeUtil;


/**
 * Simple implementation of the {@link Gene} interface.
 * {@link SimpleGene} objects are immutable.
 * @author Julien Lajugie
 */
public final class SimpleGene implements Gene {

	/** Generated serial ID */
	private static final long serialVersionUID = -9086602517817950291L;

	/**  Saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** Name of the gene */
	private String name;

	/** Strand of the gene */
	private Strand strand;

	/** Start position of the gene */
	private int start;

	/** End position of the gene */
	private int stop;

	/** Score of the gene */
	private double score;

	/**  5' UTR boundary */
	private int UTR5Bound;

	/** 3' UTR bondary */
	private int UTR3Bound;

	/** {@link ListView} of the exons of the gene*/
	private ListView<ScoredChromosomeWindow> exons;


	/**
	 * Creates an instance of {@link SimpleGene} having the exact same values as the gene in parameter.
	 * @param gene a {@link Gene}
	 */
	public SimpleGene(Gene gene) {
		this(gene.getName(), gene.getStrand(), gene.getStart(), gene.getStop(), gene.getScore(), gene.getUTR5Bound(), gene.getUTR3Bound(), gene.getExons());
	}


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
	public SimpleGene(String name, Strand strand, int start, int stop, double score, int UTR5Bound,int UTR3Bound, ListView<ScoredChromosomeWindow> exons) {
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
	public SimpleGene(String name, Strand strand, int start, int stop, double score, ListView<ScoredChromosomeWindow> exons) {
		this(name, strand, start, stop, score, start, stop, exons);
	}


	@Override
	public int compareTo(ChromosomeWindow chromosomeWindow) {
		return new SimpleChromosomeWindow(start, stop).compareTo(chromosomeWindow);
	}


	@Override
	public int containsPosition(int position) {
		return new SimpleChromosomeWindow(start, stop).containsPosition(position);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleGene other = (SimpleGene) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (strand == null) {
			if (other.strand != null) {
				return false;
			}
		} else if (!strand.equals(other.strand)) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		if (stop != other.stop) {
			return false;
		}
		if (score != other.score) {
			return false;
		}
		if (UTR5Bound != other.UTR5Bound) {
			return false;
		}
		if (UTR3Bound != other.UTR3Bound) {
			return false;
		}
		if (exons == null) {
			if (other.exons != null) {
				return false;
			}
		} else if (!exons.equals(other.exons)) {
			return false;
		}
		return true;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getExons() {
		return exons;
	}


	@Override
	public double getMiddlePosition() {
		return (start + stop) / 2d;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public double getScore() {
		return score;
	}


	@Override
	public int getSize() {
		return new SimpleChromosomeWindow(start, stop).getSize();
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


	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.SEED;
		hashCode = HashCodeUtil.hash(hashCode, name);
		hashCode = HashCodeUtil.hash(hashCode, strand);
		hashCode = HashCodeUtil.hash(hashCode, start);
		hashCode = HashCodeUtil.hash(hashCode, stop);
		hashCode = HashCodeUtil.hash(hashCode, score);
		hashCode = HashCodeUtil.hash(hashCode, UTR5Bound);
		hashCode = HashCodeUtil.hash(hashCode, UTR3Bound);
		hashCode = HashCodeUtil.hash(hashCode, exons);
		return hashCode;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		name = (String) in.readObject();
		strand = (Strand) in.readObject();
		start = in.readInt();
		stop = in.readInt();
		score = in.readDouble();
		UTR5Bound = in.readInt();
		UTR3Bound = in.readInt();
		exons = (ListView<ScoredChromosomeWindow>) in.readObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(name);
		out.writeObject(strand);
		out.writeInt(start);
		out.writeInt(stop);
		out.writeDouble(score);
		out.writeInt(UTR5Bound);
		out.writeInt(UTR3Bound);
		out.writeObject(exons);
	}
}
