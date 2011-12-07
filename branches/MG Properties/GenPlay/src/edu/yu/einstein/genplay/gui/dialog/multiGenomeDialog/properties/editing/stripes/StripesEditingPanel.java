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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.EditingPanel;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class StripesEditingPanel extends EditingPanel<StripesData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 1002708176297238005L;
	
	private final Color[]		defaultVariationColor = {Color.green, Color.red, Color.cyan};	// Array of default variation colors (Insertion, Deletion, SNPs)
	private JComboBox 			jcbGenome;			// The combo box for the genome selection
	private List<VariantType> 	variationName;		// Variation names list
	private List<JCheckBox> 	selectedVariation;	// Variation check box selection list
	private List<JButton> 		variationColor;		// Variation color list


	/**
	 * Constructor of {@link StripesEditingPanel}
	 */
	protected StripesEditingPanel () {
		super();

		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Editing"), gbc);

		// Genome selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Genome"), gbc);

		// Genome selection box
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getGenomeBox(), gbc);

		// Variation selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Variations"), gbc);

		// Variation selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getVariationSelectionPanel(), gbc);

		// Track selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Tracks"), gbc);

		// Track selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		gbc.weighty = 1;
		add(getTrackSelectionPanel(), gbc);

		// Validation panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 0;
		add(getValidationPanel(), gbc);
	}

	
	@Override
	public void clearSelection () {
		jcbGenome.setSelectedIndex(0);
		jcbGenome.setToolTipText(jcbGenome.getSelectedItem().toString());
		for (int i = 0; i < defaultVariationColor.length; i++) {
			selectedVariation.get(i).setSelected(false);
			variationColor.get(i).setBackground(defaultVariationColor[i]);
		}
		selectedTracks.setModel(new DefaultListModel());
		getApplyButton().setEnabled(false);
	}
	
	
	/**
	 * Creates a combo box containing all genomes
	 * @return the combo box
	 */
	private JComboBox getGenomeBox () {
		jcbGenome = new JComboBox(ProjectManager.getInstance().getGenomeSynchronizer().getFormattedGenomeArray());
		int height = jcbGenome.getFontMetrics(jcbGenome.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(180, height);
		jcbGenome.setPreferredSize(dimension);
		jcbGenome.setMinimumSize(dimension);
		jcbGenome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox)e.getSource();
				String element = box.getSelectedItem().toString();
				box.setToolTipText(element);
			}
		});
		jcbGenome.setToolTipText("Select a genome to display its variation(s).");
		return jcbGenome;
	}


	/**
	 * Creates the variation selection panel.
	 * It contains all variation with a check box to select it for display and a button to choose its color.
	 * @return the variation selection panel
	 */
	private JPanel getVariationSelectionPanel () {
		// Initialize lists
		variationName = new ArrayList<VariantType>();
		selectedVariation = new ArrayList<JCheckBox>();
		variationColor = new ArrayList<JButton>();

		// Fill the list that contains the variation names
		variationName.add(VariantType.INSERTION);	// INSERTION will be on index 0
		variationName.add(VariantType.DELETION);	// DELETION will be on index 1
		variationName.add(VariantType.SNPS);		// SNPS will be on index 2

		// Fill the list that contains the checkbox for selecting variation
		for (int i = 0; i < 3; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setBorder(null);
			checkBox.setMargin(new Insets(0, 0, 0, 0));
			checkBox.setToolTipText("Enable/Disable " + variationName.get(i).toString().toLowerCase() + ".");
			selectedVariation.add(checkBox);
		}

		// Fill the list that contains the button for selecting variation color
		for (int i = 0; i < 3; i++) {
			JButton colorButton = new JButton();
			Dimension colorDim = new Dimension(13, 13);
			colorButton.setPreferredSize(colorDim);
			colorButton.setBorder(null);
			colorButton.setToolTipText("Select color for " + variationName.get(i).toString().toLowerCase() + ".");
			colorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JButton button = (JButton) arg0.getSource();
					Color newColor = JColorChooser.showDialog(getCurrentInstance(), "Choose color", button.getBackground());
					if (newColor != null) {
						button.setBackground(newColor);
					}
				}
			});
			// Initialize button color
			colorButton.setBackground(defaultVariationColor[i]);

			// Add the button to the list
			variationColor.add(colorButton);
		}


		// Create the panel
		JPanel panel = new JPanel();
		int height = getFontMetrics(getFont()).getHeight() * 3;
		Dimension dimension = new Dimension(120, height);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);
		
		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Insets
		Insets nameInset = new Insets(0, 0, 0, 0);
		Insets selectInset = new Insets(0, 15, 0, 0);
		Insets colorInset = new Insets(0, 10, 0, 0);

		// Add components to the panel
		for (int i = 0; i < 3; i++) {
			// Variation name
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.insets = nameInset;
			panel.add(new JLabel(variationName.get(i).toString()), gbc);

			// Selection button
			gbc.gridx = 1;
			gbc.gridy = i;
			gbc.insets = selectInset;
			panel.add(selectedVariation.get(i), gbc);

			// Color button
			gbc.gridx = 2;
			gbc.gridy = i;
			gbc.insets = colorInset;
			panel.add(variationColor.get(i), gbc);
		}

		// Return the panel
		return panel;
	}
	
	
	@Override
	protected void setEditingPanel (StripesData data) {
		// Set the genome
		jcbGenome.setSelectedItem(data.getGenome());
		
		// Set selected variation and color
		for (int i = 0; i < variationName.size(); i++) {
			int variationIndex = data.getVariationTypeList().indexOf(variationName.get(i));
			if (variationIndex == -1) {
				selectedVariation.get(i).setSelected(false);
				variationColor.get(i).setBackground(defaultVariationColor[i]);
			} else {
				selectedVariation.get(i).setSelected(true);
				variationColor.get(i).setBackground(data.getColorList().get(variationIndex));
			}
		}
		
		// Set the selected track list
		DefaultListModel listModel = new DefaultListModel();
		for (Track<?> track: data.getTrackList()) {
			listModel.addElement(track);
		}
		selectedTracks.setModel(listModel);
	}
	
	
	@Override
	protected StripesData getElement () {
		// Retrieve the genome name
		String genome = jcbGenome.getSelectedItem().toString();
		
		// Retrieve the variant and color lists
		List<VariantType> variantList = new ArrayList<VariantType>();
		List<Color> colorList = new ArrayList<Color>();
		for (int i = 0; i < variationName.size(); i++) {
			if (selectedVariation.get(i).isSelected()) {
				VariantType type = variationName.get(i);
				Color color = variationColor.get(i).getBackground();
				if (type == VariantType.INSERTION || type == VariantType.INS) {
					variantList.add(VariantType.INSERTION);
					variantList.add(VariantType.INS);
					colorList.add(color);
					colorList.add(color);
				} else if (type == VariantType.DELETION || type == VariantType.DEL) {
					variantList.add(VariantType.DELETION);
					variantList.add(VariantType.DEL);
					colorList.add(color);
					colorList.add(color);
				} else if (type == VariantType.SNPS || type == VariantType.SVSNPS) {
					variantList.add(VariantType.SNPS);
					variantList.add(VariantType.SVSNPS);
					colorList.add(color);
					colorList.add(color);
				}
			}
		}
		
		// Retrieve selected tracks
		Track<?>[] trackList = getSelectedTracks();
		
		// Create the stripe data object
		StripesData data = new StripesData(genome, variantList, colorList, trackList);
		
		// Return the stripe data object
		return data;
	}

}
