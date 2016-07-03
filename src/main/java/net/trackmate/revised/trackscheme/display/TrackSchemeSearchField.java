package net.trackmate.revised.trackscheme.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.trackmate.graph.GraphChangeListener;
import net.trackmate.revised.trackscheme.TrackSchemeGraph;
import net.trackmate.revised.trackscheme.TrackSchemeVertex;

public class TrackSchemeSearchField extends JTextField
{
	private static final long serialVersionUID = 1L;

	private static final String UNFOCUSED_TEXT = "Search...";

	private static final ImageIcon FOCUSED_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private static final ImageIcon UNFOCUSED_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private static final ImageIcon FOUND_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private static final ImageIcon NOT_FOUND_ICON = new ImageIcon( TrackSchemeSearchField.class.getResource( "find-24x24.png" ) );

	private TrackSchemeGraph< ?, ? > graph;

	private int leftInset;

	private ImageIcon icon;

	public TrackSchemeSearchField( TrackSchemeGraph< ?, ? > graph )
	{
		this.graph = graph;

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
				icon = FOCUSED_ICON;
				repaint();
			}

			@Override
			public void focusLost( final java.awt.event.FocusEvent evt )
			{
				icon = UNFOCUSED_ICON;
				repaint();
			}
		} );

		SearchAction sa = new SearchAction();
		graph.addGraphChangeListener( sa );
		addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				setEnabled( false );
				new Thread()
				{
					public void run()
					{
						try
						{
							boolean found = sa.search( getText() );
							icon = found ? FOUND_ICON : NOT_FOUND_ICON;
						}
						finally
						{
							setEnabled( true );
							requestFocusInWindow();
						}
					};
				}.start();
			}
		} );

	}

	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

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

	private class SearchAction implements GraphChangeListener
	{

		private Iterator< TrackSchemeVertex > iterator;
		private boolean graphChanged;

		public SearchAction()
		{
			reinit();
		}

		private synchronized boolean search( final String text )
		{
			graphChanged = false;
			TrackSchemeVertex start = graph.vertexRef();
			TrackSchemeVertex v = next();
			start.refTo( v );

			// Avoid testing equality with unset reference.
			boolean set = false;
			while ( !graphChanged && ( !set ||  !( v = next() ).equals( start ) ) )
			{
				if (!set) 
					set = true;

				if ( v.getLabel().contains( text ) )
				{
					graph.releaseRef( start );
					System.out.println( v ); // DEBUG
					// DO SOMETHING TODO.
					return true;
				}
			}

			System.out.println( "not found with graphchanged = " + graphChanged ); // DEBUG
			
			graph.releaseRef( start );
			return false;
		}

		private TrackSchemeVertex next()
		{
			if ( !iterator.hasNext() )
				iterator = graph.vertices().iterator();

			return iterator.next();
		}

		@Override
		public void graphChanged()
		{
			reinit();
		}

		private synchronized void reinit()
		{
			graphChanged = true;
			iterator = graph.vertices().iterator();
		}
	}
}
