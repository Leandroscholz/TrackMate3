package org.mastodon.revised.model.feature;

/**
 * Interface for classes that can compute the min &amp; max value of a feature
 * projection over a collection.
 *
 * @author Jean-Yves Tinevez
 *
 */
public interface FeatureRangeCalculator
{

	/**
	 * Returns the range (min &amp; max values) of the feature projection with
	 * the specified key. Returns <code>null</code> if the projection is
	 * unknown.
	 *
	 * @param projectionKey
	 *            the feature projection key.
	 * @return a new <code>double[]</code> array of 2 elements: the first is the
	 *         min value, the second is the max value, or <code>null</code> if
	 *         the feature projection key is unknown.
	 */
	public double[] getRange( String projectionKey );
}
