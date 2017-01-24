package org.mastodon.revised.trackscheme.display.style;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mastodon.adapter.FocusAdapter;
import org.mastodon.adapter.HighlightAdapter;
import org.mastodon.adapter.NavigationHandlerAdapter;
import org.mastodon.adapter.RefBimap;
import org.mastodon.adapter.SelectionAdapter;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.trackscheme.TrackSchemeEdge;
import org.mastodon.revised.trackscheme.TrackSchemeEdgeBimap;
import org.mastodon.revised.trackscheme.TrackSchemeGraph;
import org.mastodon.revised.trackscheme.TrackSchemeVertex;
import org.mastodon.revised.trackscheme.TrackSchemeVertexBimap;
import org.mastodon.revised.trackscheme.display.AbstractTrackSchemeOverlay;
import org.mastodon.revised.trackscheme.display.TrackSchemeOptions;
import org.mastodon.revised.trackscheme.display.TrackSchemePanel;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.trackscheme.display.style.dummygraph.DummyEdge;
import org.mastodon.revised.trackscheme.display.style.dummygraph.DummyGraph;
import org.mastodon.revised.trackscheme.display.style.dummygraph.DummyGraph.Examples;
import org.mastodon.revised.trackscheme.display.style.dummygraph.DummyVertex;
import org.mastodon.revised.trackscheme.wrap.DefaultModelGraphProperties;
import org.mastodon.revised.trackscheme.wrap.ModelGraphProperties;
import org.mastodon.revised.ui.grouping.GroupManager;
import org.mastodon.revised.ui.selection.FocusModel;
import org.mastodon.revised.ui.selection.FocusModelImp;
import org.mastodon.revised.ui.selection.HighlightModel;
import org.mastodon.revised.ui.selection.HighlightModelImp;
import org.mastodon.revised.ui.selection.NavigationHandler;
import org.mastodon.revised.ui.selection.NavigationHandlerImp;
import org.mastodon.revised.ui.selection.Selection;

/**
 * A previewer, editor and managers for TrackScheme styles.
 *
 * @author Jean-Yves Tinevez
 */
class TrackSchemeStyleChooserPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	JButton buttonDeleteStyle;

	JButton buttonNewStyle;

	JButton buttonSetStyleName;

	JButton saveButton;

	JComboBox< TrackSchemeStyle > comboBoxStyles;

	public TrackSchemeStyleChooserPanel(
			final MutableComboBoxModel< TrackSchemeStyle > model,
			final FeatureKeys featureKeys,
			final FeatureRangeCalculator featureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		final Examples ex = DummyGraph.Examples.CELEGANS;
		final DummyGraph example = ex.getGraph();
		final GraphIdBimap< DummyVertex, DummyEdge > idmap = example.getIdBimap();
		final ModelGraphProperties< DummyVertex, DummyEdge > dummyProps = new DefaultModelGraphProperties<>();
		final TrackSchemeGraph< DummyVertex, DummyEdge > graph = new TrackSchemeGraph<>( example, idmap, dummyProps );
		final RefBimap< DummyVertex, TrackSchemeVertex > vertexMap = new TrackSchemeVertexBimap<>( idmap, graph );
		final RefBimap< DummyEdge, TrackSchemeEdge > edgeMap = new TrackSchemeEdgeBimap<>( idmap, graph );
		final HighlightModel< TrackSchemeVertex, TrackSchemeEdge > highlight = new HighlightAdapter<>( new HighlightModelImp<>( idmap ), vertexMap, edgeMap );
		final FocusModel< TrackSchemeVertex, TrackSchemeEdge > focus = new FocusAdapter<>( new FocusModelImp<>( idmap ), vertexMap, edgeMap );
		final Selection< TrackSchemeVertex, TrackSchemeEdge > selection = new SelectionAdapter<>( ex.getSelection(), vertexMap, edgeMap );
		final NavigationHandler< TrackSchemeVertex, TrackSchemeEdge > navigation = new NavigationHandlerAdapter<>( new NavigationHandlerImp<>( new GroupManager().createGroupHandle() ), vertexMap, edgeMap );

		final TrackSchemeStyle editedStyle = TrackSchemeStyle.defaultStyle().copy( "Edited" );

		final DummyLayoutColorGenerator colorGenerator = new DummyLayoutColorGenerator( graph );
		final TrackSchemeOptions options = TrackSchemeOptions.options().
				vertexColorGenerator( colorGenerator ).
				edgeColorGenerator( colorGenerator );

		final TrackSchemePanel panelPreview = new TrackSchemePanel( graph, highlight, focus, selection, navigation, options );
		final AbstractTrackSchemeOverlay graphOverlay = panelPreview.getGraphOverlay();
		if ( graphOverlay instanceof DefaultTrackSchemeOverlay )
		{
			final DefaultTrackSchemeOverlay dtso = ( DefaultTrackSchemeOverlay ) graphOverlay;
			dtso.setStyle( editedStyle );
		}
		panelPreview.setTimepointRange( 0, 7 );
		panelPreview.timePointChanged( 2 );
		panelPreview.graphChanged();

		setLayout( new BorderLayout() );
		final JPanel dialogPane = new JPanel();
		dialogPane.setBorder( new EmptyBorder( 12, 12, 12, 12 ) );
		dialogPane.setLayout( new BorderLayout() );

		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BorderLayout() );

		final JPanel panelChooseStyle = new JPanel();
		panelChooseStyle.setLayout( new GridLayout( 3, 0, 0, 10 ) );

		final JLabel jlabelTitle = new JLabel();
		jlabelTitle.setText( "TrackScheme display styles." );
		jlabelTitle.setHorizontalAlignment( SwingConstants.CENTER );
		jlabelTitle.setFont( dialogPane.getFont().deriveFont( Font.BOLD ) );
		panelChooseStyle.add( jlabelTitle );

		// Combo box panel
		final JPanel comboBoxPanel = new JPanel();
		final BorderLayout layout = new BorderLayout();
		comboBoxPanel.setLayout( layout );
		comboBoxPanel.add( new JLabel( "Style: " ), BorderLayout.WEST );
		this.comboBoxStyles = new JComboBox<>( model );
		comboBoxPanel.add( comboBoxStyles, BorderLayout.CENTER );
		panelChooseStyle.add( comboBoxPanel );

		final JPanel panelStyleButtons = new JPanel();
		panelStyleButtons.setLayout( new BoxLayout( panelStyleButtons, BoxLayout.LINE_AXIS ) );
		this.buttonDeleteStyle = new JButton();
		buttonDeleteStyle.setText( "Delete" );
		panelStyleButtons.add( buttonDeleteStyle );

		final JPanel hSpacer1 = new JPanel( null );
		panelStyleButtons.add( hSpacer1 );

		this.buttonNewStyle = new JButton();
		buttonNewStyle.setText( "New" );
		panelStyleButtons.add( buttonNewStyle );

		this.buttonSetStyleName = new JButton();
		buttonSetStyleName.setText( "Set name" );
		panelStyleButtons.add( buttonSetStyleName );
		panelChooseStyle.add( panelStyleButtons );
		contentPanel.add( panelChooseStyle, BorderLayout.NORTH );

		final TrackSchemeStyleEditorPanel editorPanel = new TrackSchemeStyleEditorPanel(
				editedStyle,
				featureKeys, featureRangeCalculator,
				branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );
		contentPanel.add( editorPanel, BorderLayout.SOUTH );

		panelPreview.setPreferredSize( new Dimension( 300, 250 ) );
		contentPanel.add( panelPreview, BorderLayout.CENTER );

		final JPanel buttonBar = new JPanel();
		buttonBar.setBorder( new EmptyBorder( 12, 0, 0, 0 ) );
		final GridBagLayout buttonBarLayout = new GridBagLayout();
		buttonBarLayout.columnWidths = new int[] { 80, 164, 80 };
		buttonBarLayout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		buttonBar.setLayout( buttonBarLayout );

		this.saveButton = new JButton();
		saveButton.setText( "Save styles" );
		buttonBar.add( saveButton, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets( 0, 0, 0, 0 ), 0, 0 ) );
		dialogPane.add( buttonBar, BorderLayout.SOUTH );
		add( dialogPane, BorderLayout.CENTER );
		dialogPane.add( contentPanel, BorderLayout.CENTER );

		/*
		 * Listeners.
		 */

		editedStyle.addUpdateListener( new UpdateListener()
		{
			@Override
			public void trackSchemeStyleChanged()
			{
				colorGenerator.edgeColorMap = editedStyle.getEdgeColorMap();
				colorGenerator.vertexColorMap = editedStyle.getVertexColorMap();
				panelPreview.graphChanged();

				final TrackSchemeStyle selectedStyle = ( TrackSchemeStyle ) comboBoxStyles.getSelectedItem();
				selectedStyle.set( editedStyle );
			}
		} );

		comboBoxStyles.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final TrackSchemeStyle selectedStyle = ( TrackSchemeStyle ) comboBoxStyles.getSelectedItem();
				editedStyle.set( selectedStyle );

				editorPanel.setEnabled( !TrackSchemeStyle.defaults.contains( selectedStyle ) );
				editorPanel.update();
			}
		} );
	}

}
