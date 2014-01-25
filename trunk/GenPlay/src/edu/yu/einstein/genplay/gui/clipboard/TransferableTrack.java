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
package edu.yu.einstein.genplay.gui.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * Transferable track and its assembly for clipboard and cut and paste
 * @author Julien Lajugie
 */
public class TransferableTrack implements Transferable, Serializable {

	/** generated serial ID */
	public static final long serialVersionUID = -5416305611985714193L;

	/** Tranferable rack data flavor */
	public static DataFlavor trackDataFlavor = new DataFlavor(TransferableTrack.class, "GenPlay Track");

	private final Track 	trackToTransfer;	// track to transfer
	private final String 	assemblyName;		// assembly of the track to transfer


	/**
	 * Creates an instance of {@link TransferableTrack}
	 * @param trackToTransfer {@link Track} to transfer
	 */
	public TransferableTrack(Track trackToTransfer) {
		this.trackToTransfer = trackToTransfer;
		assemblyName = ProjectManager.getInstance().getAssembly().getName();
	}


	/**
	 * @return the name of the assembly of the track to transfer
	 */
	public String getAssemblyName() {
		return assemblyName;
	}


	/**
	 * @return the track to transfer
	 */
	public Track getTrackToTransfer() {
		return trackToTransfer;
	}


	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor == DataFlavor.javaFileListFlavor) {
			List<File> fileList = new ArrayList<File>();
			File file = writeSerializedData();
			fileList.add(file);
			return fileList;
		}
		if (flavor == DataFlavor.imageFlavor) {
			return TASaveAsImage.createImage(trackToTransfer);
		}
		if (flavor == DataFlavor.stringFlavor) {
			return trackToTransfer.getName();
		}
		if (flavor == trackDataFlavor) {
			return this;
		}
		return null;
	}


	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = {DataFlavor.imageFlavor, DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor, trackDataFlavor};
		return flavors;
	}


	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor == DataFlavor.imageFlavor) {
			return true;
		}
		if (flavor == DataFlavor.javaFileListFlavor) {
			return true;
		}
		if (flavor == DataFlavor.stringFlavor) {
			return true;
		}
		if (flavor == trackDataFlavor) {
			return true;
		}
		return false;
	}


	/**
	 * @return a File containing the {@link TransferableTrack} serialized
	 */
	private File writeSerializedData() {
		ObjectOutputStream oos = null;
		try {
			File tmpFile = File.createTempFile(trackToTransfer.getName(), ".gptf");
			FileOutputStream out = new FileOutputStream(tmpFile);
			oos = new ObjectOutputStream(out);
			oos.writeObject(this);
			oos.flush();
			return tmpFile;
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {}
		}
		return null;
	}
}
