package org.mastodon.revised.model.feature;

import java.util.Set;

import org.mastodon.revised.model.AbstractModel;
import org.mastodon.revised.ui.ProgressListener;
import org.scijava.service.SciJavaService;

public interface FeatureComputerService< AM extends AbstractModel< ?, ?, ? > > extends SciJavaService
{

	public Set< String > getAvailableVertexFeatureComputers();

	public Set< String > getAvailableEdgeFeatureComputers();

	public Set< String > getAvailableBranchVertexFeatureComputers();

	public Set< String > getAvailableBranchEdgeFeatureComputers();

	public boolean compute( AM model, Set< String > computerNames, ProgressListener progressListener );


}