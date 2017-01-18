package org.mastodon.revised.ui.coloring;

import java.awt.Color;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.feature.FeatureProjection;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.util.ColorMap;

/**
 * This color generator expands the {@link FeaturesColorGenerator} by adding the
 * capability to color vertices and edges using branch vertices and edges
 * features. The branch graph and its feature model have to be specified.
 * <p>
 * This color generator can deal with the {@link VertexColorMode#BRANCH_VERTEX},
 * {@link VertexColorMode#BRANCH_EDGE}, {@link EdgeColorMode#BRANCH_VERTEX} and
 * {@link EdgeColorMode#BRANCH_EDGE} cases.
 *
 * @param <V>
 *            the type of vertices to color.
 * @param <E>
 *            the type of edges to color.
 * @param <BV>
 *            the type of the branch vertices.
 * @param <BE>
 *            the type of the branch edges.
 * @author Jean-Yves Tinevez
 */
public class FeaturesColorGeneratorBranchFeatures< V extends Vertex< E >, E extends Edge< V >, BV extends Vertex< BE >, BE extends Edge< BV > >
		extends FeaturesColorGenerator< V, E >
{

	private final BranchGraph< BV, BE, V, E > branchGraph;

	private final FeatureModel< BV, BE > branchFeatures;

	public FeaturesColorGeneratorBranchFeatures(
			final ColorMode colorMode,
			final ReadOnlyGraph< V, E > graph,
			final FeatureModel< V, E > features,
			final BranchGraph< BV, BE, V, E > branchGraph,
			final FeatureModel< BV, BE > branchFeatures )
	{
		super( colorMode, graph, features );
		this.branchGraph = branchGraph;
		this.branchFeatures = branchFeatures;
	}

	@Override
	public void colorModeChanged()
	{
		super.colorModeChanged();
		switch ( colorMode.getVertexColorMode() )
		{
		case BRANCH_VERTEX:
		{
			final FeatureProjection< BV > vfp = branchFeatures.getVertexProjection( colorMode.getVertexFeatureKey() );
			if ( null == vfp )
				vertexColorGenerator = new FixedVertexColorGenerator( colorMode.getVertexColorMap().getMissingColor() );
			else
				vertexColorGenerator = new BranchVertexVertexColorGenerator( vfp, colorMode.getVertexColorMap(), colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange() );
			break;
		}
		case BRANCH_EDGE:
		{
			final FeatureProjection< BE > vfp = branchFeatures.getEdgeProjection( colorMode.getVertexFeatureKey() );
			if ( null == vfp )
				vertexColorGenerator = new FixedVertexColorGenerator( colorMode.getVertexColorMap().getMissingColor() );
			else
				vertexColorGenerator = new BranchEdgeVertexColorGenerator( vfp, colorMode.getVertexColorMap(), colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange() );
			break;
		}
		default:
			break;
		}

		switch ( colorMode.getEdgeColorMode() )
		{
		default:
			break;
		case BRANCH_EDGE:
		{
			final FeatureProjection< BE > efp = branchFeatures.getEdgeProjection( colorMode.getEdgeFeatureKey() );
			if ( null == efp )
				edgeColorGenerator = new FixedEdgeColorGenerator( colorMode.getEdgeColorMap().getMissingColor() );
			else
				edgeColorGenerator = new BranchEdgeEdgeColorGenerator( efp, colorMode.getEdgeColorMap(), colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange() );
			break;
		}
		case BRANCH_VERTEX:
		{
			final FeatureProjection< BV > efp = branchFeatures.getVertexProjection( colorMode.getEdgeFeatureKey() );
			if ( null == efp )
				edgeColorGenerator = new FixedEdgeColorGenerator( colorMode.getEdgeColorMap().getMissingColor() );
			else
				edgeColorGenerator = new BranchVertexEdgeColorGenerator( efp, colorMode.getEdgeColorMap(), colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange() );
			break;
		}
		}
	}

	/*
	 * Colorer classes.
	 */

	private final class BranchVertexVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< BV > featureProjection;

		public BranchVertexVertexColorGenerator( final FeatureProjection< BV > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			final Color color;
			final BV ref = branchGraph.vertexRef();
			final V vref = graph.vertexRef();
			final E eref = graph.edgeRef();

			V source = vertex;
			BV bv = branchGraph.getBranchVertex( source, ref );
			while ( null == bv && !source.incomingEdges().isEmpty() )
			{
				// Climb up to find branch vertex
				source = source.incomingEdges().get( 0, eref ).getSource( vref );
				bv = branchGraph.getBranchVertex( source, ref );
			}

			if ( null == bv )
			{
				color = colorMap.get( Double.NaN );
			}
			else if ( !featureProjection.isSet( bv ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( bv );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			branchGraph.releaseRef( ref );
			graph.releaseRef( vref );
			graph.releaseRef( eref );
			return color;
		}
	}

	private class BranchEdgeVertexColorGenerator implements VertexColorGenerator< V >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< BE > featureProjection;

		public BranchEdgeVertexColorGenerator( final FeatureProjection< BE > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final V vertex )
		{
			final Color color;
			final BE ref = branchGraph.edgeRef();
			final BE be = branchGraph.getBranchEdge( vertex, ref );

			if ( null == be )
			{
				color = colorMap.get( Double.NaN );
			}
			else if ( !featureProjection.isSet( be ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( be );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			branchGraph.releaseRef( ref );
			return color;
		}
	}

	private class BranchEdgeEdgeColorGenerator implements EdgeColorGenerator< E >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< BE > featureProjection;

		public BranchEdgeEdgeColorGenerator( final FeatureProjection< BE > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			final Color color;
			final BE ref = branchGraph.edgeRef();
			final BE be = branchGraph.getBranchEdge( edge, ref );

			if ( !featureProjection.isSet( be ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( be );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			branchGraph.releaseRef( ref );
			return color;
		}
	}

	private final class BranchVertexEdgeColorGenerator implements EdgeColorGenerator< E >
	{

		private final ColorMap colorMap;

		private final double min;

		private final double max;

		private final FeatureProjection< BV > featureProjection;

		public BranchVertexEdgeColorGenerator( final FeatureProjection< BV > featureProjection, final ColorMap colorMap, final double min, final double max )
		{
			this.featureProjection = featureProjection;
			this.colorMap = colorMap;
			this.min = min;
			this.max = max;
		}

		@Override
		public Color color( final E edge )
		{
			final Color color;
			final BV ref = branchGraph.vertexRef();
			final V vref = graph.vertexRef();
			final E eref = graph.edgeRef();

			V source = edge.getSource( vref );
			BV bv = branchGraph.getBranchVertex( source, ref );
			while ( null == bv && !source.incomingEdges().isEmpty() )
			{
				// Climb up to find branch vertex
				source = source.incomingEdges().get( 0, eref ).getSource( vref );
				bv = branchGraph.getBranchVertex( source, ref );
			}

			if ( null == bv )
			{
				color = colorMap.get( Double.NaN );
			}
			else if ( !featureProjection.isSet( bv ) )
			{
				color = colorMap.getMissingColor();
			}
			else
			{
				final double value = featureProjection.value( bv );
				color = colorMap.get( ( value - min ) / ( max - min ) );
			}
			branchGraph.releaseRef( ref );
			graph.releaseRef( vref );
			graph.releaseRef( eref );
			return color;
		}
	}
}
