/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.event.operationProgressEvent;

/**
 * This event is emitted when the progress state of an operation changes.
 * @author Julien Lajugie
 */
public class OperationProgressEvent {

	/**
	 * the operation is starting
	 */
	public static int STARTING = 0;

	/**
	 * the operation is in progress
	 */
	public static int IN_PROGRESS = 1;

	/**
	 * the operation was successfully completed
	 */
	public static int COMPLETE = 2;

	/**
	 * the operation was aborted
	 */
	public static int ABORT = 3;


	private final int 		state;		// state of the progress (STARTING, IN_PROGRESS...)
	private final double 	completion;	// % of completion when the state is IN_PROGRESS


	/**
	 * Creates an instance if {@link OperationProgressEvent}
	 * @param state state of the progress
	 * @param completion % of completion if the state is in progress
	 */
	public OperationProgressEvent(int state, double completion) {
		this.state = state;
		this.completion = completion;
	}


	/**
	 * @return the completion
	 */
	public double getCompletion() {
		return completion;
	}


	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
}
