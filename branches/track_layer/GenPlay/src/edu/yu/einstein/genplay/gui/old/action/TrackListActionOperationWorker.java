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
package edu.yu.einstein.genplay.gui.old.action;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Action that starts a {@link Operation} in a thread that can be stopped
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionOperationWorker<T> extends TrackListActionWorker<T> {

	private static final long serialVersionUID = -1626148358656459751L; // generated ID
	protected Operation<T> operation;// operation to be processed
	
	
	/**
	 * Public constructor 
	 */
	public TrackListActionOperationWorker() {
		super();
	}

	
	/**
	 * Initializes the Operation
	 * @return an initialized Operation or null if the user canceled
	 * @throws Exception
	 */
	public abstract Operation<T> initializeOperation() throws Exception;

	
	@Override
	protected T processAction() throws Exception {
		operation = initializeOperation();
		if (operation != null) {
			notifyActionStart(operation.getProcessingDescription(), operation.getStepCount(), true);
			return operation.compute();
		} else {
			return null;
		}
	}
	
	
	/**
	 * Override that stops the extractor
	 */
	@Override
	public void stop() {
		if ((operation != null) && (operation instanceof Stoppable)) {
			((Stoppable) operation).stop();
		}
		super.stop();
	}
}
