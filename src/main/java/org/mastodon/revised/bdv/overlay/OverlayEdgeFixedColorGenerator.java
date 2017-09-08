package org.mastodon.revised.bdv.overlay;

import java.awt.Color;

/**
 * Specialized fixed color generator for edges painted with the
 * OverlayGraphRenderer, that interpolates edge color from one to another as
 * they fade in time.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <E>
 *            the type of edges painted in the renderer.
 */
public class OverlayEdgeFixedColorGenerator< E extends OverlayEdge< E, ? > > implements OverlayEdgeColorGenerator< E >
{

	private final Color color;

	private final Color colorAway;

	/**
	 * Creates a new fixed edge color generator. The colors returned by this
	 * edge color generator will be the same for all edges.
	 *
	 * @param color
	 *            the color to paint edges when they are close in time.
	 * @param colorAway
	 *            the color to interpolate edge color with, when they fade in
	 *            time.
	 */
	public OverlayEdgeFixedColorGenerator( final Color color, final Color colorAway )
	{
		this.color = color;
		this.colorAway = colorAway;
	}

	@Override
	public Color color( final E edge )
	{
		return color;
	}

	@Override
	public Color getColorAway( final E edge )
	{
		return colorAway;
	}
}
