package org.mastodon.revised.mamut;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ActionMap;
import javax.swing.InputMap;

import org.mastodon.adapter.FocusAdapter;
import org.mastodon.adapter.HighlightAdapter;
import org.mastodon.adapter.NavigationHandlerAdapter;
import org.mastodon.adapter.RefBimap;
import org.mastodon.adapter.SelectionAdapter;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.revised.bdv.BigDataViewerMaMuT;
import org.mastodon.revised.bdv.overlay.BdvHighlightHandler;
import org.mastodon.revised.bdv.overlay.BdvSelectionBehaviours;
import org.mastodon.revised.bdv.overlay.EditBehaviours;
import org.mastodon.revised.bdv.overlay.EditSpecialBehaviours;
import org.mastodon.revised.bdv.overlay.OverlayContext;
import org.mastodon.revised.bdv.overlay.OverlayGraphRenderer;
import org.mastodon.revised.bdv.overlay.OverlayNavigation;
import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.mastodon.revised.bdv.overlay.RenderSettings.UpdateListener;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsDialog;
import org.mastodon.revised.bdv.overlay.wrap.OverlayContextWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayEdgeWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayEdgeWrapperBimap;
import org.mastodon.revised.bdv.overlay.wrap.OverlayGraphWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayVertexWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayVertexWrapperBimap;
import org.mastodon.revised.context.Context;
import org.mastodon.revised.context.ContextListener;
import org.mastodon.revised.context.ContextProvider;
import org.mastodon.revised.mamut.TrackSchemeManager.TsWindow;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.ModelOverlayProperties;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.ui.HighlightBehaviours;
import org.mastodon.revised.ui.SelectionActions;
import org.mastodon.revised.ui.grouping.GroupHandle;
import org.mastodon.revised.ui.selection.FocusListener;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.HighlightListener;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationHandlerImp;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionListener;
import org.scijava.ui.behaviour.KeyStrokeAdder;

import bdv.tools.ToggleDialogAction;
import bdv.viewer.ViewerFrame;
import bdv.viewer.ViewerPanel;

public class BdvManager
{

	private final MamutAppModel mamutAppModel;

	private final MamutWindowModel mamutWindowModel;

	public BdvManager( final MamutAppModel mamutAppModel, final MamutWindowModel mamutWindowModel )
	{
		this.mamutAppModel = mamutAppModel;
		this.mamutWindowModel = mamutWindowModel;
	}

	private synchronized void addBdvWindow( final BdvWindow w )
	{
		w.getViewerFrame().addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				removeBdvWindow( w );
			}
		} );
		mamutWindowModel.bdvWindows.add( w );
		mamutWindowModel.contextProviders.add( w.getContextProvider() );
		for ( final TsWindow tsw : mamutWindowModel.tsWindows )
			tsw.getContextChooser().updateContextProviders( mamutWindowModel.contextProviders );
	}

	private synchronized void removeBdvWindow( final BdvWindow w )
	{
		mamutWindowModel.bdvWindows.remove( w );
		mamutWindowModel.contextProviders.remove( w.getContextProvider() );
		for ( final TsWindow tsw : mamutWindowModel.tsWindows )
			tsw.getContextChooser().updateContextProviders( mamutWindowModel.contextProviders );
	}

	private int bdvName = 1;

	ViewerFrame createBigDataViewer()
	{
		final GroupHandle bdvGroupHandle = mamutWindowModel.groupManager.createGroupHandle();

		final OverlayGraphWrapper< Spot, Link > overlayGraph = new OverlayGraphWrapper<>(
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraphIdBimap(),
				mamutAppModel.model.getSpatioTemporalIndex(),
				new ModelOverlayProperties( mamutAppModel.model.getGraph(), mamutAppModel.radiusStats ) );
		final RefBimap< Spot, OverlayVertexWrapper< Spot, Link > > vertexMap = new OverlayVertexWrapperBimap<>( overlayGraph );
		final RefBimap< Link, OverlayEdgeWrapper< Spot, Link > > edgeMap = new OverlayEdgeWrapperBimap<>( overlayGraph );

		final HighlightModel< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayHighlight = new HighlightAdapter<>( mamutAppModel.highlightModel, vertexMap, edgeMap );
		final FocusModel< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayFocus = new FocusAdapter<>( mamutAppModel.focusModel, vertexMap, edgeMap );
		final Selection< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlaySelection = new SelectionAdapter<>( mamutAppModel.selection, vertexMap, edgeMap );
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( bdvGroupHandle );
		final NavigationHandler< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayNavigationHandler = new NavigationHandlerAdapter<>( navigationHandler, vertexMap, edgeMap );
		final String windowTitle = "BigDataViewer " + ( bdvName++ );
		final BigDataViewerMaMuT bdv = BigDataViewerMaMuT.open( mamutAppModel.sharedBdvData, windowTitle, bdvGroupHandle );
		final ViewerFrame viewerFrame = bdv.getViewerFrame();
		final ViewerPanel viewer = bdv.getViewer();
		final OverlayGraphRenderer< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > tracksOverlay = new OverlayGraphRenderer<>(
				overlayGraph,
				overlayHighlight,
				overlayFocus,
				overlaySelection );
		viewer.getDisplay().addOverlayRenderer( tracksOverlay );
		viewer.addRenderTransformListener( tracksOverlay );
		viewer.addTimePointListener( tracksOverlay );
		overlayHighlight.addHighlightListener( new HighlightListener()
		{
			@Override
			public void highlightChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		overlayFocus.addFocusListener( new FocusListener()
		{
			@Override
			public void focusChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		mamutAppModel.model.getGraph().addGraphChangeListener( new GraphChangeListener()
		{
			@Override
			public void graphChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		mamutAppModel.model.getGraph().addVertexPositionListener( ( v ) -> viewer.getDisplay().repaint() );
		overlaySelection.addSelectionListener( new SelectionListener()
		{
			@Override
			public void selectionChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		// TODO: remember those listeners and remove them when the BDV window is
		// closed!!!

		final OverlayNavigation< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayNavigation = new OverlayNavigation<>( viewer, overlayGraph );
		overlayNavigationHandler.addNavigationListener( overlayNavigation );

		final BdvHighlightHandler< ?, ? > highlightHandler = new BdvHighlightHandler<>( overlayGraph, tracksOverlay, overlayHighlight );
		viewer.getDisplay().addHandler( highlightHandler );
		viewer.addRenderTransformListener( highlightHandler );

		final BdvSelectionBehaviours< ?, ? > selectionBehaviours = new BdvSelectionBehaviours<>( overlayGraph, tracksOverlay, overlaySelection, overlayNavigationHandler );
		selectionBehaviours.installBehaviourBindings( viewerFrame.getTriggerbindings(), mamutWindowModel.keyconf );

		final OverlayContext< OverlayVertexWrapper< Spot, Link > > overlayContext = new OverlayContext<>( overlayGraph, tracksOverlay );
		viewer.addRenderTransformListener( overlayContext );
		final BdvContextAdapter< Spot > contextProvider = new BdvContextAdapter<>( windowTitle );
		final OverlayContextWrapper< Spot, Link > overlayContextWrapper = new OverlayContextWrapper<>(
				overlayContext,
				contextProvider );

		UndoActions.installActionBindings( viewerFrame.getKeybindings(), mamutAppModel.model, mamutWindowModel.keyconf );
		EditBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), mamutWindowModel.keyconf, overlayGraph, tracksOverlay, mamutAppModel.model );
		EditSpecialBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), mamutWindowModel.keyconf, viewerFrame.getViewerPanel(), overlayGraph, tracksOverlay, mamutAppModel.model );
		HighlightBehaviours.installActionBindings(
				viewerFrame.getTriggerbindings(),
				mamutWindowModel.keyconf,
				new String[] { "bdv" },
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraph(),
				mamutAppModel.highlightModel,
				mamutAppModel.model );
		SelectionActions.installActionBindings(
				viewerFrame.getKeybindings(),
				mamutWindowModel.keyconf,
				new String[] { "bdv" },
				mamutAppModel.model.getGraph(),
				mamutAppModel.model.getGraph(),
				mamutAppModel.selection,
				mamutAppModel.model );

		// TODO revise
		// RenderSettingsDialog triggered by "R"
		final RenderSettings renderSettings = RenderSettings.defaultStyle();
		final String RENDER_SETTINGS = "render settings";
		final RenderSettingsDialog renderSettingsDialog = new RenderSettingsDialog( viewerFrame, renderSettings );
		final ActionMap actionMap = new ActionMap();
		new ToggleDialogAction( RENDER_SETTINGS, renderSettingsDialog ).put( actionMap );

		final InputMap inputMap = new InputMap();
		final KeyStrokeAdder a = mamutWindowModel.keyconf.keyStrokeAdder( inputMap, "mamut" );
		a.put( RENDER_SETTINGS, "R" );
		viewerFrame.getKeybindings().addActionMap( "mamut", actionMap );
		viewerFrame.getKeybindings().addInputMap( "mamut", inputMap );
		renderSettings.addUpdateListener( new UpdateListener()
		{
			@Override
			public void renderSettingsChanged()
			{
				tracksOverlay.setRenderSettings( renderSettings );
				// TODO: less hacky way of triggering repaint and context update
				viewer.repaint();
				overlayContextWrapper.contextChanged( overlayContext );
			}
		} );

		final BdvWindow bdvWindow = new BdvWindow( viewerFrame, tracksOverlay, bdvGroupHandle, contextProvider );
		addBdvWindow( bdvWindow );

		return viewerFrame;
	}

	/**
	 * Information for one BigDataViewer window.
	 */
	public static class BdvWindow
	{
		private final ViewerFrame viewerFrame;

		private final OverlayGraphRenderer< ?, ? > tracksOverlay;

		private final GroupHandle groupHandle;

		private final ContextProvider< Spot > contextProvider;

		public BdvWindow(
				final ViewerFrame viewerFrame,
				final OverlayGraphRenderer< ?, ? > tracksOverlay,
				final GroupHandle groupHandle,
				final ContextProvider< Spot > contextProvider )
		{
			this.viewerFrame = viewerFrame;
			this.tracksOverlay = tracksOverlay;
			this.groupHandle = groupHandle;
			this.contextProvider = contextProvider;
		}

		public ViewerFrame getViewerFrame()
		{
			return viewerFrame;
		}

		public OverlayGraphRenderer< ?, ? > getTracksOverlay()
		{
			return tracksOverlay;
		}

		public GroupHandle getGroupHandle()
		{
			return groupHandle;
		}

		public ContextProvider< Spot > getContextProvider()
		{
			return contextProvider;
		}
	}

	/**
	 * TODO!!! related to {@link OverlayContextWrapper}
	 *
	 * @param <V>
	 *            the type of vertices in the model.
	 *
	 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
	 */
	static class BdvContextAdapter< V > implements ContextListener< V >, ContextProvider< V >
	{
		private final String contextProviderName;

		private final ArrayList< ContextListener< V > > listeners;

		private Context< V > context;

		public BdvContextAdapter( final String contextProviderName )
		{
			this.contextProviderName = contextProviderName;
			listeners = new ArrayList<>();
		}

		@Override
		public String getContextProviderName()
		{
			return contextProviderName;
		}

		@Override
		public synchronized boolean addContextListener( final ContextListener< V > l )
		{
			if ( !listeners.contains( l ) )
			{
				listeners.add( l );
				l.contextChanged( context );
				return true;
			}
			return false;
		}

		@Override
		public synchronized boolean removeContextListener( final ContextListener< V > l )
		{
			return listeners.remove( l );
		}

		@Override
		public synchronized void contextChanged( final Context< V > context )
		{
			this.context = context;
			for ( final ContextListener< V > l : listeners )
				l.contextChanged( context );
		}
	}
}
