package net.trackmate.kdtree;

import java.util.Random;

import net.imglib2.RealLocalizable;
import net.imglib2.util.BenchmarkHelper;
import net.trackmate.graph.PoolObjectList;
import net.trackmate.graph.mempool.ByteMappedElement;
import net.trackmate.graph.mempool.DoubleMappedElement;
import net.trackmate.kdtree.RealLocalizableVertices.MyVertex;
import net.trackmate.kdtree.RealLocalizableVertices.MyVertexPool;

public class KDTreeBenchmark
{
	private final int numDataVertices;

	private final int numTestVertices;

	private final double minCoordinateValue;

	private final double maxCoordinateValue;

	private final MyVertexPool vertexPool;

	private final PoolObjectList< MyVertex, ByteMappedElement > dataVertices;

	private final PoolObjectList< MyVertex, ByteMappedElement > testVertices;

	public KDTreeBenchmark(final int numDataVertices, final int numTestVertices, final double minCoordinateValue, final double maxCoordinateValue)
	{
		this.numDataVertices = numDataVertices;
		this.numTestVertices = numTestVertices;
		this.minCoordinateValue = minCoordinateValue;
		this.maxCoordinateValue = maxCoordinateValue;
		vertexPool = new MyVertexPool( numDataVertices + numTestVertices );
		dataVertices = new PoolObjectList< MyVertex, ByteMappedElement >( vertexPool, numDataVertices );
		testVertices = new PoolObjectList< MyVertex, ByteMappedElement >( vertexPool, numTestVertices );
		createDataVertices();
	}

	private void createDataVertices()
	{
		final MyVertex vertex = vertexPool.createRef();
		final int n = vertex.numDimensions();
		final double[] p = new double[ n ];
		final double size = ( maxCoordinateValue - minCoordinateValue );
		final Random rnd = new Random( 4379 );
		for ( int i = 0; i < numDataVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * size + minCoordinateValue;
			vertexPool.create( vertex );
			vertex.setPosition( p );
			dataVertices.add( vertex );
		}
		for ( int i = 0; i < numTestVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * 2 * size + minCoordinateValue - size / 2;
			vertexPool.create( vertex );
			vertex.setPosition( p );
			testVertices.add( vertex );
		}
		vertexPool.releaseRef( vertex );
	}

	private KDTree< MyVertex, DoubleMappedElement > kdtree;

	public void createKDTree()
	{
		kdtree = KDTree.kdtree( dataVertices, vertexPool );
	}

	public void nearestNeighborSearch( final int numRuns )
	{
		final NearestNeighborSearchOnKDTree< MyVertex, DoubleMappedElement > kd = new NearestNeighborSearchOnKDTree< MyVertex, DoubleMappedElement >( kdtree );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	private net.imglib2.KDTree< MyVertex > kdtreeImgLib2;

	public void createKDTreeImgLib2()
	{
		kdtreeImgLib2 = new net.imglib2.KDTree< MyVertex >( dataVertices, dataVertices );
	}

	public void nearestNeighborSearchImgLib2( final int numRuns )
	{
		final net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree< MyVertex > kd = new net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree< MyVertex >( kdtreeImgLib2 );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	public static void main( final String[] args )
	{
		final KDTreeBenchmark b = new KDTreeBenchmark( 10000, 1000, -5, 5 );
		final boolean printIndividualTimes = true;

		System.out.println( "createKDTree()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.createKDTree();
			}
		} );

		System.out.println( "nearestNeighborSearch()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.nearestNeighborSearch( 10 );
			}
		} );

		System.out.println( "createKDTreeImgLib2()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.createKDTreeImgLib2();
			}
		} );

		System.out.println( "nearestNeighborSearchImgLib2()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.nearestNeighborSearchImgLib2( 10 );
			}
		} );
	}
}