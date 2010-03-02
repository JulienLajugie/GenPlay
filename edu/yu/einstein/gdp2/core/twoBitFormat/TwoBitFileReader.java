package yu.einstein.gdp2.core.twoBitFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TwoBitFileReader {

	private final int signature;
	private final int version;
	private final RandomAccessFile twoBitFile;
	private final List<TwoBitSequence> sequenceList;
	
	public TwoBitFileReader(File file) throws FileNotFoundException, IOException {
		twoBitFile = new RandomAccessFile(file, "r");
		twoBitFile.seek(0);
		signature = twoBitFile.readInt();
		version = twoBitFile.readInt();
		int sequenceCount = Integer.reverseBytes(twoBitFile.readInt()) & 0x7fffffff;
		System.out.println("sequence count: " + sequenceCount);
		// skip 4 reserved bytes
		twoBitFile.skipBytes(4);
		sequenceList = new ArrayList<TwoBitSequence>();
		for (int i = 0; i < sequenceCount; i++) {
			byte sequenceNameSize = twoBitFile.readByte();
			byte[] sequenceName = new byte[sequenceNameSize];
			twoBitFile.read(sequenceName);
			int offset = twoBitFile.readInt() & 0x7fffffff;
			System.out.println("sequence " + (i + 1) + ", name =" + new String(sequenceName) + ", offset = " + offset);
			long currentPosition = twoBitFile.getFilePointer();
			TwoBitSequence sequence = new TwoBitSequence(twoBitFile, offset, new String(sequenceName));
			sequenceList.add(sequence);
			twoBitFile.seek(currentPosition);
		}
	}

	
	/**
	 * @return the signature
	 */
	public final int getSignature() {
		return signature;
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
	 * @return the sequenceList
	 */
	public final List<TwoBitSequence> getSequenceList() {
		return sequenceList;
	}



	public static void main(String[] args) {
		try {
			TwoBitFileReader tbfr = new TwoBitFileReader(new File("C:\\Documents and Settings\\Administrator\\My Documents\\Downloads\\hg18.2bit"));
			List<TwoBitSequence> seqList = tbfr.getSequenceList();
			for (TwoBitSequence curSeq: seqList) {
				System.out.println(curSeq.getName() + " -> " + curSeq.getDnaSize());
			}
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
