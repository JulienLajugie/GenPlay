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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.duplicateReadFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class DupReadFilterv2 {
	private int maxDupCount;
	
//	/**
//	 * private method to sort the file
//	 * @throws IOException
//	 */
//	private void sortRepositionedFile(File outputFile) throws IOException {
//		BufferedReader buf = new BufferedReader(new FileReader(outputFile));
//		List<BedFileSorter> file = new ArrayList<BedFileSorter>();
//		String lineRead = buf.readLine();
//		StringTokenizer strtok = new StringTokenizer(lineRead,"\t\n");
//		while (lineRead != null) {
//			String readName = strtok.nextToken();			// read name
//			String chrName = strtok.nextToken();
//			int start = Integer.parseInt(strtok.nextToken());
//			int stop = Integer.parseInt(strtok.nextToken());
//			strtok.nextToken();			// unused field denoted by dash
//			double score = Double.parseDouble(strtok.nextToken());
//			String strand = strtok.nextToken();
//			BedFileSorter fileSorter = new BedFileSorter(readName, chrName, start, stop, score, strand);
//			file.add(fileSorter);
//			lineRead = buf.readLine();
//			if (lineRead != null) {
//				strtok = new StringTokenizer(lineRead,"\t\n");
//			}
//		}			
//		
//		Collections.sort(file);
//		// write the sorted data to the file		
//		//String sortedFile = outputFile.getParent() + "sorted" + outputFile.getName();
//		//BufferedWriter bufWriter = new BufferedWriter(new FileWriter(sortedFile));
//		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
//		Iterator<BedFileSorter> iter = file.iterator();
//		while (iter.hasNext()) {
//			BedFileSorter fs = iter.next();
//			bufWriter.write(fs.getChromosomeName() + "\t" + fs.getStart() + "\t" + fs.getStop() + "\t-" + "\t" + fs.getScore() + "\t" + fs.getStrand() + "\n");
//		}
//		bufWriter.close();		
//	}		
	
	public static void main (String args[]) throws IOException {
		DupReadFilterv2 drf = new DupReadFilterv2();
		if (args.length != 2) {
			System.out.println("Usage: " + "java -jar <inputfile> <outputfile>");
			System.exit(0);
		}
		Scanner in = new Scanner(System.in);
		System.out.println("\nEnter the maximum number of duplicate reads to be allowed i.e. a positive number");
		String input = in.nextLine();
		
		try {
			drf.maxDupCount = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.err.println("Invalid input " + "\"" + input + "\"");
			System.exit(0);
		} 
		
		
		BufferedReader bufReader = new BufferedReader(new FileReader(args[0]));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(args[1]));
		Map<String, List<ReadsData>> mapForInputFile = new HashMap<String, List<ReadsData>>();
		
		String lineRead = bufReader.readLine();
		StringTokenizer strTok;
		//int lineNumber = 0;
		
		System.out.println("Writing to output file...");
		while (lineRead != null) {
			if (lineRead.length() != 0) {
				//System.out.println(lineNumber++);
				strTok = new StringTokenizer(lineRead, "\t\n");
				String readName = strTok.nextToken();
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
				
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);				
				
				if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() < drf.maxDupCount) {
					mapForInputFile.get(readName).add(rd);
				} else if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() == drf.maxDupCount) {
					mapForInputFile.clear();
				} else {					
					Iterator<String> iter = mapForInputFile.keySet().iterator();
					while (iter.hasNext()) {
						String readNameToPrint = iter.next();
						double score = 1;
						List<ReadsData> rdList = mapForInputFile.get(readNameToPrint);
						score /= rdList.size();
						for (int i = 0; i < rdList.size(); i++) {
							ReadsData rdFromMap = rdList.get(i);
							// for debug purposes readName is also written to the file
							//bufWriter.write(readName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
							bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
						}						
					}
					mapForInputFile.clear();
					List<ReadsData> readsData = new ArrayList<ReadsData>();
					readsData.add(rd);
					mapForInputFile.put(readName, readsData);
				}								
			}
			lineRead = bufReader.readLine();			
		}
		bufWriter.close();	
	}	
}
