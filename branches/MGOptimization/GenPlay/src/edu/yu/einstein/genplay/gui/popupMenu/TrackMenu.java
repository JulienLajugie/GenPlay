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
package edu.yu.einstein.genplay.gui.popupMenu;

import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACopy;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACut;
import edu.yu.einstein.genplay.gui.action.allTrack.ATADelete;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAInsert;
import edu.yu.einstein.genplay.gui.action.allTrack.ATALoadStripes;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAPaste;
import edu.yu.einstein.genplay.gui.action.allTrack.ATARemoveStripes;
import edu.yu.einstein.genplay.gui.action.allTrack.ATARename;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASaveAsImage;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASetHeight;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASetVerticalLineCount;
import edu.yu.einstein.genplay.gui.action.project.multiGenome.PAMultiGenomeExport;
import edu.yu.einstein.genplay.gui.trackList.TrackList;



/**
 * Base class of the popup menus of a {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackMenu extends JPopupMenu implements PopupMenuListener {

	private static final long serialVersionUID = -2376957246826289131L;	// generated ID

	private final JMenuItem 	jmiCopy;					// menu copy track
	private final JMenuItem 	jmiCut;						// menu cut track
	private final JMenuItem 	jmiInsert;					// menu insert blank track
	private final JMenuItem		jmiPaste;					// menu paste track
	private final JMenuItem 	jmiDelete;					// menu delete track
	private final JMenuItem 	jmiRename;					// menu rename track
	private final JMenuItem 	jmiSetHeight;				// menu set height
	private final JMenuItem 	jmiSetVerticalLineCount;	// menu set vertical line count
	private final JMenuItem 	jmiSaveAsImage;				// menu save track as image
	private final JMenuItem 	jmiExportAsVCF;				// menu export track as VCF
	private final JMenuItem 	jmiLoadStripes;				// menu load stripes
	private final JMenuItem 	jmiRemoveStripes;			// menu remove stripe

	protected final TrackList 	trackList;					// track list where the menu popped up
	protected final ActionMap	actionMap;					// map containing the actions for this menu


	/**
	 * Constructor.
	 * @param tl {@link TrackList} where the menu popped up
	 */
	public TrackMenu(TrackList tl) {
		super ("Track Menu");
		this.trackList = tl;
		this.actionMap = tl.getActionMap();

		jmiCopy = new JMenuItem(actionMap.get(ATACopy.ACTION_KEY));
		jmiCut = new JMenuItem(actionMap.get(ATACut.ACTION_KEY));
		jmiDelete = new JMenuItem(actionMap.get(ATADelete.ACTION_KEY));
		jmiInsert = new JMenuItem(actionMap.get(ATAInsert.ACTION_KEY));
		jmiLoadStripes = new JMenuItem(actionMap.get(ATALoadStripes.ACTION_KEY));
		jmiPaste = new JMenuItem(actionMap.get(ATAPaste.ACTION_KEY));
		jmiRemoveStripes = new JMenuItem(actionMap.get(ATARemoveStripes.ACTION_KEY));
		jmiRename = new JMenuItem(actionMap.get(ATARename.ACTION_KEY));
		jmiSaveAsImage = new JMenuItem(actionMap.get(ATASaveAsImage.ACTION_KEY));
		jmiSetHeight = new JMenuItem(actionMap.get(ATASetHeight.ACTION_KEY));
		jmiSetVerticalLineCount = new JMenuItem(actionMap.get(ATASetVerticalLineCount.ACTION_KEY));
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			jmiExportAsVCF = new JMenuItem(actionMap.get(PAMultiGenomeExport.ACTION_KEY));
			if (trackList.getSelectedTrack().getMultiGenomeDrawer().getStripesList().size() == 0) {
				jmiExportAsVCF.setEnabled(false);
			}
		} else {
			jmiExportAsVCF = null;
		}

		add(jmiCopy);
		add(jmiCut);
		add(jmiPaste);
		add(jmiDelete);
		add(jmiInsert);
		add(jmiRename);
		add(jmiSetHeight);
		if (!(trackList.getSelectedTrack().getData() instanceof BinList)
				&& !(trackList.getSelectedTrack().getData() instanceof ScoredChromosomeWindowList)) {
			add(jmiSetVerticalLineCount);
		}
		addSeparator();
		add(jmiSaveAsImage);
		if (jmiExportAsVCF != null) {
			add(jmiExportAsVCF);
		}
		addSeparator();
		add(jmiLoadStripes);
		add(jmiRemoveStripes);

		jmiPaste.setEnabled(trackList.isPasteEnable());
		jmiRemoveStripes.setEnabled(trackList.isRemoveStripesEnable());

		addPopupMenuListener(this);
	}


	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {}


	/**
	 * Unlocks the handle of the tracks when a menu disappear
	 */
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		trackList.unlockTracksHandles();
	}


	/**
	 * Locks the handle of the tracks when a menu appear
	 */
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		trackList.lockTrackHandles();
	}
}
