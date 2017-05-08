package org.mastodon.revised.model.mamut;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class LinkPool extends AbstractListenableEdgePool< Link, Spot, ByteMappedElement >
{
	LinkPool( final int initialCapacity, final SpotPool vertexPool )
	{
		super( initialCapacity, AbstractEdgePool.layout, Link.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
	}

	@Override
	protected Link createEmptyRef()
	{
		return new Link( this );
	}

	/**
	 * Adds a link between the specified source and target.
	 * <p>
	 * If a link already exists between this source and target (with this
	 * direction), the link is not added and this method returns
	 * <code>null</code>.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param edge
	 *            a reference object used for operation.
	 * @return the added link, or <code>null</code> if an edge already exists
	 *         between source and target.
	 */
	@Override
	public Link addEdge( final AbstractVertex< ?, ?, ?, ? > source, final AbstractVertex< ?, ?, ?, ? > target, final Link edge )
	{
		if ( getEdge( source, target, edge ) != null )
			return null;

		return super.addEdge( source, target, edge );
	}

	/**
	 * Inserts an edge between the specified source and target, at the specified
	 * positions in the edge lists of the source and target vertices.
	 * <p>
	 * If an edge already exists between this source and target (with this
	 * direction), the edge is not added and this method returns
	 * <code>null</code>.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutInsertAt
	 *            the position the created edge is to be inserted in the source
	 *            vertex outgoing edge list.
	 * @param target
	 *            the target vertex.
	 * @param targetInInsertAt
	 *            the position the created edge is to be inserted in the target
	 *            vertex incoming edge list.
	 * @param edge
	 *            a reference object used for operation.
	 * @return the added edge, or <code>null</code> if an edge already exists
	 *         between source and target.
	 */
	@Override
	public Link insertEdge( final AbstractVertex< ?, ?, ?, ? > source, final int sourceOutInsertAt, final AbstractVertex< ?, ?, ?, ? > target, final int targetInInsertAt, final Link edge )
	{
		if ( getEdge( source, target, edge ) != null )
			return null;

		return super.insertEdge( source, sourceOutInsertAt, target, targetInInsertAt, edge );
	}
}