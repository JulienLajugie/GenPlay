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

public class NewReScorer {

	public static void main (String args[]) throws IOException{
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
			index = 0;

			Iterator<String> iter = startStopScore.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();	
				List<Double> value = startStopScore.get(key);
				int start = value.get(index*3).intValue();
				int stop = value.get(index*3+1).intValue();
				double score = value.get(index*3+2);
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
						exonStarts.add(Integer.parseInt(exonStartTok.nextToken()));					
					}
					
					if (geneFromFile2.compareTo("NM_000017") == 0) {
						System.out.println("in nm_000017");
					}
					
					List<Integer> annotationExonStartStops = new ArrayList<Integer>();
					int j = 0;
					for (i = 0, j = 0; i < 2*basePairs.size(); i++) {
						int relpos = exonStarts.get(j);
						if (i%2 == 0) {
							annotationExonStartStops.add(relpos + chrStart);
						} else {
							annotationExonStartStops.add(relpos + basePairs.get(j++) + chrStart);
						}
					}
					// now we have the annotation and coverage lists ready
					
					double[][] finalfilearray = new double[covStartStopScoreList.size()/3+2][3];
					int k = 0;
					int lastIndex = -3;
					int leftoverval = 0;
					int distanceBetweenTwoExons = 0;
					double leftoverscore = 0;
					int exonStart = 0;
					int exonStop = 0;
					boolean elseflag = false;
					for (i = 0; i < annotationExonStartStops.size() - 1; i+=2) {
//						if (i != 0) {
//							distanceBetweenTwoExons = annotationExonStartStops.get(i) - annotationExonStartStops.get(i-1);
//						}
						lastIndex += 3;
						exonStart = annotationExonStartStops.get(i);
						exonStop = annotationExonStartStops.get(i+1);
						
						for (j = lastIndex; j < covStartStopScoreList.size() - 2; j+=3) {
							double currStart = covStartStopScoreList.get(j);
							double currStop = covStartStopScoreList.get(j+1);
							if ((int)currStart + exonStart >= exonStart
									&& (int)currStop + exonStart - distanceBetweenTwoExons <= exonStop) {
								if (k < finalfilearray.length) {
									finalfilearray[k][0] = currStart + exonStart - distanceBetweenTwoExons;
									finalfilearray[k][1] = currStop + exonStart - distanceBetweenTwoExons;
									finalfilearray[k][2] = covStartStopScoreList.get(j+2);
									k++;
								}
							} else if ((int)currStop + exonStart - distanceBetweenTwoExons > exonStop) {
								elseflag = true;
								int counter = 1;
								double partScore = 0;
								while (counter <= (int)(currStop - currStart)
										&& counter + exonStart + (int)currStart - distanceBetweenTwoExons < exonStop) {
									counter++;
								}
								partScore = covStartStopScoreList.get(j+2) * counter / (currStop - currStart);
								if (k < finalfilearray.length) {
									finalfilearray[k][0] = currStart + exonStart - distanceBetweenTwoExons;
									finalfilearray[k][1] = currStart + counter + exonStart - distanceBetweenTwoExons;
									finalfilearray[k][2] = partScore;
									k++;
								}
								leftoverval = (int)currStop + exonStart - exonStop;
								leftoverscore = covStartStopScoreList.get(j+2) - partScore;
								double adjustpartsofscores = 0;
								// adjust the remaining part of the score in the other exon
								if (i < annotationExonStartStops.size() - 3 && annotationExonStartStops.get(i+3) - annotationExonStartStops.get(i+2) < (int)currStop - ((int)currStart+counter)) {
									while (i < annotationExonStartStops.size() - 3 && annotationExonStartStops.get(i+3) - annotationExonStartStops.get(i+2) < (int)currStop - ((int)currStart+counter) && k < finalfilearray.length) {
										finalfilearray[k][0] = annotationExonStartStops.get(i+2);
										finalfilearray[k][1] = annotationExonStartStops.get(i+3);
										adjustpartsofscores = covStartStopScoreList.get(j+2) * (annotationExonStartStops.get(i+3)-annotationExonStartStops.get(i+2)) / (currStop - currStart);
										finalfilearray[k][2] = adjustpartsofscores;
										k++;
										leftoverscore -= adjustpartsofscores;
										counter += annotationExonStartStops.get(i+3)-annotationExonStartStops.get(i+2);
										i+=2;
									}
									
								} 
								// this will adjust the last chunk of the score
								if (k < finalfilearray.length && i < annotationExonStartStops.size() - 2) {
									distanceBetweenTwoExons = (int)currStart + counter;
									finalfilearray[k][0] = annotationExonStartStops.get(i+2);
									finalfilearray[k][1] = annotationExonStartStops.get(i+2) + currStop - distanceBetweenTwoExons;
									finalfilearray[k][2] = leftoverscore;
									k++;
								}
								
								lastIndex = j;
								break;
							}
						}
					}					
//					System.out.println("1 NM done");
					for (i = 0; i < finalfilearray.length; i++) {
						if (finalfilearray[i][2] > 0.0)
							bufWriter.write(chrmomosome + "\t" + geneFromFile2 + "\t" + ((int)finalfilearray[i][0]) + "\t" + ((int)finalfilearray[i][1]) + "\t" + finalfilearray[i][2] + "\n");
					}
				}				
			}
			lineReadFromFile2 = newbuf.readLine();
			if (lineReadFromFile2 != null) {
				newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
			}
		}	
		bufWriter.close();
		double endTime = System.currentTimeMillis()/1000;
		System.out.println("Program Run Time: " + ((int)(endTime - startTime))/60 + " minute(s) and " + ((int)(endTime - startTime))%60 + " second(s)");
	}
}
