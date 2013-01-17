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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.convert;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ConvertSCWDialog extends MultiGenomeTrackActionDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -1321930230220361216L;

	private final static String DIALOG_TITLE = "Convert as variable window track";

	private String genomeName;
	private JComboBox jcbAlleleTrack01;
	private JComboBox jcbAlleleTrack02;
	private VCFHeaderType header;


	/**
	 * Constructor of {@link ConvertSCWDialog}
	 * @param settings the export settings
	 */
	public ConvertSCWDialog(ExportSettings settings) {
		super(settings, DIALOG_TITLE);
	}


	@Override
	protected void initializeContentPanel() {
		// Initialize the content panel
		contentPanel = new JPanel();

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Conversion settings");
		contentPanel.setBorder(titledBorder);

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		contentPanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		contentPanel.add(getGenomeSelectionPanel(settings.getGenomeNames()), gbc);

		gbc.gridy++;
		contentPanel.add(getIDPanel(settings.getFileList()), gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		contentPanel.add(getOptionPanel(), gbc);
	}


	private JPanel getGenomeSelectionPanel (List<String> genomeList) {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the labels
		JLabel genomeLabel = new JLabel("Select a genome to export:");
		JLabel alleleTrackLabel01 = null;
		JLabel alleleTrackLabel02 = null;

		// Create the boxes
		JComboBox jcbGenome = getGenomeComboBox(genomeList);
		alleleTrackLabel01 = new JLabel("Select the track for the " + AlleleType.ALLELE01.toString() + " allele:");
		alleleTrackLabel02 = new JLabel("Select the track for the " + AlleleType.ALLELE02.toString() + " allele:");
		jcbAlleleTrack01 = getTrackListBox();
		jcbAlleleTrack02 = getTrackListBox();

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;


		// Insert the genome label
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(genomeLabel, gbc);

		// Insert the genome combo box
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		panel.add(jcbGenome, gbc);

		if (alleleTrackLabel01 != null) {
			// Insert the first allele track label
			gbc.gridy++;
			gbc.insets = new Insets(10, 0, 0, 0);
			panel.add(alleleTrackLabel01, gbc);

			// Insert the first allele track combo box
			gbc.insets = new Insets(0, 10, 0, 0);

			gbc.gridy++;
			panel.add(jcbAlleleTrack01, gbc);
		}

		if (alleleTrackLabel02 != null) {
			// Insert the second allele track label
			gbc.gridy++;
			gbc.insets = new Insets(10, 0, 0, 0);
			panel.add(alleleTrackLabel02, gbc);

			// Insert the second allele track combo box
			gbc.gridy++;
			gbc.weighty = 1;
			gbc.insets = new Insets(0, 10, 5, 0);
			panel.add(jcbAlleleTrack02, gbc);
		}

		return panel;
	}


	/**
	 * Creates the genome combo box.
	 * @return the genome combo box
	 */
	private JComboBox getGenomeComboBox (List<String> genomeList) {
		// Creates the combo box
		JComboBox jcbGenome = new JComboBox(genomeList.toArray());
		jcbGenome.setSelectedIndex(0);
		genomeName = jcbGenome.getSelectedItem().toString();

		//Dimension
		int height = jcbGenome.getFontMetrics(jcbGenome.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(MIN_DIALOG_WIDTH - 50, height);
		jcbGenome.setPreferredSize(dimension);
		jcbGenome.setMinimumSize(dimension);

		jcbGenome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				genomeName = ((JComboBox) arg0.getSource()).getSelectedItem().toString();
			}
		});

		return jcbGenome;
	}


	private JComboBox getTrackListBox () {
		Track<?>[] tracks = MainFrame.getInstance().getTrackList().getEmptyTracks();

		JComboBox box = new JComboBox();
		box.addItem("Do not convert this allele.");
		for (Track<?> track: tracks) {
			box.addItem(track);
		}

		return box;
	}


	private JPanel getIDPanel (List<VCFFile> fileList) {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the label
		JLabel idLabel = new JLabel("Select the ID field to use as a score value:");

		// Create the combo box
		JComboBox box = getIDComboBox(retrieveHeaderFields(fileList));

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;


		// Insert the genome label
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(idLabel, gbc);


		// Insert the header type combo box
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 10, 5, 0);
		panel.add(box, gbc);

		return panel;
	}


	private JComboBox getIDComboBox (List<VCFHeaderType> headers) {
		JComboBox box = new JComboBox(headers.toArray());
		box.setSelectedIndex(0);
		header = (VCFHeaderType) box.getSelectedItem();
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				header =  (VCFHeaderType)((JComboBox) arg0.getSource()).getSelectedItem();
			}
		});
		return box;
	}


	private List<VCFHeaderType> retrieveHeaderFields (List<VCFFile> fileList) {
		List<VCFHeaderType> result = new ArrayList<VCFHeaderType>();
		for (VCFFile file: fileList) {
			List<VCFHeaderType> numberHeader = file.getHeader().getAllSortedNumberHeader();
			for (VCFHeaderType header: numberHeader) {
				if (!((header.getColumnCategory() == VCFColumnName.FORMAT) && header.getId().equals("PL"))) {
					result.add(header);
				}
			}
		}
		return result;
	}


	/**
	 * @return the selected genome name
	 */
	public String getGenomeName () {
		return genomeName;
	}


	/**
	 * @return the selected track of the first allele
	 */
	public Track<?> getFirstAlleleTrack () {
		if (jcbAlleleTrack01 != null) {
			Object object = jcbAlleleTrack01.getSelectedItem();
			if (object instanceof Track<?>) {
				return (Track<?>) object;
			}
		}
		return null;
	}


	/**
	 * @return the selected track of the second allele
	 */
	public Track<?> getSecondAlleleTrack () {
		if (jcbAlleleTrack02 != null) {
			Object object = jcbAlleleTrack02.getSelectedItem();
			if (object instanceof Track<?>) {
				return (Track<?>) object;
			}
		}
		return null;
	}


	/**
	 * @return the header
	 */
	public VCFHeaderType getHeader() {
		return header;
	}


	@Override
	protected String getErrors() {
		String error = "";

		if ((getFirstAlleleTrack() == null) && (getSecondAlleleTrack() == null)) {
			error += "No track has been selected.";
		}

		if ((jtfDotValue != null) && jtfDotValue.isEnabled() && (getDotValue() == null)) {
			if (!error.isEmpty()) {
				error += "\n";
			}
			error += "The defined constant for \".\" in genotype does not seem to be a valid number.";
		}

		return error;
	}
}
