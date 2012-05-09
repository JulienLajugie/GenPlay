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
package edu.yu.einstein.genplay.core.manager.recording;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.UIManager;

import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackList.TrackList;


/**
 * This class manages the saving and loading processes of the current project.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectRecording {

	private 		File 					fileToLoad;							// The project file to load
	private 		Track<?>[]				trackList;							// The list of tracks to save
	private 		ObjectInputStream 		ois;								// The input file stream
	private 		ProjectInformation		projectInformation;					// The project information
	private 		boolean 				trackListReadyToLoad 	= false;	// Checks if the list of track can be loaded
	private			boolean 				loadingEvent	 		= false;	// Checks if the request is for loading or for saving
	private 		String 					currentProjectPath;					// path to the current project


	protected ProjectRecording () {
		ois = null;
		projectInformation = null;
		trackListReadyToLoad = false;
		loadingEvent = false;
	}


	/**
	 * Saves the current list of tracks into a file
	 * @param outputFile file where the project needs to be saved
	 * @return true if the saving was successful. Returns false otherwise
	 */
	public boolean saveProject(File outputFile) {
		try {
			TrackList trackList = MainFrame.getInstance().getTrackList();
			// remove all the references to the listener so we don't save them
			for (Track<?> currentTrack: trackList.getTrackList()) {
				currentTrack.removePropertyChangeListener(trackList);
			}
			FileOutputStream fos = new FileOutputStream(outputFile);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			// there is bug during the serialization with the nimbus LAF if the track list is visible 
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				trackList.setViewportView(null);
			}
			updatesCurrentProjectInformation();
			oos.writeObject(projectInformation);
			oos.writeObject(ProjectManager.getInstance());
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				oos.writeObject(MGDisplaySettings.getInstance());
			}
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
			}
			//ProjectManager.getInstance().getProjectConfiguration().writeConfigurationFile(); 	// deactivate the configuration file saving
		} catch (IOException e) {
			ExceptionManager.handleException(MainFrame.getInstance().getRootPane(), e, "An error occurred while saving the project");
			return false;
		}
		return true;
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param inputFile		project file
	 * @throws Exception
	 */
	public void initObjectInputStream (File inputFile) throws Exception {
		fileToLoad = inputFile;
		FileInputStream fis = new FileInputStream(inputFile);
		initObjectInputStream(fis);
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param is InputStream object
	 * @throws Exception
	 */
	public void initObjectInputStream (InputStream is) throws Exception {
		try {
			GZIPInputStream gz = new GZIPInputStream(is);
			ois = new ObjectInputStream(gz);
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type 
			throw new InvalidFileTypeException();
		}
	}


	/**
	 * Initializes the project information.
	 * It unserializes the first object contained in the file that is the information about the project.
	 * This method must be the first one to be called once the object input stream has been created.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void initProjectInformation () throws ClassNotFoundException, IOException {
		if (ois != null) {
			projectInformation = (ProjectInformation) ois.readObject();
		}
	}


	/**
	 * Initializes the project manager.
	 * It unserializes the second object contained in the file that is the project manager.
	 * This method must be the second one to be called once the object input stream has been created.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void initProjectManager () throws ClassNotFoundException, IOException {
		if (ois != null) {
			ois.readObject();		// read the project manager
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				System.out.println("ProjectRecording.initProjectManager() isMultiGenomeProject");
				ois.readObject();		// read the MGDisplaySettings
			} else {
				System.out.println("ProjectRecording.initProjectManager() !isMultiGenomeProject");
			}
			trackListReadyToLoad = true;
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
	 * @return the projectInformation
	 */
	public ProjectInformation getProjectInformation() {
		return projectInformation;
	}


	/**
	 * Updates the current information about the project
	 */
	public void updatesCurrentProjectInformation() {
		projectInformation = new ProjectInformation();
		projectInformation.setFile(new File(currentProjectPath));
		ProjectManager projectManager = ProjectManager.getInstance();
		projectInformation.setProjectName(projectManager.getProjectName());
		projectInformation.setProjectGenome(projectManager.getGenomeName());
		if (projectManager.isMultiGenomeProject()) {
			projectInformation.setProjectType("Multi Genome Project");
		} else {
			projectInformation.setProjectType("Single Genome Project");
		}

		GregorianCalendar calendar = new GregorianCalendar();
		String currentDate = (calendar.get(Calendar.MONTH) + 1) + "/" +
		calendar.get(Calendar.DATE) + "/" +
		calendar.get(Calendar.YEAR);
		projectInformation.setProjectDate(currentDate);

		// we count the number of non-empty tracks in the track list
		TrackList trackList = MainFrame.getInstance().getTrackList();
		Integer trackCount = 0;
		for (Track<?> currentTrack: trackList.getTrackList()) {
			if (!(currentTrack instanceof EmptyTrack)) {
				trackCount++;
			}
		}
		projectInformation.setProjectTrackNumber(Integer.toString(trackCount));

		if (ProjectFiles.getInstance().isFileDependant()) {
			projectInformation.setProjectFiles(ProjectFiles.getInstance().getValidArrayOfFiles());
		}
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


	/**
	 * @param currentProjectPath the currentProjectPath to set
	 */
	public void setCurrentProjectPath(String currentProjectPath) {
		this.currentProjectPath = currentProjectPath;
	}

}
