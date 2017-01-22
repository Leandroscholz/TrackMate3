package org.mastodon.revised.trackscheme.action;

import java.awt.event.ActionEvent;

import org.mastodon.revised.trackscheme.display.style.DefaultTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.coloring.FeaturesColorGenerator;
import org.scijava.ui.behaviour.util.AbstractNamedAction;

/**
 * Sets the style of a TrackScheme panel when executed and registers a listener
 * for style changes to repaint said panel.
 *
 * TODO remove
 */
public class TrackSchemeStyleAction extends AbstractNamedAction
{

	private static final long serialVersionUID = 1L;

	private final TrackSchemeStyle style;

	private final UpdateListener panelRepainter;

	private final DefaultTrackSchemeOverlay overlay;

	private final FeaturesColorGenerator< ?, ? > colorGenerator;

	public TrackSchemeStyleAction( final TrackSchemeStyle style, final DefaultTrackSchemeOverlay overlay, final UpdateListener panelRepainter, final FeaturesColorGenerator< ?, ? > colorGenerator )
	{
		super( style.getName() );
		this.style = style;
		this.overlay = overlay;
		this.panelRepainter = panelRepainter;
		this.colorGenerator = colorGenerator;
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		colorGenerator.setColorMode( style );
		// FIXME
		final TrackSchemeStyle oldStyle = overlay.getStyle();
		oldStyle.removeUpdateListener( panelRepainter );

		style.addUpdateListener( panelRepainter );
		overlay.setStyle( style );

		panelRepainter.trackSchemeStyleChanged();
	}
}