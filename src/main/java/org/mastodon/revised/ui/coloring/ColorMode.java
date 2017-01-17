package org.mastodon.revised.ui.coloring;

import org.mastodon.revised.ui.util.ColorMap;

/**
 * Interface for settings that can store a coloring configuration. This
 * configuration is then used by objects that color vertices and edges, such as
 * {@link VertexColorGenerator}s and {@link EdgeColorGenerator}s.
 *
 * @author Jean-Yves Tinevez
 */
public interface ColorMode
{

	/**
	 * Supported modes for edge coloring.
	 */
	public enum EdgeColorMode
	{
		/** All edges have a single, common color. */
		FIXED,
		/**
		 * Each edge has a color that depends on a numerical feature defined for
		 * this edge.
		 */
		EDGE,
		/**
		 * Edges have a color determined by a numerical feature of their source
		 * vertex.
		 */
		SOURCE_VERTEX,
		/**
		 * Edges have a color determined by a numerical feature of their target
		 * vertex.
		 */
		TARGET_VERTEX,
		/**
		 * Edges have a color determined by a numerical feature of the branch
		 * edge they are linked to in the branch graph.
		 */
		BRANCH_EDGE,
		/**
		 * Edges have a color determined by a numerical feature of the source or
		 * target branch vertex of the branch edge they are linked to in the
		 * branch graph.
		 */
		BRANCH_VERTEX;
	}

	/**
	 * Supported modes for vertex coloring.
	 */
	public enum VertexColorMode
	{
		/** All vertices have a single, common color. */
		FIXED,
		/**
		 * Each vertex has a color determined by a numerical feature defined for
		 * this vertex.
		 */
		VERTEX,
		/**
		 * Vertices have a color determined by a numerical feature of their
		 * incoming edge, iff they have exactly one incoming edge. Otherwise,
		 * they have the 'undefined' color ( {@code ColorMap.get(Double.Nan)} ).
		 */
		INCOMING_EDGE,
		/**
		 * Vertices have a color determined by a numerical feature of their
		 * outgoing edge, iff they have exactly one outgoing edge. Otherwise,
		 * they have the 'undefined' color ( {@code ColorMap.get(Double.Nan)} ).
		 */
		OUTGOING_EDGE,
		/**
		 * Vertices have a color determined by a numerical feature of the
		 * incoming or outgoing branch edge of the branch vertex they link to in
		 * the branch graph, iff they have exactly one of them. Otherwise, they
		 * have the 'undefined' color ( {@code ColorMap.get(Double.Nan)} ).
		 */
		BRANCH_EDGE,
		/**
		 * Vertices have a color determined by a numerical feature of the branch
		 * vertex they link to in the branch graph.
		 */
		BRANCH_VERTEX;
	}

	/*
	 * Getters.
	 */

	/**
	 * Returns the color mode for vertices.
	 *
	 * @return the vertex color mode.
	 */
	public VertexColorMode getVertexColorMode();

	/**
	 * Returns the feature projection key to use for vertex color modes based on
	 * numerical feature values. The returned string might be the key of an edge
	 * feature projection, if the vertex color mode is set to mode that reads
	 * edge feature values.
	 *
	 * @return a feature projection key.
	 */
	public String getVertexFeatureKey();

	/**
	 * Returns the color map to use for vertex coloring for vertex color modes
	 * based on numerical features.
	 *
	 * @return a color map.
	 */
	public ColorMap getVertexColorMap();

	/**
	 * Returns the minimal range bound for the vertex color map. Vertices with
	 * feature values lower than the returned value will get the first color of
	 * the color map.
	 *
	 * @return the minimal feature range bound for the vertex color map.
	 */
	public double getMinVertexColorRange();

	/**
	 * Returns the maximal range bound for the vertex color map. Vertices with
	 * feature values higher than the returned value will get the last color of
	 * the color map.
	 *
	 * @return the maximal feature range bound for the vertex color map.
	 */
	public double getMaxVertexColorRange();

	/**
	 * Returns the color mode for edges.
	 *
	 * @return the vertex color mode.
	 */
	public EdgeColorMode getEdgeColorMode();

	/**
	 * Returns the feature projection key to use for edge color modes based on
	 * numerical feature values. The returned string might be the key of an
	 * vertex feature projection, if the edge color mode is set to mode that
	 * reads vertex feature values.
	 *
	 * @return a feature projection key.
	 */
	public String getEdgeFeatureKey();

	/**
	 * Returns the color map to use for edge coloring for edge color modes based
	 * on numerical features.
	 *
	 * @return a color map.
	 */
	public ColorMap getEdgeColorMap();

	/**
	 * Returns the minimal range bound for the edge color map. Edges with
	 * feature values lower than the returned value will get the first color of
	 * the color map.
	 *
	 * @return the minimal feature range bound for the edge color map.
	 */
	public double getMinEdgeColorRange();

	/**
	 * Returns the maximal range bound for the edge color map. Edges with
	 * feature values higher than the returned value will get the last color of
	 * the color map.
	 *
	 * @return the maximal feature range bound for the edge color map.
	 */
	public double getMaxEdgeColorRange();

	/*
	 * Setters.
	 */

	/**
	 * Sets the edge color mode and the key of the feature projection or a tag
	 * key to use for edge coloring.
	 *
	 * @param edgeColorMode
	 *            the edge color mode.
	 * @param featureKey
	 *            a key to a feature projection.
	 * @return this instance.
	 */
	public ColorMode edgeColorMode( final EdgeColorMode edgeColorMode, final String featureKey );

	/**
	 * Sets the vertex color mode and the key of the feature projection or a tag
	 * key to use for vertex coloring.
	 *
	 * @param vertexColorMode
	 *            the vertex color mode.
	 * @param featureKey
	 *            a key to a feature projection.
	 * @return this instance.
	 */
	public ColorMode vertexColorMode( final VertexColorMode vertexColorMode, final String featureKey );

	/**
	 * Sets the color map to use for edge coloring.
	 *
	 * @param colorMap
	 *            the color map.
	 * @return this instance.
	 */
	public ColorMode edgeColorMap( final ColorMap colorMap );

	/**
	 * Sets the color map to use for vertex coloring.
	 *
	 * @param colorMap
	 *            the color map.
	 * @return this instance.
	 */
	public ColorMode vertexColorMap( final ColorMap colorMap );

	/**
	 * Sets the minimal range bound for edge coloring.
	 *
	 * @param val
	 *            the minimal range bound.
	 *
	 * @return this instance.
	 */
	public ColorMode minEdgeColorRange( final double val );

	/**
	 * Sets the maximal range bound for edge coloring.
	 *
	 * @param val
	 *            the maximal range bound.
	 *
	 * @return this instance.
	 */
	public ColorMode maxEdgeColorRange( final double val );

	/**
	 * Sets the minimal range bound for vertex coloring.
	 *
	 * @param val
	 *            the minimal range bound.
	 *
	 * @return this instance.
	 */
	public ColorMode minVertexColorRange( final double val );

	/**
	 * Sets the maximal range bound for vertex coloring.
	 *
	 * @param val
	 *            the maximal range bound.
	 *
	 * @return this instance.
	 */
	public ColorMode maxVertexColorRange( final double val );

}
