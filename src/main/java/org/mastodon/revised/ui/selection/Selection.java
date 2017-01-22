package org.mastodon.revised.ui.selection;

import java.util.Collection;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.Vertex;

/**
 * A class that manages a selection of vertices and edges of a graph.
 * <p>
 * Created instances register themselves as a {@link GraphListener} to always
 * return consistent results. For instance, if a vertex marked as selected in
 * this class is later removed from the graph, the
 * {@link #getSelectedVertices()} method will not return it.
 *
 * @author Tobias Pietzsch
 *
 * @param <V>
 *            the type of the vertices.
 * @param <E>
 *            the type of the edges.
 */
// TODO: rename to SelectionModel?
public interface Selection< V extends Vertex< E >, E extends Edge< V > >
{
	/**
	 * Get the selected state of a vertex.
	 *
	 * @param vertex
	 *            a vertex.
	 * @return {@code true} if specified vertex is selected.
	 */
	public boolean isSelected( final V vertex );

	/**
	 * Get the selected state of an edge.
	 *
	 * @param edge
	 *            an edge.
	 * @return {@code true} if specified edge is selected.
	 */
	public boolean isSelected( final E edge );

	/**
	 * Sets the selected state of a vertex.
	 *
	 * @param vertex
	 *            a vertex.
	 * @param selected
	 *            selected state to set for specified vertex.
	 */
	public void setSelected( final V vertex, final boolean selected );

	/**
	 * Sets the selected state of an edge.
	 *
	 * @param edge
	 *            an edge.
	 * @param selected
	 *            selected state to set for specified edge.
	 */
	public void setSelected( final E edge, final boolean selected );

	/**
	 * Toggles the selected state of a vertex.
	 *
	 * @param vertex
	 *            a vertex.
	 */
	public void toggle( final V vertex );

	/**
	 * Toggles the selected state of an edge.
	 *
	 * @param edge
	 *            an edge.
	 */
	public void toggle( final E edge );

	/**
	 * Sets the selected state of a collection of edges.
	 *
	 * @param edges
	 *            the edge collection.
	 * @param selected
	 *            selected state to set for specified edge collection.
	 * @return {@code true} if the selection was changed by this call.
	 */
	public boolean setEdgesSelected( final Collection< E > edges, final boolean selected );

	/**
	 * Sets the selected state of a collection of vertices.
	 *
	 * @param vertices
	 *            the vertex collection.
	 * @param selected
	 *            selected state to set for specified vertex collection.
	 * @return {@code true} if the selection was changed by this call.
	 */
	public boolean setVerticesSelected( final Collection< V > vertices, final boolean selected );

	/**
	 * Clears this selection.
	 *
	 * @return {@code true} if this selection was not empty prior to
	 *         calling this method.
	 */
	public boolean clearSelection();

	/**
	 * Get the selected edges.
	 *
	 * @return a <b>new</b> {@link RefSet} containing the selected edges.
	 */
	public RefSet< E > getSelectedEdges();

	/**
	 * Get the selected vertices.
	 *
	 * @return a <b>new</b> {@link RefSet} containing the selected vertices.
	 */
	public RefSet< V > getSelectedVertices();

	/**
	 * Adds a listener that will be notified when the selection is changed via
	 * this instance.
	 *
	 * @param listener
	 *            the listener to add.
	 * @return <code>true</code> if the listener was not already registered and
	 *         was successfully added.
	 */
	public boolean addSelectionListener( final SelectionListener listener );

	/**
	 * Removes the specified listener from this instance.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return <code>true</code> if the listener was registered in this instance
	 *         and was successfully removed.
	 */
	public boolean removeSelectionListener( final SelectionListener listener );

	/**
	 * Pauses emitting events to listeners. If a selection change happens after
	 * this method has been called, the listeners will not be notified.
	 *
	 * @see #resumeListeners()
	 */
	public void pauseListeners();

	/**
	 * Resumes emitting events to listener, after it has been paused. If a
	 * selection change event happened while the emitting of events was paused,
	 * an event is fired as this method is called.
	 */
	public void resumeListeners();

}
