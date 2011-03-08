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
package yu.einstein.gdp2.gui.action.SNPListTrack;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.SNPList.operation.SLOFindNext;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Finds the next SNP from a specified position 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLAFindNext extends TrackListActionOperationWorker<SNP> {

	private static final long serialVersionUID = -5784510426787285411L;	// generated ID
	private static final String 	ACTION_NAME = "Find Next SNP";		// action name
	private static final String 	DESCRIPTION = 
		"Search the next SNP on the current track";						// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SLAFindNext";


	/**
	 * Creates an instance of {@link SLAFindNext}
	 */
	public SLAFindNext() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}

	
	@Override
	public Operation<SNP> initializeOperation() throws Exception {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if ((selectedTrack != null) && (selectedTrack.getData() instanceof SNPList)) {
			SNPList inputList = (SNPList) selectedTrack.getData();
			GenomeWindow currentWindow = selectedTrack.getGenomeWindow();
			Operation<SNP> operation = new SLOFindNext(inputList, currentWindow.getChromosome(), (int) currentWindow.getMiddlePosition());
			return operation;
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(SNP actionResult) {
		if (actionResult != null) {
			GenomeWindow currentWindow = getTrackList().getGenomeWindow();
			Chromosome currentChromosome = currentWindow.getChromosome();
			int currentLength = currentWindow.getSize();
			int newStart = actionResult.getPosition() - currentLength / 2;
			int newStop = newStart + currentLength;
			GenomeWindow newGenomeWindow = new GenomeWindow(currentChromosome, newStart, newStop);
			getTrackList().setGenomeWindow(newGenomeWindow);			
		}		
	}
}
