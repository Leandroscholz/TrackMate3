/**
 *
 */
package org.mastodon.revised.bdv.overlay.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;

import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;

/**
 * @author Jean=Yves Tinevez
 *
 */
public class RenderSettingsChooser
{

	private final RenderSettingsPanel panel;


	private final MutableComboBoxModel< RenderSettings > model;

	private final RenderSettingsManager renderSettingsManager;

	public RenderSettingsChooser( final RenderSettingsManager renderSettingsManager,
			final FeatureKeys graphFeatureKeys, final FeatureRangeCalculator graphFeatureRangeCalculator )
	{
		this.renderSettingsManager = renderSettingsManager;
		// Give the choose its own render settings instance.
		this.model = new DefaultComboBoxModel<>( renderSettingsManager.getRenderSettings() );
		panel = new RenderSettingsPanel( model,
				graphFeatureKeys, graphFeatureRangeCalculator );
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
					renderSettingsManager.saveStyles();
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
				final RenderSettings current = ( RenderSettings ) model.getSelectedItem();
				final boolean enabled = !RenderSettings.defaults.contains( current );
				panel.buttonDeleteStyle.setEnabled( enabled );
				panel.buttonSetStyleName.setEnabled( enabled );
			}
		} );
		panel.comboBoxStyles.setSelectedIndex( 0 );

	}

	private void setStyleName()
	{
		final RenderSettings current = ( RenderSettings ) model.getSelectedItem();
		if ( null == current || RenderSettings.defaults.contains( current ) )
			return;

		final String newName = ( String ) JOptionPane.showInputDialog( panel, "Enter the render settings name:", "Style name", JOptionPane.PLAIN_MESSAGE, null, null, current.getName() );
		current.setName( newName );
		panel.repaint();
	}

	private void newStyle()
	{
		RenderSettings current = ( RenderSettings ) model.getSelectedItem();
		if ( null == current )
			current = RenderSettings.defaultStyle();

		final RenderSettings newStyle = renderSettingsManager.copy( current );
		model.setSelectedItem( newStyle );
	}

	private void delete()
	{
		if ( RenderSettings.defaults.contains( model.getSelectedItem() ) )
			return;

		model.removeElement( model.getSelectedItem() );
	}

	public JPanel getPanel()
	{
		return panel;
	}
}