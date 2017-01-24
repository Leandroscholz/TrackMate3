package org.mastodon.revised.model.feature;

import java.util.Collection;

import org.mastodon.revised.model.tagset.TagSetFeature;

/**
 * Interface for tag-set models.
 * 
 * @author Jean-Yves Tinevez
 * 
 * @param <V>
 *            the type of vertices in the model.
 * @param <E>
 *            the type of edges in the model.
 */
public interface TagSetModel< V, E > extends TagSetKeys
{

	/**
	 * Returns the tag-set feature with the specified key.
	 * 
	 * @param key
	 *            the tag-set key.
	 * @return the tag-set feature, or <code>null</code> if this key is unknown.
	 */
	public TagSetFeature< ? > getTagSet( String key );
	
	/**
	 * Returns the projection of the tag-set with the specified key.
	 * 
	 * @param key
	 *            the tag-set key.
	 * @return the tag-set projection, or <code>null</code> if this key is
	 *         unknown to this model or defined for another target than a
	 *         vertex.
	 */
	public TagSetProjection< V > getVertexTagSetProjection( String key );

	/**
	 * Returns the projection of the tag-set with the specified key.
	 * 
	 * @param key
	 *            the tag-set key.
	 * @return the tag-set projection, or <code>null</code> if this key is
	 *         unknown to this model or defined for another target than an edge.
	 */
	public TagSetProjection< E > getEdgeTagSetProjection( String key );

	/**
	 * Clear this tag-set model.
	 */
	public void clear();

	/**
	 * Clears the collection of tag-sets registered for the specified target.
	 * 
	 * @param target
	 *            the target for which to clear tag-sets.
	 */
	public void clearTagSets( FeatureTarget target );

	/**
	 * Registers the specified tag-set for the specified target.
	 * 
	 * @param tagset
	 *            the tag set feature to register.
	 * @param target
	 *            the target of this tag-set.
	 */
	public void declareTagSet( TagSetFeature< ? > tagset, FeatureTarget target );

	/**
	 * Registers the specified collection of tag-set for the specified target.
	 * 
	 * @param tagsets
	 *            the tag set features to register.
	 * @param target
	 *            the target of this tag-set.
	 */
	public void declareTagSets( Collection< TagSetFeature< ? > > tagsets, FeatureTarget target );


}
