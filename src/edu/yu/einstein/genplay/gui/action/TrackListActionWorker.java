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
package edu.yu.einstein.genplay.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;
import javax.swing.SwingWorker;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.gui.event.operationProgressEvent.OperationProgressEvent;
import edu.yu.einstein.genplay.gui.event.operationProgressEvent.OperationProgressListener;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.statusBar.StatusBar;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;
import edu.yu.einstein.genplay.gui.trackList.TrackList;



/**
 * Action that starts a SwingWorker so the GUI doesn't freeze
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionWorker<T> extends AbstractAction implements OperationProgressListener, Stoppable {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	private int currentStep = 1;			// current step of the action
	protected SwingWorker<T, Void> worker;	// worker that will process the action
	protected String							genomeName = null;		// genome name for a multi genome project
	protected AlleleType						alleleType = null;		// allele type for a multi genome project
	
	
	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackList().getRootPane();
	}
	
	
	/**
	 * Shortcut for MainFrame.getInstance().getTrackList()
	 * @return the track list of the project
	 */
	protected TrackList getTrackList() {
		return MainFrame.getInstance().getTrackList();
	}
	
	
	/**
	 * Private inner class that extends SwingWorker<T, Void>.
	 * Processes the action.
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class PooledActionWorker extends SwingWorker<T, Void> {
		
		@Override
		final protected T doInBackground() throws Exception {
			OperationPool.getInstance().addOperationProgressListener(TrackListActionWorker.this);
			getTrackList().actionStarts();			
			return processAction();
		}		
		
		@Override
		final protected void done() {
			try {
				garbageCollect();
				getStatusBar().actionStop("Operation Done");
				doAtTheEnd(this.get());
			} catch (Exception e) {
				if (e.getCause() instanceof InterruptedException) {
					getStatusBar().actionStop("Operation Aborted");
				} else {
					getStatusBar().actionStop("Error");
					ExceptionManager.handleException(getTrackList().getRootPane(), e, "An unexpected error occurred during the operation");
				}
			} finally {
				OperationPool.getInstance().removeOperationProgressListener(TrackListActionWorker.this);
				getTrackList().actionEnds();
			}
		}
	}
	
	
	/**
	 * Tries to force the garbage collector to run
	 */
	private void garbageCollect() {
		System.gc();/*System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();*/
	}
	
	
	/**
	 * Public constructor 
	 */
	public TrackListActionWorker() {
		super();
	}
	
	
	/**
	 * @return the status bar of the application
	 */
	private StatusBar getStatusBar() {
		return MainFrame.getInstance().getStatusBar();
	}



	
	@Override
	public final void actionPerformed(ActionEvent arg0) {
		worker = new PooledActionWorker();
		worker.execute();
	}


	@Override
	public void operationProgressChanged(OperationProgressEvent evt) {
		StatusBar statusBar = getStatusBar();
		if (evt.getState() == OperationProgressEvent.STARTING) {
			// when a step start
			statusBar.setProgress(currentStep, 0);
		} else if (evt.getState() == OperationProgressEvent.IN_PROGRESS) {
			// when a step is in progress
			statusBar.setProgress(currentStep, (int)evt.getCompletion());
		} else if (evt.getState() == OperationProgressEvent.COMPLETE) {
			// when a step is done
			statusBar.setProgress(currentStep, 100);
			currentStep++;
		}
	}

	
	/**
	 * Notifies that an action starts
	 * Must be called right before the computation starts
	 * @param description description of the action
	 * @param stepCount number of steps needed to complete the action
	 * @param stoppable must be set to true if the action can be stopped. False otherwise
	 */
	protected void notifyActionStart(String description, int stepCount, boolean stoppable) {
		currentStep = 1;
		if (stoppable) {
			getStatusBar().actionStart(description, stepCount, this);
		} else {
			getStatusBar().actionStart(description, stepCount, null);
		}
	}
	
	
	/**
	 * Notifies the status bar that an action ends.
	 */
	protected void notifyActionStop() {
		getStatusBar().actionStop("Operation Done");
	}
	
	
	@Override
	public void stop() {
		worker.cancel(true);
		OperationPool.getInstance().stopPool();	
		garbageCollect();
		getStatusBar().actionStop("Operation Aborted");
	}

	
	/**
	 * Specifies the action to process
	 * @return the result of the action
	 * @throws Exception
	 */
	protected abstract T processAction() throws Exception;


	/**
	 * Method called at the end of the action.
	 * Can be extended to define the action to do at the end.
	 * @param actionResult result returned by the action method
	 */
	protected void doAtTheEnd(T actionResult) {};
}
