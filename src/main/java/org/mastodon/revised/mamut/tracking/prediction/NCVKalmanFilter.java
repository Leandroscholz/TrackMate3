package org.mastodon.revised.mamut.tracking.prediction;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.mastodon.revised.tracking.prediction.StateAndCovariance;

import Jama.Matrix;

/**
 * Kalman filter or nearly-constant velocity motion model.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class NCVKalmanFilter
{

	/**
	 * The evolution matrix, or state transition matrix. In our case, it is the
	 * matrix that links position evolution and velocity through
	 * <code><b>x</b>(k+1) = <b>x</b>(k) + <b>v</b> × dt</code>. We assume
	 * <code><b>v</b></code> is constant and measured in unit of frames, so
	 * <code>dt = 1</code>.
	 */
	private static final Matrix A;

	/**
	 * The measurement matrix.
	 */
	private static final Matrix H;
	static
	{
		// Evolution matrix
		A = Matrix.identity( 6, 6 );
		for ( int i = 0; i < 3; i++ )
			A.set( i, 3 + i, 1 );

		// Measurement matrix. Out measurements are direct.
		H = Matrix.identity( 3, 6 );
	}

	/**
	 * The <i>a posteriori</i> error covariance matrix, measure the accuracy of
	 * the state estimate.
	 */
	private Matrix P;

	/**
	 * Covariance matrix of the process noise. Determine how noisy the process
	 * is.
	 */
	private final Matrix Q;

	/**
	 * Covariance matrix of the observation noise. Determine how noisy our
	 * measurements are.
	 */
	private final Matrix R;

	/** Current state. */
	private Matrix X;

	/** Prediction. */
	private Matrix Xp;

	/**
	 * Number of occlusions (no measurements) that happened so far.
	 */
	private int nOcclusion;

	private boolean predicted = false;

	/**
	 * Initialize a new Kalman filter with the specified initial state.
	 *
	 *
	 * @param X0
	 *            initial state estimate. Must a 6 elements
	 *            <code>double[]</code> array with
	 *            <code>x0, y0, z0, vx0, vy0, vz0</code> with velocity in
	 *            <code>length/frame</code> units.
	 * @param initStateCovariance
	 *            the initial state covariance. Give it a large value if you do
	 *            not trust the initial state estimate (<i>e.g.</i> 100), a
	 *            small value otherwise (<i>e.g.</i>1e-2).
	 * @param positionProcessStd
	 *            the std of the additive white gaussian noise affecting the
	 *            <b>position</b> evolution. Large values means that the
	 *            position undergoes heavy fluctuations.
	 * @param velocityProcessStd
	 *            the std of the additive white gaussian noise affecting the
	 *            <b>velocity</b> evolution. Careful, we expect it to be in
	 *            units of <code>length/frame</code>. Large values means that
	 *            the velocity undergoes heavy fluctuations.
	 * @param positionCovariance
	 *            the covariance affecting the position <b>measurement</b>.
	 */
	public NCVKalmanFilter( final double[] X0, final double[][] positionCovariance, final double initStateCovariance, final double positionProcessStd, final double velocityProcessStd )
	{
		// Initial state
		X = new Matrix( X0, X0.length );

		// State covariance
		P = Matrix.identity( 6, 6 ).times( initStateCovariance );

		// Process covariance
		Q = Matrix.identity( 6, 6 );
		for ( int i = 0; i < 3; i++ )
		{
			Q.set( i, i, positionProcessStd * positionProcessStd );
			Q.set( 3 + i, 3 + i, velocityProcessStd * velocityProcessStd );
		}

		// Observation covariance. We use the spot covariance.
		R = new Matrix( 3, 3 );
		for ( int i = 0; i < 3; i++ )
			for ( int j = 0; j < 3; j++ )
				R.set( i, j, positionCovariance[ i ][ j ] );
	}

	/**
	 * Runs the update step of the Kalman filter based on the specified
	 * measurement.
	 *
	 * @param measurement
	 *            the measured position, must be specified as a 3 elements
	 *            <code>double[]</code>array, containing the measured
	 *            <code>x, y, z</code> position. It can be <code>null</code>;
	 *            the filter then assumes an occlusion occurred, and update its
	 *            state based on solely the prediction step.
	 */
	public void update( final double[] measurement, final double[][] positionCovariance )
	{
		predicted = false;
		if ( null == measurement )
		{
			// Occlusion.
			nOcclusion++;
			X = Xp;
		}
		else
		{
			nOcclusion = 0;
			for ( int i = 0; i < 3; i++ )
				for ( int j = 0; j < 3; j++ )
					R.set( i, j, positionCovariance[ i ][ j ] );
			final Matrix TEMP = H.times( P.times( H.transpose() ) ).plus( R );
			final Matrix K = P.times( H.transpose() ).times( TEMP.inverse() );
			// State
			final Matrix XM = new Matrix( measurement, 3 );
			X = Xp.plus( K.times( XM.minus( H.times( Xp ) ) ) );
			// Covariance
			P = ( Matrix.identity( 6, 6 ).minus( K.times( H ) ) ).times( P );
		}
	}

	public int getNOcclusion()
	{
		return nOcclusion;
	}

	/**
	 * Runs the prediction step of the Kalman filter.
	 */
	public void predict()
	{
		if ( !predicted )
		{
			Xp = A.times( X );
			P = A.times( P.times( A.transpose() ) ).plus( Q );
			predicted = true;
		}
	}

	/**
	 * Returns the predicted state and covariance.
	 *
	 * @return the predicted state and covariance.
	 */
	public StateAndCovariance getPredictedState()
	{
		predict();
		return new StateAndCovariance( Xp.getColumnPackedCopy(), P.getArrayCopy() );
	}

	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder( super.toString() );
		final ByteArrayOutputStream st = new ByteArrayOutputStream();
		final PrintWriter pw = new PrintWriter( st );

		str.append( "\nState:" );
		X.print( pw, 5, 1 );
		pw.flush();
		str.append( st );

		str.append( "Covariance:" );
		st.reset();
		P.print( pw, 5, 1 );
		pw.flush();
		str.append( st );

		return str.toString();
	}

}
