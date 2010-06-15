/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Unzips and unserializes a {@link ByteArrayOutputStream} and returns a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOUnzipAndUnserialize implements Operation<BinList> {

	private final ByteArrayOutputStream baos;	// input BinList


	/**
	 * Unzips and unserializes a {@link ByteArrayOutputStream} and returns a {@link BinList}
	 * @param baos a {@link ByteArrayOutputStream} to unzip and unserialize
	 */
	public BLOUnzipAndUnserialize(ByteArrayOutputStream baos) {
		this.baos = baos;
	}


	@Override
	public BinList compute() throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		GZIPInputStream gz = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gz);
		BinList binList = (BinList)ois.readObject();
		ois.close();
		gz.close();
		return binList;
	}
	

	@Override
	public String getDescription() {
		return "Operation: Unzip and Unserialize";
	}
	
	
	@Override
	public int getStepCount() {
		return 1;
	}
	

	@Override
	public String getProcessingDescription() {
		return "Unserializing";
	}
}
