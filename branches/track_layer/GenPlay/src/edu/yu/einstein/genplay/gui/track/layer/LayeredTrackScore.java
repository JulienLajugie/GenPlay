package edu.yu.einstein.genplay.gui.track.layer;

public class LayeredTrackScore {
	private double minimumDisplayedScore;
	private double maximumDisplayedScore;
	private double currentScore;
	private double yRatio;
	
	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score, int height) {
		if (score < minimumDisplayedScore) {
			return height;
		} else if (score > maximumDisplayedScore) {
			return 0;
		} else {
			return (height - (int)Math.round((double)(score - minimumDisplayedScore) * yRatio));
		}
	}


	/**
	 * @param score a double value
	 * @return the value on the screen
	 */
	protected int scoreToScreenPos(double score) {
		if (score < minimumDisplayedScore) {
			return trackHeight;
		} else if (score > maximumDisplayedScore) {
			return 0;
		} else {
			return (trackHeight - (int)Math.round((double)(score - scoreMin) * yRatio));
		}
	}
}
