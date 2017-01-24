package org.mastodon.adapter;

import java.awt.Color;
import java.util.Collection;
import java.util.Set;

import org.mastodon.features.WithFeatures;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.model.feature.TagSetModel;
import org.mastodon.revised.model.feature.TagSetProjection;
import org.mastodon.revised.model.tagset.Tag;
import org.mastodon.revised.model.tagset.TagSetFeature;

public class TagSetModelAdapter< 
	V extends Vertex< E > & WithFeatures< V >, 
	E extends Edge< V > & WithFeatures< E >,
	WV extends Vertex< WE >, 
	WE extends Edge< WV > >
		implements TagSetModel< WV, WE >
{

	private final TagSetModel< V, E > tagsetModel;

	private final RefBimap< V, WV > vertexMap;

	private final RefBimap< E, WE > edgeMap;

	public TagSetModelAdapter(
			final TagSetModel< V, E > tagsetModel,
			final RefBimap< V, WV > vertexMap,
			final RefBimap< E, WE > edgeMap )
	{
		this.tagsetModel = tagsetModel;
		this.vertexMap = vertexMap;
		this.edgeMap = edgeMap;
	}

	@Override
	public Set< String > getTagSets( final FeatureTarget target )
	{
		return tagsetModel.getTagSets( target );
	}

	@Override
	public String getName( final String key )
	{
		return tagsetModel.getName( key );
	}

	@Override
	public TagSetProjection< WV > getVertexTagSetProjection( final String key )
	{
		final TagSetProjection< V > tsp = tagsetModel.getVertexTagSetProjection( key );
		if ( null == tsp )
			return null;
		return new TagSetProjectionAdapter< V, WV >( tsp, vertexMap );
	}

	@Override
	public TagSetProjection< WE > getEdgeTagSetProjection( final String key )
	{
		final TagSetProjection< E > tsp = tagsetModel.getEdgeTagSetProjection( key );
		if ( null == tsp )
			return null;
		return new TagSetProjectionAdapter< E, WE >( tsp, edgeMap );
	}

	@Override
	public void clear()
	{
		tagsetModel.clear();
	}

	@Override
	public void clearTagSets( final FeatureTarget target )
	{
		tagsetModel.clearTagSets( target );
	}

	@Override
	public void declareTagSet( final TagSetFeature< ? > tagset, final FeatureTarget target )
	{
		tagsetModel.declareTagSet( tagset, target );
	}

	@Override
	public void declareTagSets( final Collection< TagSetFeature< ? > > tagsets, final FeatureTarget target )
	{
		tagsetModel.declareTagSets( tagsets, target );
	}

	@Override
	public TagSetFeature< ? > getTagSet( final String key )
	{
		return tagsetModel.getTagSet( key );
	}

	private static final class TagSetProjectionAdapter< K, WK > implements TagSetProjection< WK >
	{

		private final TagSetProjection< K > tsp;

		private final RefBimap< K, WK > map;

		public TagSetProjectionAdapter( final TagSetProjection< K > tsp, final RefBimap< K, WK > map )
		{
			this.tsp = tsp;
			this.map = map;
		}

		@Override
		public boolean isSet( final WK obj )
		{
			return tsp.isSet( map.getLeft( obj ) );
		}

		@Override
		public Tag get( final WK obj )
		{
			return tsp.get( map.getLeft( obj ) );
		}

		@Override
		public Color getMissingColor()
		{
			return tsp.getMissingColor();
		}
	}
}
