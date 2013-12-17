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
package edu.yu.einstein.genplay.gui.action.actionWaiting;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.SwingWorker;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SwingWorkerActionWaiting extends SwingWorker<Void, Boolean> {

	private final ActionWaiter actionWaiter;
	private final AbstractAction actionPerformer;
	private final JDialog dialog;


	/**
	 * Constructor of {@link SwingWorkerActionWaiting}
	 * 
	 * @param actionWaiter		the source calling the action
	 * @param actionPerformer	the action to perform
	 */
	public SwingWorkerActionWaiting (ActionWaiter actionWaiter, AbstractAction actionPerformer) {
		this.actionWaiter = actionWaiter;
		this.actionPerformer = actionPerformer;
		this.dialog = new DefaultWaitingDialog();
	}


	/**
	 * Constructor of {@link SwingWorkerActionWaiting}
	 * 
	 * @param actionWaiter		the source calling the action
	 * @param actionPerformer	the action to perform
	 * @param dialog			a dialog to show while the action is processing
	 */
	public SwingWorkerActionWaiting (ActionWaiter actionWaiter, AbstractAction actionPerformer, JDialog dialog) {
		this.actionWaiter = actionWaiter;
		this.actionPerformer = actionPerformer;
		this.dialog = dialog;
	}


	@Override
	protected Void doInBackground() throws Exception {
		publish(true);
		actionPerformer.actionPerformed(null);
		return null;
	}


	@Override
	protected void process(List<Boolean> chunks) {
		dialog.setVisible(true);
	}


	@Override
	protected void done() {
		dialog.setVisible(false);
		dialog.dispose();
		actionWaiter.doAtTheEnd();
	}
}
