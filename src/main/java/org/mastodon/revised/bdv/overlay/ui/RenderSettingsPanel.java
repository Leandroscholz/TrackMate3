package org.mastodon.revised.bdv.overlay.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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

import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.mastodon.revised.bdv.overlay.RenderSettings.UpdateListener;
import org.mastodon.revised.model.feature.FeatureKeys;
import org.mastodon.revised.model.feature.FeatureRangeCalculator;

/**
 * An editor and manager for BDV RenderSettings.
 *
 * @author Jean-Yves Tinevez
 */
class RenderSettingsPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	JButton buttonDeleteStyle;

	JButton buttonNewStyle;

	JButton buttonSetStyleName;

	JButton okButton;

	JButton saveButton;

	JComboBox< RenderSettings > comboBoxStyles;

	public RenderSettingsPanel( final MutableComboBoxModel< RenderSettings > model,
			final FeatureKeys graphFeatureKeys, final FeatureRangeCalculator graphFeatureRangeCalculator,
			final FeatureKeys branchGraphFeatureKeys, final FeatureRangeCalculator branchGraphFeatureRangeCalculator )
	{

		final JPanel contentPanel = new JPanel();
		contentPanel.setBorder( null );
		final JPanel panelChooseStyle = new JPanel();
		final JLabel jlabelTitle = new JLabel();
		final RenderSettings targetSettings = RenderSettings.defaultStyle().copy( "target" );
		final RenderSettingsEditorPanel renderSettingsPanel = new RenderSettingsEditorPanel( targetSettings,
				graphFeatureKeys, graphFeatureRangeCalculator,
				branchGraphFeatureKeys, branchGraphFeatureRangeCalculator );

		this.comboBoxStyles = new JComboBox<>( model );
		// Update common render settings when a settings is chosen in the menu.
		comboBoxStyles.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				final RenderSettings rs = comboBoxStyles.getItemAt( comboBoxStyles.getSelectedIndex() );
				enableComponents( renderSettingsPanel, !RenderSettings.defaults.contains( rs ) );
				// Copy settings selected in the list to the one edited in the
				// editor.
				targetSettings.set( rs );
			}
		} );
		if ( model.getSize() > 0 )
			comboBoxStyles.setSelectedIndex( 0 );

		// Update menu settings when the common render settings is changed.
		targetSettings.addUpdateListener( new UpdateListener()
		{
			@Override
			public void renderSettingsChanged()
			{
				// Copy edited settings to the one in the list, used elsewhere.
				comboBoxStyles.getItemAt( comboBoxStyles.getSelectedIndex() ).set( targetSettings );
			}
		} );

		final JPanel panelStyleButtons = new JPanel();
		buttonDeleteStyle = new JButton();
		final JPanel hSpacer1 = new JPanel( null );
		buttonNewStyle = new JButton();
		buttonSetStyleName = new JButton();
		final JPanel buttonBar = new JPanel();
		okButton = new JButton();
		saveButton = new JButton();

		// ======== this ========
		setLayout( new BorderLayout() );

		// ======== dialogPane ========
		{
			setBorder( new EmptyBorder( 12, 12, 12, 12 ) );
			setLayout( new BorderLayout() );

			// ======== contentPanel ========
			{
				contentPanel.setLayout( new BorderLayout() );

				// ======== panelChooseStyle ========
				{
					panelChooseStyle.setLayout( new GridLayout( 3, 0, 0, 10 ) );

					// ---- jlabelTitle ----
					jlabelTitle.setText( "BDV render settings." );
					jlabelTitle.setHorizontalAlignment( SwingConstants.CENTER );
					jlabelTitle.setFont( getFont().deriveFont( Font.BOLD ) );
					panelChooseStyle.add( jlabelTitle );

					// Combo box panel
					final JPanel comboBoxPanel = new JPanel();
					{
						final BorderLayout layout = new BorderLayout();
						comboBoxPanel.setLayout( layout );
						comboBoxPanel.add( new JLabel( "Render settings: " ), BorderLayout.WEST );
						comboBoxPanel.add( comboBoxStyles, BorderLayout.CENTER );
					}

					panelChooseStyle.add( comboBoxPanel );

					// ======== panelStyleButtons ========
					{
						panelStyleButtons.setLayout( new BoxLayout( panelStyleButtons, BoxLayout.LINE_AXIS ) );

						// ---- buttonDeleteStyle ----
						buttonDeleteStyle.setText( "Delete" );
						panelStyleButtons.add( buttonDeleteStyle );
						panelStyleButtons.add( hSpacer1 );

						// ---- buttonNewStyle ----
						buttonNewStyle.setText( "New" );
						panelStyleButtons.add( buttonNewStyle );

						// ---- buttonSetStyleName ----
						buttonSetStyleName.setText( "Set name" );
						panelStyleButtons.add( buttonSetStyleName );

					}
					panelChooseStyle.add( panelStyleButtons );
				}
				contentPanel.add( panelChooseStyle, BorderLayout.NORTH );
				contentPanel.add( renderSettingsPanel, BorderLayout.CENTER );


			}
			add( contentPanel, BorderLayout.CENTER );

			// ======== buttonBar ========
			{
				buttonBar.setBorder( new EmptyBorder( 12, 0, 0, 0 ) );
				buttonBar.setLayout( new GridBagLayout() );
				( ( GridBagLayout ) buttonBar.getLayout() ).columnWidths = new int[] { 80, 164, 80 };
				( ( GridBagLayout ) buttonBar.getLayout() ).columnWeights = new double[] { 0.0, 1.0, 0.0 };

				// ---- okButton ---- don't show it for now.
//				okButton.setText( "OK" );
//				buttonBar.add( okButton, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0,
//						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
//						new Insets( 0, 0, 0, 0 ), 0, 0 ) );

				// ---- saveButton -----
				saveButton.setText( "Save styles" );
				buttonBar.add( saveButton, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets( 0, 0, 0, 0 ), 0, 0 ) );
			}
			add( buttonBar, BorderLayout.SOUTH );
		}
	}

	private static final void enableComponents( final Container container, final boolean enable )
	{
		final Component[] components = container.getComponents();
		for ( final Component component : components )
		{
			component.setEnabled( enable );
			if ( component instanceof Container )
			{
				enableComponents( ( Container ) component, enable );
			}
		}
	}

}
