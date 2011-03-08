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
package yu.einstein.gdp2.core.operation;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Operation on a {@link BinList}
 * T: result type of the operation
 * @author Julien Lajugie
 * @version 0.1
 */
public interface Operation<T> extends Stoppable {
	
	
	/**
	 * @return a description of the operation
	 */
	public String getDescription();
	
	
	/**
	 * @return a description of what is done during the process
	 */
	public String getProcessingDescription();
	
	
	/**
	 * Processes the operation
	 * @return the result of the operation
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public T compute() throws Exception;
	
	
	/**
	 * @return the number of steps needed to complete the operation 
	 */
	public int getStepCount();
}
