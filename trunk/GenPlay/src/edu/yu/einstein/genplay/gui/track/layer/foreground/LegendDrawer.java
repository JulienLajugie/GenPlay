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
package edu.yu.einstein.genplay.gui.track.layer.foreground;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.VariantLayerDisplaySettings;
import edu.yu.einstein.genplay.gui.track.Drawer;
import edu.yu.einstein.genplay.gui.track.GraphicsPanel;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class LegendDrawer implements Drawer, MouseListener, MouseMotionListener {

	/**
	 * This class processes the opening & the closing of the legend
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class Rolling extends Thread {

		/** Option to open the legend */
		public static final int OPEN = 1;
		/** Option to close the legend */
		public static final int CLOSE = 0;
		/** The lower, the faster! */
		private static final int SPEED = 1;

		private final int motion; // Option to open or close the legend


		/**
		 * Constructor of {@link Rolling}
		 * @param option to open or close the legend
		 */
		public Rolling (int option) {
			motion = option;
		}


		/**
		 * Close the legend
		 */
		private void close () {
			while (legendWidth >= 0) {
				legendWidth--;
				repaint();
			}
		}


		/**
		 * Open the legend
		 */
		private void open () {
			while (legendWidth <= originalLegendWidth) {
				legendWidth++;
				repaint();
			}
		}


		/**
		 * Repaint the legend
		 */
		private void repaint () {
			parent.getTrack().getGraphicsPanel().repaint();
			try {
				Thread.sleep(SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		@Override
		public void run() {
			if (motion == OPEN) {
				open();
				isVisible = true;
			} else if (motion == CLOSE) {
				isVisible = false;
				close();
			}
		}

	}
	private final Layer<?> parent;
	private final int widthOffset = 2; 				// Space between the border of the rectangle and the text.
	private final int heightOffset = 2; 			// Space between the border of the rectangle and the top of the track.
	private final int transparency = 240;			// Transparency of the legend and the roller.
	private final int rollerWidth = 7;				// Width of the roller.

	private final int rollerHeight = 15;			// Height of the roller.
	private Graphics graphic;						// The track graphic.
	private Graphics gRoller;						// The graphic of the roller.
	private Graphics gLegend;						// The graphic of the legend.
	private String trackName;						// The track name
	private Layer<?>[] layers;						// The layers of the track.
	private Layer<?> activeLayer;					// The active layer of the track.
	private int trackWidth;							// Width of the track.
	private int originalLegendWidth;				// Original width of the legend.
	private int legendWidth;						// Width of the legend.

	private int legendHeight;						// Height of the legend.


	private boolean isVisible;						// If the legend is visible or not.
	private boolean isCursorOverLegend;				// true if the cursor is over the legend


	/**
	 * Constructor of {@link LegendDrawer}
	 * @param parent the parent layer
	 */
	public LegendDrawer (Layer<?> parent) {
		this.parent = parent;
		isVisible = true;
		registerListener(parent.getTrack().getGraphicsPanel());
	}


	/**
	 * Close the legend
	 */
	private void closeLegend () {
		Thread rolling = new Rolling(Rolling.CLOSE);
		rolling.start();
		parent.getTrack().updateGraphicCursor();
	}


	/**
	 * Draw the legend and the roller button
	 * @param g
	 */
	public void draw (Graphics g) {
		graphic = g;
		// Get the right graphics
		gRoller = getTopRightRollerGraphic();
		gLegend = getTopRightLegendGraphic();

		// Draw the legend elements
		drawLegendArea();
		drawTrackName();
		drawLayersName();

		// Draw the roller
		drawRoller();
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		g.setFont(TrackConstants.FONT_LEGEND);
		initialize(g, width);
		draw(g);
	}


	/**
	 * Draw the name of the layers
	 */
	private void drawLayersName () {
		// Set the metrics
		FontMetrics fm = gLegend.getFontMetrics();
		int y = fm.getHeight();									// height of the text
		if (trackName != null) {
			y *= 2;
		}

		// Draw the layers
		for (Layer<?> layer: layers) {
			if (layer.getName() != null){
				// Set the current font
				if ((activeLayer != null) && layer.equals(activeLayer)) {
					gLegend.setFont(TrackConstants.FONT_LEGEND_ACTIVE_LAYER);
				} else {
					gLegend.setFont(TrackConstants.FONT_LEGEND);
				}

				// Set the current color
				if (layer instanceof ColoredLayer) {
					gLegend.setColor(Colors.removeTransparency(((ColoredLayer)layer).getColor()));
				} else {
					gLegend.setColor(Colors.BLACK);
				}

				// Draw the current layer name
				if (layer instanceof VariantLayer) {
					drawVariantLayerName((VariantLayer) layer, y);
				} else {
					gLegend.drawString(layer.getName(), widthOffset, y);
				}

				// Update the metrics
				y += fm.getHeight();
			}
		}
	}


	/**
	 * Draw the legend area elements (borders and rectangle content).
	 */
	private void drawLegendArea () {
		Color rect = Colors.addTransparency(Colors.TRACK_BACKGROUND, transparency);
		gLegend.setColor(rect);
		gLegend.fillRect(0, 0, gLegend.getClipBounds().width, gLegend.getClipBounds().height - 1);
		gLegend.setColor(Colors.BLACK);
		gLegend.drawRect(0, 0, gLegend.getClipBounds().width, gLegend.getClipBounds().height - 1);
	}


	/**
	 * Draw the roller graphic
	 */
	private void drawRoller () {
		// Set metrics
		int backgroundHeight = gRoller.getClipBounds().height;
		int backgroundWidth = gRoller.getClipBounds().width;
		int dotDiameter = 3;
		int dotNumber = 3;
		int dotOffset = (backgroundHeight / dotNumber) - 1;
		int dotX = (backgroundWidth - dotDiameter) / 2;

		// Draw background
		Color backgroundColor = Colors.addTransparency(Colors.LIGHT_GREY, transparency);
		gRoller.setColor(backgroundColor);
		gRoller.fillRect(0, 0, backgroundWidth, backgroundHeight);

		// Draw dots
		gRoller.setColor(Colors.BLACK);
		int currentDotY = 2;
		for (int i = 0; i < dotNumber; i++) {
			gRoller.fillOval(dotX, currentDotY, dotDiameter, dotDiameter);
			currentDotY += dotOffset;
		}
	}


	/**
	 * Draw the name of the track
	 */
	private void drawTrackName () {
		if (trackName != null) {
			FontMetrics fm = gLegend.getFontMetrics();
			int textHeight = fm.getHeight();									// height of the text

			// Draw the track name
			gLegend.drawString(trackName, widthOffset, textHeight);
		}
	}


	/**
	 * Draw a {@link VariantLayer} name
	 * @param layer
	 * @param y
	 */
	private void drawVariantLayerName (VariantLayer layer, int y) {
		if (layer.getName() != null) {
			VariantLayerDisplaySettings data = layer.getData();
			if (data != null) {
				int x = widthOffset;
				String s = data.getGenome() + " (";
				x = printString(s,  x, y, Colors.BLACK);
				for (int i = 0; i < data.getVariationTypeList().size(); i++) {
					VariantType type = data.getVariationTypeList().get(i);
					if (type == VariantType.INSERTION) {
						s = "I";
					} else if (type == VariantType.DELETION) {
						s = "D";
					} else if (type == VariantType.SNPS) {
						s = "SNPs";
					}
					x = printString(s,  x, y, data.getColorList().get(i));
					if (i < (data.getVariationTypeList().size() - 1)) {
						x = printString(", ",  x, y, Colors.BLACK);
					}
				}
				printString(")", x, y, Colors.BLACK);
			}
		}
	}



	/**
	 * @return the height of the legend area
	 */
	private int getHeight () {
		int lines = 0;

		if (trackName != null) {
			lines++;
		}

		lines += layers.length;

		FontMetrics fm = graphic.getFontMetrics();
		int height = (lines * fm.getHeight()) + (heightOffset * 2);

		return height;
	}


	/**
	 * @return the X position where the legend starts on the track
	 */
	private int getLegendX () {
		return trackWidth - legendWidth;
	}


	/**
	 * @return the longest name in this graphic between the track name and the layers
	 */
	private int getMaxWidth () {
		FontMetrics fm = graphic.getFontMetrics(TrackConstants.FONT_LEGEND_ACTIVE_LAYER);
		int width = 0;

		if (trackName != null) {
			width = fm.stringWidth(trackName);
		}

		for (Layer<?> layer: layers) {
			if (layer.getName() != null) {
				width = Math.max(width, fm.stringWidth(layer.getName()));
			}
		}

		width += (widthOffset * 2);

		return width;
	}


	/**
	 * @return the Y position where the roller starts on the track
	 */
	private int getRollerX () {
		return getLegendX() - rollerWidth;
	}


	/**
	 * @return the graphic matching the top right corner of the given graphic to draw the legend
	 */
	private Graphics getTopRightLegendGraphic () {
		return graphic.create(getLegendX(), 1, legendWidth, legendHeight);
	}


	/**
	 * @return the graphic matching the top right corner of the given graphic to draw the button
	 */
	private Graphics getTopRightRollerGraphic () {
		return graphic.create(getRollerX(), 1, rollerWidth, rollerHeight);
	}


	/**
	 * Initialize all the drawing attributes
	 * @param g
	 * @param width
	 */
	private void initialize (Graphics g, int width) {
		graphic = g;
		trackWidth = width;

		// Set names
		trackName = null;
		activeLayer = null;
		if ((parent.getTrack().getName() != null) && (!parent.getTrack().getName().trim().isEmpty())) {
			trackName = parent.getTrack().getName();
			activeLayer = parent.getTrack().getActiveLayer();
		}
		layers = parent.getTrack().getLayers().getLayers();

		// Get area dimensions
		if (isVisible) {
			legendWidth = getMaxWidth();
			originalLegendWidth = legendWidth;
		}
		legendHeight = getHeight();
	}


	/**
	 * @return true if the cursor is over the legend
	 */
	public boolean isCursorOverLegend() {
		return isCursorOverLegend;
	}


	/**
	 * @param p a {@link Point} on the track
	 * @return true if the point is in the roller, false otherwise
	 */
	private boolean isInRoller (Point p) {
		int x = p.x;
		int y = p.y;

		int rollerX1 = getRollerX();
		int rollerX2 = rollerX1 + rollerWidth;
		int rollerY1 = 1;
		int rollerY2 = rollerY1 + rollerHeight;

		boolean contain = false;

		if ((x >= rollerX1) && (x <= rollerX2) && (y >= rollerY1) && (y <= rollerY2)) {
			contain = true;
		}

		return contain;
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		if ((arg0.getButton() == MouseEvent.BUTTON1) && isInRoller(arg0.getPoint())) {
			if (isVisible) {
				closeLegend();
			} else {
				openLegend();
			}
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent arg0) {}


	@Override
	public void mouseExited(MouseEvent arg0) {}


	@Override
	public void mouseMoved(MouseEvent e) {
		isCursorOverLegend = isInRoller(e.getPoint());
		parent.getTrack().updateGraphicCursor();
	}


	@Override
	public void mousePressed(MouseEvent arg0) {}


	@Override
	public void mouseReleased(MouseEvent arg0) {}


	/**
	 * Open the legend
	 */
	private void openLegend () {
		Thread rolling = new Rolling(Rolling.OPEN);
		rolling.start();
	}


	/**
	 * Print a string
	 * @param s	the string
	 * @param x	the x position
	 * @param y the y position
	 * @param color the color of the text
	 * @return the x position the string continues
	 */
	private int printString (String s, int x, int y, Color color) {
		gLegend.setColor(Colors.BLACK);
		gLegend.drawString(s, x, y);
		return x += gLegend.getFontMetrics().stringWidth(s);
	}


	/**
	 * Register the {@link ForegroundLayer} listeners to the {@link GraphicsPanel}
	 * @param graphicsPanel
	 */
	private void registerListener (GraphicsPanel graphicsPanel) {
		graphicsPanel.addMouseListener(this);
		graphicsPanel.addMouseMotionListener(this);
	}
}
