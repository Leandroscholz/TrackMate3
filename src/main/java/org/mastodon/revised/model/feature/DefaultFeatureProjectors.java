package org.mastodon.revised.model.feature;

import org.mastodon.features.DoubleFeature;
import org.mastodon.features.IntFeature;
import org.mastodon.features.WithFeatures;

/**
 * Static utilities to build feature projections for numerical features.
 * 
 * @author Jean-Yves Tinevez
 *
 */
public class DefaultFeatureProjectors
{

	/**
	 * Returns a view of the specified scalar <code>double</code> feature as a
	 * projection.
	 * 
	 * @param feature
	 *            the feature.
	 * @return a feature projection for the specified feature.
	 */
	public static final < O extends WithFeatures< O > > FeatureProjection< O > project( final DoubleFeature< O > feature )
	{
		return new DoubleFeatureProjection< O >( feature );
	}

	/**
	 * Returns a view of the specified scalar <code>int</code> feature as a
	 * projection.
	 * 
	 * @param feature
	 *            the feature.
	 * @return a feature projection for the specified feature.
	 */
	public static final < O extends WithFeatures< O > > FeatureProjection< O > project( final IntFeature< O > feature )
	{
		return new IntFeatureProjection< O >( feature );
	}

	private static final class DoubleFeatureProjection< O extends WithFeatures< O > > implements FeatureProjection< O >
	{

		private final DoubleFeature< O > feature;

		public DoubleFeatureProjection( final DoubleFeature< O > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final O obj )
		{
			return obj.feature( feature ).isSet();
		}

		@Override
		public double value( final O obj )
		{
			return obj.feature( feature ).getDouble();
		}
	}

	private static final class IntFeatureProjection< O extends WithFeatures< O > > implements FeatureProjection< O >
	{

		private final IntFeature< O > feature;

		public IntFeatureProjection( final IntFeature< O > feature )
		{
			this.feature = feature;
		}

		@Override
		public boolean isSet( final O obj )
		{
			return obj.feature( feature ).isSet();
		}

		@Override
		public double value( final O obj )
		{
			return obj.feature( feature ).getInt();
		}

	}

	private DefaultFeatureProjectors()
	{}
}
