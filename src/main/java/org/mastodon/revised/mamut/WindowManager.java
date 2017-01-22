package org.mastodon.revised.mamut;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.mastodon.adapter.FeatureModelAdapter;
import org.mastodon.adapter.FocusAdapter;
import org.mastodon.adapter.HighlightAdapter;
import org.mastodon.adapter.NavigationHandlerAdapter;
import org.mastodon.adapter.RefBimap;
import org.mastodon.adapter.SelectionAdapter;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.graph.branch.BranchGraph;
import org.mastodon.revised.bdv.BigDataViewerMaMuT;
import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.bdv.overlay.BdvHighlightHandler;
import org.mastodon.revised.bdv.overlay.BdvSelectionBehaviours;
import org.mastodon.revised.bdv.overlay.EditBehaviours;
import org.mastodon.revised.bdv.overlay.EditSpecialBehaviours;
import org.mastodon.revised.bdv.overlay.OverlayBranchGraphRenderer;
import org.mastodon.revised.bdv.overlay.OverlayContext;
import org.mastodon.revised.bdv.overlay.OverlayGraphRenderer;
import org.mastodon.revised.bdv.overlay.OverlayNavigation;
import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.mastodon.revised.bdv.overlay.ui.RenderSettingsManager;
import org.mastodon.revised.bdv.overlay.wrap.OverlayBranchGraphWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayContextWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayEdgeWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayEdgeWrapperBimap;
import org.mastodon.revised.bdv.overlay.wrap.OverlayGraphWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayProperties;
import org.mastodon.revised.bdv.overlay.wrap.OverlayVertexWrapper;
import org.mastodon.revised.bdv.overlay.wrap.OverlayVertexWrapperBimap;
import org.mastodon.revised.context.Context;
import org.mastodon.revised.context.ContextChooser;
import org.mastodon.revised.context.ContextListener;
import org.mastodon.revised.context.ContextProvider;
import org.mastodon.revised.model.branchgraph.BranchGraphAdapter;
import org.mastodon.revised.model.branchgraph.BranchGraphFocusAdapter;
import org.mastodon.revised.model.branchgraph.BranchGraphHighlightAdapter;
import org.mastodon.revised.model.branchgraph.BranchGraphNavigationHandlerAdapter;
import org.mastodon.revised.model.branchgraph.BranchGraphSelectionAdapter;
import org.mastodon.revised.model.feature.FeatureModel;
import org.mastodon.revised.model.mamut.BoundingSphereRadiusStatistics;
import org.mastodon.revised.model.mamut.BranchGraphModelOverlayProperties;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.ModelOverlayProperties;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.model.mamut.branchgraph.BranchEdge;
import org.mastodon.revised.model.mamut.branchgraph.BranchVertex;
import org.mastodon.revised.model.mamut.branchgraph.ModelBranchGraph;
import org.mastodon.revised.trackscheme.TrackSchemeContextListener;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeEdgeBimap;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.TrackSchemeVertexBimap;
import org.mastodon.revised.trackscheme.action.TrackSchemeAction;
import org.mastodon.revised.trackscheme.action.TrackSchemeActionProvider;
import org.mastodon.revised.trackscheme.action.TrackSchemeBehaviour;
import org.mastodon.revised.trackscheme.action.TrackSchemeBehaviourProvider;
import org.mastodon.revised.trackscheme.action.TrackSchemeService;
import org.mastodon.revised.trackscheme.display.TrackSchemeEditBehaviours;
import org.mastodon.revised.trackscheme.display.TrackSchemeFrame;
import org.mastodon.revised.trackscheme.display.TrackSchemeOptions;
import org.mastodon.revised.trackscheme.display.TrackSchemePanel;
import org.mastodon.revised.trackscheme.display.style.BranchGraphTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.DefaultTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyleManager;
import org.mastodon.revised.trackscheme.wrap.DefaultModelGraphProperties;
import org.mastodon.revised.trackscheme.wrap.ModelGraphProperties;
import org.mastodon.revised.ui.HighlightBehaviours;
import org.mastodon.revised.ui.SelectionActions;
import org.mastodon.revised.ui.coloring.BranchGraphFeaturesColorGenerator;
import org.mastodon.revised.ui.coloring.FeaturesColorGenerator;
import org.mastodon.revised.ui.coloring.FeaturesColorGeneratorBranchFeatures;
import org.mastodon.revised.ui.grouping.GroupHandle;
import org.mastodon.revised.ui.grouping.GroupManager;
import org.mastodon.revised.ui.selection.FocusListener;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.FocusModelImp;
import org.mastodon.revised.ui.selection.HighlightListener;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.HighlightModelImp;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationHandlerImp;
import org.mastodon.revised.ui.selection.Selection;
import org.mastodon.revised.ui.selection.SelectionImp;
import org.mastodon.revised.ui.selection.SelectionListener;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.io.InputTriggerDescription;
import org.scijava.ui.behaviour.io.InputTriggerDescriptionsBuilder;
import org.scijava.ui.behaviour.io.yaml.YamlConfigIO;
import org.scijava.ui.behaviour.util.AbstractNamedAction;
import org.scijava.ui.behaviour.util.Actions;
import org.scijava.ui.behaviour.util.Behaviours;
import org.scijava.ui.behaviour.util.InputActionBindings;
import org.scijava.ui.behaviour.util.TriggerBehaviourBindings;

import bdv.spimdata.SpimDataMinimal;
import bdv.viewer.RequestRepaint;
import bdv.viewer.TimePointListener;
import bdv.viewer.ViewerFrame;
import bdv.viewer.ViewerOptions;
import bdv.viewer.ViewerPanel;
import mpicbg.spim.data.generic.AbstractSpimData;

public class WindowManager
{
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
	 * Information for one TrackScheme window.
	 */
	public static class TsWindow
	{
		private final TrackSchemeFrame trackSchemeFrame;

		private final GroupHandle groupHandle;

		private final ContextChooser< Spot > contextChooser;

		public TsWindow(
				final TrackSchemeFrame trackSchemeFrame,
				final GroupHandle groupHandle,
				final ContextChooser< Spot > contextChooser )
		{
			this.trackSchemeFrame = trackSchemeFrame;
			this.groupHandle = groupHandle;
			this.contextChooser = contextChooser;
		}

		public TrackSchemeFrame getTrackSchemeFrame()
		{
			return trackSchemeFrame;
		}

		public GroupHandle getGroupHandle()
		{
			return groupHandle;
		}

		public ContextChooser< Spot > getContextChooser()
		{
			return contextChooser;
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
	public static class BdvContextAdapter< V > implements ContextListener< V >, ContextProvider< V >
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

	private final Model model;

	private final InputTriggerConfig keyconf;

	private final GroupManager groupManager;

	private final SharedBigDataViewerData sharedBdvData;

	private final int minTimepoint;

	private final int maxTimepoint;

	private final Selection< Spot, Link > selection;

	private final HighlightModel< Spot, Link > highlightModel;

	private final FocusModel< Spot, Link > focusModel;

	private final BoundingSphereRadiusStatistics radiusStats;

	/**
	 * All currently open BigDataViewer windows.
	 */
	private final List< BdvWindow > bdvWindows = new ArrayList<>();

	/**
	 * The {@link ContextProvider}s of all currently open BigDataViewer windows.
	 */
	private final List< ContextProvider< Spot > > contextProviders = new ArrayList<>();

	/**
	 * All currently open TrackScheme windows.
	 */
	private final List< TsWindow > tsWindows = new ArrayList<>();

	private final RenderSettingsManager renderSettingsManager;

	private final TrackSchemeStyleManager trackSchemeStyleManager;

	final org.scijava.Context context;

	private final TrackSchemeBehaviourProvider trackSchemeBehaviourProvider;

	private final TrackSchemeActionProvider trackSchemeActionProvider;

	public WindowManager(
			final String spimDataXmlFilename,
			final SpimDataMinimal spimData,
			final Model model,
			final RenderSettingsManager bdvSettingsManager,
			final TrackSchemeStyleManager trackSchemeStyleManager,
			final InputTriggerConfig keyconf )
	{
		this.model = model;
		this.renderSettingsManager = bdvSettingsManager;
		this.trackSchemeStyleManager = trackSchemeStyleManager;
		this.keyconf = keyconf;

		groupManager = new GroupManager();
		final RequestRepaint requestRepaint = new RequestRepaint()
		{
			@Override
			public void requestRepaint()
			{
				for ( final BdvWindow w : bdvWindows )
					w.getViewerFrame().getViewerPanel().requestRepaint();
			}
		};
		sharedBdvData = new SharedBigDataViewerData( spimDataXmlFilename, spimData, ViewerOptions.options().inputTriggerConfig( keyconf ), requestRepaint );

		final ListenableReadOnlyGraph< Spot, Link > graph = model.getGraph();
		final GraphIdBimap< Spot, Link > idmap = model.getGraphIdBimap();
		selection = new SelectionImp<>( graph, idmap );
		highlightModel = new HighlightModelImp<>( idmap );
		radiusStats = new BoundingSphereRadiusStatistics( model );
		focusModel = new FocusModelImp<>( idmap );

		minTimepoint = 0;
		maxTimepoint = sharedBdvData.getNumTimepoints() - 1;
		/*
		 * TODO: (?) For now, we use timepoint indices in MaMuT model, instead
		 * of IDs/names. This is because BDV also displays timepoint index, and
		 * it would be confusing to have different labels in TrackScheme. If
		 * this is changed in the future, then probably only in the model files.
		 */

		/*
		 * TESTING the action provider and context thingies.
		 */
		this.context = new org.scijava.Context();
		this.trackSchemeBehaviourProvider = new TrackSchemeBehaviourProvider();
		context.inject( trackSchemeBehaviourProvider );
		this.trackSchemeActionProvider = new TrackSchemeActionProvider();
		context.inject( trackSchemeActionProvider );
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
		bdvWindows.add( w );
		contextProviders.add( w.getContextProvider() );
		for ( final TsWindow tsw : tsWindows )
			tsw.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void removeBdvWindow( final BdvWindow w )
	{
		bdvWindows.remove( w );
		contextProviders.remove( w.getContextProvider() );
		for ( final TsWindow tsw : tsWindows )
			tsw.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void addTsWindow( final TsWindow w )
	{
		w.getTrackSchemeFrame().addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				removeTsWindow( w );
			}
		} );
		tsWindows.add( w );
		w.getContextChooser().updateContextProviders( contextProviders );
	}

	private synchronized void removeTsWindow( final TsWindow w )
	{
		tsWindows.remove( w );
		w.getContextChooser().updateContextProviders( new ArrayList<>() );
	}

	// TODO
	private int bdvName = 1;

	public void createBigDataViewer()
	{
		final GroupHandle bdvGroupHandle = groupManager.createGroupHandle();

		final OverlayGraphWrapper< Spot, Link > overlayGraph = new OverlayGraphWrapper<>(
				model.getGraph(),
				model.getGraphIdBimap(),
				model.getSpatioTemporalIndex(),
				new ModelOverlayProperties( model.getGraph(), radiusStats ) );
		final RefBimap< Spot, OverlayVertexWrapper< Spot, Link > > vertexMap = new OverlayVertexWrapperBimap<>( overlayGraph );
		final RefBimap< Link, OverlayEdgeWrapper< Spot, Link > > edgeMap = new OverlayEdgeWrapperBimap<>( overlayGraph );

		final HighlightModel< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayHighlight = new HighlightAdapter<>( highlightModel, vertexMap, edgeMap );
		final FocusModel< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayFocus = new FocusAdapter<>( focusModel, vertexMap, edgeMap );
		final Selection< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlaySelection = new SelectionAdapter<>( selection, vertexMap, edgeMap );
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( bdvGroupHandle );
		final NavigationHandler< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > overlayNavigationHandler = new NavigationHandlerAdapter<>( navigationHandler, vertexMap, edgeMap );
		final String windowTitle = "BigDataViewer " + ( bdvName++ );
		final BigDataViewerMaMuT bdv = BigDataViewerMaMuT.open( sharedBdvData, windowTitle, bdvGroupHandle );
		final ViewerFrame viewerFrame = bdv.getViewerFrame();
		final ViewerPanel viewer = bdv.getViewer();

		// TODO: It's ok to create the wrappers here, but wiring up Listeners
		// should be done elsewhere

//		if ( !bdv.tryLoadSettings( bdvFile ) ) // TODO
//			InitializeViewerState.initBrightness( 0.001, 0.999, bdv.getViewer(), bdv.getSetupAssignments() );

		/*
		 * Features for the BDV.
		 */
		final FeatureModel< Spot, Link > featureModel = model.getGraphFeatureModel();
		final FeatureModel< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > bdvFeatures =
				new FeatureModelAdapter<>( featureModel, vertexMap, edgeMap );

		/*
		 * Color generator for BDV.
		 */

		final BranchGraph< BranchVertex, BranchEdge, OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > bdvBranchGraph =
				new BranchGraphAdapter<>( model.getBranchGraph(), vertexMap, edgeMap );
		final FeaturesColorGenerator< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > colorGenerator =
				new FeaturesColorGeneratorBranchFeatures<>( RenderSettings.defaultStyle(),
						overlayGraph, bdvFeatures,
						bdvBranchGraph, model.getBranchGraphFeatureModel() );

		viewer.setTimepoint( currentTimepoint );
		final OverlayGraphRenderer< OverlayVertexWrapper< Spot, Link >, OverlayEdgeWrapper< Spot, Link > > tracksOverlay = new OverlayGraphRenderer<>(
				overlayGraph,
				overlayHighlight,
				overlayFocus,
				overlaySelection,
				colorGenerator,
				colorGenerator );
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
		model.getGraph().addGraphChangeListener( new GraphChangeListener()
		{
			@Override
			public void graphChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		model.getGraph().addVertexPositionListener( ( v ) -> viewer.getDisplay().repaint() );
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
		selectionBehaviours.installBehaviourBindings( viewerFrame.getTriggerbindings(), keyconf );

		final OverlayContext< OverlayVertexWrapper< Spot, Link > > overlayContext = new OverlayContext<>( overlayGraph, tracksOverlay );
		viewer.addRenderTransformListener( overlayContext );
		final BdvContextAdapter< Spot > contextProvider = new BdvContextAdapter<>( windowTitle );
		final OverlayContextWrapper< Spot, Link > overlayContextWrapper = new OverlayContextWrapper<>(
				overlayContext,
				contextProvider );

		UndoActions.installActionBindings( viewerFrame.getKeybindings(), model, keyconf );
		EditBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), keyconf, overlayGraph, tracksOverlay, model );
		EditSpecialBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), keyconf, viewerFrame.getViewerPanel(), overlayGraph, tracksOverlay, model );
		HighlightBehaviours.installActionBindings(
				viewerFrame.getTriggerbindings(),
				keyconf,
				new String[] { "bdv" },
				model.getGraph(),
				model.getGraph(),
				highlightModel,
				model );
		SelectionActions.installActionBindings(
				viewerFrame.getKeybindings(),
				keyconf,
				new String[] { "bdv" },
				model.getGraph(),
				model.getGraph(),
				selection,
				model );

		/*
		 * TODO: this is still wrong. There should be one central entity syncing
		 * time for several BDV frames and TrackSchemePanel should listen to
		 * that. Ideally windows should be configurable to "share" timepoints or
		 * not.
		 */
		viewer.addTimePointListener( tpl );

		/*
		 * BDV menu.
		 */

		final BdvRenderSettingsUpdater panelRepainter = new BdvRenderSettingsUpdater( tracksOverlay, colorGenerator, viewer, false );
		final JMenu styleMenu = new JMenu( "Styles" );
		styleMenu.addMenuListener( new MenuListener()
		{
			@Override
			public void menuSelected( final MenuEvent e )
			{
				styleMenu.removeAll();
				for ( final RenderSettings rs : renderSettingsManager.getRenderSettings() )
					styleMenu.add( new JMenuItem(
							new BdvRenderSettingsAction( rs, panelRepainter ) ) );

			}

			@Override
			public void menuDeselected( final MenuEvent e )
			{}

			@Override
			public void menuCanceled( final MenuEvent e )
			{}
		} );
		viewerFrame.getJMenuBar().add( styleMenu );

		/*
		 * De-register render settings listener upon window closing.
		 */
		viewerFrame.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				for ( final RenderSettings rs : renderSettingsManager.getRenderSettings() )
					rs.removeUpdateListener( panelRepainter );
			};
		} );

		final BdvWindow bdvWindow = new BdvWindow( viewerFrame, tracksOverlay, bdvGroupHandle, contextProvider );
		addBdvWindow( bdvWindow );
	}

	public void createBranchGraphBigDataViewer()
	{
		final GroupHandle bdvGroupHandle = groupManager.createGroupHandle();
		final ModelBranchGraph branchGraph = model.getBranchGraph();
		final ModelGraph graph = model.getGraph();

		final OverlayGraphWrapper< Spot, Link > overlayGraph = new OverlayGraphWrapper<>(
				model.getGraph(),
				model.getGraphIdBimap(),
				model.getSpatioTemporalIndex(),
				new ModelOverlayProperties( model.getGraph(), radiusStats ) );

		final OverlayProperties< BranchVertex, BranchEdge > properties =
				new BranchGraphModelOverlayProperties( branchGraph, graph, radiusStats );
		final OverlayBranchGraphWrapper< BranchVertex, BranchEdge, Spot, Link > overlayBranchGraph =
				new OverlayBranchGraphWrapper<>(
						model.getBranchGraph(),
						model.getBranchGraph().getGraphIdBimap(),
						model.getBranchGraphSpatioTemporalIndex(),
						properties,
						overlayGraph );
		final RefBimap< BranchVertex, OverlayVertexWrapper< BranchVertex, BranchEdge > > vertexMap = new OverlayVertexWrapperBimap<>( overlayBranchGraph );
		final RefBimap< BranchEdge, OverlayEdgeWrapper< BranchVertex, BranchEdge > > edgeMap = new OverlayEdgeWrapperBimap<>( overlayBranchGraph );

		// Highlight.
		final HighlightModel< BranchVertex, BranchEdge > branchHighlightModel =
				new BranchGraphHighlightAdapter<>( branchGraph, graph, highlightModel );
		final HighlightModel< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > overlayHighlight =
				new HighlightAdapter<>( branchHighlightModel, vertexMap, edgeMap );

		// Focus
		final FocusModel< BranchVertex, BranchEdge > branchFocusfocusModel =
				new BranchGraphFocusAdapter<>( branchGraph, graph, focusModel );
		final FocusModel< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > overlayFocus =
				new FocusAdapter<>( branchFocusfocusModel, vertexMap, edgeMap );

		// Selection
		final Selection< BranchVertex, BranchEdge > branchSelection =
				new BranchGraphSelectionAdapter<>( branchGraph, graph, selection );
		final Selection< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > overlaySelection =
				new SelectionAdapter<>( branchSelection, vertexMap, edgeMap );

		// Navigation.
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( bdvGroupHandle );
		final NavigationHandler< BranchVertex, BranchEdge > branchGraphNavigation =
				new BranchGraphNavigationHandlerAdapter<>( branchGraph, graph, navigationHandler );
		final NavigationHandler< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > overlayNavigationHandler =
				new NavigationHandlerAdapter<>( branchGraphNavigation, vertexMap, edgeMap );

		/*
		 * Features for the BDV on a branch graph.
		 */
		final FeatureModel< BranchVertex, BranchEdge > featureModel = model.getBranchGraphFeatureModel();
		final FeatureModel< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > bdvBranchFeatures =
				new FeatureModelAdapter<>( featureModel, vertexMap, edgeMap );
		final BranchGraphFeaturesColorGenerator< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > colorGenerator =
				new BranchGraphFeaturesColorGenerator<>( RenderSettings.defaultStyle(), overlayBranchGraph, bdvBranchFeatures );

		// BDV.
		final String windowTitle = "BigDataViewer branches " + ( bdvName++ );
		final BigDataViewerMaMuT bdv = BigDataViewerMaMuT.open( sharedBdvData, windowTitle, bdvGroupHandle );
		final ViewerFrame viewerFrame = bdv.getViewerFrame();
		final ViewerPanel viewer = bdv.getViewer();

		viewer.setTimepoint( currentTimepoint );
		final OverlayGraphRenderer< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > tracksOverlay =
				new OverlayBranchGraphRenderer<>(
						overlayBranchGraph,
						overlayGraph,
						overlayHighlight,
						overlayFocus,
						overlaySelection,
						colorGenerator,
						colorGenerator );
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
		model.getGraph().addGraphChangeListener( new GraphChangeListener()
		{
			@Override
			public void graphChanged()
			{
				viewer.getDisplay().repaint();
			}
		} );
		model.getGraph().addVertexPositionListener( ( v ) -> viewer.getDisplay().repaint() );
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

		final OverlayNavigation< OverlayVertexWrapper< BranchVertex, BranchEdge >, OverlayEdgeWrapper< BranchVertex, BranchEdge > > overlayNavigation = new OverlayNavigation<>( viewer, overlayBranchGraph );
		overlayNavigationHandler.addNavigationListener( overlayNavigation );

		final BdvHighlightHandler< ?, ? > highlightHandler = new BdvHighlightHandler<>( overlayBranchGraph, tracksOverlay, overlayHighlight );
		viewer.getDisplay().addHandler( highlightHandler );
		viewer.addRenderTransformListener( highlightHandler );

		final BdvSelectionBehaviours< ?, ? > selectionBehaviours = new BdvSelectionBehaviours<>( overlayBranchGraph, tracksOverlay, overlaySelection, overlayNavigationHandler );
		selectionBehaviours.installBehaviourBindings( viewerFrame.getTriggerbindings(), keyconf );

		// TODO: use the branch context in branch TrackScheme.
		final OverlayContext< OverlayVertexWrapper< BranchVertex, BranchEdge > > overlayContext = new OverlayContext<>( overlayBranchGraph, tracksOverlay );
		viewer.addRenderTransformListener( overlayContext );
		final BdvContextAdapter< BranchVertex > contextProvider = new BdvContextAdapter<>( windowTitle );
		final OverlayContextWrapper< BranchVertex, BranchEdge > overlayContextWrapper = new OverlayContextWrapper<>(
				overlayContext,
				contextProvider );

		UndoActions.installActionBindings( viewerFrame.getKeybindings(), model, keyconf );
		EditBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), keyconf, overlayBranchGraph, tracksOverlay, model );
		EditSpecialBehaviours.installActionBindings( viewerFrame.getTriggerbindings(), keyconf, viewerFrame.getViewerPanel(), overlayBranchGraph, tracksOverlay, model );
		HighlightBehaviours.installActionBindings(
				viewerFrame.getTriggerbindings(),
				keyconf,
				new String[] { "bdv" },
				model.getGraph(),
				model.getGraph(),
				highlightModel,
				model );
		SelectionActions.installActionBindings(
				viewerFrame.getKeybindings(),
				keyconf,
				new String[] { "bdv" },
				model.getGraph(),
				model.getGraph(),
				selection,
				model );

		/*
		 * TODO: this is still wrong. There should be one central entity syncing
		 * time for several BDV frames and TrackSchemePanel should listen to
		 * that. Ideally windows should be configurable to "share" timepoints or
		 * not.
		 */
		viewer.addTimePointListener( tpl );

		/*
		 * BDV menu.
		 */

		final BdvRenderSettingsUpdater panelRepainter = new BdvRenderSettingsUpdater( tracksOverlay, colorGenerator, viewer, true );
		final JMenu styleMenu = new JMenu( "Styles" );
		styleMenu.addMenuListener( new MenuListener()
		{
			@Override
			public void menuSelected( final MenuEvent e )
			{
				styleMenu.removeAll();
				for ( final RenderSettings rs : renderSettingsManager.getRenderSettings() )
				{
					/*
					 * Do not add RenderSettings with color modes that we cannot
					 * define for a branch graph.
					 */
					switch ( rs.getVertexColorMode() )
					{
					case BRANCH_EDGE:
					case BRANCH_VERTEX:
					case FIXED:
						break;
					default:
						continue;
					}
					switch ( rs.getEdgeColorMode() )
					{
					case FIXED:
					case BRANCH_EDGE:
					case BRANCH_VERTEX:
						break;
					default:
						continue;
					}
					styleMenu.add( new JMenuItem(
							new BdvRenderSettingsAction( rs, panelRepainter ) ) );
				}

			}

			@Override
			public void menuDeselected( final MenuEvent e )
			{}

			@Override
			public void menuCanceled( final MenuEvent e )
			{}
		} );
		viewerFrame.getJMenuBar().add( styleMenu );

		/*
		 * De-register render settings listener upon window closing.
		 */
		viewerFrame.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				for ( final RenderSettings rs : renderSettingsManager.getRenderSettings() )
					rs.removeUpdateListener( panelRepainter );
			};
		} );

	}

	private class BdvRenderSettingsUpdater implements org.mastodon.revised.bdv.overlay.RenderSettings.UpdateListener
	{

		private final ViewerPanel viewer;

		private final OverlayGraphRenderer< ?, ? > overlay;

		private RenderSettings renderSettings;

		private final FeaturesColorGenerator< ?, ? > colorGenerator;

		private final boolean isBranchGraph;

		/**
		 * Creates a listener to a render settings that will update a display
		 * and a color generator, then trigger the repaint of a BDV viewer upon
		 * render settings changes.
		 *
		 * @param overlay
		 *            the overlay to be notified of render settings changes.
		 * @param viewer
		 *            the BDV panel to repaint.
		 * @param colorGenerator
		 *            the color generator to be notified of render settings
		 *            changes.
		 * @param isBranchGraph
		 *            whether the display we paint in is meant for branch
		 *            graphs.
		 */
		public BdvRenderSettingsUpdater( final OverlayGraphRenderer< ?, ? > overlay, final FeaturesColorGenerator< ?, ? > colorGenerator, final ViewerPanel viewer, final boolean isBranchGraph )
		{
			this.overlay = overlay;
			this.viewer = viewer;
			this.colorGenerator = colorGenerator;
			this.isBranchGraph = isBranchGraph;
		}

		@Override
		public void renderSettingsChanged()
		{
			if ( isBranchGraph )
			{
				/*
				 * If we are a settings updater for the display of a branch
				 * graph, and the render settings was updated for something not
				 * defined for branch graph, then we set the render settings of
				 * the display to the default one.
				 */
				switch ( renderSettings.getVertexColorMode() )
				{
				case BRANCH_EDGE:
				case BRANCH_VERTEX:
				case FIXED:
					break;
				default:
					new BdvRenderSettingsAction( RenderSettings.defaultStyle(), this ).actionPerformed( null );
					return;
				}
				switch ( renderSettings.getEdgeColorMode() )
				{
				case FIXED:
				case BRANCH_EDGE:
				case BRANCH_VERTEX:
					break;
				default:
					new BdvRenderSettingsAction( RenderSettings.defaultStyle(), this ).actionPerformed( null );
					return;
				}
			}
			overlay.setRenderSettings( renderSettings );
			colorGenerator.setColorMode( renderSettings );
			viewer.repaint();
		}
	}

	/**
	 * Sets the style of a BDV when executed &amp; registers itself as a
	 * listener for style changes to repaint said BDV.
	 */
	private class BdvRenderSettingsAction extends AbstractNamedAction
	{

		private static final long serialVersionUID = 1L;

		private final RenderSettings rs;

		private final BdvRenderSettingsUpdater panelRepainter;

		public BdvRenderSettingsAction( final RenderSettings rs, final BdvRenderSettingsUpdater panelRepainter )
		{
			super( rs.getName() );
			this.rs = rs;
			this.panelRepainter = panelRepainter;
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			panelRepainter.renderSettings = rs;
			rs.addUpdateListener( panelRepainter );
			panelRepainter.renderSettingsChanged();
		}
	}

	// TODO testing only
	private int currentTimepoint = 0;

	// TODO testing only
	private final TimePointListener tpl = new TimePointListener()
	{
		@Override
		public void timePointChanged( final int timePointIndex )
		{
			if ( currentTimepoint != timePointIndex )
			{
				currentTimepoint = timePointIndex;
				for ( final TsWindow w : tsWindows )
					w.getTrackSchemeFrame().getTrackschemePanel().timePointChanged( timePointIndex );
			}
		}
	};

	public void createTrackScheme()
	{
		final ListenableReadOnlyGraph< Spot, Link > graph = model.getGraph();
		final GraphIdBimap< Spot, Link > idmap = model.getGraphIdBimap();

		/*
		 * TrackSchemeGraph listening to model.
		 */
		final ModelGraphProperties< Spot, Link > graphProperties = new DefaultModelGraphProperties<>();
		final TrackSchemeGraph< Spot, Link > trackSchemeGraph = new TrackSchemeGraph<>( graph, idmap, graphProperties );
		final RefBimap< Spot, TrackSchemeVertex > vertexMap = new TrackSchemeVertexBimap<>( idmap, trackSchemeGraph );
		final RefBimap< Link, TrackSchemeEdge > edgeMap = new TrackSchemeEdgeBimap<>( idmap, trackSchemeGraph );

		/*
		 * Highlight model for TrackScheme.
		 */
		final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeHighlight =
				new HighlightAdapter<>( highlightModel, vertexMap, edgeMap );

		/*
		 * Selection model for TrackScheme.
		 */
		final Selection< TrackSchemeVertex, TrackSchemeEdge > trackSchemeSelection =
				new SelectionAdapter<>( selection, vertexMap, edgeMap );

		/*
		 * TrackScheme GroupHandle
		 */
		final GroupHandle groupHandle = groupManager.createGroupHandle();

		/*
		 * Navigation for TrackScheme.
		 */
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( groupHandle );
		final NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > trackSchemeNavigation =
				new NavigationHandlerAdapter<>( navigationHandler, vertexMap, edgeMap );

		/*
		 * Focus model for TrackScheme.
		 */
		final FocusModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFocus =
				new FocusAdapter<>( focusModel, vertexMap, edgeMap );

		/*
		 * Features for TrackScheme.
		 */
		final FeatureModel< Spot, Link > featureModel = model.getGraphFeatureModel();
		final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFeatures =
				new FeatureModelAdapter<>( featureModel, vertexMap, edgeMap );

		/*
		 * TrackScheme ContextChooser.
		 */
		final TrackSchemeContextListener< Spot > contextListener = new TrackSchemeContextListener<>(
				idmap,
				trackSchemeGraph );
		final ContextChooser< Spot > contextChooser = new ContextChooser<>( contextListener );

		/*
		 * Tune TrackScheme options to use a feature-based coloring scheme.
		 */

		final BranchGraph< BranchVertex, BranchEdge, TrackSchemeVertex, TrackSchemeEdge > trackSchemeBranchGraph =
				new BranchGraphAdapter<>( model.getBranchGraph(), vertexMap, edgeMap );
		final FeaturesColorGenerator< TrackSchemeVertex, TrackSchemeEdge > colorGenerator =
				new FeaturesColorGeneratorBranchFeatures<>( TrackSchemeStyle.defaultStyle(), trackSchemeGraph, trackSchemeFeatures, trackSchemeBranchGraph, model.getBranchGraphFeatureModel() );

		final TrackSchemeOptions options  = TrackSchemeOptions.options().
			inputTriggerConfig( keyconf ).
			vertexColorGenerator( colorGenerator ).
			edgeColorGenerator( colorGenerator );

		/*
		 * Show TrackSchemeFrame.
		 */

		final TrackSchemeFrame frame = new TrackSchemeFrame(
				trackSchemeGraph,
				trackSchemeHighlight,
				trackSchemeFocus,
				trackSchemeSelection,
				trackSchemeNavigation,
				model,
				groupHandle,
				contextChooser,
				options );

		installTrackSchemeMenu( frame, colorGenerator, false );

		/*
		 * Register this TrackScheme in the TrackSchemeService.
		 */
		final TrackSchemeService service = context.getService( TrackSchemeService.class );
		service.register( frame,
				trackSchemeGraph, trackSchemeSelection, trackSchemeHighlight, trackSchemeFocus, trackSchemeNavigation );

		frame.getTrackschemePanel().setTimepointRange( minTimepoint, maxTimepoint );
		frame.getTrackschemePanel().graphChanged();
		contextListener.setContextListener( frame.getTrackschemePanel() );
		frame.setVisible( true );

		UndoActions.installActionBindings( frame.getKeybindings(), model, keyconf );
		HighlightBehaviours.installActionBindings(
				frame.getTriggerbindings(),
				keyconf,
				new String[] { "ts" },
				model.getGraph(),
				model.getGraph(),
				highlightModel,
				model );
		SelectionActions.installActionBindings(
				frame.getKeybindings(),
				keyconf,
				new String[] { "ts" },
				model.getGraph(),
				model.getGraph(),
				selection,
				model );
		TrackSchemeEditBehaviours.installActionBindings(
				frame.getTriggerbindings(),
				keyconf,
				frame.getTrackschemePanel(),
				trackSchemeGraph,
				frame.getTrackschemePanel().getGraphOverlay(),
				model.getGraph(),
				model.getGraph().getGraphIdBimap(),
				model );

		/*
		 * Actions discovered by TrackScheme action provider and with mappings
		 * specified in keyconfig.yaml. With the syntax below, only the mappings
		 * with contexts having "trackscheme" will be picked up.
		 */
		final Actions actions = new Actions( keyconf, "trackscheme" );
		for ( final String key : trackSchemeActionProvider.getKeys() )
		{
			/*
			 * TODO Do not instantiate when there is no mapping for this action.
			 * The problem is that we cannot know whether 'keyconf' has a
			 * mapping for the action or not.
			 */
			final TrackSchemeAction action = trackSchemeActionProvider.create( key );
			if ( null == action )
				continue;

			service.put( action, frame );
			action.initialize();
			actions.runnableAction( action, key );
		}
		final InputActionBindings keybindings = frame.getKeybindings();
		actions.install( keybindings, "trackscheme" );

		/*
		 * Behaviors discovered by the TrackScheme behaviour provider and with
		 * mappings specified in keyconfig.yaml. With the syntax below, only the
		 * mappings with contexts having "trackscheme" will be picked up.
		 */
		final Behaviours behaviours = new Behaviours( keyconf, "trackscheme" );
		for ( final String key : trackSchemeBehaviourProvider.getKeys() )
		{
			/*
			 * TODO Do not instantiate when there is no mapping for this action.
			 * The problem is that we cannot know whether 'keyconf' has a
			 * mapping for the action or not.
			 */
			final TrackSchemeBehaviour behaviour = trackSchemeBehaviourProvider.create( key );
			if ( null == behaviour )
				continue;

			service.put( behaviour, frame );
			behaviour.initialize();
			behaviours.behaviour( behaviour, key );
		}
		final TriggerBehaviourBindings triggerbindings = frame.getTriggerbindings();
		behaviours.install( triggerbindings, "trackscheme" );

		final TsWindow tsWindow = new TsWindow( frame, groupHandle, contextChooser );
		addTsWindow( tsWindow );
		frame.getTrackschemePanel().repaint();
	}

	public void createBranchGraphTrackScheme()
	{
		final ModelBranchGraph graph = model.getBranchGraph();
		final GraphIdBimap< BranchVertex, BranchEdge > idmap = graph.getGraphIdBimap();

		/*
		 * TrackSchemeGraph listening to branch graph.
		 */
		final ModelGraphProperties< BranchVertex, BranchEdge > properties = new DefaultModelGraphProperties<>();
		final TrackSchemeGraph< BranchVertex, BranchEdge > trackSchemeGraph =
				new TrackSchemeGraph< BranchVertex, BranchEdge >( graph, idmap, properties );
		final RefBimap< BranchVertex, TrackSchemeVertex > vertexMap = new TrackSchemeVertexBimap<>( idmap, trackSchemeGraph );
		final RefBimap< BranchEdge, TrackSchemeEdge > edgeMap = new TrackSchemeEdgeBimap<>( idmap, trackSchemeGraph );

		/*
		 * Highlight model for branch graph.
		 */
		final HighlightModel< BranchVertex, BranchEdge > branchGraphHighlight =
				new BranchGraphHighlightAdapter<>( graph, model.getGraph(), highlightModel );
		final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeHighlight =
				new HighlightAdapter<>( branchGraphHighlight, vertexMap, edgeMap );

		/*
		 * Selection model for branch graph.
		 */
		final Selection< BranchVertex, BranchEdge > branchGraphSelection =
				new BranchGraphSelectionAdapter<>( graph, model.getGraph(), selection );
		final Selection< TrackSchemeVertex, TrackSchemeEdge > trackSchemeSelection =
				new SelectionAdapter<>( branchGraphSelection, vertexMap, edgeMap );

		/*
		 * TrackScheme GroupHandle.
		 */
		final GroupHandle groupHandle = groupManager.createGroupHandle();

		/*
		 * Navigation model for branch graph.
		 */
		final NavigationHandler< Spot, Link > navigationHandler = new NavigationHandlerImp<>( groupHandle );
		final NavigationHandler< BranchVertex, BranchEdge > branchGraphNavigation =
				new BranchGraphNavigationHandlerAdapter<>( graph, model.getGraph(), navigationHandler );
		final NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > trackSchemeNavigation =
				new NavigationHandlerAdapter<>( branchGraphNavigation, vertexMap, edgeMap );

		/*
		 * Focus model for branch graph.
		 */

		final FocusModel< BranchVertex, BranchEdge > branchGraphFocus =
				new BranchGraphFocusAdapter<>( graph, model.getGraph(), focusModel );
		final FocusModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFocus =
				new FocusAdapter<>( branchGraphFocus, vertexMap, edgeMap );

		/*
		 * Branch graph features.
		 */

		/*
		 * Features for TrackScheme.
		 */
		final FeatureModel< BranchVertex, BranchEdge > featureModel = model.getBranchGraphFeatureModel();
		final FeatureModel< TrackSchemeVertex, TrackSchemeEdge > trackSchemeFeatures =
				new FeatureModelAdapter<>( featureModel, vertexMap, edgeMap );
		final BranchGraphFeaturesColorGenerator< TrackSchemeVertex, TrackSchemeEdge > colorGenerator =
				new BranchGraphFeaturesColorGenerator<>( TrackSchemeStyle.defaultStyle(), trackSchemeGraph, trackSchemeFeatures );

		/*
		 * TrackScheme options.
		 */

		final TrackSchemeOptions options = TrackSchemeOptions.options().
				inputTriggerConfig( keyconf ).
				vertexColorGenerator( colorGenerator ).
				edgeColorGenerator( colorGenerator ).
				trackSchemeOverlayFactory( new BranchGraphTrackSchemeOverlay.Factory() );

		/*
		 * Show TrackSchemeFrame.
		 */
		final TrackSchemeFrame frame = new TrackSchemeFrame(
				trackSchemeGraph,
				trackSchemeHighlight,
				trackSchemeFocus,
				trackSchemeSelection,
				trackSchemeNavigation,
				model,
				groupHandle,
				null,
				options );

		installTrackSchemeMenu( frame, colorGenerator, true );
		frame.setTitle( "Branch graph" );
		frame.getTrackschemePanel().setTimepointRange( minTimepoint, maxTimepoint );
		frame.getTrackschemePanel().graphChanged();
		frame.setVisible( true );
		frame.getTrackschemePanel().repaint();
	}

	public void closeAllWindows()
	{
		final ArrayList< JFrame > frames = new ArrayList<>();
		for ( final BdvWindow w : bdvWindows )
			frames.add( w.getViewerFrame() );
		for ( final TsWindow w : tsWindows )
			frames.add( w.getTrackSchemeFrame() );
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				for ( final JFrame f : frames )
					f.dispatchEvent( new WindowEvent( f, WindowEvent.WINDOW_CLOSING ) );
			}
		} );
	}

	public Model getModel()
	{
		return model;
	}

	public AbstractSpimData< ? > getSpimData()
	{
		return sharedBdvData.getSpimData();
	}

	public RenderSettingsManager getBDVSettingsManager()
	{
		return renderSettingsManager;
	}

	private void installTrackSchemeMenu(
			final TrackSchemeFrame frame,
			final FeaturesColorGenerator< TrackSchemeVertex, TrackSchemeEdge > colorGenerator,
			final boolean isBranchGraph )
	{

		final JMenuBar menu;
		if ( frame.getJMenuBar() == null )
			menu = new JMenuBar();
		else
			menu = frame.getJMenuBar();

		// Styles auto-populated from TrackScheme style manager.
		if ( frame.getTrackschemePanel().getGraphOverlay() instanceof DefaultTrackSchemeOverlay )
		{
			final TrackSchemePanel panel = frame.getTrackschemePanel();
			final DefaultTrackSchemeOverlay overlay = ( DefaultTrackSchemeOverlay ) panel.getGraphOverlay();
			final TsStyleListener styleListener = new TsStyleListener( panel, overlay, colorGenerator, isBranchGraph );

			final JMenu styleMenu = new JMenu( "Styles" );
			// Populate menu on the fly when it is opened.
			styleMenu.addMenuListener( new MenuListener()
			{
				@Override
				public void menuSelected( final MenuEvent e )
				{
					styleMenu.removeAll();
					for ( final TrackSchemeStyle style : trackSchemeStyleManager.getStyles() )
					{
						// Branch graph cannot get styles not set to a branch feature.
						if (isBranchGraph )
						{
							switch ( style.getVertexColorMode() )
							{
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
							case FIXED:
								break;
							default:
								continue;
							}
							switch ( style.getEdgeColorMode() )
							{
							case FIXED:
							case BRANCH_EDGE:
							case BRANCH_VERTEX:
								break;
							default:
								continue;
							}
						}
						styleMenu.add( new JMenuItem(
								new TsStyleAction( style, panel, overlay, colorGenerator, styleListener ) ) );
					}
				}

				@Override
				public void menuDeselected( final MenuEvent e )
				{}

				@Override
				public void menuCanceled( final MenuEvent e )
				{}
			} );
			/*
			 * De-register style listener upon window closing.
			 */
			frame.addWindowListener( new WindowAdapter()
			{
				@Override
				public void windowClosing( final WindowEvent e )
				{
					for ( final TrackSchemeStyle style : trackSchemeStyleManager.getStyles() )
						style.removeUpdateListener( styleListener );
				};
			} );

			menu.add( styleMenu );
		}
		frame.setJMenuBar( menu );
	}

	private class TsStyleAction extends AbstractNamedAction
	{

		private static final long serialVersionUID = 1L;

		private final TrackSchemeStyle style;

		private final TsStyleListener updater;

		private final DefaultTrackSchemeOverlay overlay;

		private final FeaturesColorGenerator< ?, ? > colorGenerator;

		private final TrackSchemePanel panel;

		public TsStyleAction(
				final TrackSchemeStyle style,
				final TrackSchemePanel panel,
				final DefaultTrackSchemeOverlay overlay,
				final FeaturesColorGenerator< ?, ? > colorGenerator,
				final TsStyleListener updater )
		{
			super( style.getName() );
			this.style = style;
			this.overlay = overlay;
			this.colorGenerator = colorGenerator;
			this.panel = panel;
			this.updater = updater;
		}

		@Override
		public void actionPerformed( final ActionEvent e )
		{
			overlay.getStyle().removeUpdateListener( updater );
			overlay.setStyle( style );
			colorGenerator.setColorMode( style );
			style.addUpdateListener( updater );
			panel.graphChanged();
		}
	}

	/**
	 * Notifies the ColorGenerator that the style have been updated and trigger
	 * a repaint of the TrackScheme panel.
	 */
	private static final class TsStyleListener implements UpdateListener
	{

		private final TrackSchemePanel panel;

		private final FeaturesColorGenerator< ?, ? > colorGenerator;

		private final boolean isBranchGraph;

		private final DefaultTrackSchemeOverlay overlay;

		public TsStyleListener(
				final TrackSchemePanel panel,
				final DefaultTrackSchemeOverlay overlay,
				final FeaturesColorGenerator< ?, ? > colorGenerator,
				final boolean isBranchGraph )
		{
			this.panel = panel;
			this.overlay = overlay;
			this.colorGenerator = colorGenerator;
			this.isBranchGraph = isBranchGraph;
		}

		@Override
		public void trackSchemeStyleChanged()
		{
			/*
			 * If we are in a branch view, check that we can still manage this
			 * style.
			 */
			if ( isBranchGraph )
			{
				// Forbid styles that do not work for branch graph.
				final TrackSchemeStyle style = overlay.getStyle();
				switch ( style.getVertexColorMode() )
				{
				case BRANCH_EDGE:
				case BRANCH_VERTEX:
				case FIXED:
					break;
				default:
					overlay.setStyle( TrackSchemeStyle.defaultStyle() );
					colorGenerator.setColorMode( TrackSchemeStyle.defaultStyle() );
					break;
				}
				switch ( style.getEdgeColorMode() )
				{
				case BRANCH_VERTEX:
				case BRANCH_EDGE:
				case FIXED:
					break;
				default:
					overlay.setStyle( TrackSchemeStyle.defaultStyle() );
					colorGenerator.setColorMode( TrackSchemeStyle.defaultStyle() );
					break;
				}
			}

			colorGenerator.colorModeChanged();
			panel.graphChanged();
		}
	}

	// TODO: move somewhere else. make bdvWindows, tsWindows accessible.
	public static class DumpInputConfig
	{
		private static List< InputTriggerDescription > buildDescriptions( final WindowManager wm ) throws IOException
		{
			final InputTriggerDescriptionsBuilder builder = new InputTriggerDescriptionsBuilder();

			final ViewerFrame viewerFrame = wm.bdvWindows.get( 0 ).viewerFrame;
			builder.addMap( viewerFrame.getKeybindings().getConcatenatedInputMap(), "bdv" );
			builder.addMap( viewerFrame.getTriggerbindings().getConcatenatedInputTriggerMap(), "bdv" );

			final TrackSchemeFrame trackschemeFrame = wm.tsWindows.get( 0 ).trackSchemeFrame;
			builder.addMap( trackschemeFrame.getKeybindings().getConcatenatedInputMap(), "ts" );
			builder.addMap( trackschemeFrame.getTriggerbindings().getConcatenatedInputTriggerMap(), "ts" );

			return builder.getDescriptions();
		}

		public static boolean mkdirs( final String fileName )
		{
			final File dir = new File( fileName ).getParentFile();
			return dir == null ? false : dir.mkdirs();
		}

		public static void writeToYaml( final String fileName, final WindowManager wm ) throws IOException
		{
			mkdirs( fileName );
			YamlConfigIO.write( buildDescriptions( wm ), fileName );
		}
	}
}
