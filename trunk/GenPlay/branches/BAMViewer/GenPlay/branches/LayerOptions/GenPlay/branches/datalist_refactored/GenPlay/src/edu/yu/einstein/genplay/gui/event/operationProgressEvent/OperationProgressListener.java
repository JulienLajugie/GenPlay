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
package edu.yu.einstein.genplay.gui.event.operationProgressEvent;


/**
 * The listener interface for receiving {@link OperationProgressEvent}.
 * The class that is interested in processing a {@link OperationProgressEvent} implements this interface.
 * The listener object created from that class is then registered with a component using the component's addOperationProgressListener method. 
 * A {@link OperationProgressEvent} is generated when the progress state of an operation changes.  
 * @author Julien Lajugie
 * @version 0.1
 */
public interface OperationProgressListener {

	
	/**
	 * Invoked when the progress state of an operation changes
	 * @param evt {@link OperationProgressEvent}
	 */
	public abstract void operationProgressChanged(OperationProgressEvent evt);
}
