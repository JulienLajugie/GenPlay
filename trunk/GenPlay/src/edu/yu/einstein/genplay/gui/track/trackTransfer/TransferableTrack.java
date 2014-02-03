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
package edu.yu.einstein.genplay.gui.track.trackTransfer;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.writer.geneListWriter.GeneListAsBedWriter;
import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayTrackFilter;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.ListView.ChromosomeWindowListViews;

/**
 * Transferable track singleton for clipboard, cut and paste
 * @author Julien Lajugie
 */
public class TransferableTrack implements Transferable {

	/** Max element to put in the clipboard for the text flavor containing the data of a track */
	private static final int MAX_ELEMENT_TO_PUT_IN_CLIPBOARD = 1000;

	/** Tranferable rack data flavor */
	public transient static final DataFlavor TRACK_FLAVOR = new DataFlavor(TransferableTrack.class, "GenPlay Track");

	/** URI list flavor*/
	public transient static DataFlavor uriListFlavor = null;

	// create the flavor
	static {
		try {
			uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
		} catch (ClassNotFoundException e){} // can't happen
	}

	private static 	TransferableTrack 	instance = null; // singleton instance

	/** Data flavors supported by {@link TransferableTrack} */
	public static final DataFlavor[] TRANSFERABLE_TRACK_FLAVOR =  {
		DataFlavor.imageFlavor,
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor,
		TRACK_FLAVOR,
		uriListFlavor
	};


	/**
	 * @param transferable a {@link Transferable}
	 * @return Retrieve a file from the transferable if possible
	 */
	public static File getFileFromTransferable(Transferable transferable) {
		try {
			try {
				// try to retrieve javaFileListFlavor data
				@SuppressWarnings("unchecked")
				List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (!fileList.isEmpty()) {
					return fileList.get(0);
				}
			} catch (UnsupportedFlavorException e) {}
			try {
				// try to retrieve uriListFlavor data
				String fileNameList = (String)  transferable.getTransferData(TransferableTrack.uriListFlavor);
				return new File(new URI(fileNameList.split("\r\n")[0]));
			} catch (UnsupportedFlavorException e) {}
		} catch (Exception e) {}
		// cannot retrieve data, return null
		return null;
	}


	/**
	 * @return an instance of a {@link ConfigurationManager}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static TransferableTrack getInstance() {
		if (instance == null) {
			synchronized(TransferableTrack.class) {
				if (instance == null) {
					instance = new TransferableTrack();
				}
			}
		}
		return instance;
	}


	/**
	 * @param transferable
	 * @return a track from the specified transferable. Null if it cannot be extracted.
	 * @throws IOException
	 * @throws IncompatibleAssembliesException
	 */
	public static Track getTrackFromTransferable(Transferable transferable) throws IOException, IncompatibleAssembliesException {
		try {
			TrackForTransfer transTrack = (TrackForTransfer) transferable.getTransferData(TRACK_FLAVOR);
			return transTrack.getTrackForTransfer();
		} catch (UnsupportedFlavorException e) {
			return null;
		}
	}


	private Image				trackImage;			// image for the paste of the track as image
	private TrackForTransfer	track;				// track to transfer


	/**
	 * Creates an instance of {@link TransferableTrack}
	 * @param trackToTransfer {@link Track} to transfer
	 */
	private TransferableTrack() {}


	/**
	 * @return a File List containing one file with the {@link TransferableTrack} serialized
	 */
	private List<File> getDataAsFileList() {
		ObjectOutputStream oos = null;
		try {
			File tmpFile = new File(Utils.getTmpDirectoryPath(), track.getTrackForTransfer().getName() + GenPlayTrackFilter.EXTENSIONS[0]);
			FileOutputStream out = new FileOutputStream(tmpFile);
			oos = new ObjectOutputStream(out);
			oos.writeObject(track);
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


	private String getTrackDataAsString() {
		Layer<?> activeLayer = track.getTrackForTransfer().getActiveLayer();
		if (activeLayer != null) {
			Object data = activeLayer.getData();
			GenomeWindow displayedWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
			Chromosome chr = displayedWindow.getChromosome();
			if (data instanceof SCWList) {
				SCWList scwList = (SCWList) data;
				ListView<ScoredChromosomeWindow> chrLV = ChromosomeWindowListViews.subList(scwList, displayedWindow);
				String clipboardString = "";
				int i =0;
				for (i = 0; (i < chrLV.size()) && (i < MAX_ELEMENT_TO_PUT_IN_CLIPBOARD); i++) {
					clipboardString += chr + "\t" + chrLV.get(i).getStart() + "\t" + chrLV.get(i).getStop() + "\t" + chrLV.get(i).getScore() + "\n";
				}
				if (i >= MAX_ELEMENT_TO_PUT_IN_CLIPBOARD) {
					clipboardString += "and more...\n";
				}
				return clipboardString;
			} else if (data instanceof GeneList) {
				GeneList scwList = (GeneList) data;
				ListView<Gene> chrLV = ChromosomeWindowListViews.subList(scwList, displayedWindow);
				String clipboardString = "";
				int i = 0;
				for (i = 0; (i < chrLV.size()) && (i < MAX_ELEMENT_TO_PUT_IN_CLIPBOARD); i++) {
					clipboardString += GeneListAsBedWriter.geneToString(chrLV.get(i), chr) + "\n";
				}
				if (i >= MAX_ELEMENT_TO_PUT_IN_CLIPBOARD) {
					clipboardString += "and more...\n";
				}
				return clipboardString;
			}
		}
		return null;
	}


	/**
	 * @return the track to transfer
	 */
	public Track getTrackToTransfer() {
		return track.getTrackForTransfer();
	}


	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.javaFileListFlavor)) {
			return getDataAsFileList();
		}
		if (flavor.equals(DataFlavor.imageFlavor)) {
			if (trackImage == null) {
				return TASaveAsImage.createImage(track.getTrackForTransfer());
			} else {
				return trackImage;
			}
		}
		if (flavor.equals(DataFlavor.stringFlavor)) {
			String dataString = getTrackDataAsString();
			if (dataString != null) {
				// if we can retrieve the data from the active layer we put it in the clipboard
				return dataString;
			} else {
				// otherwise the default is the track name
				return track.getTrackForTransfer().getName();
			}
		}
		if (flavor.equals(TRACK_FLAVOR)) {
			return track;
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


	/**
	 * Set the image of the track that needs to be transfered.
	 * Used if the track is paste as an image.
	 * If the image is not specified before transfer the transferable
	 * will try to generate an image. This can result in an error if
	 * the track is not visible
	 * @param trackImage image of the track to be transfered
	 */
	public void setImageToTransfer(Image trackImage) {
		this.trackImage = trackImage;

	}


	/**
	 * Sets the track that need to be transfered
	 * @param trackToTransfer {@link Track} to transfer
	 */
	public void setTrackToTransfer(Track trackToTransfer) {
		track = new TrackForTransfer(trackToTransfer);
	}
}
