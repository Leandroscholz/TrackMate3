package org.mastodon.revised.ui.coloring;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.model.feature.FeatureTarget;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.util.CategoryJComboBox;
import org.mastodon.revised.ui.util.ColorMap;

/**
 * GUI element to configure a {@link ColorMode} implementation.
 *
 * @author Jean-Yves Tinevez.
 */
public class ColorModePicker extends JPanel
{

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension( 400, 200 );

	private final CategoryJComboBox< VertexColorMode, FeatureKeyWrapper > colorVertexChoices;

	private final JComboBox< String > cmapVertex;

	private final ColorMapPainter colorMapPainterVertex;

	private final JFormattedTextField minVertex;

	private final JFormattedTextField maxVertex;

	private final JButton autoscaleVertex;

	private final CategoryJComboBox< EdgeColorMode, FeatureKeyWrapper > colorEdgeChoices;

	private final JComboBox< String > cmapEdge;

	private final ColorMapPainter colorMapPainterEdge;

	private final JFormattedTextField minEdge;

	private final JFormattedTextField maxEdge;

	private final JButton autoscaleEdge;

	private final FeatureRangeCalculator featureRangeCalculator;

	private final FeatureRangeCalculator branchGraphFeatureRangeCalculator;

	private final ColorMode current;

	private final FeatureKeys featureKeys;

	private final FeatureKeys branchGraphFeatureKeys;

	public ColorModePicker( final ColorMode current,
			final FeatureKeys featureKeys,
			final FeatureRangeCalculator featureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		super( new GridBagLayout() );
		this.current = current;
		this.featureKeys = featureKeys;
		this.featureRangeCalculator = featureRangeCalculator;
		this.branchGraphFeatureKeys = branchGraphFeatureKeys;
		this.branchGraphFeatureRangeCalculator = branchGraphFeatureRangeCalculator;

		/*
		 * Current settings.
		 */

		final double minVertexColorRange = current.getMinVertexColorRange();
		final double maxVertexColorRange = current.getMaxVertexColorRange();
		final double minEdgeColorRange = current.getMinEdgeColorRange();
		final double maxEdgeColorRange = current.getMaxEdgeColorRange();

		/*
		 * Layout panel.
		 */

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 0, 0, 0, 0 );
		c.ipadx = 0;
		c.ipady = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.RELATIVE;

		/*
		 * Listeners.
		 */

		final ActionListener autoscaleVertexAction = new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				new Thread( () -> autoScaleVertexFeature(), "Vertex feature range calculation thread." ).start();
			}
		};
		final ActionListener autoscaleEdgeAction = new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				new Thread( () -> autoScaleEdgeFeature(), "Edge feature range calculation thread." ).start();
			}
		};

		/*
		 * Vertices.
		 */

		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		final JLabel lbl1 = new JLabel( "Color vertices by" );
		lbl1.setFont( getFont().deriveFont( Font.BOLD ) );
		lbl1.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl1, c );

		c.gridx++;
		c.weightx = 1.;
		c.gridwidth = 3;
		colorVertexChoices = new CategoryJComboBox<>();
		colorVertexChoices.setEditable( false );
		add( colorVertexChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl3 = new JLabel( "ColorMap" );
		lbl3.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl3, c );

		c.gridx = 1;
		c.gridwidth = 1;
		cmapVertex = new JComboBox<>(
				ColorMap.getColorMapNames().toArray( new String[] {} ) );
		add( cmapVertex, c );

		c.gridx = 2;
		c.gridwidth = 2;
		colorMapPainterVertex = new ColorMapPainter( cmapVertex );
		add( colorMapPainterVertex, c );

		c.gridx = 0;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl5 = new JLabel( "Min/Max" );
		lbl5.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl5, c );

		c.gridx = 1;
		c.weightx = 1.;
		c.gridwidth = 3;
		final JPanel scalePanel1 = new JPanel();
		final BoxLayout boxLayout1 = new BoxLayout( scalePanel1, BoxLayout.LINE_AXIS );
		scalePanel1.setLayout( boxLayout1 );

		minVertex = new JFormattedTextField( Double.valueOf( minVertexColorRange ) );
		minVertex.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel1.add( minVertex );
		maxVertex = new JFormattedTextField( Double.valueOf( maxVertexColorRange ) );
		maxVertex.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel1.add( maxVertex );
		autoscaleVertex = new JButton( "Autoscale" );
		scalePanel1.add( autoscaleVertex );
		add( scalePanel1, c );

		/*
		 * Wire up listeners.
		 */
		cmapVertex.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final ColorMap colorMap = ColorMap.getColorMap( ( String ) cmapVertex.getSelectedItem() );
				if (!current.getVertexColorMap().equals( colorMap ))
					current.vertexColorMap( colorMap );

				colorMapPainterVertex.repaint();
			}
		} );
		minVertex.addActionListener( ( e ) -> current.minVertexColorRange( ( double ) minVertex.getValue() ) );
		minVertex.addFocusListener( new FocusListener()
		{
			@Override public void focusLost( final FocusEvent e ) { current.minVertexColorRange( ( double ) minVertex.getValue() );  }
			@Override public void focusGained( final FocusEvent e ) {}
		} );
		maxVertex.addActionListener( ( e ) -> current.maxVertexColorRange( ( double ) maxVertex.getValue() ) );
		maxVertex.addFocusListener( new FocusListener()
		{
			@Override public void focusLost( final FocusEvent e ) { current.maxVertexColorRange( ( double ) maxVertex.getValue() );  }
			@Override public void focusGained( final FocusEvent e ) {}
		} );
		autoscaleVertex.addActionListener( autoscaleVertexAction );

		final Collection< JComponent > toMute1 = Arrays.asList( new JComponent[] { cmapVertex, minVertex, maxVertex, autoscaleVertex, colorMapPainterVertex } );
		final ComponentMuter muter1 = new ComponentMuter( toMute1 );
		colorVertexChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final FeatureKeyWrapper fkw = new FeatureKeyWrapper( current.getVertexFeatureKey(), current.getVertexColorMode() );
				final FeatureKeyWrapper selected = colorVertexChoices.getSelectedItem();
				if ( fkw != selected )
					current.vertexColorMode( ( VertexColorMode ) selected.category, selected.featureKey );

				// Mute some controls for FIXED case.
				switch ( colorVertexChoices.getSelectedCategory() )
				{
				case FIXED:
					muter1.enable( false );
					break;
				default:
					muter1.enable( true );
					break;
				}
			}
		} );
		muter1.enable( colorVertexChoices.getSelectedCategory() != VertexColorMode.FIXED );

		/*
		 * Edges.
		 */

		c.gridy++;
		add( Box.createVerticalStrut( 5 ), c );

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		final JLabel lbl2 = new JLabel( "Color edges by" );
		lbl2.setFont( getFont().deriveFont( Font.BOLD ) );
		lbl2.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl2, c );

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		c.gridwidth = 3;
		colorEdgeChoices = new CategoryJComboBox<>();
		colorEdgeChoices.setEditable( false );
		add( colorEdgeChoices, c );

		// Colormap and ranges.
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl4 = new JLabel( "ColorMap" );
		lbl4.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl4, c );

		c.gridx = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		cmapEdge = new JComboBox<>( ColorMap.getColorMapNames().toArray( new String[] {} ) );
		add( cmapEdge, c );

		c.gridx = 2;
		c.gridwidth = 2;
		colorMapPainterEdge = new ColorMapPainter( cmapEdge );
		add( colorMapPainterEdge, c );
		cmapEdge.addActionListener( e -> colorMapPainterEdge.repaint() );

		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.;
		c.gridwidth = 1;
		c.gridy++;
		final JLabel lbl6 = new JLabel( "Min/Max" );
		lbl6.setHorizontalAlignment( SwingConstants.RIGHT );
		add( lbl6, c );

		c.gridx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.;
		final JPanel scalePanel2 = new JPanel();
		final BoxLayout boxLayout2 = new BoxLayout( scalePanel2, BoxLayout.LINE_AXIS );
		scalePanel2.setLayout( boxLayout2 );

		minEdge = new JFormattedTextField( Double.valueOf( minEdgeColorRange ) );
		minEdge.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel2.add( minEdge );
		maxEdge = new JFormattedTextField( Double.valueOf( maxEdgeColorRange ) );
		maxEdge.setHorizontalAlignment( SwingConstants.CENTER );
		scalePanel2.add( maxEdge );
		autoscaleEdge = new JButton( "Autoscale" );
		scalePanel2.add( autoscaleEdge );
		add( scalePanel2, c );

		/*
		 * Wire up edge listeners.
		 */

		cmapEdge.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final ColorMap colorMap = ColorMap.getColorMap( ( String ) cmapEdge.getSelectedItem() );
				if ( !current.getEdgeColorMap().equals( colorMap ) )
					current.edgeColorMap( colorMap );

				colorMapPainterEdge.repaint();
			}
		} );
		minEdge.addActionListener( ( e ) -> current.minEdgeColorRange( ( double ) minEdge.getValue() ) );
		minEdge.addFocusListener( new FocusListener()
		{
			@Override public void focusLost( final FocusEvent e ) { current.minEdgeColorRange( ( double ) minEdge.getValue() );  }
			@Override public void focusGained( final FocusEvent e ) {}
		} );
		maxEdge.addActionListener( ( e ) -> current.maxEdgeColorRange( ( double ) maxEdge.getValue() ) );
		maxEdge.addFocusListener( new FocusListener()
		{
			@Override public void focusLost( final FocusEvent e ) { current.maxEdgeColorRange( ( double ) maxEdge.getValue() );  }
			@Override public void focusGained( final FocusEvent e ) {}
		} );
		autoscaleEdge.addActionListener( autoscaleEdgeAction );
		final Collection< JComponent > toMute2 = Arrays.asList( new JComponent[] { cmapEdge, minEdge, maxEdge, autoscaleEdge, colorMapPainterEdge } );
		final ComponentMuter muter2 = new ComponentMuter( toMute2 );
		colorEdgeChoices.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final FeatureKeyWrapper fkw = new FeatureKeyWrapper( current.getEdgeFeatureKey(), current.getEdgeColorMode() );
				final FeatureKeyWrapper selected = colorEdgeChoices.getSelectedItem();
				if ( fkw != selected )
					current.edgeColorMode( ( EdgeColorMode ) selected.category, selected.featureKey );

				switch ( colorEdgeChoices.getSelectedCategory() )
				{
				case FIXED:
					muter2.enable( false );
					break;
				default:
					muter2.enable( true );
					break;
				}
			}
		} );
		muter2.enable( colorEdgeChoices.getSelectedCategory() != EdgeColorMode.FIXED );
	}

	public void update()
	{
		updateVertexColorModes();
		updateEdgeColorModes();
		cmapVertex.setSelectedItem( current.getVertexColorMap().getName() );
		cmapEdge.setSelectedItem( current.getEdgeColorMap().getName() );
		minEdge.setValue( current.getMinEdgeColorRange() );
		maxEdge.setValue( current.getMaxEdgeColorRange() );
		minVertex.setValue( current.getMinVertexColorRange() );
		maxVertex.setValue( current.getMaxVertexColorRange() );
	}

	private void autoScaleVertexFeature()
	{
		colorVertexChoices.setEnabled( false );
		minVertex.setEnabled( false );
		maxVertex.setEnabled( false );
		try
		{
			final VertexColorMode category = colorVertexChoices.getSelectedCategory();
			final String featureKey = colorVertexChoices.getSelectedItem().featureKey;
			final double[] range;
			switch ( category )
			{
			case BRANCH_EDGE:
			case BRANCH_VERTEX:
				range = branchGraphFeatureRangeCalculator.getRange( featureKey );
				break;
			default:
				range = featureRangeCalculator.getRange( featureKey );
				break;
			}
			if (null == range)
				return;
			current.minVertexColorRange( Double.valueOf( range[ 0 ] ) );
			current.maxVertexColorRange( Double.valueOf( range[ 1 ] ) );
			minVertex.setValue( Double.valueOf( range[ 0 ] ) );
			maxVertex.setValue( Double.valueOf( range[ 1 ] ) );
		}
		finally
		{
			colorVertexChoices.setEnabled( true );
			minVertex.setEnabled( true );
			maxVertex.setEnabled( true );
		}
	}

	private void autoScaleEdgeFeature()
	{
		colorEdgeChoices.setEnabled( false );
		minEdge.setEnabled( false );
		maxEdge.setEnabled( false );
		try
		{
			final EdgeColorMode category = colorEdgeChoices.getSelectedCategory();
			final String featureKey = colorEdgeChoices.getSelectedItem().featureKey;
			final double[] range;
			switch ( category )
			{
			case BRANCH_EDGE:
			case BRANCH_VERTEX:
				range = branchGraphFeatureRangeCalculator.getRange( featureKey );
				break;
			default:
				range = featureRangeCalculator.getRange( featureKey );
				break;
			}
			if ( null == range )
				return;
			current.minEdgeColorRange( Double.valueOf( range[ 0 ] ) );
			current.maxEdgeColorRange( Double.valueOf( range[ 1 ] ) );
			minEdge.setValue( Double.valueOf( range[ 0 ] ) );
			maxEdge.setValue( Double.valueOf( range[ 1 ] ) );
		}
		finally
		{
			colorEdgeChoices.setEnabled( true );
			minEdge.setEnabled( true );
			maxEdge.setEnabled( true );
		}
	}

	private void updateVertexColorModes()
	{
		/*
		 * Harvest possible choices.
		 */

		final Map< VertexColorMode, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< VertexColorMode, String > categoryNames = new HashMap<>();
		final Map< FeatureKeyWrapper, String > itemNames = new HashMap<>();

		// Fixed.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color", VertexColorMode.FIXED );
		items.put( VertexColorMode.FIXED, Collections.singleton( fixedColor ) );
		categoryNames.put( VertexColorMode.FIXED, categoryName( VertexColorMode.FIXED ) );

		// This vertex.
		final Collection< FeatureKeyWrapper > vertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			vertexProjections.add( new FeatureKeyWrapper( projectionKey, VertexColorMode.VERTEX ) );
		if ( !vertexProjections.isEmpty() )
		{
			items.put( VertexColorMode.VERTEX, vertexProjections );
			categoryNames.put( VertexColorMode.VERTEX, categoryName( VertexColorMode.VERTEX ) );
		}

		// Incoming and outgoing edges.
		final Collection< FeatureKeyWrapper > incomingEdgeProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > outgoingEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
		{
			incomingEdgeProjections.add( new FeatureKeyWrapper( projectionKey, VertexColorMode.INCOMING_EDGE ) );
			outgoingEdgeProjections.add( new FeatureKeyWrapper( projectionKey, VertexColorMode.OUTGOING_EDGE ) );
		}
		if ( !incomingEdgeProjections.isEmpty() )
		{
			items.put( VertexColorMode.INCOMING_EDGE, incomingEdgeProjections );
			items.put( VertexColorMode.OUTGOING_EDGE, outgoingEdgeProjections );
			categoryNames.put( VertexColorMode.INCOMING_EDGE, categoryName( VertexColorMode.INCOMING_EDGE ) );
			categoryNames.put( VertexColorMode.OUTGOING_EDGE, categoryName( VertexColorMode.OUTGOING_EDGE ) );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
			branchVertexProjections.add( new FeatureKeyWrapper( projectionKey, VertexColorMode.BRANCH_VERTEX ) );
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( VertexColorMode.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( VertexColorMode.BRANCH_VERTEX, categoryName( VertexColorMode.BRANCH_VERTEX ) );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
			branchEdgeProjections.add( new FeatureKeyWrapper( projectionKey, VertexColorMode.BRANCH_EDGE ) );
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( VertexColorMode.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( VertexColorMode.BRANCH_EDGE, categoryName( VertexColorMode.BRANCH_EDGE ) );
		}

		// Check if we can select the current mode.
		final Collection< FeatureKeyWrapper > all = new ArrayList<>();
		for ( final Collection< FeatureKeyWrapper > a : items.values() )
			all.addAll( a );
		final FeatureKeyWrapper c = new FeatureKeyWrapper( current.getVertexFeatureKey(), current.getVertexColorMode() );
		if ( !all.contains( c ) && current.getVertexColorMode() != VertexColorMode.FIXED )
		{
			// Make a dummy selectable item that will not affect the ColorMode.
			Collection< FeatureKeyWrapper > col = items.get( current.getVertexColorMode() );
			if ( null == col )
			{
				col = new ArrayList<>();
				items.put( current.getVertexColorMode(), col );
				categoryNames.put( current.getVertexColorMode(), categoryName( current.getVertexColorMode() ) );
			}
			col.add( c );
			itemNames.put( c, "Not computed: " + c.featureKey );
		}

		colorVertexChoices.resetContent( items, itemNames, categoryNames );
		colorVertexChoices.setSelectedItem( c );
	}

	private void updateEdgeColorModes()
	{
		/*
		 * Harvest possible choices.
		 */
		final Map< EdgeColorMode, Collection< FeatureKeyWrapper > > items = new LinkedHashMap<>();
		final Map< EdgeColorMode, String > categoryNames = new HashMap<>();
		final Map< FeatureKeyWrapper, String > itemNames = new HashMap<>();

		// Fixed color.
		final FeatureKeyWrapper fixedColor = new FeatureKeyWrapper( "Fixed color", EdgeColorMode.FIXED );
		items.put( EdgeColorMode.FIXED, Collections.singleton( fixedColor ) );
		itemNames.put( fixedColor, "Fixed color" );
		categoryNames.put( EdgeColorMode.FIXED, categoryName( EdgeColorMode.FIXED ) );

		// This edge.
		final Collection< FeatureKeyWrapper > edgeProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
		{
			final FeatureKeyWrapper fk = new FeatureKeyWrapper( projectionKey, EdgeColorMode.EDGE );
			edgeProjections.add( fk );
			itemNames.put( fk, fk.featureKey );
		}
		if ( !edgeProjections.isEmpty() )
		{
			items.put( EdgeColorMode.EDGE, edgeProjections );
			categoryNames.put( EdgeColorMode.EDGE, categoryName( EdgeColorMode.EDGE ) );
		}

		// Source and target vertex.
		final Collection< FeatureKeyWrapper > sourceVertexProjections = new ArrayList<>();
		final Collection< FeatureKeyWrapper > targetVertexProjections = new ArrayList<>();
		for ( final String projectionKey : featureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
		{
			final FeatureKeyWrapper fk1 = new FeatureKeyWrapper( projectionKey, EdgeColorMode.SOURCE_VERTEX );
			sourceVertexProjections.add( fk1 );
			itemNames.put( fk1, fk1.featureKey );

			final FeatureKeyWrapper fk2 = new FeatureKeyWrapper( projectionKey, EdgeColorMode.TARGET_VERTEX );
			targetVertexProjections.add( fk2 );
			itemNames.put( fk2, fk2.featureKey );
		}
		if ( !sourceVertexProjections.isEmpty() )
		{
			items.put( EdgeColorMode.SOURCE_VERTEX, sourceVertexProjections );
			items.put( EdgeColorMode.TARGET_VERTEX, targetVertexProjections );
			categoryNames.put( EdgeColorMode.SOURCE_VERTEX, categoryName( EdgeColorMode.SOURCE_VERTEX ) );
			categoryNames.put( EdgeColorMode.TARGET_VERTEX, categoryName( EdgeColorMode.TARGET_VERTEX ) );
		}

		// Branch edge.
		final Collection< FeatureKeyWrapper > branchEdgeProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.EDGE ) )
		{
			final FeatureKeyWrapper fk = new FeatureKeyWrapper( projectionKey, EdgeColorMode.BRANCH_EDGE );
			branchEdgeProjections.add( fk );
			itemNames.put( fk, fk.featureKey );
		}
		if ( !branchEdgeProjections.isEmpty() )
		{
			items.put( EdgeColorMode.BRANCH_EDGE, branchEdgeProjections );
			categoryNames.put( EdgeColorMode.BRANCH_EDGE, categoryName( EdgeColorMode.BRANCH_EDGE ) );
		}

		// Branch vertex.
		final Collection< FeatureKeyWrapper > branchVertexProjections = new ArrayList<>();
		for ( final String projectionKey : branchGraphFeatureKeys.getProjectionKeys( FeatureTarget.VERTEX ) )
		{
			final FeatureKeyWrapper fk = new FeatureKeyWrapper( projectionKey, EdgeColorMode.BRANCH_VERTEX );
			branchVertexProjections.add( fk );
			itemNames.put( fk, fk.featureKey );
		}
		if ( !branchVertexProjections.isEmpty() )
		{
			items.put( EdgeColorMode.BRANCH_VERTEX, branchVertexProjections );
			categoryNames.put( EdgeColorMode.BRANCH_VERTEX, categoryName( EdgeColorMode.BRANCH_VERTEX ) );
		}

		// Check if we can select the current mode.
		final Collection< FeatureKeyWrapper > all = new ArrayList<>();
		for ( final Collection< FeatureKeyWrapper > a : items.values() )
			all.addAll( a );
		final FeatureKeyWrapper c = new FeatureKeyWrapper( current.getEdgeFeatureKey(), current.getEdgeColorMode() );
		if ( !all.contains( c ) && current.getEdgeColorMode() != EdgeColorMode.FIXED )
		{
			// Make a dummy selectable item that will not affect the ColorMode.
			Collection< FeatureKeyWrapper > col = items.get( current.getEdgeColorMode() );
			if ( null == col )
			{
				col = new ArrayList<>();
				items.put( current.getEdgeColorMode(), col );
				categoryNames.put( current.getEdgeColorMode(), categoryName( current.getEdgeColorMode() ) );
			}
			col.add( c );
			itemNames.put( c, "Not computed: " + c.featureKey );
		}

		colorEdgeChoices.resetContent( items, itemNames, categoryNames );
		colorEdgeChoices.setSelectedItem( c );

	}

	private static final String categoryName( final EdgeColorMode colorMode )
	{
		switch ( colorMode )
		{
		case BRANCH_EDGE:
			return "Branch link feature";
		case BRANCH_VERTEX:
			return "Branch spot feature";
		case EDGE:
			return "Link feature";
		case FIXED:
			return "Fixed";
		case SOURCE_VERTEX:
			return "Source spot feature";
		case TARGET_VERTEX:
			return "Target spot feature";
		default:
			return colorMode.toString();
		}
	}

	private static final String categoryName( final VertexColorMode colorMode )
	{
		switch ( colorMode )
		{
		case BRANCH_EDGE:
			return "Branch link feature";
		case BRANCH_VERTEX:
			return "Branch spot feature";
		case FIXED:
			return "Fixed";
		case INCOMING_EDGE:
			return "Incoming link feature";
		case OUTGOING_EDGE:
			return "Outoing link feature";
		case VERTEX:
			return "Spot feature";
		default:
			return colorMode.toString();
		}
	}

	private static final class FeatureKeyWrapper
	{
		private final String featureKey;

		private final Enum< ? > category;

		public FeatureKeyWrapper( final String featureKey, final Enum< ? > category )
		{
			this.featureKey = featureKey;
			this.category = category;
		}

		@Override
		public String toString()
		{
			return featureKey;
		}

		@Override
		public boolean equals( final Object obj )
		{
			if ( obj instanceof FeatureKeyWrapper )
			{
				final FeatureKeyWrapper o = ( FeatureKeyWrapper ) obj;
				return ( category.equals( o.category ) && featureKey.equals( o.featureKey ) );
			}
			return false;
		}
	}

	private static final class ColorMapPainter extends JComponent
	{

		private static final long serialVersionUID = 1L;

		private final JComboBox< String > choices;

		public ColorMapPainter( final JComboBox< String > choices )
		{
			this.choices = choices;
		}

		@Override
		protected void paintComponent( final Graphics g )
		{
			super.paintComponent( g );
			if ( !isEnabled() )
				return;

			final String cname = ( String ) choices.getSelectedItem();
			final ColorMap cmap = ColorMap.getColorMap( cname );
			final int w = getWidth();
			final int h = getHeight();
			final int lw = ( int ) ( 0.8 * w );
			for ( int i = 0; i < lw; i++ )
			{
				g.setColor( cmap.get( ( double ) i / lw ) );
				g.drawLine( i, 0, i, h );
			}

			// NaN.
			g.setColor( cmap.get( Double.NaN ) );
			g.fillRect( ( int ) ( 0.83 * w ), 0, ( int ) ( 0.07 * w ), h );

			// Missing color.
			g.setColor( cmap.getMissingColor() );
			g.fillRect( ( int ) ( 0.93 * w ), 0, ( int ) ( 0.07 * w ), h );
		}

		@Override
		public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			dimension.height = 20;
			return dimension;
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return PREFERRED_SIZE;
	}

	private static final class ComponentMuter
	{
		private final Collection< JComponent > toMute;

		public ComponentMuter( final Collection< JComponent > toMute )
		{
			this.toMute = toMute;
		}

		public void enable( final boolean enable )
		{
			for ( final JComponent c : toMute )
				c.setEnabled( enable );
		}

	}

	@Override
	public void setFont( final Font font )
	{
		super.setFont( font );
		for ( final Component child : getComponents() )
			changeFont( child, font );
	}

	private static final void changeFont( final Component component, final Font font )
	{
		component.setFont( font );
		if ( component instanceof Container )
			for ( final Component child : ( ( Container ) component ).getComponents() )
				changeFont( child, font );
	}

	@Override
	public void setEnabled( final boolean enabled )
	{
		super.setEnabled( enabled );
		colorEdgeChoices.setEnabled( enabled );
		colorVertexChoices.setEnabled( enabled );
		// Don't enable for fixed colors.
		final boolean vertexEnable = enabled && ( colorVertexChoices.getSelectedCategory() != VertexColorMode.FIXED );
		colorMapPainterVertex.setEnabled( vertexEnable );
		cmapVertex.setEnabled( vertexEnable );
		minVertex.setEnabled( vertexEnable );
		maxVertex.setEnabled( vertexEnable );
		autoscaleVertex.setEnabled( vertexEnable );
		final boolean edgeEnable = enabled && ( colorEdgeChoices.getSelectedCategory() != EdgeColorMode.FIXED );
		colorMapPainterEdge.setEnabled( edgeEnable );
		cmapEdge.setEnabled( edgeEnable );
		minEdge.setEnabled( edgeEnable );
		maxEdge.setEnabled( edgeEnable );
		autoscaleEdge.setEnabled( edgeEnable );
	}
}
