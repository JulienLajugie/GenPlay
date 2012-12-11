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
package edu.yu.einstein.genplay.gui.old.popupMenu;

import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGAVCFStatistics;
import edu.yu.einstein.genplay.gui.action.multiGenome.convert.MGASCWLConvert;
import edu.yu.einstein.genplay.gui.action.multiGenome.export.MGAGlobalVCFExport;
import edu.yu.einstein.genplay.gui.action.multiGenome.update.MGAVCFApplyGenotype;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATAConvert;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATACopy;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATACut;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATADelete;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATAInsert;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATAPaste;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATAPasteSpecial;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATARename;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASaveAsImage;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASetHeight;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASetVerticalLineCount;
import edu.yu.einstein.genplay.gui.old.action.maskTrack.MTAApplyMask;
import edu.yu.einstein.genplay.gui.old.action.maskTrack.MTAInvertMask;
import edu.yu.einstein.genplay.gui.old.action.maskTrack.MTALoadMask;
import edu.yu.einstein.genplay.gui.old.action.maskTrack.MTARemoveMask;
import edu.yu.einstein.genplay.gui.old.action.maskTrack.MTASaveMask;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



/**
 * Base class of the popup menus of a {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackMenu extends JPopupMenu implements PopupMenuListener {

	private static final long serialVersionUID = -2376957246826289131L;	// generated ID

	private final JMenuItem 	jmiConvert;					// menu convert track
	private final JMenuItem 	jmiCopy;					// menu copy track
	private final JMenuItem 	jmiCut;						// menu cut track
	private final JMenuItem 	jmiInsert;					// menu insert blank track
	private final JMenuItem		jmiPaste;					// menu paste track
	private final JMenuItem		jmiPasteSpecial;			// menu special paste track
	private final JMenuItem 	jmiDelete;					// menu delete track
	private final JMenuItem 	jmiRename;					// menu rename track
	private final JMenuItem 	jmiSetHeight;				// menu set height
	private final JMenuItem 	jmiSetVerticalLineCount;	// menu set vertical line count
	private final JMenuItem 	jmiSaveAsImage;				// menu save track as image

	// Menu for mask track
	private final JMenu			jmMask;						// menu for the mask
	private final JMenuItem 	jmiLoadMask;				// menu load mask
	private final JMenuItem 	jmiSaveMask;				// menu load mask
	private final JMenuItem 	jmiRemoveMask;				// menu remove mask
	private final JMenuItem 	jmiInvertMask;				// menu invert mask
	private final JMenuItem 	jmiApplyMask;				// menu apply mask

	// Menu for Multi Genome
	private final JMenu			jmMultiGenome;				// menu for multi genome
	private final JMenuItem		jmiTrackStatistics;			// menu to generate statistics of the track
	private final JMenuItem		jmiExportVCF;				// menu to export stripes as VCF
	//private final JMenuItem		jmiExportBED;				// menu to export stripes as BED	// Temporary unusued
	private final JMenuItem		jmiConvertBED;				// menu to convert stripes as BED
	private final JMenuItem		jmiApplyGenotype;				// menu to update the Gt of a VCF

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


		// Initialize items
		jmiConvert = new JMenuItem(actionMap.get(ATAConvert.ACTION_KEY));
		jmiCopy = new JMenuItem(actionMap.get(ATACopy.ACTION_KEY));
		jmiCut = new JMenuItem(actionMap.get(ATACut.ACTION_KEY));
		jmiDelete = new JMenuItem(actionMap.get(ATADelete.ACTION_KEY));
		jmiInsert = new JMenuItem(actionMap.get(ATAInsert.ACTION_KEY));
		jmiPaste = new JMenuItem(actionMap.get(ATAPaste.ACTION_KEY));
		jmiPasteSpecial = new JMenuItem(actionMap.get(ATAPasteSpecial.ACTION_KEY));
		jmiRename = new JMenuItem(actionMap.get(ATARename.ACTION_KEY));
		jmiSaveAsImage = new JMenuItem(actionMap.get(ATASaveAsImage.ACTION_KEY));
		jmiSetHeight = new JMenuItem(actionMap.get(ATASetHeight.ACTION_KEY));
		jmiSetVerticalLineCount = new JMenuItem(actionMap.get(ATASetVerticalLineCount.ACTION_KEY));


		// Initialize mask items
		jmMask = new JMenu("Mask");
		jmiLoadMask = new JMenuItem(actionMap.get(MTALoadMask.ACTION_KEY));
		jmiSaveMask = new JMenuItem(actionMap.get(MTASaveMask.ACTION_KEY));
		jmiRemoveMask = new JMenuItem(actionMap.get(MTARemoveMask.ACTION_KEY));
		jmiInvertMask = new JMenuItem(actionMap.get(MTAInvertMask.ACTION_KEY));
		jmiApplyMask = new JMenuItem(actionMap.get(MTAApplyMask.ACTION_KEY));
		jmMask.add(jmiLoadMask);
		jmMask.add(jmiSaveMask);
		jmMask.add(jmiRemoveMask);
		jmMask.addSeparator();
		jmMask.add(jmiInvertMask);
		jmMask.add(jmiApplyMask);
		jmiSaveMask.setEnabled(trackList.isMaskRemovable());
		jmiRemoveMask.setEnabled(trackList.isMaskRemovable());
		jmiInvertMask.setEnabled(trackList.isMaskRemovable());
		jmiApplyMask.setEnabled(trackList.isMaskApplicable());


		// Initialize multi genome items
		jmMultiGenome = new JMenu("Multi Genome");
		jmiTrackStatistics = new JMenuItem(actionMap.get(MGAVCFStatistics.ACTION_KEY));
		jmiExportVCF = new JMenuItem(actionMap.get(MGAGlobalVCFExport.ACTION_KEY));
		//jmiExportBED = new JMenuItem(actionMap.get(MGABedExport.ACTION_KEY));
		jmiConvertBED = new JMenuItem(actionMap.get(MGASCWLConvert.ACTION_KEY));
		jmiApplyGenotype = new JMenuItem(actionMap.get(MGAVCFApplyGenotype.ACTION_KEY));
		jmMultiGenome.add(jmiTrackStatistics);
		jmMultiGenome.addSeparator();
		jmMultiGenome.add(jmiConvertBED);
		jmMultiGenome.add(jmiExportVCF);
		//jmMultiGenome.add(jmiExportBED);
		//jmMultiGenome.addSeparator();
		//jmMultiGenome.addSeparator();
		jmMultiGenome.add(jmiApplyGenotype);
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			List<VariantData> stripes = trackList.getSelectedTrack().getMultiGenomeDrawer().getStripesList();
			if ((stripes == null) || (stripes.size() == 0)) {
				jmiTrackStatistics.setEnabled(false);
				jmiExportVCF.setEnabled(false);
				//jmiExportBED.setEnabled(false);
				jmiConvertBED.setEnabled(false);
				jmiApplyGenotype.setEnabled(false);
			}
		} else {
			jmMultiGenome.setEnabled(false);
			jmiTrackStatistics.setEnabled(false);
			jmiExportVCF.setEnabled(false);
			//jmiExportBED.setEnabled(false);
			jmiConvertBED.setEnabled(false);
			jmiApplyGenotype.setEnabled(false);
		}


		// Add items
		add(jmiCopy);
		add(jmiCut);
		add(jmiPaste);
		add(jmiPasteSpecial);
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
		addSeparator();
		add(jmiConvert);
		add(jmMask);
		add(jmMultiGenome);


		jmiConvert.setEnabled(trackList.getSelectedTrack().isConvertible());
		jmiPaste.setEnabled(trackList.isPasteEnable());
		jmiPasteSpecial.setEnabled(trackList.isPasteEnable());

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
