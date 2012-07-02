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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGVariousSettings implements Serializable {
	
	/** Generated serial version ID */
	private static final long serialVersionUID = 8278562587807182382L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private String		defaultDialogItem;	// The item that will be shown by default when the dialog is open
	private String		defaultGroupText;	// The default group text for VCF Loader
	private int 		transparency;		// Transparency of stripes
	private boolean 	showLegend;			// Boolean that determines wether the legend must be shown or not

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(transparency);
		out.writeBoolean(showLegend);
		out.writeObject(defaultGroupText);
		out.writeObject(defaultDialogItem);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		transparency = in.readInt();
		showLegend = in.readBoolean();
		defaultGroupText = (String) in.readObject();
		defaultDialogItem = (String) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link MGVariousSettings}
	 */
	protected MGVariousSettings () {
		transparency = 70;
		showLegend = true;
		defaultGroupText = "Group 1";
		defaultDialogItem = PropertiesDialog.GENERAL;
	}
	
	
	/**
	 * @param defaultDialogItem 	the default dialog item to show
	 * @param defaultGroupText 		the default group text for VCF Loader
	 * @param transparency 			the transparency to set
	 * @param showLegend 			the showLegend to set
	 */
	public void setVariousSettings(String defaultDialogItem, String defaultGroupText, int transparency, boolean showLegend) {
		this.defaultDialogItem = defaultDialogItem;
		this.defaultGroupText = defaultGroupText;
		this.transparency = transparency;
		this.showLegend = showLegend;
	}
	
	
	/**
	 * @return the transparency
	 */
	public int getTransparencyPercentage() {
		return transparency;
	}
	
	
	/**
	 * @return the opacity according to the transparency percentage
	 */
	public int getColorOpacity () {
		int opacityPercentage = 100 - transparency;
		int opacity = opacityPercentage * 255 / 100;
		return opacity;
	}
	
	
	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return showLegend;
	}
	
	
	/**
	 * @return the defaultGroupText
	 */
	public String getDefaultGroupText() {
		return defaultGroupText;
	}


	/**
	 * @param defaultGroupText the defaultGroupText to set
	 */
	public void setDefaultGroupText(String defaultGroupText) {
		this.defaultGroupText = defaultGroupText;
	}


	/**
	 * @return the defaultDialogItem
	 */
	public String getDefaultDialogItem() {
		return defaultDialogItem;
	}


	/**
	 * @param defaultDialogItem the defaultDialogItem to set
	 */
	public void setDefaultDialogItem(String defaultDialogItem) {
		this.defaultDialogItem = defaultDialogItem;
	}


	/**
	 * Show the settings
	 */
	public void showSettings () {
		System.out.println("Transparency: " + transparency);
		System.out.println("Show legend: " + showLegend);
	}
}
