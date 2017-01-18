package org.mastodon.revised.model.feature;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mastodon.features.Feature;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;

public class DefaultFeatureModel< V extends Vertex< E >, E extends Edge< V > > implements FeatureModel< V, E >
{

	private final Map< String, FeatureTarget > featureTargets;

	private final Map< String, FeatureTarget > projectionTargets;

	private final Map< String, Feature< ?, ?, ? > > feature;

	private final Map< FeatureTarget, Map< String, FeatureProjection< ? > > > projections;

	private final EnumMap< FeatureTarget, Set< String > > featureKeys;

	private final EnumMap< FeatureTarget, Set< String > > projectionKeys;

	public DefaultFeatureModel()
	{
		featureTargets = new HashMap<>();
		feature = new HashMap<>();
		projections = new EnumMap<>( FeatureTarget.class );
		projectionTargets = new HashMap<>();
		featureKeys = new EnumMap<>( FeatureTarget.class );
		projectionKeys = new EnumMap<>( FeatureTarget.class );
	}

	@Override
	public void declareFeature( final FeatureComputer< ?, ?, ? > fc )
	{
		// Features.
		final FeatureTarget target = fc.getTarget();
		final String featureKey = fc.getFeature().getKey();
		featureTargets.put( featureKey, target );
		feature.put( featureKey, fc.getFeature() );

		// Feature keys.
		Set< String > fkeys = featureKeys.get( target );
		if ( null == fkeys )
		{
			fkeys = new HashSet<>();
			featureKeys.put( target, fkeys );
		}
		fkeys.add( featureKey );

		// Projections.
		Map< String, FeatureProjection< ? > > pmap = projections.get( target );
		if ( null == pmap )
		{
			pmap = new HashMap<>();
			projections.put( target, pmap );
		}
		pmap.putAll( fc.getProjections() );

		// Projection keys.
		Set< String > kset = projectionKeys.get( target );
		if (null == kset)
		{
			kset = new HashSet<>();
			projectionKeys.put( target, kset );
		}
		kset.addAll( fc.getProjections().keySet() );

		// Projections target.
		for ( final String projectionKey : fc.getProjections().keySet() )
			projectionTargets.put( projectionKey, target );
	}

	@Override
	public void clear()
	{
		featureTargets.clear();
		feature.clear();
		projections.clear();
		projectionTargets.clear();
		featureKeys.clear();
		projectionKeys.clear();
	}

	@Override
	public FeatureTarget getFeatureTarget( final String featureKey )
	{
		return featureTargets.get( featureKey );
	}

	@Override
	public FeatureTarget getProjectionTarget( final String projectionKey )
	{
		return projectionTargets.get( projectionKey );
	}

	@Override
	public Set< String > getFeatureKeys( final FeatureTarget target )
	{
		final Set< String > set = featureKeys.get( target );
		if ( null == set )
			return Collections.emptySet();
		return Collections.unmodifiableSet( set );
	}

	@Override
	public Set< String > getProjectionKeys( final FeatureTarget target )
	{
		final Set< String > set = projectionKeys.get( target );
		if ( null == set )
			return Collections.emptySet();
		return Collections.unmodifiableSet( set );
	}

	@Override
	public Feature< ?, ?, ? > getFeature( final String featureKey )
	{
		return feature.get( featureKey );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public FeatureProjection< E > getEdgeProjection( final String projectionKey )
	{
		if ( null == projections.get( FeatureTarget.EDGE ) )
			return null;
		return ( FeatureProjection< E > ) projections.get( FeatureTarget.EDGE ).get( projectionKey );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public FeatureProjection< V > getVertexProjection( final String projectionKey )
	{
		if ( null == projections.get( FeatureTarget.VERTEX ) )
			return null;
		return ( FeatureProjection< V > ) projections.get( FeatureTarget.VERTEX ).get( projectionKey );
	}
}
