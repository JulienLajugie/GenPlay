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
package edu.yu.einstein.genplay.core.manager.recording;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class manages the saving and loading processes of the current project.
 * @author Nicolas Fourel
 */
public class ProjectRecording {

	private File 				fileToLoad; // The project file to load
	private Track[] 			trackList; // The list of tracks to save
	private FileInputStream 	fis;
	private GZIPInputStream 	gz;
	private ObjectInputStream 	ois; // The input file stream
	private ProjectInformation 	projectInformation; // The project information
	private boolean 			trackListReadyToLoad = false; // Checks if the list of track can be loaded
	private boolean 			mgManagerReadyToLoad = false; // Checks if the multi genome manager can be loaded
	private boolean 			loadingEvent = false; // Checks if the request is for loading or for saving
	private String 				currentProjectPath; // path to the current project


	/**
	 * Creates an instance of {@link ProjectRecording}
	 */
	protected ProjectRecording() {
		ois = null;
		projectInformation = null;
		trackListReadyToLoad = false;
		mgManagerReadyToLoad = false;
		loadingEvent = false;
	}


	/**
	 * Closes input streams
	 */
	public void closeStreams() {
		try {
			ois.close();
		} catch (IOException e) {
			ExceptionManager.getInstance().caughtException(e);
		}

		try {
			gz.close();
		} catch (IOException e) {
			ExceptionManager.getInstance().caughtException(e);
		}

		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}

		ois = null;
		gz = null;
		fis = null;
	}


	/**
	 * @return the fileToLoad
	 */
	public File getFileToLoad() {
		return fileToLoad;
	}


	/**
	 * @return the projectInformation
	 */
	public ProjectInformation getProjectInformation() {
		return projectInformation;
	}


	/**
	 * Reads the track list object.
	 * @return a track list
	 */
	public Track[] getTrackList() {
		if (trackListReadyToLoad) {
			try {
				trackList = (Track[]) ois.readObject();
				trackListReadyToLoad = false;
			} catch (InvalidClassException e) {
				String invalidJREMessage = "<html><center>The project you are trying to load was saved using a Java Runtime Environment (JRE) that is not compatible with the JRE currently installed on your computer.<br/>";
				invalidJREMessage += "Please refer to the FAQ page at <a href=\"http://genplay.einstein.yu.edu\">http://genplay.einstein.yu.edu</a> for additional information.</center></html>";
				JOptionPane.showMessageDialog(MainFrame.getInstance().getRootPane(), invalidJREMessage, "Invalid Java Runtime Environment", JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			} catch (ClassNotFoundException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
			return trackList;
		}
		return null;
	}


	/**
	 * Initializes the manager of the multi genome part
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void initMultiGenomeManager() throws IOException, ClassNotFoundException {
		if (mgManagerReadyToLoad && ProjectManager.getInstance().isMultiGenomeProject()) {
			ProjectManager.getInstance().readMultiGenomeObject(ois); // read the multi-genome manager
			ois.readObject(); // read the MGDisplaySettings
		}
		trackListReadyToLoad = true;
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param inputFile project file
	 * @throws Exception
	 */
	public void initObjectInputStream(File inputFile) throws Exception {
		fileToLoad = inputFile;
		fis = new FileInputStream(inputFile);
		initObjectInputStream(fis);
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param is InputStream object
	 * @throws Exception
	 */
	public void initObjectInputStream(InputStream is) throws Exception {
		try {
			gz = new GZIPInputStream(is);
			ois = new ObjectInputStream(gz);
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type
			throw new InvalidFileTypeException();
		}
	}


	/**
	 * Initializes the project information. It unserializes the first object
	 * contained in the file that is the information about the project. This
	 * method must be the first one to be called once the object input stream
	 * has been created.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void initProjectInformation() throws ClassNotFoundException, IOException {
		if (ois != null) {
			projectInformation = (ProjectInformation) ois.readObject();
		}
	}


	/**
	 * Initializes the project manager. It unserializes the second object
	 * contained in the file that is the project manager. This method must be
	 * the second one to be called once the object input stream has been
	 * created.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void initProjectManager() throws ClassNotFoundException, IOException {
		if (ois != null) {
			ois.readObject(); // read the project manager
			// set the project name to match the file name
			if (fileToLoad != null) {
				ProjectManager.getInstance().setProjectName(Utils.getFileNameWithoutExtension(new File(fileToLoad.getName())));
			}
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				mgManagerReadyToLoad = true;
				trackListReadyToLoad = false;
			} else {
				mgManagerReadyToLoad = false;
				trackListReadyToLoad = true;
			}
		}
	}


	/**
	 * @return the loadingEvent
	 */
	public boolean isLoadingEvent() {
		return loadingEvent;
	}


	/**
	 * Saves the current list of tracks into a file
	 * @param outputFile file where the project needs to be saved
	 * @return true if the saving was successful. Returns false otherwise
	 */
	public boolean saveProject(File outputFile) {
		try {
			TrackListPanel trackListPanel = MainFrame.getInstance().getTrackListPanel();
			// remove all the references to the listener so we don't save them
			for (Track currentTrack : trackListPanel.getModel().getTracks()) {
				currentTrack.removeTrackListener(trackListPanel);
			}
			FileOutputStream fos = new FileOutputStream(outputFile);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			// there is bug during the serialization with the nimbus LAF if the
			// track list is visible
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				trackListPanel.setViewportView(null);
			}
			updatesCurrentProjectInformation();
			oos.writeObject(projectInformation);
			oos.writeObject(ProjectManager.getInstance());
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				oos.writeObject(ProjectManager.getInstance().getMultiGenomeProject());
				oos.writeObject(MGDisplaySettings.getInstance());
			}
			oos.writeObject(trackListPanel.getModel().getTracks());

			// there is bug during the serialization with the nimbus LAF if the
			// track list is visible
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				trackListPanel.setViewportView(trackListPanel.getJpTrackList());
			}
			oos.flush();
			oos.close();
			gz.flush();
			gz.close();
			fos.flush();
			fos.close();
			// rebuild the references to the listener
			for (Track currentTrack : trackListPanel.getModel().getTracks()) {
				currentTrack.addTrackListener(trackListPanel);
			}
			// ProjectManager.getInstance().getProjectConfiguration().writeConfigurationFile();
			// // deactivate the configuration file saving
		} catch (IOException e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "An error occurred while saving the project");
			return false;
		}
		return true;
	}


	/**
	 * @param currentProjectPath the currentProjectPath to set
	 */
	public void setCurrentProjectPath(String currentProjectPath) {
		this.currentProjectPath = currentProjectPath;
	}


	/**
	 * @param fileToLoad the fileToLoad to set
	 */
	public void setFileToLoad(File fileToLoad) {
		this.fileToLoad = fileToLoad;
	}


	/**
	 * @param loadingEvent the loadingEvent to set
	 */
	public void setLoadingEvent(boolean loadingEvent) {
		this.loadingEvent = loadingEvent;
	}


	/**
	 * Updates the current information about the project
	 */
	public void updatesCurrentProjectInformation() {
		projectInformation = new ProjectInformation();
		projectInformation.setFile(new File(currentProjectPath));
		ProjectManager projectManager = ProjectManager.getInstance();
		projectInformation.setProjectName(projectManager.getProjectName());
		if (projectManager.getProjectScorePrecision() == null) {
			// for compatibility with old projects when they were no score precision
			projectInformation.setProjectPrecision("Unknown");
		} else {
			projectInformation.setProjectPrecision(projectManager.getProjectScorePrecision().toString());
		}
		projectInformation.setProjectGenome(projectManager.getGenomeName());
		if (projectManager.isMultiGenomeProject()) {
			projectInformation.setProjectType("Multi Genome Project");
		} else {
			projectInformation.setProjectType("Single Genome Project");
		}

		GregorianCalendar calendar = new GregorianCalendar();
		String currentDate = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR);
		projectInformation.setProjectDate(currentDate);

		// we count the number of non-empty tracks in the track list
		TrackListPanel trackListPanel = MainFrame.getInstance().getTrackListPanel();
		Integer trackCount = 0;
		for (Track currentTrack : trackListPanel.getModel().getTracks()) {
			if (currentTrack.getLayers().size() > 0) {
				trackCount++;
			}
		}
		projectInformation.setProjectTrackNumber(Integer.toString(trackCount));

		if (ProjectFiles.getInstance().isFileDependant()) {
			projectInformation.setProjectFiles(ProjectFiles.getInstance().getValidArrayOfFiles());
		}
	}
}
