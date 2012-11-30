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
package edu.yu.einstein.genplay.gui.track;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.gui.track.layer.ScoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * This class handles the scores showed in the track
 * @author Julien Lajugie
 */
public class TrackScore implements Serializable {

	private static final long serialVersionUID = -2515234024119807964L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private boolean			isTrackScored;			// true if the track contains a list on scored layer, false otherwise
	private double 			minimumScore;			// minimum score displayed in the track
	private double		 	maximumScore;			// maximum score displayed in the track
	private double 			currentScore;			// score displayed at the center of the track
	private boolean 		isScoreAxisAutorescaled;// true if the score axis needs to be auto rescaled
	private Track 	track;					// track displaying this scores


	/**
	 * Creates an instance of {@link TrackScore} 
	 * @param track track displaying this score
	 */
	public TrackScore(Track track) {
		setTrackScored(false);
		setMinimumScore(0);
		setMaximumScore(0);
		setCurrentScore(0);
		setScoreAxisAutorescaled(true);
		setTrack(track);
	}


	/**
	 * @return the score displayed in the middle of the track
	 */
	public double getCurrentScore() {
		return currentScore;
	}


	/**
	 * @return the maximum score displayed in the track
	 */
	public double getMaximumScore() {
		return maximumScore;
	}


	/**
	 * @return the minimum score displayed in the track
	 */
	public double getMinimumScore() {
		return minimumScore;
	}


	/**
	 * @return the track that displays this score
	 */
	public Track getTrack() {
		return track;
	}


	/**
	 * @return the ratio between the score range and the track height (in pixel)
	 */
	public double getYRatio() {
		double scoreRange = maximumScore - minimumScore;
		return scoreRange / (double)track.getHeight();
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		setTrackScored(in.readBoolean());
		setMinimumScore(in.readDouble());
		setMaximumScore(in.readDouble());
		setCurrentScore(in.readDouble());
		setScoreAxisAutorescaled(in.readBoolean());
		setTrack((Track)in.readObject());
	}


	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	public int scoreToScreenPosition(double score) {
		int trackHeight = getTrack().getHeight();
		if (score < getMinimumScore()) {
			return trackHeight;
		} else if (score > getMaximumScore()) {
			return 0;
		} else {
			return (trackHeight - (int)Math.round((double)(score - getMinimumScore()) * getYRatio()));
		}
	}


	/**
	 * Sets the score displayed in the middle of the track.
	 * This function is not public. External classes need to use 
	 * the updateCurrentScore method instead.
	 * @param currentScore the score to set
	 */
	private void setCurrentScore(double currentScore) {
		this.currentScore = currentScore;
	}


	/**
	 * Sets the maximum score displayed in the track
	 * @param maximumScore the maximum displayed score to set
	 */
	public void setMaximumScore(double maximumScore) {
		this.maximumScore = maximumScore;
	}


	/**
	 * Sets the minimum score displayed in the track
	 * @param minimumScore the minimum displayed score to set
	 */
	public void setMinimumScore(double minimumScore) {
		this.minimumScore = minimumScore;
	}


	/**
	 * The track that displays this score
	 * @param track the track to set
	 */
	public void setTrack(Track track) {
		this.track = track;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeBoolean(isTrackScored());
		out.writeDouble(getMinimumScore());
		out.writeDouble(getMaximumScore());
		out.writeDouble(getCurrentScore());
		out.writeBoolean(isScoreAxisAutorescaled());
		out.writeObject(getTrack());
	}


	/**
	 * @return true if the score axis of the track will be 
	 * automatically rescaled after an operation, false otherwise
	 */
	public boolean isScoreAxisAutorescaled() {
		return isScoreAxisAutorescaled;
	}


	/**
	 * @param isScoreAxisAutorescaled set to true in order to automatically rescale 
	 * a track after an operation
	 */
	public void setScoreAxisAutorescaled(boolean isScoreAxisAutorescaled) {
		this.isScoreAxisAutorescaled = isScoreAxisAutorescaled;
	}


	/**
	 * Auto rescales the score axis of the track if the autorescale mode is on
	 */
	public void autorescaleScoreAxis() {
		if (isScoreAxisAutorescaled()) {
			List<Double> minimumScores = new ArrayList<Double>();
			List<Double> maximumScores = new ArrayList<Double>();
			for (Layer<?> currentLayer: getTrack().getLayers()) {
				if (currentLayer instanceof ScoredLayer) {
					// for each scoredLayer of the track we save the minimum and maximum value to display
					ScoredLayer scoredLayer = (ScoredLayer)currentLayer;
					minimumScores.add(scoredLayer.getMinimumScoreToDisplay());
					maximumScores.add(scoredLayer.getMaximumScoreToDisplay());
				}
			}
			if (!minimumScores.isEmpty()) {
				// the minimum score displayed in the track is the minimum of the ScoredLayer minimums
				setMinimumScore(Collections.min(minimumScores));
				// we do the opposite for the maximum
				setMaximumScore(Collections.max(maximumScores));
				getTrack().repaint();
			}
		}
	}


	/**
	 * Updates the current score displayed (the score displayed at the middle of the track).
	 * The current score is the one of the active layer.
	 */
	public void updateCurrentScore() {
		if (isTrackScored()) {
			if (getTrack().getActiveLayer() instanceof ScoredLayer) {
				ScoredLayer activeScoredLayer = (ScoredLayer)getTrack().getActiveLayer();
				setCurrentScore(activeScoredLayer.getCurrentScoreToDisplay());
			}
		}
	}


	/**
	 * @return the isTrackScored
	 */
	public boolean isTrackScored() {
		return isTrackScored;
	}


	/**
	 * Sets if the track is scored (ie. if the track has at least one {@link ScoredLayer}).
	 * This method is private, external classes need to use updateTrackScored instead
	 * @param isTrackScored set to true if the track is scored, false otherwise
	 */
	private void setTrackScored(boolean isTrackScored) {
		this.isTrackScored = isTrackScored;
	}

}
