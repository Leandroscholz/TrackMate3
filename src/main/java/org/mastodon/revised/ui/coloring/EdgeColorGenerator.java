package org.mastodon.revised.ui.coloring;

import java.awt.Color;

import org.mastodon.graph.Edge;

/**
 * Interface for object that can associate a color to an edge in a graph view.
 *
 * @author Jean-Yves Tinevez.
 *
 * @param <E>
 *            the type of the edges to color.
 */
public interface EdgeColorGenerator< E extends Edge< ? > >
{
	/**
	 * Gets the color for the specified edge.
	 *
	 * @param edge
	 *            the edge.
	 * @return a color. Is never <code>null</code>.
	 */
	public Color color( E edge );
}