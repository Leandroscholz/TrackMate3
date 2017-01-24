package org.mastodon.revised.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.mastodon.revised.bdv.overlay.ui.RenderSettingsChooser;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleChooser;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;

public class DisplaySettingsDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public DisplaySettingsDialog(
			final JFrame owner,
			final RenderSettingsManager renderSettingsManager,
			final TrackSchemeStyleManager trackschemeStyleManager,
			final FeatureKeys graphFeatureKeys,
			final FeatureRangeCalculator graphFeatureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		super( owner, "Display settings" );
		setLayout( new BorderLayout() );

		// BDV display settings.
		final RenderSettingsChooser bdvDisplaySettingsChooser =
				new RenderSettingsChooser( renderSettingsManager,
						graphFeatureKeys, graphFeatureRangeCalculator,
						branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );

		// TrackScheme display settings.
		final TrackSchemeStyleChooser trackSchemeStyleChooser =
				new TrackSchemeStyleChooser( trackschemeStyleManager,
						graphFeatureKeys, graphFeatureRangeCalculator,
						branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );

		// Tabbed pane.
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add( "bdv", bdvDisplaySettingsChooser.getPanel() );
		tabbedPane.add( "trackscheme", trackSchemeStyleChooser.getPanel() );

		getContentPane().add( tabbedPane, BorderLayout.CENTER );
		pack();
	}
}
