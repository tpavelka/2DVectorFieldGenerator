import java.awt.Point;

/**
 * Has a direction and a magnitude. (degrees)
 * @author Travis Pavelka
 *
 */
public class Vector {
	public static final int COMPONENTS = 1;
	public static final int DIR_MAG = 2;
	
	private boolean isRNG;
	public boolean isRNG() {
		return this.isRNG;
	}
	public void setRNG(boolean b) {
		this.isRNG = b;
	}
	
	private double direction;
	public double getDirection() {
		return this.direction;
	}
	public void setDirection(double dir) {
		this.direction = dir;
	}
	
	private double magnitude;
	public double getMagnitude() {
		return this.magnitude;
	}
	public void setMagnitude(double mag) {
		this.magnitude = mag;
	}
	
	private Tuple components;
	public double getX() {
		return this.components.getX();
	}
	public  void setX(double x) {
		this.components.setX(x);
	}
	public double getY() {
		return this.components.getY();
	}
	public void setY(double y) {
		this.components.setY(y);
	}
	
	public void calcComps() {
		double x = this.magnitude * Math.cos(this.direction);
		double y = this.magnitude * Math.sin(this.direction);
		this.components.setX(x);
		this.components.setY(y);
	}
	public void calcDirMag() {
		// get req info
		Point p1 = new Point(0, 0);
		double x = this.getX();
		double y = this.getY();
		if(x == 0 && y == 0) {
			this.direction = 0;
			this.magnitude = 0;
		} else {
			// calc direction
			double dir = toDegreesSTD(Math.atan(x/y));
			this.setDirection(dir);
			// calc magnitude
			double mag = p1.distance(x, y);
			this.setMagnitude(mag);
		}
	}
	
	/**
	 * Adds vector v to this vector
	 */
	public void addVector(Vector v) {
		double x = this.getX() + v.getX();
		double y = this.getY() + v.getY();
		this.components.setX(x);
		this.components.setY(y);
	}
	
	/**
	 * Returns radians as a double degree value between 0-359.999
	 */
	public static double toDegreesSTD(double radians) {
		double degrees = (radians * 180) % 360;
		if(degrees < 0) {
			degrees = 360 + degrees;
		}
		return degrees;
	}
	
	@Override
	public Vector clone() {
		double direction = this.getDirection();
		double magnitude = this.getMagnitude();
		
		Vector copy = new Vector(direction, magnitude, DIR_MAG);
		copy.setRNG(this.isRNG);
		
		return copy;
	}
	
	/**
	 * @param arg1 xcomp or direction
	 * @param arg2 ycomp or magnitude
	 * @param mode Vector.COMPONENTS or Vector.DIR_MAG
	 */
	public Vector(double arg1, double arg2, int mode) {
		if(mode == COMPONENTS) {
			this.components = new Tuple(0, 0);
			this.components.setX(arg1);
			this.components.setY(arg2);
		} else if(mode == DIR_MAG) {
			this.components = new Tuple(0, 0);
			this.direction = arg1;
			this.magnitude = arg2;
		}
	}
}
