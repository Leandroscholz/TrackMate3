package org.mastodon.revised.bdv.overlay;

import java.awt.Color;

import org.mastodon.revised.ui.coloring.EdgeColorGenerator;

/**
 * Adapts an {@link EdgeColorGenerator} to a {@link OverlayEdgeColorGenerator}
 * by returning the same color provided by the {@link EdgeColorGenerator} in the
 * two methods of this interface.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <E> the type of the edges to color.
 */
public class OverlayEdgeColorGeneratorWrapper< E extends OverlayEdge< E, ? > > implements OverlayEdgeColorGenerator< E >
{

	private final EdgeColorGenerator< E > edgeColorGenerator;

	public OverlayEdgeColorGeneratorWrapper( final EdgeColorGenerator< E > edgeColorGenerator )
	{
		this.edgeColorGenerator = edgeColorGenerator;
	}

	@Override
	public Color color( final E edge )
	{
		return edgeColorGenerator.color( edge );
	}

	@Override
	public Color getColorAway( final E edge )
	{
		return edgeColorGenerator.color( edge );
	}

}
