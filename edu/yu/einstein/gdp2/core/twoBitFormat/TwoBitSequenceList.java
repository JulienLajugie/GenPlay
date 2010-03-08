/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.twoBitFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.util.ChromosomeManager;


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
	private final RandomAccessFile 		twoBitFile;						// 2bit file
	
	
	/**
	 * Creates an instance of {@link TwoBitSequenceList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param file 2Bit file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidFileTypeException
	 */
	public TwoBitSequenceList(ChromosomeManager chromosomeManager, File file) throws FileNotFoundException, IOException, InvalidFileTypeException {
		super(chromosomeManager);
		// initializes the lists
		for (int i = 0; i < chromosomeManager.chromosomeCount(); i++) {
			add(null);
		}
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
		for (int i = 0; i < sequenceCount; i++) {
			byte sequenceNameSize = twoBitFile.readByte();
			byte[] sequenceNameBytes = new byte[sequenceNameSize];
			twoBitFile.read(sequenceNameBytes);
			String sequenceName = new String(sequenceNameBytes);
			int offset = 0;
			if (reverseBytes) {
				offset = Integer.reverseBytes(twoBitFile.readInt());
			} else {
				offset = twoBitFile.readInt();
			}
			// we add the sequence to the list if the chromosome is specified in the ChromosomeManager
			short k = 0;
			boolean found = false;
			while ((k < chromosomeManager.chromosomeCount()) && (!found)) {
				if (chromosomeManager.getChromosome(k).getName().equalsIgnoreCase(sequenceName)) {
					long currentPosition = twoBitFile.getFilePointer();
					TwoBitSequence sequence = new TwoBitSequence(twoBitFile, offset, sequenceName, reverseBytes);
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
		Nucleotide[] result = new Nucleotide[stop - start];
		List<Nucleotide> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (ManagerDataNotLoadedException e) {
			e.printStackTrace();
			fittedDataList = null;
			return null;
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return null;
		}
		
		for (int i = start; i <= stop; i++) {
			result[i] = currentList.get(i);
		}		
		return result;
	}



	/*public static void main(String[] args) {
		try {
			TwoBitSequenceList tbsList = new TwoBitSequenceList(ChromosomeManager.getInstance(), new File("C:\\Documents and Settings\\Administrator\\My Documents\\Downloads\\hg18.2bit"));
			for (int i = 49975; i < 50125; i++) {
				if (i % 50 == 0) {
					System.out.println();
				}
				System.out.print(tbsList.get(0).get(i));
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}*/
	
}
