package org.mastodon.revised.mamut.tracking;

import java.util.Random;

import org.mastodon.revised.mamut.tracking.prediction.NearlyConstantVelocityPredictor;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.tracking.prediction.StateAndCovariance;

public class PredictionExample
{

	public static void main( final String[] args )
	{
		final Model model = new Model();
		final ModelGraph graph = model.getGraph();
		final double radius = 1.;
		final double[] pos = new double[ 3 ];
		final double[] v = new double[] { 2., 1., 0. };
		Spot source = null;
		final Link eref = graph.edgeRef();
		final int maxT = 5;
		final Random ran = new Random( 0l );
		for ( int t = 0; t < maxT; t++ )
		{
			for ( int d = 0; d < pos.length; d++ )
				pos[ d ] = v[ d ] * t + ran.nextGaussian() * 0.1;
			final Spot target = graph.addVertex().init( t, pos, radius );
			target.setLabel( String.valueOf( ( char ) ( 'A' + t ) ) );

			if ( null == source )
				source = target;
			else
			{
				graph.addEdge( source, target, eref ).init();
				source = target;
			}
		}
		graph.releaseRef( eref );

		System.out.println( "Graph generated:\n" + Utils.dump( model ) );

		final NearlyConstantVelocityPredictor predictor = new NearlyConstantVelocityPredictor( graph );
		final StateAndCovariance prediction = predictor.predict( source );
		System.out.println( "Predicted position at time " + maxT + ":\n" + prediction );

	}

}
