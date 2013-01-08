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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayMultiListScanner;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog.VCFLineDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;
import edu.yu.einstein.genplay.util.Images;

/**
 * This class shows variant stripe information. It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantInformationDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4932470485711131874L;

	/** Dialog width */
	public static final int WIDTH = 250; // width of the dialog

	private final VCFLineDialog vcfLineDialog;
	private VariantDisplayMultiListScanner iterator;

	private Variant currentVariant; // the current variant object to display
	private VCFLine currentLine; // the current variant object to display

	private final JButton jbFullLine; // button to show the full line
	private SearchOption options;

	private final JPanel headerPanel; // panel containing the global information
	private final JPanel infoPanel; // panel containing the INFO field information of the VCF
	private final JPanel formatPanel; // panel containing the FORMAT field information of the VCF
	private final JPanel navigationPanel; // panel to move forward/backward

	/**
	 * Constructor of {@link VariantInformationDialog}
	 * @param multiGenomeDrawer the multigenome drawer
	 */
	public VariantInformationDialog(MultiGenomeDrawer multiGenomeDrawer) {
		super(MainFrame.getInstance());
		this.vcfLineDialog = new VCFLineDialog();
		options = new SearchOption();
		int trackNumber = MainFrame.getInstance().getTrackList().getTrackNumberFromMGGenomeDrawer(multiGenomeDrawer);
		String title = "Variant properties";
		if (trackNumber > 0) {
			title += " (Track " + trackNumber + ")";
		}

		// Dialog settings
		setTitle(title);
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		// Create the full line button
		jbFullLine = new JButton("See the full line");
		jbFullLine.setToolTipText("See the whole VCF line.");
		jbFullLine.setMargin(new Insets(0, 0, 0, 0));
		jbFullLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showVCFLine();
			}
		});

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Initialize panels
		headerPanel = new JPanel();
		infoPanel = new JPanel();
		formatPanel = new JPanel();
		navigationPanel = new JPanel();

		// Add content
		add(headerPanel, gbc);

		gbc.gridy++;
		add(infoPanel, gbc);

		gbc.gridy++;
		add(formatPanel, gbc);

		gbc.gridy++;
		add(jbFullLine, gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		add(navigationPanel, gbc);
	}

	/**
	 * Method for showing the dialog box.
	 * @param iterator the multi list iterator
	 * @param X X position on the screen
	 * @param Y Y position on the screen
	 */
	public void show(VariantDisplayMultiListScanner iterator, int X, int Y) {
		this.iterator = iterator;
		this.currentVariant = iterator.getCurrentVariants().get(0);
		refreshDialog();
		setLocation(X, Y);
		setVisible(true);
	}

	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newVariant the variant to display
	 */
	private void refreshDialog() {
		// Initialize the current variant
		if (currentVariant == null) {
			currentLine = null;
		} else {
			this.currentLine = currentVariant.getVCFLine();
			if (currentLine != null) {
				this.currentLine.processForAnalyse();
			}
		}

		// Initialize the content of the dialog
		initContent();

		// Relocate the screen position
		relocateScreenPosition();
	}

	/**
	 * Initializes the content of the dialog box according to a variant.
	 */
	private void initContent() {
		String genomeName = getCurrentGenomeName();
		VariantInfo variantInfo;
		VariantFormat variantFormat;

		if (currentLine == null) {
			variantInfo = new VariantInfo(null);
			variantFormat = new VariantFormat(null, null, null);
		} else {
			variantInfo = new VariantInfo(currentLine);
			variantFormat = new VariantFormat(currentVariant, currentLine, genomeName);
		}


		updatePanel(headerPanel, new GlobalInformationPanel(currentVariant, currentLine, genomeName));
		updatePanel(infoPanel, variantInfo.getPane());
		updatePanel(formatPanel, variantFormat.getPane());
		NavigationPanel newNavigationPanel = new NavigationPanel(this);
		if (currentLine == null) {
			jbFullLine.setEnabled(false);
		} else {
			jbFullLine.setEnabled(true);
		}
		updatePanel(navigationPanel, newNavigationPanel);

		validate();

		pack();
	}

	/**
	 * Locates the screen position to the start position of the actual variant.
	 */
	private void relocateScreenPosition() {
		int variantStart = currentVariant.getStart();
		GenomeWindow currentGenomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		int width = currentGenomeWindow.getSize();
		int startWindow = variantStart - (width / 2);
		int stopWindow = startWindow + width;
		Chromosome chromosome = currentGenomeWindow.getChromosome();
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, startWindow, stopWindow);
		ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
	}


	/**
	 * Updates a panel with another one
	 * @param previousPanel panel to update
	 * @param newPanel new panel
	 */
	private void updatePanel(JPanel previousPanel, JPanel newPanel) {
		previousPanel.removeAll();
		previousPanel.add(newPanel);
	}

	/**
	 * @return the variant
	 */
	public Variant getVariant() {
		return currentVariant;
	}

	/**
	 * Looks for the next variant and run the dialog initialization.
	 * @return true if it moves to the next variant, false otherwise
	 */
	protected boolean goToNextVariant() {
		if (iterator.hasNext()) {
			currentVariant = iterator.next().get(0);
			refreshDialog();
			return true;
		}
		return false;
	}

	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant() {
		if (iterator.hasPrevious()) {
			currentVariant = iterator.previous().get(0);
			refreshDialog();
			return true;
		}
		return false;
	}


	/**
	 * Shows the vcf line dialog
	 */
	protected void showVCFLine() {
		vcfLineDialog.show(currentLine);
	}

	/**
	 * @return the search options
	 */
	protected SearchOption getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	protected void setOptions(SearchOption options) {
		this.options = options;
	}


	private String getCurrentGenomeName () {
		if ((currentVariant == null) || (currentVariant instanceof MixVariant)) {
			return null;
		}

		return iterator.getCurrentVariantDisplayList(currentVariant).getGenomeName();
	}
}
