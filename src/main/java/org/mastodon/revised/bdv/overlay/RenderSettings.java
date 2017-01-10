package org.mastodon.revised.bdv.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;

import org.mastodon.revised.ui.ColorMode;
import org.mastodon.revised.ui.util.ColorMap;

public class RenderSettings implements ColorMode
{
	/*
	 * PUBLIC DISPLAY CONFIG DEFAULTS.
	 */

	public static final int DEFAULT_LIMIT_TIME_RANGE = 20;
	public static final double DEFAULT_LIMIT_FOCUS_RANGE = 100.;
	public static final boolean DEFAULT_USE_ANTI_ALIASING = true;
	public static final boolean DEFAULT_USE_GRADIENT = false;
	public static final boolean DEFAULT_DRAW_SPOTS = true;
	public static final boolean DEFAULT_DRAW_LINKS = true;
	public static final boolean DEFAULT_DRAW_ELLIPSE = true;
	public static final boolean DEFAULT_DRAW_SLICE_INTERSECTION = true;
	public static final boolean DEFAULT_DRAW_SLICE_PROJECTION = !DEFAULT_DRAW_SLICE_INTERSECTION;
	public static final boolean DEFAULT_DRAW_POINTS = !DEFAULT_DRAW_ELLIPSE || (DEFAULT_DRAW_ELLIPSE && DEFAULT_DRAW_SLICE_INTERSECTION);
	public static final boolean DEFAULT_DRAW_POINTS_FOR_ELLIPSE = false;
	public static final boolean DEFAULT_DRAW_SPOT_LABELS = false;
	public static final boolean DEFAULT_IS_FOCUS_LIMIT_RELATIVE = true;
	public static final double DEFAULT_ELLIPSOID_FADE_DEPTH = 0.2;
	public static final double DEFAULT_POINT_FADE_DEPTH = 0.2;
	public static final Stroke DEFAULT_SPOT_STROKE  = new BasicStroke();
	public static final Stroke DEFAULT_SPOT_HIGHLIGHT_STROKE  = new BasicStroke( 4f );
	public static final Stroke DEFAULT_SPOT_FOCUS_STROKE  = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 8f, 3f }, 0 );
	public static final Stroke DEFAULT_LINK_STROKE  = new BasicStroke();
	public static final Stroke DEFAULT_LINK_HIGHLIGHT_STROKE  = new BasicStroke( 3f );
	public static final Color DEFAULT_COLOR_1 = new Color(0f, 1f, 0.1f);
	public static final Color DEFAULT_COLOR_2 = new Color(1f, 0f, 0.1f);

	public interface UpdateListener
	{
		public void renderSettingsChanged();
	}

	private final ArrayList< UpdateListener > updateListeners;

	private RenderSettings()
	{
		updateListeners = new ArrayList<>();
	}

	public synchronized void set( final RenderSettings settings )
	{
		useAntialiasing = settings.useAntialiasing;
		useGradient = settings.useGradient;
		timeLimit = settings.timeLimit;
		drawLinks = settings.drawLinks;
		drawSpots = settings.drawSpots;
		drawEllipsoidSliceProjection = settings.drawEllipsoidSliceProjection;
		drawEllipsoidSliceIntersection = settings.drawEllipsoidSliceIntersection;
		drawPoints = settings.drawPoints;
		drawPointsForEllipses = settings.drawPointsForEllipses;
		drawSpotLabels = settings.drawSpotLabels;
		focusLimit = settings.focusLimit;
		isFocusLimitViewRelative = settings.isFocusLimitViewRelative;
		ellipsoidFadeDepth = settings.ellipsoidFadeDepth;
		pointFadeDepth = settings.pointFadeDepth;
		name = settings.name;
		color1 = settings.color1;
		color2 = settings.color2;
		drawLinkArrows = settings.drawLinkArrows;
		spotStroke = settings.spotStroke;
		spotFocusStroke = settings.spotFocusStroke;
		spotHighlightStroke = settings.spotHighlightStroke;
		linkStroke = settings.linkStroke;
		linkHighlightStroke = settings.linkHighlightStroke;
		vertexColorMode = settings.vertexColorMode;
		vertexFeatureKey = settings.vertexFeatureKey;
		vertexColorMap = settings.vertexColorMap;
		minVertexColorRange = settings.minVertexColorRange;
		maxVertexColorRange = settings.maxVertexColorRange;
		edgeColorMode = settings.edgeColorMode;
		edgeFeatureKey = settings.edgeFeatureKey;
		edgeColorMap = settings.edgeColorMap;
		minEdgeColorRange = settings.minEdgeColorRange;
		maxEdgeColorRange = settings.maxEdgeColorRange;
		notifyListeners();
	}

	@Override
	public void notifyListeners()
	{
		for ( final UpdateListener l : updateListeners )
			l.renderSettingsChanged();
	}

	public synchronized boolean addUpdateListener( final UpdateListener l )
	{
		if ( !updateListeners.contains( l ) )
		{
			updateListeners.add( l );
			return true;
		}
		return false;
	}

	public synchronized boolean removeUpdateListener( final UpdateListener l )
	{
		return updateListeners.remove( l );
	}

	/*
	 * DISPLAY SETTINGS FIELDS.
	 */

	/**
	 * Whether to use antialiasing (for drawing everything).
	 */
	private boolean useAntialiasing;

	/**
	 * If {@code true}, draw links using a gradient from source color to target
	 * color. If {@code false}, draw links using the target color.
	 */
	private boolean useGradient;

	/**
	 * Maximum number of timepoints into the past for which outgoing edges
	 * should be drawn.
	 */
	private int timeLimit;

	/**
	 * Whether to draw links (at all).
	 */
	private boolean drawLinks;

	/**
	 * Whether to draw link arrow heads.
	 */
	private boolean drawLinkArrows;

	/**
	 * Whether to draw spots (at all).
	 */
	private boolean drawSpots;

	/**
	 * Whether to draw the projections of spot ellipsoids onto the view plane.
	 */
	private boolean drawEllipsoidSliceProjection;

	/**
	 * Whether to draw the intersections of spot ellipsoids with the view plane.
	 */
	private boolean drawEllipsoidSliceIntersection;

	/**
	 * Whether to draw spot centers.
	 */
	private boolean drawPoints;

	/**
	 * Whether to draw spot centers also for those points that are visible as ellipses.
	 */
	private boolean drawPointsForEllipses;

	/**
	 * Whether to draw spot labels next to ellipses.
	 */
	private boolean drawSpotLabels;

	/**
	 * Maximum distance from view plane up to which to draw spots.
	 *
	 * <p>
	 * Depending on {@link #isFocusLimitViewRelative}, the distance is either in
	 * the current view coordinate system or in the global coordinate system. If
	 * {@code isFocusLimitViewRelative() == true} then the distance is in
	 * current view coordinates. For example, a value of 100 means that spots
	 * will be visible up to 100 pixel widths from the view plane. Thus, the
	 * effective focus range depends on the current zoom level. If
	 * {@code isFocusLimitViewRelative() == false} then the distance is in
	 * global coordinates. A value of 100 means that spots will be visible up to
	 * 100 units (of the global coordinate system) from the view plane.
	 *
	 * <p>
	 * Ellipsoids are drawn increasingly translucent the closer they are to
	 * {@link #focusLimit}. See {@link #ellipsoidFadeDepth}.
	 */
	private double focusLimit;

	/**
	 * Whether the {@link #focusLimit} is relative to the the current
	 * view coordinate system.
	 *
	 * <p>
	 * If {@code true} then the distance is in current view coordinates. For
	 * example, a value of 100 means that spots will be visible up to 100 pixel
	 * widths from the view plane. Thus, the effective focus range depends on
	 * the current zoom level. If {@code false} then the distance is in global
	 * coordinates. A value of 100 means that spots will be visible up to 100
	 * units (of the global coordinate system) from the view plane.
	 */
	private boolean isFocusLimitViewRelative;

	/**
	 * The ratio of {@link #focusLimit} at which ellipsoids start to
	 * fade. Ellipsoids are drawn increasingly translucent the closer they are
	 * to {@link #focusLimit}. Up to ratio {@link #ellipsoidFadeDepth}
	 * they are fully opaque, then their alpha value goes to 0 linearly.
	 */
	private double ellipsoidFadeDepth;

	/**
	 * The ratio of {@link #focusLimit} at which points start to
	 * fade. Points are drawn increasingly translucent the closer they are
	 * to {@link #focusLimit}. Up to ratio {@link #pointFadeDepth}
	 * they are fully opaque, then their alpha value goes to 0 linearly.
	 */
	private double pointFadeDepth;

	/**
	 * The name of this render settings.
	 */
	private String name;

	/**
	 * The stroke used to paint the spot outlines.
	 */
	private Stroke spotStroke;

	/**
	 * The stroke used to paint the selected spot outlines.
	 */
	private Stroke spotHighlightStroke;

	/**
	 * The stroke used to paint the focused spot outlines.
	 */
	private Stroke spotFocusStroke;

	/**
	 * The stroke used to paint links.
	 */
	private Stroke linkStroke;

	/**
	 * The stroke used to paint highlighted links.
	 */
	private Stroke linkHighlightStroke;

	/**
	 * The first color to paint links. The actual color of edges is interpolated
	 * from {@link #color1} to {@link #color2} along time.
	 */
	private Color color1;

	/**
	 * The second color to paint edges. The actual color of edges is
	 * interpolated from {@link #color1} to {@link #color2} along time.
	 */
	private Color color2;

	private EdgeColorMode edgeColorMode;

	private VertexColorMode vertexColorMode;

	private String vertexFeatureKey;

	private ColorMap vertexColorMap;

	private double minVertexColorRange;

	private double maxVertexColorRange;

	private String edgeFeatureKey;

	private ColorMap edgeColorMap;

	private double minEdgeColorRange;

	private double maxEdgeColorRange;

	/*
	 * ACCESSOR METHODS.
	 */

	/**
	 * Get the antialiasing setting.
	 *
	 * @return {@code true} if antialiasing is used.
	 */
	public boolean getUseAntialiasing()
	{
		return useAntialiasing;
	}

	/**
	 * Sets whether to use anti-aliasing for drawing.
	 *
	 * @param useAntialiasing
	 *            whether to use use anti-aliasing.
	 */
	public synchronized void setUseAntialiasing( final boolean useAntialiasing )
	{
		this.useAntialiasing = useAntialiasing;
	}

	/**
	 * Returns whether a gradient is used for drawing links.
	 *
	 * @return {@code true} if links are drawn using a gradient from source
	 *         color to target color, or {@code false}, if links are drawn using
	 *         the target color.
	 */
	public boolean getUseGradient()
	{
		return useGradient;
	}

	/**
	 * Sets whether to use a gradient for drawing links. If
	 * {@code useGradient=true}, draw links using a gradient from source color
	 * to target color. If {@code useGradient=false}, draw links using the
	 * target color.
	 *
	 * @param useGradient
	 *            whether to use a gradient for drawing links.
	 */
	public synchronized void setUseGradient( final boolean useGradient )
	{
		this.useGradient = useGradient;
	}

	/**
	 * Gets the maximum number of time-points into the past for which outgoing
	 * edges should be drawn.
	 *
	 * @return maximum number of time-points into the past to draw links.
	 */
	public int getTimeLimit()
	{
		return timeLimit;
	}

	/**
	 * Sets the maximum number of time-points into the past for which outgoing
	 * edges should be drawn.
	 *
	 * @param timeLimit
	 *            maximum number of time-points into the past to draw links.
	 */
	public synchronized void setTimeLimit( final int timeLimit )
	{
		this.timeLimit = timeLimit;
	}

	/**
	 * Gets whether to draw links (at all). For specific settings, see
	 * {@link #getTimeLimit()}, {@link #getUseGradient()}.
	 *
	 * @return {@code true} if links are drawn.
	 */
	public boolean getDrawLinks()
	{
		return drawLinks;
	}

	/**
	 * Sets whether to draw links (at all). For specific settings, see
	 * {@link #setTimeLimit(int)}, {@link #setUseGradient(boolean)}.
	 *
	 * @param drawLinks
	 *            whether to draw links.
	 */
	public synchronized void setDrawLinks( final boolean drawLinks )
	{
		this.drawLinks = drawLinks;
	}

	/**
	 * Gets whether to draw link arrow heads.
	 *
	 * @return {@code true} if link arrow heads are drawn.
	 */
	public boolean getDrawLinkArrows()
	{
		return drawLinkArrows;
	}

	/**
	 * Set whether to draw link arrow heads.
	 *
	 * @param drawLinkArrows
	 *            whether to draw link arrow heads.
	 */
	public synchronized void setDrawLinkArrows( final boolean drawLinkArrows )
	{
		this.drawLinkArrows = drawLinkArrows;
	}

	/**
	 * Gets the first color to paint links. The actual color of edges is
	 * interpolated from {@code color1} to {@code color2} along time.
	 *
	 * @return the first link color.
	 */
	public Color getLinkColor1()
	{
		return color1;
	}

	/**
	 * Sets the first color to paint links. The actual color of edges is
	 * interpolated from {@code color1} to {@code color2} along time.
	 *
	 * @param color1
	 *            the first link color.
	 */
	public synchronized void setLinkColor1( final Color color1 )
	{
		this.color1 = color1;
	}

	/**
	 * Gets the second color to paint links. The actual color of edges is
	 * interpolated from {@code color1} to {@code color2} along time.
	 *
	 * @return the second link color.
	 */
	public Color getLinkColor2()
	{
		return color2;
	}

	/**
	 * Sets the second color to paint links. The actual color of edges is
	 * interpolated from {@code color1} to {@code color2} along time.
	 *
	 * @param color2
	 *            the first link color.
	 */
	public synchronized void setLinkColor2( final Color color2 )
	{
		this.color2 = color2;
	}

	/**
	 * Gets the stroke used to paint links.
	 *
	 * @return the stroke used to paint links.
	 */
	public Stroke getLinkStroke()
	{
		return linkStroke;
	}

	/**
	 * Sets the stroke used to paint links.
	 *
	 * @param linkStroke
	 *            the stroke used to paint links.
	 */
	public synchronized void setLinkStroke( final Stroke linkStroke )
	{
		this.linkStroke = linkStroke;
	}

	/**
	 * Gets the stroke used to paint highlighted links.
	 *
	 * @return the stroke used to paint links.
	 */
	public Stroke getLinkHighlightStroke()
	{
		return linkHighlightStroke;
	}

	/**
	 * Sets the stroke used to paint highlighted links.
	 *
	 * @param linkHighlightStroke
	 *            the stroke used to paint highlighted links.
	 */
	public synchronized void setLinkHighlightStroke( final Stroke linkHighlightStroke )
	{
		this.linkHighlightStroke = linkHighlightStroke;
	}

	/**
	 * Gets whether to draw spots (at all). For specific settings, see other
	 * spot drawing settings.
	 *
	 * @return {@code true} if spots are to be drawn.
	 * @see #getDrawEllipsoidSliceIntersection()
	 * @see #getDrawEllipsoidSliceProjection()
	 * @see #getDrawSpotCenters()
	 * @see #getDrawSpotCentersForEllipses()
	 * @see #getDrawSpotLabels()
	 * @see #getEllipsoidFadeDepth()
	 * @see #getFocusLimit()
	 * @see #getFocusLimitViewRelative()
	 * @see #getPointFadeDepth()
	 */
	public boolean getDrawSpots()
	{
		return drawSpots;
	}

	/**
	 * Sets whether to draw spots (at all). For specific settings, see other
	 * spot drawing settings.
	 *
	 * @param drawSpots
	 *            whether to draw spots.
	 * @see #setDrawEllipsoidSliceIntersection(boolean)
	 * @see #setDrawEllipsoidSliceProjection(boolean)
	 * @see #setDrawSpotCenters(boolean)
	 * @see #setDrawSpotCentersForEllipses(boolean)
	 * @see #setDrawSpotLabels(boolean)
	 * @see #setEllipsoidFadeDepth(double)
	 * @see #setFocusLimit(double)
	 * @see #setFocusLimitViewRelative(boolean)
	 * @see #setPointFadeDepth(double)
	 */
	public synchronized void setDrawSpots( final boolean drawSpots )
	{
		this.drawSpots = drawSpots;
	}

	/**
	 * Get whether the projections of spot ellipsoids onto the view plane are
	 * drawn.
	 *
	 * @return {@code true} iff projections of spot ellipsoids onto the view
	 *         plane are drawn.
	 */
	public boolean getDrawEllipsoidSliceProjection()
	{
		return drawEllipsoidSliceProjection;
	}

	/**
	 * Set whether to draw the projections of spot ellipsoids onto the view
	 * plane.
	 *
	 * @param drawEllipsoidSliceProjection
	 *            whether to draw projections of spot ellipsoids onto the view
	 *            plane.
	 */
	public synchronized void setDrawEllipsoidSliceProjection( final boolean drawEllipsoidSliceProjection )
	{
		this.drawEllipsoidSliceProjection = drawEllipsoidSliceProjection;
	}

	/**
	 * Get whether the intersections of spot ellipsoids with the view plane are
	 * drawn.
	 *
	 * @return {@code true} iff intersections of spot ellipsoids with the view
	 *         plane are drawn.
	 */
	public boolean getDrawEllipsoidSliceIntersection()
	{
		return drawEllipsoidSliceIntersection;
	}

	/**
	 * Set whether to draw the intersections of spot ellipsoids with the view
	 * plane.
	 *
	 * @param drawEllipsoidSliceIntersection
	 *            whether to draw intersections of spot ellipsoids with the view
	 *            plane.
	 */
	public synchronized void setDrawEllipsoidSliceIntersection( final boolean drawEllipsoidSliceIntersection )
	{
		this.drawEllipsoidSliceIntersection = drawEllipsoidSliceIntersection;
	}

	/**
	 * Get whether spot centers are drawn.
	 * <p>
	 * Note that spot centers are usually only drawn, if no ellipse for the spot
	 * was drawn (unless {@link #getDrawSpotCentersForEllipses()}
	 * {@code == true}).
	 *
	 * @return whether spot centers are drawn.
	 */
	public boolean getDrawSpotCenters()
	{
		return drawPoints;
	}

	/**
	 * Set whether spot centers are drawn.
	 * <p>
	 * Note that spot centers are usually only drawn, if no ellipse for the spot
	 * was drawn (unless {@link #getDrawSpotCentersForEllipses()}
	 * {@code == true}).
	 *
	 * @param drawPoints
	 *            whether spot centers are drawn.
	 */
	public synchronized void setDrawSpotCenters( final boolean drawPoints )
	{
		this.drawPoints = drawPoints;
	}

	/**
	 * Get whether spot centers are also drawn for those points that are visible
	 * as ellipses. See {@link #getDrawSpotCenters()}.
	 *
	 * @return whether spot centers are also drawn for those points that are
	 *         visible as ellipses.
	 */
	public boolean getDrawSpotCentersForEllipses()
	{
		return drawPointsForEllipses;
	}

	/**
	 * Set whether spot centers are also drawn for those points that are visible
	 * as ellipses.
	 *
	 * @param drawPointsForEllipses
	 *            whether spot centers are also drawn for those points that are
	 *            visible as ellipses.
	 */
	public synchronized void setDrawSpotCentersForEllipses( final boolean drawPointsForEllipses )
	{
		this.drawPointsForEllipses = drawPointsForEllipses;
	}

	/**
	 * Get whether spot labels are drawn next to ellipses.
	 *
	 * @return whether spot labels are drawn next to ellipses.
	 */
	public boolean getDrawSpotLabels()
	{
		return drawSpotLabels;
	}

	/**
	 * Set whether spot labels are drawn next to ellipses.
	 *
	 * @param drawSpotLabels
	 *            whether spot labels are drawn next to ellipses.
	 */
	public void setDrawSpotLabels( final boolean drawSpotLabels )
	{
		this.drawSpotLabels = drawSpotLabels;
	}

	/**
	 * Gets the stroke used to paint the spot outlines.
	 *
	 * @return the stroke used to paint the spot outlines.
	 */
	public Stroke getSpotStroke()
	{
		return spotStroke;
	}

	/**
	 * Sets the stroke used to paint the spot outlines.
	 *
	 * @param spotStroke
	 *            the stroke used to paint the spot outlines.
	 */
	public synchronized void setSpotStroke( final Stroke spotStroke )
	{
		this.spotStroke = spotStroke;
	}

	/**
	 * Gets the stroke used to paint the focused spot outlines.
	 *
	 * @return the stroke used to paint the focused spot outlines.
	 */
	public Stroke getSpotFocusStroke()
	{
		return spotStroke;
	}

	/**
	 * Sets the stroke used to paint the focused spot outlines.
	 *
	 * @param spotFocusStroke
	 *            the stroke used to paint the focused spot outlines.
	 */
	public synchronized void setSpotFocusStroke( final Stroke spotFocusStroke )
	{
		this.spotFocusStroke = spotFocusStroke;
	}

	/**
	 * Gets the stroke used to paint the highlighted spot outlines.
	 *
	 * @return the stroke used to paint the highlighted spot outlines.
	 */
	public Stroke getSpotHighlightStroke()
	{
		return spotHighlightStroke;
	}

	/**
	 * Sets the stroke used to paint the highlighted spot outlines.
	 *
	 * @param spotHighlightStroke
	 *            the stroke used to paint the highlighted spot outlines.
	 */
	public synchronized void setSpotHighlightStroke( final Stroke spotHighlightStroke )
	{
		this.spotHighlightStroke = spotHighlightStroke;
	}

	/**
	 * Get the maximum distance from the view plane up to which to spots are
	 * drawn.
	 * <p>
	 * Depending on {@link #getFocusLimitViewRelative()}, the distance is either
	 * in the current view coordinate system or in the global coordinate system.
	 * If {@code getFocusLimitViewRelative() == true} then the distance is in
	 * current view coordinates. For example, a value of 100 means that spots
	 * will be visible up to 100 pixel widths from the view plane. Thus, the
	 * effective focus range depends on the current zoom level. If
	 * {@code getFocusLimitViewRelative() == false} then the distance is in
	 * global coordinates. A value of 100 means that spots will be visible up to
	 * 100 units (of the global coordinate system) from the view plane.
	 * <p>
	 * Ellipsoids are drawn increasingly translucent the closer they are to the
	 * {@code focusLimit}. See {@link #getEllipsoidFadeDepth()}.
	 *
	 * @return the maximum distance from the view plane up to which to spots are
	 *         drawn.
	 */
	public double getFocusLimit()
	{
		return focusLimit;
	}

	/**
	 * Set the maximum distance from the view plane up to which to spots are
	 * drawn. See {@link #getFocusLimit()}.
	 *
	 * @param focusLimit
	 *            the maximum distance from the view plane up to which to spots
	 *            are drawn.
	 */
	public synchronized void setFocusLimit( final double focusLimit )
	{
		this.focusLimit = focusLimit;
	}

	/**
	 * Set whether the {@link #getFocusLimit()} is relative to the the current
	 * view coordinate system.
	 * <p>
	 * If {@code true} then the distance is in current view coordinates. For
	 * example, a value of 100 means that spots will be visible up to 100 pixel
	 * widths from the view plane. Thus, the effective focus range depends on
	 * the current zoom level. If {@code false} then the distance is in global
	 * coordinates. A value of 100 means that spots will be visible up to 100
	 * units (of the global coordinate system) from the view plane.
	 *
	 * @return {@code true} iff the {@link #getFocusLimit()} is relative to the
	 *         the current view coordinate system.
	 */
	public boolean getFocusLimitViewRelative()
	{
		return isFocusLimitViewRelative;
	}

	/**
	 * Set whether the {@link #getFocusLimit()} is relative to the the current
	 * view coordinate system. See {@link #getFocusLimitViewRelative()}.
	 *
	 * @param isFocusLimitViewRelative
	 *            whether the {@link #getFocusLimit()} is relative to the the
	 *            current view coordinate system.
	 */
	public synchronized void setFocusLimitViewRelative( final boolean isFocusLimitViewRelative )
	{
		this.isFocusLimitViewRelative = isFocusLimitViewRelative;
	}

	/**
	 * Get the ratio of {@link #getFocusLimit()} at which ellipsoids start to
	 * fade. Ellipsoids are drawn increasingly translucent the closer they are
	 * to {@link #getFocusLimit()}. Up to ratio {@link #getEllipsoidFadeDepth()}
	 * they are fully opaque, then their alpha value goes to 0 linearly.
	 *
	 * @return the ratio of {@link #getFocusLimit()} at which ellipsoids start
	 *         to fade.
	 */
	public double getEllipsoidFadeDepth()
	{
		return ellipsoidFadeDepth;
	}

	/**
	 * Set the ratio of {@link #getFocusLimit()} at which ellipsoids start to
	 * fade. See {@link #getEllipsoidFadeDepth()}.
	 *
	 * @param ellipsoidFadeDepth
	 *            the ratio of {@link #getFocusLimit()} at which ellipsoids
	 *            start to fade.
	 */
	public synchronized void setEllipsoidFadeDepth( final double ellipsoidFadeDepth )
	{
		this.ellipsoidFadeDepth = ellipsoidFadeDepth;
	}

	/**
	 * The ratio of {@link #getFocusLimit()} at which points start to fade.
	 * Points are drawn increasingly translucent the closer they are to
	 * {@link #getFocusLimit()}. Up to ratio {@link #getPointFadeDepth} they are
	 * fully opaque, then their alpha value goes to 0 linearly.
	 *
	 * @return the ratio of {@link #getFocusLimit()} at which points start to
	 *         fade.
	 */
	public double getPointFadeDepth()
	{
		return pointFadeDepth;
	}

	/**
	 * Set the ratio of {@link #getFocusLimit()} at which points start to fade.
	 * See {@link #getPointFadeDepth()}.
	 *
	 * @param pointFadeDepth
	 *            the ratio of {@link #getFocusLimit()} at which points start to
	 *            fade.
	 */
	public synchronized void setPointFadeDepth( final double pointFadeDepth )
	{
		this.pointFadeDepth = pointFadeDepth;
	}

	/**
	 * Gets the name of this render settings object.
	 *
	 * @return the name of this render settings object.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this render settings object.
	 *
	 * @param name
	 *            the name of this render settings object.
	 */
	public void setName( final String name )
	{
		this.name = name;
	}


	@Override
	public RenderSettings edgeColorMode( final EdgeColorMode edgeColorMode )
	{
		this.edgeColorMode = edgeColorMode;
		return this;
	}

	@Override
	public RenderSettings vertexColorMode( final VertexColorMode vertexColorMode )
	{
		this.vertexColorMode = vertexColorMode;
		return this;
	}

	@Override
	public VertexColorMode getVertexColorMode()
	{
		return vertexColorMode;
	}

	@Override
	public String getVertexFeatureKey()
	{
		return vertexFeatureKey;
	}

	@Override
	public ColorMap getVertexColorMap()
	{
		return vertexColorMap;
	}

	@Override
	public double getMinVertexColorRange()
	{
		return minVertexColorRange;
	}

	@Override
	public double getMaxVertexColorRange()
	{
		return maxVertexColorRange;
	}

	@Override
	public EdgeColorMode getEdgeColorMode()
	{
		return edgeColorMode;
	}

	@Override
	public String getEdgeFeatureKey()
	{
		return edgeFeatureKey;
	}

	@Override
	public ColorMap getEdgeColorMap()
	{
		return edgeColorMap;
	}

	@Override
	public double getMinEdgeColorRange()
	{
		return minEdgeColorRange;
	}

	@Override
	public double getMaxEdgeColorRange()
	{
		return maxEdgeColorRange;
	}

	@Override
	public RenderSettings edgeColorMap( final ColorMap colorMap )
	{
		this.edgeColorMap = colorMap;
		return this;
	}

	@Override
	public RenderSettings vertexColorMap( final ColorMap colorMap )
	{
		this.vertexColorMap = colorMap;
		return this;
	}

	@Override
	public RenderSettings edgeColorFeatureKey( final String key )
	{
		this.edgeFeatureKey = key;
		return this;
	}

	@Override
	public RenderSettings vertexColorFeatureKey( final String key )
	{
		this.vertexFeatureKey = key;
		return this;
	}

	@Override
	public RenderSettings minEdgeColorRange( final double val )
	{
		this.minEdgeColorRange = val;
		return this;
	}

	@Override
	public RenderSettings maxEdgeColorRange( final double val )
	{
		this.maxEdgeColorRange = val;
		return this;
	}

	@Override
	public RenderSettings minVertexColorRange( final double val )
	{
		this.minVertexColorRange = val;
		return this;
	}

	@Override
	public RenderSettings maxVertexColorRange( final double val )
	{
		this.maxVertexColorRange = val;
		return this;
	}

	/**
	 * Copy these render settings using the specified name.
	 *
	 * @param name
	 *            the name for the new render settings.
	 * @return a new render settings, identical to this one, but for the name.
	 */
	public RenderSettings copy( final String name )
	{
		final RenderSettings rs = new RenderSettings();
		rs.set( this );
		rs.setName( name );
		return rs;
	}

	@Override
	public String toString()
	{
		return name;
	}

	private static RenderSettings df;
	static
	{
		df = new RenderSettings();
		df.vertexColorMode = VertexColorMode.FIXED ;
		df.vertexFeatureKey = "";
		df.vertexColorMap = ColorMap.PARULA;
		df.minVertexColorRange = 0.;
		df.maxVertexColorRange = 1.;
		df.edgeColorMode = EdgeColorMode.FIXED;
		df.edgeFeatureKey = "";
		df.edgeColorMap = ColorMap.PARULA;
		df.minEdgeColorRange = 0.;
		df.maxEdgeColorRange = 1.;
		df.useAntialiasing = DEFAULT_USE_ANTI_ALIASING;
		df.useGradient = DEFAULT_USE_GRADIENT;
		df.timeLimit = DEFAULT_LIMIT_TIME_RANGE;
		df.drawLinks = DEFAULT_DRAW_LINKS;
		df.drawSpots = DEFAULT_DRAW_SPOTS;
		df.drawEllipsoidSliceProjection = DEFAULT_DRAW_SLICE_PROJECTION;
		df.drawEllipsoidSliceIntersection = DEFAULT_DRAW_SLICE_INTERSECTION;
		df.drawPoints = DEFAULT_DRAW_POINTS;
		df.drawPointsForEllipses = DEFAULT_DRAW_POINTS_FOR_ELLIPSE;
		df.drawSpotLabels = DEFAULT_DRAW_SPOT_LABELS;
		df.focusLimit = DEFAULT_LIMIT_FOCUS_RANGE;
		df.isFocusLimitViewRelative = DEFAULT_IS_FOCUS_LIMIT_RELATIVE;
		df.ellipsoidFadeDepth = DEFAULT_ELLIPSOID_FADE_DEPTH;
		df.pointFadeDepth = DEFAULT_POINT_FADE_DEPTH;
		df.spotStroke = DEFAULT_SPOT_STROKE;
		df.spotFocusStroke = DEFAULT_SPOT_FOCUS_STROKE;
		df.spotHighlightStroke = DEFAULT_SPOT_HIGHLIGHT_STROKE;
		df.linkStroke = DEFAULT_LINK_STROKE;
		df.linkHighlightStroke = DEFAULT_LINK_HIGHLIGHT_STROKE;
		df.color1 = DEFAULT_COLOR_1;
		df.color2 = DEFAULT_COLOR_2;
		df.name = "Default";
	}

	private static RenderSettings POINT_CLOUD;
	static
	{
		POINT_CLOUD = df.copy( "Point cloud" );
		POINT_CLOUD.drawLinks = false;
		POINT_CLOUD.drawEllipsoidSliceIntersection = false;
		POINT_CLOUD.isFocusLimitViewRelative = false;
	}

	private static RenderSettings ARROWS;
	static
	{
		ARROWS = df.copy( "Arrows" );
		ARROWS.drawSpots = false;
		ARROWS.drawLinkArrows = true;
		ARROWS.color2 = new Color( 55, 150, 126, 255 );
	}

	private static RenderSettings NONE;
	static
	{
		NONE = df.copy( "No overlay" );
		NONE.drawLinks = false;
		NONE.drawSpots = false;
	}

	public static Collection< RenderSettings > defaults;
	static
	{
		defaults = new ArrayList<>( 4 );
		defaults.add( df );
		defaults.add( POINT_CLOUD );
		defaults.add( ARROWS );
		defaults.add( NONE );
	}

	public static RenderSettings defaultStyle()
	{
		return df;
	}
}
