package org.mastodon.revised.model.feature;

import java.util.Set;

/**
 * Interface for classes that can return the tag-set keys currently defined in a
 * model.
 * 
 * @author Jean-Yves Tinevez
 */
public interface TagSetKeys
{

	/**
	 * Returns the set of known tag sets for the specified feature target.
	 * 
	 * @param target
	 *            the feature target.
	 * @return the set of feature keys defined for the specified target.
	 */
	public Set< String > getTagSets( FeatureTarget target );

	/**
	 * Returns the name of the tag-set with the specified key.
	 * 
	 * @param key
	 *            the tag-set key.
	 * @return the tag-set name, or an explanatory String if the key is unknown.
	 */
	public String getName( String key );

}
