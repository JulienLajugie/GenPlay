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
package edu.yu.einstein.genplay.gui.popupMenu.layerMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.yu.einstein.genplay.gui.action.layer.versionnedLayer.VLAHistory;
import edu.yu.einstein.genplay.gui.action.layer.versionnedLayer.VLARedo;
import edu.yu.einstein.genplay.gui.action.layer.versionnedLayer.VLAReset;
import edu.yu.einstein.genplay.gui.action.layer.versionnedLayer.VLAUndo;
import edu.yu.einstein.genplay.gui.track.layer.VersionedLayer;


/**
 * Menu for the versioned layers
 * @author Julien Lajugie
 * @version 0.1
 */
public class VersionedLayerMenu extends JMenu {

	private static final long serialVersionUID = -593896017514090894L; // generated serial ID


	/**
	 * Creates an instance of {@link VersionedLayerMenu}
	 * @param layer the layer associated to this menu
	 */
	public VersionedLayerMenu(VersionedLayer<?> layer) {
		super("Versions");
		// show history
		VLAHistory vlaHistory = new VLAHistory();
		vlaHistory.putValue("Layer", layer);
		JMenuItem jmiHistory = new JMenuItem(vlaHistory);
		jmiHistory.setEnabled(layer.getHistory() != null);

		// undo
		VLAUndo vlaUndo = new VLAUndo();
		vlaUndo.putValue("Layer", layer);
		JMenuItem jmiUndo = new JMenuItem(vlaUndo);
		jmiUndo.setEnabled(layer.isUndoable());

		// redo
		VLARedo vlaRedo = new VLARedo();
		vlaRedo.putValue("Layer", layer);
		JMenuItem jmiRedo = new JMenuItem(vlaRedo);
		jmiRedo.setEnabled(layer.isRedoable());

		// reset
		VLAReset vlaReset = new VLAReset();
		vlaReset.putValue("Layer", layer);
		JMenuItem jmiReset = new JMenuItem(vlaReset);
		jmiReset.setEnabled(layer.isResetable());

		// add items to the menu
		add(jmiHistory);
		addSeparator();
		add(jmiUndo);
		add(jmiRedo);
		add(jmiReset);
	}
}
