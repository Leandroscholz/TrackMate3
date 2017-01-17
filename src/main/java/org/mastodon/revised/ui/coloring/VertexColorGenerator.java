package org.mastodon.revised.ui.coloring;

import java.awt.Color;

import org.mastodon.graph.Vertex;

/**
 * Interface for object that can associate a color to a vertex in a graph view.
 *
 * @author Jean-Yves Tinevez.
 * @param <V>
 *            the type of vertices to color.
 *
 */
public interface VertexColorGenerator< V extends Vertex< ? > >
{
	/**
	 * Gets the color for the specified vertex.
	 * 
	 * @param vertex
	 *            the vertex.
	 * @return a color. Is never <code>null</code>.
	 */
	public Color color( V vertex );
}