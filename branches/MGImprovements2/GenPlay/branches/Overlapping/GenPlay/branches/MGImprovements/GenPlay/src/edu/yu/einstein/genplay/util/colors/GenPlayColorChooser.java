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
package edu.yu.einstein.genplay.util.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenPlayColorChooser {


	private static final String		DEFAULT_TITLE = "Choose a color";
	private static JColorChooser	chooser = null;		// color chooser object
	private static JDialog 			dialog;				// color chooser dialog
	private static ActionListener 	okListener;			// ok button listener of the color chooser 
	private static ActionListener 	cancelListener;		// cancel button listener of the color chooser
	private static Color 			selectedColor;		// selected color
	

	/**
	 * Initializes the color chooser.
	 */
	private static void init () {
		if (chooser == null) {
			chooser = new JColorChooser();
			okListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedColor = chooser.getColor();
				}
			};
			cancelListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {}
			};
		}
	}

	
	/**
	 * Shows the color chooser
	 * @param component	to display the color chooser
	 * @param color		the initial color
	 * @return			the selected color (the initial color is returned if no color selected)
	 */
	public static Color showDialog (Component component, Color color) {
		return showDialog(component, DEFAULT_TITLE, color);
	}
	
	
	/**
	 * Shows the color chooser
	 * @param component	to display the color chooser
	 * @param title 	title of the dialog
	 * @param color		the initial color
	 * @return			the selected color (the initial color is returned if no color selected)
	 */
	public static Color showDialog (Component component, String title, Color color) {
		init();
		selectedColor = color;
		dialog = JColorChooser.createDialog(component, "Choose a color", true, chooser, okListener, cancelListener);
		dialog.setVisible(true);
		return selectedColor;
	}

}
