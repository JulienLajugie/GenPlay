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

public class ReScorerNew {
	public static void main (String args[]) throws IOException {
		double startTime = System.currentTimeMillis()/1000;
		Map<String, List<Double>> startStopScore = new HashMap<String, List<Double>>();
		List<Double> tempList = new ArrayList<Double>();

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
			tempList.add(prevStart);
			tempList.add(prevStop);
			tempList.add(prevScore);
		}

		lineRead = buf.readLine();
        int annotationlines = 0;
		while (lineRead != null) {
			strtok = new StringTokenizer(lineRead, "\t\n");
			while (strtok.hasMoreTokens()) {
				String gene = strtok.nextToken();
				if (gene.compareTo(prevGene) == 0) {
					tempList.add(Double.parseDouble(strtok.nextToken()));
					tempList.add(Double.parseDouble(strtok.nextToken()));
					tempList.add(Double.parseDouble(strtok.nextToken()));
				} else {
					annotationlines = 1;
					startStopScore.put(prevGene, tempList);
					tempList = new ArrayList<Double>();
					tempList.add(Double.parseDouble(strtok.nextToken()));
					tempList.add(Double.parseDouble(strtok.nextToken()));
					tempList.add(Double.parseDouble(strtok.nextToken()));
					prevGene = gene;
				}
			}
			lineRead = buf.readLine();
		}
		if (annotationlines == 0) { // there is just one line
			startStopScore.put(prevGene, tempList);
		}
		//		System.out.println("Printing Map");
		//		Iterator<String> iter = startStopScore.keySet().iterator();
		//		while (iter.hasNext()) {
		//			String key = iter.next();
		//			List<Integer> value = startStopScore.get(key);
		//			System.out.println(key);
		//			for (int i = 0; i < value.size()/3; i++)
		//				System.out.println(value.get(i*3) + "\t" + value.get(i*3+1) + "\t" + value.get(i*3+2) + "\n");
		//		}
		//now we have the populated hashmap containing start stop and scores

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

					String basePair = newstrtok.nextToken();
					String exonStart = newstrtok.nextToken();

					List<Integer> basePairs = new ArrayList<Integer>();
					List<Integer> exonStarts = new ArrayList<Integer>();

					StringTokenizer basePairTok = new StringTokenizer(basePair,",");
					while (basePairTok.hasMoreElements()) {
						basePairs.add(Integer.parseInt(basePairTok.nextToken()));					
					}

					StringTokenizer exonStartTok = new StringTokenizer(exonStart,",");
					while (exonStartTok.hasMoreElements()) {
						exonStarts.add(Integer.parseInt(exonStartTok.nextToken()));					
					}
					
					if (geneFromFile2.compareTo("NM_000017") == 0) {
						System.out.println("in nm_000017");
					}
					
					List<Integer> absoluteExonList = new ArrayList<Integer>();
					for (i = 0; i < exonStarts.size(); ++i) {
						absoluteExonList.add(exonStarts.get(i) + chrStart);
						absoluteExonList.add(exonStarts.get(i) + basePairs.get(i) + chrStart);
					}
					
					List<Integer> basePairLengths = new ArrayList<Integer>();
					basePairLengths.add(basePairs.get(0));
					for (i = 1; i < basePairs.size(); ++i) {
						basePairLengths.add(basePairs.get(i) + basePairLengths.get(i-1));						
					}
					
					List<Integer> startStopPos = new ArrayList<Integer>();
					List<Double> startstopscore = startStopScore.get(key);
					int j = 1;
					for (i = 0; i < startstopscore.size(); ++i) {
						if (j % 3 != 0) {
							startStopPos.add(startstopscore.get(i).intValue());							
						}
						j++;
					}
					
					List<Integer> onlyStarts = new ArrayList<Integer>();
					onlyStarts.add(startstopscore.get(0).intValue());
					for (i = 0; i < basePairLengths.size(); i++) {
						int pos = basePairLengths.get(i);
						onlyStarts.add(pos);
						onlyStarts.add(pos+1);
					}
					
					List<Integer> mergedList = new ArrayList<Integer>();
					i = 0;
					j = 0;
					while (i < basePairLengths.size() && j < startStopPos.size()) {
						if (basePairLengths.get(i) < startStopPos.get(j)) {
							mergedList.add(basePairLengths.get(i));
							i++;
						} else {
							mergedList.add(startStopPos.get(j));
							j++;
						}
					}
					
					while (i < basePairLengths.size()) {
						mergedList.add(basePairLengths.get(i));
						i++;
					}
					
					while (j < startStopPos.size()) {
						mergedList.add(startStopPos.get(j));
						j++;
					}

					List<Integer> finalStartStop = new ArrayList<Integer>();
					finalStartStop.add(mergedList.get(0));
					for (i = 1; i < mergedList.size(); i++) {
						int retVal = mergedList.get(i);
						finalStartStop.add(retVal);
						if (i != mergedList.size()-1) {
							finalStartStop.add(retVal+1);
						}
					}
					
					List<Double> finalScores = new ArrayList<Double>();
					j = 0;
					i = 0;
					while (j < value.size()/3 - 1) {
						while (i < finalStartStop.size() && finalStartStop.get(i) >= value.get(j*3) && finalStartStop.get(i) <= value.get(j*3+1)) {
							finalScores.add(value.get(j*3+2));
							i+=2;
						}
						while (i < finalStartStop.size() && finalStartStop.get(i) >= value.get(j*3+1) && finalStartStop.get(i) <= value.get(j*3+3)) {
							finalScores.add(new Double(0));
							i+=2;
						}
						j++;
					}	
					
					while (j < value.size()/3) {
						while (i < finalStartStop.size() && finalStartStop.get(i) >= value.get(j*3) && finalStartStop.get(i) <= value.get(j*3+1)) {
							finalScores.add(value.get(j*3+2));
							i+=2;
						}
						j++;
					}
					
					double[][] arrayofstartstopscore = new double[3][finalStartStop.size()/2];
					int stopstartdiffval = 0;
					int k = 0;
					for (i = 0, j = 0; i < finalStartStop.size() - 1; i+=2, j++) {
						// check these conditions...edited on sept 20 around 4.55 pm...before this I dint have any ifs
//						if (k < startStopPos.size() && startStopPos.size() != 1) {							
//							if (basePairLengths.contains(finalStartStop.get(i+1)) != false || (!((finalStartStop.get(i) > startStopPos.get(2*k+1)) && finalStartStop.get(i+1) < startStopPos.get(2*k+2)))) {
								arrayofstartstopscore[0][j] = finalStartStop.get(i);
								arrayofstartstopscore[1][j] = finalStartStop.get(i+1);
								if (j < finalScores.size()) {
									arrayofstartstopscore[2][j] = finalScores.get(j).intValue();
								} else {
									arrayofstartstopscore[2][j] = 0;
								}
//							}
//						}
					}
					
					
					List<Double> weightedScores = new ArrayList<Double>();
					j = 0;
					for (i = 0; i < basePairLengths.size(); i++) {
						int stopval = basePairLengths.get(i);
						double newScore = 0;
						k = j;
						while (j < arrayofstartstopscore[1].length && arrayofstartstopscore[1][j] <= stopval) {
							j++;
						}
						int startval = (int)arrayofstartstopscore[0][k];
						while (k < j) {
							newScore += arrayofstartstopscore[2][k] * (arrayofstartstopscore[1][k] - arrayofstartstopscore[0][k]);
							k++;
						}	
						if (k != 0) {
							newScore /= (arrayofstartstopscore[1][k-1] - startval);
						}
						weightedScores.add(newScore);
					}
					
					List<Integer> prunedStartStops = new ArrayList<Integer>();
					List<Double> prunedScores = new ArrayList<Double>();
					j = 0;
					for (i = 0; i < finalStartStop.size(); i+=2) {
						if (j < finalScores.size() && finalScores.get(j).intValue() > 0) {
							prunedStartStops.add(finalStartStop.get(i)+ chrStart);
							prunedStartStops.add(finalStartStop.get(i+1)+ chrStart);
							prunedScores.add(finalScores.get(j));
						}
						j++;
					}
					System.out.println("done");
					String chromosomename = chrmomosome;
					for (i = 0; i < arrayofstartstopscore[0].length; i++) {
						double scoretobewritten = 0;
						if (i < weightedScores.size()) {
							scoretobewritten = weightedScores.get(i);
						}
						bufWriter.write(chromosomename + "\t" + geneFromFile2 + "\t" + ((int)arrayofstartstopscore[0][i]+chrStart) + "\t" + ((int)arrayofstartstopscore[1][i]+chrStart) + "\t" + scoretobewritten + "\n");						
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