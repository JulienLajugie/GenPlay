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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class provides tools to manage the different levels of zoom available.
 * This class follows the design pattern <i>Singleton</i> 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ZoomManager implements Serializable {

	private static final long serialVersionUID = -1885523812708037537L;	// generated ID
	private static ZoomManager instance = null;		// unique instance of the singleton
	
	private static final int[] DEFAULT_ZOOM = 
	{10, 20, 50, 
		100, 200, 500, 
		1000, 2000, 5000, 
		10000, 20000, 50000,
		100000, 200000, 500000, 
		1000000, 2000000, 5000000, 
		10000000, 20000000,	50000000, 
		100000000, 200000000, 500000000 };				// default zooms
	private int[] 			zoomSizes = DEFAULT_ZOOM;	// the different zoom sizes available


	/**
	 * @return an instance of a {@link ZoomManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ZoomManager getInstance() {
		if (instance == null) {
			synchronized(ZoomManager.class) {
				if (instance == null) {
					instance = new ZoomManager();
				}
			}
		}
		return instance;
	}

	
	/**
	 * Private constructor of the singleton. Creates an instance of a {@link ZoomManager}.
	 */
	private ZoomManager() {
		super();
	}
	

	/**
	 * @param index index of a zoom
	 * @return value of the zoom associated to index
	 */
	public int getZoom(int index)  {
		return zoomSizes[index];
	}


	/**
	 * @param currentZoom the current zoom value
	 * @return the new zoom value after a zoom out
	 */
	public int getZoomIn(int currentZoom) {
		int currentZoomIndex = java.util.Arrays.binarySearch(zoomSizes, currentZoom);
		if (currentZoomIndex < 0) {
			currentZoomIndex = -currentZoomIndex - 1;
		}
		if (currentZoomIndex > 0) {
			currentZoomIndex--;
		}
		return zoomSizes[currentZoomIndex];
	}	


	/**
	 * @param zoom a zoom
	 * @return the index of  the zoom in parameter in the array of different zooms available.
	 * The index of the first element smaller is the zoom is not found
	 */
	public int getZoomIndex(int zoom)  {
		int zoomIndex = java.util.Arrays.binarySearch(zoomSizes, zoom);
		if (zoomIndex < 0) {
			zoomIndex = -zoomIndex - 1;
		}
		if (zoomIndex >= zoomSizes.length) {
			zoomIndex = zoomSizes.length - 1;
		}
		return zoomIndex;
	}


	/**
	 * @param currentZoom the current zoom value
	 * @return the new zoom value after a zoom in 
	 */
	public int getZoomOut(int currentZoom) {
		int currentZoomIndex = java.util.Arrays.binarySearch(zoomSizes, currentZoom);
		if (currentZoomIndex < 0) {
			currentZoomIndex = -currentZoomIndex - 2;
		}
		if (currentZoomIndex < zoomSizes.length - 1) {
			currentZoomIndex++;
		}
		return zoomSizes[currentZoomIndex];
	}


	/**
	 * @return the array of the different zoom available
	 */
	public int[] getZoomSizes() {
		return zoomSizes;
	}


	/**
	 * Load the data of the manager.
	 * @param configurationFile configuration file
	 * @throws IOException 
	 */
	public void loadConfigurationFile(File configurationFile) throws IOException {
		BufferedReader reader = null;
		try {
			ArrayList<Integer> listTmp = new ArrayList<Integer>();
			// try to open the input file
			reader = new BufferedReader(new FileReader(configurationFile));
			// extract data
			String line = null;
			while((line = reader.readLine()) != null) {
				listTmp.add(Integer.parseInt(line));			
			}
			zoomSizes = new int[listTmp.size()];
			for (int i = 0; i < listTmp.size(); i++) {
				zoomSizes[i] = listTmp.get(i);
			}
			Arrays.sort(zoomSizes);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}	
	}


	/**
	 * Methods used for the serialization of the singleton object.
	 * The readResolve method is called when ObjectInputStream has 
	 * read an object from the stream and is preparing to return it 
	 * to the caller.
	 * See javadocs for more information
	 * @return the unique instance of the singleton
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return getInstance();
	}
}
