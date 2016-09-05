package org.mastodon.revised.bdv.overlay.wrap;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.bdv.overlay.OverlaySelection;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionListener;

public class OverlaySelectionWrapper< V extends Vertex< E >, E extends Edge< V > >
		implements OverlaySelection< OverlayVertexWrapper< V, E >, OverlayEdgeWrapper< V, E > >
{
	private final Selection< V, E > wrappedSelectionModel;

	public OverlaySelectionWrapper(	final Selection< V, E > selection )
	{
		this.wrappedSelectionModel = selection;
	}

	@Override
	public void setSelected( final OverlayVertexWrapper< V, E > vertex, final boolean selected )
	{
		wrappedSelectionModel.setSelected( vertex.wv, selected );
	}

	@Override
	public void setSelected( final OverlayEdgeWrapper< V, E > edge, final boolean selected )
	{
		wrappedSelectionModel.setSelected( edge.we, selected );
	}

	@Override
	public void toggleSelected( final OverlayVertexWrapper< V, E > vertex )
	{
		wrappedSelectionModel.toggle( vertex.wv );
	}

	@Override
	public void toggleSelected( final OverlayEdgeWrapper< V, E > edge )
	{
		wrappedSelectionModel.toggle( edge.we );
	}

	@Override
	public void clearSelection()
	{
		wrappedSelectionModel.clearSelection();
	}

	@Override
	public boolean addSelectionListener( final SelectionListener l )
	{
		return wrappedSelectionModel.addSelectionListener( l );
	}

	@Override
	public boolean removeSelectionListener( final SelectionListener l )
	{
		return wrappedSelectionModel.removeSelectionListener( l );
	}

	@Override
	public void pauseListeners()
	{
		wrappedSelectionModel.pauseListeners();
	}

	@Override
	public void resumeListeners()
	{
		wrappedSelectionModel.resumeListeners();
	}
}