package algorithms;


/**
 * A 2-element vector that is represented by double-precision floating
 * point x,y coordinates.
 *
 */
public class Vector2d {

	double x, y;
	// Combatible with 1.1
	static final long serialVersionUID = 8572646365302599857L;

	/**
	 * Constructs and initializes a Vector2d from the specified xy coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector2d(MyPoint p)
	{
		this.x = p.getX();
		this.y = p.getY();
	}

	/**
	 * Computes the dot product of the this vector and vector v1.
	 * @param v1 the other vector
	 */
	public final double dot(Vector2d v1)
	{
		return (this.x*v1.x + this.y*v1.y);
	}


	/**
	 * Returns the length of this vector.
	 * @return the length of this vector
	 */
	public final double length()
	{
		return (double) Math.sqrt(this.x*this.x + this.y*this.y);
	}

	/**
	 * Returns the squared length of this vector.
	 * @return the squared length of this vector
	 */
	public final double lengthSquared()
	{
		return (this.x*this.x + this.y*this.y);
	}

	/**
	 * Sets the value of this vector to the normalization of vector v1.
	 * @param v1 the un-normalized vector
	 */
	public final void normalize(Vector2d v1)
	{
		double norm;

		norm = (double) (1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y));
		this.x = v1.x*norm;
		this.y = v1.y*norm;
	}

	/**
	 * Normalizes this vector in place.
	 */
	public final void normalize()
	{
		double norm;

		norm = (double)
				(1.0/Math.sqrt(this.x*this.x + this.y*this.y));
		this.x *= norm;
		this.y *= norm;
	}


	/**
	 *   Returns the angle in radians between this vector and the vector
	 *   parameter; the return value is constrained to the range [0,PI].
	 *   @param v1    the other vector
	 *   @return   the angle in radians in the range [0,PI]
	 */
	public final double angle(Vector2d v1)
	{
		double vDot = this.dot(v1) / ( this.length()*v1.length() );
		if( vDot < -1.0) 
			vDot = -1.0;
		if( vDot >  1.0) 
			vDot =  1.0;
		return((double) (Math.acos( vDot )));

	}


}
