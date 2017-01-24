package org.mastodon.revised.model.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.WithFeatures;
import org.mastodon.revised.model.tagset.TagSetFeature;

public class DefaultTagSetModel< V extends WithFeatures< V >, E extends WithFeatures< E > > implements TagSetModel< V, E >
{

	private final Map< FeatureTarget, Map< String, TagSetFeature< ? > > > tagsets;

	private final EnumMap< FeatureTarget, Set< String > > keys;

	public DefaultTagSetModel()
	{
		tagsets = new HashMap<>();
		keys = new EnumMap<>( FeatureTarget.class );
	}

	@Override
	public Set< String > getTagSets( final FeatureTarget target )
	{
		final Set< String > set = keys.get( target );
		if ( null == set )
			return Collections.emptySet();
		return Collections.unmodifiableSet( set );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public TagSetFeature< V > getVertexTagSet( final String key )
	{
		if ( null == tagsets.get( FeatureTarget.VERTEX ) )
			return null;
		return ( TagSetFeature< V > ) tagsets.get( FeatureTarget.VERTEX ).get( key );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public TagSetFeature< E > getEdgeTagSet( final String key )
	{
		if ( null == tagsets.get( FeatureTarget.EDGE ) )
			return null;
		return ( TagSetFeature< E > ) tagsets.get( FeatureTarget.EDGE ).get( key );
	}

	@Override
	public void clear()
	{
		tagsets.clear();
		keys.clear();
	}

	@Override
	public void clearTagSets( final FeatureTarget target )
	{
		if ( null != tagsets.get( target ) )
			tagsets.get( target ).clear();
		if ( null != keys.get( target ) )
			keys.get( target ).clear();
	}

	@Override
	public void declareTagSet( final TagSetFeature< ? > tagset, final FeatureTarget target )
	{
		Set< String > fkeys = keys.get( target );
		if ( null == fkeys )
		{
			fkeys = new HashSet<>();
			keys.put( target, fkeys );
		}
		fkeys.add( tagset.getKey() );

		Map< String, TagSetFeature< ? > > pmap = tagsets.get( target );
		if ( null == pmap )
		{
			pmap = new HashMap<>();
			tagsets.put( target, pmap );
		}
		pmap.put( tagset.getKey(), tagset );
	}

	@Override
	public void declareTagSets( final Collection< TagSetFeature< ? > > tagsets, final FeatureTarget target )
	{
		for ( final TagSetFeature< ? > tagset : tagsets )
			declareTagSet( tagset, target );
	}

	@Override
	public String getName( final String key )
	{
		TagSetFeature< ? > tagset = getVertexTagSet( key );
		if ( null == tagset )
		{
			tagset = getEdgeTagSet( key );
			if ( null == tagset )
				return "Unknown tag-set: " + key;
		}
		return tagset.getName();
	}

}
