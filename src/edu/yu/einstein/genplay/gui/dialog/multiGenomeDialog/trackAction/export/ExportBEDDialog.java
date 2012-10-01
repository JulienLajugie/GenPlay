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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.export;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportUtils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportBEDDialog extends MultiGenomeTrackActionDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -1321930230220361216L;

	private final static String DIALOG_TITLE = "Export as BED file";

	private String genomeName;
	private AlleleType allele;
	private JTextField 	jtfFile;		// Text field for the path of the new VCF file
	private VCFHeaderType header;
	private CoordinateSystemType coordinateSystem;


	/**
	 * Constructor of {@link ExportBEDDialog}
	 * @param settings the export settings
	 */
	public ExportBEDDialog(ExportSettings settings) {
		super(settings, DIALOG_TITLE);
	}


	@Override
	protected void initializeContentPanel() {
		// Initialize the content panel
		contentPanel = new JPanel();

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Export settings");
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


		contentPanel.add(getBedPanel(), gbc);
		gbc.gridy++;

		contentPanel.add(getGenomeSelectionPanel(settings.getGenomeNames()), gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		contentPanel.add(getIDPanel(settings.getFileList()), gbc);
	}

	/**
	 * @return the panel to select a path to export the track
	 */
	private JPanel getBedPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Create the layout
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Create the title label
		JLabel label = new JLabel("Please select a destination file:");

		// Create the text field
		jtfFile = new JTextField();
		jtfFile.setEditable(false);
		Dimension jtfDim = new Dimension(MIN_DIALOG_WIDTH - 25, 21);
		ExportUtils.setComponentSize(jtfFile, jtfDim);

		// Create the button
		JButton button = new JButton();
		Dimension bDim = new Dimension(20, 20);
		ExportUtils.setComponentSize(button, bDim);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilter[] filters = {new BedFilter()};
				File file = ExportUtils.getFile(filters, false);
				if (file != null) {
					jtfFile.setText(file.getPath());
				}
			}
		});

		// Add components
		panel.add(label, gbc);

		gbc.gridy++;
		panel.add(jtfFile, gbc);

		gbc.gridx++;
		panel.add(button, gbc);

		return panel;
	}


	private JPanel getGenomeSelectionPanel (List<String> genomeList) {
		// Create the panel
		JPanel panel = new JPanel();


		// Create the labels
		JLabel genomeLabel = new JLabel("Select a genome to export:");
		JLabel alleleLabel = new JLabel("Select the allele(s) of the genome to export:");
		JLabel exportLabel = new JLabel("Select the genome coordinate system for start/stop positions:");


		// Create the boxes
		JComboBox jcbGenome = getGenomeComboBox(genomeList);
		JComboBox jcbAllele = getAlleleTypeComboBox();

		// Create the radios
		JRadioButton metaButton = new JRadioButton("Meta genome");
		JRadioButton referenceButton = new JRadioButton("Reference genome");
		JRadioButton currentButton = new JRadioButton("Current genome");
		ButtonGroup group = new ButtonGroup();
		group.add(metaButton);
		group.add(referenceButton);
		group.add(currentButton);
		currentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {coordinateSystem = CoordinateSystemType.CURRENT_GENOME;}});
		referenceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {coordinateSystem = CoordinateSystemType.REFERENCE;}});
		metaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {coordinateSystem = CoordinateSystemType.METAGENOME;}});
		currentButton.setSelected(true);
		coordinateSystem = CoordinateSystemType.CURRENT_GENOME;

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

		// Insert the allele type label
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(alleleLabel, gbc);

		// Insert the allele type combo box
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		panel.add(jcbAllele, gbc);

		// Insert the coordinate system label
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(exportLabel, gbc);

		// Insert the meta genome radio
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		panel.add(metaButton, gbc);

		// Insert the reference genome radio
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		panel.add(referenceButton, gbc);

		// Insert the current genome radio
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 5, 0);
		gbc.weighty = 1;
		panel.add(currentButton, gbc);

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


	/**
	 * Create the allele type combo box.
	 * @return the allele type combo box
	 */
	private JComboBox getAlleleTypeComboBox () {
		// Creates the combo box
		Object[] alleles = new Object[]{AlleleType.BOTH, AlleleType.ALLELE01, AlleleType.ALLELE02};
		JComboBox jcbAllele = new JComboBox(alleles);
		jcbAllele.setSelectedIndex(0);
		allele =  (AlleleType) jcbAllele.getSelectedItem();

		//Dimension
		int height = jcbAllele.getFontMetrics(jcbAllele.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(MIN_DIALOG_WIDTH - 50, height);
		jcbAllele.setPreferredSize(dimension);
		jcbAllele.setMinimumSize(dimension);

		jcbAllele.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				allele =  (AlleleType)((JComboBox) arg0.getSource()).getSelectedItem();
			}
		});

		return jcbAllele;
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
			List<VCFHeaderType> numberHeader = file.getHeader().getAllNumberHeader();
			for (VCFHeaderType header: numberHeader) {
				/*if (header instanceof VCFHeaderAdvancedType) {
					VCFHeaderAdvancedType advancedHeader = (VCFHeaderAdvancedType) header;
					String number = advancedHeader.getNumber();
					if (!number.equals("A") && !number.equals("G")) {
						int valueNumber = Integer.parseInt(number);
						if ((valueNumber == 1) || (valueNumber == 2)) {
							result.add(header);
						}
					}
				} else if (header.getColumnCategory() == VCFColumnName.QUAL) {
					result.add(header);
				}*/
				if (!((header.getColumnCategory() == VCFColumnName.FORMAT) && header.getId().equals("PL"))) {
					result.add(header);
				}
			}
		}

		return result;
	}


	/**
	 * @return the path of the selected BED
	 */
	public String getBEDPath () {
		return jtfFile.getText();
	}


	/**
	 * @return the selected genome name
	 */
	public String getGenomeName () {
		return genomeName;
	}


	/**
	 * @return the selected allele type
	 */
	public AlleleType getAlleleType () {
		return allele;
	}


	/**
	 * @return the header
	 */
	public VCFHeaderType getHeader() {
		return header;
	}


	/**
	 * @return the coordinateSystem
	 */
	public CoordinateSystemType getCoordinateSystem() {
		return coordinateSystem;
	}


	@Override
	protected String getErrors() {
		String error = "";

		String filePath = jtfFile.getText();
		if (filePath == null) {
			error += "The path of the file has not been found.";
		} else if (filePath.isEmpty()){
			error += "The path of the file has not been found.";
		}

		return error;
	}
}
