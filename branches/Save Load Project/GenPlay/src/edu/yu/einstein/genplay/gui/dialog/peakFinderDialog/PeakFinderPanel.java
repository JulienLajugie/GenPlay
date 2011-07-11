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
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.operation.Operation;


/**
 * Defines the common methods of the different peak finder panels
 * @author Julien Lajugie
 * @version 0.1
 */
public interface PeakFinderPanel {
	
	/**
	 * Checks if the input are valid. Notifies the user if not.
	 * @return the Operation with the parameters set by the user. Null if the input are not valid
	 */
	public Operation<BinList[]> validateInput();
	
	
	/**
	 * Saves the input so it can be restored next time the dialog is open  	
	 */
	public void saveInput();
}
