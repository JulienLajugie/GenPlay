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
package edu.yu.einstein.genplay.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Utility method for the look and feel of the application
 * @author Julien Lajugie
 */
public class LookAndFeels {


	/**
	 * Customizes the look and feel
	 */
	public static void customizeLookAndFeel() {
		// change the colors for nimbus look and feel
		UIManager.put("nimbusBase", new Color(41, 96, 150));
		UIManager.put("nimbusBlueGrey", new Color(187, 196, 209));
		//UIManager.put("control", new Color(228, 236, 247));
		UIManager.put("control", Colors.MAIN_GUI_BACKGROUND);
	}


	/**
	 * @return true if the current look and feel is a native look and feel, false otherwise
	 */
	public static boolean isNativeLookAndFeel() {
		return UIManager.getLookAndFeel().isNativeLookAndFeel();
	}


	/**
	 * Changes the look and feel of the application
	 */
	public static void setLookAndFeel(Component c) {
		try {
			UIManager.setLookAndFeel(ConfigurationManager.getInstance().getLookAndFeel());
			SwingUtilities.updateComponentTreeUI(c);
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while loading the look and feel specified in the config file");
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), e1, "Error while loading the default look and feel");
			}
		}
	}
}
