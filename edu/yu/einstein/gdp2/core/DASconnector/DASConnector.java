package yu.einstein.gdp2.core.DASconnector;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class DASConnector {
	
	private final String serverAddress;
	
	private List<DataSource> dataSourceList = null;
	private boolean dataSourceListRetrieved = false; 
	
	private List<DASType> 	dasTypeList = null;
	private boolean dasTypeListRetrieved = false;
	
	private DataSource selectedDataSource = null;

	
	
	public DASConnector(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	
	private void retrieveDataSourceList() throws IOException, ParserConfigurationException, SAXException {
		URL dsnURL = new URL(serverAddress + "dsn/");
		URLConnection connection = dsnURL.openConnection();
		connection.setUseCaches(true);		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		SAXParser parser = parserFactory.newSAXParser();
		DataSourceHandler dsh = new DataSourceHandler();
		parser.parse(connection.getInputStream(), dsh);
		dataSourceList = dsh.getDataSourceList();
		dataSourceListRetrieved = true;
	}

	
	public List<DataSource> getDataSourceList() throws IOException, ParserConfigurationException, SAXException {
		if (!dataSourceListRetrieved) {
			retrieveDataSourceList();
		}
		return dataSourceList;
	}
	
	
	public void selectDataSource(DataSource dataSource) {
		selectedDataSource = dataSource;
	}
	
	
	private void retrieveDasTypeList() throws IOException, ParserConfigurationException, SAXException {
		if (selectedDataSource != null) {
			URL dasTypesURL = new URL(serverAddress + selectedDataSource.getID() + "/types");
			URLConnection connection = dasTypesURL.openConnection();
			connection.setUseCaches(true);		
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			SAXParser parser = parserFactory.newSAXParser();
			DASTypeHandler dth = new DASTypeHandler();
			parser.parse(connection.getInputStream(), dth);
			dasTypeList = dth.getDasTypeList();
			dasTypeListRetrieved = true;
		}		
	}
	
	
	public List<DASType> getDASTypeList() throws IOException, ParserConfigurationException, SAXException {
		if (!dasTypeListRetrieved) {
			retrieveDasTypeList();
		}
		return dasTypeList;
	}
	
	public static void main(String[] args) {
		try {
			DASConnector dasc = new DASConnector("http://genome.ucsc.edu/cgi-bin/das/");
			List<DataSource> dsList = dasc.getDataSourceList();
			for (DataSource currentDS: dsList) {
				System.out.println(currentDS.getDescription());
			}
			dasc.selectDataSource(dsList.get(0));
			List<DASType> dasTypeList = dasc.getDASTypeList();
			for (DASType currentDASType: dasTypeList) {
				System.out.println(currentDASType.getID() + " -> " + currentDASType.getCategory());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
