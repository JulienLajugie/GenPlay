package edu.yu.einstein.genplay.dataStructure.genomeWindow;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ChromosomeWindowException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.HashCodeUtil;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Simple implementation of the {@link GenomeWindow} interface.
 * {@link SimpleChromosomeWindow} objects are immutable.
 * @author Julien Lajugie
 */
public final class SimpleGenomeWindow implements GenomeWindow {

	/** Generated serial ID */
	private static final long serialVersionUID = 8873056842762282328L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Chromosome of the window */
	private final Chromosome chromosome;

	/**  Start and stop positions of the window on the chromosome */
	private final ChromosomeWindow chromosomeWindow;


	/**
	 * Creates an instance of {@link GenomeWindow}.
	 * @param chromosome a chromosome
	 * @param start a window start
	 * @param stop a window stop
	 */
	public SimpleGenomeWindow(Chromosome chromosome, int start, int stop) {
		this.chromosome = chromosome;
		chromosomeWindow = new SimpleChromosomeWindow(start, stop);
	}


	/**
	 * Creates an instance of {@link GenomeWindow} from a String.
	 * @param genomeWindowStr String following the format "chr:start-stop" (ex: "chr1:100-120")
	 * @param projectChromosome a {@link ProjectChromosome}
	 * @throws ChromosomeWindowException
	 * @throws InvalidChromosomeException
	 */
	public SimpleGenomeWindow(String genomeWindowStr, ProjectChromosome projectChromosome) throws ChromosomeWindowException, InvalidChromosomeException {
		chromosome = projectChromosome.get(Utils.split(genomeWindowStr, ':')[0].trim());
		chromosomeWindow = new SimpleChromosomeWindow(Utils.split(genomeWindowStr, ':')[1].trim());
	}


	@Override
	public int compareTo(ChromosomeWindow o) {
		return chromosomeWindow.compareTo(o);
	}


	@Override
	public int containsPosition(int position) {
		return chromosomeWindow.containsPosition(position);
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


	/**
	 * @return the chromosome
	 */
	@Override
	public final Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public double getMiddlePosition() {
		return chromosomeWindow.getMiddlePosition();
	}


	@Override
	public int getSize() {
		return chromosomeWindow.getSize();
	}


	@Override
	public int getStart() {
		return chromosomeWindow.getStart();
	}


	@Override
	public int getStop() {
		return chromosomeWindow.getStop();
	}


	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = HashCodeUtil.hash(hashCode, chromosome);
		return hashCode;
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


	@Override
	public String toString() {
		return chromosome.toString() + ":" + chromosomeWindow.toString();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
