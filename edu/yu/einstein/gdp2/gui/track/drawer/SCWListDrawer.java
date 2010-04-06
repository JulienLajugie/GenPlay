/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.gui.track.SCWListTrackGraphics;
import yu.einstein.gdp2.util.Utils;


/**
 * Draws the data of a {@link SCWListTrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListDrawer extends CurveDrawer {

	private final ScoredChromosomeWindowList data; // data to draw

	
	/**
	 * Creates an instance of {@link SCWListDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param genomeWindow {@link GenomeWindow} of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 * @param data data to draw
	 */
	public SCWListDrawer(Graphics graphics, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax, Color trackColor, GraphicsType typeOfGraph, ScoredChromosomeWindowList data) {
		super(graphics, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph);
		this.data = data;
	}

	
	@Override
	protected void drawBarGraphics() {
		if (data != null) {
			int screenY0 = scoreToScreenPos(0);
			Color reverseCurveColor = Color.gray;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}		
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xRatio);
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = genomePosToScreenPos(currentWindow.getStart()); 
					int widthWindow = genomePosToScreenPos(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					int y = scoreToScreenPos(currentWindow.getScore());
					int rectHeight = y - screenY0;
					if (currentWindow.getScore() > 0) {
						graphics.setColor(trackColor);
						graphics.fillRect(x, y, widthWindow, -rectHeight);
					} else {
						graphics.setColor(reverseCurveColor);
						graphics.fillRect(x, screenY0, widthWindow, rectHeight);
					}
				}
			}		
		}
	}

	
	@Override
	protected void drawCurveGraphics() {
		if (data != null) {
			graphics.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xRatio);
			if ((listToPrint != null) && (listToPrint.size() > 0)) {
				int x1 = -1; 
				int x2 = -1;
				double score1 = -1;
				int y1 = -1;
				double score2 = -1;
				int y2 = -1;
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					x2 = genomePosToScreenPos(currentWindow.getStart());
					score2 = currentWindow.getScore();
					y2 = scoreToScreenPos(score2);
					if (x1 != -1) {
						if ((score1 == 0) && (score2 != 0)) {
							graphics.drawLine(x2, y1, x2, y2);
						} else if ((score1 != 0) && (score2 == 0)) {
							graphics.drawLine(x1, y1, x2, y1);
							graphics.drawLine(x2, y1, x2, y2);					
						} else if ((score1 != 0) && (score2 != 0)) {
							graphics.drawLine(x1, y1, x2, y2);
						}						
					}
					x1 = x2;
					score1 = score2;
					y1 = y2;					
				}
				if (x1 != -1) {
					if ((score1 == 0) && (score2 != 0)) {
						graphics.drawLine(x2, y1, x2, y2);
					} else if ((score1 != 0) && (score2 == 0)) {
						graphics.drawLine(x1, y1, x2, y1);
						graphics.drawLine(x2, y1, x2, y2);					
					} else if ((score1 != 0) && (score2 != 0)) {
						graphics.drawLine(x1, y1, x2, y2);
					}						
				}				
			}
		}			
	}
	

	@Override
	protected void drawDenseGraphics() {
		if (data != null) {
			//int height = getHeight();			
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xRatio);
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = genomePosToScreenPos(currentWindow.getStart()); 
					int widthWindow = genomePosToScreenPos(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					graphics.setColor(Utils.scoreToColor(currentWindow.getScore(), scoreMin, scoreMax));
					graphics.fillRect(x, 0, widthWindow, trackHeight);
				}
			}		
		}
	}
	

	@Override
	protected void drawPointGraphics() {
		if (data != null) {
			graphics.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xRatio);
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x1 = genomePosToScreenPos(currentWindow.getStart()); 
					int x2 = genomePosToScreenPos(currentWindow.getStop());
					if (x2 - x1 < 1) {
						x2 = x1 + 1;
					}
					int y = scoreToScreenPos(currentWindow.getScore());					
					graphics.drawLine(x1, y, x2, y);
				}
			}		
		}	
	}
}
