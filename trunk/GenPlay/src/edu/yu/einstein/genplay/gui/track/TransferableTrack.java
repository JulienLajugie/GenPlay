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
package edu.yu.einstein.genplay.gui.track;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Transferable track and its assembly for clipboard and cut and paste
 * @author Julien Lajugie
 */
public class TransferableTrack implements Transferable, Serializable {

	/** generated serial ID */
	public static final long serialVersionUID = -5416305611985714193L;

	/** Tranferable rack data flavor */
	public static final DataFlavor TRACK_FLAVOR = new DataFlavor(TransferableTrack.class, "GenPlay Track");

	/** URI list flavor*/
	public static DataFlavor uriListFlavor = null;

	// create the flavor
	static {
		try {
			uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
		} catch (ClassNotFoundException e){} // can't happen
	}

	/** Data flavors supported by {@link TransferableTrack} */
	public static final DataFlavor[] TRANSFERABLE_TRACK_FLAVOR =  {
		DataFlavor.imageFlavor,
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor,
		TRACK_FLAVOR,
		uriListFlavor};


	/**
	 * @param transferable
	 * @return a track from the specified transferable
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 * @throws IncompatibleAssembliesException
	 * @throws URISyntaxException
	 */
	public static Track getTrackFromTransferable(Transferable transferable) throws UnsupportedFlavorException, IOException, IncompatibleAssembliesException, URISyntaxException {
		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		int i = 0;
		Track copiedTrack = null;
		while ((i < flavors.length) && (copiedTrack == null)) {
			TransferableTrack transTrack = null;
			if (flavors[i].match(TransferableTrack.TRACK_FLAVOR)) {
				transTrack = (TransferableTrack) transferable.getTransferData(flavors[i]);
			}
			if (transTrack != null) {
				if (transTrack.getAssemblyName().equals(ProjectManager.getInstance().getAssembly().getName())) {
					copiedTrack = transTrack.getTrackToTransfer();
				} else {
					throw new IncompatibleAssembliesException("The track cannot be pasted because the source "
							+ "and the target assemblies are not compatible.");
				}
			}
			i++;
		}
		return copiedTrack;
	}


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
	 * @return a File List containing one file with the {@link TransferableTrack} serialized
	 */
	private List<File> getDataAsFileList() {
		ObjectOutputStream oos = null;
		try {
			File tmpFile = new File(Utils.getTmpDirectoryPath(), trackToTransfer.getName() + ".gptf");
			FileOutputStream out = new FileOutputStream(tmpFile);
			oos = new ObjectOutputStream(out);
			oos.writeObject(this);
			oos.flush();
			List<File> fileList = new ArrayList<File>();
			fileList.add(tmpFile);
			return fileList;
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


	/**
	 * @return the track to transfer
	 */
	public Track getTrackToTransfer() {
		return trackToTransfer;
	}


	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.javaFileListFlavor)) {
			return getDataAsFileList();
		}
		if (flavor.equals(DataFlavor.imageFlavor)) {
			return TASaveAsImage.createImage(trackToTransfer);
		}
		if (flavor.equals(DataFlavor.stringFlavor)) {
			return trackToTransfer.getName();
		}
		if (flavor.equals(TRACK_FLAVOR)) {
			return this;
		}
		if (flavor.equals(uriListFlavor)) {
			List<File> fileList = getDataAsFileList();
			if (fileList == null) {
				return null;
			}
			if (flavor.isRepresentationClassInputStream() ||
					flavor.getRepresentationClass().equals(String.class)) {
				return fileList.get(0).toURI() + "\r\n";
			} else {
				return fileList;
			}
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return TRANSFERABLE_TRACK_FLAVOR;
	}


	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for(DataFlavor supportedFlavor: TRANSFERABLE_TRACK_FLAVOR) {
			if (flavor.match(supportedFlavor)) {
				return true;
			}
		}
		return false;
	}
}
