package net.trackmate.revised.trackscheme.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import net.trackmate.graph.GraphChangeListener;
import net.trackmate.graph.algorithm.traversal.DepthFirstIterator;
import net.trackmate.revised.trackscheme.TrackSchemeEdge;
import net.trackmate.revised.trackscheme.TrackSchemeGraph;
import net.trackmate.revised.trackscheme.TrackSchemeVertex;

public class TrackSchemeSearchField extends JTextField
{
	private static final long serialVersionUID = 1L;

	private static final String UNFOCUSED_TEXT = "Search...";

	private static final ImageIcon FOCUSED_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private static final ImageIcon UNFOCUSED_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private final Font normalFont;

	private final Font notFoundFont;

	private final PropertyChangeSupport observer = new PropertyChangeSupport( this );

	private TrackSchemeGraph< ?, ? > graph;

	private int leftInset;

	private ImageIcon icon;

	/**
	 */
	@SuppressWarnings( "unchecked" )
	public TrackSchemeSearchField( TrackSchemeGraph< ?, ? > graph )
	{
		this.graph = graph;

		normalFont = getFont();
		@SuppressWarnings( "rawtypes" )
		final Map attributes = normalFont.getAttributes();
		attributes.put( TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON );
		attributes.put( TextAttribute.FOREGROUND, Color.RED.darker() );
		notFoundFont = new Font( attributes );

		Border border = UIManager.getBorder( "TextField.border" );
		JTextField dummy = new JTextField();
		leftInset = border.getBorderInsets( dummy ).left;
		
		icon = UNFOCUSED_ICON;

		setPreferredSize( new Dimension( 80, 25 ) );
		setMaximumSize( new Dimension( 160, 25 ) );

		addFocusListener( new java.awt.event.FocusAdapter()
		{
			@Override
			public void focusGained( final java.awt.event.FocusEvent evt )
			{
				searchBoxFocusGained( evt );
				repaint();
			}

			@Override
			public void focusLost( final java.awt.event.FocusEvent evt )
			{
				searchBoxFocusLost( evt );
				repaint();
			}
		} );
		addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyReleased( final KeyEvent e )
			{
				searchBoxKey( e );
			}
		} );

		SearchAction sa = new SearchAction();
		graph.addGraphChangeListener( sa );
		observer.addPropertyChangeListener( sa );
	}

	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		if (hasFocus())
			icon = hasFocus() ? FOCUSED_ICON : UNFOCUSED_ICON;

		int textX = 2;
		if ( this.icon != null )
		{
			int iconWidth = icon.getIconWidth();
			int iconHeight = icon.getIconHeight();
			int x = leftInset;
			textX = x + iconWidth + 2;
			int y = ( this.getHeight() - iconHeight ) / 2;
			icon.paintIcon( this, g, x, y );
		}
		setMargin( new Insets( 2, textX, 2, 2 ) );

		if ( !hasFocus() && getText().equals( "" ) )
		{
			int height = this.getHeight();
			Font prev = g.getFont();
			Font italic = prev.deriveFont( Font.ITALIC );
			Color prevColor = g.getColor();
			g.setFont( italic );
			g.setColor( UIManager.getColor( "textInactiveText" ) );
			int h = g.getFontMetrics().getHeight();
			int textBottom = ( height - h ) / 2 + h - 4;
			int x = this.getInsets().left;
			Graphics2D g2d = ( Graphics2D ) g;
			RenderingHints hints = g2d.getRenderingHints();
			g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
			g2d.drawString( UNFOCUSED_TEXT, x, textBottom );
			g2d.setRenderingHints( hints );
			g.setFont( prev );
			g.setColor( prevColor );
		}
		

	}

	private void searchBoxKey( final KeyEvent e )
	{
		setFont( normalFont );
		if ( getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER )
			observer.firePropertyChange( "Searching...", null, getText() );
	}

	private void searchBoxFocusGained( final java.awt.event.FocusEvent evt )
	{
		setFont( normalFont );
		setFont( getFont().deriveFont( Font.PLAIN ) );
	}

	private void searchBoxFocusLost( final java.awt.event.FocusEvent evt )
	{
		setFont( normalFont );
		setFont( getFont().deriveFont( Font.ITALIC ) );
	}

	private class SearchAction implements PropertyChangeListener, Iterator< TrackSchemeVertex >, GraphChangeListener
	{

		private Iterator< TrackSchemeVertex > iterator;

		private Iterator< TrackSchemeVertex > trackIterator;

		public SearchAction()
		{
			reinit();
		}

		@Override
		public void propertyChange( final PropertyChangeEvent evt )
		{
			final String text = ( String ) evt.getNewValue();
			if ( !text.isEmpty() )
				search( text );
		}

		private void search( final String text )
		{
			TrackSchemeVertex start = null;
			TrackSchemeVertex v;
			while ( ( v = next() ) != start )
			{
				if ( start == null )
					start = v;

				if ( v.getLabel().contains( text ) )
				{
					System.out.println( v ); // DEBUG
					return;
				}
			}
			setFont( notFoundFont );

		}

		@Override
		public boolean hasNext()
		{
			return true;
		}

		@Override
		public TrackSchemeVertex next()
		{
			if ( null == iterator || !iterator.hasNext() )
			{
				if ( null == trackIterator || !trackIterator.hasNext() )
					trackIterator = graph.getRoots().iterator();

				final TrackSchemeVertex root = trackIterator.next();
				iterator = new DepthFirstIterator< TrackSchemeVertex, TrackSchemeEdge >( root, graph );
			}
			return iterator.next();
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void graphChanged()
		{
			reinit();
		}

		@SuppressWarnings( "unchecked" )
		private void reinit()
		{
			this.trackIterator = graph.getRoots().iterator();
			if ( trackIterator.hasNext() )
			{
				final TrackSchemeVertex root = trackIterator.next();
				iterator = new DepthFirstIterator< TrackSchemeVertex, TrackSchemeEdge >( root, graph );
			}
			else
			{
				iterator = Collections.EMPTY_LIST.iterator();
			}
		}
	}
}
