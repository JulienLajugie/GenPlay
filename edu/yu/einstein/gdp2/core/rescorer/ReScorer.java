package yu.einstein.gdp2.core.rescorer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReScorer {

	public static void main (String args[]) {
		try {
			int linecounter = 0;
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter("C:\\Users\\Chirag\\Desktop\\newFile.txt"));
			BufferedReader newbuf = new BufferedReader(new FileReader(args[1]));
			String lineReadFromFile2 = newbuf.readLine();
			StringTokenizer newstrtok = new StringTokenizer(lineReadFromFile2,"\t\n");
			while (lineReadFromFile2 != null) {
				System.out.println(++linecounter);
				String chrmomosome = newstrtok.nextToken();
				int chrStart = Integer.parseInt(newstrtok.nextToken());
				int chrStop = Integer.parseInt(newstrtok.nextToken());
				String geneFromFile2 = newstrtok.nextToken();
								
				BufferedReader buf = new BufferedReader(new FileReader(args[0]));
				String lineRead = buf.readLine();
				
				StringTokenizer strtok = new StringTokenizer(lineRead,"\t\n");
				while (lineRead != null) {
					String geneFromFile1 = strtok.nextToken();
					int start = Integer.parseInt(strtok.nextToken());
					int stop = Integer.parseInt(strtok.nextToken());
					int score = Integer.parseInt(strtok.nextToken());

					if (geneFromFile1.compareTo(geneFromFile2) != 0) {
						lineRead = buf.readLine();
						if (lineRead != null) {
							strtok = new StringTokenizer(lineRead,"\t\n");
						}
						continue;
					}

					if (geneFromFile1.compareTo(geneFromFile2) == 0) {
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
						while (i < basePairs.size() && geneFromFile2.compareTo(geneFromFile1) == 0) {
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
								lineRead = buf.readLine();
								if (lineRead != null) {
									strtok = new StringTokenizer(lineRead,"\t\n");
									geneFromFile1 = strtok.nextToken();
									start = Integer.parseInt(strtok.nextToken());
									stop = Integer.parseInt(strtok.nextToken());
									score = Integer.parseInt(strtok.nextToken());
									bp = (start - bp) < (stop - start) ? (start - bp) : (stop - start);
								}								
							}							
						}
						break;
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