package worldView;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;



public class ThreeDPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener {
	
	
	
	private static final long serialVersionUID = 1L;							//Because why not
	
	//Universal
	public int MODE;															//View mode (MODEL, 3D, FUNCTION)
	private double P = 1;                 										//Perspective constant - 1/3 of How far away you are
	
	//MODE constants, used in the panel constructor
	public static final int FUNCTION = 1;										//Models z=f(x,y) on domain max(|x|,|y|)<=SCALE
	public static final int MODEL    = 2;										//Displays points in 3D space and connects K-graph style
	public static final int THREE_D  = 3;										//Same as MODEL, but creates 2 objects, red and blue, separated
	
	//FUNCTION only
	public int SCALE = 10;	 			 										//Boundaries in x-y plane
	private int SIZE = 2*5*SCALE + 1;											//Number of values taken at one x or y coord, exists for simplicities sake
	public int DENSITY = 5;														//Number of values taken in an interval of 1
	private boolean AXES = true;												//Turn on and off Axes
	
	public Parser parser = new Parser("1/y + 1/x");								//Function to be evaluated, changed by user

    //THREE_D only
    public double shiftSep = 1;													//Separation distance between the centers of the red and blue copies
    
    
    //COOL LOOKING FUNCTIONS MAN (For presenting)
    //20-Math.abs(x+y)-Math.abs(y-x)
  	//-1/Math.sqrt(x*x+y*y)
  	//Math.sin(x) + Math.sin(y)
    
    
	//Various functionalities require different ArrayLists
	private ArrayList<Points> pts = new ArrayList<Points>();					//Main points array, containing all 3-space points
	private ArrayList<Points> ptsShift = new ArrayList<Points>();				//Array for converting to 3D (3D glasses nescessary)
	private ArrayList<Points> userBlock = new ArrayList<Points>();				//Block that the user pivots around (toggleable)
	private ArrayList<Points> ptsAxis = new ArrayList<Points>();				//Separate array for the coordinate axes in FUNCTION mode
																				//X is red, Y is green, Z is blue, as the custom goes
	
	//Screen Properties (init in constructor)
	private final Dimension screenDim;											//Screen height, width
	private final double screenX;												// 1/2 of screen width
	private final double screenY;												// 1/2 of screen height
	private final double SCREENRATIO;											//Ratio of screen width to screen height
	
	//These make up the user perspective plane
	private Points u = new Points(3*P,0,0);										//Point of convergence, where all vectors from points are mapped to (u for user)
	private Points c = new Points(2*P,0,0);										//Center of perspective plane, the plane representing the screen (c for center)
	private Points ref1;														//Perspective plane ref point, making vector c->ref1 a horizontal basis vector of the screen
	private Points ref2 = new Points(2*P,0,P);									//Perspective plane ref point, making vector c->ref2 a vertical basis vector of the screen
	
	private Points o = new Points(0,0,0);										//Origin Shift vector, to make viewing offset from the origin
	
	//Spherical Coordinates used to keep user at constant radius from entity
	private double theta;														//XY plane positive rotation   (Radians, like a civilized person)
	private double phi;															//Z-Vector angle (a.k.a pitch) (Radians, like a civilized person)
	
	
	
	//CONSTRUCTION METHODS//////////////////////////////////////////////////////////////////////////////
	public ThreeDPanel(int MODE) {
		
		//Transfer mode into memory
		this.MODE = MODE;
		
		//Initialize screen values, including ref1
		screenDim   = Toolkit.getDefaultToolkit().getScreenSize();
		screenX     = screenDim.getWidth ()/2;
		screenY     = screenDim.getHeight()/2;
		SCREENRATIO = screenDim.getWidth ()/screenDim.getHeight();
		
		ref1 = new Points(2*P,P*SCREENRATIO,0);
				
		//Create Panel
		setSize((int)screenDim.getWidth(), (int)screenDim.getHeight());
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		
		//Create Menu
		add(new MenuBar(this));
		
		createPoints();
	}
	//
	//
	//
	public void createPoints() {
		
		pts.clear();
		ptsAxis.clear();
		ptsShift.clear();
		
		o.x = 0;
		o.y = 0;
		
		userBlock.add(new Points(  0,  0,  0));
		userBlock.add(new Points( .1,  0,  0));
		userBlock.add(new Points(-.1,  0,  0));
		userBlock.add(new Points(  0, .1,  0));
		userBlock.add(new Points(  0,-.1,  0));
		userBlock.add(new Points(  0,  0, .1));
		userBlock.add(new Points(  0,  0,-.1));
		
		//Add points in 3D Space to view
		if (MODE == MODEL || MODE == THREE_D) {
			
			pts.add(new Points(-5,-5,-5));
			pts.add(new Points(-5,-5, 5));
			pts.add(new Points(-5, 5,-5));
			pts.add(new Points(-5, 5, 5));
			pts.add(new Points( 5,-5,-5));
			pts.add(new Points( 5,-5, 5));
			pts.add(new Points( 5, 5,-5));
			pts.add(new Points( 5, 5, 5));
		
			/*
			pts.add(new Points(-5,-5,  0));
			pts.add(new Points(-5, 0,  0));
			pts.add(new Points(-5, 5,  0));
			pts.add(new Points( 0,-5,  0));
			pts.add(new Points( 0, 0, 10));
			pts.add(new Points( 0, 5,  0));
			pts.add(new Points( 5,-5,  0));
			pts.add(new Points( 5, 0,  0));
			pts.add(new Points( 5, 5,  0));
			*/
			/*
			pts.add(new Points(   0,                 0, 5*Math.sqrt(3)));
			pts.add(new Points(   5,                 0,              0));
			pts.add(new Points(-2.5,  2.5*Math.sqrt(3),              0));
			pts.add(new Points(-2.5, -2.5*Math.sqrt(3),              0));
			*/
		}
		
		//Shift 3D if necessary
		if (MODE == 3D)
			for (Points i : pts)
				ptsShift.add(new Points(i.x + shiftSep, i.y, i.z));
				
				
		//Otherwise, fill pts with points on graph
		if (MODE == FUNCTION) {
			for (int i = -SCALE*5; i <= SCALE*5; i++)
				for (int j = -SCALE*5; j<= SCALE*5; j++)
					pts.add(new Points(i/5.0, j/5.0, parser.evaluate(i/5.0,j/5.0)));
			
			ptsAxis.add(new Points( SCALE,     0,     0));
			ptsAxis.add(new Points(-SCALE,     0,     0));
			ptsAxis.add(new Points(     0, SCALE,     0));
			ptsAxis.add(new Points(     0,-SCALE,     0));
			ptsAxis.add(new Points(     0,     0, SCALE));
			ptsAxis.add(new Points(     0,     0,-SCALE));
		}
					
		repaint();
	}
	//
	//
	//
	public void request() {requestFocusInWindow();}
	//END CONSTRUCTION METHODS//////////////////////////////////////////////////////////////////////////
	
	
	
	//LISTENERS/////////////////////////////////////////////////////////////////////////////////////////
	public void mouseMoved(MouseEvent e) {
		
		theta = (screenX - e.getX()*2 )*Math.PI/screenX;
		phi   = (screenY - e.getY()/2 )*Math.PI/screenY;
		
		updateFramePos();
	}
	//
	//
	//
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		P = Math.max(.1, P + e.getWheelRotation()/10.0);
			
		updateFramePos();
	}
	//
	//
	//
	public void keyPressed(KeyEvent e)   {
		
		switch(e.getKeyCode()) {
			
			case KeyEvent.VK_W:
				o.x += .1*(c.x-u.x);
				o.y += .1*(c.y-u.y);
				break;
			case KeyEvent.VK_A:
				o.x -= .1*(c.y-u.y);
				o.y += .1*(c.x-u.x);
				break;
			case KeyEvent.VK_S:
				o.x -= .1*(c.x-u.x);
				o.y -= .1*(c.y-u.y);
				break;
			case KeyEvent.VK_D:
				o.x += .1*(c.y-u.y);
				o.y -= .1*(c.x-u.x);
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			default:
				break;
		}
		
		if (MODE == FUNCTION) {
			pts.clear();
			for (int i = (int) (-SCALE*5); i <= SCALE*5; i++)
				for (int j = (int) (-SCALE*5); j<= SCALE*5; j++)
					pts.add(new Points(i/5.0 + o.x, j/5.0 + o.y, parser.evaluate(i/5.0 + o.x,j/5.0 + o.y)));
		}
		
		updateFramePos();
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
	public void keyReleased  (KeyEvent e)   {}
	public void keyTyped     (KeyEvent e)   {}
	//END LISTENERS/////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//PAINT METHODS/////////////////////////////////////////////////////////////////////////////////////
	public void updateFramePos() {
		
		//Spherical coordinates with 2 basis bijection from finite box subset of R2
		c.sphereCoords(2*P, phi, theta);
		
		//U is an extension of c from the origin
		u.sphereCoords(3*P, phi, theta);
		
		//ref2 is c with an decreased phi of atan(|ref2|/|c|) in this case is pi/6
		ref2.sphereCoords(P*Math.sqrt(5), phi - Math.PI/6, theta);
		
		//ref1 = c + 50*(-c.y,c.x,0)/(|c.x|+|c.y|)  [c.y is neg to make the cross product with c-c.z positive]
		ref1.pUpdate(-c.y, c.x,0);
		ref1.scale( .5*SCREENRATIO/Math.sin(phi) );
		ref1.vectAdd(c);
		
		//Offset the perspective by the origin shift vector
		   c.vectAdd(o);
		   u.vectAdd(o);
		ref1.vectAdd(o);
		ref2.vectAdd(o);
		
		repaint();
	}
	//
	//
	//
	public void updatePoints(ArrayList<Points> ptsList, boolean screenlocked) {
		
		
		Points uc = Points.vectSubt(u, c);
		double duc = Points.mag(uc);
		double pixelXConv = screenX/Math.pow(Points.mag(Points.vectSubt(c, ref1)),2);
		double pixelYConv = screenY/Math.pow(Points.mag(Points.vectSubt(c, ref2)),2);
		
		Points up;
		double upuc;
		
		for (Points i : ptsList) {
			
			//r = |uc|^2*up/up.uc - uc
			up = Points.vectSubt(u, i);
			if (screenlocked) up.vectAdd(o);
			upuc = Points.dot(up, uc);
		
			
			if (upuc >= 0) {
				up.scale(Math.pow(duc,2)/upuc);
				up.vectSubt(uc);
				i.rUpdate(up.x, up.y, up.z);
			}
			else {
				i.rUpdate(Double.NaN, Double.NaN, Double.NaN);
			}
			
			//Convert to java coords
			i.gx = (int)( screenX + pixelXConv*i.dotNoR1(Points.vectSubt(c, ref1)) );
			i.gy = (int)( screenY - pixelYConv*i.dotNoR1(Points.vectSubt(c, ref2)) );
		}
	}
	//
	//
	//
	private boolean lineLegal(Points x, Points y) {
		return !(x.gx == 0 && x.gy == 0) && !(y.gx == 0 && y.gy == 0);
	}
	//
	//
	//
	@Override
	protected void paintComponent(Graphics g) {
		
		//Black Background, White lines (Changed in 3D)
		g.setColor(Color.BLACK);
		g.fillRect(0,0, (int)screenDim.getWidth(), (int)screenDim.getHeight());
		
		
		//Update the graphical position of the origin block and draw
		g.setColor(Color.RED);
		updatePoints(userBlock, true);
		for (Points i : userBlock)
			for (Points j : userBlock)
				if (lineLegal(i, j))
					g.drawLine(i.gx, i.gy, j.gx, j.gy);
		
		
		g.setColor(Color.WHITE);
		
		//Draw lines between every modeled point
		if (MODE == MODEL) {
			
			//Update the graphical positions of points unlocked from screen
			updatePoints(pts, false);
			
			for (Points i : pts)
				for (Points j : pts)
					if (lineLegal(i, j))
						g.drawLine(i.gx, i.gy, j.gx, j.gy);
		}
		
		
		//Draw parameterization graph surface
		if (MODE == FUNCTION) {
			
			//Update the graphical positions of points locked on screen
			updatePoints(pts, false);
			
			//SIZE is points per row/column (2*5*SCALE + 1)
			//position i in pts is at x-y coord (floor(i/SIZE), i%SIZE)
			SIZE = 2*5*SCALE + 1;
			for (int i = 0; i < pts.size() - 1; i++)
				if ( pts.get(i).gx == pts.get(i+1).gx && lineLegal(pts.get(i), pts.get(i+1)))
					g.drawLine(pts.get(i).gx, pts.get(i).gy, pts.get(i+1).gx, pts.get(i+1).gy);
		
			for (int i = 0; i < pts.size() - SIZE; i++)
				if ( lineLegal(pts.get(i), pts.get(i + SIZE)) )
					g.drawLine(pts.get(i).gx, pts.get(i).gy, pts.get(i + SIZE).gx, pts.get(i + SIZE).gy);
			
			
			if (AXES){
				
				updatePoints(ptsAxis, false);
				
				g.setColor(Color.RED);
				if (lineLegal(ptsAxis.get(0), ptsAxis.get(1)))
					g.drawLine(ptsAxis.get(0).gx, ptsAxis.get(0).gy, ptsAxis.get(1).gx, ptsAxis.get(1).gy);
				
				g.drawString("+x", ptsAxis.get(0).gx, ptsAxis.get(0).gy);
				
				g.setColor(Color.GREEN);
				if (lineLegal(ptsAxis.get(2), ptsAxis.get(3)))
					g.drawLine(ptsAxis.get(2).gx, ptsAxis.get(2).gy, ptsAxis.get(3).gx, ptsAxis.get(3).gy);
				
				g.drawString("+y", ptsAxis.get(2).gx, ptsAxis.get(2).gy);
				
				g.setColor(Color.BLUE);
				if (lineLegal(ptsAxis.get(4), ptsAxis.get(5)))
					g.drawLine(ptsAxis.get(4).gx, ptsAxis.get(4).gy, ptsAxis.get(5).gx, ptsAxis.get(5).gy);
				
				g.drawString("+z", ptsAxis.get(4).gx, ptsAxis.get(4).gy);
			}
		}
		
		
		//Draw model image in blue, and red image shifted (shiftSep) units (automatically creates depth)
		if (MODE == THREE_D) {
			
			//Update the graphical positions of points of both arrays unlocked from screen
			updatePoints(pts, false);
			updatePoints(ptsShift, false);
			
			g.setColor(Color.BLUE);
			for (Points i : pts)
				for (Points j : pts)
					if (lineLegal(i, j))
						g.drawLine(i.gx, i.gy, j.gx, j.gy);
					
			g.setColor(Color.RED);
			for (Points i : ptsShift)
				for (Points j : ptsShift)
					if (lineLegal(i, j))
						g.drawLine(i.gx, i.gy, j.gx, j.gy);
		}
	}
	//END PAINT METHODS/////////////////////////////////////////////////////////////////////////////////
	
	
	
	//MAIN METHOD///////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		JFrame f = new JFrame();
		ThreeDPanel panel = new ThreeDPanel(ThreeDPanel.MODEL);
		
		f.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		f.setUndecorated(true);
		f.add(panel);
		
		f.setVisible(true);
		panel.request();
		panel.repaint();
	}
}



@SuppressWarnings("serial")
class MenuBar extends JMenuBar {
	
	
	
	public MenuBar(ThreeDPanel p) {
		
		JMenu modeMenu = new JMenu("Mode");
		
		modeMenu.add(modeMenuItem("3D Modeling", ThreeDPanel.MODEL   , p, KeyEvent.VK_1));
		modeMenu.add(modeMenuItem("3D Graphing", ThreeDPanel.FUNCTION, p, KeyEvent.VK_2));
		modeMenu.add(modeMenuItem("Real 3D"    , ThreeDPanel.THREE_D , p, KeyEvent.VK_3));
		
		add(modeMenu);
		
		
		
		JMenu configMenu = new JMenu("Config");
		JMenuItem menuItem;
		
		
		menuItem = new JMenuItem("Function Input");
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					p.parser = new Parser((String) JOptionPane.showInputDialog(p, "Enter New Function", "Config Function", JOptionPane.PLAIN_MESSAGE, null, null, ""));
					p.createPoints();
				} catch (Throwable e1) {}
			}
		});
		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK));
		configMenu.add(menuItem);
		
		
		
		menuItem = new JMenuItem("Function Scale");
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					p.SCALE = Integer.parseInt((String) JOptionPane.showInputDialog(p, "Enter New Scale", "Config Function Render", JOptionPane.PLAIN_MESSAGE, null, null, ""));
					p.createPoints();
				} catch (Throwable e1) {}
			}
		});
		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK));
		configMenu.add(menuItem);
		
		add(configMenu);
		
		
		
		menuItem = new JMenuItem("3D Separation");
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					p.shiftSep = Double.parseDouble((String) JOptionPane.showInputDialog(p, "Enter New Separation", "Config 3D", JOptionPane.PLAIN_MESSAGE, null, null, ""));
					p.createPoints();
				} catch (Throwable e1) {}
			}
		});
		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK));
		configMenu.add(menuItem);
		
		add(configMenu);
	}
	//
	//
	//
	private JMenuItem modeMenuItem(String name, int constant, ThreeDPanel p, int accelerator) {
		
		JMenuItem menuItem = new JMenuItem(name);
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				p.MODE = constant; 
				p.createPoints();
			}
		});
		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_MASK));
		return menuItem;
	}
}
