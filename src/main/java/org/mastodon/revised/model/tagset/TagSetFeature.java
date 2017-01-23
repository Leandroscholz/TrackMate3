package org.mastodon.revised.model.tagset;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureCleanup;
import org.mastodon.features.FeatureRegistry.DuplicateKeyException;
import org.mastodon.features.Features;
import org.mastodon.features.UndoFeatureMap;
import org.mastodon.features.WithFeatures;
import org.mastodon.revised.ui.util.ColorMap;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TIntObjectArrayMap;
import gnu.trove.map.TObjectIntMap;

/**
 * Tag-set feature.
 * <p>
 * Defines and manage a set of tags for a specified Ref collection. Tags can be
 * added and removed to the set managed by this feature instance.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <O>
 *            the type of objects that will labeled by this tag-set.
 */
public class TagSetFeature< O extends WithFeatures< O > > extends Feature< TObjectIntMap< O >, O, TagFeatureValue< O > >
{
	private final int noEntryValue;

	/**
	 * The map of tag indices to tags. Tags know their indices in this map, to
	 * be able to reverse it simply.
	 */
	private final TIntObjectArrayMap< Tag > tags;

	/**
	 * The map from object to Tag index. In other features, this is managed by
	 * the {@link Features} class, but here we need to keep a reference to it to
	 * be able to properly delete tags by iterating over the map.
	 * <p>
	 * TODO I do not know if this is a good idea. Check what happens while
	 * working on serialization.
	 */
	private final TObjectIntMap< O > map;

	/**
	 * The internal Tag id generator. Starts at 0.
	 */
	private final AtomicInteger tagID;

	/**
	 * A random number generator used to generate the default color of new tags.
	 */
	private final Random ran;

	private String name;

	public TagSetFeature( final String key, final String name, final RefCollection< O > pool ) throws DuplicateKeyException
	{
		super( key );
		this.name = name;
		this.noEntryValue = -1;
		this.tags = new TIntObjectArrayMap<>();
		this.map = RefCollections.createRefIntMap( pool, noEntryValue );
		this.tagID = new AtomicInteger( 0 );
		this.ran = new Random( 0l );
	}

	/**
	 * Returns the tags managed in this feature.
	 *
	 * @return the tags.
	 */
	public Collection< Tag > getTags()
	{
		return Collections.unmodifiableCollection( tags.valueCollection() );
	}

	/**
	 * Creates a new tag in this feature.
	 *
	 * @return the new tag.
	 */
	public Tag createTag()
	{
		final Tag tag = new Tag(
				tagID.getAndIncrement(),
				"label " + tagID.get(),
				ColorMap.GLASBEY.getColor( ran.nextInt( ColorMap.GLASBEY.getNColors() ) ) );
		tags.put( tag.index(), tag );
		return tag;
	}

	/**
	 * Removes the specified tag from this feature. Object with values set to
	 * this tag are unset.
	 *
	 * TODO Make it undoable.
	 *
	 * @param tag
	 *            the tag to remove.
	 * @return <code>true</code> if the specified tag belonged to this feature
	 *         and was successfully removed.
	 */
	public boolean removeTag( final Tag tag )
	{
		if ( !tags.containsValue( tag ) )
			return false;

		final int index = tag.index();
		final TObjectIntIterator< O > it = map.iterator();
		while(it.hasNext())
			if ( it.value() == index )
				it.remove();

		tags.remove( index );
		return true;
	}

	@Override
	protected TObjectIntMap< O > createFeatureMap( final RefCollection< O > pool )
	{
		return map;
	}

	@Override
	public TagFeatureValue< O > createFeatureValue( final O object, final Features< O > features )
	{
		return new TagFeatureValue<>(
				map,
				tags,
				object,
				new NotifyValueChange<>( features, this, object ) );
	}

	@Override
	protected FeatureCleanup< O > createFeatureCleanup( final TObjectIntMap< O > featureMap )
	{
		return new FeatureCleanup< O >()
		{
			@Override
			public void delete( final O object )
			{
				featureMap.remove( object );
			}
		};
	}

	@Override
	public UndoFeatureMap< O > createUndoFeatureMap( final TObjectIntMap< O > featureMap )
	{
		return new TagUndoFeatureMap<>( featureMap, noEntryValue );
	}

	@Override
	public String toString()
	{
		return name;
	}

	public void setName( final String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
