/**
 * @author Travis Pavelka
**/
public class Tuple {
	double x;
	public double getX() {
		return this.x;
	}
	public void setX(double x) {
		this.x = x;
	}
	
	double y;
	public double getY() {
		return this.y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "Tuple["+x+", "+y+"]";
	}
	
	public Tuple() { this(0, 0); }
	public Tuple(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
