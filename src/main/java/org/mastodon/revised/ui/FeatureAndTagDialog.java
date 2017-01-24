package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.mastodon.revised.model.feature.FeatureComputerService;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.model.mamut.branchgraph.BranchEdge;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;
import org.mastodon.revised.ui.features.FeatureComputersPanel;
import org.mastodon.revised.ui.features.TagSetPanel;

public class FeatureAndTagDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public FeatureAndTagDialog( final JFrame owner, final Model model, final FeatureComputerService< Model > computerService )
	{
		super( owner, "Features and tags" );

		// Feature computing panel.
		final FeatureComputersPanel featureComputersPanel = new FeatureComputersPanel( computerService, model );

		// Tag sets panels.
		final TagSetPanel< Spot > tagVertices = new TagSetPanel<>( model.getGraph().vertices() );
		final TagSetPanel< Link > tagEdges = new TagSetPanel<>( model.getGraph().edges() );
		final TagSetPanel< BranchVertex > tagBranchVertices = new TagSetPanel<>( model.getBranchGraph().vertices() );
		final TagSetPanel< BranchEdge > tagBranchEdge = new TagSetPanel<>( model.getBranchGraph().edges() );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane( JTabbedPane.TOP );
		tabbedPane.add( "features", featureComputersPanel );
		tabbedPane.add( "spot tags", tagVertices );
		tabbedPane.add( "link tags", tagEdges );
		tabbedPane.add( "branch spot tags", tagBranchVertices );
		tabbedPane.add( "branch link tags", tagBranchEdge );
		getContentPane().add( tabbedPane, BorderLayout.CENTER );
		pack();
	}

}
