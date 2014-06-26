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
package edu.yu.einstein.genplay.gui.action.layer.geneLayer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneSearcher;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.SearchGeneDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;


/**
 * Searches a gene on a {@link GeneLayer}
 * @author Julien Lajugie
 */
public final class GLASearchGene extends TrackListActionWorker<GeneSearcher> {

	private static final long serialVersionUID = 2102571378866219218L; 	// generated ID
	private static final String 	ACTION_NAME = "Search Gene";		// action name
	private static final String 	DESCRIPTION =
			"Search a gene on the selected layer" + HELP_TOOLTIP_SUFFIX;// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Search_Gene";


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLASearchGene.class.getName();


	/**
	 * Creates an instance of {@link GLASearchGene}
	 */
	public GLASearchGene() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(GeneSearcher actionResult) {
		if (actionResult != null) {
			SearchGeneDialog.showSearchGeneDialog(MainFrame.getInstance(), actionResult);
		}
	}


	@Override
	protected GeneSearcher processAction() throws Exception {
		GeneLayer selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			return selectedLayer.getData().getGeneSearcher();
		}
		return null;
	}
}
