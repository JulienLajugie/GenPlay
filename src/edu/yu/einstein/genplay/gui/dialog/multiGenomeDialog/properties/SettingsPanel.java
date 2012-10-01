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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class SettingsPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3353198770426567657L;


	// Propeties dialog options
	private JComboBox jcbDefaultItem;

	// VCF Loader options
	private JTextField jtfDefaultGroupText;

	// Transparency option
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static final int TRANSPARENCY_INIT = 50;
	private JLabel sliderValue;
	private JSlider slider;

	// Legend option
	private boolean showLegend = true;
	private JRadioButton yesLegendButton;
	private JRadioButton noLegendButton;

	// Reference option
	private boolean showReference = true;
	private JRadioButton yesReferenceButton;
	private JRadioButton noReferenceButton;

	// Static in option
	private List<String> optionNameList;
	private List<Integer> optionValueList;
	private final List<JRadioButton> yesOptionRadioList;
	private final List<JRadioButton> noOptionRadioList;

	private Color referenceColor = MGDisplaySettings.REFERENCE_INSERTION_COLOR;


	/**
	 * Constructor of {@link SettingsPanel}
	 */
	protected SettingsPanel () {
		initializesStaticOptionLists();
		yesOptionRadioList = new ArrayList<JRadioButton>();
		noOptionRadioList = new ArrayList<JRadioButton>();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;


		// Stripes transparency option title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = PropertiesDialog.FIRST_TITLE_INSET;
		add(Utils.getTitleLabel("Properties Dialog"), gbc);

		// Slider for stripes transparency option
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getDefaultDialogItemPanel(), gbc);

		// VCF Loader option title
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("VCF Loader"), gbc);

		// Default group text option
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getDefaultGroupTextPanel(), gbc);

		// Stripes transparency option title
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Stripes transparency"), gbc);

		// Slider for stripes transparency option
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getSliderPanel(), gbc);

		// Stripes legend option title
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Global display settings"), gbc);

		// Radios for stripes legend option
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getStripeLegendPanel(), gbc);

		// Stripes display settings title
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Variant stripes settings"), gbc);

		// Adds the static options
		gbc.insets = PropertiesDialog.PANEL_INSET;
		for (int i = 0; i < optionNameList.size(); i++) {
			gbc.gridy++;
			add(getStaticOptionPanel(i), gbc);
		}

		// Reference stripes settings title
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Reference stripes settings"), gbc);


		// Reference stripes settings
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getReferenceOptionPanel(), gbc);

	}


	/**
	 * Initializes the lists for the static options
	 */
	private void initializesStaticOptionLists () {
		optionNameList = new ArrayList<String>();
		optionNameList.add("Show filtered variation");
		optionNameList.add("Show border of insertion");
		optionNameList.add("Show border of deletion");
		optionNameList.add("Show nucleotides of insertion stripes");
		optionNameList.add("Show nucleotides of deletion stripes");
		optionNameList.add("Show nucleotide of SNP stripes");

		optionValueList = new ArrayList<Integer>();
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_FILTERED_VARIANT));
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_INSERTION_EDGE));
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_DELETION_EDGE));
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_INSERTION_LETTERS));
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_DELETION_LETTERS));
		optionValueList.add(getOptionValue(MGDisplaySettings.DRAW_SNP_LETTERS));
	}


	private int getOptionValue (int option) {
		if (option == MGDisplaySettings.YES_MG_OPTION) {
			return MGDisplaySettings.YES_MG_OPTION;
		} else {
			return MGDisplaySettings.NO_MG_OPTION;
		}
	}


	/**
	 * Refreshes the static option radios according to the list
	 */
	private void refreshStaticOptionBoxes () {
		for (int i = 0; i < optionValueList.size(); i++) {
			if (optionValueList.get(i) == MGDisplaySettings.YES_MG_OPTION) {
				yesOptionRadioList.get(i).setSelected(true);
				noOptionRadioList.get(i).setSelected(false);
			} else {
				yesOptionRadioList.get(i).setSelected(false);
				noOptionRadioList.get(i).setSelected(true);
			}
		}
	}


	/////////////////////////////////////////// VCF Loader option

	private JPanel getDefaultDialogItemPanel () {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JLabel jlTitle = new JLabel("Default section to open");
		jlTitle.setPreferredSize(new Dimension(250, jlTitle.getPreferredSize().height));

		jcbDefaultItem = new JComboBox(PropertiesDialog.getPropertiesDialogMainItems());
		jcbDefaultItem.setPreferredSize(new Dimension(100, jcbDefaultItem.getPreferredSize().height));

		panel.add(jlTitle);
		panel.add(jcbDefaultItem);

		return panel;
	}


	/////////////////////////////////////////// VCF Loader option

	/**
	 * Creates the panel that displays the default group text for the VCF Loader.
	 */
	private JPanel getDefaultGroupTextPanel () {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JLabel jlTitle = new JLabel("Default group text name");
		jlTitle.setPreferredSize(new Dimension(250, jlTitle.getPreferredSize().height));

		jtfDefaultGroupText = new JTextField();
		//jtfDefaultGroupText.setText(MGDisplaySettings.getInstance().getVariousSettings().getDefaultGroupText());
		jtfDefaultGroupText.setPreferredSize(new Dimension(100, jtfDefaultGroupText.getPreferredSize().height));

		panel.add(jlTitle);
		panel.add(jtfDefaultGroupText);

		return panel;
	}


	/////////////////////////////////////////// Stripes transparency option

	/**
	 * Creates a panel that contains a slider and a label to show its current value.
	 * @return the panel
	 */
	private JPanel getSliderPanel () {
		// Initializes the panel and the layout
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(2, 1, 0, 0);
		panel.setLayout(layout);

		// Initializes the slider label value
		sliderValue = new JLabel(TRANSPARENCY_INIT + " %", SwingConstants.CENTER);

		// Initializes the slider
		slider = new JSlider(SwingConstants.HORIZONTAL, TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				int transparency = source.getValue();
				sliderValue.setText(transparency + " %");
			}
		});
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);

		// Adds label and slider to the panel
		panel.add(sliderValue);
		panel.add(slider);

		// Returns the panel
		return panel;
	}


	/////////////////////////////////////////// Stripes legend option

	/**
	 * Creates a panel that contains a slider and a label to show its current value.
	 * @return the panel
	 */
	private JPanel getStripeLegendPanel () {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JLabel jlTitle = new JLabel("Show legend");
		jlTitle.setPreferredSize(new Dimension(250, jlTitle.getPreferredSize().height));

		// Initializes the radio buttons
		yesLegendButton = new JRadioButton("yes");
		noLegendButton = new JRadioButton("no");
		if (showLegend) {
			yesLegendButton.setSelected(true);
		} else {
			yesLegendButton.setSelected(false);
		}

		// Group the radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(yesLegendButton);
		group.add(noLegendButton);

		//Register a listener for the radio buttons
		yesLegendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setShowLegend(true);}});
		noLegendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setShowLegend(false);}});

		// Adds label and buttons to the panel
		panel.add(jlTitle);
		panel.add(yesLegendButton);
		panel.add(noLegendButton);

		return panel;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////// MGDisplaySettings Panels

	/**
	 * Creates a panel to define MGDisplaySettings.DRAW_INSERTION_EDGE option.
	 * @return the panel
	 */
	private JPanel getStaticOptionPanel (final int option) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JLabel jlTitle = new JLabel(optionNameList.get(option));
		jlTitle.setPreferredSize(new Dimension(250, jlTitle.getPreferredSize().height));

		// Initializes the radio buttons
		JRadioButton yesButton = new JRadioButton("yes");
		JRadioButton noButton = new JRadioButton("no");
		yesOptionRadioList.add(yesButton);
		noOptionRadioList.add(noButton);

		//Register a listener for the radio buttons
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setOption(option, MGDisplaySettings.YES_MG_OPTION);}});
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setOption(option, MGDisplaySettings.NO_MG_OPTION);}});

		// Group the radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(yesButton);
		group.add(noButton);


		// Adds label and buttons to the panel
		panel.add(jlTitle);
		panel.add(yesButton);
		panel.add(noButton);

		return panel;
	}


	/**
	 * Set the option value list
	 * @param option	index (symbolizes the index of the static option)
	 * @param value		value (symbolizes the YES or NO)
	 */
	private void setOption (int option, int value) {
		optionValueList.set(option, value);
	}


	/////////////////////////////////////////// Reference stripes legend option

	/**
	 * Creates a panel that contains a slider and a label to show its current value.
	 * @return the panel
	 */
	private JPanel getReferenceOptionPanel () {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// First option title
		JLabel jlTitle01 = new JLabel("Show reference stripes");
		jlTitle01.setPreferredSize(new Dimension(250, jlTitle01.getPreferredSize().height));

		// Second option title
		JLabel jlTitle02 = new JLabel("Reference stripes color");
		jlTitle02.setPreferredSize(new Dimension(250, jlTitle01.getPreferredSize().height));

		// Initializes the radio buttons
		yesReferenceButton = new JRadioButton("yes");
		noReferenceButton = new JRadioButton("no");
		if (showReference) {
			yesReferenceButton.setSelected(true);
		} else {
			yesReferenceButton.setSelected(false);
		}

		// Group the radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(yesReferenceButton);
		group.add(noReferenceButton);

		//Register a listener for the radio buttons
		yesReferenceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setShowReference(true);}});
		noReferenceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {setShowReference(false);}});

		// Create the button for the color
		JButton colorButton = new JButton();
		Dimension colorDim = new Dimension(13, 13);
		colorButton.setPreferredSize(colorDim);
		colorButton.setBorder(null);
		colorButton.setToolTipText("Select color for reference stripes.");
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton button = (JButton) arg0.getSource();
				Color newColor = GenPlayColorChooser.showDialog(getCurrentInstance(), button.getBackground());
				if (newColor != null) {
					button.setBackground(newColor);
					referenceColor = newColor;
				}
			}
		});
		// Initialize button color
		colorButton.setBackground(referenceColor);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Add the first line
		// The title
		panel.add(jlTitle01, gbc);

		// The yes button
		gbc.gridx = 1;
		panel.add(yesReferenceButton);

		// The no button
		gbc.gridx = 2;
		gbc.weightx = 1;
		panel.add(noReferenceButton);

		// Add the second line
		// The title
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		panel.add(jlTitle02, gbc);

		// The color button
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		panel.add(colorButton, gbc);

		return panel;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * @param showLegend the showLegend to set
	 */
	private void setShowLegend (boolean showLegend) {
		this.showLegend = showLegend;
	}


	/**
	 * @param showReference the showReference to set
	 */
	private void setShowReference (boolean showReference) {
		this.showReference = showReference;
	}


	/**
	 * Set the settings panel with specific values
	 * @param defaultItemDialog the default item dialog to show
	 * @param defaultGroupText 	the default group text for VCF Loader
	 * @param transparency		transparency value
	 * @param showLegend		legend value
	 */
	public void setSettings (String defaultItemDialog, String defaultGroupText, int transparency, boolean showLegend) {
		jcbDefaultItem.setSelectedItem(defaultItemDialog);

		jtfDefaultGroupText.setText(defaultGroupText);

		sliderValue.setText(transparency + " %");
		slider.setValue(transparency);

		this.showLegend = showLegend;
		if (showLegend) {
			yesLegendButton.setSelected(true);
			noLegendButton.setSelected(false);
		} else {
			yesLegendButton.setSelected(false);
			noLegendButton.setSelected(true);
		}

		this.showReference = MGDisplaySettings.DRAW_REFERENCE_INSERTION == MGDisplaySettings.YES_MG_OPTION;
		if (showReference) {
			yesReferenceButton.setSelected(true);
			noReferenceButton.setSelected(false);
		} else {
			yesReferenceButton.setSelected(false);
			noReferenceButton.setSelected(true);
		}

		initializesStaticOptionLists();
		refreshStaticOptionBoxes();
	}


	/**
	 * @return the default item dialog to show
	 */
	public String getDefaultItemDialog () {
		return jcbDefaultItem.getSelectedItem().toString();
	}


	/**
	 * @return the default group text
	 */
	public String getDefaultGroupText () {
		return jtfDefaultGroupText.getText();
	}


	/**
	 * @return the transparency value
	 */
	public int getTransparency() {
		return slider.getValue();
	}


	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return showLegend;
	}


	/**
	 * @return the optionValueList
	 */
	public List<Integer> getOptionValueList() {
		return optionValueList;
	}


	/**
	 * @return the referenceColor
	 */
	public Color getReferenceColor() {
		return referenceColor;
	}


	/**
	 * @return the showReference
	 */
	public boolean isShowReference() {
		return showReference;
	}


	/**
	 * @return the stripes editing panel instance
	 */
	protected Component getCurrentInstance() {
		return this;
	}
}
