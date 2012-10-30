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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import edu.yu.einstein.genplay.core.converter.ConverterFactory;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.TrackType;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ConvertDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -895612710328946926L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private int dialogwidth = 400;
	private JPanel binPanel;


	// Input track elements
	private final Track<?> inputTrack;
	private final TrackType[] trackTypes;

	// Bin list elements
	private JSpinner 	jsBinSize;
	private JComboBox 	jcbCalculMetod; 	// combo box for the score calculation method
	private JComboBox 	jcbDataPrecision; 	// combo box for the data precision

	// Output track elements
	private TrackType outputTrackType;		// The output type track.
	private JTextField jtfTrackName;		// Text field for the track name.
	private Track<?> outputTrack;		// The


	/**
	 * Constructor of {@link ConvertDialog}
	 * @param inputTrack the track to convert
	 */
	public ConvertDialog (Track<?> inputTrack) {
		this.inputTrack = inputTrack;
		if ((ChromosomeListOfLists<?>) inputTrack.getData() != null) {
			this.trackTypes = ConverterFactory.getTrackTypes((ChromosomeListOfLists<?>) inputTrack.getData());
		} else if ((ChromosomeListOfLists<?>) inputTrack.getMask() != null) {
			this.trackTypes = ConverterFactory.getMaskTrackType();
		} else {
			this.trackTypes = null;
		}

		// Layout settings
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Get the panels
		JPanel inputTrackPanel = getInputTrackPanel();
		JPanel outputTypeTrackPanel = getOutputTypeTrack();
		JPanel outputTrackPanel = getOutputTrackPanel();
		JPanel validationPanel = getValidationPanel();

		// Set the panels dimension
		setDimension(inputTrackPanel);
		setDimension(outputTypeTrackPanel);
		setDimension(outputTrackPanel);
		setDimension(validationPanel);

		// Add the input track panel
		add(inputTrackPanel, gbc);

		// Add the output type track panel
		gbc.gridy++;
		add(outputTypeTrackPanel, gbc);

		// Add the output track panel
		gbc.gridy++;
		add(outputTrackPanel, gbc);

		// Add the validation panel
		gbc.gridy++;
		gbc.weighty = 1;
		add(validationPanel, gbc);

		// Dialog settings
		setTitle("Convert the track");
		setIconImage(Images.getApplicationImage());
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModal(true);
		setVisible(true);

		return approved;
	}


	/**
	 * Set the dimension of a panel with the dialog width.
	 * @param panel the panel
	 */
	private void setDimension (JPanel panel) {
		int height = panel.getPreferredSize().height;
		Dimension dimension = new Dimension(dialogwidth, height);
		panel.setPreferredSize(dimension);
	}


	/**
	 * Set the width of the dialog (cannot be less than 400) according to the length of a string.
	 * @param s the string
	 */
	private void setDialogWidth (String s) {
		JLabel test = new JLabel();
		int width = getFontMetrics(test.getFont()).stringWidth(s);
		if (width > dialogwidth) {
			dialogwidth = width;
		}
	}


	/**
	 * Creates the panel describing the input track
	 * @return	the input track panel
	 */
	private JPanel getInputTrackPanel () {
		// Creates panel elements
		JLabel jlName = new JLabel("Track name: " + inputTrack.getName());
		JLabel jlType = new JLabel(getInputTrackTypeString());
		setDialogWidth(jlName.getText());

		// Creates the panel
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Input Track"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Add the track name
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jlName, gbc);

		// Add the track type
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 5, 5, 0);
		panel.add(jlType, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the input track type description
	 */
	private String getInputTrackTypeString () {
		String typeName = "Track type: ";
		if (inputTrack.getData() instanceof BinList) {
			typeName += "Fixed Window Track";
		} else if (inputTrack.getData() instanceof ScoredChromosomeWindowList) {
			typeName += "Variable Window Track";
		} else if (inputTrack.getData() instanceof GeneList) {
			typeName += "Gene Track";
		}
		return typeName;
	}


	/**
	 * @return the panel that manages the output track type option
	 */
	private JPanel getOutputTypeTrack () {
		// Creates the panel
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Convertion Options"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Create and add elements
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < trackTypes.length; i++) {
			final TrackType currentTrackType = trackTypes[i];
			JRadioButton radio = new JRadioButton(currentTrackType.toString());
			group.add(radio);
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					outputTrackType = currentTrackType;
					updateBinPanel(outputTrackType);
				}
			});

			if (i == 0) {
				radio.setSelected(true);
				outputTrackType = currentTrackType;
				gbc.insets = new Insets(5, 5, 0, 0);
				gbc.gridy = 0;
			} else {
				gbc.gridy++;
				gbc.insets = new Insets(0, 5, 0, 0);
				if (i == (trackTypes.length - 1)) {
					gbc.weighty = 1;
				}
			}
			panel.add(radio, gbc);
			if (currentTrackType == TrackType.BIN) {
				gbc.gridy++;
				gbc.insets = new Insets(0, 25, 0, 0);
				binPanel = getBinPanel();
				updateBinPanel(outputTrackType);
				panel.add(binPanel, gbc);
			}
		}

		// Return the panel
		return panel;
	}


	/**
	 * @return the panel for the {@link BinList} options
	 */
	private JPanel getBinPanel () {
		Dimension dimension = new Dimension(100, 20);

		// Create bin size element
		JLabel jlBinSize = new JLabel("Select a bin size:");
		SpinnerNumberModel snm = new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 100);
		jsBinSize = new JSpinner(snm);
		jsBinSize.setPreferredSize(dimension);

		// Create the calculation method elements
		JLabel jlCalculationMethod = new JLabel("Select a calculation method:");
		jcbCalculMetod = new JComboBox(ScoreCalculationMethod.values());
		jcbCalculMetod.setSelectedItem(ScoreCalculationMethod.SUM);
		jcbCalculMetod.setPreferredSize(dimension);

		// Create the data precision elements
		JLabel jlDataPrecision = new JLabel("Select a data precision:");
		jcbDataPrecision = new JComboBox(DataPrecision.values());
		jcbDataPrecision.setSelectedItem(DataPrecision.PRECISION_32BIT);
		jcbCalculMetod.setPreferredSize(dimension);

		// Creates the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;

		// Add the bin size elements
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(jlBinSize, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		panel.add(jsBinSize, gbc);

		// Add the calculation method elements
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		panel.add(jlCalculationMethod, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jcbCalculMetod, gbc);

		// Add the data precision elements
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		panel.add(jlDataPrecision, gbc);
		gbc.gridx = 1;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jcbDataPrecision, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * Enable/Disable the bin panel according to th output track type
	 * @param trackType the output track type
	 */
	private void updateBinPanel (TrackType trackType) {
		if (binPanel != null) {
			boolean enable = false;
			if ((trackType != null) && (trackType == TrackType.BIN)) {
				enable = true;
			}
			jsBinSize.setEnabled(enable);
			jcbCalculMetod.setEnabled(enable);
			jcbDataPrecision.setEnabled(enable);
		}
	}


	/**
	 * @return the panel that manages the output track options
	 */
	private JPanel getOutputTrackPanel () {
		// Creates panel elements
		JLabel jlTrackName = new JLabel("Output track name:");
		JLabel jlTrack = new JLabel("Output track:");
		jtfTrackName = new JTextField("Converted " + inputTrack.getName());
		jtfTrackName.setColumns(15);
		JComboBox jcbOutputTrack = new JComboBox(MainFrame.getInstance().getTrackList().getEmptyTracks());
		jcbOutputTrack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {outputTrack = (Track<?>) ((JComboBox)e.getSource()).getSelectedItem();}
		});
		jcbOutputTrack.setSelectedIndex(0);

		// Creates the panel
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Output Track"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;

		gbc.weightx = 1;
		gbc.weighty = 0;

		// Add the output track name
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 5, 0, 0);
		panel.add(jlTrackName, gbc);
		gbc.gridx = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(jtfTrackName, gbc);

		// Add the output track selection box
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jlTrack, gbc);
		gbc.gridx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 0, 10, 0);
		panel.add(jcbOutputTrack, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * @return the output track type
	 */
	public TrackType getOutputTrackType () {
		return outputTrackType;
	}


	/**
	 * @return the output track name
	 */
	public String getOutputTrackName() {
		return jtfTrackName.getText();
	}


	/**
	 * @return the output track
	 */
	public Track<?> getOutputTrack() {
		return outputTrack;
	}


	/**
	 * @return the selected binsize
	 */
	public int getBinSize() {
		return (Integer) jsBinSize.getValue();
	}


	/**
	 * @return the selected score calculation method
	 */
	public ScoreCalculationMethod getScoreCalculationMethod() {
		return (ScoreCalculationMethod) jcbCalculMetod.getSelectedItem();
	}


	/**
	 * @return the selected {@link DataPrecision}
	 */
	public DataPrecision getDataPrecision() {
		return (DataPrecision) jcbDataPrecision.getSelectedItem();
	}
}
