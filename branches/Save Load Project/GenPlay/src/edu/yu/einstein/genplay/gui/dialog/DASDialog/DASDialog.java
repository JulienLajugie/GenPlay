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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.DASDialog;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;


/**
 * A dialog box for choosing data to download from a DAS server
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASDialog extends JDialog {

	private static final long serialVersionUID = 4995384388348077375L;	// generated ID
	private final static Dimension WINDOW_SIZE = 
		new Dimension(600, 360);						// size of the window
	private final static int MARGIN = 5;				// margin between the components in the window
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 0;
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 1;	
	/**
	 * Generate a Gene List Option
	 */
	public static final int GENERATE_GENE_LIST = 0;
	/**
	 * Generate a Scored Chromosome Window Option
	 */
	public static final int GENERATE_SCW_LIST = 1;
	/**
	 * Generate the Genome Wide List
	 */
	public static final int GENERATE_GENOMEWIDE_LIST = 0;
	/**
	 * Generate the current value Option
	 */
	public static final int GENERATE_CURRENT_LIST = 1;
	/**
	 * Generate the user specified Option
	 */
	public static final int GENERATE_USER_SPECIFIED_LIST = 2;
	
	private final DataSelectionPanel 		dataSelectionPanel;			// panel selection of the data
	private final GenerateTrackTypePanel 	generateTrackTypePanel;		// panel selection of the generated track type 
	private final DataRangePanel 			dataRangePanel;				// panel selection of the data range
	private final JButton 					jbCancel;					// cancel button
	private final JButton 					jbOk;						// ok button
	private int								approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not 

	
	/**
	 * Creates an instance of {@link DASDialog}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DASDialog() throws ParserConfigurationException, SAXException, IOException {
		super();
		
		// create the panels
		dataSelectionPanel = new DataSelectionPanel();
		generateTrackTypePanel = new GenerateTrackTypePanel();
		dataRangePanel = new DataRangePanel();
		
		// create the cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
			}
		});
		
		// create the ok button
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkClicked();
			}
		});

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;		
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		add(dataSelectionPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.insets = new Insets(MARGIN, 0, MARGIN, 0);
		add(generateTrackTypePanel, c);
		
		c.gridx = 0;		
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.insets = new Insets(0, 0, 0, 0);
		add(dataRangePanel, c);
		
		c.gridx = 0;		
		c.gridy = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c.gridx = 1;		
		c.gridy = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);

		setTitle("Retrieve DAS Data");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
		setPreferredSize(WINDOW_SIZE);
		setMinimumSize(WINDOW_SIZE);
		setModal(true);		
	}

	
	/**
	 * @return the selected DasConnector
	 */
	public final DASConnector getSelectedDasConnector() {
		return dataSelectionPanel.getSelectedDasConnector();
	}


	/**
	 * @return the selected DasType
	 */
	public final DASType getSelectedDasType() {
		return dataSelectionPanel.getSelectedDasType();
	}


	/**
	 * @return the selected DataSource
	 */
	public final DataSource getSelectedDataSource() {
		return dataSelectionPanel.getSelectedDataSource();
	}
	
	
	/**
	 * @return generateType (GENERATE_GENE_LIST or GENERATE_SCW_LIST)
	 */
	public final int getGenerateType() {
		return generateTrackTypePanel.getGenerateType();
	}

	
	/**
	 * @return dataRange(GENERATE_GENOMEWIDE_LIST or GENERATE_CURRENT_LIST or GENERATE_USER_SPECIFIED_LIST)
	 */
	public final int getDataRange() {
		return dataRangePanel.getDataRange();
	}

		
	/**
	 * @return the selected Chromosome
	 */
	public final GenomeWindow getUserSpecifiedGenomeWindow() {
		return dataRangePanel.getUserSpecifiedGenomeWindow();
	}


	/**
	 * Method called when the button okay is clicked
	 */
	protected void jbOkClicked() {
		approved = APPROVE_OPTION;
		setVisible(false);
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return GENERATE_OPTION if Generate is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDASDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}