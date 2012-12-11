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
package edu.yu.einstein.genplay.gui.action.multiGenome.properties;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;
import edu.yu.einstein.genplay.gui.old.action.TrackListAction;


/**
 * Displays the multi genome project properties dialog
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MGAProperties extends TrackListAction{

	private static final 	long serialVersionUID = -6475180772964541278L; 			// generated ID
	private static final 	String ACTION_NAME = "Multi Genome Properties";			// action name
	private static final 	String DESCRIPTION = "Shows the project properties"; 	// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key

	private 				PropertiesDialog 	dialog;								// the dialog properties
	private final			MGDisplaySettings 	settings;							// the multi genome settings object shortcut
	private					String				itemDialog;							// the dialog section to show


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMultiGenomeProperties";


	/**
	 * Creates an instance of {@link MGAProperties}
	 */
	public MGAProperties() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		settings = MGDisplaySettings.getInstance();
		itemDialog = settings.getVariousSettings().getDefaultDialogItem();
	}


	/**
	 * Shows the Multi Genome Project Properties dialog
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {		// if it is a multi genome project
			if (dialog == null){										// and the dialog has not been created,
				dialog = new PropertiesDialog();						// we create it
			}
			dialog.setSettings(settings);								// and set it with the current settings
			if (dialog.showDialog(getRootPane(), itemDialog) == PropertiesDialog.APPROVE_OPTION) {	// we show it waiting to be approved
				List<MGFilter> previousFilterList = settings.getFilterSettings().getAllMGFilters();

				// Set the various settings
				settings.getVariousSettings().setVariousSettings(dialog.getDefaultItemDialog(), dialog.getDefaultGroupText(), dialog.getTransparency(), dialog.isShowLegend());

				// Set the filters
				settings.getFilterSettings().setFiltersSettings(dialog.getFiltersData());

				// Set the variants
				settings.getVariantSettings().setVariantsSettings(dialog.getVariantsData());

				// Set the static options
				MGDisplaySettings.DRAW_FILTERED_VARIANT = dialog.getOptionValueList().get(0);
				MGDisplaySettings.DRAW_INSERTION_EDGE = dialog.getOptionValueList().get(1);
				MGDisplaySettings.DRAW_DELETION_EDGE = dialog.getOptionValueList().get(2);
				MGDisplaySettings.DRAW_INSERTION_LETTERS = dialog.getOptionValueList().get(3);
				MGDisplaySettings.DRAW_DELETION_LETTERS = dialog.getOptionValueList().get(4);
				MGDisplaySettings.DRAW_SNP_LETTERS = dialog.getOptionValueList().get(5);

				// Set the reference stripes color
				MGDisplaySettings.REFERENCE_INSERTION_COLOR = dialog.getReferenceColor();
				MGDisplaySettings.REFERENCE_DELETION_COLOR = dialog.getReferenceColor();
				MGDisplaySettings.REFERENCE_SNP_COLOR = dialog.getReferenceColor();

				// Set the reference stripes showing
				MGDisplaySettings.getInstance().setReferencePolicy(dialog.isShowReference());

				// Updates track (SNPs, filters, display)
				MGARefresh action = new MGARefresh();
				action.setPreviousFilterList(previousFilterList);
				action.actionPerformed(null);
			}
		}
	}


	/**
	 * @return the itemDialog
	 */
	public String getItemDialog() {
		return itemDialog;
	}


	/**
	 * @param itemDialog the itemDialog to set
	 */
	public void setItemDialog(String itemDialog) {
		this.itemDialog = itemDialog;
	}

}