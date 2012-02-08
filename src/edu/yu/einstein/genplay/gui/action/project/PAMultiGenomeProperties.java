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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Displays the multi genome project properties dialog
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class PAMultiGenomeProperties extends TrackListAction {

	private static final 	long serialVersionUID = -6475180772964541278L; 			// generated ID
	private static final 	String ACTION_NAME = "Multi Genome Properties";			// action name
	private static final 	String DESCRIPTION = "Shows the project properties"; 	// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key
	
	private 				PropertiesDialog 	dialog;								// the dialog properties
	private					MGDisplaySettings 	settings;							// the multi genome settings object shortcut

	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK); 
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMultiGenomeProperties";


	/**
	 * Creates an instance of {@link PAMultiGenomeProperties}
	 */
	public PAMultiGenomeProperties() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		settings = MGDisplaySettings.getInstance();
	}


	/**
	 * Shows the Multi Genome Project Properties dialog
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			if (dialog == null){
				dialog = new PropertiesDialog();
			}
			dialog.setSettings(settings);
			if (dialog.showDialog(getRootPane(), PropertiesDialog.GENERAL) == PropertiesDialog.APPROVE_OPTION) {
				approve();
			}
		}
	}


	/**
	 * Called if the dialog has been approved.
	 */
	private void approve () {
		// Set the various settings
		settings.getVariousSettings().setVariousSettings(dialog.getTransparency(), dialog.isShowLegend());
		
		// Set the filters
		settings.getFilterSettings().setFiltersSettings(dialog.getFiltersData());
		
		// Set the stripes
		settings.getStripeSettings().setStripesSettings(dialog.getStripesData());
		
		// Set the static options
		MGDisplaySettings.DRAW_INSERTION_EDGE = dialog.getOptionValueList().get(0);
		MGDisplaySettings.DRAW_DELETION_EDGE = dialog.getOptionValueList().get(1);
		MGDisplaySettings.DRAW_INSERTION_LETTERS = dialog.getOptionValueList().get(2);
		MGDisplaySettings.DRAW_DELETION_LETTERS = dialog.getOptionValueList().get(3);
		MGDisplaySettings.DRAW_SNP_LETTERS = dialog.getOptionValueList().get(4);
		
		
		// Update the SNPs
		// Set the SNP synchronization action and run it (the action will decide itself how to manage the SNPs)
		PAMultiGenomeSNP multiGenomeSNP = new PAMultiGenomeSNP();
		multiGenomeSNP.actionPerformed(null);
		
		// Update tracks
		Track<?>[] tracks = getTrackList().getTrackList();
		for (Track<?> track: tracks) {
			List<VCFFilter> filtersList = settings.getFilterSettings().getVCFFiltersForTrack(track);
			List<StripesData> stripesList = settings.getStripeSettings().getStripesForTrack(track);
			track.updateMultiGenomeInformation(stripesList, filtersList);
		}
	}
	
}
