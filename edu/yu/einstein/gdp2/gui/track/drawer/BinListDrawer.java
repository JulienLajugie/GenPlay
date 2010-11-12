/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track.drawer;

import java.awt.Color;
import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.util.ColorConverters;


/**
 * Draws the data of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListDrawer extends CurveDrawer {

	private final BinList binList; // data to draw
	
	
	/**
	 * Creates an instance of {@link BinListDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param genomeWindow {@link GenomeWindow} of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 * @param binList data to draw
	 */
	public BinListDrawer(Graphics graphics, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax, Color trackColor, GraphicsType typeOfGraph, BinList binList) {
		super(graphics, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph);
		this.binList = binList;
	}

	
	@Override
	protected void drawBarGraphics() {
		double[] data = binList.getFittedData(genomeWindow, xRatio);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			// Compute the reverse color
			Color reverseCurveColor = Color.gray;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			// Compute the Y = 0 position 
			int screenY0 = scoreToScreenPos(0);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * xRatio);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int rectHeight = screenYPosition - screenY0;
					if (currentIntensity > 0) {
						graphics.setColor(trackColor);
						graphics.fillRect(screenXPosition, screenYPosition, screenWindowWidth, -rectHeight);
					} else {
						graphics.setColor(reverseCurveColor);
						graphics.fillRect(screenXPosition, screenY0, screenWindowWidth, rectHeight);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}
	}

	
	@Override
	protected void drawCurveGraphics() {
		double[] data = binList.getFittedData(genomeWindow, xRatio);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * xRatio);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				int nextIndex = (currentGenomePosition + windowData) / windowData;
				if ((currentGenomePosition >= 0) && (nextIndex < data.length)){
					double currentIntensity = data[currentIndex];
					double nextIntensity = data[nextIndex];
					//int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX1Position = (int)Math.round((double)(currentGenomePosition - genomeWindow.getStart()) * xRatio);
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
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}
		}
	}

	
	@Override
	protected void drawDenseGraphics() {
		double[] data = binList.getFittedData(genomeWindow, xRatio);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * xRatio);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					graphics.setColor(ColorConverters.scoreToColor(currentIntensity, scoreMin, scoreMax));
					graphics.fillRect(screenXPosition, 0, screenWindowWidth, trackHeight);
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}
	}

	
	@Override
	protected void drawPointGraphics() {
		double[] data = binList.getFittedData(genomeWindow, xRatio);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * xRatio);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX2Position = screenX1Position + screenWindowWidth;
					int screenYPosition = scoreToScreenPos(currentIntensity);				
					graphics.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}	
		}
	}
}
