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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.UIManager;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.exception.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackList.TrackList;


/**
 * This class manages the project records.
 * It allows application to:
 * 	- save a project
 * 	- load a project
 * 	- load project basics information (not the track list)
 * @author Nicolas
 */
public class ProjectRecordingManager {

	private static ProjectRecordingManager instance;
	private File 				fileToLoad;
	private Track<?>[]			trackList;
	private ObjectInputStream 	ois;
	private boolean 			trackListReadyToLoad = false;
	private boolean 			loadingEvent = false;
	
	
	/**
	 * @return the instance of the singleton {@link ProjectRecordingManager}.
	 */
	public static ProjectRecordingManager getInstance () {
		if (instance == null) {
			instance = new ProjectRecordingManager();
		}
		return instance;
	}
	
	
	
	/**
	 * Saves the current list of tracks into a file
	 */
	public void saveProject(File outputFile) {
		try {
			TrackList trackList = MainFrame.getInstance().getTrackList();
			// remove all the references to the listener so we don't save them
			for (Track<?> currentTrack: trackList.getTrackList()) {
				currentTrack.removePropertyChangeListener(trackList);
				currentTrack.removeGenomeWindowListener(trackList);
			}
			FileOutputStream fos = new FileOutputStream(outputFile);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			// there is bug during the serialization with the nimbus LAF if the track list is visible 
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				trackList.setViewportView(null);
			}
			oos.writeObject(ChromosomeManager.getInstance().getProjectName());
			oos.writeObject(ChromosomeManager.getInstance().getCladeName());
			oos.writeObject(ChromosomeManager.getInstance().getGenomeName());
			oos.writeObject(ChromosomeManager.getInstance().getVarFiles());
			oos.writeObject(ChromosomeManager.getInstance().getAssembly());
			Integer count = 0;
			for (Track<?> currentTrack: trackList.getTrackList()) {
				if (!(currentTrack instanceof EmptyTrack)) {
					count++;
				}
			}
			oos.writeObject(count);
			oos.writeObject(trackList.getTrackList());
			
			// there is bug during the serialization with the nimbus LAF if the track list is visible
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				trackList.setViewportView(trackList.getJpTrackList());
			}
			oos.flush();
			oos.close();
			gz.flush();
			gz.close();
			fos.flush();
			fos.close();
			// rebuild the references to the listener
			for (Track<?> currentTrack: trackList.getTrackList()) {
				currentTrack.addPropertyChangeListener(trackList);
				currentTrack.addGenomeWindowListener(trackList);
			}
			ConfigurationManager.getInstance().setCurrentProjectPath(outputFile.getPath());
			ConfigurationManager.getInstance().writeConfigurationFile();
		} catch (IOException e) {
			ExceptionManager.handleException(MainFrame.getInstance().getRootPane(), e, "An error occurred while saving the project"); 
		}
	}
	
	
	/**
	 * Creates/sets chromosome manager object.
	 * A project file has to be set before!
	 * @throws Exception
	 */
	public void createChromosomeManager () throws Exception {
		createChromosomeManager (fileToLoad);
	}
	
	
	/**
	 * Creates/sets chromosome manager object.
	 * @param inputFile		project file
	 * @throws Exception
	 */
	public void createChromosomeManager (File inputFile) throws Exception {
		fileToLoad = inputFile;
		FileInputStream fis = new FileInputStream(inputFile);
		createChromosomeManager(fis);
	}
	
	
	/**
	 * Creates/sets chromosome manager object.
	 * @param is	InputStream object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createChromosomeManager (InputStream is) throws Exception {
		try {
			GZIPInputStream gz = new GZIPInputStream(is);
			ois = new ObjectInputStream(gz);
			
			ChromosomeManager instance = ChromosomeManager.getInstance();
			
			instance.setProjectName((String)ois.readObject());
			instance.setCladeName((String)ois.readObject());
			instance.setGenomeName((String)ois.readObject());
			instance.setVarFiles((List<File>)ois.readObject());
			instance.setAssembly((Assembly)ois.readObject());
			ois.readObject();// read the track number object but don't affected because not used
			
			trackListReadyToLoad = true;
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type 
			throw new InvalidFileTypeException();
		}
	}
	
	
	/**
	 * Reads the track list object.
	 * @return a track list
	 */
	public Track<?>[] getTrackList() {
		if (trackListReadyToLoad) {
			try {
				trackList = (Track[])ois.readObject();
				trackListReadyToLoad = false;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return trackList;
		}
		return null;
	}
	
	
	/**
	 * Reads the project basics information.
	 * They are displayed on the loading project screen.
	 * Track list are not in those information.
	 * @param inputFile	project file
	 * @return			an ordered string list
	 * @throws Exception
	 */
	public List<String> getProjectHeader (File inputFile) throws Exception {
		List<String> list = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			GZIPInputStream gz = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(gz);
			
			String projectName = (String)ois.readObject();
			ois.readObject();// read the clade object but don't affected because not used
			String genomeName = (String)ois.readObject();
			List<?> varFiles = (List<?>)ois.readObject();
			String assemblyName = ((Assembly)ois.readObject()).getDisplayName();
			Integer count = (Integer)ois.readObject();
			
			list.add(projectName);
			list.add(genomeName + " - " + assemblyName);
			if (varFiles != null) {
				list.add("multi");
			} else {
				list.add("simple");
			}
			Date date = new Date(inputFile.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("MM / d / yyyy");
			list.add(sdf.format(date));
			list.add("" + count);
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type 
			throw new InvalidFileTypeException();
		}
		return list;
	}


	/**
	 * @return the fileToLoad
	 */
	public File getFileToLoad() {
		return fileToLoad;
	}


	/**
	 * @param fileToLoad the fileToLoad to set
	 */
	public void setFileToLoad(File fileToLoad) {
		this.fileToLoad = fileToLoad;
	}


	/**
	 * @return the loadingEvent
	 */
	public boolean isLoadingEvent() {
		return loadingEvent;
	}


	/**
	 * @param loadingEvent the loadingEvent to set
	 */
	public void setLoadingEvent(boolean loadingEvent) {
		this.loadingEvent = loadingEvent;
	}
	
}