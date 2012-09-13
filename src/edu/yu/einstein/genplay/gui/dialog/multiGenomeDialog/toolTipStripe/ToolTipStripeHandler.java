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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ToolTipStripeHandler {

	private static	ToolTipStripeHandler	instance = null;		// unique instance of the singleton
	private List<ToolTipStripeDialog> currentDialogs;			// the open dialogs


	/**
	 * @return an instance of a {@link ProjectManager}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ToolTipStripeHandler getInstance() {
		if (instance == null) {
			synchronized(ToolTipStripeHandler.class) {
				if (instance == null) {
					instance = new ToolTipStripeHandler();
				}
			}
		}
		return instance;
	}


	/**
	 * Constructor of {@link ToolTipStripeHandler}
	 */
	private ToolTipStripeHandler () {
		currentDialogs = new ArrayList<ToolTipStripeDialog>();
	}


	/**
	 * @param dialog the dialog to add to the list
	 */
	public void addDialog (ToolTipStripeDialog dialog) {
		currentDialogs.add(dialog);
	}


	/**
	 * Kill all open dialogs
	 */
	public void killAllDialogs () {
		if (currentDialogs.size() > 0) {
			for (ToolTipStripeDialog dialog: currentDialogs) {
				dialog.dispose();
			}
			currentDialogs = new ArrayList<ToolTipStripeDialog>();
		}
	}


	/**
	 * Kill all open dialogs requested by the given multi genome drawer
	 * @param multiGenomeDrawer
	 */
	public void killDialogs (MultiGenomeDrawer multiGenomeDrawer) {
		if (currentDialogs.size() > 0) {
			List<ToolTipStripeDialog> newList = new ArrayList<ToolTipStripeDialog>();
			List<ToolTipStripeDialog> toKill = new ArrayList<ToolTipStripeDialog>();
			for (ToolTipStripeDialog dialog: currentDialogs) {
				if (dialog.getMultiGenomeDrawer() == multiGenomeDrawer) {
					toKill.add(dialog);
				} else {
					newList.add(dialog);
				}
			}

			for (ToolTipStripeDialog dialog: toKill) {
				dialog.dispose();
			}

			currentDialogs = newList;
		}
	}
}
