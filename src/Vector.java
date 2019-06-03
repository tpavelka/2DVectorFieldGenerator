import java.awt.Point;

/**
 * Has a direction and a magnitude. (degrees)
 * @author Travis Pavelka
 *
 */
public class Vector {
	public static final int COMPONENTS = 1;
	public static final int DIR_MAG = 2;
	
	/**
	 * Adds two vectors and returns the result
	 */
	public static Vector addVectors(Vector v1, Vector v2) {
		double x = v1.getX() + v2.getX();
		double y = v1.getY() + v2.getY();
		return new Vector(x, y, Vector.COMPONENTS);
	}
	
	/**
	 * Inverts the direction of a vector by 180 degrees and returns the result.
	 */
	public static Vector invert(Vector v) {
		double dir = v.getDirection();
		double mag = v.getMagnitude();
		if(v.getDirection() == -1) {
			return new Vector(dir, mag, Vector.DIR_MAG);
		} else {
			dir += 180;
			dir %= 360;
			return new Vector(dir, mag, Vector.DIR_MAG);
		}
	}
	
	/**
	 * Inverts the direction by 180 degrees and returns the result.
	 */
	public static double invert(double dir) {
		double invert = dir;
		invert += 180;
		invert %= 360;
		return invert;
	}
	
	public static Vector scale(Vector v, double scale) {
		double dir = v.getDirection();
		double mag = v.getMagnitude();
		if(v.getDirection() == -1) {
			return new Vector(dir, mag, Vector.DIR_MAG);
		} else {
			mag *= scale;
			return new Vector(dir, mag, Vector.DIR_MAG);
		}
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
	
	public void scale(double scale) {
		this.magnitude *= scale;
	}
	
	public void calcComps() {
		if(this.direction == -1) {
			this.components.setX(0);
			this.components.setY(0);
		} else {
			double x = this.magnitude * Math.cos(this.direction);
			double y = this.magnitude * Math.sin(this.direction);
			this.components.setX(x);
			this.components.setY(y);
		}
	}
	public void calcDirMag() {
		// get req info
		double x = this.getX();
		double y = this.getY();
		if(x == 0 && y == 0) {
			this.direction = -1;
			this.magnitude = 0;
		} else {
			// calc direction
			double dir = toDegreesSTD(Math.atan(x/y));
			this.setDirection(dir);
			// calc magnitude
			Point p1 = new Point(0, 0);
			double mag = p1.distance(x, y);
			this.setMagnitude(mag);
		}
	}
	
	public Vector(Vector copy) {
		this.isRNG = copy.isRNG();
		this.magnitude = copy.getMagnitude();
		this.direction = copy.getDirection();
		this.components = new Tuple();
		this.components.x = copy.getX();
		this.components.y = copy.getY();
	}
	/**
	 * @param arg1 xcomp or direction
	 * @param arg2 ycomp or magnitude
	 * @param mode Vector.COMPONENTS or Vector.DIR_MAG
	 */
	public Vector(double arg1, double arg2, int mode) {
		if(mode == COMPONENTS) {
			this.components = new Tuple();
			this.components.setX(arg1);
			this.components.setY(arg2);
		} else if(mode == DIR_MAG) {
			this.components = new Tuple();
			this.setDirection(arg1);
			this.setMagnitude(arg2);
		}
	}
}
