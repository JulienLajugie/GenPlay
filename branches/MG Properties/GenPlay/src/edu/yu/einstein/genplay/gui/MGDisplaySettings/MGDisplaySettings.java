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
package edu.yu.einstein.genplay.gui.MGDisplaySettings;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGDisplaySettings {

	private static MGDisplaySettings 	instance;			// Instance of the class

	private MGFilterSettings 	filterSettings; 	// All settings about the filters
	private MGStripeSettings 	stripeSettings; 	// All settings about the stripes
	private MGVariousSettings 	variousSettings;	// All settings about various settings


	/**
	 * @return an instance of a {@link MGDisplaySettings}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static MGDisplaySettings getInstance() {
		if (instance == null) {
			instance = new MGDisplaySettings();
		}
		return instance;
	}


	/**
	 * Constructor of {@link MGDisplaySettings}
	 */
	private MGDisplaySettings () {
		filterSettings = new MGFilterSettings();
		stripeSettings = new MGStripeSettings();
		variousSettings = new MGVariousSettings();
	}


	/**
	 * @return the filterSettings
	 */
	public MGFilterSettings getFilterSettings() {
		return filterSettings;
	}


	/**
	 * @return the stripeSettings
	 */
	public MGStripeSettings getStripeSettings() {
		return stripeSettings;
	}


	/**
	 * @return the variousSettings
	 */
	public MGVariousSettings getVariousSettings() {
		return variousSettings;
	}
	
	
	/**
	 * Show the settings
	 */
	public void showSettings () {
		variousSettings.showSettings();
		filterSettings.showSettings();
		stripeSettings.showSettings();
	}

}
