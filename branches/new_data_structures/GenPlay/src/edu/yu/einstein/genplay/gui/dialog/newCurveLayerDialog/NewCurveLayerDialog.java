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
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.sf.samtools.SAMReadGroupRecord;
import edu.yu.einstein.genplay.core.IO.extractor.Extractor;
import edu.yu.einstein.genplay.core.IO.extractor.SAMExtractor;
import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.gui.dialog.genomeSelectionPanel.GenomeSelectionPanel;
import edu.yu.einstein.genplay.util.Images;


/**
 * A dialog for the input for a curve layer loading
 * @author Julien Lajugie
 */
public class NewCurveLayerDialog extends JDialog {

	private static final long serialVersionUID = -4896476921693184496L; // generated ID
	private static final int 			INSET = 7;					// inset between the components
	private final 	LayerNamePanel 			layerNamePanel;			// panel for the layer name
	private final 	BinSizePanel			binSizePanel;			// panel for the binsize
	private final 	ChromoSelectionPanel 	chromoSelectionPanel;	// panel for selecting chromosomes
	private final 	CalculMethodPanel 		calculMethodPanel;		// panel for the method of score calculation
	private final 	StrandSelectionPanel	strandSelectionPanel;	// panel for the selection of the strand to extract
	private final 	ReadDefinitionPanel		readDefinitionPanel;	// panel for the shift and the length of the reads
	private final	GenomeSelectionPanel	genomeSelectionPanel;	// panel for the selection of the genome in a multigenome project
	private final	SAMPanel				samPanel;				// panel for SAM/BAM options
	private final 	JButton 				jbOk; 					// Button OK
	private final 	JButton 				jbCancel; 				// Button cancel
	private int 							approved = CANCEL_OPTION;	// indicate if the user canceled or validated

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;

	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;


	/**
	 * Creates an instance of {@link NewCurveLayerDialog}
	 * @param extractor {@link Extractor} that will be configured by this dialog
	 */
	public NewCurveLayerDialog(Extractor extractor) {
		super();
		// we don't need to select method of calculation for SAM files
		// (it's always addition because the reads have no scores)
		boolean isMethodNeeded = !(extractor instanceof SAMExtractor);
		// need to add the strand panel if the extractor is stranded
		boolean isStrandNeeded = extractor instanceof StrandedExtractor;
		boolean isGenomeSelectionPanelNeeded = ProjectManager.getInstance().isMultiGenomeProject();
		boolean isSAMPanelNeeded = extractor instanceof SAMExtractor;

		// create panels
		layerNamePanel = new LayerNamePanel(extractor.getDataName());
		chromoSelectionPanel = new ChromoSelectionPanel();
		binSizePanel = new BinSizePanel();
		if (isStrandNeeded) {
			strandSelectionPanel = new StrandSelectionPanel();
			readDefinitionPanel = new ReadDefinitionPanel();
		} else {
			strandSelectionPanel = null;
			readDefinitionPanel = null;
		}
		if (isMethodNeeded) {
			calculMethodPanel = new CalculMethodPanel();
		} else {
			calculMethodPanel = null;
		}
		if (isSAMPanelNeeded) {
			samPanel = new SAMPanel((SAMExtractor) extractor);
		} else {
			samPanel = null;
		}

		if (isGenomeSelectionPanelNeeded) {
			genomeSelectionPanel = new GenomeSelectionPanel();
		} else {
			genomeSelectionPanel = null;
		}

		// create the OK button
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (binSizePanel != null) {
					binSizePanel.saveDefault();
				}
				if (calculMethodPanel != null) {
					calculMethodPanel.saveDefault();
				}
				if (chromoSelectionPanel != null) {
					chromoSelectionPanel.saveDefault();
				}
				if (strandSelectionPanel != null) {
					strandSelectionPanel.saveDefault();
				}
				if (readDefinitionPanel != null) {
					readDefinitionPanel.saveDefault();
				}
				if (genomeSelectionPanel != null) {
					genomeSelectionPanel.saveDefault();
				}
				if (samPanel != null) {
					samPanel.saveDefault();
				}
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// create the cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(INSET, INSET, INSET, INSET);
		c.weightx = 1;
		c.weighty = 1;
		add(layerNamePanel, c);

		c.gridy++;
		add(binSizePanel, c);

		if (isMethodNeeded) {
			c.gridy++;
			add(calculMethodPanel, c);
		}

		if (isSAMPanelNeeded) {
			c.gridy++;
			add(samPanel, c);
		}

		if (isStrandNeeded) {
			c.gridy++;
			add(strandSelectionPanel, c);

			c.gridy++;
			add(readDefinitionPanel, c);
		}

		if (genomeSelectionPanel != null) {
			c.gridy++;
			add(genomeSelectionPanel, c);
		}

		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridy++;
		add(jbOk, c);

		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 1;
		add(jbCancel, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridheight = c.gridy;
		c.gridx = 1;
		c.gridy = 0;
		add(chromoSelectionPanel, c);

		setTitle("New Layer");
		setIconImage(Images.getApplicationImage());
		pack();
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(getRootPane());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * @return the selected allele type
	 */
	public AlleleType getAlleleType() {
		return genomeSelectionPanel.getAlleleType();
	}


	/**
	 * @return the selected BinSize
	 */
	public int getBinSize() {
		return binSizePanel.getBinSize();
	}


	/**
	 * @return the strand shifting value
	 */
	public int getFragmentLengthValue() {
		return readDefinitionPanel.getFragmentLengthValue();
	}


	/**
	 * @return the name of the genome
	 */
	public String getGenomeName() {
		return genomeSelectionPanel.getGenomeName();
	}


	/**
	 * @return the name of the layer
	 */
	public String getLayerName() {
		return layerNamePanel.getLayerName();
	}


	/**
	 * @return the minimum mapping quality to extract
	 */
	public int getMappingQuality() {
		return samPanel.getMappingQuality();
	}


	/**
	 * @return the selected read group, null if all read-groups are selected
	 */
	public SAMReadGroupRecord getReadGroup() {
		return samPanel.getSelectedReadGroup();
	}


	/**
	 * @return the read length value. Returns zero if the read length is not specified
	 */
	public int getReadLengthValue() {
		return readDefinitionPanel.getReadLengthValue();
	}


	/**
	 * @return the selected {@link ScoreOperation}
	 */
	public ScoreOperation getScoreCalculationMethod() {
		// the default value is addition
		if (calculMethodPanel == null) {
			return ScoreOperation.ADDITION;
		}
		return calculMethodPanel.getScoreCalculationMethod();
	}


	/**
	 * @return an array of booleans set to true for the selected {@link Chromosome}
	 */
	public boolean[] getSelectedChromosomes() {
		return chromoSelectionPanel.getSelectedChromosomes();
	}


	/**
	 * @return the Strand to extract. Null if both
	 */
	public Strand getStrandToExtract() {
		return strandSelectionPanel.getStrandToExtract();
	}


	/**
	 * @return true if all the reads should be extracted
	 */
	public boolean isAllReadsSelected() {
		return samPanel.isAllReadsSelected();
	}


	/**
	 * @return true if the create bin list option is selected
	 */
	public boolean isCreateBinListSelected() {
		return binSizePanel.isCreateBinListSelected();
	}


	/**
	 * @return true if the pair end mode is selected
	 */
	public boolean isPairedEndSelected() {
		return samPanel.isPairedEndSelected();
	}


	/**
	 * @return true if the unique reads and the primary alignments should be extracted
	 */
	public boolean isPrimaryAligmentSelected() {
		return samPanel.isPrimaryAligmentSelected();
	}


	/**
	 * @return true if the remove duplicates options is selected
	 */
	public boolean isRemoveDuplicatesSelected() {
		return samPanel.isRemoveDuplicatesSelected();
	}


	/**
	 * @return true if the extraction should be done in single end mode
	 */
	public boolean isSingleEndSelected() {
		return samPanel.isSingleEndSelected();
	}


	/**
	 * @return true if only the unique reads should be extracted
	 */
	public boolean isUniqueSelected() {
		return samPanel.isUniqueSelected();
	}


	/**
	 * Shows the component
	 * @param parent parent component. Can be null
	 * @return OK or CANCEL
	 */
	public int showDialog(Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
