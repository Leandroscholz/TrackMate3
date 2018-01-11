package org.mastodon.revised.tracking.prediction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StateAndCovariance
{

	/**
	 * State vector.
	 */
	public final double[] state;

	/**
	 * Covariance matrix on state.
	 */
	public final double[][] covariance;

	public StateAndCovariance( final double[] state, final double[][] covariance )
	{
		this.state = state;
		this.covariance = covariance;
	}

	@Override
	public String toString()
	{
		final int d = 1;
		final int w = 5;
		final DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols( new DecimalFormatSymbols( Locale.US ) );
		format.setMinimumIntegerDigits( 1 );
		format.setMaximumFractionDigits( d );
		format.setMinimumFractionDigits( d );
		format.setGroupingUsed( false );

		final StringBuilder str = new StringBuilder( super.toString() );

		str.append( "\nState:\n" );
		for ( int i = 0; i < state.length; i++ )
		{
			final String s = format.format( state[ i ] );
			final int padding = Math.max( 1, w - s.length() );
			for ( int k = 0; k < padding; k++ )
				str.append(  ' ' );
			str.append( s );
			str.append( '\n' );
		}

		str.append( "Covariance:\n" );
		for ( int i = 0; i < covariance.length; i++ )
		{
			for ( int j = 0; j < covariance[0].length; j++ )
			{
				final String s = format.format( covariance[ i ][ j ] );
				final int padding = Math.max( 1, w - s.length() );
				for ( int k = 0; k < padding; k++ )
					str.append(  ' ' );
				str.append( s );
			}
			str.append( '\n' );
		}

		return str.toString();
	}
}
