package org.mastodon.revised.model.feature;

import java.awt.Color;

import org.mastodon.revised.model.tagset.Tag;

/**
 * Interface for tag-set projections.
 * <p>
 * Tag-set projections are views of a tag-set feature for object type that do
 * not implement the <code>WithFeatures</code> interface.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <K>
 *            the tag-set target (vertex, edge, ...)
 */
public interface TagSetProjection< K >
{
	/**
	 * Returns whether a tag is set for the specified object.
	 * 
	 * @param obj
	 *            the object.
	 * @return <code>true</code> if a tag is present for the specified object,
	 *         <code>false</code> otherwise.
	 */
	public boolean isSet( K obj );

	/**
	 * Returns the tag for the specified object.
	 * 
	 * @param obj
	 *            the object.
	 * @return the tag.
	 */
	public Tag get( K obj );

	/**
	 * Returns the color to use to signify a tag is not defined for an object.
	 * 
	 * @return a color.
	 */
	public Color getMissingColor();
}
