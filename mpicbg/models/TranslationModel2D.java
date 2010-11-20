package mpicbg.models;

import java.awt.geom.AffineTransform;
import java.util.Collection;

/**
 * 2d-translation {@link AbstractModel} to be applied to points in 2d-space.
 * 
 * @version 0.2b
 */
public class TranslationModel2D extends AbstractAffineModel2D< TranslationModel2D >
{
	static final protected int MIN_NUM_MATCHES = 1;
	
	protected float tx = 0, ty = 0;
	
	@Override
	final public int getMinNumMatches(){ return MIN_NUM_MATCHES; }
	
	@Override
	final public AffineTransform createAffine(){ return new AffineTransform( 1, 0, 0, 1, tx, ty ); }
	
	@Override
	final public AffineTransform createInverseAffine(){ return new AffineTransform( 1, 0, 0, 1, -tx, -ty ); }
	
	@Override
	final public float[] apply( final float[] l )
	{
		assert l.length == 2 : "2d translation transformations can be applied to 2d points only.";
		
		return new float[]{ l[ 0 ] + tx, l[ 1 ] + ty };
	}
	
	@Override
	final public void applyInPlace( final float[] l )
	{
		assert l.length == 2 : "2d translation transformations can be applied to 2d points only.";
		
		l[ 0 ] += tx;
		l[ 1 ] += ty;
	}
	
	@Override
	final public float[] applyInverse( final float[] l )
	{
		assert l.length == 2 : "2d translation transformations can be applied to 2d points only.";
		
		return new float[]{ l[ 0 ] - tx, l[ 1 ] - ty };
	}

	@Override
	final public void applyInverseInPlace( final float[] l )
	{
		assert l.length == 2 : "2d translation transformations can be applied to 2d points only.";
		
		l[ 0 ] -= tx;
		l[ 1 ] -= ty;
	}
	
	@Override
	final public < P extends PointMatch >void fit( final Collection< P > matches ) throws NotEnoughDataPointsException
	{
		if ( matches.size() < MIN_NUM_MATCHES ) throw new NotEnoughDataPointsException( matches.size() + " data points are not enough to estimate a 2d translation model, at least " + MIN_NUM_MATCHES + " data points required." );
		
		// center of mass:
		float pcx = 0, pcy = 0;
		float qcx = 0, qcy = 0;
		
		float ws = 0.0f;
		
		for ( final P m : matches )
		{
			final float[] p = m.getP1().getL(); 
			final float[] q = m.getP2().getW(); 
			
			final float w = m.getWeight();
			ws += w;
			
			pcx += w * p[ 0 ];
			pcy += w * p[ 1 ];
			qcx += w * q[ 0 ];
			qcy += w * q[ 1 ];
		}
		pcx /= ws;
		pcy /= ws;
		qcx /= ws;
		qcy /= ws;

		tx = qcx - pcx;
		ty = qcy - pcy;
	}
	
//	@Override
//	final public void shake( final float amount )
//	{
//		tx += rnd.nextGaussian() * amount;
//		ty += rnd.nextGaussian() * amount;
//	}

	@Override
	public TranslationModel2D copy()
	{
		final TranslationModel2D m = new TranslationModel2D();
		m.tx = tx;
		m.ty = ty;
		m.cost = cost;
		return m;
	}
	
	@Override
	final public void set( final TranslationModel2D m )
	{
		tx = m.tx;
		ty = m.ty;
		cost = m.getCost();
	}

	@Override
	final public void preConcatenate( final TranslationModel2D m )
	{
		tx += m.tx;
		ty += m.ty;
	}
	
	@Override
	final public void concatenate( final TranslationModel2D m )
	{
		tx += m.tx;
		ty += m.ty;
	}
	
	/**
	 * Initialize the model such that the respective affine transform is:
	 * 
	 * 1 0 tx
	 * 0 1 ty
	 * 0 0 1
	 * 
	 * @param tx
	 * @param ty
	 */
	final public void set( final float tx, final float ty )
	{
		this.tx = tx;
		this.ty = ty;
	}
	
	/**
	 * TODO Not yet tested
	 */
	//@Override
	public TranslationModel2D createInverse()
	{
		final TranslationModel2D ict = new TranslationModel2D();
		
		ict.tx = -tx;
		ict.ty = -ty;
		
		ict.cost = cost;
		
		return ict;
	}
	
	@Override
	public void toArray( float[] data )
	{
		data[ 0 ] = 1;
		data[ 1 ] = 0;
		data[ 2 ] = 1;
		data[ 3 ] = 0;
		data[ 4 ] = tx;
		data[ 5 ] = ty;
	}

	@Override
	public void toArray( double[] data )
	{
		data[ 0 ] = 1;
		data[ 1 ] = 0;
		data[ 2 ] = 1;
		data[ 3 ] = 0;
		data[ 4 ] = tx;
		data[ 5 ] = ty;
	}

	@Override
	public void toMatrix( float[][] data )
	{
		data[ 0 ][ 0 ] = 1;
		data[ 0 ][ 1 ] = 0;
		data[ 0 ][ 2 ] = tx;
		data[ 1 ][ 0 ] = 0;
		data[ 1 ][ 1 ] = 1;
		data[ 1 ][ 1 ] = ty;
	}

	@Override
	public void toMatrix( double[][] data )
	{
		data[ 0 ][ 0 ] = 1;
		data[ 0 ][ 1 ] = 0;
		data[ 0 ][ 2 ] = tx;
		data[ 1 ][ 0 ] = 0;
		data[ 1 ][ 1 ] = 1;
		data[ 1 ][ 1 ] = ty;
	}
}
