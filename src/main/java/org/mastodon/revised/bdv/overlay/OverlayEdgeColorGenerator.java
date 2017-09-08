package org.mastodon.revised.bdv.overlay;

import java.awt.Color;

import org.mastodon.revised.ui.coloring.EdgeColorGenerator;

/**
 * Edge color interface suitable to be used in the {@link OverlayGraphRenderer}.
 * Since the edge are colored using two colors to signify fading in time, this
 * interface has two methods to return two colors.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <E> the type of the edges to color.
 */
public interface OverlayEdgeColorGenerator< E extends OverlayEdge< E, ? > > extends EdgeColorGenerator< E >
{

	/**
	 * Gets the color to for the specified edge as it fades away in time.
	 *
	 * @param edge
	 *            the edge.
	 * @return a color. Is never <code>null</code>.
	 */
	public Color getColorAway( E edge );

}
