package yu.einstein.gdp2.core.rescorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Repositioner {
	
	public static void main (String args[]) {
		try {
			double startTime = System.currentTimeMillis()/1000;
			Map<String, List<Double>> startStopScore = new HashMap<String, List<Double>>();
			List<Double> covStartStopScoreList = new ArrayList<Double>();

			// coverage file
			BufferedReader buf = new BufferedReader(new FileReader(args[0]));
			String lineRead = buf.readLine();
			StringTokenizer strtok;
			String prevGene = "";
			double prevStart = 0;
			double prevStop = 0;
			double prevScore = 0;
			if (lineRead != null) {
				strtok = new StringTokenizer(lineRead, "\t\n");
				prevGene = strtok.nextToken();
				prevStart = Double.parseDouble(strtok.nextToken());
				prevStop = Double.parseDouble(strtok.nextToken());
				prevScore = Double.parseDouble(strtok.nextToken());
				covStartStopScoreList.add(prevStart);
				covStartStopScoreList.add(prevStop);
				covStartStopScoreList.add(prevScore);
			}

			lineRead = buf.readLine();
	        int annotationlines = 0;
			while (lineRead != null) {
				strtok = new StringTokenizer(lineRead, "\t\n");
				while (strtok.hasMoreTokens()) {
					String gene = strtok.nextToken();
					if (gene.compareTo(prevGene) == 0) {
						double start = Double.parseDouble(strtok.nextToken());
						double stop = Double.parseDouble(strtok.nextToken());
						double score = Double.parseDouble(strtok.nextToken());
						if (covStartStopScoreList.size() > 0 && covStartStopScoreList.get(covStartStopScoreList.size()-2).intValue() == (int)start) {
							covStartStopScoreList.add(start);
							covStartStopScoreList.add(stop);
							covStartStopScoreList.add(score);
						} else {
							covStartStopScoreList.add(covStartStopScoreList.get(covStartStopScoreList.size()-2));
							covStartStopScoreList.add(start);
							covStartStopScoreList.add(new Double(0));
							covStartStopScoreList.add(start);
							covStartStopScoreList.add(stop);
							covStartStopScoreList.add(score);
						}
					} else {
						annotationlines = 1;
						startStopScore.put(prevGene, covStartStopScoreList);
						covStartStopScoreList = new ArrayList<Double>();
						covStartStopScoreList.add(Double.parseDouble(strtok.nextToken()));
						covStartStopScoreList.add(Double.parseDouble(strtok.nextToken()));
						covStartStopScoreList.add(Double.parseDouble(strtok.nextToken()));
						prevGene = gene;
					}
				}
				lineRead = buf.readLine();
			}
			if (annotationlines == 0) { // there is just one line
				annotationlines = 1;
				startStopScore.put(prevGene, covStartStopScoreList);
			}
			
			int linecounter = 0;
			int index = 0;
			BufferedReader newbuf = new BufferedReader(new FileReader(args[1]));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(args[2]));
			String lineReadFromFile2 = newbuf.readLine();
			StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
			while (lineReadFromFile2 != null) {			
				System.out.println(++linecounter);
				String chrmomosome = newstrtok.nextToken();
				int chrStart = Integer.parseInt(newstrtok.nextToken());
				int chrStop = Integer.parseInt(newstrtok.nextToken());
				String geneFromFile2 = newstrtok.nextToken();
				
				Iterator<String> iter = startStopScore.keySet().iterator();
				while (iter.hasNext()) {
					index = 0;
					String key = iter.next();	
					List<Double> value = startStopScore.get(key);
					int size = 0;
					if (value.get(0).intValue() != 0) {
						size = value.size()/3 + 1;
					} else {
						size = value.size()/3;
					}
					double[][] startstopscorearray = new double[size][3];
					int j = 0;
					for (int i = 0; i < value.size(); i+=3) {
						if (i == 0 && value.get(i).intValue() != 0) {
							startstopscorearray[j][0] = 0;
							startstopscorearray[j][1] = value.get(i);
							startstopscorearray[j][2] = 0;
							j++;
						}
						startstopscorearray[j][0] = value.get(i);
						startstopscorearray[j][1] = value.get(i+1);
						startstopscorearray[j][2] = value.get(i+2);
						j++;
					}
					
//					int start = value.get(index*3).intValue();
//					int stop = value.get(index*3+1).intValue();
//					double score = value.get(index*3+2);
					if (key.compareTo(geneFromFile2) != 0) {
						index = 0;
					}

					if (key.compareTo(geneFromFile2) == 0) {
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
						while (i < relativebplengths.size() && j < value.size()) {
							if ((j+1)%3 == 0) {
								j++;
								continue;
							}
							int relbp = relativebplengths.get(i);
							int val = value.get(j).intValue();
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
								if (relativebplengths.get(index) == mergedK) {
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
							}
						}
//						System.out.println("done");
					}					
				}
				lineReadFromFile2 = newbuf.readLine();
				if (lineReadFromFile2 != null) {
					newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
				}				
			}
			bufWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
		