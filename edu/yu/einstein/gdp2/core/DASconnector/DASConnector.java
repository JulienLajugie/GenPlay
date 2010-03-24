/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DASconnector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.util.ChromosomeManager;


/**
 * Provides tools to connect and retrieve data from a DAS server
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASConnector {

	private final String serverAddress;	// address of a DAS Server

	
	/**
	 * Creates an instance of {@link DASConnector} 
	 * @param serverAddress address of a DAS server
	 */
	public DASConnector(String serverAddress) {
		// get rid of spaces if there is some
		serverAddress = serverAddress.trim();
		// add a "/" at the end of the address if there is none
		if (!serverAddress.substring(serverAddress.length() - 1).equals("/")) {
			serverAddress += "/";
		}
		this.serverAddress = serverAddress;
	}


	/**
	 * Retrieves a list of Data Sources from the DAS server
	 * @return a List of {@link DataSource}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<DataSource> getDataSourceList() throws IOException, ParserConfigurationException, SAXException {
		URL dsnURL = new URL(serverAddress + "dsn/");
		URLConnection connection = dsnURL.openConnection();
		connection.setUseCaches(true);		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DataSourceHandler dsh = new DataSourceHandler();
		connection.getInputStream();
		parser.parse(connection.getInputStream(), dsh);		
		return dsh.getDataSourceList();
	}


	/**
	 * Retrieves a list of DAS types for a specified data Source 
	 * @param dataSource a {@link DataSource}
	 * @return a List of {@link DataSource}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<DASType> getDASTypeList(DataSource dataSource) throws IOException, ParserConfigurationException, SAXException {
		URL dasTypesURL = new URL(serverAddress + dataSource.getID() + "/types");
		URLConnection connection = dasTypesURL.openConnection();
		connection.setUseCaches(true);		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DASTypeHandler dth = new DASTypeHandler();
		parser.parse(connection.getInputStream(), dth);
		return dth.getDasTypeList();
	}


	/**
	 * Retrieves a list of DAS entry points for a specified data Source 
	 * @param dataSource a {@link DataSource}
	 * @return a List of {@link EntryPoint}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<EntryPoint> getEntryPointList(DataSource dataSource) throws IOException, ParserConfigurationException, SAXException {
		URL entryPointURL = new URL(serverAddress + dataSource.getID() + "/entry_points");
		URLConnection connection = entryPointURL.openConnection();
		connection.setUseCaches(true);		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		EntryPointHandler eph = new EntryPointHandler();
		parser.parse(connection.getInputStream(), eph);
		return eph.getEntryPointList();
	}


	/**
	 * Retrieves a list of genes from a specified Data Source and a specified DAS Type 
	 * @param cm a {@link ChromosomeManager}
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @return a {@link GeneList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public GeneList getGeneList(ChromosomeManager cm, DataSource dataSource, DASType dasType) throws IOException, ParserConfigurationException, SAXException {
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		GeneList resultList = new GeneList(cm);
		for (Chromosome currentChromo: cm) {
			EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
			// if we found a chromosome retrieve the data and 
			// we create a genelist for this chromosome
			if (currentEntryPoint != null) {
				URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType);				
				URLConnection connection = queryUrl.openConnection();
				connection.setUseCaches(true);		
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setValidating(true);
				SAXParser parser = parserFactory.newSAXParser();
				GeneHandler gh = new GeneHandler(currentChromo);				
				parser.parse(connection.getInputStream(), gh);
				List<Gene> currentGeneList = gh.getGeneList();
				resultList.add(currentGeneList);
			} else {
				resultList.add(null);
			}				
		}
		boolean areExonsScored = false;
		for (List<Gene> currentList: resultList) {
			Collections.sort(currentList);
			// check if the exons are scored
			int i = 0;
			while (!areExonsScored && (i < currentList.size())) {
				int j = 0;
				double[] exonScores = currentList.get(i).getExonScores(); 
				while (!areExonsScored && (j < exonScores.length)) {
					if (exonScores[j] != 0) {
						areExonsScored = true;
					}
					j++;
				}
				i++;
			}
		}
		// if the exons are not scored we set the exonScore field of every gene to null 
		if (!areExonsScored) {
			for (List<Gene> currentList: resultList) {
				for (Gene currentGene: currentList) {
					currentGene.setExonScores(null);
				}
			}
		}
		return resultList;
	}


	/**
	 * Retrieves a list of ScoredChromosomeWindow from a specified Data Source and a specified DAS Type 
	 * @param cm a {@link ChromosomeManager}
	 * @param dataSource a {@link DataSource}
	 * @param dasType a {@link DASType}
	 * @return a {@link ScoredChromosomeWindowList}
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public ScoredChromosomeWindowList getSCWList(ChromosomeManager cm, DataSource dataSource, DASType dasType) throws IOException, ParserConfigurationException, SAXException {
		List<EntryPoint> entryPointList = getEntryPointList(dataSource);
		ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(cm);
		for (Chromosome currentChromo: cm) {
			EntryPoint currentEntryPoint = findEntryPoint(entryPointList, currentChromo);
			// if we found a chromosome retrieve the data and 
			// we create a genelist for this chromosome
			if (currentEntryPoint != null) {
				URL queryUrl = generateQuery(dataSource, currentEntryPoint, dasType);				
				URLConnection connection = queryUrl.openConnection();
				connection.setUseCaches(true);		
				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setValidating(true);
				SAXParser parser = parserFactory.newSAXParser();
				SCWHandler scwh = new SCWHandler();				
				parser.parse(connection.getInputStream(), scwh);
				List<ScoredChromosomeWindow> currentSCWList = scwh.getScoreChromosomeWindowList();
				resultList.add(currentSCWList);
			} else {
				resultList.add(null);
			}				
		}
		for (List<ScoredChromosomeWindow> currentList: resultList) {
			Collections.sort(currentList);
		}
		return resultList;
	}


	/**
	 * Searches if there is an entry point associated to the specify chromosome in the list of entries
	 * @param chr a {@link Chromosome}
	 * @return the name of the entry. Null if none
	 */
	private EntryPoint findEntryPoint(List<EntryPoint> entryPointList, Chromosome chr) {
		String currentName = chr.getName();
		// we remove the "chr" if the name of the chromosome starts with that
		// so "chr1" becomes "1"
		if (currentName.trim().substring(0, 3).equals("chr")) {
			currentName = currentName.trim().substring(3); 
		}
		boolean found = false;
		int i = 0;
		// we search for an entry point corresponding to the current chromosome
		while ((i < entryPointList.size()) && (!found)) {
			if (entryPointList.get(i).getID().equalsIgnoreCase(currentName)) {
				found = true;
			} else {
				i++;
			}
		}
		if (found) {
			// if the stop position of the entry point can't be greater than the one of the chromosome
			EntryPoint resultEntry = entryPointList.get(i);
			if (resultEntry.getStop() > chr.getLength()) {
				resultEntry.setStop(chr.getLength());
			}
			return resultEntry;
		} else {
			return null;
		}
	}


	/**
	 * Generates a query for all the data for a specified data source, entry point and das type 
	 * @param dataSource a {@link DataSource}
	 * @param entryPoint an {@link EntryPoint}
	 * @param dasType a {@link DASType}
	 * @return a {@link URL} containing the query
	 * @throws MalformedURLException
	 */
	private URL generateQuery(DataSource dataSource, EntryPoint entryPoint, DASType dasType) throws MalformedURLException {
		String URLStr = new String(serverAddress); 
		URLStr += dataSource.getID();
		URLStr += "/features?segment=";
		URLStr += entryPoint.getID();
		URLStr += ":";
		URLStr += entryPoint.getStart();
		URLStr += ",";
		URLStr += entryPoint.getStop();
		URLStr += ";type=";
		URLStr += dasType.getID();
		return new URL(URLStr);
	}


//	public static void main(String[] args) {
//		try {
//			long startTime = System.currentTimeMillis();
//			DASConnector dasc = new DASConnector("http://genome.ucsc.edu/cgi-bin/das/");
//			//DASConnector dasc = new DASConnector("http://www.ensembl.org/das/");
//			List<DataSource> dsList = dasc.getDataSourceList();
//			DataSource dataSource = dsList.get(0);
//			System.out.println(dataSource.getID());
//			List<DASType> dasTypeList = dasc.getDASTypeList(dataSource);
//			DASType dasType = dasTypeList.get(39);
//			System.out.println(dasType.getID());
//			//ScoredChromosomeWindowList scwList = dasc.getSCWList(ChromosomeManager.getInstance(), dataSource, dasType);
//			GeneList geneList = dasc.getGeneList(ChromosomeManager.getInstance(), dataSource, dasType);
//			GeneListAsBedWriter glabw = new GeneListAsBedWriter(ChromosomeManager.getInstance(), new File("testDAS.bed"), geneList, "test");
//			glabw.write();
//			int length = (int)((System.currentTimeMillis() - startTime) / 1000l);
//			System.out.println(length);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}	
}
