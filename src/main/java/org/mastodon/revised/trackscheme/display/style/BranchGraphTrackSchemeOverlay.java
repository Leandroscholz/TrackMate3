package org.mastodon.revised.trackscheme.display.style;

import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.APPEAR;
import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.DISAPPEAR;

import java.awt.Color;
import java.awt.Graphics2D;

import org.mastodon.revised.trackscheme.ScreenEdge;
import org.mastodon.revised.trackscheme.ScreenVertex;
import org.mastodon.revised.trackscheme.ScreenVertex.Transition;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.AbstractTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.util.GeometryUtils;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightModel;

/**
 * A specialized TrackScheme overlay derived from the default, stylized one,
 * that makes edges corner connectors.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class BranchGraphTrackSchemeOverlay extends DefaultTrackSchemeOverlay
{

	public BranchGraphTrackSchemeOverlay( final TrackSchemeGraph< ?, ? > graph, final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight, final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus, final TrackSchemeStyle style )
	{
		super( graph, highlight, focus, style );
	}

	@Override
	public void drawEdge( final Graphics2D g2, final ScreenEdge edge, final ScreenVertex vs, final ScreenVertex vt )
	{
		Transition transition = edge.getTransition();
		double ratio = edge.getInterpolationCompletionRatio();
		if ( vt.getTransition() == APPEAR )
		{
			transition = APPEAR;
			ratio = vt.getInterpolationCompletionRatio();
		}
		if ( vs.getTransition() == APPEAR || vs.getTransition() == DISAPPEAR )
		{
			transition = vs.getTransition();
			ratio = vs.getInterpolationCompletionRatio();
		}
		final boolean highlighted = ( highlightedEdgeId >= 0 ) && ( edge.getTrackSchemeEdgeId() == highlightedEdgeId );
		final boolean selected = edge.isSelected();
		final boolean ghost = vs.isGhost() && vt.isGhost();

		final Color edgeColor = ( style.getEdgeColorMode() == EdgeColorMode.FIXED )
				? style.getEdgeColor() : edge.getColor();
		final Color drawColor = getColor( selected, ghost, transition, ratio,
				edgeColor, style.getSelectedEdgeColor(),
				style.getGhostEdgeColor(), style.getGhostSelectedEdgeColor() );
		g2.setColor( drawColor );
		if ( highlighted )
			g2.setStroke( style.getEdgeHighlightStroke() );
		else if ( ghost )
			g2.setStroke( style.getEdgeGhostStroke() );

		final int sx = ( int ) vs.getX();
		final int sy = ( int ) vs.getY();
		final int tx = ( int ) vt.getX();
		final int ty = ( int ) vt.getY();
		g2.drawLine( sx, sy, tx, sy );
		g2.drawLine( tx, sy, tx, ty );

		if ( highlighted || ghost )
			g2.setStroke( style.getEdgeStroke() );
	}

	@Override
	protected double distanceToPaintedEdge( final double x0, final double y0, final ScreenEdge edge, final ScreenVertex source, final ScreenVertex target )
	{
		final double xs = source.getX();
		final double ys = source.getY();
		final double xt = target.getX();
		final double yt = target.getY();
		final double d1 = GeometryUtils.segmentDist( x0, y0, xs, ys, xt, ys );
		final double d2 = GeometryUtils.segmentDist( x0, y0, xt, ys, xt, yt );
		return Math.min( d1, d2 );
	}

	public static final class Factory implements TrackSchemeOverlayFactory
	{
		@Override
		public AbstractTrackSchemeOverlay create(
				final TrackSchemeGraph< ?, ? > graph,
				final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight,
				final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus )
		{
			return new BranchGraphTrackSchemeOverlay( graph, highlight, focus, TrackSchemeStyle.defaultStyle() );
		}
	}
}
