/**
 *
 */
package org.mastodon.revised.trackscheme.display.style;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.MutableComboBoxModel;

/**
 * @author Jean=Yves Tinevez
 */
public class TrackSchemeStyleChooser
{

	private final TrackSchemeStyleChooserPanel panel;

	private final TrackSchemeStyleManager styleManager;

	private final MutableComboBoxModel< TrackSchemeStyle > model;

	public TrackSchemeStyleChooser( final JFrame owner, final TrackSchemeStyleManager trackschemeStyleManager )
	{

		this.styleManager = trackschemeStyleManager;
		this.model = new DefaultComboBoxModel<>( trackschemeStyleManager.getStyles() );
		if ( model.getSize() > 0 )
			model.setSelectedItem( model.getElementAt( 0 ) );

		this.panel = new TrackSchemeStyleChooserPanel( owner, model );
		panel.buttonDeleteStyle.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				delete();
			}
		} );
		panel.buttonNewStyle.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				newStyle();
			}
		} );
		panel.buttonSetStyleName.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				setStyleName();
			}
		} );
		panel.saveButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				panel.saveButton.setEnabled( false );
				try
				{
					saveStyles();
				}
				finally
				{
					panel.saveButton.setEnabled( true );
				}
			}
		} );
		panel.comboBoxStyles.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final boolean enabled = !TrackSchemeStyle.defaults.contains( model.getSelectedItem() );
				panel.buttonDeleteStyle.setEnabled( enabled );
				panel.buttonSetStyleName.setEnabled( enabled );
				panel.buttonEditStyle.setEnabled( enabled );

			}
		} );
		panel.comboBoxStyles.setSelectedIndex( 0 );
	}

	private void saveStyles()
	{
		styleManager.saveStyles();
	}

	private void setStyleName()
	{
		final TrackSchemeStyle current = ( TrackSchemeStyle ) model.getSelectedItem();
		if ( null == current || TrackSchemeStyle.defaults.contains( current ) )
			return;

		final String newName = ( String ) JOptionPane.showInputDialog(
				panel,
				"Enter the style name:",
				"Style name",
				JOptionPane.PLAIN_MESSAGE, null, null, current.name );
		current.name = newName;
	}

	private void newStyle()
	{
		TrackSchemeStyle current = ( TrackSchemeStyle ) model.getSelectedItem();
		if ( null == current )
			current = TrackSchemeStyle.defaultStyle();

		final TrackSchemeStyle newStyle = styleManager.copy( current );
		model.setSelectedItem( newStyle );
	}

	private void delete()
	{
		if ( TrackSchemeStyle.defaults.contains( model.getSelectedItem() ) )
			return;

		model.removeElement( model.getSelectedItem() );
	}

	public TrackSchemeStyleChooserPanel getPanel()
	{
		return panel;
	}
}
