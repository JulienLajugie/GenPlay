/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import net.sf.samtools.util.BlockCompressedInputStream;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGABGZIPDecompression extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION = "Decompress BGZIP"; 		// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 	ACTION_NAME = "Decompress BGZIP";	// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Decompress BGZIP";

	private File bgzFile;	// the bgzip file
	private File file;		// the uncpmpressed file


	/**
	 * Creates an instance of {@link MGABGZIPDecompression}.
	 * @param file the file to compress
	 */
	public MGABGZIPDecompression(File file) {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		bgzFile = file;
	}


	@Override
	protected Void processAction() throws Exception {

		if (Utils.getExtension(bgzFile).equals("gz")) {
			// Notifies the action
			notifyActionStart(ACTION_NAME, 1, false);

			// Open the BGZIP input stream
			BlockCompressedInputStream bgzipBCIS = new BlockCompressedInputStream(bgzFile);
			InputStreamReader bgzipISR = new InputStreamReader(bgzipBCIS);
			BufferedReader bgzipBR = new BufferedReader(bgzipISR);

			// Get the file
			file = getFile(bgzFile);

			// Open the file output stream
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream on = new DataOutputStream(fos);
			OutputStreamWriter osr = new OutputStreamWriter(on);
			BufferedWriter bw = new BufferedWriter(osr);

			String newLine = "\n";
			String gzLine;
			while ((gzLine = bgzipBR.readLine()) != null) {
				bw.write(gzLine);
				bw.write(newLine);
			}

			// Close the VCF output stream
			bw.close();
			osr.close();
			on.close();
			fos.close();

			// Close the BGZIP input stream
			bgzipBR.close();
			bgzipISR.close();
			bgzipBCIS.close();
		} else {
			JOptionPane.showMessageDialog(getRootPane(), "The BGZIP extension has not been found.\nThe file will not be decompressed.", "Compression error", JOptionPane.INFORMATION_MESSAGE);
		}

		return null;
	}


	@Override
	protected void doAtTheEnd(Void actionResult) {
		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @param file a compressed file
	 * @return the associated file
	 */
	private File getFile (File file) {
		File newFile = null;
		String filePath = file.getPath();
		String newPath = filePath.substring(0, filePath.length() - 3);
		newFile = new File(newPath);
		return newFile;
	}


	/**
	 * @return the BGZIP file
	 */
	public File getDecompressedFile() {
		return file;
	}
	
	
	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}