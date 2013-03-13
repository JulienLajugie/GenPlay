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
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * The Gene class provides a representation of a gene.
 * @author Julien Lajugie
 * @version 0.1
 */
public interface Gene extends Serializable, Cloneable, ScoredChromosomeWindow {


	/**
	 * Adds an exon to the Gene with no score
	 * @param exonStart start position of the exon
	 * @param exonStop stop position of the exon
	 */
	public void addExon(int exonStart, int exonStop);


	/**
	 * Adds an exon to the Gene
	 * @param exonStart start position of the exon
	 * @param exonStop stop position of the exon
	 * @param exonScore score of the exon
	 */
	public void addExon(int exonStart, int exonStop, double exonScore);


	/**
	 * @return a deep copy of the object
	 */
	@Override
	public Gene deepClone();


	/**
	 * @param aName Name of a chromosome
	 * @return True if <i>aName</i> equals the name of the chromosome. False otherwise.
	 */
	public boolean equals(String aName);


	/**
	 * @return The chromosome of the gene.
	 */
	public Chromosome getChromosome();


	/**
	 * @return the exonScores
	 */
	public double[] getExonScores();


	/**
	 * @return the exonStarts
	 */
	public int[] getExonStarts();


	/**
	 * @return the exonStops
	 */
	public int[] getExonStops();


	/**
	 * @return the name
	 */
	public String getName();


	/**
	 * @return the strand
	 */
	public Strand getStrand();


	/**
	 * @return the 3' bondary of the translation
	 */
	public int getUTR3Bound();


	/**
	 * @return the 5' bondary of the translation
	 */
	public int getUTR5Bound();


	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(Chromosome chromosome);


	/**
	 * @param exonScores the exonScores to set
	 */
	public void setExonScores(double[] exonScores);


	/**
	 * @param exonStarts the exonStarts to set
	 */
	public void setExonStarts(int[] exonStarts);


	/**
	 * @param exonStops the exonStops to set
	 */
	public void setExonStops(int[] exonStops);


	/**
	 * @param name the name to set
	 */
	public void setName(String name);


	/**
	 * @param strand the strand to set
	 */
	public void setStrand(Strand strand);


	/**
	 * @param UTR3Bound the 3' translation bound to set
	 */
	public void setUTR3Bond(int UTR3Bound);


	/**
	 * @param UTR5Bound the 5' translation bound to set
	 */
	public void setUTR5Bound(int UTR5Bound);
}
