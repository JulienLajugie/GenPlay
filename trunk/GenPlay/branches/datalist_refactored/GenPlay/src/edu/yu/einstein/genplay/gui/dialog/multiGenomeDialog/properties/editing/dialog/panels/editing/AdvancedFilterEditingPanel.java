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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.advancedEditors.AdvancedEditor;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AdvancedFilterEditingPanel extends EditingPanel<FilterInterface> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private AdvancedEditor filterEditor;


	/**
	 * Constructor of {@link AdvancedFilterEditingPanel}
	 */
	public AdvancedFilterEditingPanel() {
		super("Filter");
	}


	@Override
	protected void initializeContentPanel() {}


	@Override
	public void update(Object object) {
		initEditors(object);

		JPanel panel;
		if (filterEditor != null) {
			panel = filterEditor.updatePanel();
		} else {
			panel = new JPanel();
		}

		setNewContentPanel(panel);
		initializeContentPanelSize(MINIMUM_WIDTH, panel.getPreferredSize().height + 10);
		repaint();
	}



	private void initEditors (Object object) {
		filterEditor = null;
	}


	/**
	 * @return the ID filter
	 */
	public FilterInterface getFilter () {
		return filterEditor.getFilter();
	}


	@Override
	public String getErrors() {
		String errors = "";

		if (filterEditor == null) {
			errors += "Filter selection\n";
		} else {
			errors += filterEditor.getErrors();
		}

		return errors;
	}


	@Override
	public void reset() {
		resetContentPanel();
		element = null;
		filterEditor = null;
	}


	@Override
	public void initialize(FilterInterface element) {
		if (filterEditor != null) {
			if (element instanceof FilterInterface) {
				filterEditor.initializesPanel(element);
			}
		}
	}

}
