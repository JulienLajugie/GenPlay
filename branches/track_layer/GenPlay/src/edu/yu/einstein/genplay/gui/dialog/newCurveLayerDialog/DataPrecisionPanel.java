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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.DataPrecision;



/**
 * Panel for the data precision input of a {@link NewCurveLayerDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class DataPrecisionPanel extends JPanel {

	private static final long serialVersionUID = -2255804422921021285L; 				// generated ID
	private final JComboBox 		jcbDataPrecision; 									// combo box for the data precision 
	private static DataPrecision 	defaultPrecision = DataPrecision.PRECISION_32BIT; 	// default data precision
	
	
	/**
	 * Creates an instance of {@link DataPrecisionPanel}
	 */
	DataPrecisionPanel() {
		super();
		jcbDataPrecision = new JComboBox(DataPrecision.values());
		jcbDataPrecision.setSelectedItem(defaultPrecision);
		add(jcbDataPrecision);
		setBorder(BorderFactory.createTitledBorder("Data Precision"));
	}
	
	
	/**
	 * @return the selected {@link DataPrecision}
	 */
	DataPrecision getDataPrecision() {
		return (DataPrecision) jcbDataPrecision.getSelectedItem();
	}
	
	
	/**
	 * Saves the selected data precision as default
	 */
	void saveDefault() {
		defaultPrecision = getDataPrecision();
	}
}
