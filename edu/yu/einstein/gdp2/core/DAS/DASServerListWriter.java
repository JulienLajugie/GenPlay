/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class allows writing the DAS Server List
 * to a file. 
 * @author Chirag Gorasia
 * @version 0.1
 */
public class DASServerListWriter {

	/**
	 * @param tableData of type Object[][]
	 * @param fileName of type String
	 */
	public void write(Object[][] tableData, String fileName) throws IOException {
		BufferedWriter writer = null;
		//System.out.println("File Path: " + fileName);
		try {
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			// print the title of the graph
			writer.write("<DASLIST>");
			writer.newLine();
			for (int i = 0; i < tableData.length; i++) {
				writer.write("<SERVER href=" + "\"" + tableData[i][1] + "\" name=" + "\"" + tableData[i][0] + "\"/>");
				writer.newLine();
			}
			writer.write("</DASLIST>");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}

