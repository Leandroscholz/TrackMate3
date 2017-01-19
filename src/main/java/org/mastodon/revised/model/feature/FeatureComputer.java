package org.mastodon.revised.model.feature;

import java.util.Map;
import java.util.Set;

import org.mastodon.features.Feature;
import org.mastodon.revised.model.AbstractModel;
import org.scijava.plugin.SciJavaPlugin;

/**
 * Classes that compute a single feature value on a model.
 * <p>
 * Concrete implementations must be stateless, without side effects.
 *
 * @param <K>
 *            the type of the feature computed by this class.
 * @param <O>
 *            the target this feature applies to (vertices, edges, ...)
 * @param <AM>
 *            the type of model to use for computation.
 */
public interface FeatureComputer< K extends Feature< ?, O, ? >, O, AM extends AbstractModel< ?, ?, ? > > extends SciJavaPlugin
{

	/**
	 * Returns the set of dependencies of this feature computer.
	 * <p>
	 * Dependencies are expressed as the set of feature computer names.
	 *
	 * @return the set of dependencies.
	 */
	public Set< String > getDependencies();

	/**
	 * Performs feature computation on the specified model.
	 * 
	 * @param model
	 *            the model.
	 */
	public void compute( final AM model );

	/**
	 * Exposes the feature computed by this class.
	 * 
	 * @return the feature.
	 */
	public K getFeature();

	/**
	 * Returns the feature projections associated to the feature computed by
	 * this class. Projections are returned as a map of projection names to
	 * projection instances.
	 * 
	 * @return the feature projections.
	 */
	public Map< String, FeatureProjection< O > > getProjections();

	/**
	 * Returns the target on which the feature computed by this class is defined
	 * (vertices, edges, ...).
	 * 
	 * @return the feature target.
	 */
	public FeatureTarget getTarget();

}