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
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.UIManager;

import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
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
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class ProjectRecordingManager {


	/**
	 * This class contains information about a project
	 * @author Julien Lajugie
	 */
	public class ProjectInformations implements Serializable {

		private static final long serialVersionUID = 5252641489962010266L; // generated ID
		private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
		private String projectName;				// project name
		private String projectGenome;			// project genome
		private String projectType;				// project type
		private String projectDate;				// project date
		private String projectTrackNumber;		// number of track in the project

		
		/**
		 * Method used for serialization
		 * @param out
		 * @throws IOException
		 */
		private void writeObject(ObjectOutputStream out) throws IOException {
			out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
			out.writeObject(projectName);
			out.writeObject(projectGenome);
			out.writeObject(projectType);
			out.writeObject(projectDate);
			out.writeObject(projectTrackNumber);
		}


		/**
		 * Method used for unserialization
		 * @param in
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.readInt();
			projectName = (String) in.readObject();
			projectGenome = (String) in.readObject();
			projectType = (String) in.readObject();
			projectDate = (String) in.readObject();
			projectTrackNumber = (String) in.readObject();
		}


		/**
		 * @param projectName the projectName to set
		 */
		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}


		/**
		 * @return the projectName
		 */
		public String getProjectName() {
			return projectName;
		}


		/**
		 * @param projectGenome the projectGenome to set
		 */
		public void setProjectGenome(String projectGenome) {
			this.projectGenome = projectGenome;
		}


		/**
		 * @return the projectGenome
		 */
		public String getProjectGenome() {
			return projectGenome;
		}


		/**
		 * @param projectType the projectType to set
		 */
		public void setProjectType(String projectType) {
			this.projectType = projectType;
		}


		/**
		 * @return the projectType
		 */
		public String getProjectType() {
			return projectType;
		}


		/**
		 * @param projectDate the projectDate to set
		 */
		public void setProjectDate(String projectDate) {
			this.projectDate = projectDate;
		}


		/**
		 * @return the projectDate
		 */
		public String getProjectDate() {
			return projectDate;
		}


		/**
		 * @param projectTrackNumber the projectTrackNumber to set
		 */
		public void setProjectTrackNumber(String projectTrackNumber) {
			this.projectTrackNumber = projectTrackNumber;
		}


		/**
		 * @return the projectTrackNumber
		 */
		public String getProjectTrackNumber() {
			return projectTrackNumber;
		}		
	}


	private static 	ProjectRecordingManager instance;							// Unique instance of the singleton
	private 		File 					fileToLoad;							// The project file to load
	private 		Track<?>[]				trackList;							// The list of tracks to save
	private 		ObjectInputStream 		ois;								// The input file stream
	private 		boolean 				trackListReadyToLoad 	= false;	// Checks if the list of track can be loaded
	private			boolean 				loadingEvent	 		= false;	// Checks if the request is for loading or for saving


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
	 * @param outputFile file where the project needs to be saved
	 * @return true if the saving was successful. Returns false otherwise 
	 */
	public boolean saveProject(File outputFile) {
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
			oos.writeObject(retrieveProjectInformations());
			oos.writeObject(ProjectManager.getInstance());
			oos.writeObject(ChromosomeManager.getInstance());
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				oos.writeObject(MultiGenomeManager.getInstance());
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
				currentTrack.addGenomeWindowListener(trackList);
			}
			ConfigurationManager.getInstance().setCurrentProjectPath(outputFile.getPath());
			ConfigurationManager.getInstance().writeConfigurationFile();
		} catch (IOException e) {
			ExceptionManager.handleException(MainFrame.getInstance().getRootPane(), e, "An error occurred while saving the project");
			return false;
		}
		return true;
	}


	/**
	 * @return a {@link ProjectInformations} object containing informations about the project
	 */
	private ProjectInformations retrieveProjectInformations() {
		ProjectInformations projectInformations = new ProjectInformations();
		ProjectManager projectManager = ProjectManager.getInstance();
		projectInformations.setProjectName(projectManager.getProjectName());
		projectInformations.setProjectGenome(projectManager.getGenomeName());
		if (projectManager.isMultiGenomeProject()) {
			projectInformations.setProjectType("Multi Genome Project");
		} else {
			projectInformations.setProjectType("Simple Genome Project");
		}

		GregorianCalendar calendar = new GregorianCalendar();
		String currentDate = (calendar.get(Calendar.MONTH) + 1) + "/" +
		calendar.get(Calendar.DATE) + "/" +
		calendar.get(Calendar.YEAR);
		projectInformations.setProjectDate(currentDate);

		// we count the number of non-empty tracks in the track list
		TrackList trackList = MainFrame.getInstance().getTrackList();
		Integer trackCount = 0;
		for (Track<?> currentTrack: trackList.getTrackList()) {
			if (!(currentTrack instanceof EmptyTrack)) {
				trackCount++;
			}
		}		
		projectInformations.setProjectTrackNumber(Integer.toString(trackCount));
		return projectInformations;
	}


	/**
	 * Creates/sets chromosome manager object.
	 * A project file has to be set before!
	 * @throws Exception
	 */
	public void initManagers () throws Exception {
		initManagers (fileToLoad);
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param inputFile		project file
	 * @throws Exception
	 */
	public void initManagers (File inputFile) throws Exception {
		fileToLoad = inputFile;
		FileInputStream fis = new FileInputStream(inputFile);
		initManagers(fis);
	}


	/**
	 * Creates/sets chromosome manager object.
	 * @param is	InputStream object
	 * @throws Exception
	 */
	public void initManagers (InputStream is) throws Exception {
		try {
			GZIPInputStream gz = new GZIPInputStream(is);
			ois = new ObjectInputStream(gz);
			ois.readObject();
			ois.readObject(); // init the project manager
			ois.readObject(); // init the chromosome manager
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				ois.readObject(); // multi-genome manager
			}
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
	 * Retrieve the project informations from an input file
	 * @param inputFile
	 * @return the {@link ProjectInformations} of the specified input file
	 * @throws Exception
	 */
	public ProjectInformations getProjectInformation(File inputFile) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			GZIPInputStream gz = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(gz);
			ProjectInformations projectInformations = (ProjectInformations) ois.readObject();
			return projectInformations;
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type 
			throw new InvalidFileTypeException();
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
}
