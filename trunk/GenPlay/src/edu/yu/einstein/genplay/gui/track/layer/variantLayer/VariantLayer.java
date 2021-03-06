/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer.variantLayer;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.VariantLayerDisplaySettings;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.AbstractLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;

/**
 * @author Nicolas Fourel
 */
public class VariantLayer extends AbstractLayer<VariantLayerDisplaySettings> implements MouseListener, MouseMotionListener {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -7054772225249273886L;

	private final MultiGenomeDrawer genomeDrawer;
	private List<MGFilter> filters;


	/**
	 * Constructor of {@link VariantLayer}
	 * @param track the track which the layer belongs to
	 */
	public VariantLayer (Track track) {
		super(track, null);
		genomeDrawer = new MultiGenomeDrawer();
		setData(null, null);
	}


	/**
	 * Creates an instance of {@link VariantLayer} with the same properties as the specified {@link VariantLayer}.
	 * The copy of the data is shallow.
	 * @param variantLayer
	 */
	private VariantLayer(VariantLayer variantLayer) {
		super(variantLayer);
		genomeDrawer = variantLayer.genomeDrawer;
		filters = variantLayer.filters;
	}


	@Override
	public VariantLayer clone() {
		return new VariantLayer(this);
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			genomeDrawer.drawMultiGenomeInformation(g, width, height);
		}
	}


	/**
	 * @return the filters
	 */
	public List<MGFilter> getFilters() {
		return filters;
	}


	/**
	 * @return the {@link MultiGenomeDrawer}
	 */
	public MultiGenomeDrawer getGenomeDrawer () {
		return genomeDrawer;
	}


	@Override
	public String getName() {
		if ((super.getName() == null) || (super.getName().isEmpty())) {
			return getData().getDescription();
		} else {
			return super.getName();
		}
	}


	@Override
	public LayerType getType() {
		return LayerType.VARIANT_LAYER;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getButton() == MouseEvent.BUTTON3)) {
			genomeDrawer.showVariantInformationDialog(getTrack().getHeight(), e);
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {
		if (!ScrollingManager.getInstance().isScrollingEnabled() && (genomeDrawer != null) && genomeDrawer.hasToBeRepaintAfterExit()) {
			getTrack().repaint();
		}
	}


	// MouseListener implementation

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!ScrollingManager.getInstance().isScrollingEnabled() && (genomeDrawer != null) && genomeDrawer.isOverVariant(getTrack().getHeight(), e)) {
			getTrack().repaint();
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * @param filters the list of {@link MGFilter}
	 */
	public void setData (List<MGFilter> filters) {
		setData(getData(), filters);
	}


	/**
	 * @param data the {@link VariantLayerDisplaySettings}
	 * @param filters the list of {@link MGFilter}
	 */
	private void setData (VariantLayerDisplaySettings data, List<MGFilter> filters) {
		if ((getData() != null) && getData().getDescription().equals(getName())) {
			setName(null);
		}
		this.filters = filters;
		super.setData(data);
		updateMultiGenomeInformation();
	}


	// MouseMotion implementation

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		updateMultiGenomeInformation();
	}


	/**
	 * Updates the multi genome information
	 */
	private void updateMultiGenomeInformation () {
		if (isVisible()) {
			List<VariantLayerDisplaySettings> variantDataList = new ArrayList<VariantLayerDisplaySettings>();
			variantDataList.add(getData());
			genomeDrawer.updateMultiGenomeInformation(variantDataList, filters);
		}
	}
}
