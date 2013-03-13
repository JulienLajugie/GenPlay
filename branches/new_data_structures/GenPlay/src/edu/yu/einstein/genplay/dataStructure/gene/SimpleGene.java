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

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;



/**
 * The Gene class provides a representation of a gene.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SimpleGene implements Gene {

	private static final long serialVersionUID = -9086602517817950291L; // generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// Saved format version

	private String 		name; 			// name of the gene
	private Chromosome	chromosome;		// chromosome
	private Strand		strand;			// strand of the gene
	private int 		start;	 		// start position of the gene
	private int 		stop;	 		// end position of the gene
	private double		score;			// score of the gene
	private int			UTR5Bound;		// 5' UTR boundary
	private int			UTR3Bound;		// 3' UTR bondary
	private int[] 		exonStarts; 	// exon start positions
	private int[] 		exonStops; 		// exon end positions
	private double[]	exonScores;		// exon score


	/**
	 * Creates an instance of {@link SimpleGene}
	 */
	public SimpleGene() {
		this(null, null, null, 0, 0, Double.NaN, 0, 0, null, null, null);
	}


	/**
	 * Creates an instance of {@link SimpleGene} having the exact same values as the {@link SimpleGene} in parameter
	 * @param gene a {@link SimpleGene}
	 */
	public SimpleGene(Gene gene) {
		this(gene.getName(), gene.getChromosome(), gene.getStrand(), gene.getStart(), gene.getStop(), gene.getScore(), gene.getUTR5Bound(), gene.getUTR3Bound(), null, null, null);
		if (gene.getExonStarts() != null) {
			exonStarts = gene.getExonStarts().clone();
		}
		if (gene.getExonStops() != null) {
			exonStops = gene.getExonStops().clone();
		}
		if (gene.getExonScores() != null) {
			exonScores = gene.getExonScores().clone();
		}
	}


	/**
	 * Creates an instance of Gene.
	 * @param name Name of gene
	 * @param chromosome chromosome
	 * @param strand Strand of the gene
	 * @param start gene start position
	 * @param stop gene end position
	 * @param score score of the gene
	 * @param UTR5Bound transcription 5' bond
	 * @param UTR3Bound transcription 3' bond
	 * @param exonStarts exon start positions
	 * @param exonStops exon end positions
	 * @param exonScores exon scores
	 */
	public SimpleGene(String name, Chromosome chromosome, Strand strand, int start, int stop, double score, int UTR5Bound,int UTR3Bound, int[] exonStarts, int[] exonStops, double[] exonScores) {
		super();
		this.name = name;
		this.chromosome = chromosome;
		this.strand = strand;
		this.start = start;
		this.stop = stop;
		this.score = score;
		this.UTR5Bound = UTR5Bound;
		this.UTR3Bound = UTR3Bound;
		this.exonStarts = exonStarts;
		this.exonStops = exonStops;
		this.exonScores = exonScores;
	}


	/**
	 * Creates an instance of Gene.
	 * @param name Name of gene.
	 * @param chromosome chromosome
	 * @param strand strand of the gene.
	 * @param start transcription start position.
	 * @param stop transcription end position.
	 * @param score score of the gene
	 * @param exonStarts exon start positions.
	 * @param exonStops exon end positions.
	 * @param exonScores exon scores
	 */
	public SimpleGene(String name, Chromosome chromosome, Strand strand, int start, int stop, double score, int[] exonStarts, int[] exonStops, double[] exonScores) {
		this(name, chromosome, strand, start, stop, score, start, stop, exonStarts, exonStops, exonScores);
	}


	/**
	 * Adds an exon to the Gene with no score
	 * @param exonStart start position of the exon
	 * @param exonStop stop position of the exon
	 */
	@Override
	public void addExon(int exonStart, int exonStop) {
		// case where it's the first exon
		if (exonStarts == null) {
			exonStarts = new int[1];
			exonStops = new int[1];
			exonStarts[0] = exonStart;
			exonStops[0] = exonStop;
		} else {
			int length = exonStarts.length;
			int[] exonStartsTmp = new int[length + 1];
			int[] exonStopsTmp = new int[length + 1];
			for (int i = 0; i < exonStarts.length; i++) {
				exonStartsTmp[i] = exonStarts[i];
				exonStopsTmp[i] = exonStops[i];
			}
			exonStartsTmp[length] = exonStart;
			exonStopsTmp[length] = exonStop;
			exonStarts = exonStartsTmp;
			exonStops = exonStopsTmp;
		}
	}


	/**
	 * Adds an exon to the Gene
	 * @param exonStart start position of the exon
	 * @param exonStop stop position of the exon
	 * @param exonScore score of the exon
	 */
	@Override
	public void addExon(int exonStart, int exonStop, double exonScore) {
		// case where it's the first exon
		if (exonStarts == null) {
			exonStarts = new int[1];
			exonStops = new int[1];
			exonScores = new double[1];
			exonStarts[0] = exonStart;
			exonStops[0] = exonStop;
			exonScores[0] = exonScore;
		} else {
			int length = exonStarts.length;
			int[] exonStartsTmp = new int[length + 1];
			int[] exonStopsTmp = new int[length + 1];
			double[] exonScoresTmp = new double[length + 1];
			for (int i = 0; i < exonStarts.length; i++) {
				exonStartsTmp[i] = exonStarts[i];
				exonStopsTmp[i] = exonStops[i];
				exonScoresTmp[i] = exonScores[i];
			}
			exonStartsTmp[length] = exonStart;
			exonStopsTmp[length] = exonStop;
			exonScoresTmp[length] = exonScore;
			exonStarts = exonStartsTmp;
			exonStops = exonStopsTmp;
			exonScores = exonScoresTmp;
		}
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
	public Gene deepClone() {
		return new SimpleGene(this);
	}


	/**
	 * @param aName Name of a chromosome
	 * @return True if <i>aName</i> equals the name of the chromosome. False otherwise.
	 */
	@Override
	public boolean equals(String aName) {
		return name.equalsIgnoreCase(aName);
	}


	/**
	 * @return The chromosome of the gene.
	 */
	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @return the exonScores
	 */
	@Override
	public double[] getExonScores() {
		return exonScores;
	}


	/**
	 * @return the exonStarts
	 */
	@Override
	public int[] getExonStarts() {
		return exonStarts;
	}


	/**
	 * @return the exonStops
	 */
	@Override
	public int[] getExonStops() {
		return exonStops;
	}


	/**
	 * @return the middle position of the genes
	 */
	@Override
	public double getMiddlePosition() {
		return (start + stop) / 2d;
	}


	/**
	 * @return the name
	 */
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


	/**
	 * @return the start position of the gene
	 */
	@Override
	public int getStart() {
		return start;
	}


	/**
	 * @return the stop position of the gene
	 */
	@Override
	public int getStop() {
		return stop;
	}


	/**
	 * @return the strand
	 */
	@Override
	public Strand getStrand() {
		return strand;
	}


	/**
	 * @return the 3' bondary of the translation
	 */
	@Override
	public int getUTR3Bound() {
		return UTR3Bound;
	}


	/**
	 * @return the 5' bondary of the translation
	 */
	@Override
	public int getUTR5Bound() {
		return UTR5Bound;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		name = (String) in.readObject();
		chromosome = (Chromosome) in.readObject();
		strand = (Strand) in.readObject();
		start = in.readInt();
		stop = in.readInt();
		score = in.readDouble();
		UTR5Bound = in.readInt();
		UTR3Bound = in.readInt();
		exonStarts = (int[]) in.readObject();
		exonStops = (int[]) in.readObject();
		exonScores = (double[]) in.readObject();
	}


	/**
	 * @param chromosome the chromosome to set
	 */
	@Override
	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}


	/**
	 * @param exonScores the exonScores to set
	 */
	@Override
	public void setExonScores(double[] exonScores) {
		this.exonScores = exonScores;
	}


	/**
	 * @param exonStarts the exonStarts to set
	 */
	@Override
	public void setExonStarts(int[] exonStarts) {
		this.exonStarts = exonStarts;
	}


	/**
	 * @param exonStops the exonStops to set
	 */
	@Override
	public void setExonStops(int[] exonStops) {
		this.exonStops = exonStops;
	}


	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}


	@Override
	public void setScore(double score) {
		this.score = score;
	}


	/**
	 * @param start the start position of the gene to set
	 */
	@Override
	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @param stop the stop to set
	 */
	@Override
	public void setStop(int stop) {
		this.stop = stop;
	}


	/**
	 * @param strand the strand to set
	 */
	@Override
	public void setStrand(Strand strand) {
		this.strand = strand;
	}


	/**
	 * @param UTR3Bound the 3' translation bound to set
	 */
	@Override
	public void setUTR3Bond(int UTR3Bound) {
		this.UTR3Bound = UTR3Bound;
	}


	/**
	 * @param UTR5Bound the 5' translation bound to set
	 */
	@Override
	public void setUTR5Bound(int UTR5Bound) {
		this.UTR5Bound = UTR5Bound;
	}


	@Override
	public String toString() {
		return chromosome.toString() + "\t" + start + "\t" + stop +"\t" + name + "\t" + strand;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(name);
		out.writeObject(chromosome);
		out.writeObject(strand);
		out.writeInt(start);
		out.writeInt(stop);
		out.writeDouble(score);
		out.writeInt(UTR5Bound);
		out.writeInt(UTR3Bound);
		out.writeObject(exonStarts);
		out.writeObject(exonStops);
		out.writeObject(exonScores);
	}
}
