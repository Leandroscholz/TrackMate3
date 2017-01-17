package org.mastodon.revised.ui.coloring;

import java.util.List;
import java.util.Map;

import org.mastodon.revised.io.yaml.AbstractWorkaroundConstruct;
import org.mastodon.revised.io.yaml.WorkaroundConstructor;
import org.mastodon.revised.io.yaml.WorkaroundRepresent;
import org.mastodon.revised.io.yaml.WorkaroundRepresenter;
import org.mastodon.revised.ui.coloring.ColorMode.EdgeColorMode;
import org.mastodon.revised.ui.coloring.ColorMode.VertexColorMode;
import org.mastodon.revised.ui.util.ColorMap;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class ColorModeIO
{

	private static final String EDGE_COLOR_MODE_KEY = "edgeColorMode";

	private static final String EDGE_COLOR_FEATURE_KEY = "edgeColorFeatureKey";

	private static final String EDGE_COLORMAP_KEY = "edgeColorMap";

	private static final String EDGE_COLOR_RANGE_KEY = "edgeColorRange";

	private static final String VERTEX_COLOR_MODE_KEY = "vertexColorMode";

	private static final String VERTEX_COLOR_FEATURE_KEY = "vertexColorFeatureKey";

	private static final String VERTEX_COLORMAP_KEY = "vertexColorMap";

	private static final String VERTEX_COLOR_RANGE_KEY = "vertexColorRange";

	private static final Tag COLORMAP_TAG = new Tag( "!colormap" );

	private static final Tag COLOREDGEBY_TAG = new Tag( "!edgeColorMode" );

	private static final Tag COLORVERTEXBY_TAG = new Tag( "!vertexColorMode" );

	public static void representData( final Map< String, Object > mapping, final ColorMode colorMode )
	{
		// Color edge strategy.
		mapping.put( EDGE_COLOR_MODE_KEY, colorMode.getEdgeColorMode() );
		mapping.put( EDGE_COLOR_FEATURE_KEY, colorMode.getEdgeFeatureKey() );
		mapping.put( EDGE_COLORMAP_KEY, colorMode.getEdgeColorMap() );
		mapping.put( EDGE_COLOR_RANGE_KEY, new double[] { colorMode.getMinEdgeColorRange(), colorMode.getMaxEdgeColorRange() } );
		// Color vertex strategy.
		mapping.put( VERTEX_COLOR_MODE_KEY, colorMode.getVertexColorMode() );
		mapping.put( VERTEX_COLOR_FEATURE_KEY, colorMode.getVertexFeatureKey() );
		mapping.put( VERTEX_COLORMAP_KEY, colorMode.getVertexColorMap() );
		mapping.put( VERTEX_COLOR_RANGE_KEY, new double[] { colorMode.getMinVertexColorRange(), colorMode.getMaxVertexColorRange() } );
	}

	public static void construct( final Map< Object, Object > mapping, final ColorMode colorMode )
	{
		colorMode.edgeColorMode( ( EdgeColorMode ) mapping.get( EDGE_COLOR_MODE_KEY ), ( String ) mapping.get( EDGE_COLOR_FEATURE_KEY ) );
		colorMode.edgeColorMap( ( ColorMap ) mapping.get( EDGE_COLORMAP_KEY ) );
		@SuppressWarnings( "unchecked" )
		final List< Double > edgeColorRange = ( List< Double > ) mapping.get( EDGE_COLOR_RANGE_KEY );
		if ( null == edgeColorRange )
		{
			colorMode.minEdgeColorRange( 0. );
			colorMode.maxEdgeColorRange( 1. );
		}
		else
		{
			colorMode.minEdgeColorRange( edgeColorRange.get( 0 ) );
			colorMode.maxEdgeColorRange( edgeColorRange.get( 1 ) );
		}

		colorMode.vertexColorMode( ( VertexColorMode ) mapping.get( VERTEX_COLOR_MODE_KEY ), ( String ) mapping.get( VERTEX_COLOR_FEATURE_KEY ) );
		colorMode.vertexColorMap( ( ColorMap ) mapping.get( VERTEX_COLORMAP_KEY ) );
		@SuppressWarnings( "unchecked" )
		final List< Double > vertexColorRange = ( List< Double > ) mapping.get( VERTEX_COLOR_RANGE_KEY );
		if ( null == vertexColorRange )
		{
			colorMode.minVertexColorRange( 0. );
			colorMode.maxVertexColorRange( 1. );
		}
		else
		{
			colorMode.minVertexColorRange( vertexColorRange.get( 0 ) );
			colorMode.maxVertexColorRange( vertexColorRange.get( 1 ) );
		}
	}

	public static class ColorModeRepresenter extends WorkaroundRepresenter
	{
		public ColorModeRepresenter()
		{
			putRepresent( new RepresentColorMap( this ) );
			putRepresent( new RepresentEdgeColorMode( this ) );
			putRepresent( new RepresentVertexColorMode( this ) );
		}
	}

	public static class ColorModeConstructor extends WorkaroundConstructor
	{
		public ColorModeConstructor()
		{
			super( Object.class );
			putConstruct( new ConstructColorMap( this ) );
			putConstruct( new ConstructEdgeColorMode( this ) );
			putConstruct( new ConstructVertexColorMode( this ) );
		}
	}

	private static class RepresentColorMap extends WorkaroundRepresent
	{

		public RepresentColorMap( final WorkaroundRepresenter r )
		{
			super( r, COLORMAP_TAG, ColorMap.class );
		}

		@Override
		public Node representData( final Object data )
		{
			final ColorMap cm = ( ColorMap ) data;
			return new ScalarNode( getTag(), cm.getName(), null, null, null );
		}
	}

	private static class RepresentEdgeColorMode extends WorkaroundRepresent
	{

		public RepresentEdgeColorMode( final WorkaroundRepresenter r )
		{
			super( r, COLOREDGEBY_TAG, EdgeColorMode.class );
		}

		@Override
		public Node representData( final Object data )
		{
			final EdgeColorMode c = ( EdgeColorMode ) data;
			return new ScalarNode( getTag(), c.name(), null, null, null );
		}
	}

	private static class RepresentVertexColorMode extends WorkaroundRepresent
	{

		public RepresentVertexColorMode( final WorkaroundRepresenter r )
		{
			super( r, COLORVERTEXBY_TAG, VertexColorMode.class );
		}

		@Override
		public Node representData( final Object data )
		{
			final VertexColorMode c = ( VertexColorMode ) data;
			return new ScalarNode( getTag(), c.name(), null, null, null );
		}
	}

	private static class ConstructColorMap extends AbstractWorkaroundConstruct
	{

		public ConstructColorMap( final WorkaroundConstructor c )
		{
			super( c, COLORMAP_TAG );
		}

		@Override
		public Object construct( final Node node )
		{
			try
			{
				final String cmName = ( ( ScalarNode ) node ).getValue();
				return ColorMap.getColorMap( cmName );
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}

	}

	private static final class ConstructEdgeColorMode extends AbstractWorkaroundConstruct
	{

		public ConstructEdgeColorMode( final WorkaroundConstructor c )
		{
			super( c, COLOREDGEBY_TAG );
		}

		@Override
		public Object construct( final Node node )
		{
			try
			{
				final String cmName = ( ( ScalarNode ) node ).getValue();
				return EdgeColorMode.valueOf( cmName );
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}

	}

	private static final class ConstructVertexColorMode extends AbstractWorkaroundConstruct
	{

		public ConstructVertexColorMode( final WorkaroundConstructor c )
		{
			super( c, COLORVERTEXBY_TAG );
		}

		@Override
		public Object construct( final Node node )
		{
			try
			{
				final String cmName = ( ( ScalarNode ) node ).getValue();
				return VertexColorMode.valueOf( cmName );
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}

	}

	private ColorModeIO()
	{}

}
