package textureView;

//Point class - A record of the 3D location of any point, and it's position on the screen
public class Point {
	
	//Real Coords
	public double x;
	public double y;
	public double z;
	
	//Graphics Coords in form pr = rx(ref1) + ry(ref2)
	public double rx;
	public double ry;
	
	
	public int dx;
	public int dy;
	
	
	//Constructors
	public Point(double x0, double y0, double z0) {
		
		x = x0;
		y = y0;
		z = z0;
	}
	//
	//
	//
	public Point(double x0, double y0, double z0, int dx, int dy) {
		
		x = x0;
		y = y0;
		z = z0;
		
		this.dx = dx;
		this.dy = dy;
	}
	//
	//
	//
	public Point(Point p) {
		
		pUpdate(p);
		rUpdate(p);
		this.dx = p.dx;
		this.dy = p.dy;
	}
	//
	//
	//
	public Point() {}
	
	
	
	public Point clone() {
		return new Point(this);
	}
	
	
	//Updaters
	public void pUpdate(double x0, double y0, double z0) {
		x = x0;
		y = y0;
		z = z0;
	}
	public void pUpdate(Point p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}
	//
	//
	//
	public void rUpdate(double x0, double y0) {
		rx = x0;
		ry = y0;
	}
	public void rUpdate(Point p) {
		rx = p.rx;
		ry = p.ry;
	}
	
	
	
	//Set the point with spherical coordinates
	public void sphereCoords(double r, double phi, double theta) {
		x = r*Math.sin(phi)*Math.cos(theta);
		y = r*Math.sin(phi)*Math.sin(theta);
		z = r*Math.cos(phi);
	}
	
	
	
	//Vector scaling
	public Point scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	//
	//
	//
	public static Point scale(Point p, double scalar) {
		Point  x = new Point(p);
		return x.scale(scalar);
	}
	//
	//
	//
	public static Point uvect(Point p) {
		double u = mag(p);
		return scale(p, 1/u);
	}
	//
	//
	//
	public Point uvect() {
		return this.scale(1/mag(this));
	}
	
	
	
	//Vector Addition and Subtraction
	public Point vectAdd(Point i) {
		x+=i.x;
		y+=i.y;
		z+=i.z;
		return this;
	}
	//
	//
	//
	public Point vectSubt(Point i) {
		x-=i.x;
		y-=i.y;
		z-=i.z;
		return this;
	}
	//
	//
	//
	public static Point vectAdd(Point i, Point j) {
		return new Point(i.x + j.x, i.y + j.y, i.z + j.z);
	}
	//
	//
	//
	public static Point vectSubt(Point j, Point i) {
		return new Point(i.x - j.x, i.y - j.y, i.z - j.z);
	}
	
	
	
	//Magnitude Finder
	public static double mag(Point i) {
		return Math.sqrt(Math.pow(i.x,2) + Math.pow(i.y,2) + Math.pow(i.z,2));
	}
	
	
	
	//Dot Products (only of screen vectors)
	public static double dot(Point i, Point j) {
		return (i.x)*(j.x) + (i.y)*(j.y) + (i.z)*(j.z);
	}

	
	
	//Cross product
	public static Point cross(Point i, Point j) {
		return new Point(i.y*j.z - i.z*j.y, i.x*j.z - i.z*j.x, i.x*j.y - i.y*j.x);
	}
}