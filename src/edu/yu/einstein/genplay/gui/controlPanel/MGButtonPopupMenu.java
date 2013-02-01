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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.yu.einstein.genplay.gui.action.multiGenome.properties.MGAProperties;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGButtonPopupMenu extends JPopupMenu implements ActionListener {

	/** Generated default version ID */
	private static final long serialVersionUID = 5640045293380707735L;


	/**
	 * Constructor of {@link MGButtonPopupMenu}.
	 */
	public MGButtonPopupMenu () {
		super();
		initializeItem();
	}


	/**
	 * Initializes the list of items for this menu
	 */
	private void initializeItem () {
		add(getItemMenu(PropertiesDialog.GENERAL));
		add(getItemMenu(PropertiesDialog.SETTINGS));
		add(getItemMenu(PropertiesDialog.FILTERS));
		//add(getItemMenu(PropertiesDialog.FILTERS_FILE));
		//add(getItemMenu(PropertiesDialog.FILTERS_ADVANCED));
	}


	/**
	 * Instantiates an item using a text as label
	 * @param text 	text that will be use as a label
	 * @return		the item
	 */
	private JMenuItem getItemMenu (String text) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(this);
		return menuItem;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String item = ((JMenuItem)arg0.getSource()).getText();
		MGAProperties action = new MGAProperties();
		action.setItemDialog(item);
		action.actionPerformed(null);
	}
}
