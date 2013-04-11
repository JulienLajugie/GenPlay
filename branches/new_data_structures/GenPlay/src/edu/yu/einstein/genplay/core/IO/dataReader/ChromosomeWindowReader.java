package edu.yu.einstein.genplay.core.IO.dataReader;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;

/**
 * Interface defining method for data readers that can read elements needed to create {@link ChromosomeWindow} objects.
 * @author Julien Lajugie
 */
public interface ChromosomeWindowReader extends DataReader {

	/**
	 * @return the chromosome of the last extracted item
	 */
	public Chromosome getChromosome();


	/**
	 * @return the start position of the last extracted item
	 */
	public int getStart();


	/**
	 * @return the stop position of the last extracted item
	 */
	public int getStop();
}
