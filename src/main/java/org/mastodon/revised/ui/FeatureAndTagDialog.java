package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.mastodon.revised.model.feature.FeatureComputerService;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.ui.features.FeatureComputersPanel;
import org.mastodon.revised.ui.features.TagSetPanel;
import org.mastodon.revised.ui.features.TagSetPanel.UpdateListener;

public class FeatureAndTagDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public FeatureAndTagDialog( final JFrame owner, final Model model, final FeatureComputerService< Model > computerService )
	{
		super( owner, "Features and tags" );

		// Feature computing panel.
		final FeatureComputersPanel featureComputersPanel = new FeatureComputersPanel( computerService, model );


		final TagSetPanel tagVertices = new TagSetPanel( model.getGraph().vertices() );
		tagVertices.addUpdateListener( new UpdateListener()
		{
			@Override
			public void tagSetCollectionUpdated()
			{
				model.getTagSetModel().clearTagSets( FeatureTarget.VERTEX );
				model.getTagSetModel().declareTagSets( tagVertices.getTagSets(), FeatureTarget.VERTEX );
			}
		} );
		
		final TagSetPanel tagEdges = new TagSetPanel( model.getGraph().edges() );
		tagEdges.addUpdateListener( () -> model.getTagSetModel().declareTagSets( tagEdges.getTagSets(), FeatureTarget.EDGE ) );

		final TagSetPanel tagBranchVertices = new TagSetPanel( model.getBranchGraph().vertices() );
		// TODO capture them in branch tag set model

		final TagSetPanel tagBranchEdge = new TagSetPanel( model.getBranchGraph().edges() );
		// TODO capture them in branch tag set model

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
