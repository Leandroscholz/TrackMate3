package org.mastodon.revised.model.feature;

import java.util.Set;

import org.mastodon.revised.model.AbstractModel;
import org.mastodon.revised.ui.ProgressListener;
import org.scijava.service.SciJavaService;

/**
 * Service that can discover feature computers and execute computation.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <AM>
 *            the type of the model on which features are computed.
 */
public interface FeatureComputerService< AM extends AbstractModel< ?, ?, ? > > extends SciJavaService
{

	/**
	 * Returns the set of available feature computers for vertex features.
	 * 
	 * @return the available vertex feature computers.
	 */
	public Set< String > getAvailableVertexFeatureComputers();

	/**
	 * Returns the set of available feature computers for edge features.
	 * 
	 * @return the available edge feature computers.
	 */
	public Set< String > getAvailableEdgeFeatureComputers();

	/**
	 * Returns the set of available feature computers for branch vertex
	 * features.
	 * 
	 * @return the available branch vertex feature computers.
	 */
	public Set< String > getAvailableBranchVertexFeatureComputers();

	/**
	 * Returns the set of available feature computers for branch edge features.
	 * 
	 * @return the available branch edge feature computers.
	 */
	public Set< String > getAvailableBranchEdgeFeatureComputers();

	/**
	 * Executes feature computation for the specified computers on the specified
	 * model.
	 * 
	 * @param model
	 *            the model to compute features on.
	 * @param computerNames
	 *            what feature computers to compute.
	 * @param progressListener
	 *            a progress listener used to report computation progress.
	 * @return <code>true</code> if computation terminated successfully.
	 */
	public boolean compute( AM model, Set< String > computerNames, ProgressListener progressListener );

}