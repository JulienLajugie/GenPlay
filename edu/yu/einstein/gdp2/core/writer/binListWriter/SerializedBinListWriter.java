/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer.binListWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;


import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Allows to write a BinList as a compressed serialized object.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SerializedBinListWriter extends BinListWriter {

	
	/**
	 * Creates an instance of {@link SerializedBinListWriter}.
	 * @param outputFile output {@link File}
	 * @param data {@link BinList} to write
	 * @param name a name for the {@link BinList}
	 */
	public SerializedBinListWriter(File outputFile, BinList data, String name) {
		super(outputFile, data, name);
	}

	@Override
	public void write() throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		GZIPOutputStream gz = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(data);
		oos.flush();
		oos.close();
		fos.close();
	}
}
