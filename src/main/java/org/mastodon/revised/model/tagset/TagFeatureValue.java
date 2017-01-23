package org.mastodon.revised.model.tagset;

import org.mastodon.features.FeatureValue;
import org.mastodon.features.NotifyFeatureValueChange;

import gnu.trove.map.TIntObjectArrayMap;
import gnu.trove.map.TObjectIntMap;

public class TagFeatureValue< O > implements FeatureValue< Tag >
{

	final TObjectIntMap< O > featureMap;

	private final O object;

	private final NotifyFeatureValueChange notify;

	private final TIntObjectArrayMap< Tag > tags;

	TagFeatureValue( final TObjectIntMap< O > featureMap, final TIntObjectArrayMap< Tag > tags, final O object, final NotifyFeatureValueChange notify )
	{
		this.featureMap = featureMap;
		this.tags = tags;
		this.object = object;
		this.notify = notify;
	}

	@Override
	public void set( final Tag value )
	{
		notify.notifyBeforeFeatureChange();
		if ( value == null )
			featureMap.remove( object );
		else
			featureMap.put( object, value.index() );
	}

	@Override
	public void remove()
	{
		notify.notifyBeforeFeatureChange();
		featureMap.remove( object );
	}

	@Override
	public Tag get()
	{
		final int index = featureMap.get( object );
		return ( index == featureMap.getNoEntryValue() ) ? null : tags.get( index );
	}

	@Override
	public boolean isSet()
	{
		return featureMap.containsKey( object );
	}
}
