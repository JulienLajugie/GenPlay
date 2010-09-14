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
		Map<String, List<Integer>> startStopScore = new HashMap<String, List<Integer>>();
		List<Integer> tempList = new ArrayList<Integer>();
		
		BufferedReader buf = new BufferedReader(new FileReader(args[0]));
		String lineRead = buf.readLine();
		StringTokenizer strtok;
		String prevGene = "";
		int prevStart = 0;
		int prevStop = 0;
		int prevScore = 0;
		if (lineRead != null) {
			strtok = new StringTokenizer(lineRead, "\t\n");
			prevGene = strtok.nextToken();
			prevStart = Integer.parseInt(strtok.nextToken());
			prevStop = Integer.parseInt(strtok.nextToken());
			prevScore = Integer.parseInt(strtok.nextToken());
			tempList.add(prevStart);
			tempList.add(prevStop);
			tempList.add(prevScore);
		}
		
		lineRead = buf.readLine();
		
		while (lineRead != null) {
			strtok = new StringTokenizer(lineRead, "\t\n");
			while (strtok.hasMoreTokens()) {
				String gene = strtok.nextToken();
				if (gene.compareTo(prevGene) == 0) {
					tempList.add(Integer.parseInt(strtok.nextToken()));
					tempList.add(Integer.parseInt(strtok.nextToken()));
					tempList.add(Integer.parseInt(strtok.nextToken()));
				} else {
					startStopScore.put(prevGene, tempList);
					tempList = new ArrayList<Integer>();
					tempList.add(Integer.parseInt(strtok.nextToken()));
					tempList.add(Integer.parseInt(strtok.nextToken()));
					tempList.add(Integer.parseInt(strtok.nextToken()));
					prevGene = gene;
				}
			}
			lineRead = buf.readLine();
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
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter("convertedFile.txt"));
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
				String key = iter.next();	
				List<Integer> value = startStopScore.get(key);
				int start = value.get(index*3);
				int stop = value.get(index*3+1);
				int score = value.get(index*3+2);
				if (key.compareTo(geneFromFile2) != 0) {
					key = iter.next();		
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
					
					String chromosomename = chrmomosome;
					int newstart = chrStart;
					int newstop = 0;
					double newscore = score;
					int bp = 0;
					i = 0;	
					int elseflag = 0;
					while (i < basePairs.size() && geneFromFile2.compareTo(key) == 0) {
						if (bp + basePairs.get(i) <= stop) {
							if (elseflag == 0) {
								newstart = chrStart + exonStarts.get(i);
								newstop = newstart + basePairs.get(i);
								newscore = score * (newstop - newstart) / basePairs.get(i);
							} else {
								newstart = newstop;
								newstop = newstart + bp;
								newscore = score * (newstop - newstart) / ((double) basePairs.get(i));
							}
							bufWriter.write(chromosomename+"\t"+newstart+"\t"+newstop+"\t"+newscore+"\n");
							chromosomename = chrmomosome;
							bp += basePairs.get(i);
							i++;								
						} else {
							elseflag = 1;
							index++;
							if (index < startStopScore.get(key).size()/3) {
								start = startStopScore.get(key).get(index*3);
								stop = startStopScore.get(key).get(index*3+1);
								score = startStopScore.get(key).get(index*3+2);
								bp = (start - bp) < (stop - start) ? (start - bp) : (stop - start);
							}								
						}							
					}
					break;
				}
			}
		}		
	}
}
