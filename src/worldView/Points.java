package worldView;



public class Points {
	
	//Real Coords
	public double x;
	public double y;
	public double z;
	
	//Graphics Coords
	public double rx;
	public double ry;
	public double rz;
	
	//Java Coords
	public int gx = 0;
	public int gy = 0;
	
	
	
	//Constructor
	public Points(double x0, double y0, double z0) {
		
		x = x0;
		y = y0;
		z = z0;
	}
	
	
	
	//Updaters
	public void pUpdate(double x0, double y0, double z0) {
		x = x0;
		y = y0;
		z = z0;
	}
	//
	//
	//
	public void rUpdate(double x0, double y0, double z0) {
		rx = x0;
		ry = y0;
		rz = z0;
	}
	//
	//
	//
	public void gUpdate(int x0, int y0) {
		gx = x0;
		gy = y0;
	}
	
	
	
	//Set the point with spherical coordinates
	public void sphereCoords(double r, double phi, double theta) {
		x = r*Math.sin(phi)*Math.cos(theta);
		y = r*Math.sin(phi)*Math.sin(theta);
		z = r*Math.cos(phi);
	}
	
	
	
	//Vector scaling
	public void scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}
	
	
	
	//Vector Addition and Subtraction
	public void vectAdd(Points i) {
		x+=i.x;
		y+=i.y;
		z+=i.z;
	}
	//
	//
	//
	public void vectSubt(Points i) {
		x-=i.x;
		y-=i.y;
		z-=i.z;
	}
	//
	//
	//
	public static Points vectAdd(Points i, Points j) {
		return new Points(i.x + j.x, i.y + j.y, i.z + j.z);
	}
	//
	//
	//
	public static Points vectSubt(Points j, Points i) {
		return new Points(i.x - j.x, i.y - j.y, i.z - j.z);
	}
	
	
	
	//Magnitude Finder
	public static double mag(Points i) {
		return Math.sqrt(Math.pow(i.x,2) + Math.pow(i.y,2) + Math.pow(i.z,2));
	}
	
	
	
	//Dot Products (only of screen vectors)
	public static double dotInR(Points i, Points j) {
		return j.rx*i.rx + j.ry*i.ry + j.rz*i.rz;
	}
	//
	//
	//
	public double dotNoR1(Points i) {
		return rx*(i.x) + ry*(i.y) + rz*(i.z);
	}
	//
	//
	//
	public static double dot(Points i, Points j) {
		return (i.x)*(j.x) + (i.y)*(j.y) + (i.z)*(j.z);
	}

	
	
	//cross product
	public static Points cross(Points i, Points j) {
		return new Points(i.y*j.z - i.z*j.y, i.x*j.z - i.z*j.x, i.x*j.y - i.y*j.x);
	}
}
