package org.mastodon.revised.trackscheme.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.SciJavaPlugin;

public class AbstractProvider< K extends SciJavaPlugin >
{

	@Parameter
	private PluginService pluginService;

	@Parameter
	private LogService log;

	@Parameter
	private Context context;

	private List< String > names;

	private List< String > visibleNames;

	private List< String > disabled;

	private Map< String, PluginInfo< K > > infoMap;

	private final Class< K > cl;

	protected AbstractProvider( final Class< K > cl )
	{
		this.cl = cl;
	}

	private void registerModules()
	{
		final List< PluginInfo< K > > infos = pluginService.getPluginsOfType( cl );

		final Comparator< PluginInfo< K > > priorityComparator = new Comparator< PluginInfo< K > >()
		{
			@Override
			public int compare( final PluginInfo< K > o1, final PluginInfo< K > o2 )
			{
				return o1.compareTo( o2 );
			}
		};

		Collections.sort( infos, priorityComparator );

		names = new ArrayList< String >( infos.size() );
		visibleNames = new ArrayList< String >( infos.size() );
		disabled = new ArrayList< String >( infos.size() );
		infoMap = new HashMap<>();

		for ( final PluginInfo< K > info : infos )
		{
			if ( !info.isEnabled() )
			{
				disabled.add( info.getClassName() );
				continue;
			}

			// Manage.
			final String key = info.getName();
			infoMap.put( key, info );
			names.add( key );
			if ( info.isVisible() )
				visibleNames.add( key );

		}
	}

	public K create( final String name )
	{
		final PluginInfo< K > info = infoMap.get( name );
		if ( null == info )
			return null;

		return pluginService.createInstance( info );
	}

	public List< String > getKeys()
	{
		if ( null == names )
			registerModules();
		return Collections.unmodifiableList( names );
	}

	public List< String > getVisibleKeys()
	{
		if ( null == visibleNames )
			registerModules();
		return Collections.unmodifiableList( visibleNames );
	}

	public List< String > getDisabled()
	{
		if ( null == disabled )
			registerModules();
		return Collections.unmodifiableList( disabled );
	}

	public String echo()
	{
		final StringBuilder str = new StringBuilder();
		str.append( "Discovered implementations of " + cl.getSimpleName() + ":\n" );
		str.append( "  Enabled & visible:" );
		if ( getVisibleKeys().isEmpty() )
		{
			str.append( " none.\n" );
		}
		else
		{
			str.append( '\n' );
			for ( final String key : getVisibleKeys() )
				str.append( "  - " + key + "\t-->\t" + infoMap.get( key ).getLabel() + '\n' );
		}
		str.append( "  Enabled & not visible:" );

		final List< String > invisibleKeys = new ArrayList<>( names );
		invisibleKeys.removeAll( visibleNames );
		if ( invisibleKeys.isEmpty() )
		{
			str.append( " none.\n" );
		}
		else
		{
			str.append( '\n' );
			for ( final String key : invisibleKeys )
				str.append( "  - " + key + "\t-->\t" + infoMap.get( key ).getLabel() + '\n' );
		}
		str.append( "  Disabled:" );
		if ( getDisabled().isEmpty() )
		{
			str.append( " none.\n" );
		}
		else
		{
			str.append( '\n' );
			for ( final String cn : getDisabled() )
				str.append( "  - " + cn + '\n' );
		}
		return str.toString();
	}
}