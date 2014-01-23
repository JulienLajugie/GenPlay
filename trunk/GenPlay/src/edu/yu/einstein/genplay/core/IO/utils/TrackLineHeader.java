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
package edu.yu.einstein.genplay.core.IO.utils;

import java.awt.Color;

import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.GraphType;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Track line from the BED, BEDGRAPH, WIG, PSL, GFF, GFF3 files.
 * @author Julien Lajugie
 */
public class TrackLineHeader {

	/** Header of a track line.  A track line starts by the following string */
	private static final String TRACK_LINE_HEADER = "track";


	/**  Name header */
	private static final String NAME_HEADER = "name";


	/** File type header  */
	private static final String FILE_TYPE_HEADER = "type";


	/** Color header */
	private static final String COLOR_HEADER = "color";


	/**  Transparency header */
	private static final String TRANSPARENCY_HEADER = "transparency";


	/**  Auto-scale header */
	private static final String AUTOSCALE_HEADER = "autoScale";


	/**  Grid default header */
	private static final String GRID_DEFAULT_HEADER = "gridDefault";


	/**  Graph type header */
	private static final String GRAPH_TYPE_HEADER = "graphType";


	/**  Gene database URL header */
	private static final String GENE_DATABASE_URL_HEADER = "geneDBURL";


	/**  Gene score type header */
	private static final String GENE_SCORE_TYPE_HEADER = "geneScoreType";


	/**
	 * @param line
	 * @return true if the specified line is a track line
	 */
	public static boolean isTrackLine(String line) {
		if (line == null) {
			return false;
		}
		line = line.trim();
		if (!line.isEmpty() && (line.charAt(0) == '#')) {
			line = line.substring(1);
			line.trim();
		}
		return line.startsWith(TRACK_LINE_HEADER);
	}


	/**
	 * @param line
	 * @return the specified line without the track line header if the line is a track line header.
	 * Returns the line otherwise.
	 */
	private static String removeTrackLineHeader(String line) {
		if (!isTrackLine(line)) {
			return line;
		}
		line = line.trim();
		if (!line.isEmpty() && (line.charAt(0) == '#')) {
			line = line.substring(1);
			line.trim();
		}
		return line.substring(TRACK_LINE_HEADER.length()).trim();
	}


	private String 			name;			// name of the data
	private String			fileType;		// type of the file
	private Color 			color; 			// color of the data (RRR,GGG,BBB) 0-255
	private Boolean 		isAutoScale; 	// on | off
	private Boolean 		isGridVisible; 	// on | off
	private GraphType 		graphType; 		//bar | points | heatmap
	private String 			geneDBURL;		// URL of the database for the genes (GenPlay extension)
	private GeneScoreType 	geneScoreType;	// type of the scores of the genes ("RPKM", "Base Coverage Sum", "Maximum Coverage") (GenPlay extension)


	/**
	 * Generates a track line header representing the state of the current object.
	 * @return a track line. Null if all the fields are null
	 */
	public String generateTrackLine() {
		String trackLine = TRACK_LINE_HEADER + " ";
		if (name != null) {
			trackLine += NAME_HEADER + "=\"" + name + "\" ";
		}
		if (fileType != null) {
			trackLine += FILE_TYPE_HEADER + "=" + fileType + " ";
		}
		if (color != null) {
			trackLine += COLOR_HEADER + "=" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + " ";
			if (color.getAlpha() != 255) {
				trackLine += TRANSPARENCY_HEADER + "=" + color.getAlpha() + " ";
			}
		}
		if (isAutoScale != null) {
			trackLine += AUTOSCALE_HEADER + "=" + (isAutoScale ? "on " : "off ");
		}
		if (isGridVisible != null) {
			trackLine += GRID_DEFAULT_HEADER + "=" + (isGridVisible ? "on" : "off");
		}
		if (graphType != null) {
			trackLine += GRAPH_TYPE_HEADER + "=" + graphType + " ";
		}
		if (geneDBURL != null){
			trackLine += GENE_DATABASE_URL_HEADER + "=\"" + geneDBURL + "\" ";
		}
		if (geneScoreType != null) {
			trackLine += GENE_SCORE_TYPE_HEADER + "=\"" + geneScoreType +"\" ";
		}
		// if there is no field set we return null
		if (trackLine.trim().equals(TRACK_LINE_HEADER)) {
			return null;
		}
		return trackLine;
	}


	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}


	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}


	/**
	 * @return the geneDBURL
	 */
	public String getGeneDBURL() {
		return geneDBURL;
	}


	/**
	 * @return the geneScoreType
	 */
	public GeneScoreType getGeneScoreType() {
		return geneScoreType;
	}


	/**
	 * @return the graphType
	 */
	public GraphType getGraphType() {
		return graphType;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the isAutoScale
	 */
	public boolean isAutoScale() {
		return isAutoScale;
	}


	/**
	 * @return the isGridVisible
	 */
	public boolean isGridVisible() {
		return isGridVisible;
	}


	/**
	 * Parses the specified line and sets the values of the fields of an instance
	 * with the values retrieved from the track line header
	 * @param line
	 */
	public void parseTrackLine(String line) {
		// make sure that the specified parameter is a track line
		if (isTrackLine(line)) {
			// we remove the track line header from the line
			line = removeTrackLineHeader(line);
			// split space separated but ignoring space between double quotes
			String[] splitLine = line.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			for (String currentField: splitLine) {
				// split "=" separated if the = sign is not between double quotes
				String[] splitField = currentField.split("=(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				// set the extracted field with the extracted value
				if (splitField.length == 2) {
					setValue(splitField[0].trim(), splitField[1].trim());
				}
			}
		}
	}


	/**
	 * @param isAutoScale the isAutoScale to set
	 */
	public void setAutoScale(boolean isAutoScale) {
		this.isAutoScale = isAutoScale;
	}


	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}


	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	/**
	 * @param geneDBURL the geneDBURL to set
	 */
	public void setGeneDBURL(String geneDBURL) {
		this.geneDBURL = geneDBURL;
	}


	/**
	 * @param geneScoreType the geneScoreType to set
	 */
	public void setGeneScoreType(GeneScoreType geneScoreType) {
		this.geneScoreType = geneScoreType;
	}


	/**
	 * @param graphType the graphType to set
	 */
	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}


	/**
	 * @param isGridVisible the isGridVisible to set
	 */
	public void setGridVisible(boolean isGridVisible) {
		this.isGridVisible = isGridVisible;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Sets the specified field with the specified value extracted from the track line header
	 * @param fieldName name of a field
	 * @param fieldValue value of a field
	 */
	private void setValue(String fieldName, String fieldValue) {
		// check that the parameters are valid
		if ((fieldName != null)
				&& !fieldName.isEmpty()
				&& (fieldValue != null)
				&& !fieldValue.isEmpty()) {
			if (fieldValue.charAt(0) == '"') {
				fieldValue = fieldValue.substring(1);
			}
			if ((!fieldValue.isEmpty()) && (fieldValue.charAt(fieldValue.length() - 1) == '"')) {
				fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
			}
			fieldValue.trim();
			// check again if the value is empty now that we removed the double quotes
			if (!fieldValue.isEmpty()) {
				if (fieldName.equalsIgnoreCase(NAME_HEADER)) {
					name = fieldValue;
				} else if (fieldName.equalsIgnoreCase(FILE_TYPE_HEADER)) {
					fileType = fieldValue;
				} else if (fieldName.equalsIgnoreCase(COLOR_HEADER)) {
					int alpha = 255;
					if ((color != null) && (color.getAlpha() != 0)) {
						alpha = color.getAlpha();
					}
					String[] colorRGB = fieldValue.split(",");
					if (colorRGB.length == 3) {
						int red = Integer.parseInt(colorRGB[0]);
						int green = Integer.parseInt(colorRGB[1]);
						int blue = Integer.parseInt(colorRGB[2]);
						color = new Color(red, green, blue);
						color = Colors.addTransparency(color, alpha);
					}
				} else if (fieldName.equalsIgnoreCase(TRANSPARENCY_HEADER)) {
					if (color == null) {
						color = new Color(0);
					}
					int alpha = Integer.parseInt(fieldValue);
					color = Colors.addTransparency(color, alpha);
				} else if (fieldName.equalsIgnoreCase(AUTOSCALE_HEADER)) {
					if (fieldValue.equalsIgnoreCase("On")) {
						isAutoScale = true;
					} else if (fieldValue.equalsIgnoreCase("Off")) {
						isAutoScale = false;
					}
				} else if (fieldName.equalsIgnoreCase(GRID_DEFAULT_HEADER)) {
					if (fieldValue.equalsIgnoreCase("On")) {
						isGridVisible = true;
					} else if (fieldValue.equalsIgnoreCase("Off")) {
						isGridVisible = false;
					}
				} else if (fieldName.equalsIgnoreCase(GRAPH_TYPE_HEADER)) {
					graphType = GraphType.lookup(fieldValue);
				} else if (fieldName.equalsIgnoreCase(GENE_DATABASE_URL_HEADER)) {
					geneDBURL = fieldValue;
				} else if (fieldName.equalsIgnoreCase(GENE_SCORE_TYPE_HEADER)) {
					geneScoreType = GeneScoreType.lookup(fieldValue);
				}
			}
		}
	}
}
