package org.mastodon.revised.ui.coloring;

import java.awt.Color;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;

/**
 * Color generators for vertices and edges from a feature model, following hints
 * from a {@link ColorMode}.
 * <p>
 * Can deal with vertex and edge features and attribute them to vertices and
 * edges. Interestingly, it can color a vertex using an edge feature, and
 * vice-versa, abiding to the different modes of {@link VertexColorMode} and
 * {@link EdgeColorMode}.
 * <p>
 * Unless explicitly specified, the generators returned here are not thread
 * safe.
 *
 * @author Jean-Yves Tinevez
 */
public class ColorGenerators
{

	private static final Color DEFAULT_EDGE_COLOR = Color.BLACK;

	private static final Color DEFAULT_VERTEX_COLOR = Color.BLACK;

	/**
	 * Returns a properly configured {@link VertexColorGenerator}.
	 *
	 * @param colorMode
	 *            the color mode that specified the color generator
	 *            configuration.
	 * @param graph
	 *            the graph whose vertices are to be colored.
	 * @param features
	 *            a feature model of the graph.
	 * @return a new {@link VertexColorGenerator}.
	 */
	public static final < V extends Vertex< E >, E extends Edge< V > > VertexColorGenerator< V > getVertexColorGenerator(
			final ColorMode colorMode,
			final ReadOnlyGraph< V, E > graph,
			final FeatureModel< V, E > features )
	{
		switch ( colorMode.getVertexColorMode() )
		{
		case FIXED:
		default:
			return new FixedVertexColorGenerator< V >( DEFAULT_VERTEX_COLOR );

		case INCOMING_EDGE:
		{
			final FeatureProjection< E > vfp = features.getEdgeProjection( colorMode.getVertexFeatureKey() );
			if ( null == vfp )
				return new FixedVertexColorGenerator< V >( colorMode.getVertexColorMap().getMissingColor() );
			else
				return new IncomingEdgeVertexColorGenerator< V, E >( vfp, colorMode.getVertexColorMap(), colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange(), graph.edgeRef() );
		}
		case OUTGOING_EDGE:
		{
			final FeatureProjection< E > vfp = features.getEdgeProjection( colorMode.getVertexFeatureKey() );
			if ( null == vfp )
				return new FixedVertexColorGenerator< V >( colorMode.getVertexColorMap().getMissingColor() );
			else
				return new OutgoingEdgeVertexColorGenerator< V, E >( vfp, colorMode.getVertexColorMap(), colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange(), graph.edgeRef() );
		}
		case VERTEX:
		{
			final FeatureProjection< V > vfp = features.getVertexProjection( colorMode.getVertexFeatureKey() );
			if ( null == vfp )
				return new FixedVertexColorGenerator< V >( colorMode.getVertexColorMap().getMissingColor() );
			else
				return new ThisVertexColorGenerator< V >( vfp, colorMode.getVertexColorMap(), colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange() );
		}
		}
	}

	/**
	 * Returns a properly configured {@link EdgeColorGenerator}.
	 *
	 * @param colorMode
	 *            the color mode that specified the color generator
	 *            configuration.
	 * @param graph
	 *            the graph whose edges are to be colored.
	 * @param features
	 *            a feature model of the graph.
	 * @return a new {@link EdgeColorGenerator}.
	 */
	public static final < V extends Vertex< E >, E extends Edge< V > > EdgeColorGenerator< E > getEdgeColorGenerator(
			final ColorMode colorMode,
			final ReadOnlyGraph< V, E > graph,
			final FeatureModel< V, E > features )
	{

		switch ( colorMode.getEdgeColorMode() )
		{
		case FIXED:
		default:
			return new FixedEdgeColorGenerator< E >( DEFAULT_EDGE_COLOR );

		case EDGE:
		{
			final FeatureProjection< E > efp = features.getEdgeProjection( colorMode.getEdgeFeatureKey() );
			if ( null == efp )
				return new FixedEdgeColorGenerator< E >( colorMode.getEdgeColorMap().getMissingColor() );
			else
				return new ThisEdgeColorGenerator< E >( efp, colorMode.getEdgeColorMap(), colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange() );
		}
		case SOURCE_VERTEX:
		{
			final FeatureProjection< V > efp = features.getVertexProjection( colorMode.getEdgeFeatureKey() );
			if ( null == efp )
				return new FixedEdgeColorGenerator< E >( colorMode.getEdgeColorMap().getMissingColor() );
			else
				return new SourceVertexEdgeColorGenerator< E, V >( efp, colorMode.getEdgeColorMap(), colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange(), graph.vertexRef() );
		}
		case TARGET_VERTEX:
		{
			final FeatureProjection< V > efp = features.getVertexProjection( colorMode.getEdgeFeatureKey() );
			if ( null == efp )
				return new FixedEdgeColorGenerator< E >( colorMode.getEdgeColorMap().getMissingColor() );
			else
				return new TargetVertexEdgeColorGenerator< E, V >( efp, colorMode.getEdgeColorMap(), colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange(), graph.vertexRef() );
		}
		}
	}

	/*
	 * Colorer classes.
	 */

	private static final class FixedVertexColorGenerator< V extends Vertex< ? > > implements VertexColorGenerator< V >
	{

		private final Color color;

		public FixedVertexColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final V vertex )
		{
			return color;
		}
	}

	private static final class ThisVertexColorGenerator< V extends Vertex< ? > > implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		public ThisVertexColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( !featureProjection.isSet( vertex ) )
				return colorMap.getMissingColor();

			final double value = featureProjection.value( vertex );
			return colorMap.get( ( value - min ) / ( max - min ) );
		}
	}

	private static final class IncomingEdgeVertexColorGenerator< V extends Vertex< E >, E extends Edge< V > >
	implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		private final E ref;

		public IncomingEdgeVertexColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max, final E ref )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
			this.ref = ref;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( vertex.incomingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final E edge = vertex.incomingEdges().get( 0, ref );
			if ( !featureProjection.isSet( edge ) )
				color = colorMap.getMissingColor();
			else
			{
				final double value = featureProjection.value( edge );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			return color;
		}
	}

	private static final class OutgoingEdgeVertexColorGenerator< V extends Vertex< E >, E extends Edge< V > > implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		private final E ref;

		public OutgoingEdgeVertexColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max, final E ref )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
			this.ref = ref;
		}

		@Override
		public Color color( final V vertex )
		{
			if ( vertex.outgoingEdges().size() != 1 )
				return colorMap.get( Double.NaN );

			final Color color;
			final E edge = vertex.outgoingEdges().get( 0, ref );
			if ( !featureProjection.isSet( edge ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( edge );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			return color;
		}
	}

	private static final class FixedEdgeColorGenerator< E extends Edge< ? > > implements EdgeColorGenerator< E >
	{

		private final Color color;

		public FixedEdgeColorGenerator( final Color color )
		{
			this.color = color;
		}

		@Override
		public Color color( final E edge )
		{
			return color;
		}
	}

	private static final class ThisEdgeColorGenerator< E extends Edge< ? > > implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< E > featureProjection;

		public ThisEdgeColorGenerator( final FeatureProjection< E > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			if ( !featureProjection.isSet( edge ) )
				return colorMap.getMissingColor();

			final double value = featureProjection.value( edge );
			return colorMap.get( ( value - min ) / ( max - min ) );
		}
	}

	private final static class SourceVertexEdgeColorGenerator< E extends Edge< V >, V extends Vertex< E > > implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		private final V ref;

		public SourceVertexEdgeColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max, final V ref )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
			this.ref = ref;
		}

		@Override
		public Color color( final E edge )
		{
			Color color;
			final V vertex = edge.getSource( ref );
			if ( !featureProjection.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( vertex );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			return color;
		}
	}

	private final static class TargetVertexEdgeColorGenerator< E extends Edge< V >, V extends Vertex< E > > implements EdgeColorGenerator< E >
	{
		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< V > featureProjection;

		private final V ref;

		public TargetVertexEdgeColorGenerator( final FeatureProjection< V > featureProjection, final ColorMap colorMap, final double min, final double max, final V ref )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
			this.ref = ref;
		}

		@Override
		public Color color( final E edge )
		{
			Color color;
			final V vertex = edge.getTarget( ref );
			if ( !featureProjection.isSet( vertex ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( vertex );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			return color;
		}
	}
}
