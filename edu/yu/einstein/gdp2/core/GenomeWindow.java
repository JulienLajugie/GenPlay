/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

import java.io.Serializable;

import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.exception.ChromosomeWindowException;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * The GenomeWindow class represents a window on the genome. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWindow extends ChromosomeWindow implements Serializable, Cloneable, Comparable<ChromosomeWindow> {

	private static final long serialVersionUID = 8873056842762282328L; // generated ID
	private Chromosome chromosome;	// Chromosome of the window
	
	
	/**
	 * Default constructor.
	 */
	public GenomeWindow() {
		super();
	}

	
	/**
	 * Creates an instance of {@link GenomeWindow}.
	 * @param chromosome a chromosome
	 * @param start a window start
	 * @param stop a window stop
	 */
	public GenomeWindow(Chromosome chromosome, int start, int stop) {
		super(start, stop);
		this.chromosome = chromosome;
	}
	
	
	/**
	 * Creates an instance of {@link GenomeWindow} from a String. 
	 * @param genomeWindowStr String following the format "chr:start-stop" (ex: "chr1:100-120")
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @throws ChromosomeWindowException
	 * @throws InvalidChromosomeException
	 */
	public GenomeWindow(String genomeWindowStr, ChromosomeManager chromosomeManager) throws ChromosomeWindowException, InvalidChromosomeException {
		super(genomeWindowStr.split(":")[1].trim());
		chromosome = chromosomeManager.get(genomeWindowStr.split(":")[0].trim());
	}

	
	/**
	 * @return the chromosome
	 */
	public final Chromosome getChromosome() {
		return chromosome;
	}

	
	/**
	 * @param chromosome the chromosome to set
	 */
	public final void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}
	
	
	@Override
	public String toString() {
		return chromosome.toString() + ":" + super.toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenomeWindow other = (GenomeWindow) obj;
		if (chromosome == null) {
			if (other.chromosome != null) {
				return false;
			}
		} else if (!chromosome.equals(other.chromosome)) {
			return false;
		}
		return true;
	}
}
