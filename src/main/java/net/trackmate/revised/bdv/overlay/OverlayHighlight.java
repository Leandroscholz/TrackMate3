package net.trackmate.revised.bdv.overlay;

import net.trackmate.revised.ui.selection.HighlightListener;

public interface OverlayHighlight< O extends OverlayVertex< O, E >, E extends OverlayEdge< E, ? > >
{
	public O getHighlightedVertex( O ref );

	public void highlightVertex( O vertex );

	public boolean addHighlightListener( final HighlightListener l );

	public boolean removeHighlightListener( final HighlightListener l );
}