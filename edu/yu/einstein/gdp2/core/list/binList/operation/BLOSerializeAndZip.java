/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Serializes and zips a BinList into a {@link ByteArrayOutputStream}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOSerializeAndZip implements Operation<ByteArrayOutputStream> {

	private final BinList binList;	// input BinList


	/**
	 * Serializes and zips a BinList into a {@link ByteArrayOutputStream}
	 * @param binList input BinList
	 */
	public BLOSerializeAndZip(BinList binList) {
		this.binList = binList;
	}


	@Override
	public ByteArrayOutputStream compute() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gz = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(binList);
		oos.flush();
		oos.close();
		gz.flush();
		gz.close();
		return baos;
	}
	

	@Override
	public String getDescription() {
		return "Operation: Serialize and Zip";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
	
	
	@Override
	public String getProcessingDescription() {
		return "Serializing";
	}
}
