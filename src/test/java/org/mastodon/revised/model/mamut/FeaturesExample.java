package org.mastodon.revised.model.mamut;

public class FeaturesExample // TODO
{
	/*
	static final int NO_ENTRY_VALUE = -1;

	public static final IntFeature< Spot > TRACKLENGTH = new IntFeature<>( "track length", NO_ENTRY_VALUE );
	public static final ObjFeature< Spot, String > LABEL = new ObjFeature<>( "label2" );

	static
	{
		FeatureSerializers.put( TRACKLENGTH, new IntFeatureSerializer<>() );
		FeatureSerializers.put( LABEL, new StringFeatureSerializer<>() );
	}

	private FeaturesExample() {};

	public static void main( final String[] args )
	{

		final Model model = new Model();
		final Spot ref = model.getGraph().vertexRef();

		final Random random = new Random();
		final double[] pos = new double[ 3 ];
		final double[][] cov = new double[ 3 ][ 3 ];

		for ( int i = 0; i < 1000000; ++i )
		{
			for ( int d = 0; d < 3; ++d )
				pos[ d ] = random.nextDouble();
			final Spot spot = model.getGraph().addVertex( ref ).init( 0, pos, cov );

			spot.feature( LABEL ).set( "the vertex label " + i );
			spot.feature( TRACKLENGTH ).set( 3 );
		}

		model.getGraph().releaseRef( ref );

		while( true )
		{
			try
			{
				final File file = new File( "/Users/pietzsch/Desktop/model_with_features.raw" );

				long t0 = System.currentTimeMillis();
				model.saveRaw( file );
				long t1 = System.currentTimeMillis();
				System.out.println( "saved in " + ( t1 - t0 ) + " ms" );

				final Model loaded = new Model();
				final Spot tmp = loaded.getGraph().vertexRef();
				final Spot s = loaded.getGraph().addVertex( tmp ).init( 0, pos, cov );
				s.feature( LABEL );
				s.feature( TRACKLENGTH );
				t0 = System.currentTimeMillis();
				loaded.loadRaw( file );
				t1 = System.currentTimeMillis();
				System.out.println( "loaded in " + ( t1 - t0 ) + " ms" );

				final Spot next = model.getGraph().vertices().iterator().next();
				System.out.println( next.feature( LABEL ).get() );
				System.out.println( next.feature( TRACKLENGTH ).get() );
				loaded.getGraph().releaseRef( ref );
			}
			catch ( final IOException e )
			{
				e.printStackTrace();
			}

		}
	}
	*/
}
