package org.mastodon.revised.trackscheme.display;

import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.APPEAR;
import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.DISAPPEAR;
import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.NONE;
import static org.mastodon.revised.trackscheme.ScreenVertex.Transition.SELECTING;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import org.mastodon.model.FocusModel;
import org.mastodon.model.HighlightModel;
import org.mastodon.revised.Util;
import org.mastodon.revised.trackscheme.ScreenEdge;
import org.mastodon.revised.trackscheme.ScreenEntities;
import org.mastodon.revised.trackscheme.ScreenVertex;
import org.mastodon.revised.trackscheme.ScreenVertex.Transition;
import org.mastodon.revised.trackscheme.ScreenVertexRange;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;

/**
 * An AbstractTrackSchemeOverlay implementation that:
 * <ul>
 * <li>draws vertex as circles with the label inside.
 * <li>offers two sizes of vertices (full and simplified).
 * <li>draws edges as lines.
 * </ul>
 * <p>
 * Colors and strokes can be configured separately, using a
 * {@link TrackSchemeStyle}.
 */
public class DefaultTrackSchemeOverlay extends AbstractTrackSchemeOverlay
{
	/*
	 * CONSTANTS
	 */

	public static final double simplifiedVertexRadius = 2.5;

	public static final double simplifiedVertexSelectTolerance = 3.5;

	public static final double minDisplayVertexDist = 17.0;

	public static final double maxDisplayVertexSize = 100.0;

	public static final double minDisplaySimplifiedVertexDist = 5.0;

	public static final double avgLabelLetterWidth = 5.0;

	/*
	 * FIELDS
	 */

	private final TrackSchemeStyle style;

	public DefaultTrackSchemeOverlay(
			final TrackSchemeGraph< ?, ? > graph,
			final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight,
			final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus,
			final TrackSchemeOptions options,
			final TrackSchemeStyle style )
	{
		super( graph, highlight, focus, options );
		this.style = style;
	}

	@Override
	protected void paintBackground( final Graphics2D g2, final ScreenEntities screenEntities )
	{
		PaintDecorations.paintBackground( g2, getWidth(), getHeight(), headerWidth, headerHeight, screenEntities, getCurrentTimepoint(), style );
	}

	@Override
	protected void paintHeaders( final Graphics2D g2, final ScreenEntities screenEntities )
	{
		PaintDecorations.paintHeaders( g2, getWidth(), getHeight(), headerWidth, headerHeight, screenEntities, getCurrentTimepoint(), style );
	}

	@Override
	protected void beforeDrawVertex( final Graphics2D g2 )
	{
		g2.setStroke( style.getVertexStroke() );
	}

	@Override
	protected void drawVertex( final Graphics2D g2, final ScreenVertex vertex )
	{
		final double d = vertex.getVertexDist();
		if ( d >= minDisplayVertexDist )
			drawVertexFull( g2, vertex );
		else if ( d >= minDisplaySimplifiedVertexDist )
			drawVertexSimplified( g2, vertex );
		else
			drawVertexSimplifiedIfHighlighted( g2, vertex );
	}

	@Override
	protected double distanceToPaintedEdge( final double x, final double y, final ScreenEdge edge, final ScreenVertex source, final ScreenVertex target )
	{
		final double x1 = source.getX();
		final double y1 = source.getY();
		final double x2 = target.getX();
		final double y2 = target.getY();
		final double d = Util.segmentDist( x, y, x1, y1, x2, y2 );
		return d;
	}

	@Override
	protected boolean isInsidePaintedVertex( final double x, final double y, final ScreenVertex vertex )
	{
		final double d = vertex.getVertexDist();
		double radius = 0;
		if ( d >= minDisplayVertexDist )
		{
			final double spotdiameter = Math.min( vertex.getVertexDist() - 10.0, maxDisplayVertexSize );
			radius = spotdiameter / 2;
		}
		else if ( d >= minDisplaySimplifiedVertexDist )
		{
			radius = simplifiedVertexRadius + simplifiedVertexSelectTolerance;
		}
		final double dx = x - vertex.getX();
		final double dy = y - vertex.getY();
		return ( dx * dx + dy * dy < radius * radius );
	}

	@Override
	protected void beforeDrawVertexRange( final Graphics2D g2 )
	{
		g2.setColor( style.getVertexRangeColor() );
	}

	@Override
	protected void drawVertexRange( final Graphics2D g2, final ScreenVertexRange range )
	{
		final int x = ( int ) range.getMinX();
		final int y = ( int ) range.getMinY();
		final int w = ( int ) range.getMaxX() - x;
		final int h = ( int ) range.getMaxY() - y;
		g2.fillRect( x, y, w, h );
	}

	@Override
	public void beforeDrawEdge( final Graphics2D g2 )
	{
		g2.setStroke( style.getEdgeStroke() );
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
		final Color drawColor = getColor( selected, ghost, transition, ratio,
				style.getEdgeColor(), style.getSelectedEdgeColor(),
				style.getGhostEdgeColor(), style.getGhostSelectedEdgeColor() );
		g2.setColor( drawColor );
		if ( highlighted )
			g2.setStroke( style.getEdgeHighlightStroke() );
		else if ( ghost )
			g2.setStroke( style.getEdgeGhostStroke() );
		g2.drawLine( ( int ) vs.getX(), ( int ) vs.getY(), ( int ) vt.getX(), ( int ) vt.getY() );
		if ( highlighted || ghost )
			g2.setStroke( style.getEdgeStroke() );
	}

	protected void drawVertexSimplified( final Graphics2D g2, final ScreenVertex vertex )
	{
		final Transition transition = vertex.getTransition();
		final boolean disappear = ( transition == DISAPPEAR );
		final double ratio = vertex.getInterpolationCompletionRatio();

		final boolean highlighted = ( highlightedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == highlightedVertexId );
		final boolean focused = ( focusedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == focusedVertexId );
		final boolean selected = vertex.isSelected();
		final boolean ghost = vertex.isGhost();

		double spotradius = simplifiedVertexRadius;
		if ( disappear )
			spotradius *= ( 1 + 3 * ratio );

		if ( highlighted || focused )
			spotradius *= 1.5;

		final Color fillColor = getColor( selected, ghost, transition, ratio,
				disappear ? style.getSelectedSimplifiedVertexFillColor() : style.getSimplifiedVertexFillColor(),
				style.getSelectedSimplifiedVertexFillColor(),
				disappear ? style.getGhostSelectedSimplifiedVertexFillColor() : style.getGhostSimplifiedVertexFillColor(),
				style.getGhostSelectedSimplifiedVertexFillColor() );

		final double x = vertex.getX();
		final double y = vertex.getY();
		g2.setColor( fillColor );
		final int ox = ( int ) x - ( int ) spotradius;
		final int oy = ( int ) y - ( int ) spotradius;
		final int ow = 2 * ( int ) spotradius;

		if ( focused )
			g2.fillRect( ox, oy, ow, ow );
		else
			g2.fillOval( ox, oy, ow, ow );
	}

	protected void drawVertexSimplifiedIfHighlighted( final Graphics2D g2, final ScreenVertex vertex )
	{
		final boolean highlighted = ( highlightedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == highlightedVertexId );
		final boolean focused = ( focusedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == focusedVertexId );
		if ( highlighted || focused )
		{
			final Transition transition = vertex.getTransition();
			final boolean disappear = ( transition == DISAPPEAR );
			final double ratio = vertex.getInterpolationCompletionRatio();

			final boolean selected = vertex.isSelected();
			final boolean ghost = vertex.isGhost();

			double spotradius = simplifiedVertexRadius;
			if ( disappear )
				spotradius *= ( 1 + 3 * ratio );

			final Color fillColor = getColor( selected, ghost, transition, ratio,
					disappear ? style.getSelectedSimplifiedVertexFillColor() : style.getSimplifiedVertexFillColor(),
					style.getSelectedSimplifiedVertexFillColor(),
					disappear ? style.getGhostSelectedSimplifiedVertexFillColor() : style.getGhostSimplifiedVertexFillColor(),
					style.getGhostSelectedSimplifiedVertexFillColor() );

			final double x = vertex.getX();
			final double y = vertex.getY();
			g2.setColor( fillColor );
			final int ox = ( int ) x - ( int ) spotradius;
			final int oy = ( int ) y - ( int ) spotradius;
			final int ow = 2 * ( int ) spotradius;

			if ( focused )
				g2.fillRect( ox, oy, ow, ow );
			else
				g2.fillOval( ox, oy, ow, ow );
		}
	}

	protected void drawVertexFull( final Graphics2D g2, final ScreenVertex vertex )
	{
		final Transition transition = vertex.getTransition();
		final boolean disappear = ( transition == DISAPPEAR );
		final double ratio = vertex.getInterpolationCompletionRatio();

		final boolean highlighted = ( highlightedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == highlightedVertexId );
		final boolean focused = ( focusedVertexId >= 0 ) && ( vertex.getTrackSchemeVertexId() == focusedVertexId );
		final boolean selected = vertex.isSelected();
		final boolean ghost = vertex.isGhost();

		double spotdiameter = Math.min( vertex.getVertexDist() - 10.0, maxDisplayVertexSize );
		if ( highlighted )
			spotdiameter += 10.0;
		if ( disappear )
			spotdiameter *= ( 1 + ratio );
		final double spotradius = spotdiameter / 2;

		final Color fillColor = getColor( selected, ghost, transition, ratio,
				style.getVertexFillColor(), style.getSelectedVertexFillColor(),
				style.getGhostVertexFillColor(), style.getGhostSelectedVertexFillColor() );
		final Color drawColor = getColor( selected, ghost, transition, ratio,
				style.getVertexDrawColor(), style.getSelectedVertexDrawColor(),
				style.getGhostVertexDrawColor(), style.getGhostSelectedVertexDrawColor() );

		final double x = vertex.getX();
		final double y = vertex.getY();
		final int ox = ( int ) x - ( int ) spotradius;
		final int oy = ( int ) y - ( int ) spotradius;
		final int sd = 2 * ( int ) spotradius;
		g2.setColor( fillColor );
		g2.fillOval( ox, oy, sd, sd );

		g2.setColor( drawColor );
		if ( highlighted )
			g2.setStroke( style.getVertexHighlightStroke() );
		else if ( focused )
			// An animation might be better for the focus, but for now this is it.
			g2.setStroke( style.getFocusStroke() );
		else if ( ghost )
			g2.setStroke( style.getVertexGhostStroke() );
		g2.drawOval( ox, oy, sd, sd );
		if ( highlighted || focused || ghost )
			g2.setStroke( style.getVertexStroke() );

		final int maxLabelLength = ( int ) ( spotdiameter / avgLabelLetterWidth );
		if ( maxLabelLength > 2 && !disappear )
		{
			String label = vertex.getLabel();
			if ( label.length() > maxLabelLength )
				label = label.substring( 0, maxLabelLength - 2 ) + "...";

			if ( ! label.isEmpty() )
			{
				final FontRenderContext frc = g2.getFontRenderContext();
				final TextLayout layout = new TextLayout( label, style.getFont(), frc );
				final Rectangle2D bounds = layout.getBounds();
				final float tx = ( float ) ( x - bounds.getCenterX() );
				final float ty = ( float ) ( y - bounds.getCenterY() );
				layout.draw( g2, tx, ty );
			}
		}
	}

	protected Color getColor(
			final boolean isSelected,
			final boolean isGhost,
			final Transition transition,
			final double completionRatio,
			final Color normalColor,
			final Color selectedColor,
			final Color ghostNormalColor,
			final Color ghostSelectedColor )
	{
		if ( transition == NONE )
			return isGhost
					? ( isSelected ? ghostSelectedColor : ghostNormalColor )
					: ( isSelected ? selectedColor : normalColor );
		else
		{
			final double ratio = ( transition == APPEAR || transition == SELECTING )
					? 1 - completionRatio
					: completionRatio;
			final boolean fade = ( transition == APPEAR || transition == DISAPPEAR );
			int r = normalColor.getRed();
			int g = normalColor.getGreen();
			int b = normalColor.getBlue();
			int a = normalColor.getAlpha();
			if ( isSelected || !fade )
			{
				r = ( int ) ( ratio * r + ( 1 - ratio ) * selectedColor.getRed() );
				g = ( int ) ( ratio * g + ( 1 - ratio ) * selectedColor.getGreen() );
				b = ( int ) ( ratio * b + ( 1 - ratio ) * selectedColor.getBlue() );
				a = ( int ) ( ratio * a + ( 1 - ratio ) * selectedColor.getAlpha() );
			}
			if ( fade )
				a = ( int ) ( a * ( 1 - ratio ) );
			final Color color = new Color( r, g, b, a );
			return isGhost
					? TrackSchemeStyle.mixGhostColor( color, style.getBackgroundColor() )
					: color;
		}
	}

	/*
	 * FACTORY.
	 */

	public static final class Factory implements TrackSchemeOverlayFactory
	{
		@Override
		public DefaultTrackSchemeOverlay create(
				final TrackSchemeGraph< ?, ? > graph,
				final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight,
				final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus,
				final TrackSchemeOptions options )
		{
			return new DefaultTrackSchemeOverlay( graph, highlight, focus, options, TrackSchemeStyle.defaultStyle() );
		}
	}
}