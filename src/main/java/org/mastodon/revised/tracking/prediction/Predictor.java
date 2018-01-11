package org.mastodon.revised.tracking.prediction;

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
	 *            the track head to run prediction on.
	 * @return the predicted state and covariance.
	 */
	public StateAndCovariance predict( V trackHead );

}
