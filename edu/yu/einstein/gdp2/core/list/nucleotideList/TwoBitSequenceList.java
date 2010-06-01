/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.nucleotideList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidFileTypeException;


/**
 * Reads a 2Bit files and extracts the data from this kind of files.
 * 2bit files are used to store genome sequences in a random access file
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequenceList extends DisplayableListOfLists<Nucleotide, Nucleotide[]> implements Serializable {

	private static final long serialVersionUID = -2253030492143151302L;	// generated ID
	private final static String 		TWOBIT_SIGNATURE = "1A412743";	// signature of a 2bit file
	private boolean 					reverseBytes = false;			// true if the bytes of a multi-byte entity need to be reversed when read
	private final int 					version;						// version of the 2bit file
	private final String				filePath;						// path of the 2bit file  (used for the serialization)
	private transient RandomAccessFile	twoBitFile;						// 2bit file


	/**
	 * Creates an instance of {@link TwoBitSequenceList}
	 * @param file 2Bit file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidFileTypeException
	 */
	public TwoBitSequenceList(File file) throws FileNotFoundException, IOException, InvalidFileTypeException {
		super();
		// initializes the lists
		for (int i = 0; i < chromosomeManager.size(); i++) {
			add(null);
		}
		filePath = file.getAbsolutePath();
		twoBitFile = new RandomAccessFile(file, "r");
		twoBitFile.seek(0);
		int signature = twoBitFile.readInt();
		// if the signature is not equal to the signature defined in the 2bit files
		// it might means that the byte order need to be reversed
		if (!Integer.toHexString(signature).equalsIgnoreCase(TWOBIT_SIGNATURE)) {
			signature = Integer.reverseBytes(signature);
			// check if it matches with the bytes reversed
			if (Integer.toHexString(signature).equalsIgnoreCase(TWOBIT_SIGNATURE)) {
				// if it matches, turns the reverse mode on
				reverseBytes = true;
			} else {
				// if it doesn't the file is not correct
				throw new InvalidFileTypeException();
			}
		}		
		if (reverseBytes) {
			version = Integer.reverseBytes(twoBitFile.readInt());	
		} else {
			version = twoBitFile.readInt();
		}		
		int sequenceCount = 0;
		if (reverseBytes) {
			sequenceCount = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			sequenceCount = twoBitFile.readInt();
		}
		// skip 4 reserved bytes
		twoBitFile.skipBytes(4);
		String[] sequenceNames = new String[sequenceCount];
		int[] offsets = new int[sequenceCount];
		for (int i = 0; i < sequenceCount; i++) {
			byte sequenceNameSize = twoBitFile.readByte();
			byte[] sequenceNameBytes = new byte[sequenceNameSize];
			twoBitFile.read(sequenceNameBytes);
			sequenceNames[i] = new String(sequenceNameBytes);
			if (reverseBytes) {
				offsets[i] = Integer.reverseBytes(twoBitFile.readInt());
			} else {
				offsets[i] = twoBitFile.readInt();
			}
		}
		// we add the sequence to the list if the chromosome is specified in the ChromosomeManager
		for (int i = 0; i < sequenceCount; i++) {
			short k = 0;
			boolean found = false;
			while ((k < chromosomeManager.size()) && (!found)) {
				if (chromosomeManager.get(k).getName().equalsIgnoreCase(sequenceNames[i])) {
					long currentPosition = twoBitFile.getFilePointer();
					TwoBitSequence sequence = new TwoBitSequence(filePath, twoBitFile, offsets[i], sequenceNames[i], reverseBytes);
					set(k, sequence);
					twoBitFile.seek(currentPosition);
					found = true;
				}
				k++;
			}
		}
	}


	/**
	 * @return the version
	 */
	public final int getVersion() {
		return version;
	}



	/**
	 * @return the twoBitFile
	 */
	public final RandomAccessFile getTwoBitFile() {
		return twoBitFile;
	}


	/**
	 * Does nothing
	 */
	@Override
	protected void fitToScreen() {}


	@Override
	protected Nucleotide[] getFittedData(int start, int stop) {
		Nucleotide[] result = new Nucleotide[stop - start + 1];
		List<Nucleotide> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return null;
		}

		int j = 0;
		for (int i = start; i <= stop; i++) {
			result[j] = currentList.get(i);
			j++;
		}		
		return result;
	}


	/**
	 * Methods used for the unserialization of the object.
	 * Since the random access file can't be serialized we try to recreate it if the file path is still the same
	 * See javadocs for more information
	 * @return the unserialized object
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		try {
			twoBitFile = new RandomAccessFile(new File(filePath), "r");
			return this;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
