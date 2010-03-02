/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.util.ZoomManager;

/**
* A {@link TrackGraphics} part of a {@link SCWListTrack}
* @author Julien Lajugie
* @version 0.1
*/
public final class SCWListTrackGraphics extends CurveTrackGraphics {

	private static final long serialVersionUID = -996344743923414353L; // generated ID
	private final ScoredChromosomeWindowList data; // data showed


	/**
	 * Creates an instance of a {@link SCWListTrackGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data displayed {@link ScoredChromosomeWindowList} 
	 */
	protected SCWListTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, ScoredChromosomeWindowList data) {
		super(zoomManager, displayedGenomeWindow, data.minScoreToDisplay(), data.maxScoreToDisplay());
		this.data = data;
		// we don't want the max equals to the min
		if (yMin == yMax) {
			if (yMin > 0) {
				setYMin(0);
			} else if (yMax < 0) {
				setYMax(0);
			}
		}
	}


	@Override
	protected void drawBarGraphics(Graphics g) {
		if (data != null) {
			int screenY0 = scoreToScreenPos(0);
			Color reverseCurveColor = Color.gray;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}		
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xFactor);
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
						g.setColor(trackColor);
						g.fillRect(x, y, widthWindow, -rectHeight);
					} else {
						g.setColor(reverseCurveColor);
						g.fillRect(x, screenY0, widthWindow, rectHeight);
					}
				}
			}		
		}
	}


	@Override
	protected void drawCurveGraphics(Graphics g) {
		if (data != null) {
			g.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xFactor);
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
							g.drawLine(x2, y1, x2, y2);
						} else if ((score1 != 0) && (score2 == 0)) {
							g.drawLine(x1, y1, x2, y1);
							g.drawLine(x2, y1, x2, y2);					
						} else if ((score1 != 0) && (score2 != 0)) {
							g.drawLine(x1, y1, x2, y2);
						}						
					}
					x1 = x2;
					score1 = score2;
					y1 = y2;					
				}
				if (x1 != -1) {
					if ((score1 == 0) && (score2 != 0)) {
						g.drawLine(x2, y1, x2, y2);
					} else if ((score1 != 0) && (score2 == 0)) {
						g.drawLine(x1, y1, x2, y1);
						g.drawLine(x2, y1, x2, y2);					
					} else if ((score1 != 0) && (score2 != 0)) {
						g.drawLine(x1, y1, x2, y2);
					}						
				}				
			}
		}					
	}


	@Override
	protected void drawDenseGraphics(Graphics g) {
		if (data != null) {
			int height = getHeight();			
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xFactor);
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = genomePosToScreenPos(currentWindow.getStart()); 
					int widthWindow = genomePosToScreenPos(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					g.setColor(scoreToColor(currentWindow.getScore(), yMin, yMax));
					g.fillRect(x, 0, widthWindow, height);
				}
			}		
		}
	}


	@Override
	protected void drawPointGraphics(Graphics g) {
		if (data != null) {
			g.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(genomeWindow, xFactor);
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x1 = genomePosToScreenPos(currentWindow.getStart()); 
					int x2 = genomePosToScreenPos(currentWindow.getStop());
					if (x2 - x1 < 1) {
						x2 = x1 + 1;
					}
					int y = scoreToScreenPos(currentWindow.getScore());					
					g.drawLine(x1, y, x2, y);
				}
			}		
		}	
	}


	@Override
	protected void drawScore(Graphics g) {
		double middleScore = 0;
		g.setColor(Color.red);
		double middlePosition = genomeWindow.getMiddlePosition();
		List<ScoredChromosomeWindow> list = data.getFittedData(genomeWindow, xFactor);
		if ((list != null) && (list.size() > 0)) {
			int i = 0;
			List<Double> values = new ArrayList<Double>();
			// we add the score of every window that exists on middlePosition
			while ((i < list.size()) && (list.get(i).getStart() < middlePosition)) {
				if (list.get(i).getStop() >= middlePosition) {
					values.add(list.get(i).getScore());
				}
				i++;
			}
			// we want to print the greatest score of the windows in the middle
			if (values.size() > 0) {
				middleScore = Collections.max(values);
			}			
		}
		g.drawString("y=" + SCORE_FORMAT.format(middleScore), getWidth() / 2 + 3, getHeight() - 2);	
	}


	@Override
	protected void yFactorChanged() {
		repaint();		
	}
}
