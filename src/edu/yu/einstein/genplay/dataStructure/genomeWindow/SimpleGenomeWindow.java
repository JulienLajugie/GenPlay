package edu.yu.einstein.genplay.dataStructure.genomeWindow;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ChromosomeWindowException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Simple implementation of the {@link GenomeWindow} interface.
 * A window on the genome with a chromosome and a start and stop position (in bp) on this chromosome
 * @author Julien Lajugie
 */
public class SimpleGenomeWindow extends SimpleChromosomeWindow implements GenomeWindow {
	private static final long serialVersionUID = 8873056842762282328L; 	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Chromosome chromosome;	// Chromosome of the window


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosome);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		chromosome = (Chromosome) in.readObject();
	}


	/**
	 * Default constructor.
	 */
	public SimpleGenomeWindow() {
		super();
	}


	/**
	 * Creates an instance of {@link GenomeWindow}.
	 * @param chromosome a chromosome
	 * @param start a window start
	 * @param stop a window stop
	 */
	public SimpleGenomeWindow(Chromosome chromosome, int start, int stop) {
		super(start, stop);
		this.chromosome = chromosome;
	}


	/**
	 * Creates an instance of {@link GenomeWindow} from a String.
	 * @param genomeWindowStr String following the format "chr:start-stop" (ex: "chr1:100-120")
	 * @param projectChromosome a {@link ProjectChromosome}
	 * @throws ChromosomeWindowException
	 * @throws InvalidChromosomeException
	 */
	public SimpleGenomeWindow(String genomeWindowStr, ProjectChromosome projectChromosome) throws ChromosomeWindowException, InvalidChromosomeException {
		super(Utils.split(genomeWindowStr, ':')[1].trim());
		chromosome = projectChromosome.get(Utils.split(genomeWindowStr, ':')[0].trim());
	}


	/**
	 * @return the chromosome
	 */
	@Override
	public final Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @param chromosome the chromosome to set
	 */
	@Override
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
			if (other.getChromosome() != null) {
				return false;
			}
		} else if (!chromosome.equals(other.getChromosome())) {
			return false;
		}
		return true;
	}
}