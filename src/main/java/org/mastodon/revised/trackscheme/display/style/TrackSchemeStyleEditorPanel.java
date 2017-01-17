package org.mastodon.revised.trackscheme.display.style;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;
import org.mastodon.revised.trackscheme.display.style.TrackSchemeStyle.UpdateListener;
import org.mastodon.revised.ui.coloring.ColorModePicker;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;

public class TrackSchemeStyleEditorPanel extends JPanel implements UpdateListener
{
	private static final long serialVersionUID = 1L;

	private final JColorChooser colorChooser;

	private final List< JButton > edgeColorButtonToMute;

	private final List< JButton > vertexColorButtonToMute;

	final TrackSchemeStyle style;

	private final ColorModePicker colorModeUI;

	private final Map< JButton, ColorSetter > colorSetters;

	private final Map< JCheckBox, BooleanSetter > checkBoxes;

	public TrackSchemeStyleEditorPanel(
			final TrackSchemeStyle style, final FeatureKeys featureKeys,
			final FeatureRangeCalculator featureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys,
			final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{
		super( new GridBagLayout() );
		this.style = style;
		style.addUpdateListener( this );
		colorChooser = new JColorChooser();

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 0, 5, 0, 5 );
		c.ipadx = 0;
		c.ipady = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		/*
		 * Vertices and edges color modes.
		 */

		this.colorModeUI = new ColorModePicker( style,
				featureKeys, featureRangeCalculator,
				branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );
		colorModeUI.setFont( getFont().deriveFont( 11f ) );
		c.gridwidth = 5;
		add( colorModeUI, c );

		/*
		 * Fixed color setters.
		 */

		final List< ColorSetter > styleColors = styleColors( style );
		final List< BooleanSetter > styleBooleans = styleBooleans( style );

		this.edgeColorButtonToMute = new ArrayList<>();
		this.vertexColorButtonToMute = new ArrayList<>();
		this.colorSetters = new HashMap<>();

		c.gridx = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridy++;
		add( Box.createVerticalStrut( 5 ), c );
		c.gridy++;
		c.gridwidth = 2;
		final int columnStart = c.gridy;
		for ( final ColorSetter colorSetter : styleColors )
		{
			final JButton button = new JButton( colorSetter.getLabel(), new ColorIcon( colorSetter.getColor() ) );
			colorSetters.put( button, colorSetter );
			button.setOpaque( false );
			button.setContentAreaFilled( false );
			button.setBorderPainted( false );
			button.setMargin( new Insets( 0, 0, 0, 0 ) );
			button.setBorder( new EmptyBorder( 2, 2, 2, 2 ) );
			button.setHorizontalAlignment( SwingConstants.LEFT );
			button.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed( final ActionEvent e )
				{
					colorChooser.setColor( colorSetter.getColor() );
					final JDialog d = JColorChooser.createDialog( button, "Choose a color", true, colorChooser, new ActionListener()
					{
						@Override
						public void actionPerformed( final ActionEvent arg0 )
						{
							final Color c = colorChooser.getColor();
							if ( c != null )
							{
								button.setIcon( new ColorIcon( c ) );
								colorSetter.setColor( c );
							}
						}
					}, null );
					d.setVisible( true );
				}
			} );
			c.anchor = GridBagConstraints.LINE_END;
			add( button, c );
			c.gridy++;

			if ( c.gridy - columnStart > 8 )
			{
				c.gridy = columnStart;
				c.gridx = 2;
			}

			if ( colorSetter.skip > 0 )
			{
				add( Box.createVerticalStrut( 5 ), c );
				c.gridy++;
			}

			switch ( colorSetter.getLabel() )
			{
			case "edge color":
				edgeColorButtonToMute.add( button );
				break;
			case "vertex fill color":
			case "simplified vertex fill color":
				vertexColorButtonToMute.add( button );
				break;
			}
		}

		c.gridy = columnStart + 9;
		c.gridx = 0;
		add( Box.createVerticalStrut( 5 ), c );
		c.gridy++;

		// On 2 columns
		this.checkBoxes = new HashMap<>();
		final int startLine = c.gridy;
		boolean firstCol = true;
		for ( int i = 0; i < styleBooleans.size(); i++ )
		{
			if ( firstCol && i >= styleBooleans.size() / 2 )
			{
				firstCol = false;
				c.gridy = startLine;
				c.gridx = c.gridx + 2;
			}
			final BooleanSetter booleanSetter = styleBooleans.get( i );
			final JCheckBox checkbox = new JCheckBox( booleanSetter.getLabel(), booleanSetter.get() );
			checkBoxes.put( checkbox, booleanSetter );
			checkbox.addActionListener( ( e ) -> booleanSetter.set( checkbox.isSelected() ) );
			add( checkbox, c );
			c.gridy++;
		}
	}

	@Override
	public void trackSchemeStyleChanged()
	{
		final boolean muteVertexStuff = ( style.getVertexColorMode() == VertexColorMode.FIXED );
		for ( final JButton jb : vertexColorButtonToMute )
			jb.setEnabled( muteVertexStuff );

		final boolean muteEdgeStuff = ( style.getEdgeColorMode() == EdgeColorMode.FIXED );
		for ( final JButton jb : edgeColorButtonToMute )
			jb.setEnabled( muteEdgeStuff );
	}

	void update()
	{
		colorModeUI.update();
		for ( final JButton button : colorSetters.keySet() )
			button.setIcon( new ColorIcon( colorSetters.get( button ).getColor() ) );
		for ( final JCheckBox cb : checkBoxes.keySet() )
			cb.setSelected( checkBoxes.get( cb ).get() );
	}

	@Override
	public void setEnabled( final boolean enabled )
	{
		super.setEnabled( enabled );
		final Component[] comps = getComponents();
		for ( final Component comp : comps )
			comp.setEnabled( enabled );

		// Specially don't enable stuff to mute.
		if ( enabled )
		{
			final VertexColorMode colorVertexBy = style.getVertexColorMode();
			for ( final JComponent button : vertexColorButtonToMute )
				button.setEnabled( colorVertexBy == VertexColorMode.FIXED );

			final EdgeColorMode colorEdgeBy = style.getEdgeColorMode();
			for ( final JComponent button : edgeColorButtonToMute )
				button.setEnabled( colorEdgeBy == EdgeColorMode.FIXED );
		}
	}

	private static abstract class ColorSetter
	{
		private final String label;

		private final int skip;

		public ColorSetter( final String label )
		{
			this( label, 0 );
		}

		public ColorSetter( final String label, final boolean skip )
		{
			this( label, skip ? 15 : 0 );
		}

		public ColorSetter( final String label, final int skip )
		{
			this.label = label;
			this.skip = skip;
		}

		public String getLabel()
		{
			return label;
		}

		public abstract Color getColor();

		public abstract void setColor( Color c );
	}

	private static abstract class BooleanSetter
	{
		private final String label;

		public BooleanSetter( final String label )
		{
			this.label = label;
		}

		public String getLabel()
		{
			return label;
		}

		public abstract boolean get();

		public abstract void set( boolean b );
	}

	private static List< ColorSetter > styleColors( final TrackSchemeStyle style )
	{
		return Arrays.asList( new ColorSetter[] {
				new ColorSetter( "edge color" )
				{
					@Override public Color getColor() { return style.getEdgeColor(); }
					@Override public void setColor( final Color c ) { style.edgeColor( c ); }
				},
				new ColorSetter( "vertex fill color" )
				{
					@Override public Color getColor() { return style.getVertexFillColor(); }
					@Override public void setColor( final Color c ) { style.vertexFillColor( c ); }
				},
				new ColorSetter( "vertex draw color" )
				{
					@Override public Color getColor() { return style.getVertexDrawColor(); }
					@Override public void setColor( final Color c ) { style.vertexDrawColor( c ); }
				},
				new ColorSetter( "simplified vertex fill color", true )
				{
					@Override public Color getColor() { return style.getSimplifiedVertexFillColor(); }
					@Override public void setColor( final Color c ) { style.simplifiedVertexFillColor( c ); }
				},
				new ColorSetter( "selected edge color" )
				{
					@Override public Color getColor() { return style.getSelectedEdgeColor(); }
					@Override public void setColor( final Color c ) { style.selectedEdgeColor( c ); }
				},
				new ColorSetter( "selected vertex draw color" )
				{
					@Override public Color getColor() { return style.getSelectedVertexDrawColor(); }
					@Override public void setColor( final Color c ) { style.selectedVertexDrawColor( c ); }
				},
				new ColorSetter( "selected vertex fill color" ) {
					@Override public Color getColor() { return style.getSelectedVertexFillColor(); }
					@Override public void setColor( final Color c ) { style.selectedVertexFillColor( c ); }
				},
				// Column break
				new ColorSetter( "selected simplified vertex fill color", false )
				{
					@Override public Color getColor() { return style.getSelectedSimplifiedVertexFillColor(); }
					@Override public void setColor( final Color c ) { style.selectedSimplifiedVertexFillColor( c ); }
				},
				new ColorSetter( "background color" ) {
					@Override public Color getColor() { return style.getBackgroundColor(); }
					@Override public void setColor( final Color c ) { style.backgroundColor( c ); }
				},
				new ColorSetter( "current timepoint color" ) {
					@Override public Color getColor() { return style.getCurrentTimepointColor(); }
					@Override public void setColor( final Color c ) { style.currentTimepointColor( c ); }
				},
				new ColorSetter( "decoration color" ) {
					@Override public Color getColor() { return style.getDecorationColor(); }
					@Override public void setColor( final Color c ) { style.decorationColor( c ); }
				},
				new ColorSetter( "vertex range color", true ) {
					@Override public Color getColor() { return style.getVertexRangeColor(); }
					@Override public void setColor( final Color c ) { style.vertexRangeColor( c ); }
				},
				new ColorSetter( "header background color" ) {
					@Override public Color getColor() { return style.getHeaderBackgroundColor(); }
					@Override public void setColor( final Color c ) { style.headerBackgroundColor( c ); }
				},
				new ColorSetter( "header decoration color" ) {
					@Override public Color getColor() { return style.getHeaderDecorationColor(); }
					@Override public void setColor( final Color c ) { style.headerDecorationColor( c ); }
				},
				new ColorSetter( "header current timepoint color" ) {
					@Override public Color getColor() { return style.getHeaderCurrentTimepointColor(); }
					@Override public void setColor( final Color c ) { style.headerCurrentTimepointColor( c ); }
				}
		} );
	}

	private static List< BooleanSetter > styleBooleans( final TrackSchemeStyle style )
	{
		return Arrays.asList( new BooleanSetter[] {
				new BooleanSetter( "paint rows ") {
					@Override public boolean get() { return style.isPaintRows(); }
					@Override public void set( final boolean b ) { style.paintRows( b ); }
				},
				new BooleanSetter( "paint columns ") {
					@Override public boolean get() { return style.isPaintColumns(); }
					@Override public void set( final boolean b ) { style.paintColumns( b ); }
				},
				new BooleanSetter( "highlight current timepoint ") {
					@Override public boolean get() { return style.isHighlightCurrentTimepoint(); }
					@Override public void set( final boolean b ) { style.highlightCurrentTimepoint( b ); }
				},
				new BooleanSetter( "paint header shadow ") {
					@Override public boolean get() { return style.isPaintHeaderShadow(); }
					@Override public void set( final boolean b ) { style.paintHeaderShadow( b ); }
				}
		} );
	}

	/**
	 * Adapted from http://stackoverflow.com/a/3072979/230513
	 */
	private static class ColorIcon implements Icon
	{
		private final int size = 16;

		private final Color color;

		public ColorIcon( final Color color )
		{
			this.color = color;
		}

		@Override
		public void paintIcon( final Component c, final Graphics g, final int x, final int y )
		{
			final Graphics2D g2d = ( Graphics2D ) g;
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2d.setColor( color );
			// g2d.fillOval( x, y, size, size );
			g2d.fill( new RoundRectangle2D.Float( x, y, size, size, 5, 5 ) );
		}

		@Override
		public int getIconWidth()
		{
			return size;
		}

		@Override
		public int getIconHeight()
		{
			return size;
		}
	}
}