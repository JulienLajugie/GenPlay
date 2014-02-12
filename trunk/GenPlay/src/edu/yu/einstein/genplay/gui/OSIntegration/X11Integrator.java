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
package edu.yu.einstein.genplay.gui.OSIntegration;

import java.awt.Toolkit;

import edu.yu.einstein.genplay.util.Utils;

/**
 * Methods to integrate genplay into X11 systems
 * @author Julien Lajugie
 */
public class X11Integrator {

	/**
	 * Change the name if the application registered in the WM_CLASS property on
	 * X11 so the window manager can keep track of the application (and associate
	 * the correct icons on gnome shell for example)
	 */
	public static void setWMClassName() {
		if (!Utils.isMacOS() && !Utils.isWindowsOS()) { // if it's unix or linux
			try {
				Toolkit xToolkit = Toolkit.getDefaultToolkit();
				java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
				awtAppClassNameField.setAccessible(true);
				awtAppClassNameField.set(xToolkit, "GenPlay");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
