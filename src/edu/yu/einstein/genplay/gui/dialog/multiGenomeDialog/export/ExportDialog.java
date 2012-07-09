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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportDialog extends JDialog implements ActionListener {

	/** Generated default serial version ID */
	private static final long serialVersionUID = 6935491578227358869L;
	/** Return value when OK has been clicked. */
	public static final 	int 	APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 	CANCEL_OPTION 		= 1;
	/** Width of the dialog */
	public static final int DIALOG_WIDTH = 400;

	private int					approved 				= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private final JTabbedPane 			tabbedPane;
	private final ExportVCFPane 		vcfPane;
	private final ExportBITPane 		bitPane;
	private final ExportBEDPane			bedPane;
	private final ExportValidationPane 	vcfValidationPane;
	private final ExportValidationPane 	bedValidationPane;
	private boolean 					isVCFExport = false;
	private final boolean 				isBITExport = false;
	private boolean 					isBEDExport = false;


	/**
	 * Constructor of {@link ExportDialog}
	 * @param settings settings for the export
	 */
	public ExportDialog (ExportSettings settings) {
		// Create the tabbed pane
		tabbedPane = new JTabbedPane();

		// Create the legend pane
		ExportLegendPane vcfLegendPane = new ExportLegendPane(settings.getVariationMap(), settings.getFileList());
		ExportLegendPane bitLegendPane = new ExportLegendPane(settings.getVariationMap(), settings.getFileList());
		ExportLegendPane bedLegendPane = new ExportLegendPane(settings.getVariationMap(), settings.getFileList());

		// Create the panel to export with VCF options
		vcfPane = new ExportVCFPane();

		// Create the panel to export with 2bit options
		bitPane = new ExportBITPane();

		// Create the panel to export with BED options
		bedPane = new ExportBEDPane(settings.getGenomeNames(), settings.getFileList());

		// Create the validation pane;
		vcfValidationPane = new ExportValidationPane(this);
		//ExportValidationPane bitValidationPane = new ExportValidationPane(this);
		bedValidationPane = new ExportValidationPane(this);

		// Add tabs
		tabbedPane.addTab(ExportVCFPane.TAB_NAME, new ExportPane(vcfLegendPane, vcfPane, vcfValidationPane));
		//tabbedPane.addTab(ExportBITPane.TAB_NAME, new ExportPane(bitLegendPane, bitPane, bitValidationPane));
		tabbedPane.addTab(ExportBITPane.TAB_NAME, new ExportPane(bitLegendPane, bitPane, null));
		tabbedPane.addTab(ExportBEDPane.TAB_NAME, new ExportPane(bedLegendPane, bedPane, bedValidationPane));

		// Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Add components
		add(tabbedPane, BorderLayout.CENTER);

		// Dialog settings
		setTitle("Export track");
		setIconImage(Images.getApplicationImage());
		//setAlwaysOnTop(true);
		setResizable(false);
		setVisible(false);
		pack();
	}



	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModal(true);
		setVisible(true);

		return approved;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton button = (JButton) e.getSource();
			String action = button.getText();
			checkStatus(button);
			if (action.equals(ExportValidationPane.EXPORT_TEXT)) {
				approved = APPROVE_OPTION;
				setVisible(false);
			} else if (action.equals(ExportValidationPane.CANCEL_TEXT)) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		}
	}


	/**
	 * Checks which export is requested
	 */
	private void checkStatus (JButton button) {
		isVCFExport = vcfValidationPane.contains(button);
		isBEDExport = bedValidationPane.contains(button);
	}


	/**
	 * @return true if the export is about VCF files
	 */
	public boolean exportAsVCF () {
		return isVCFExport;
	}


	/**
	 * @return true if the export is about 2Bit files
	 */
	public boolean exportAsBIT () {
		return isBITExport;
	}


	/**
	 * @return true if the export is about BED files
	 */
	public boolean exportAsBED () {
		return isBEDExport;
	}


	/**
	 * @return the path of the selected output
	 */
	public String getOutputPath () {
		if (isVCFExport) {
			return vcfPane.getVCFPath();
		} else if (isBEDExport) {
			return bedPane.getBEDPath();
		}
		return null;
	}


	/**
	 * @return true if the file has to be compressed
	 */
	public boolean compressVCF () {
		return vcfPane.compressVCF();
	}


	/**
	 * @return true if the file has to be indexed
	 */
	public boolean indexVCF () {
		return vcfPane.indexVCF();
	}


	/**
	 * @return the vcfPane
	 */
	public ExportVCFPane getVcfPane() {
		return vcfPane;
	}


	/**
	 * @return the bitPane
	 */
	public ExportBITPane getBitPane() {
		return bitPane;
	}


	/**
	 * @return the bedPane
	 */
	public ExportBEDPane getBedPane() {
		return bedPane;
	}

}
