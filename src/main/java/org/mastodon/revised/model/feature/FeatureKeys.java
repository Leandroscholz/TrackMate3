package org.mastodon.revised.model.feature;

import java.util.Set;

/**
 * Interface for classes that can return the feature keys and projection keys
 * currently defined for a model, and for what object types (feature targets)
 * they are defined.
 * 
 * @author Jean-Yves Tinevez
 */
public interface FeatureKeys
{

	/**
	 * Returns the target of the feature with the specified key.
	 * 
	 * @param featureKey
	 *            the feature key.
	 * @return the feature target, or <code>null</code> if the feature key is
	 *         unknown.
	 */
	public FeatureTarget getFeatureTarget( final String featureKey );

	/**
	 * Returns the set of known feature keys for the specified feature target.
	 * 
	 * @param target
	 *            the feature target.
	 * @return the set of feature keys defined for the specified target.
	 */
	public Set< String > getFeatureKeys( FeatureTarget target );

	/**
	 * Returns the target of the feature projection with the specified key.
	 * 
	 * @param projectionKey
	 *            the feature projection key.
	 * @return the feature projection target, or <code>null</code> if the
	 *         feature projection key is unknown.
	 */
	public FeatureTarget getProjectionTarget( final String projectionKey );

	/**
	 * Returns the set of known feature projection keys for the specified
	 * feature target.
	 * 
	 * @param target
	 *            the feature projection target.
	 * @return the set of feature projection keys defined for the specified
	 *         target.
	 */
	public Set< String > getProjectionKeys( FeatureTarget target );
}
