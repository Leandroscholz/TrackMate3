package org.mastodon.revised.bdv.overlay.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mastodon.revised.bdv.overlay.RenderSettings;
import org.yaml.snakeyaml.Yaml;


/**
 * Manages a list of {@link RenderSettings} for multiple BDV windows.
 * Provides models based on a common list of settings than can be used in swing items.
 *
 * @author Jean-Yves Tinevez.
 *
 */
public class RenderSettingsManager
{
	private static final String STYLE_FILE = System.getProperty( "user.home" ) + "/.mastodon/rendersettings.yaml";

	private final Vector< RenderSettings > rs;

	public RenderSettingsManager()
	{
		this.rs = new Vector<>();
		for ( final RenderSettings r : RenderSettings.defaults )
			rs.add( r );

		loadStyles();
	}

	public Vector< RenderSettings > getRenderSettings()
	{
		return rs;
	}

	public RenderSettings copy( final RenderSettings current )
	{
		final String name = current.getName();
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
			for ( int j = 0; j < rs.size(); j++ )
			{
				if ( rs.get( j ).getName().equals( newName ) )
					continue INCREMENT;
			}
			break;
		}

		final RenderSettings newStyle = current.copy( newName );
		rs.add( newStyle );
		return newStyle;
	}

	private void loadStyles()
	{
		try
		{
			final FileReader input = new FileReader( STYLE_FILE );
			final Yaml yaml = RenderSettingsIO.createYaml();
			final Iterable< Object > objs = yaml.loadAll( input );
			for ( final Object obj : objs )
				rs.add( ( RenderSettings ) obj );

		}
		catch ( final FileNotFoundException e )
		{
			System.out.println( "BDV render settings file " + STYLE_FILE + " not found. Using builtin styles." );
		}
	}

	public void saveStyles()
	{
		try
		{
			final List< RenderSettings > stylesToSave = new ArrayList<>();
			for ( int i = 0; i < rs.size(); i++ )
			{
				final RenderSettings style = rs.get( i );
				if ( RenderSettings.defaults.contains( style ) )
					continue;
				stylesToSave.add( style );
			}

			mkdirs( STYLE_FILE );
			final FileWriter output = new FileWriter( STYLE_FILE );
			final Yaml yaml = RenderSettingsIO.createYaml();
			yaml.dumpAll( stylesToSave.iterator(), output );
			output.close();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}

	/*
	 * STATIC UTILITIES
	 */

	private static boolean mkdirs( final String fileName )
	{
		final File dir = new File( fileName ).getParentFile();
		return dir == null ? false : dir.mkdirs();
	}

}