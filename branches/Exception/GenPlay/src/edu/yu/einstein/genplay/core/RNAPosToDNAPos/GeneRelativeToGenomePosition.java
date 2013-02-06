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
package edu.yu.einstein.genplay.core.RNAPosToDNAPos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.yu.einstein.genplay.core.enums.RNAToDNAResultType;
import edu.yu.einstein.genplay.exception.ExceptionManager;


/**
 * Creates a file with DNA coordinate from a RNA coordinate coverage file and an annotation file
 * @author Chirag Gorasia
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneRelativeToGenomePosition {
	private static Map<String, List<Double>> startStopScore; 						// map to store the start stop and score
	private static Map<String, List<List<String>>> remainderLineFromCoverageFile;	// map containing all lines of the coverage file
	private final RNAToDNAResultType outputFileType;										// result type
	private final File coverageFile;								 						// coverage file
	private final File annotationFile;							 						// annotation file
	private final File outputFile;														// output file


	/**
	 * Creates an instance of {@link GeneRelativeToGenomePosition}
	 * @param coverageFile coverage file
	 * @param annotationFile annotation file
	 * @param outputFile output file
	 * @param outputFileType type of output
	 */
	public GeneRelativeToGenomePosition(File coverageFile, File annotationFile, File outputFile, RNAToDNAResultType outputFileType) {
		this.coverageFile = coverageFile;
		this.annotationFile = annotationFile;
		this.outputFile = outputFile;
		this.outputFileType = outputFileType;
	}


	/**
	 * private method to populate the startStopScore hash map
	 * @throws IOException
	 */
	private void loadCoverageFileCompletelyOnHashMap() throws IOException {
		remainderLineFromCoverageFile = new HashMap<String, List<List<String>>>();

		// coverage file
		BufferedReader buf = new BufferedReader(new FileReader(coverageFile));
		String lineRead = buf.readLine();
		StringTokenizer strtok;
		List<List<String>> tempList = new ArrayList<List<String>>();

		while ((lineRead != null) && (lineRead.length() > 0)) {
			try {
				strtok = new StringTokenizer(lineRead, "\t\n");
				String gene = strtok.nextToken();

				// now skip the start stop and score and store the remainder of the line
				List<String> toBeAdded = new ArrayList<String>();
				toBeAdded.add(strtok.nextToken());
				toBeAdded.add(strtok.nextToken());

				// UNCOMMENT THE NEXT LINE FOR JUNCTION FILES
				//strtok.nextToken();

				toBeAdded.add(strtok.nextToken());
				String remainingLine = "";
				while (strtok.hasMoreTokens()) {
					remainingLine += strtok.nextToken()+"\t";
				}
				toBeAdded.add(remainingLine);
				if (remainderLineFromCoverageFile.containsKey(gene) == true) {
					remainderLineFromCoverageFile.get(gene).add(toBeAdded);
				} else {
					tempList.add(toBeAdded);
					remainderLineFromCoverageFile.put(gene, tempList);
				}
			} catch (NoSuchElementException e) {
				lineRead = buf.readLine();
				tempList = new ArrayList<List<String>>();
			}
			lineRead = buf.readLine();
			tempList = new ArrayList<List<String>>();
		}
		buf.close();
	}


	/**
	 * private method to populate the startStopScore hashmap
	 * @throws IOException
	 */
	private void loadCoverageFileOnHashMap() throws IOException {
		startStopScore = new HashMap<String, List<Double>>();

		// coverage file
		BufferedReader buf = new BufferedReader(new FileReader(coverageFile));
		String lineRead = buf.readLine();
		StringTokenizer strtok;

		List<Double> tempList = new ArrayList<Double>();
		while ((lineRead != null) && (lineRead.length() > 0)) {
			try {
				strtok = new StringTokenizer(lineRead, "\t\n");
				String gene = strtok.nextToken();
				double start = Double.parseDouble(strtok.nextToken());
				double stop = Double.parseDouble(strtok.nextToken());
				// uncomment the next line for junction files
				//strtok.nextToken();
				double score = Double.parseDouble(strtok.nextToken());
				if (startStopScore.containsKey(gene)) {
					startStopScore.get(gene).add(start);
					startStopScore.get(gene).add(stop);
					startStopScore.get(gene).add(score);
				} else {
					tempList.add(start);
					tempList.add(stop);
					tempList.add(score);
					startStopScore.put(gene, tempList);
				}
			} catch (NoSuchElementException e) {
				lineRead = buf.readLine();
				tempList = new ArrayList<Double>();
			}
			lineRead = buf.readLine();
			tempList = new ArrayList<Double>();
		}
		buf.close();
	}


	/**
	 * private method to populate and sort an intermediate array having the start stop and scores
	 * @param value
	 * @param intermediatestartstopscorearray
	 * @return intermediatestartstopscorearray
	 */
	private double[][] populateAndSortIntermediateArray (List<Double> value, double[][] intermediatestartstopscorearray, List<List<String>> remainingString) {
		int j = 0;
		for (int i = 0; i < value.size(); i+=3) {
			intermediatestartstopscorearray[j][0] = value.get(i);
			intermediatestartstopscorearray[j][1] = value.get(i+1);
			intermediatestartstopscorearray[j][2] = value.get(i+2);
			j++;
		}

		// sorting intermediatestartstopscorearray
		for (int i = 0; i < intermediatestartstopscorearray.length; i++) {
			for (j = i+1; j < intermediatestartstopscorearray.length; j++) {
				if (intermediatestartstopscorearray[i][0] > intermediatestartstopscorearray[j][0]) {
					double tempstart = intermediatestartstopscorearray[i][0];
					double tempstop = intermediatestartstopscorearray[i][1];
					double tempscore = intermediatestartstopscorearray[i][2];
					intermediatestartstopscorearray[i][0] = intermediatestartstopscorearray[j][0];
					intermediatestartstopscorearray[i][1] = intermediatestartstopscorearray[j][1];
					intermediatestartstopscorearray[i][2] = intermediatestartstopscorearray[j][2];
					intermediatestartstopscorearray[j][0] = tempstart;
					intermediatestartstopscorearray[j][1] = tempstop;
					intermediatestartstopscorearray[j][2] = tempscore;

					if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
						Collections.swap(remainingString, i, j);
					}
				}
			}
		}
		return intermediatestartstopscorearray;
	}


	/**
	 * private method to populate the missing range values in an arraylist
	 * @param tempList
	 * @param intermediatestartstopscorearray
	 * @return tempList
	 */
	private List<Double> populateMissingValuesIntoList (List<Double> tempList, double[][] intermediatestartstopscorearray, List<List<String>> remainingString, List<List<String>> newRemainingString) {
		List<String> tempRemainingListElement = new ArrayList<String>();
		for (int i = 0; i < intermediatestartstopscorearray.length; i++) {
			if ((i == 0) && (intermediatestartstopscorearray[i][0] > 0)) {
				tempList.add(0.0);
				tempList.add(intermediatestartstopscorearray[i][0]);
				tempList.add(0.0);

				if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
					tempRemainingListElement.add("0.0");
					tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i][0]).toString());
					tempRemainingListElement.add("0.0");
					tempRemainingListElement.add("");

					newRemainingString.add(tempRemainingListElement);
					tempRemainingListElement = new ArrayList<String>();
				}
			}
			if ((i !=0) && (intermediatestartstopscorearray[i][0] > intermediatestartstopscorearray[i-1][1])) {
				tempList.add(intermediatestartstopscorearray[i-1][1]);
				tempList.add(intermediatestartstopscorearray[i][0]);
				tempList.add(0.0);

				if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
					tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i-1][1]).toString());
					tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i][0]).toString());
					tempRemainingListElement.add("0.0");
					tempRemainingListElement.add("");

					newRemainingString.add(tempRemainingListElement);
					tempRemainingListElement = new ArrayList<String>();
				}
			}
			tempList.add(intermediatestartstopscorearray[i][0]);
			tempList.add(intermediatestartstopscorearray[i][1]);
			tempList.add(intermediatestartstopscorearray[i][2]);

			if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
				tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i][0]).toString());
				tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i][1]).toString());
				tempRemainingListElement.add(new Double(intermediatestartstopscorearray[i][2]).toString());
				tempRemainingListElement.add(remainingString.get(i).get(3));

				newRemainingString.add(tempRemainingListElement);
				tempRemainingListElement = new ArrayList<String>();
			}
		}
		return tempList;
	}


	/**
	 * private method to populate an array out of the final list of start stop and score values
	 * @param tempList
	 * @param startstopscorearray
	 * @return startstopscorearray
	 */
	private double[][] populateFinalArray (List<Double> tempList, double[][] startstopscorearray) {
		int j = 0;
		for (int i = 0; i < tempList.size(); i+=3) {
			startstopscorearray[j][0] = tempList.get(i);
			startstopscorearray[j][1] = tempList.get(i+1);
			startstopscorearray[j][2] = tempList.get(i+2);
			j++;
		}
		return startstopscorearray;
	}


	/**
	 * private method to merge the list of absolute lengths and list of start stop values from the coverage file
	 * @param absolutebplengths
	 * @param tempList
	 * @param mergedList
	 * @return mergedList
	 */
	private List<Integer> listsMerger (List<Integer> absolutebplengths, List<Double> tempList, List<Integer> mergedList) {
		mergedList.add(0);
		int i = 0;
		int j = 0;
		while ((i < absolutebplengths.size()) && (j < tempList.size())) {
			if (((j+1)%3) == 0) {
				j++;
				continue;
			}
			int relbp = absolutebplengths.get(i);
			int val = tempList.get(j).intValue();
			if (relbp < val) {
				if (mergedList.contains(relbp) == false) {
					mergedList.add(relbp);
				}
				i++;
			} else {
				if (mergedList.contains(val) == false) {
					mergedList.add(val);
				}
				j++;
			}
		}
		return mergedList;
	}


	/**
	 * private method to populate an array list with the repositioned values and weighted scores
	 * @param startstopscorearray
	 * @param mergedList
	 * @param absolutebplengths
	 * @param exonStarts
	 * @param basePairs
	 * @param finalStartStopScore
	 * @return finalStartStopScore
	 */
	private List<Double> populateRepositionedArrayList(double[][] startstopscorearray, List<Integer> mergedList, List<Integer> absolutebplengths, List<Integer> exonStarts, List<Integer> basePairs, List<Double> finalStartStopScore, List<List<String>> newRemainingString, List<String> remainderStringForPrinting) {
		//long time_inside_function = System.currentTimeMillis();
		int prevLength = 0;
		for (int i = 0; i < startstopscorearray.length; i++) {
			int startval = (int)startstopscorearray[i][0];
			int stopval = (int)startstopscorearray[i][1];
			double totalscore = startstopscorearray[i][2];
			//int length = stopval - startval;
			int startindex = 0;
			int stopindex = 0;
			for (int j = 0; j < mergedList.size(); j++) {
				int mergedJ = mergedList.get(j);
				if (mergedJ == startval) {
					startindex = j;
				}
				if (mergedJ == stopval) {
					stopindex = j;
					break;
				}
			}

			for (int k = startindex; k < stopindex; k++) {
				// locate the index position in relativebplengths and then the corresponding actual position
				int index = 0;
				int mergedK = mergedList.get(k).intValue();
				while (index < absolutebplengths.size()) {
					if (absolutebplengths.get(index) >= mergedK) {
						break;
					}
					index++;
				}
				int position = exonStarts.get(index);
				if ((absolutebplengths.get(index) == mergedK) && ((index+1) < exonStarts.size())) {
					position = exonStarts.get(index+1);
					prevLength += basePairs.get(index);
				}
				int mergedK1 = mergedList.get(k+1);
				finalStartStopScore.add(((double) position + mergedK) - prevLength);
				finalStartStopScore.add(((double) position + mergedK1) - prevLength);
				//finalStartStopScore.add(totalscore * (mergedK1 - mergedK) / length);
				finalStartStopScore.add(totalscore * (mergedK1 - mergedK));	// changed on 10/19/2010 as per Julien's logic

				if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
					remainderStringForPrinting.add(newRemainingString.get(i).get(3));
				}
			}
		}
		//System.out.println("time inside calculation method " + (System.currentTimeMillis() - time_inside_function));
		return finalStartStopScore;
	}


	/**
	 * private method to print the start stop and score values to the output file
	 * @param chromosome
	 * @param finalStartStopScore
	 * @param bufWriter
	 * @throws IOException
	 */
	private void printToOutputFile (String chromosome, List<Double> finalStartStopScore, BufferedWriter bufWriter) throws IOException {
		for (int i = 0; i < finalStartStopScore.size(); i+=3) {
			int finalstart = finalStartStopScore.get(i).intValue();
			int finalstop = finalStartStopScore.get(i+1).intValue();
			double finalscore = finalStartStopScore.get(i+2);
			if (finalscore > 0.0) {
				bufWriter.write(chromosome + "\t" + finalstart + "\t" + finalstop + "\t" + finalscore + "\n");
			}
		}
	}


	/**
	 * private method to print the start stop and score values to the output file
	 * @param chromosome
	 * @param finalStartStopScore
	 * @param bufWriter
	 * @throws IOException
	 */
	private void printOutputWithExtraFieldsToFile (String key, String chromosome, int chrStart, List<Double> finalStartStopScore, List<String> remainderStringForPrinting, BufferedWriter bufWriter) throws IOException {
		for (int i = 0, j = 0; i < finalStartStopScore.size(); i+=3, j++) {
			int finalstart = finalStartStopScore.get(i).intValue();
			int finalstop = finalStartStopScore.get(i+1).intValue();
			double finalscore = finalStartStopScore.get(i+2);

			if (finalscore > 0.0) {
				bufWriter.write(chromosome + "\t" + finalstart + "\t" + finalstop + "\t" + finalscore + "\t" + key + "\t" + remainderStringForPrinting.get(j) + "\n");
			}
		}
	}


	/**
	 * Private method to write file in gdp format
	 * @param key
	 * @param chrmomosome
	 * @param strand
	 * @param chrStart
	 * @param chrStop
	 * @param finalStartStopScore
	 * @param exonStarts
	 * @param basePairs
	 * @param bufWriter
	 * @throws IOException
	 */
	private void printToOutputFileDifferentFormat(String key, String chrmomosome, String strand, int chrStart, int chrStop, List<Double> finalStartStopScore, List<Integer> exonStarts, List<Integer> basePairs, BufferedWriter bufWriter) throws IOException {
		String starts = "";
		String stops = "";
		String scores = "";

		int startIndex = 0;
		int stopIndex = 1;

		for (int i = 0; i < exonStarts.size(); i++) {
			int individualExonStart = exonStarts.get(i);
			int individualExonStop = individualExonStart + basePairs.get(i);
			double score = 0;

			while ((startIndex < (finalStartStopScore.size() - 2)) && (individualExonStart != finalStartStopScore.get(startIndex).intValue())) {
				startIndex += 3;
			}

			while ((stopIndex < (finalStartStopScore.size() - 1)) && (individualExonStop != finalStartStopScore.get(stopIndex).intValue())) {
				stopIndex += 3;
			}

			for (int k = startIndex+2; k <= (stopIndex+1); k += 3) {
				if (k < finalStartStopScore.size()) {
					score += finalStartStopScore.get(k);
				}
			}

			starts += Integer.toString(individualExonStart) + ",";
			stops += Integer.toString(individualExonStop) + ",";
			scores += Double.toString(score) + ",";
		}
		bufWriter.write(key + "\t" + chrmomosome + "\t" + strand + "\t" + chrStart + "\t" + chrStop + "\t" + starts + "\t" + stops + "\t" + scores + "\n");
	}


	/**
	 * Private method to convert exon scores to RPKM
	 * @throws IOException
	 */
	private void scoreToRPKM() throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(outputFile));
		Map<String, List<String>> gdpFileMap = new HashMap<String, List<String>>();
		double totalScoreOfAllExons = 0;

		String lineRead = bufReader.readLine();
		StringTokenizer strTok;

		while ((lineRead != null) && (lineRead.length() > 0)) {
			strTok = new StringTokenizer(lineRead, "\t\n");
			String gene = strTok.nextToken();
			String chr = strTok.nextToken();
			String strand = strTok.nextToken();
			String chrStart = strTok.nextToken();
			String chrStop = strTok.nextToken();
			String exonStarts = strTok.nextToken();
			String exonStops = strTok.nextToken();
			String exonScores = strTok.nextToken();
			List<String> toBeAdded = new ArrayList<String>();
			toBeAdded.add(chr);
			toBeAdded.add(strand);
			toBeAdded.add(chrStart);
			toBeAdded.add(chrStop);
			toBeAdded.add(exonStarts);
			toBeAdded.add(exonStops);
			toBeAdded.add(exonScores);

			gdpFileMap.put(gene, toBeAdded);

			StringTokenizer exonScoreTok = new StringTokenizer(exonScores,",");
			while (exonScoreTok.hasMoreTokens()) {
				totalScoreOfAllExons += Double.parseDouble(exonScoreTok.nextToken());
			}
			lineRead = bufReader.readLine();
		}
		bufReader.close();

		totalScoreOfAllExons = 1000000000 / totalScoreOfAllExons;

		Iterator<String> iter = gdpFileMap.keySet().iterator();
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
		while (iter.hasNext()) {
			List<String> starts = new ArrayList<String>();
			String finalStarts = "";
			List<String> stops = new ArrayList<String>();
			String finalStops = "";
			List<String> scores = new ArrayList<String>();
			String finalScores = "";

			List<String> value = new ArrayList<String>();

			String gene = iter.next();
			value = gdpFileMap.get(gene);
			String chr = value.get(0);
			String strand = value.get(1);
			String chrStart = value.get(2);
			String chrStop = value.get(3);

			StringTokenizer startTok = new StringTokenizer(value.get(4),",");
			while (startTok.hasMoreTokens()) {
				String token = startTok.nextToken();
				finalStarts += token + ",";
				starts.add(token);
			}

			StringTokenizer stopTok = new StringTokenizer(value.get(5),",");
			while (stopTok.hasMoreTokens()) {
				String token = stopTok.nextToken();
				finalStops += token + ",";
				stops.add(token);
			}

			StringTokenizer exonScoreTok = new StringTokenizer(value.get(6),",");
			int i = 0;
			while (exonScoreTok.hasMoreTokens()) {
				String token = exonScoreTok.nextToken();
				scores.add(new Double((Double.parseDouble(token) * totalScoreOfAllExons) / (Integer.parseInt(stops.get(i)) - Integer.parseInt(starts.get(i)))).toString());
				finalScores += NumberFormat.getInstance().format((Double.parseDouble(token) * totalScoreOfAllExons) / (Integer.parseInt(stops.get(i)) - Integer.parseInt(starts.get(i))))+",";
			}
			bufWriter.write(gene + "\t" + chr + "\t" + strand + "\t" + chrStart + "\t" + chrStop + "\t" + finalStarts + "\t" + finalStops + "\t" + finalScores + "\n");
		}
		bufWriter.close();
	}


	/**
	 * private method to sort the repositioned file
	 * @throws IOException
	 */
	private void sortRepositionedFile() throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(outputFile));
		List<FileDataLineForSorting> file = new ArrayList<FileDataLineForSorting>();
		String lineRead = buf.readLine();
		StringTokenizer strtok = new StringTokenizer(lineRead,"\t\n");
		while (lineRead != null) {
			String chrName = strtok.nextToken();
			int start = Integer.parseInt(strtok.nextToken());
			int stop = Integer.parseInt(strtok.nextToken());
			double score = Double.parseDouble(strtok.nextToken());
			FileDataLineForSorting fileSorter = new FileDataLineForSorting(chrName, start, stop, score);
			file.add(fileSorter);
			lineRead = buf.readLine();
			if (lineRead != null) {
				strtok = new StringTokenizer(lineRead,"\t\n");
			}
		}
		buf.close();

		Collections.sort(file);
		// write the sorted data to the file
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
		Iterator<FileDataLineForSorting> iter = file.iterator();
		while (iter.hasNext()) {
			FileDataLineForSorting fs = iter.next();
			bufWriter.write(fs.getChromosomeName() + "\t" + fs.getStart() + "\t" + fs.getStop() + "\t" + fs.getScore() + "\n");
		}
		bufWriter.close();
	}


	/**
	 * Method to get the Genome positions and scores
	 */
	public void rePosition() {
		try {
			loadCoverageFileOnHashMap();

			if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
				loadCoverageFileCompletelyOnHashMap();
			}

			BufferedReader newbuf = new BufferedReader(new FileReader(annotationFile));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
			String lineReadFromFile2 = newbuf.readLine();
			StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");

			while (lineReadFromFile2 != null) {
				String chrmomosome = "";
				int chrStart = 0;
				int chrStop = 0;
				String geneFromFile2 = "";
				String strand = "";
				try {
					chrmomosome = newstrtok.nextToken();
					chrStart = Integer.parseInt(newstrtok.nextToken());
					chrStop = Integer.parseInt(newstrtok.nextToken());	// for the stop position
					geneFromFile2 = newstrtok.nextToken();
					strand = "";
				} catch (NoSuchElementException e) {
					lineReadFromFile2 = newbuf.readLine();
					if (lineReadFromFile2 != null) {
						newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
					}
				}

				Iterator<String> iter = startStopScore.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					if (key.compareTo(geneFromFile2) != 0) {
						continue;
					}
					if (key.compareTo(geneFromFile2) == 0) {
						List<Double> value = startStopScore.get(key);

						List<List<String>> remainingString = null;
						if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
							remainingString = remainderLineFromCoverageFile.get(key);
						}
						double[][] intermediatestartstopscorearray = new double[value.size()/3][3];
						intermediatestartstopscorearray = populateAndSortIntermediateArray(value, intermediatestartstopscorearray, remainingString);

						// now copy sorted array to a list and while adding the array also add the zeros
						List<Double> tempList = new ArrayList<Double>();

						List<List<String>> newRemainingString = null;
						if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
							newRemainingString = new ArrayList<List<String>>();
						}
						tempList = populateMissingValuesIntoList(tempList,intermediatestartstopscorearray, remainingString, newRemainingString);

						// now copy this list to the finalarray
						double[][] startstopscorearray = new double[tempList.size()/3][3];
						startstopscorearray = populateFinalArray(tempList, startstopscorearray)	;

						// get to the exon positions and lengths in the file
						int i = 0;
						while (newstrtok.hasMoreTokens()) {
							if (i == 6) {
								break;
							}
							if (i == 1) {
								strand = newstrtok.nextToken();
								i++;
								continue;
							}
							newstrtok.nextToken();
							i++;
						}

						// add the exon start positions and lengths to lists
						String basePairsAsStrings = newstrtok.nextToken();
						String exonStartsAsStrings = newstrtok.nextToken();

						List<Integer> basePairs = new ArrayList<Integer>();
						List<Integer> exonStarts = new ArrayList<Integer>();

						StringTokenizer basePairTok = new StringTokenizer(basePairsAsStrings,",");
						while (basePairTok.hasMoreElements()) {
							basePairs.add(Integer.parseInt(basePairTok.nextToken()));
						}

						StringTokenizer exonStartTok = new StringTokenizer(exonStartsAsStrings,",");
						while (exonStartTok.hasMoreElements()) {
							exonStarts.add(Integer.parseInt(exonStartTok.nextToken())+chrStart);
						}

						// get the absolute lengths
						List<Integer> absolutebplengths = new ArrayList<Integer>();
						int sum = 0;
						for (i = 0; i < basePairs.size(); i++) {
							sum += basePairs.get(i);
							absolutebplengths.add(sum);
						}

						// Merge the two sorted lists
						List<Integer> mergedList = new ArrayList<Integer>();
						mergedList = listsMerger(absolutebplengths, tempList, mergedList);

						// populate a list which contains the final start stop and score values
						List<Double> finalStartStopScore = new ArrayList<Double>();
						List<String> remainderStringForPrinting = new ArrayList<String>();
						finalStartStopScore = populateRepositionedArrayList(startstopscorearray, mergedList, absolutebplengths, exonStarts, basePairs, finalStartStopScore, newRemainingString, remainderStringForPrinting);

						// write the list to the output file
						if (outputFileType == RNAToDNAResultType.BGR) {
							printToOutputFile(chrmomosome, finalStartStopScore, bufWriter);
						}

						// write to output file in gdp format
						if (outputFileType == RNAToDNAResultType.GDP) {
							printToOutputFileDifferentFormat(key, chrmomosome, strand, chrStart, chrStop, finalStartStopScore, exonStarts, basePairs, bufWriter);
						}

						// write to output file including extra fields from the input file
						if (outputFileType == RNAToDNAResultType.BGR_WITH_EXTRA_FIELDS) {
							printOutputWithExtraFieldsToFile(key, chrmomosome, chrStart, finalStartStopScore, remainderStringForPrinting, bufWriter);
						}
					}
				}
				lineReadFromFile2 = newbuf.readLine();
				if (lineReadFromFile2 != null) {
					newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
				}
			}
			newbuf.close();
			bufWriter.close();

			// We sort the output file
			if (outputFileType == RNAToDNAResultType.BGR) {
				sortRepositionedFile();
			}

			// Score to RPKM
			if (outputFileType == RNAToDNAResultType.GDP) {
				scoreToRPKM();
			}

		}catch (IOException e) {
			ExceptionManager.getInstance().handleException(e);
		}
	}
}
