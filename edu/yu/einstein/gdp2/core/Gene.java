/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

import java.io.Serializable;


import yu.einstein.gdp2.core.enums.Strand;

/**
 * The Gene class provides a representation of a gene.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Gene implements Serializable, Cloneable, Comparable<Gene> {

	private static final long serialVersionUID = -9086602517817950291L; // generated ID
	private String 		name; 			// name of the gene
	private Chromosome	chromo;			// chromosome
	private Strand		strand;			// strand of the gene
	private int 		txStart; 		// transcription start position
	private int 		txStop; 		// transcription end position
	private double			txMiddle;		// transcription middle position
	private int[] 		exonStarts; 	// exon start positions
	private int[] 		exonStops; 		// exon end positions
	private double[]	exonScores;		// exon score


	/**
	 * Creates an instance of {@link Gene} having the exact same values as the {@link Gene} in parameter
	 * @param gene a {@link Gene} 
	 */
	public Gene(Gene gene) {
		this.name = gene.name;
		this.chromo = gene.chromo;
		this.strand = gene.strand;
		this.txStart = gene.txStart;
		this.txStop = gene.txStop;
		this.setTxMiddle((this.txStart + this.txStop)/2);
		this.exonStarts = null;
		if (gene.exonStarts != null) {
			this.exonStarts = gene.exonStarts.clone();
		}
		this.exonStops = null;
		if (gene.exonStops != null) {
			this.exonStops = gene.exonStops.clone();
		}
		this.exonScores = null;
		if (gene.exonScores != null) {
			this.exonScores = gene.exonScores.clone();
		}
	}


	/**
	 * Public constructor.
	 * @param name Name of gene.
	 * @param chromo chromosome
	 * @param strand Strand of the gene.
	 * @param txStart Transcription start position.
	 * @param txStop Transcription end position.
	 * @param exonStarts Exon start positions.
	 * @param exonStops Exon end positions.
	 * @param exonScores Exon scores
	 */
	public Gene(String name, Chromosome chromo, Strand strand, int txStart, int txStop, int[] exonStarts, int[] exonStops, double[] exonScores) {
		this.name = name;
		this.chromo = chromo;
		this.strand = strand;
		this.txStart = txStart;
		this.txStop = txStop;
		this.setTxMiddle((this.txStart + this.txStop)/2);
		this.exonStarts = exonStarts;
		this.exonStops = exonStops;
		this.exonScores = exonScores;
	}

	/**
	 * Creates an instance of {@link Gene}
	 * @param name name of the gene
	 * @param chromo chromosome
	 * @param strandString string representing the strand of a gene (ie "+" or "-") 
	 * @param txStart transcription start position
	 * @param txStop transcription end position
	 * @param exonStarts exon start positions
	 * @param exonStops exon end positions
	 * @param exonScores exon scores
	 */
	public Gene(String name, Chromosome chromo, String strandString, int txStart, int txStop, int[] exonStarts, int[] exonStops, double[] exonScores) {
		this.name = name;
		this.chromo = chromo;
		this.strand = Strand.get(strandString);
		this.txStart = txStart;
		this.txStop = txStop;
		this.setTxMiddle((this.txStart + this.txStop)/2);
		this.exonStarts = exonStarts;
		this.exonStops = exonStops;
		this.exonScores = exonScores;
	}

	
	/**
	 * Creates an instance of {@link Gene}
	 */
	public Gene() {
		super();
	}


	/**
	 * A gene is superior to another one if its start position is greater 
	 * or if its start position is equal but its stop position is greater. 
	 */
	@Override
	public int compareTo(Gene otherGene) {
		if (txStart > otherGene.getTxStart()) {
			return 1;
		} else if (txStart < otherGene.getTxStart()) {
			return -1;
		} else {
			if (txStop > otherGene.getTxStop()) {
				return 1;
			} else if (txStop < otherGene.getTxStop()) {
				return -1;
			} else {
				return 0;
			}
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
	 * @param aName Name of a chromosome
	 * @return True if <i>aName</i> equals the name of the chromosome. False otherwise.
	 */
	public boolean equals(String aName) {
		return name.equalsIgnoreCase(aName);
	}

	
	/**
	 * @return The chromosome of the gene.
	 */
	public Chromosome getChromo() {
		return chromo;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the strand
	 */
	public Strand getStrand() {
		return strand;
	}

	
	/**
	 * @return the txStart
	 */
	public int getTxStart() {
		return txStart;
	}
	
	
	/**
	 * @return the txMiddle
	 */
	public double getTxMiddle() {
		return txMiddle;
	}

	
	/**
	 * @return the txStop
	 */
	public int getTxStop() {
		return txStop;
	}


	/**
	 * @param chromo the chromo to set
	 */
	public void setChromo(Chromosome chromo) {
		this.chromo = chromo;
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


	/**
	 * @param strand the strand to set
	 */
	public void setStrand(Strand strand) {
		this.strand = strand;
	}


	/**
	 * @param txStart the txStart to set
	 */
	public void setTxStart(int txStart) {
		this.txStart = txStart;
	}
	
	
	/**
	 * @param txMiddle the txMiddle to set
	 */
	public void setTxMiddle(double txMiddle) {
		this.txMiddle = txMiddle;
	}


	/**
	 * @param txStop the txStop to set
	 */
	public void setTxStop(int txStop) {
		this.txStop = txStop;
	}
	
	
	@Override
	public String toString() {
		return chromo.toString() + "\t" + txStart + "\t" + txStop +"\t" + name + "\t" + strand;
	}	
}
