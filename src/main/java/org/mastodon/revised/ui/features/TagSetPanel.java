package org.mastodon.revised.ui.features;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.mastodon.collection.RefCollection;
import org.mastodon.revised.model.tagset.Tag;
import org.mastodon.revised.model.tagset.TagSetFeature;
import org.mastodon.revised.ui.util.ColorMap;

public class TagSetPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private static final ImageIcon ADD_ICON = new ImageIcon( TagSetPanel.class.getResource( "add.png" ) );

	private static final ImageIcon REMOVE_ICON = new ImageIcon( TagSetPanel.class.getResource( "delete.png" ) );

	private static final ImageIcon SMALL_ADD_ICON = new ImageIcon( TagSetPanel.class.getResource( "bullet_green.png" ) );

	private static final ImageIcon SMALL_REMOVE_ICON = new ImageIcon( TagSetPanel.class.getResource( "bullet_delete.png" ) );

	private static final Iterator< String > TAG_KEY_GENERATOR = new Iterator< String >()
	{
		private final AtomicInteger KEYGEN = new AtomicInteger();

		@Override
		public boolean hasNext()
		{
			return true;
		}

		@Override
		public String next()
		{
			return "TAG_SET_" + KEYGEN.getAndIncrement();
		}
	};

	private static final AtomicInteger RAN = new AtomicInteger( 5 );

	private final JScrollPane scrollPane;

	private final JColorChooser colorChooser;

	private final RefCollection< ? > pool;

	private final DefaultComboBoxModel< TagSetFeature< ? > > model;

	private final ArrayList< UpdateListener > listeners;

	public TagSetPanel( final RefCollection< ? > pool )
	{
		this.pool = pool;
		this.listeners = new ArrayList<>();
		setLayout( new BorderLayout( 0, 0 ) );
		colorChooser = new JColorChooser();

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		add( scrollPane, BorderLayout.CENTER );

		final Box horizontalBox = Box.createHorizontalBox();
		add( horizontalBox, BorderLayout.NORTH );

		final Component hs1 = Box.createHorizontalStrut( 20 );
		hs1.setPreferredSize( new Dimension( 5, 0 ) );
		horizontalBox.add( hs1 );

		final JLabel lblTagSet = new JLabel( "Tag set:" );
		lblTagSet.setFont( getFont().deriveFont( Font.BOLD ) );
		horizontalBox.add( lblTagSet );

		final Component hs2 = Box.createHorizontalStrut( 20 );
		hs2.setPreferredSize( new Dimension( 10, 0 ) );
		horizontalBox.add( hs2 );

		this.model = new DefaultComboBoxModel<>();
		final JComboBox< TagSetFeature< ? > > comboBoxTagSets = new JComboBox<>( model );
		comboBoxTagSets.setFont( getFont().deriveFont( Font.BOLD ) );
		comboBoxTagSets.addActionListener( e -> update() );
		horizontalBox.add( comboBoxTagSets );
		comboBoxTagSets.setEditable( true );
		final MyComboBoxEditor editor = new MyComboBoxEditor();
		comboBoxTagSets.setEditor( editor );

		final JButton btnAdd = new JButton( ADD_ICON );
		btnAdd.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				addTagSet();
				editor.editorPane.field.requestFocusInWindow();
			}
		} );
		btnAdd.setPreferredSize( new Dimension( 25, 30 ) );
		btnAdd.setContentAreaFilled( false );
		horizontalBox.add( btnAdd );

		final JButton btnRemove = new JButton( REMOVE_ICON );
		btnRemove.addActionListener( e -> removeTagSet() );
		btnRemove.setPreferredSize( new Dimension( 25, 25 ) );
		btnRemove.setContentAreaFilled( false );
		horizontalBox.add( btnRemove );
	}

	private void update()
	{
		final JPanel panelTags = new JPanel();
		scrollPane.setViewportView( panelTags );
		final GridBagLayout layout = new GridBagLayout();
		panelTags.setLayout( layout );

		final TagSetFeature< ? > tagset = ( TagSetFeature< ? > ) model.getSelectedItem();
		if ( null == tagset )
			return;

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 0, 5, 0, 5 );
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridy = 0;

		final Collection< Tag > tags = tagset.getTags();
		int index = 1;
		if ( !tags.isEmpty() )
			for ( final Tag tag : tags )
			{
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 0;
				c.weightx = 0.;
				final JLabel lblIndex = new JLabel( "" + index++ + "." );
				panelTags.add( lblIndex, c );

				c.anchor = GridBagConstraints.PAGE_START;
				c.gridx++;
				c.weightx = 1.;
				final JTextField txtLabel = new JTextField( tag.label() );
				txtLabel.addActionListener( new ActionListener()
				{
					@Override
					public void actionPerformed( final ActionEvent e )
					{
						final String nlbl = txtLabel.getText();
						if ( !tag.label().equals( nlbl ) )
						{
							tag.setLabel( nlbl );
							update();
						}
					}
				} );
				txtLabel.addFocusListener( new FocusListener()
				{

					@Override
					public void focusLost( final FocusEvent e )
					{
						final String nlbl = txtLabel.getText();
						if ( !tag.label().equals( nlbl ) )
						{
							tag.setLabel( nlbl );
							update();
						}
					}

					@Override
					public void focusGained( final FocusEvent e )
					{
						txtLabel.selectAll();
					}
				} );
				panelTags.add( txtLabel, c );
				txtLabel.requestFocusInWindow();

				c.gridx++;
				c.weightx = 0.;
				final ColorSetter colorSetter = new ColorSetter()
				{
					@Override
					public Color getColor()
					{
						return tag.color();
					}

					@Override
					public void setColor( final Color color )
					{
						tag.setColor( color );
					}
				};
				final JButton btnColor = new JButton( new ColorIcon( colorSetter.getColor() ) );
				btnColor.setContentAreaFilled( false );
				btnColor.setBorderPainted( false );
				btnColor.addActionListener( new ActionListener()
				{
					@Override
					public void actionPerformed( final ActionEvent e )
					{
						colorChooser.setColor( colorSetter.getColor() );
						final JDialog d = JColorChooser.createDialog( btnColor, "Choose a color", true, colorChooser, new ActionListener()
						{
							@Override
							public void actionPerformed( final ActionEvent arg0 )
							{
								final Color c = colorChooser.getColor();
								if ( c != null )
								{
									btnColor.setIcon( new ColorIcon( c ) );
									colorSetter.setColor( c );
								}
							}
						}, null );
						d.setVisible( true );
					}
				} );
				panelTags.add( btnColor, c );

				final JButton btnRemove = new JButton( SMALL_REMOVE_ICON );
				btnRemove.setSize( 25, 25 );
				btnRemove.setContentAreaFilled( false );
				btnRemove.addActionListener( new ActionListener()
				{
					@Override
					public void actionPerformed( final ActionEvent e )
					{
						tagset.removeTag( tag );
						update();
					}
				} );
				c.gridx++;
				panelTags.add( btnRemove, c );

				c.gridy++;
			}

		c.gridx = 1;
		c.weightx = 1.;
		panelTags.add( new JLabel(), c );

		final JButton btnAdd = new JButton( SMALL_ADD_ICON );
		btnAdd.setSize( 25, 25 );
		btnAdd.setContentAreaFilled( false );
		btnAdd.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final Tag tag = tagset.createTag();
				tag.setColor( ColorMap.GLASBEY.getColor( RAN.getAndIncrement() % ColorMap.GLASBEY.getNColors() ) );
				update();
			}
		} );
		c.gridx = 3;
		c.weighty = 1;
		c.weightx = 0.;
		panelTags.add( btnAdd, c );
	}

	private void removeTagSet()
	{
		final TagSetFeature< ? > tagset = ( TagSetFeature< ? > ) model.getSelectedItem();
		model.removeElement( tagset );
		update();
		notifyListeners();
	}

	private void addTagSet()
	{
		final TagSetFeature< ? > current = ( TagSetFeature< ? > ) model.getSelectedItem();
		final String name = ( null == current ) ? "Tag set" : makeNewName( current.getName() );
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		final TagSetFeature< ? > tagset = new TagSetFeature( TAG_KEY_GENERATOR.next(), name, pool );
		model.addElement( tagset );
		model.setSelectedItem( tagset );
		update();
		notifyListeners();
	}

	private void notifyListeners()
	{
		for ( final UpdateListener l : listeners )
			l.tagSetCollectionUpdated();
	}

	public synchronized boolean addUpdateListener( final UpdateListener l )
	{
		if ( !listeners.contains( l ) )
		{
			listeners.add( l );
			return true;
		}
		return false;
	}

	public synchronized boolean removeUpdateListener( final UpdateListener l )
	{
		return listeners.remove( l );
	}

	private final String makeNewName( final String name )
	{
		final Pattern pattern = Pattern.compile( "(.+) \\((\\d+)\\)$" );
		final Matcher matcher = pattern.matcher( name );
		int n;
		String prefix;
		if ( matcher.matches() )
		{
			final String nstr = matcher.group( 2 );
			n = Integer.parseInt( nstr );
			prefix = matcher.group( 1 );
		}
		else
		{
			n = 1;
			prefix = name;
		}
		String newName;
		INCREMENT: while ( true )
		{
			newName = prefix + " (" + ( ++n ) + ")";
			for ( int j = 0; j < model.getSize(); j++ )
			{
				if ( model.getElementAt( j ).getName().equals( newName ) )
					continue INCREMENT;
			}
			break;
		}
		return newName;
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

	private static abstract class ColorSetter
	{
		public abstract Color getColor();

		public abstract void setColor( Color c );
	}

	public class MyComboBoxEditor implements ComboBoxEditor
	{

		private final EditorPane editorPane;

		public MyComboBoxEditor()
		{
			editorPane = new EditorPane();
			addActionListener( e -> rename() );
			editorPane.field.addFocusListener( new FocusListener()
			{
				@Override
				public void focusLost( final FocusEvent e )
				{
					rename();
					repaint();
				}

				@Override
				public void focusGained( final FocusEvent arg0 )
				{
					editorPane.field.selectAll();
				};
			} );
		}

		private void rename()
		{
			final Object obj = getItem();
			if ( obj == null )
				return;
			final TagSetFeature< ? > tsf = ( TagSetFeature< ? > ) obj;
			tsf.setName( editorPane.getText() );
		}

		@Override
		public Component getEditorComponent()
		{
			return editorPane;
		}

		@Override
		public void setItem( final Object anObject )
		{
			if ( anObject == null )
			{
				editorPane.field.setEnabled( false );
				return;
			}
			editorPane.field.setEnabled( true );
			editorPane.setText( anObject.toString() );
		}

		@Override
		public Object getItem()
		{
			return model.getSelectedItem();
		}

		@Override
		public void selectAll()
		{
			editorPane.selectAll();
		}

		@Override
		public void addActionListener( final ActionListener l )
		{
			editorPane.addActionListener( l );
		}

		@Override
		public void removeActionListener( final ActionListener l )
		{
			editorPane.removeActionListener( l );
		}

	}

	public class EditorPane extends JPanel
	{

		private static final long serialVersionUID = 1L;

		private final JTextField field;

		public EditorPane()
		{
			field = new JTextField( 10 );
			field.setFont( getFont().deriveFont( Font.BOLD ) );
			setLayout( new GridBagLayout() );
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 0.8;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			add( field, gbc );
		}

		@Override
		public void addNotify()
		{
			super.addNotify();
			field.requestFocusInWindow();
		}

		public void selectAll()
		{
			field.selectAll();
		}

		public void setText( final String text )
		{
			field.setText( text );
		}

		public String getText()
		{
			return field.getText();
		}

		public void addActionListener( final ActionListener listener )
		{
			field.addActionListener( listener );
		}

		public void removeActionListener( final ActionListener listener )
		{
			field.removeActionListener( listener );
		}

	}

	public static interface UpdateListener
	{
		public void tagSetCollectionUpdated();
	}

	public Collection< TagSetFeature< ? > > getTagSets()
	{
		final Collection< TagSetFeature< ? > > tagsets = new ArrayList<>();
		for ( int j = 0; j < model.getSize(); j++ )
			tagsets.add( model.getElementAt( j ) );
		return tagsets;
	}
}
