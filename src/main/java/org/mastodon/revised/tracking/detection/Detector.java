package org.mastodon.revised.tracking.detection;

import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.DoublePropertyMap;
import org.mastodon.revised.model.feature.Feature;
import org.scijava.Cancelable;
import org.scijava.plugin.SciJavaPlugin;

import bdv.spimdata.SpimDataMinimal;

public interface Detector< V extends Vertex< ? > > extends Cancelable, HasErrorMessage, SciJavaPlugin
{

	/**
	 * Returns the quality feature calculated by this detector.
	 * <p>
	 * The quality feature is defined for all vertices created by the last call
	 * to the detector and only them. By convention, quality values are real
	 * positive <code>double</code>s, with large values indicating higher
	 * confidence in the detection result.
	 *
	 * @return the spot quality feature.
	 */
	public Feature< V, DoublePropertyMap< V > > getQualityFeature();

	/**
	 * Executes detection.
	 * 
	 * @param graph
	 *            the graph to feed with detection results. The detection will
	 *            be added as vertices of the graph.
	 * @param spimData
	 *            the image data.
	 */
	public void detect( final Graph< V, ? > graph, final SpimDataMinimal spimData );

}
