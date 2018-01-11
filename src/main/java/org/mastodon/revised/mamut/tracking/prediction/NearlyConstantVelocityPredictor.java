package org.mastodon.revised.mamut.tracking.prediction;

import org.mastodon.collection.RefList;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.graph.Graph;
import org.mastodon.graph.algorithm.AbstractGraphAlgorithm;
import org.mastodon.graph.algorithm.ShortestPath;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.util.Graphs;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.revised.tracking.prediction.Predictor;
import org.mastodon.revised.tracking.prediction.StateAndCovariance;

/**
 * A measurement predictor that relies on the Kalman filter with a motion model
 * set to nearly constant velocity.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <Spot>
 *            the type of vertices in the graph.
 * @param <Link>
 *            the type of edges in the graph.
 */
public class NearlyConstantVelocityPredictor
		extends AbstractGraphAlgorithm< Spot, Link >
		implements Predictor< Spot >
{

	/**
	 * N dims.
	 */
	private static final int n = 3;

	/**
	 * The map that links track heads to their Kalman filter.
	 */
	private final RefObjectMap< Spot, NCVKalmanFilter > kfMap;

	public NearlyConstantVelocityPredictor( final ModelGraph graph )
	{
		super( graph );
		this.kfMap = createVertexObjectMap();
	}

	@Override
	public StateAndCovariance predict( final Spot trackHead )
	{
		// Retrieve or create a KF for this head.
		final NCVKalmanFilter kf = getKalmanFilter( trackHead );
		if ( null == kf )
		{
			// Dummy prediction.
			final double[] X0 = new double[ n ];
			final double[][] cov = new double[ 2 * n ][ 2 * n ];
			// Covariance upper-left quadrant (position covariance).
			trackHead.getCovariance( cov );
			for ( int d = 0; d < n; d++ )
			{
				X0[ d ] = trackHead.getDoublePosition( d );
				X0[ n + d ] = 0.;
				cov[ n + d ][ n + d ] = Double.POSITIVE_INFINITY;
			}
			return new StateAndCovariance( X0, cov );
		}

		return kf.getPredictedState();
	}

	/**
	 * Retrieves or creates a Kalman filter for the specified vertex.
	 *
	 * <ul>
	 * <li>If a KF is registered in this class for the specified vertex, it is
	 * returned.
	 * <li>If not:
	 * <ul>
	 * <li>If the the specified vertex has no incoming edge (track tail) or more
	 * than one incoming edge (track fusion), we cannot derive a KF and return
	 * <code>null</code>.
	 * <li>Otherwise, we iterate backwards in the graph to find a KF for the
	 * track. The track is iterated backward this way until
	 * <ul>
	 * <li>A KF is found for a parent vertex in the track. It is then updated
	 * and propagated up to the specified vertex. The KF is then registered for
	 * the specified vertex instead of its parent.
	 * <li>A KF is not found for the track. A new one is instantiated for the
	 * track, updated and propagated up to the specified vertex. The KF is then
	 * registered for the specified vertex..
	 * </ul>
	 * </ul>
	 * </ul>
	 *
	 * @param vertex
	 *            the vertex to find or create a Kalman filter for.
	 * @return a Kalman filter, or <code>null</code> if the specified vertex is
	 *         a track tail or a track fusion.
	 */
	private NCVKalmanFilter getKalmanFilter( final Spot vertex )
	{
		if ( kfMap.get( vertex ) != null )
			return kfMap.get( vertex );

		if ( vertex.incomingEdges().size() != 1 )
			return null;

		final Link eref = graph.edgeRef();
		final Spot vref1 = graph.vertexRef();
		final Spot vref2 = graph.vertexRef();
		Spot current = assign( vertex, vref2 );
		NCVKalmanFilter kf;

		// Do we have a kf registered?
		while ( ( kf = kfMap.get( current ) ) == null )
		{
			// Walk back.
			final Link edge = current.incomingEdges().get( 0, eref );
			final Spot parent = Graphs.getOppositeVertex( edge, current, vref1 );

			// Did we reach a track tail or a track fusion?
			if ( current.incomingEdges().size() != 1 )
			{
				kf = instantiateKalmanFilter( current, parent );
				current = assign( parent, current );
				break;
			}

			current = assign( parent, current );

			/*
			 * Convergence protection is we have a loop. In time-directed graph
			 * we are not supposed to have loops, but you never now.
			 */
			if ( current.equals( vertex ) )
			{
				graph.releaseRef( eref );
				graph.releaseRef( vref1 );
				graph.releaseRef( vref2 );
				throw new IllegalArgumentException( "Iterated over a track that is a loop." );
			}
		}

		// Deregister from the vertex.
		kfMap.remove( current );

		/*
		 * Propagate and update KF. We now have KF defined for the 'current'
		 * vertex, and we need to update it as we iterate forward in time up to
		 * when we reach the specified vertex. We know that there exists a path
		 * from the current vertex to the specified one, but they might be track
		 * splits on the way. So we use a shortest path algorithm.
		 */

		final ShortestPath< Spot, Link > shortestPath = new ShortestPath<>( ( Graph< Spot, Link > ) graph, SearchDirection.REVERSED );
		// Path is in reversed order so...
		final RefList< Spot > path = shortestPath.findPath( vertex, current );
		/*
		 * Remove the first spot, from which the KF was created or retrieved (we
		 * want to update it with the subsequent spots).
		 */
		path.remove( 0 );

		// Update loop.
		final double[] pos = new double[ n ];
		final double[][] cov = new double[ n ][ n ];
		for ( final Spot v : path )
		{
			v.localize( pos );
			v.getCovariance( cov );
			kf.predict();
			kf.update( pos, cov );
		}

		kfMap.put( vertex, kf );
		graph.releaseRef( eref );
		graph.releaseRef( vref1 );
		graph.releaseRef( vref2 );
		return kf;
	}

	private NCVKalmanFilter instantiateKalmanFilter( final Spot sourceVertex, final Spot targetVertex )
	{
		// Initial state:
		final double[] X0 = new double[ 2 * n ];
		for ( int d = 0; d < n; d++ )
		{
			X0[ d ] = targetVertex.getDoublePosition( d );
			X0[ n + d ] = targetVertex.getDoublePosition( d ) - sourceVertex.getDoublePosition( d );
		}

		final double[][] positionCovariance = new double[ n ][ n ];
		targetVertex.getCovariance( positionCovariance );

		final double initStateCovariance = 1e-1;
		final double positionProcessStd = Math.sqrt( targetVertex.getBoundingSphereRadiusSquared() ) / 100.;
		final double velocityProcessStd = Math.sqrt( targetVertex.getBoundingSphereRadiusSquared() ) / 100.;
		return new NCVKalmanFilter( X0, positionCovariance, initStateCovariance, positionProcessStd, velocityProcessStd );
	}
}
