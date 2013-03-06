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
package edu.yu.einstein.genplay.dataStructure.list;

import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K>
 */
public class CacheTrack<K> {


	Map<Double, K> map;


	/**
	 * Constructor of {@link CacheTrack}
	 */
	public CacheTrack () {
		initialize();
	}


	/**
	 * Initialize/Reset the cache
	 */
	public void initialize () {
		map = new HashMap<Double, K>();
	}


	/**
	 * Set some data for xRatio.
	 * it will erase current data for that ratio.
	 * @param xRatio	the xRation
	 * @param data		the data to set
	 */
	public void setData (double xRatio, K data) {
		if (isCacheEnable()) {
			map.put(xRatio, data);
		}
	}


	/**
	 * @param xRatio an xRation
	 * @return the data associated to the xRatio, null if no match
	 */
	public K getData (double xRatio) {
		/*if (map.containsKey(xRatio)) {
			return map.get(xRatio);
		}*/
		return map.get(xRatio);
	}


	/**
	 * @param xRatio an xRatio
	 * @return true if the cache has data the given xRation, false otherwise
	 */
	public boolean hasData (double xRatio) {
		if (map.containsKey(xRatio)) {
			return true;
		}
		return false;
	}


	/**
	 * @return true is cache system is enabled, false otherwise
	 */
	private boolean isCacheEnable () {
		return ProjectManager.getInstance().getProjectConfiguration().isCacheTrack();
	}
}
