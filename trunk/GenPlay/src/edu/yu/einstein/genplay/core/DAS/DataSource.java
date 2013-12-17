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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.DAS;


/**
 * A Data Source as described in the DAS 1.53 specifications:
 * <br/><a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 */
public class DataSource {
	private String description;	// descriptive information about the data source
	private String href;		// link to further human-readable information about the data source
	private String ID;			// symbolic name to use for further requests
	private String mapMaster;	// URL (site.specific.prefix/das/data_src) that is being annotated by this data source
	private String name;		// human-readable label which may or may not be different from the ID
	private String version;		// source version


	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}


	/**
	 * @return the href
	 */
	public final String getHref() {
		return href;
	}


	/**
	 * @return the iD
	 */
	public final String getID() {
		return ID;
	}


	/**
	 * @return the mapMaster
	 */
	public final String getMapMaster() {
		return mapMaster;
	}


	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}


	/**
	 * @return the version
	 */
	public final String getVersion() {
		return version;
	}


	/**
	 * @param description the description to set
	 */
	public final void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @param href the href to set
	 */
	public final void setHref(String href) {
		this.href = href;
	}


	/**
	 * @param iD the iD to set
	 */
	public final void setID(String iD) {
		ID = iD;
	}


	/**
	 * @param mapMaster the mapMaster to set
	 */
	public final void setMapMaster(String mapMaster) {
		this.mapMaster = mapMaster;
	}


	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}


	/**
	 * @param version the version to set
	 */
	public final void setVersion(String version) {
		this.version = version;
	}


	@Override
	public String toString() {
		return name;
	}
}
