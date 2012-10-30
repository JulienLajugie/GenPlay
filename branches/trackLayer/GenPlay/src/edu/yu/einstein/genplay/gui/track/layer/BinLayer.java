package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Color;

import edu.yu.einstein.genplay.core.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;

public class BinLayer {

}
/*public class BinLayer implements TrackLayer<BinList> {

	private BinList data;
	private Color color;
	
	
	
	
	
	@Override
	public void drawData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TrackLayerType getType() {
		return TrackLayerType.BIN_LAYER;
	}

	@Override
	public BinList getData() {
		return data;
	}

	
	
	
	@Override
	public void setData(BinList data) {
		this.data = data;		
	}

	@Override
	public void addToTrack() {
		// TODO Auto-generated method stub
		
	}
	
	
	protected void drawBarGraphics() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double[] chromosomeData = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = data.getFittedBinSize();
		if (chromosomeData != null) {
			// Compute the reverse color
			Color reverseCurveColor = Colors.GREY;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			// Compute the Y = 0 position
			int screenY0 = scoreToScreenPos(0);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < chromosomeData.length)){
					double currentIntensity = chromosomeData[currentIndex];
					int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int rectHeight = screenYPosition - screenY0;

					if (currentIntensity > 0) {
						graphics.setColor(trackColor);
						rectHeight *= -1;
					} else {
						graphics.setColor(reverseCurveColor);
						screenYPosition = screenY0;
					}

					if (currentGenomePosition <= currentMinX) {
						int screenWindowWidthTmp = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
						graphics.fillRect(screenXPosition, screenYPosition, screenWindowWidthTmp, rectHeight);
					} else {
						graphics.fillRect(screenXPosition, screenYPosition, screenWindowWidth, rectHeight);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	protected void drawCurveGraphics() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double[] chromosomeData = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = data.getFittedBinSize();
		if (chromosomeData != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				int nextIndex = (currentGenomePosition + windowData) / windowData;
				if ((currentGenomePosition >= 0) && (nextIndex < chromosomeData.length)){
					double currentIntensity = chromosomeData[currentIndex];
					double nextIntensity = chromosomeData[nextIndex];
					//int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX1Position = (int)Math.round((currentGenomePosition - projectWindow.getGenomeWindow().getStart()) * projectWindow.getXFactor());
					int screenX2Position = screenX1Position + screenWindowWidth;
					int screenY1Position = scoreToScreenPos(currentIntensity);
					int screenY2Position = scoreToScreenPos(nextIntensity);
					if ((currentIntensity == 0) && (nextIntensity != 0)) {
						graphics.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity == 0)) {
						graphics.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY1Position);
						graphics.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity != 0)) {
						graphics.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY2Position);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	protected void drawDenseGraphics() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double[] chromosomeData = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = data.getFittedBinSize();
		if (chromosomeData != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < chromosomeData.length)){
					double currentIntensity = chromosomeData[currentIndex];
					int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					graphics.setColor(GenPlayColor.scoreToColor(currentIntensity, scoreMin, scoreMax));
					graphics.fillRect(screenXPosition, 0, screenWindowWidth, trackHeight);
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	protected void drawPointGraphics() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double[] chromosomeData = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = data.getFittedBinSize();
		if (chromosomeData != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < chromosomeData.length)){
					double currentIntensity = chromosomeData[currentIndex];
					int screenX1Position = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					//int screenX2Position = screenX1Position + screenWindowWidth;
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int screenX2Position;
					if (currentGenomePosition <= currentMinX) {
						screenX2Position = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
					} else {
						screenX2Position = screenX1Position + screenWindowWidth;
					}

					graphics.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


}*/
