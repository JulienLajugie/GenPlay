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

import java.io.Serializable;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * The Gene class provides a representation of a gene.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Gene implements Serializable, Cloneable, ScoredChromosomeWindow {

	private static final long serialVersionUID = -9086602517817950291L; // generated ID
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
	 * Creates an instance of {@link Gene}
	 */
	public Gene() {
		this(null, null, null, 0, 0, 0, 0, null, null, null);
	}


	/**
	 * Creates an instance of {@link Gene} having the exact same values as the {@link Gene} in parameter
	 * @param gene a {@link Gene}
	 */
	public Gene(Gene gene) {
		this(gene.name, gene.chromosome, gene.strand, gene.start, gene.stop, gene.UTR5Bound, gene.UTR3Bound, null, null, null);
		if (gene.exonStarts != null) {
			exonStarts = gene.exonStarts.clone();
		}
		if (gene.exonStops != null) {
			exonStops = gene.exonStops.clone();
		}
		if (gene.exonScores != null) {
			exonScores = gene.exonScores.clone();
		}
	}


	/**
	 * Creates an instance of Gene.
	 * @param name Name of gene
	 * @param chromosome chromosome
	 * @param strand Strand of the gene
	 * @param start Transcription start position
	 * @param stop Transcription end position
	 * @param UTR5Bound transcription 5' bond
	 * @param UTR3Bound transcription 3' bond
	 * @param exonStarts Exon start positions
	 * @param exonStops Exon end positions
	 * @param exonScores Exon scores
	 */
	public Gene(String name, Chromosome chromosome, Strand strand, int start, int stop, int UTR5Bound,int UTR3Bound, int[] exonStarts, int[] exonStops, double[] exonScores) {
		super();
		this.name = name;
		this.chromosome = chromosome;
		this.strand = strand;
		this.start = start;
		this.stop = stop;
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
	 * @param strand Strand of the gene.
	 * @param start Transcription start position.
	 * @param stop Transcription end position.
	 * @param exonStarts Exon start positions.
	 * @param exonStops Exon end positions.
	 * @param exonScores Exon scores
	 */
	public Gene(String name, Chromosome chromosome, Strand strand, int start, int stop, int[] exonStarts, int[] exonStops, double[] exonScores) {
		this(name, chromosome, strand, start, stop, start, stop, exonStarts, exonStops, exonScores);
	}


	/**
	 * Adds an exon to the Gene with no score
	 * @param exonStart start position of the exon
	 * @param exonStop stop position of the exon
	 */
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


	/**
	 * @param aName Name of a chromosome
	 * @return True if <i>aName</i> equals the name of the chromosome. False otherwise.
	 */
	public boolean equals(String aName) {
		return name.equalsIgnoreCase(aName);
	}


	/**
	 * @return The chromosome of the gene.
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @return the exonScores
	 */
	public double[] getExonScores() {
		return exonScores;
	}


	/**
	 * @return the exonStarts
	 */
	public int[] getExonStarts() {
		return exonStarts;
	}


	/**
	 * @return the exonStops
	 */
	public int[] getExonStops() {
		return exonStops;
	}


	/**
	 * @return the RPKM value of a gene calculated from the RPKM of its exons
	 */
	public Double getGeneRPKM() {
		if ((getExonScores() == null) || (getExonScores().length == 0)) {
			return null;
		} else {
			int exonicLength = 0;
			double scoreByLengthSum = 0;
			for (int i = 0; i < getExonScores().length; i++) {
				exonicLength += exonStops[i] - exonStarts[i];
				scoreByLengthSum += exonScores[i] * (exonStops[i] - exonStarts[i]);
			}
			return scoreByLengthSum / exonicLength;
		}
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
	public Strand getStrand() {
		return strand;
	}


	/**
	 * @return the 3' bondary of the translation
	 */
	public int getUTR3Bound() {
		return UTR3Bound;
	}


	/**
	 * @return the 5' bondary of the translation
	 */
	public int getUTR5Bound() {
		return UTR5Bound;
	}


	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}


	/**
	 * @param exonScores the exonScores to set
	 */
	public void setExonScores(double[] exonScores) {
		this.exonScores = exonScores;
	}


	/**
	 * @param exonStarts the exonStarts to set
	 */
	public void setExonStarts(int[] exonStarts) {
		this.exonStarts = exonStarts;
	}


	/**
	 * @param exonStops the exonStops to set
	 */
	public void setExonStops(int[] exonStops) {
		this.exonStops = exonStops;
	}


	/**
	 * @param name the name to set
	 */
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
	public void setStrand(Strand strand) {
		this.strand = strand;
	}


	/**
	 * @param UTR3Bound the 3' translation bound to set
	 */
	public void setUTR3Bond(int UTR3Bound) {
		this.UTR3Bound = UTR3Bound;
	}


	/**
	 * @param UTR5Bound the 5' translation bound to set
	 */
	public void setUTR5Bound(int UTR5Bound) {
		this.UTR5Bound = UTR5Bound;
	}


	@Override
	public String toString() {
		return chromosome.toString() + "\t" + start + "\t" + stop +"\t" + name + "\t" + strand;
	}
}
