package edu.yu.einstein.genplay.core.IO.SAM;

import java.io.File;
import java.io.IOException;

import net.sf.samtools.SAMFileHeader.SortOrder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import edu.yu.einstein.genplay.core.IO.dataReader.DataReader;
import edu.yu.einstein.genplay.core.IO.extractor.Extractor;

public class SAMExtractor extends Extractor implements DataReader {

	/** Default first base position of bed files. SAM files are 1-based */
	public static final int SAM_FIRST_BASE_POSITION = 1;

	/** Default first base position of bed files. BAM files are 0-based */
	public static final int BAM_FIRST_BASE_POSITION = 1;


	private final SAMFileReader samReader;
	private final SAMRecordIterator iterator;
	private int	firstBasePosition;

	private Integer start;
	private Integer stop;


	public SAMExtractor(File dataFile) {
		super(dataFile);
		samReader = new SAMFileReader(dataFile);
		if (samReader.isBinary()) {
			firstBasePosition = BAM_FIRST_BASE_POSITION;
		} else {
			firstBasePosition = SAM_FIRST_BASE_POSITION;
		}
		iterator = samReader.iterator();
		iterator.assertSorted(SortOrder.queryname);
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	private boolean isValidRecord(SAMRecord samRecord) {
		if (samRecord.getSecondOfPairFlag()) {
			return false;
		}
		if (samRecord.getReadUnmappedFlag()) {
			return false;
		}
		if (samRecord.getMateUnmappedFlag()) {
			return false;
		}
		//if (samRecord.)
		return true;
	}


	private void processSamRecord(SAMRecord samRecord) {
		if (samRecord.getProperPairFlag()) {

		}

	}


	@Override
	public boolean readItem() throws IOException {
		SAMRecord samRecord = null;
		boolean isValidRecord = false;
		while (iterator.hasNext() && !isValidRecord) {
			samRecord = iterator.next();
			isValidRecord = isValidRecord(samRecord);
		}
		if (iterator.hasNext()) {
			processSamRecord(samRecord);
			return true;
		} else {
			return false;
		}
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;

	}
}
