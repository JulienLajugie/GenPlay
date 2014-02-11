/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.IO.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TrackForTransfer;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TransferableTrack;

/**
 * Encapsulate a {@link Track} in a {@link TransferableTrack} object and then serialize it and write it in a file
 * @author Julien Lajugie
 */
public class TransferableTrackWriter {

	private final Track trackToWrite;		// track to write
	private final File fileToWrite;			// file to write


	/**
	 * Creates an instance of {@link TransferableTrackWriter}
	 * @param fileToWrite file to write
	 */
	public TransferableTrackWriter(Track trackToWrite, File fileToWrite) {
		this.trackToWrite = trackToWrite;
		this.fileToWrite = fileToWrite;
	}


	/**
	 * Writes the track as a {@link TransferableTrack} in the specified file
	 * @throws IOException
	 */
	public void write() throws IOException {
		FileOutputStream fos = null;
		GZIPOutputStream gzos = null;
		ObjectOutputStream oos = null;
		try {
			TrackForTransfer transferableTrack = new TrackForTransfer(trackToWrite);
			fos = new FileOutputStream(fileToWrite);
			gzos = new GZIPOutputStream(fos);
			oos = new ObjectOutputStream(gzos);
			oos.writeObject(transferableTrack);
			oos.flush();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (gzos != null) {
				gzos.close();
			}
			if (fos != null) {
				fos.close();
			}
		}

	}
}
