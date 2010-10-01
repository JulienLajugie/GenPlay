/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.core.rescorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class Repositioner {

	public static void main (String args[]) {
		try {
			System.out.println("Running Repositioner");
			double startTime = System.currentTimeMillis()/1000;
			Map<String, List<Double>> startStopScore = new HashMap<String, List<Double>>();
			
			// coverage file
			BufferedReader buf = new BufferedReader(new FileReader(args[0]));
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
		
//			int linecounter = 0;
			int index = 0;
			BufferedReader newbuf = new BufferedReader(new FileReader(args[1]));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(args[2]));
			String lineReadFromFile2 = newbuf.readLine();
			StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
			while (lineReadFromFile2 != null) {			
//				System.out.println(++linecounter);
				String chrmomosome = newstrtok.nextToken();
				int chrStart = Integer.parseInt(newstrtok.nextToken());
				int chrStop = Integer.parseInt(newstrtok.nextToken());
				String geneFromFile2 = newstrtok.nextToken();

				Iterator<String> iter = startStopScore.keySet().iterator();
				while (iter.hasNext()) {
					index = 0;
					String key = iter.next();	
										
					if (key.compareTo(geneFromFile2) != 0) {
						index = 0;
						continue;
					}

					if (key.compareTo(geneFromFile2) == 0) {
						
						List<Double> value = startStopScore.get(key);
						
						double[][] intermediatestartstopscorearray = new double[value.size()/3][3];
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
						
						// now copy sorted array to a list and while adding the array also add the zeros
						tempList = new ArrayList<Double>();
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
										
						// now copy this list to the finalarray
						double[][] startstopscorearray = new double[tempList.size()/3][3];
						j = 0;
						for (int i = 0; i < tempList.size(); i+=3) {
							startstopscorearray[j][0] = tempList.get(i);
							startstopscorearray[j][1] = tempList.get(i+1);
							startstopscorearray[j][2] = tempList.get(i+2);
							j++;
						}						
						
						int i = 0;
						while (newstrtok.hasMoreTokens()) {
							if (i == 6) {
								break;
							}
							newstrtok.nextToken();
							i++;
						}

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

						List<Integer> relativebplengths = new ArrayList<Integer>();
						int sum = 0;
						for (i = 0; i < basePairs.size(); i++) {
							sum += basePairs.get(i);
							relativebplengths.add(sum);
						}

						List<Integer> mergedList = new ArrayList<Integer>();
						mergedList.add(0);
						i = 0;
						j = 0;
						while (i < relativebplengths.size() && j < tempList.size()) {
							if ((j+1)%3 == 0) {
								j++;
								continue;
							}
							int relbp = relativebplengths.get(i);
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

						List<Double> finalStartStopScore = new ArrayList<Double>();
						int prevLength = 0;
						for (i = 0; i < startstopscorearray.length; i++) {
							int startval = (int)startstopscorearray[i][0];
							int stopval = (int)startstopscorearray[i][1];
							double totalscore = startstopscorearray[i][2];
							int length = stopval - startval;
							int startindex = 0;
							int stopindex = 0;
							for (j = 0; j < mergedList.size(); j++) {
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
								index = 0;
								int mergedK = mergedList.get(k).intValue();
								while (index < relativebplengths.size()) {
									if (relativebplengths.get(index) >= mergedK) {
										break;
									}
									index++;
								}
								int position = exonStarts.get(index);
								if (relativebplengths.get(index) == mergedK && index+1 < exonStarts.size()) {
									position = exonStarts.get(index+1);
									prevLength += basePairs.get(index);
								}
								int mergedK1 = mergedList.get(k+1);
								finalStartStopScore.add((double) position + mergedK - prevLength);
								finalStartStopScore.add((double) position + mergedK1 - prevLength);
								finalStartStopScore.add(totalscore * (mergedK1 - mergedK) / length);
							}
						}
						for (i = 0; i < finalStartStopScore.size(); i+=3) {
							int finalstart = finalStartStopScore.get(i).intValue();
							int finalstop = finalStartStopScore.get(i+1).intValue();
							double finalscore = finalStartStopScore.get(i+2);
							if (finalscore > 0.0) {
								bufWriter.write(chrmomosome + "\t" + finalstart + "\t" + finalstop + "\t" + finalscore + "\n");
								//bufWriter.write(chrmomosome + "\t" + geneFromFile2 + "\t" + finalstart + "\t" + finalstop + "\t" + finalscore + "\n");
							}
						}
						// System.out.println("done");
					}					
				}
				lineReadFromFile2 = newbuf.readLine();
				if (lineReadFromFile2 != null) {
					newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
				}				
			}
			bufWriter.close();
			double endTime = System.currentTimeMillis()/1000;
			System.out.println("Repositioner Runtime: " + (endTime - startTime)/60 + " minutes");
			
			
			System.out.println("Running File Sorter");
			startTime = System.currentTimeMillis()/1000;
			buf = new BufferedReader(new FileReader(args[2]));
			List<FileSorter> file = new ArrayList<FileSorter>();
			lineRead = buf.readLine();
			strtok = new StringTokenizer(lineRead,"\t\n");
			while (lineRead != null) {
				String chrName = strtok.nextToken();
				int start = Integer.parseInt(strtok.nextToken());
				int stop = Integer.parseInt(strtok.nextToken());
				double score = Double.parseDouble(strtok.nextToken());
				FileSorter fileSorter = new FileSorter(chrName, start, stop, score);
				file.add(fileSorter);
				lineRead = buf.readLine();
				if (lineRead != null) {
					strtok = new StringTokenizer(lineRead,"\t\n");
				}
			}			
			
			Collections.sort(file);
			// write the sorted data to the file
			bufWriter = new BufferedWriter(new FileWriter(args[2]));
			Iterator<FileSorter> iter = file.iterator();
			while (iter.hasNext()) {
				FileSorter fs = iter.next();
				bufWriter.write(fs.getChromosomeName() + "\t" + fs.getStart() + "\t" + fs.getStop() + "\t" + fs.getScore() + "\n");
			}
			bufWriter.close();
			endTime = System.currentTimeMillis()/1000;
			System.out.println("Sorter Runtime: " + (endTime - startTime)/60 + " minutes");

		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}