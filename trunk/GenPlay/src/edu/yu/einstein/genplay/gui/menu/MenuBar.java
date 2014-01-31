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
package edu.yu.einstein.genplay.gui.menu;

import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import edu.yu.einstein.genplay.gui.action.project.PAAbout;
import edu.yu.einstein.genplay.gui.action.project.PAFullScreen;
import edu.yu.einstein.genplay.gui.action.project.PAHelp;
import edu.yu.einstein.genplay.gui.action.project.PALoadProject;
import edu.yu.einstein.genplay.gui.action.project.PANewProject;
import edu.yu.einstein.genplay.gui.action.project.PAOption;
import edu.yu.einstein.genplay.gui.action.project.PARNAPosToDNAPos;
import edu.yu.einstein.genplay.gui.action.project.PASaveProject;
import edu.yu.einstein.genplay.gui.action.project.PAShowErrorReport;
import edu.yu.einstein.genplay.gui.action.project.PAShowMainFrame;
import edu.yu.einstein.genplay.gui.action.project.PAShowWarningReport;
import edu.yu.einstein.genplay.gui.action.project.PASortFile;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayer;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayerFromDAS;
import edu.yu.einstein.genplay.gui.action.track.TACopy;
import edu.yu.einstein.genplay.gui.action.track.TACut;
import edu.yu.einstein.genplay.gui.action.track.TADelete;
import edu.yu.einstein.genplay.gui.action.track.TAInsert;
import edu.yu.einstein.genplay.gui.action.track.TAPasteOrDrop;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.action.track.TASaveTrack;
import edu.yu.einstein.genplay.gui.action.track.TATrackSettings;
import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.ExceptionReportDialog;
import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.WarningReportDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.menu.layerMenu.LayerMenuFactory;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Application main menu bar following the OSX feel
 * @author Julien Lajugie
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 2686285918295677584L;	// generated ID
	private final ActionMap 	actionMap;			// main frame action map
	private final JMenu 		jmFile;				// menu file
	private final JMenu 		jmEdit;				// menu edit
	private final JMenu 		jmTrack;			// menu track
	private final JMenu 		jmLayers;			// menu layers
	private final JMenu 		jmWindow;			// menu window
	private final JMenu 		jmUtilities;		// menu utilities
	private final JMenu 		jmHelp;				// menu help


	/**
	 * Creates an instance of {@link MenuBar}
	 * @param actionMap action map of the main frame
	 */
	public MenuBar(ActionMap actionMap) {
		super();
		this.actionMap = actionMap;

		jmFile = new JMenu("File");
		createFileMenu();

		jmEdit = new JMenu("Edit");
		createEditMenu();

		jmTrack = new JMenu("Track");
		createTrackMenu();

		jmLayers = new JMenu("Layers");
		createLayerMenu();

		jmUtilities = new JMenu("Utilities");
		createUtiliesMenu();

		jmWindow = new JMenu("Window");
		createViewMenu();

		jmHelp = new JMenu("Help");
		createHelpMenu();
	}


	/**
	 * Creates Edit menu
	 */
	private void createEditMenu() {
		final JMenuItem jmiCopy = new JMenuItem(actionMap.get(TACopy.ACTION_KEY));
		final JMenuItem jmiCut = new JMenuItem(actionMap.get(TACut.ACTION_KEY));
		final JMenuItem jmiPaste = new JMenuItem(actionMap.get(TAPasteOrDrop.ACTION_KEY));
		final JMenuItem jmiInsert = new JMenuItem(actionMap.get(TAInsert.ACTION_KEY));
		final JMenuItem jmiDelete = new JMenuItem(actionMap.get(TADelete.ACTION_KEY));

		jmEdit.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent e) {}

			@Override
			public void menuDeselected(MenuEvent e) {}

			@Override
			public void menuSelected(MenuEvent e) {
				boolean isTrackSelected = MainFrame.getInstance().getTrackListPanel().getSelectedTrack() != null;
				jmiCopy.setEnabled(isTrackSelected);
				jmiCut.setEnabled(isTrackSelected);
				boolean isPasteEnable = MainFrame.getInstance().getTrackListPanel().isPasteEnable();
				jmiPaste.setEnabled(isTrackSelected && isPasteEnable);
				jmiInsert.setEnabled(isTrackSelected);
				jmiDelete.setEnabled(isTrackSelected);
			}
		});

		jmEdit.add(jmiCopy);
		jmEdit.add(jmiCut);
		jmEdit.add(jmiPaste);
		jmEdit.addSeparator();
		jmEdit.add(jmiInsert);
		jmEdit.add(jmiDelete);

		add(jmEdit);
	}


	/**
	 * Creates File menu
	 */
	private void createFileMenu() {
		jmFile.add(actionMap.get(PANewProject.ACTION_KEY));
		jmFile.add(actionMap.get(PALoadProject.ACTION_KEY));
		jmFile.add(actionMap.get(PASaveProject.ACTION_KEY));
		add(jmFile);
	}


	/**
	 * Creates Help menu
	 */
	private void createHelpMenu() {
		if (!Utils.isMacOS()) {
			// on mac the option and about menu items are available from the
			// application menu so we don't need them in the help menu
			jmHelp.add(actionMap.get(PAOption.ACTION_KEY));
		}
		jmHelp.add(actionMap.get(PAHelp.ACTION_KEY));
		if (!Utils.isMacOS()) {
			jmHelp.add(actionMap.get(PAAbout.ACTION_KEY));
		}
		add(jmHelp);
	}


	/**
	 * Creates Layer menu
	 */
	private void createLayerMenu() {
		jmLayers.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent e) {
				jmLayers.removeAll();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				jmLayers.removeAll();
			}

			@Override
			public void menuSelected(MenuEvent e) {
				Track selectedTrack = MainFrame.getInstance().getTrackListPanel().getSelectedTrack();
				if (selectedTrack == null) {
					JMenuItem noTrackSelected = new JMenuItem("No track selected");
					noTrackSelected.setEnabled(false);
					jmLayers.add(noTrackSelected);
				} else {
					Layer<?>[] layers = selectedTrack.getLayers().getLayers();
					if (layers.length == 0) {
						JMenuItem noLayerAvailable = new JMenuItem("No layer loaded on selected track");
						noLayerAvailable.setEnabled(false);
						jmLayers.add(noLayerAvailable);
					} else {
						for (Layer<?> currentLayer: layers) {
							jmLayers.add(LayerMenuFactory.createLayerMenu(currentLayer));
						}
					}
				}
			}
		});
		add(jmLayers);
	}


	/**
	 * Creates Track menu
	 */
	private void createTrackMenu() {
		jmTrack.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent e) {
				jmTrack.removeAll();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
				jmTrack.removeAll();
			}

			@Override
			public void menuSelected(MenuEvent e) {
				boolean isTrackSelected = MainFrame.getInstance().getTrackListPanel().getSelectedTrack() != null;
				if (isTrackSelected) {
					jmTrack.add(actionMap.get(TAAddLayer.ACTION_KEY));
					jmTrack.add(actionMap.get(TAAddLayerFromDAS.ACTION_KEY));
					jmTrack.addSeparator();
					jmTrack.add(actionMap.get(TASaveTrack.ACTION_KEY));
					jmTrack.add(actionMap.get(TASaveAsImage.ACTION_KEY));
					jmTrack.addSeparator();
					jmTrack.add(actionMap.get(TATrackSettings.ACTION_KEY));
				} else {
					JMenuItem noTrackSelected = new JMenuItem("No track selected");
					noTrackSelected.setEnabled(false);
					jmTrack.add(noTrackSelected);
				}
			}
		});
		add(jmTrack);
	}


	/**
	 * Creates Utilities menu
	 */
	private void createUtiliesMenu() {
		jmUtilities.add(actionMap.get(PASortFile.ACTION_KEY));
		jmUtilities.add(actionMap.get(PARNAPosToDNAPos.ACTION_KEY));
		add(jmUtilities);
	}


	/**
	 * Creates View menu
	 */
	private void createViewMenu() {
		final JCheckBoxMenuItem jcbmiMainFrame = new JCheckBoxMenuItem(new PAShowMainFrame());
		final JCheckBoxMenuItem jcbmiWarningReport = new JCheckBoxMenuItem(new PAShowWarningReport());;
		final JCheckBoxMenuItem jcbmiExceptionReport = new JCheckBoxMenuItem(new PAShowErrorReport());;

		jmWindow.add(actionMap.get(PAFullScreen.ACTION_KEY));
		jmWindow.addSeparator();
		jmWindow.add(jcbmiMainFrame);
		jmWindow.add(jcbmiWarningReport);
		jmWindow.add(jcbmiExceptionReport);

		jmWindow.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent e) {}

			@Override
			public void menuDeselected(MenuEvent e) {}

			@Override
			public void menuSelected(MenuEvent e) {
				jcbmiMainFrame.setSelected(MainFrame.getInstance().isActive());
				jcbmiMainFrame.setEnabled(MainFrame.getInstance().isVisible());
				jcbmiWarningReport.setSelected(WarningReportDialog.getInstance().isActive());
				jcbmiWarningReport.setEnabled(WarningReportDialog.getInstance().isVisible());
				jcbmiExceptionReport.setSelected(ExceptionReportDialog.getInstance().isActive());
				jcbmiExceptionReport.setEnabled(ExceptionReportDialog.getInstance().isVisible());
			}
		});

		add(jmWindow);
	}
}
