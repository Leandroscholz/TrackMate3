package org.mastodon.revised.trackscheme;

import org.mastodon.revised.ui.selection.HighlightListener;
import org.mastodon.revised.ui.selection.HighlightModel;


public class TrackSchemeHighlight
{
	private final ModelHighlightProperties props;

	private final TrackSchemeGraph< ?, ? > graph;

	public TrackSchemeHighlight(
			final ModelHighlightProperties props,
			final TrackSchemeGraph< ?, ? > graph )
	{
		this.props = props;
		this.graph = graph;
	}

	/**
	 * Get the {@link TrackSchemeVertex} that is currently highlighted. Forwards
	 * to the model {@link HighlightModel}.
	 *
	 * @return currently highlighted vertex.
	 */
	public TrackSchemeVertex getHighlightedVertex( final TrackSchemeVertex ref )
	{
		return graph.getTrackSchemeVertexForModelId( props.getHighlightedVertexId(), ref );
	}

	/**
	 * Get the {@link TrackSchemeVertex} that is currently highlighted. Forwards
	 * to the model {@link HighlightModel}.
	 *
	 * @return currently highlighted vertex.
	 */
	public TrackSchemeEdge getHighlightedEdge( final TrackSchemeEdge ref )
	{
		return graph.getTrackSchemeEdgeForModelId( props.getHighlightedEdgeId(), ref );
	}

	/**
	 * highlight vertex.
	 *
	 * @param v
	 *            vertex to highlight, or {@code null} to clear highlight.
	 */
	// TODO: rename notifyHighlightVertex ?
	public void highlightVertex( final TrackSchemeVertex v )
	{
		props.highlightVertex( ( v == null ) ? -1 : v.getModelVertexId() );
	}

	/**
	 * highlight edge.
	 *
	 * @param e
	 *            edge to highlight, or {@code null} to clear highlight.
	 */
	public void highlightEdge( final TrackSchemeEdge e )
	{
		props.highlightEdge( ( e == null ) ? -1 : e.getModelEdgeId() );
	}

	/**
	 * clear highlight.
	 */
	public void clearHighlight()
	{
		props.clearHighlight();
	}

	public boolean addHighlightListener( final HighlightListener l )
	{
		return props.addHighlightListener( l );
	}

	public boolean removeHighlightListener( final HighlightListener l )
	{
		return props.removeHighlightListener( l );
	}
}