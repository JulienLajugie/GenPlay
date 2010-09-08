package yu.einstein.gdp2.core.rescorer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReScorer {
	
	public static void main (String args[]) {
		try {
			BufferedReader buf = new BufferedReader(new FileReader(args[0]));
			String lineRead = buf.readLine();
			StringTokenizer strtok = new StringTokenizer(lineRead,"\t\n");
			while (lineRead != null) {
				String geneFromFile1 = strtok.nextToken();
				int start = Integer.parseInt(strtok.nextToken());
				int stop = Integer.parseInt(strtok.nextToken());
				int score = Integer.parseInt(strtok.nextToken());
				
				BufferedReader newbuf = new BufferedReader(new FileReader(args[1]));
				String lineReadFromFile2 = newbuf.readLine();
				StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
				String chrmomosome = newstrtok.nextToken();
				int chrStart = Integer.parseInt(newstrtok.nextToken());
				int chrStop = Integer.parseInt(newstrtok.nextToken());
				String geneFromFile2 = newstrtok.nextToken();
				
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
					 basePairs.add((Integer)basePairTok.nextElement());					
				}
				
				StringTokenizer exonStartTok = new StringTokenizer(exonStart,",");
				while (basePairTok.hasMoreElements()) {
					 exonStarts.add((Integer)exonStartTok.nextElement());					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}