package net.trackmate.trackscheme;

import static net.trackmate.trackscheme.ScreenVertex.Transition.APPEAR;
import static net.trackmate.trackscheme.ScreenVertex.Transition.DISAPPEAR;
import static net.trackmate.trackscheme.ScreenVertex.Transition.NONE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import net.trackmate.trackscheme.ScreenVertex.Transition;
import net.trackmate.trackscheme.laf.TrackSchemeLAF;
import net.trackmate.trackscheme.laf.TrackSchemeStyle;

public class DefaultTrackSchemeLAF implements TrackSchemeLAF
{
	/*
	 * CONSTANTS
	 */

	/**
	 * If the time rows are smaller than this size in pixels, they won't be
	 * drawn.
	 */
	private static final int MIN_TIMELINE_SPACING = 20;


	/**
	 * Y position for columns labels.
	 */
	private static final int YTEXT = 20;

	/**
	 * X position for time labels.
	 */
	private static final int XTEXT = 20;

	/**
	 * Size in pixel below which we do not draw column bars.
	 */
	private static final int MIN_DRAWING_COLUMN_WIDTH = 30;

	public static final double simplifiedVertexRadius = 3.0;

	public static final double minDisplayVertexDist = 20.0;

	public static final double maxDisplayVertexSize = 100.0;

	public static final double minDisplaySimplifiedVertexDist = 5.0;

	public static final double avgLabelLetterWidth = 5.0;

	/*
	 * FIELDS
	 */

	private final boolean paintRows = true;

	private final boolean paintColumns = true;

	private final VertexOrder order;

	private final LineageTreeLayout layout;

	protected TrackSchemeStyle style;


	/*
	 * CONSTRUCTORS
	 */

	public DefaultTrackSchemeLAF( final VertexOrder order, final LineageTreeLayout layout, final TrackSchemeStyle style )
	{
		this.order = order;
		this.layout = layout;
		this.style = style;
	}

	/*
	 * METHODS
	 */

	public void setTrackSchemeStyle( final TrackSchemeStyle style )
	{
		this.style = style;
	}

	@Override
	public void beforeDrawVertex( final Graphics2D g2 )
	{
		g2.setStroke( style.vertexStroke );
	}

	@Override
	public void drawVertex( final Graphics2D g2, final ScreenVertex vertex )
	{
		final double d = vertex.getVertexDist();
		if ( d >= minDisplayVertexDist )
		{
			drawVertexFull( g2, vertex );
		}
		else if ( d >= minDisplaySimplifiedVertexDist )
		{
			drawVertexSimplified( g2, vertex );
		}
	}

	@Override
	public void beforeDrawVertexRange( final Graphics2D g2 )
	{
		g2.setColor( style.vertexRangeColor );
	}

	@Override
	public void drawVertexRange( final Graphics2D g2, final ScreenVertexRange range )
	{
		final int x = ( int ) range.getMinX();
		final int y = ( int ) range.getMinY();
		final int w = ( int ) range.getMaxX() - x;
		final int h = ( int ) range.getMaxY() - y;
		g2.fillRect( x, y, w, h );
	}

	protected void drawVertexSimplified( final Graphics2D g2, final ScreenVertex vertex )
	{
		final Transition transition = vertex.getTransition();
		final double ratio = vertex.getInterpolationCompletionRatio();
		final boolean disappear = ( transition == DISAPPEAR );
		final boolean selected = vertex.isSelected();

		double spotradius = simplifiedVertexRadius;
		if ( disappear )
			spotradius *= ( 1 + 3 * ratio );

		final Color fillColor = getColor( selected, transition, ratio,
				disappear ? style.selectedSimplifiedVertexFillColor : style.simplifiedVertexFillColor,
				style.selectedSimplifiedVertexFillColor );

		final double x = vertex.getX();
		final double y = vertex.getY();
		g2.setColor( fillColor );
		final int ox = ( int ) x - ( int ) spotradius;
		final int oy = ( int ) y - ( int ) spotradius;
		final int ow = 2 * ( int ) spotradius;
		g2.fillOval( ox, oy, ow, ow );
	}

	protected void drawVertexFull( final Graphics2D g2, final ScreenVertex vertex )
	{
		final Transition transition = vertex.getTransition();
		final boolean disappear = ( transition == DISAPPEAR );
		final double ratio = vertex.getInterpolationCompletionRatio();
		final boolean selected = vertex.isSelected();

		double spotdiameter = Math.min( vertex.getVertexDist() - 10.0, maxDisplayVertexSize );
		if ( disappear )
			spotdiameter *= ( 1 + ratio );
		final double spotradius = spotdiameter / 2;

		final Color fillColor = getColor( selected, transition, ratio, style.vertexFillColor, style.selectedVertexFillColor );
		final Color drawColor = getColor( selected, transition, ratio, style.vertexDrawColor, style.selectedVertexDrawColor );

		final double x = vertex.getX();
		final double y = vertex.getY();
		final int ox = ( int ) x - ( int ) spotradius;
		final int oy = ( int ) y - ( int ) spotradius;
		final int sd = 2 * ( int ) spotradius;
		g2.setColor( fillColor );
		g2.fillOval( ox, oy, sd, sd );
		g2.setColor( drawColor );
		g2.drawOval( ox, oy, sd, sd );

		final int maxLabelLength = ( int ) ( spotdiameter / avgLabelLetterWidth );
		if ( maxLabelLength > 2 && !disappear )
		{
			String label = vertex.getLabel();
			if ( label.length() > maxLabelLength )
				label = label.substring( 0, maxLabelLength - 2 ) + "...";

			final FontRenderContext frc = g2.getFontRenderContext();
			final TextLayout layout = new TextLayout( label, style.font, frc );
			final Rectangle2D bounds = layout.getBounds();
			final float tx = ( float ) ( x - bounds.getCenterX() );
			final float ty = ( float ) ( y - bounds.getCenterY() );
			layout.draw( g2, tx, ty );
		}
	}

	@Override
	public void beforeDrawEdge( final Graphics2D g2 )
	{
		g2.setStroke( style.edgeStroke );
	}

	@Override
	public void drawEdge( final Graphics2D g2, final ScreenEdge edge, final ScreenVertex vs, final ScreenVertex vt )
	{
		Transition transition = vs.getTransition();
		double ratio = vs.getInterpolationCompletionRatio();
		if ( vt.getTransition() == APPEAR )
		{
			transition = APPEAR;
			ratio = vt.getInterpolationCompletionRatio();
		}
		final boolean selected = edge.isSelected();
		final Color drawColor = getColor( selected, transition, ratio, style.edgeColor, style.selectedEdgeColor );
		g2.setColor( drawColor );
		g2.drawLine( ( int ) vs.getX(), ( int ) vs.getY(), ( int ) vt.getX(), ( int ) vt.getY() );
	}

	protected Color getColor(
			final boolean isSelected,
			final Transition transition,
			final double completionRatio,
			final Color normalColor,
			final Color selectedColor )
	{
		if ( transition == NONE )
			return isSelected ? selectedColor : normalColor;
		else
		{
			int r = normalColor.getRed();
			int g = normalColor.getGreen();
			int b = normalColor.getBlue();
			final double ratio = transition == DISAPPEAR ? completionRatio : 1 - completionRatio;
			if ( isSelected )
			{
				r = ( int ) ( ratio * r + ( 1 - ratio ) * selectedColor.getRed() );
				g = ( int ) ( ratio * g + ( 1 - ratio ) * selectedColor.getGreen() );
				b = ( int ) ( ratio * b + ( 1 - ratio ) * selectedColor.getBlue() );
			}
			return new Color( r, g, b, ( int ) ( 255 * ( 1 - ratio ) ) );
		}
	}

	@Override
	public void paintBackground( final Graphics2D g2, final double minX, final double maxX, final double minY, final double maxY,
			final int width, final int height, final double xScale, final double yScale )
	{
		g2.setColor( style.backgroundColor );
		g2.fillRect( 0, 0, width, height );

		g2.setColor( style.decorationColor );
		final FontMetrics fm = g2.getFontMetrics( style.font );
		g2.setFont( style.font );

		int minLineY = 0;
		int maxLineY = height;

		if ( paintRows )
		{
			final int fontInc = fm.getHeight() / 2;
			final int stepT = 1 + MIN_TIMELINE_SPACING / ( int ) ( 1 + yScale );
			final int maxTimepoint = order.getMaxTimepoint();

			int tstart = Math.max( 0, ( int ) minY - 1 );
			tstart = ( tstart / stepT ) * stepT;
			int tend = 1 + Math.min( maxTimepoint, ( int ) maxY );
			tend = ( 1 + tend / stepT ) * stepT;

			final int maxLineX = Math.min( width,
					( int ) ( ( layout.columns.get( layout.columns.size() - 1 ) - minX - 0.5 ) * xScale ) );

			minLineY = ( int ) ( ( tstart - minY - 0.5 ) * yScale );
			maxLineY = ( int ) ( ( tend - minY - 0.5 ) * yScale );

			for ( int t = tstart; t < tend; t = t + stepT )
			{
				final int yline = ( int ) ( ( t - minY - 0.5 ) * yScale );
				g2.drawLine( 0, yline, maxLineX, yline );

				final int ytext = ( int ) ( ( t - minY + stepT / 2 ) * yScale ) + fontInc;
				if ( ytext < 2 * YTEXT )
				{
					continue;
				}
				g2.drawString( "" + t, XTEXT, ytext );
			}

			// Last line
			final int yline = ( int ) ( ( tend - minY - 0.5 ) * yScale );
			g2.drawLine( 0, yline, maxLineX, yline );

		}

		if ( paintColumns )
		{
			int minC = layout.columns.binarySearch( minX );
			if ( minC < 0 )
			{
				minC = -1 - minC;
			}
			minC = Math.max( 0, minC - 1 ); // at least 1 column out

			int maxC = layout.columns.binarySearch( maxX + 0.5, minC, layout.columns.size() );
			if ( maxC < 0 )
			{
				maxC = -1 - maxC;
			}
			maxC = Math.min( layout.columns.size(), maxC + 1 );

			double lastX = Double.NEGATIVE_INFINITY;
			for ( int c = minC; c < maxC; c++ )
			{
				final double col = layout.columns.get( c );
				final int xline = ( int ) ( ( col - minX - 0.5 ) * xScale );
				if ( xline < 2 * XTEXT || ( xline - lastX ) < MIN_DRAWING_COLUMN_WIDTH )
				{
					continue;
				}
				g2.drawLine( xline, minLineY, xline, maxLineY );
				lastX = xline;

				if ( c < 1 )
				{
					continue;
				}
				final int xprevline = ( int ) ( ( layout.columns.get( c - 1 ) - minX - 0.5 ) * xScale );
				final String str = layout.columnNames.get( c - 1 );
				final int stringWidth = fm.stringWidth( str );

				final int columnWidth = xline - xprevline;
				if ( columnWidth < stringWidth + 5 )
				{
					continue;
				}

				final int xtext = ( Math.min( width, xline ) + Math.max( 0, xprevline ) - stringWidth ) / 2;
				if ( xtext < 2 * XTEXT )
				{
					continue;
				}
				g2.drawString( str, xtext, YTEXT );
			}
		}
	}
}