package org.mastodon.revised.ui.features;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.Set;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.mastodon.revised.model.feature.FeatureComputerService;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.feature.DefaultMamutFeatureComputerService;
import org.scijava.Context;


public class FeatureComputersPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static final ImageIcon COG_ICON = new ImageIcon( FeatureComputersPanel.class.getResource( "cog.png" ) );

	private static final ImageIcon HELP_ICON = new ImageIcon( FeatureComputersPanel.class.getResource( "help.png" ) );

	public FeatureComputersPanel( final FeatureComputerService< Model > computerService )
	{
		setLayout( new BorderLayout( 0, 0 ) );

		final JPanel panelComputation = new JPanel();
		add( panelComputation, BorderLayout.SOUTH );

		final JButton btnCompute = new JButton( "Compute" );

		final JProgressBar progressBar = new JProgressBar();

		final JLabel lblComputationDate = new JLabel( "Last feature computation: Never." );
		final GroupLayout gl_panelComputation = new GroupLayout( panelComputation );
		gl_panelComputation.setHorizontalGroup(
				gl_panelComputation.createParallelGroup( Alignment.LEADING )
						.addGroup( gl_panelComputation.createSequentialGroup()
								.addContainerGap()
								.addGroup( gl_panelComputation.createParallelGroup( Alignment.LEADING )
										.addGroup( gl_panelComputation.createSequentialGroup()
												.addComponent( btnCompute )
												.addPreferredGap( ComponentPlacement.RELATED )
												.addComponent( progressBar, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE ) )
										.addComponent( lblComputationDate, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE ) )
								.addContainerGap() ) );
		gl_panelComputation.setVerticalGroup(
				gl_panelComputation.createParallelGroup( Alignment.LEADING )
						.addGroup( gl_panelComputation.createSequentialGroup()
								.addContainerGap()
								.addGroup( gl_panelComputation.createParallelGroup( Alignment.TRAILING, false )
										.addComponent( progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
										.addComponent( btnCompute, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
								.addPreferredGap( ComponentPlacement.UNRELATED )
								.addComponent( lblComputationDate )
								.addContainerGap( GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
		panelComputation.setLayout( gl_panelComputation );

		final JPanel panelTitle = new JPanel();
		add( panelTitle, BorderLayout.NORTH );

		final JLabel lblTitle = new JLabel( "Features available for computation:" );
		lblTitle.setFont( getFont().deriveFont( Font.BOLD ) );
		final GroupLayout gl_panelTitle = new GroupLayout( panelTitle );
		gl_panelTitle.setHorizontalGroup(
				gl_panelTitle.createParallelGroup( Alignment.LEADING )
						.addGroup( gl_panelTitle.createSequentialGroup()
								.addContainerGap()
								.addComponent( lblTitle, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE )
								.addContainerGap() ) );
		gl_panelTitle.setVerticalGroup(
				gl_panelTitle.createParallelGroup( Alignment.LEADING )
						.addGroup( gl_panelTitle.createSequentialGroup()
								.addGap( 5 )
								.addComponent( lblTitle, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE )
								.addGap( 0, 0, Short.MAX_VALUE ) ) );
		panelTitle.setLayout( gl_panelTitle );

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder( null );
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		add( scrollPane, BorderLayout.CENTER );

		final JPanel panelFeatures = new JPanel();
		panelFeatures.setBorder( null );
		scrollPane.setViewportView( panelFeatures );
		final GridBagLayout gbl = new GridBagLayout();
//		c.columnWidths = new int[] { 0 };
//		c.rowHeights = new int[] { 0 };
//		c.columnWeights = new double[] { Double.MIN_VALUE };
//		c.rowWeights = new double[] { Double.MIN_VALUE };
		panelFeatures.setLayout( gbl );
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets( 0, 5, 0, 5 );
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;

		// Feed the feature panel.
		layoutComputers( panelFeatures, c, "Vertex features:", computerService.getAvailableVertexFeatureComputers() );
		layoutComputers( panelFeatures, c, "Edge features:", computerService.getAvailableEdgeFeatureComputers() );
		layoutComputers( panelFeatures, c, "Branch vertex features:", computerService.getAvailableBranchVertexFeatureComputers() );
		layoutComputers( panelFeatures, c, "Branch edge features:", computerService.getAvailableBranchEdgeFeatureComputers() );
	}

	private void layoutComputers( final JPanel panel, final GridBagConstraints c, final String title, final Set< String > computers )
	{
		if ( computers.isEmpty() )
			return;
		final JLabel lblVertexFeatures = new JLabel( title );
		lblVertexFeatures.setFont( panel.getFont().deriveFont( Font.ITALIC ) );
		c.gridwidth = 3;
		c.gridx = 0;
		panel.add( lblVertexFeatures, c );

		for ( final String computer : computers )
		{
			final boolean selected = false;
			final JCheckBox checkBox = new JCheckBox( computer, selected );
			c.gridy++;
			c.gridx = 0;
			c.weightx = 1.;
			c.gridwidth = 1;
			panel.add( checkBox, c );

			final JButton config = new JButton( COG_ICON );
			config.setBorder( null );
			config.setBorderPainted( false );
			config.setContentAreaFilled( false );
			config.setMargin( new Insets( 0, 0, 0, 0 ) );
			c.gridx++;
			c.weightx = 0.;
			panel.add( config, c );

			final JButton help = new JButton( HELP_ICON );
			help.setBorder( null );
			help.setBorderPainted( false );
			help.setContentAreaFilled( false );
			c.gridx++;
			c.weightx = 0.;
			panel.add( help, c );
		}
		c.gridy++;
		panel.add( Box.createVerticalStrut( 15 ), c );
		c.gridy++;
	}

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		Locale.setDefault( Locale.US );

		final Context context = new org.scijava.Context();
		final DefaultMamutFeatureComputerService featureComputerService = new DefaultMamutFeatureComputerService();
		context.inject( featureComputerService );
		featureComputerService.initialize();

		final JFrame frame = new JFrame( "Test" );
		final FeatureComputersPanel panel = new FeatureComputersPanel( featureComputerService );
		frame.getContentPane().add( panel );
		frame.setSize( 300, 400 );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
