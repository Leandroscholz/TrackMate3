package org.mastodon.revised.tracking.prediction;

import net.imglib2.RealLocalizable;

/**
 * Interface for classes that can make measurement prediction for the motion
 * model they implement.
 * 
 * @author Jean-Yves Tinevez
 *
 */
public interface Predictor< V >
{

	/**
	 * Returns a prediction for the state of the next measurement, as expected
	 * by the motion model implemented.
	 * 
	 * @param trackHead
	 * @return the predicted measurement vector, as a {@link RealLocalizable}. 
	 */
	public RealLocalizable predict( V trackHead );

}
