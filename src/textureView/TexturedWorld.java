/*
*  Author: Conner Partaker
*  Date of Creation: 12 December, 2017
*
*  All programming contained in this file is my work alone.
*
*  
*  Purpose:   In short, this program is a functional 3D modeler and 3D grapher. As a 3D modeler, this program
*           can have input from the user into the program (Line 208, of form "pts.add(new Point(x, y, z));")
*           to add a new point shown in 3D space via a Kn graph between every existing point. This, however, is
*           only the most primitive of functions of this program, to be developed further at a later time (I have
*           MANY somewhat conflicting plans). The main functionality of this program is as an interactive 3D
*           function grapher (specifically for functions of form z = f(x,y)). This program also contains a parser
*           used to take input from the user from within the graphical interface, parse into a real function, and
*           get calculated z coordinates at specific (x,y) coordinates. This is then used to generate an image of
*           the surface of the graph, and display it to the user. Finally, as an added bonus, all 3D models can be
*           made into an image compatible with 3D glasses, a functionality I have not been able to test very well,
*           simply due to my lack of possession of 3D glasses.
*
*
*  Functionality: 
*	Mouse:
*		Moving the mouse:
*			Movement of the mouse changes the perspective from which the user perceives the model, changed
*			on a basis of the x coordinate being a theta, and the y coordinate being a phi, (in spherical
*			coordinates) of the position of the user.
*		Mouse wheel:
*			As the movement of the mouse controls phi and theta of the position of the user in spherical
*			coordinates, the mouse wheel controls the radius of the user from the pseudo-origin (explained
*			in Keyboard)
*	Keyboard:
*		WASD:
*			Changes the location of the origin the spherical coordinates on which the user is placed. This
*			'pseudo-origin' (named so due to the modeled Point relation to the true origin) is marked by
*			a small red octahedron on screen.
*		CTRL + (1, 2, or 3):
*			Changes mode to 3D Modeling, 3D Graphing, and 3D Modeling w/ 3D Glasses, respectively
*		CTRL + SHIFT + (1, 2, or 3):
*			1) Opens dialogue box to enter a new graphed function. Functions should f(x,y), explicitly equal
*			   to z. Characters are limited to 0 to 9, x, y, (, ), ^, *, /, +, -, sin, cos, tan, asin, acos, atan, e, pi, phi, and space.
*			2) Changes the distance along the x and y axes from the pseudo-origin that is rendered in 3D graphing
*			3) Changes the separation of red and blue images in the 3D Modeling w/ 3D Glasses mode, so the user can get the best results.
*		ESC:
*			Exits the program.
*/
package textureView;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Timer;
import java.util.TimerTask;



public class TexturedWorld extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	
	
	
	private static final long serialVersionUID = 1L;					//Because why not
    
    
	//Various functionalities require different ArrayLists
	private ArrayList<PointEntity> entities
	  = new ArrayList<PointEntity>();  								    //Main Point array, containing all 3-space Point
	private Texture text = new Texture();								//Object initializes all textures, and prepares them to be used
	
	//Screen Properties (init in constructor)
	private final Dimension screenDim;									//Screen height, width
	private final int screenX;											// 1/2 of screen width
	private final int screenY;											// 1/2 of screen height
	
	//These make up the user perspective plane
	private Point u = new Point(0,0,0);									//Point of convergence, where all vectors from Point are mapped to (u for user)
	private Point c = new Point(1,0,0);									//Center of perspective plane, the plane representing the screen (c for center)
	private Point ref1 = new Point(0,1,0);								//Perspective plane ref point, making vector c->ref1 a horizontal basis vector of the screen
	private Point ref2 = new Point(0,0,1);								//Perspective plane ref point, making vector c->ref2 a vertical basis vector of the screen
	
	private Point o = new Point(0,0,0);									//Origin Shift vector, to make viewing offset from the origin
	
	//Spherical Coordinates used to keep user at constant radius from entity
	private double theta;												//XY plane positive rotation   (Radians, like a civilized person)
	private double phi;													//Z-Vector angle (a.k.a pitch) (Radians, like a civilized person)
	
	//Booleans holding directional data to prevent issues with the keyPressed method
	private boolean left = false;										//Just read it
	private boolean right = false;										//come on
	private boolean forward = false;									//really?
	private boolean backward = false;									//...
	
	private double speed = 5;											//Speed in units/sec
	
	
	
	//CONSTRUCTION METHODS//////////////////////////////////////////////////////////////////////////////
 	public TexturedWorld() {
		
		//Initialize screen values, including ref1
		screenDim   = Toolkit.getDefaultToolkit().getScreenSize();
		screenX     = screenDim.width /2;
		screenY     = screenDim.height/2;
				
		//Create Panel
		setSize(screenDim.width, screenDim.height);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		createEntities();
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {public void run() {updateFramePos();}}, 0, 16);
	}
	//
	//
	//
	public void createEntities() {
		
		o.x = 0;
		o.y = 0;
		
		entities.add(new PointEntity(new Point( 1,  1,  2, 64,  0), 
									 new Point(-1,  1,  2,  0,  0),
									 new Point(-1, -1,  2,  0, 64), 
									  					text.charz));
		/*
		entities.add(new PointEntity(-1, -1,  2,  0, 64, 
				 					  1,  1,  2, 64,  0,
				 					  1, -1,  2, 64, 64, 
				 					  text.charz));
		*/		
		repaint();
	}
	//
	//
	//
	public void request() {requestFocusInWindow();}
	//END CONSTRUCTION METHODS//////////////////////////////////////////////////////////////////////////
	
	
	
	//LISTENERS/////////////////////////////////////////////////////////////////////////////////////////
	public void mouseMoved   (MouseEvent e) {
		
		theta = (screenX - e.getX()*2)*Math.PI/(double)(screenX);
		phi   = (e.getY()/2 - screenY)*Math.PI/(double)(screenY);
	}
	//
	//
	//
	public void keyPressed   (KeyEvent e)   {
		
		switch(e.getKeyCode()) {
			
			case KeyEvent.VK_W:
				forward  = true;
				break;
			case KeyEvent.VK_A:
				left     = true;
				break;
			case KeyEvent.VK_S:
				backward = true;
				break;
			case KeyEvent.VK_D:
				right    = true;
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			default:
				break;
		}
	}
	//
	//
	//
	public void keyReleased  (KeyEvent e)   {
		
		switch(e.getKeyCode()) {
		
			case KeyEvent.VK_W:
				forward  = false;
				break;
			case KeyEvent.VK_A:
				left     = false;
				break;
			case KeyEvent.VK_S:
				backward = false;
				break;
			case KeyEvent.VK_D:
				right    = false;
				break;
			default:
				break;
		}
	}
	//
	//
	//
	public void mouseDragged (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}
	public void mousePressed (MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited  (MouseEvent e) {}
	public void keyTyped     (KeyEvent   e) {}
	//END LISTENERS/////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//PAINT METHODS/////////////////////////////////////////////////////////////////////////////////////
	public void updateFramePos() {
		
		//Update origin shift vector
		if (forward) {
			o.x += .01*speed*(c.x-u.x);
			o.y += .01*speed*(c.y-u.y);
		}
		if (left) {
			o.x -= .01*speed*(c.y-u.y);
			o.y += .01*speed*(c.x-u.x);
		}
		if (backward) {
			o.x -= .01*speed*(c.x-u.x);
			o.y -= .01*speed*(c.y-u.y);
		}
		if (right) {
			o.x += .01*speed*(c.y-u.y);
			o.y -= .01*speed*(c.x-u.x);
		}
		
		//U is an extension of c from the origin
		u.sphereCoords(0, 0, 0);
				
		//Spherical coordinates with 2 basis bijection from finite box subset of R2
		c.sphereCoords(1, phi, theta);
		
		//ref1 = 50*(-c.y,c.x,0)/(|c.x|+|c.y|)  [c.y is neg to make the cross product with c-c.z positive]
		ref1.pUpdate(-c.y, c.x,0);
		ref1.uvect();
		
		//ref2 is c with an decreased phi of atan(|ref2|/|c|) in this case is pi/6
		ref2.sphereCoords(Math.sqrt(2), phi - Math.PI/4.0, theta);
		ref2.vectSubt(c);
		
		
		if (Point.mag(Point.cross(ref1, c)) - 1> .000000001) {System.out.println("Ping");}
		
		//Offset the perspective by the origin shift vector
		c.vectAdd(o);
		u.vectAdd(o);
		
		repaint();
	}
	//
	//
	//
	public void updateRFromP(Point i) {
		
		Point[] p = new Point[1];
		p[0] = i;
		updateRFromP(p);
	}
	//
	//
	//
	public void updateRFromP(Point[] ptsList) {
		
		Point uc = Point.vectSubt(u, c);
		Point up;
		Point up_xuc;
		double x;
		
		for (Point p : ptsList) {
			
			//The scaling factor of similar triangles with hypotenuses up and upr is x=up.uc/|uc|^2
			//With this, a = (up/x-uc).ref1/|ref1|^2 (b is just with ref2) s.t. upr = uc + aref1 + bref2
			up = Point.vectSubt(u, p);
			x = Math.pow(Point.mag(uc), 2)/Point.dot(up, uc);
			
			//if the point is behind the user, don't bother
			if (x >= 0) {
				
				up_xuc = Point.vectSubt(uc, up.scale(x));
				p.rUpdate(Point.dot(up_xuc, ref1), Point.dot(up_xuc, ref2));
				
			} else {
				p.rUpdate(Double.NaN, Double.NaN);
			}
		}
	}
	//
	//
	//
	public double phi(Point i, Point j, Point a) {
		Point uc = Point.vectSubt(u, c);
		Point ui = Point.vectSubt(u, i);
		Point uj = Point.vectSubt(u, j);
		
		Point x = Point.vectAdd(a, uc).scale(Math.pow(Point.mag(uc), -2));
		Point y = Point.scale(x, Point.dot(uj, uc));
		x.scale(Point.dot(ui, uc));
		
		double ratio = x.x/y.x;
		if (ratio == 1) {ratio = x.y/y.y;}
		if (ratio == 1) {ratio = x.z/y.z;}
		
		
		return (1/(1-ratio));
		
	}
	//
	//
	//
	public boolean triangleLegal(Point i, Point j, Point k) {
		
		if (Math.max(Math.max(i.rx, j.rx), k.rx) > -1 && Math.min(Math.min(i.rx, j.rx), k.rx) < 1)
			if (i.ry > -1 && k.ry < 1)
				return true;
		return false;
	}
	//
	//
	//I just can't take these anymore
	public double pyth(double a, double b) {
		return Math.sqrt(a*a + b*b);
	}
	public double pyth(double a, double b, double c) {
		return Math.sqrt(a*a + b*b + c*c);
	}
	//
	//
	//
	public int updateRFromPhi(Point i, Integer x, int y, Point a, Point b) {
		
		double iy = 1 - y/(double)(screenY);
		double ix;
		if (x == null) {ix = (b.rx-a.rx)*(iy-a.ry)/(b.ry-a.ry) + a.rx;}
		else           {ix = x/(double)(screenX) - 1;}
		i.rUpdate(ix, iy);
		
		double iphi = phi(a, b, new Point(Point.vectAdd(Point.scale(ref1, i.rx), Point.scale(ref2, i.ry))));
		i.pUpdate(Point.vectAdd(a, Point.vectSubt(a, b).scale(iphi)));
		
		i.dx = a.dx + (int)(iphi*(b.dx - a.dx));
		i.dy = a.dy + (int)(iphi*(b.dy - a.dy));
		
		return (int)(screenX*(1+ix));
	}
	//
	//
	//
	//
	//
	//
	public void swap(Point a, Point b) {
		Point x = new Point(a);
		
		a.rUpdate(b);
		a.pUpdate(b);
		a.dx = b.dx;
		a.dy = b.dy;
		
		b.rUpdate(x);
		b.pUpdate(x);
		b.dx = x.dy;
		b.dx = x.dy;
	}
	//
	//
	//
	@Override
	protected void paintComponent(Graphics g) {
		
		BufferedImage b = new BufferedImage((int)screenDim.getWidth(), (int)screenDim.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		//Update the graphical positions of Point unlocked from screen
		for(PointEntity e : entities) {
			
			updateRFromP(e.ptslist);
			
			for(int l = 0; l < e.ptslist.length; l += 3) {
				
				//Every 3 points makes a new triangle
				Point i = e.ptslist[l  ];
				Point j = e.ptslist[l+1];
				Point k = e.ptslist[l+2];
				
				try {
				b.setRGB((int)(screenX + 2*screenY*i.rx), (int)(screenY - 2*screenY*i.ry), 0xFFFFFF);
				b.setRGB((int)(screenX + 2*screenY*j.rx), (int)(screenY - 2*screenY*j.ry), 0xFFFFFF);
				b.setRGB((int)(screenX + 2*screenY*k.rx), (int)(screenY - 2*screenY*k.ry), 0xFFFFFF);
				}catch(Exception ex) {}
				/*
				if (i.ry < k.ry) {swap(i, k);}
				if (i.ry < j.ry) {swap(i, j);}
				if (j.ry < k.ry) {swap(j, k);}
				
				//If the triangle is legally on the screen, we make it
				//We make triangles by starting at the i's y coord, and moving through to k's.
				//At each y, we loop through all x coords in the triangle, and find their associated color on the texture.
				if (triangleLegal(i, j, k)) {
					
					//p1 is the left intersection of the triangle and f(x) = y. p2 is the right. xp is the current x coord looked at
					Point p1 = new Point();
					Point p2 = new Point();
					Point xp = new Point();
					
					//The x coords of p1 and p2. They need to be 
					int p1x;
					int p2x;
					
					for (int y = Math.max((int)(screenY*(1 - i.ry)), 0); y <= Math.min((int)(screenY*(1 - k.ry)), 2*screenY-5); y += 5) {
						
						//If j is left of the line ik, p1 must be on ij, jk. Otherwise it will be on ik (and vice versa for p2)
						if ((k.ry-i.ry)*(j.rx-i.rx) > (k.rx-i.rx)*(j.ry-i.ry)) {
							if (y <= (int)(screenY*(1 - j.ry))) 
									{p1x = updateRFromPhi(p1, null, y, i, j);}
							else	{p1x = updateRFromPhi(p1, null, y, j, k);}
									 p2x = updateRFromPhi(p2, null, y, i, k);
						} else { 
							if (y <= (int)(screenY*(1 - j.ry))) 
									{p2x = updateRFromPhi(p2, null, y, i, j);}
							else 	{p2x = updateRFromPhi(p2, null, y, j, k);}
									 p1x = updateRFromPhi(p1, null, y, i, k);
						}
						
						
						for (int x = Math.max(p1x, 0); x <= Math.min(p2x, 2*screenX-5); x += 5) {
							updateRFromPhi(xp, x, y, p1, p2);
							b.setRGB(x, y, Texture.color(e.img, xp.dx, xp.dy));
						}
					}
				}
				*/
			}
		}
		
		g.drawImage(b, 0, 0, this);
	}
	//
	//
	//
	//END PAINT METHODS/////////////////////////////////////////////////////////////////////////////////
	
	
	
	//MAIN METHOD///////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		JFrame f = new JFrame();
		TexturedWorld panel = new TexturedWorld();
		
		f.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.setUndecorated(true);
		f.add(panel);
		
		f.setVisible(true);
		panel.request();
		panel.repaint();
	}
}



class PointEntity {
	
	Point[] ptslist;
	BufferedImage img;
	
	public PointEntity(Point a, Point b, Point c, BufferedImage img) {
		
		this.ptslist = new Point[] {a, b, c};
		this.img     = img;
	} 
}



class Texture {
	
	private static String userdir = System.getProperty("user.dir");
	BufferedImage charz;
	
	public Texture() {
		try {
		
			charz = ImageIO.read(new File(userdir + "/tmp/charizard.png"));
		
		
		}
		catch (IOException e) {}
	}
	
	public static int color(BufferedImage i, int x, int y) {
		try {return i.getRGB(x, y);}
		catch (Exception e) {return 0xFFFFFF;}
		
		//return 0x00000F*(x+y);
			
	}
}