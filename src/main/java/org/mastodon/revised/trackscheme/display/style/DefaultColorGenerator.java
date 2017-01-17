package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.ui.coloring.EdgeColorGenerator;
import org.mastodon.revised.ui.coloring.VertexColorGenerator;

public class DefaultColorGenerator implements VertexColorGenerator< TrackSchemeVertex >, EdgeColorGenerator< TrackSchemeEdge >
{

	private final Color vertexColor;

	private final Color edgeColor;

	public DefaultColorGenerator()
	{
		vertexColor = TrackSchemeStyle.defaultStyle().getVertexFillColor();
		edgeColor = TrackSchemeStyle.defaultStyle().getEdgeColor();
	}

	@Override
	public final Color color( final TrackSchemeEdge edge )
	{
		return vertexColor;
	}

	@Override
	public final Color color( final TrackSchemeVertex vertex )
	{
		return edgeColor;
	}

}
