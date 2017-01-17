package org.mastodon.revised.bdv.overlay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

import org.mastodon.kdtree.ClipConvexPolytope;
import org.mastodon.revised.bdv.overlay.util.GeometryUtils;
import org.mastodon.revised.ui.coloring.EdgeColorGenerator;
import org.mastodon.revised.ui.coloring.VertexColorGenerator;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.spatial.SpatialIndex;

import bdv.util.Affine3DHelpers;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.imglib2.algorithm.kdtree.ConvexPolytope;
import net.imglib2.realtransform.AffineTransform3D;

public class OverlayBranchGraphRenderer<
	BV extends OverlayVertex< BV,BE >,
	BE extends OverlayEdge< BE, BV >,
	V extends OverlayVertex< V,E >,
	E extends OverlayEdge< E, V > >
		extends OverlayGraphRenderer< BV, BE >
{

	private final OverlayBranchGraph< BV, BE, V, E > branchGraph;

	private final OverlayGraph< V, E > wrappedGraph;

	public OverlayBranchGraphRenderer(
			final OverlayBranchGraph< BV, BE, V, E > branchGraph,
			final OverlayGraph< V, E > graph,
			final HighlightModel< BV, BE > highlight,
			final FocusModel< BV, BE > focus,
			final Selection< BV, BE > selection,
			final VertexColorGenerator< BV > vertexColorGenerator,
			final EdgeColorGenerator< BE > edgeColorGenerator )
	{
		super( branchGraph, highlight, focus, selection, vertexColorGenerator, edgeColorGenerator );
		this.branchGraph = branchGraph;
		this.wrappedGraph = graph;
	}

	@Override
	public void drawOverlays( final Graphics g )
	{
		final Graphics2D graphics = ( Graphics2D ) g;
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		final int extraFontHeight = fontMetrics.getAscent() / 2;
		final int extraFontWidth = fontMetrics.charWidth( ' ' ) / 2;

		final AffineTransform3D transform = getRenderTransformCopy();
		final int currentTimepoint = renderTimepoint;

		final double maxDepth = isFocusLimitViewRelative
				? focusLimit
				: focusLimit * Affine3DHelpers.extractScale( transform, 0 );

		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, antialiasing );

		final V target = wrappedGraph.vertexRef();
		final BV ref1 = branchGraph.vertexRef();
		final BV ref2 = branchGraph.vertexRef();
		final BE ref3 = branchGraph.edgeRef();
		final double[] gPos = new double[ 3 ];
		final double[] lPos = new double[ 3 ];

		final double sliceDistanceFade = ellipsoidFadeDepth;
		final double timepointDistanceFade = 0.5;

		final boolean isVertexColorFixed = ( vertexColorMode == VertexColorMode.FIXED );
		Color vertexColor1 = fixedColor1;
		Color vertexColor2 = fixedColor2;
		final boolean isEdgeColorFixed = ( edgeColorMode == EdgeColorMode.FIXED );
		Color edgeColor1 = fixedColor1;
		Color edgeColor2 = fixedColor2;

		final ScreenVertexMath screenVertexMath = new ScreenVertexMath();

		index.readLock().lock();
		try
		{
			if ( drawLinks )
			{
				final BE highlighted = highlight.getHighlightedEdge( ref3 );

				graphics.setStroke( defaultEdgeStroke );

				for ( int t = Math.max( 0, currentTimepoint - timeLimit ); t < currentTimepoint; ++t )
				{
					final SpatialIndex< BV > si = index.getSpatialIndex( t );
					final ClipConvexPolytope< BV > ccp = si.getClipConvexPolytope();
					ccp.clip( getVisiblePolytopeGlobal( transform, t ) );
					for ( final BV vertex : ccp.getInsideValues() )
					{
						// Draw a branch at once.
						vertex.localize( gPos );
						transform.apply( gPos, lPos );
						final int x0 = ( int ) lPos[ 0 ];
						final int y0 = ( int ) lPos[ 1 ];
						final double z0 = lPos[ 2 ];
						for ( final BE edge : vertex.outgoingEdges() )
						{
							final TIntArrayList xs = new TIntArrayList();
							final TIntArrayList ys = new TIntArrayList();
							final TDoubleArrayList zs = new TDoubleArrayList();
							final TIntArrayList ts = new TIntArrayList();
							xs.add( x0 );
							ys.add( y0 );
							zs.add( z0 );
							ts.add( vertex.getTimepoint() );

							final Iterator< E > it = branchGraph.edgeBranchIterator( edge );
							while ( it.hasNext() )
							{
								final E e = it.next();
								e.getTarget( target );
								target.localize( gPos );
								transform.apply( gPos, lPos );

								final int x1 = ( int ) lPos[ 0 ];
								final int y1 = ( int ) lPos[ 1 ];
								final double z1 = lPos[ 2 ];

								xs.add( x1 );
								ys.add( y1 );
								zs.add( z1 );
								ts.add( target.getTimepoint() );
							}

							final double td0;
							if ( currentTimepoint < ts.min() )
								td0 = timeDistance( ts.min(), currentTimepoint, timeLimit );
							else if ( currentTimepoint < ts.max() )
								td0 = 0;
							else
								td0 = timeDistance( ts.max(), currentTimepoint, timeLimit );

							final double sd0 = sliceDistance( zs.min(), maxDepth );
							final double sd1 = sliceDistance( zs.max(), maxDepth );

							final boolean isHighlighted = edge.equals( highlighted );
							if ( td0 >= -1 )
							{
								if ( ( sd0 > -1 && sd0 < 1 ) || ( sd1 > -1 && sd1 < 1 ) )
								{
									if ( !isEdgeColorFixed )
									{
										edgeColor1 = edgeColorGenerator.color( edge );
										edgeColor2 = edgeColor1;
									}
									final Color c1 = getColor( sd1, td0, sliceDistanceFade, timepointDistanceFade,
											selection.isSelected( edge ), edgeColor1, edgeColor2 );
									graphics.setPaint( c1 );
									if ( isHighlighted )
										graphics.setStroke( highlightedEdgeStroke );

									int xb = xs.get( 0 );
									int yb = ys.get( 0 );
									int xa = xb;
									int ya = yb;
									for ( int i = 1; i < xs.size(); i++ )
									{
										xa = xb;
										ya = yb;
										xb = xs.get( i );
										yb = ys.get( i );
										graphics.drawLine( xa, ya, xb, yb );
									}

									// Draw arrows for edge direction.
									if ( drawLinkArrows )
									{
										final double dx = xb - xa;
										final double dy = yb - ya;
										final double alpha = Math.atan2( dy, dx );
										final double l = 5;
										final double theta = Math.PI / 6.;
										final int x1a = ( int ) Math.round( xb - l * Math.cos( alpha - theta ) );
										final int x1b = ( int ) Math.round( xb - l * Math.cos( alpha + theta ) );
										final int y1a = ( int ) Math.round( yb - l * Math.sin( alpha - theta ) );
										final int y1b = ( int ) Math.round( yb - l * Math.sin( alpha + theta ) );
										graphics.drawLine( xb, yb, x1a, y1a );
										graphics.drawLine( xb, yb, x1b, y1b );
									}

									if ( isHighlighted )
										graphics.setStroke( defaultEdgeStroke );
								}
							}
						}
					}
				}
			}

			if ( drawSpots )
			{
				final BV highlighted = highlight.getHighlightedVertex( ref1 );
				final BV focused = focus.getFocusedVertex( ref2 );

				graphics.setStroke( defaultVertexStroke );
				final AffineTransform torig = graphics.getTransform();

				final SpatialIndex< BV > si = index.getSpatialIndex( currentTimepoint );
				final ClipConvexPolytope< BV > ccp = si.getClipConvexPolytope();
				ccp.clip( getVisiblePolytopeGlobal( transform, currentTimepoint ) );
				for ( final BV vertex : ccp.getInsideValues() )
				{
					final boolean isHighlighted = vertex.equals( highlighted );
					final boolean isFocused = vertex.equals( focused );

					screenVertexMath.init( vertex, transform );

					final double x = screenVertexMath.getViewPos()[ 0 ];
					final double y = screenVertexMath.getViewPos()[ 1 ];
					final double z = screenVertexMath.getViewPos()[ 2 ];
					final double sd = sliceDistance( z, maxDepth );

					if ( !isVertexColorFixed )
					{
						vertexColor1 = vertexColorGenerator.color( vertex );
						vertexColor2 = vertexColor1;
					}

					if ( drawEllipsoidSliceIntersection )
					{
						if ( screenVertexMath.intersectsViewPlane() )
						{
							final double[] tr = screenVertexMath.getIntersectCenter();
							final double theta = screenVertexMath.getIntersectTheta();
							final Ellipse2D ellipse = screenVertexMath.getIntersectEllipse();

							graphics.translate( tr[ 0 ], tr[ 1 ] );
							graphics.rotate( theta );
							graphics.setColor( getColor( 0, 0, ellipsoidFadeDepth, timepointDistanceFade,
									selection.isSelected( vertex ), vertexColor1, vertexColor2 ) );
							if ( isHighlighted )
								graphics.setStroke( highlightedVertexStroke );
							else if ( isFocused )
								graphics.setStroke( focusedVertexStroke );
							graphics.draw( ellipse );
							if ( isHighlighted || isFocused )
								graphics.setStroke( defaultVertexStroke );

							if ( !drawEllipsoidSliceProjection && drawSpotLabels )
							{
								// TODO Don't use ellipse, which is an AWT
								// object, for calculation.
								graphics.rotate( -theta );
								final double a = ellipse.getWidth();
								final double b = ellipse.getHeight();
								final double cos = Math.cos( theta );
								final double sin = Math.sin( theta );
								final double l = Math.sqrt( a * a * cos * cos + b * b * sin * sin );
								final float xl = ( float ) l / 2 + extraFontWidth;
								final float yl = extraFontHeight;
								graphics.drawString( vertex.getLabel(), xl, yl );
							}

							graphics.setTransform( torig );
						}
					}

					if ( sd > -1 && sd < 1 )
					{
						if ( drawEllipsoidSliceProjection )
						{
							final double[] tr = screenVertexMath.getProjectCenter();
							final double theta = screenVertexMath.getProjectTheta();
							final Ellipse2D ellipse = screenVertexMath.getProjectEllipse();

							graphics.translate( tr[ 0 ], tr[ 1 ] );
							graphics.rotate( theta );
							graphics.setColor( getColor( sd, 0, ellipsoidFadeDepth, timepointDistanceFade,
									selection.isSelected( vertex ), vertexColor1, vertexColor2 ) );
							if ( isHighlighted )
								graphics.setStroke( highlightedVertexStroke );
							else if ( isFocused )
								graphics.setStroke( focusedVertexStroke );
							graphics.draw( ellipse );
							if ( isHighlighted || isFocused )
								graphics.setStroke( defaultVertexStroke );

							if ( drawSpotLabels )
							{
								// TODO Don't use ellipse, which is an AWT
								// object, for calculation.
								graphics.rotate( -theta );
								final double a = ellipse.getWidth();
								final double b = ellipse.getHeight();
								final double cos = Math.cos( theta );
								final double sin = Math.sin( theta );
								final double l = Math.sqrt( a * a * cos * cos + b * b * sin * sin );
								final float xl = ( float ) l / 2 + extraFontWidth;
								final float yl = extraFontHeight;
								graphics.drawString( vertex.getLabel(), xl, yl );
							}

							graphics.setTransform( torig );
						}

						// TODO: use simplified drawPointMaybe and
						// drawPointAlways from getVisibleVertices()
						final boolean drawPoint = drawPoints && ( ( !drawEllipsoidSliceIntersection && !drawEllipsoidSliceProjection )
								|| drawPointsForEllipses
								|| ( drawEllipsoidSliceIntersection && !screenVertexMath.intersectsViewPlane() ) );
						if ( drawPoint )
						{
							graphics.setColor( getColor( sd, 0, pointFadeDepth, timepointDistanceFade,
									selection.isSelected( vertex ), vertexColor1, vertexColor2 ) );
							double radius = pointRadius;
							if ( isHighlighted || isFocused )
								radius *= 2;
							final int ox = ( int ) ( x - radius );
							final int oy = ( int ) ( y - radius );
							final int ow = ( int ) ( 2 * radius );
							if ( isFocused )
								graphics.fillRect( ox, oy, ow, ow );
							else
								graphics.fillOval( ox, oy, ow, ow );
						}
					}
				}
			}
		}
		finally
		{
			index.readLock().unlock();
		}
		wrappedGraph.releaseRef( target );
		branchGraph.releaseRef( ref1 );
		branchGraph.releaseRef( ref2 );
	}

	@Override
	public BE getEdgeAt( final int x, final int y, final double tolerance, final BE ref )
	{
		final AffineTransform3D transform = getRenderTransformCopy();
		final int currentTimepoint = renderTimepoint;

		final ConvexPolytope visiblePolytopeGlobal = getVisiblePolytopeGlobal( transform, currentTimepoint );

		boolean found = false;
		index.readLock().lock();
		try
		{
			final double[] lPosT = new double[ 3 ];
			final double[] gPosT = new double[ 3 ];
			final double[] lPosS = new double[ 3 ];
			final double[] gPosS = new double[ 3 ];
			final V vertexRef = wrappedGraph.vertexRef();
			double bestDist = tolerance;

			for ( int t = Math.max( 0, currentTimepoint - timeLimit ); t < currentTimepoint; ++t )
			{
				final SpatialIndex< BV > si = index.getSpatialIndex( t );
				final ClipConvexPolytope< BV > ccp = si.getClipConvexPolytope();
				ccp.clip( visiblePolytopeGlobal );
				for ( final BV source : ccp.getInsideValues() )
				{
					source.localize( gPosS );
					transform.apply( gPosS, lPosS );
					double x1 = lPosS[ 0 ];
					double y1 = lPosS[ 1 ];
					for ( final BE edge : source.outgoingEdges() )
					{
						double dist = Double.POSITIVE_INFINITY;
						final Iterator< E > it = branchGraph.edgeBranchIterator( edge );
						while ( it.hasNext() )
						{
							final E e = it.next();
							final V target = e.getTarget( vertexRef );
							target.localize( gPosT );
							transform.apply( gPosT, lPosT );
							final double x2 = lPosT[ 0 ];
							final double y2 = lPosT[ 1 ];
							dist = Math.min( dist, GeometryUtils.segmentDist( x, y, x1, y1, x2, y2 ) );
							x1 = x2;
							y1 = y2;
						}

						if ( dist <= bestDist )
						{
							bestDist = dist;
							ref.refTo( edge );
							found = true;
						}
					}
				}
			}
			wrappedGraph.releaseRef( vertexRef );
		}
		finally
		{
			index.readLock().unlock();
		}
		return found ? ref : null;
	}

}
