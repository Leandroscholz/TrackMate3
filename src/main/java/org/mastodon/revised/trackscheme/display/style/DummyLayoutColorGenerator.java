package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;

import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.ui.coloring.EdgeColorGenerator;
import org.mastodon.revised.ui.coloring.VertexColorGenerator;
import org.mastodon.revised.ui.util.ColorMap;

public class DummyLayoutColorGenerator implements VertexColorGenerator< TrackSchemeVertex >, EdgeColorGenerator< TrackSchemeEdge >
{

	private final TrackSchemeGraph< ?, ? > graph;

	ColorMap edgeColorMap = ColorMap.JET;

	ColorMap vertexColorMap = ColorMap.JET;


	public DummyLayoutColorGenerator( final TrackSchemeGraph< ?, ? > graph )
	{
		this.graph = graph;
	}

	@Override
	public Color color( final TrackSchemeEdge edge )
	{
		final double val = ( double ) edge.getInternalPoolIndex() / graph.edges().size();
		return edgeColorMap.get( val );
	}

	@Override
	public Color color( final TrackSchemeVertex vertex )
	{
		final double val = ( double ) vertex.getInternalPoolIndex() / graph.vertices().size();
		return vertexColorMap.get( val );
	}
}
