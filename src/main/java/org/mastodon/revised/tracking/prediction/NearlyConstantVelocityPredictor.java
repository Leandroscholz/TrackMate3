package org.mastodon.revised.tracking.prediction;

import org.mastodon.collection.RefList;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.AbstractGraphAlgorithm;
import org.mastodon.graph.algorithm.ShortestPath;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.util.Graphs;

import net.imglib2.RealLocalizable;

/**
 * A measurement predictor that relies on the Kalman filter with a motion model
 * set to nearly constant velocity.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of vertices in the graph.
 * @param <E>
 *            the type of edges in the graph.
 */
public class NearlyConstantVelocityPredictor< V extends Vertex< E > & RealLocalizable, E extends Edge< V > >
		extends AbstractGraphAlgorithm< V, E >
		implements Predictor< V >
{

	/**
	 * The map that links track heads to their Kalman filter.
	 */
	private final RefObjectMap< V, NCVKalmanFilter > kfMap;

	public NearlyConstantVelocityPredictor( ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		this.kfMap = createVertexObjectMap();
	}

	@Override
	public RealLocalizable predict( V trackHead )
	{

		/*
		 * Is there an existing Kalman filter for the specified vertex?
		 */
		NCVKalmanFilter kalmanFilter = kfMap.get( trackHead );
		if ( null == kalmanFilter )
			;

		// TODO Auto-generated method stub
		return null;
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
	private NCVKalmanFilter getKalmanFilter( V vertex )
	{
		if ( kfMap.get( vertex ) != null )
			return kfMap.get( vertex );

		if ( vertex.incomingEdges().size() != 1 )
			return null;

		final E eref = graph.edgeRef();
		final V vref1 = graph.vertexRef();
		final V vref2 = graph.vertexRef();
		V current = assign( vertex, vref2 );
		NCVKalmanFilter kf;

		// Do we have a kf registered?
		while ( ( kf = kfMap.get( current ) ) == null )
		{
			// Walk back.
			final E edge = vertex.incomingEdges().get( 0, eref );
			final V parent = Graphs.getOppositeVertex( edge, current, vref1 );

			// Did we reach a track tail or a track fusion?
			if ( current.incomingEdges().size() != 1 )
			{
				kf = instantiateKalmanFilter( parent, current );
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
		
		// FIXME: Fix stupid mistake in ShortestPath and have it accept ReadOnlyGraph.
		ShortestPath< V, E > shortestPath = new ShortestPath<V, E>( (Graph<V, E>) graph, SearchDirection.REVERSED);
		// Path is in reversed order so...
		RefList< V > path = shortestPath.findPath( vertex, current );
		for ( V v : path )
			kf.update( v );
		
		kfMap.put( vertex, kf );
		graph.releaseRef( eref );
		graph.releaseRef( vref1 );
		graph.releaseRef( vref2 );
		return kf;
	}

	private NCVKalmanFilter instantiateKalmanFilter( V sourceVertex, V targetVertex )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
