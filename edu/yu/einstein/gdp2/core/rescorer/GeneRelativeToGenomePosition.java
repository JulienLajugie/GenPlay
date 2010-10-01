/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.rescorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * A Class to Reposition and Rescore the exons
 * @author Chirag Gorasia
 * @version 0.1
 */
public class GeneRelativeToGenomePosition {
	
	private static Map<String, List<Double>> startStopScore; // map to store the start stop and score
	private File coverageFile;								 // the coverage file
	private File annotationFile;							 // the annotation file
	private File repositionedFile;							 // the  output file created with genomic positions
	
	/**
	 * Creates an instance of {@link GeneRelativeToGenomePosition} 
	 * @param coverageFile
	 * @param annotationFile
	 * @param repositionedFile
	 */
	public GeneRelativeToGenomePosition(File coverageFile, File annotationFile, File repositionedFile) {
		this.coverageFile = coverageFile;
		this.annotationFile = annotationFile;
		this.repositionedFile = repositionedFile;
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
		while (lineRead != null) {
			strtok = new StringTokenizer(lineRead, "\t\n");
			while (strtok.hasMoreTokens()) {
				String gene = strtok.nextToken();
				double start = Double.parseDouble(strtok.nextToken());
				double stop = Double.parseDouble(strtok.nextToken());
				double score = Double.parseDouble(strtok.nextToken());
				if (startStopScore.containsKey(gene) == true) {
					startStopScore.get(gene).add(start);
					startStopScore.get(gene).add(stop);
					startStopScore.get(gene).add(score);
				} else {
					tempList.add(start);
					tempList.add(stop);
					tempList.add(score);
					startStopScore.put(gene, tempList);
				}	
			}
			lineRead = buf.readLine();
			tempList = new ArrayList<Double>();
		}
	}

	/**
	 * private method to populate and sort an intermediate array having the start stop and scores
	 * @param value
	 * @param intermediatestartstopscorearray
	 * @return intermediatestartstopscorearray
	 */
	private double[][] populateAndSortIntermediateArray (List<Double> value, double[][] intermediatestartstopscorearray) {
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
	private List<Double> populateMissingValuesIntoList (List<Double> tempList, double[][] intermediatestartstopscorearray) {
		for (int i = 0; i < intermediatestartstopscorearray.length; i++) {
			if (i == 0 && intermediatestartstopscorearray[i][0] > 0) {
				tempList.add(0.0);
				tempList.add(intermediatestartstopscorearray[i][0]);
				tempList.add(0.0);
			} 
			if (i !=0 && intermediatestartstopscorearray[i][0] > intermediatestartstopscorearray[i-1][1]) {
				tempList.add(intermediatestartstopscorearray[i-1][1]);
				tempList.add(intermediatestartstopscorearray[i][0]);
				tempList.add(0.0);
			}
			tempList.add(intermediatestartstopscorearray[i][0]);
			tempList.add(intermediatestartstopscorearray[i][1]);
			tempList.add(intermediatestartstopscorearray[i][2]);
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
	 * private method to merge the absolute lengths list and list of start stop values from the coverage file
	 * @param absolutebplengths
	 * @param tempList
	 * @param mergedList
	 * @return mergedList
	 */
	private List<Integer> listsMerger (List<Integer> absolutebplengths, List<Double> tempList, List<Integer> mergedList) {
		mergedList.add(0);
		int i = 0;
		int j = 0;
		while (i < absolutebplengths.size() && j < tempList.size()) {
			if ((j+1)%3 == 0) {
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
	 * private method to populate an array list with the repositioned values
	 * @param startstopscorearray
	 * @param mergedList
	 * @param absolutebplengths
	 * @param exonStarts
	 * @param basePairs
	 * @param finalStartStopScore
	 * @return finalStartStopScore
	 */
	private List<Double> populateRepositionedArrayList(double[][] startstopscorearray, List<Integer> mergedList, List<Integer> absolutebplengths, List<Integer> exonStarts, List<Integer> basePairs, List<Double> finalStartStopScore) {
		int prevLength = 0;
		for (int i = 0; i < startstopscorearray.length; i++) {
			int startval = (int)startstopscorearray[i][0];
			int stopval = (int)startstopscorearray[i][1];
			double totalscore = startstopscorearray[i][2];
			int length = stopval - startval;
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
				if (absolutebplengths.get(index) == mergedK && index+1 < exonStarts.size()) {
					position = exonStarts.get(index+1);
					prevLength += basePairs.get(index);
				}
				int mergedK1 = mergedList.get(k+1);
				finalStartStopScore.add((double) position + mergedK - prevLength);
				finalStartStopScore.add((double) position + mergedK1 - prevLength);
				finalStartStopScore.add(totalscore * (mergedK1 - mergedK) / length);
			}
		}
		return finalStartStopScore;
	}
	
	/**
	 * private method to print the start stop and score values into the output file
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
	 * private method to sort the repositioned file
	 * @throws IOException
	 */
	private void sortRepositionedFile() throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(repositionedFile));
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
		
		Collections.sort(file);
		// write the sorted data to the file
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(repositionedFile));
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
		
			BufferedReader newbuf = new BufferedReader(new FileReader(annotationFile));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(repositionedFile));
			String lineReadFromFile2 = newbuf.readLine();
			StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
			
			while (lineReadFromFile2 != null) {			
				String chrmomosome = newstrtok.nextToken();
				int chrStart = Integer.parseInt(newstrtok.nextToken());
				Integer.parseInt(newstrtok.nextToken());	// for the stop position
				String geneFromFile2 = newstrtok.nextToken();

				Iterator<String> iter = startStopScore.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();											
					if (key.compareTo(geneFromFile2) != 0) {
						continue;
					}
					if (key.compareTo(geneFromFile2) == 0) {						
						List<Double> value = startStopScore.get(key);
						double[][] intermediatestartstopscorearray = new double[value.size()/3][3];
						intermediatestartstopscorearray = populateAndSortIntermediateArray(value, intermediatestartstopscorearray);
						
						// now copy sorted array to a list and while adding the array also add the zeros
						List<Double> tempList = new ArrayList<Double>();
						tempList = populateMissingValuesIntoList(tempList,intermediatestartstopscorearray);
										
						// now copy this list to the finalarray
						double[][] startstopscorearray = new double[tempList.size()/3][3];
						startstopscorearray = populateFinalArray(tempList, startstopscorearray)	;				
						
						// get to the exon positions and lengths in the file
						int i = 0;
						while (newstrtok.hasMoreTokens()) {
							if (i == 6) {
								break;
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
						finalStartStopScore = populateRepositionedArrayList(startstopscorearray, mergedList, absolutebplengths, exonStarts, basePairs, finalStartStopScore);
						
						// write the list to the output file
						printToOutputFile(chrmomosome, finalStartStopScore, bufWriter);
						// System.out.println("One NM value done");
					}					
				}
				lineReadFromFile2 = newbuf.readLine();
				if (lineReadFromFile2 != null) {
					newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
				}				
			}
			bufWriter.close();
			
			// We sort the output file
			sortRepositionedFile();			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}