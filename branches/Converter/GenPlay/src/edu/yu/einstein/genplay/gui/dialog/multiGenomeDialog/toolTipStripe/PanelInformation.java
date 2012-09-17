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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PanelInformation extends JPanel {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -4402163861725735910L;
	
	protected static final int WIDTH = 230;			// width of the panel
	protected static final int HEADER_HEIGHT = 20;	// height of the header (that contains the title)
	protected static final int SCROLL_HEIGHT = 100;	// height of the scroll pane (that contains all details)
	

	/**
	 * Constructor of {@link PanelInformation}
	 */
	protected PanelInformation (String title, List<String> keys, List<String> values, List<String> description) {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		JPanel headerPanel = getHeaderPane(title);
		JPanel contentPanel = getContentPanel(keys, values, description);
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		
		Dimension dimension = new Dimension(WIDTH, SCROLL_HEIGHT);
		scrollPane.setSize(dimension);
		scrollPane.setMinimumSize(dimension);
		scrollPane.setMaximumSize(dimension);
		scrollPane.setPreferredSize(dimension);
		
		add(headerPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}


	/**
	 * Creates the header panel (contains the title)
	 * @param title	the title of the panel
	 * @return		the panel containing the title
	 */
	private JPanel getHeaderPane (String title) {
		JPanel pane = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(0);
		layout.setVgap(0);
		pane.setLayout(layout);

		JLabel label = new JLabel(title);
		pane.add(label);
		
		return pane;
	}


	/**
	 * Creates the content panel (contains all information)
	 * @param keys		keys values
	 * @param values	values
	 * @return			the panel with all association key/value
	 */
	private JPanel getContentPanel (List<String> keys, List<String> values, List<String> description) {
		JPanel pane = new JPanel();
		
		if (keys == null) {
			JLabel label = new JLabel("No information available");
			pane.add(label);
		} else {
			GridBagLayout layout = new GridBagLayout();
			pane.setLayout(layout);
			GridBagConstraints gbc = new GridBagConstraints();
			
			Insets inset = new Insets(0, 0, 0, 0);
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.insets = inset;
			gbc.gridwidth = 1;
			gbc.weighty = 0;
			
			for (int i = 0; i < keys.size(); i++) {
				if (i == (keys.size()-1)) {
					int numElements = i + 1;
					FontMetrics fm = getFontMetrics(getFont());
					int height = fm.getHeight() * numElements;
					int diff = SCROLL_HEIGHT - height;
					if (diff > 4) {
						diff = diff - 4;
						Insets lastRowInset = new Insets(0, 0, diff, 0);
						gbc.insets = lastRowInset;
					}
				}
				
				gbc.gridx = 0;
				gbc.gridy = i;
				gbc.weightx = 0.1;
				JLabel key = new JLabel(keys.get(i) + ":");
				key.setToolTipText(description.get(i));
				pane.add(key, gbc);

				gbc.gridx = 1;
				gbc.weightx = 1.9;
				gbc.gridwidth = 1;
				JLabel value = new JLabel(values.get(i));
				value.setToolTipText(value.getText());
				pane.add(value, gbc);
			}
		}		
		return pane;
	}
	
	
	/**
	 * @return the height of the panel
	 */
	protected static int getPanelHeight () {
		return HEADER_HEIGHT + SCROLL_HEIGHT;
	}

}
