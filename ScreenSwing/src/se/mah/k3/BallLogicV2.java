package se.mah.k3;

//import java.awt.Graphics;
import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;

public class BallLogicV2 extends Polygon {
	static int speed = 0;
	Ball ball = new Ball();
	Level level = new Level();
//	boolean space = false;
//	boolean upKey = false;
//	boolean downKey = false;
//	boolean leftKey = false;
//	boolean rightKey = false;
//	boolean otherKey = false;
	static Point[] ship = { new Point(0, 0), new Point(10, 10),
			new Point(0, 20), new Point(20, 10) };
	static Point[] boost = { new Point(0, 0), new Point(20, 10),
			new Point(0, 20), new Point(-12, 15), new Point(-10, 12),
			new Point(-20, 10), new Point(-10, 8), new Point(-12, 5) };

	//START ROTATION
	static int rot = 60;
	static Point pos = new Point(390, 290);
	// antigravity purpose
	static boolean prevPos = false;
	static int gravity = 0;
	//thrust image
	Polygon thrust = new Polygon(boost, new Point(pos.x -15,pos.y), rot);

	int normalizeAngle(int angle)
	{
	    int newAngle = angle;

	 // reduce the angle  
	    newAngle =  angle % 360; 

	    // force it to be the positive remainder, so that 0 <= angle < 360  
	    newAngle = (newAngle + 360) % 360;  

	    // force into the minimum absolute value residue class, so that -360 < angle <= 360  
	    if (newAngle > 360)  
	        newAngle -= 360;  
	    
	    return newAngle;
	}

	public BallLogicV2() {
		super(ship, pos, rot);
	}
	
	

	public void reset() {
		// default values
		this.rotation = 0;
		this.position = new Point(390, 290);
		this.thrust = new Polygon(boost, new Point(pos.x -15,pos.y), rot);
	}

	// this method creates array of ships.. this will be the lives
	public static BallLogicV2[] lives(int n) {
		// create empty array for ships
		BallLogicV2[] output = new BallLogicV2[n];
		for (int i = 0; i < n; i++) {
			output[i] = new BallLogicV2();
		}
		return output;
	}

	public void move(int xPos, int yPos) {
		// added to track which direction it is going
		double prevPosX = xPos; 
				//position.x;
		double prevPosY = yPos;
				//position.y;
		System.out.println(" befo= " + rotation);
		rotation = normalizeAngle( (int)rotation);
		System.out.println(" afteh= " + rotation);
		int speed = 1;

			position = new Point(position.x
					+ (speed * Math.cos(Math.toRadians(rotation))), position.y
					+ (speed * Math.sin(Math.toRadians(rotation))));
			thrust.position = position;
							
			
			//WALLS
			//nedanför följer metoder som vi inte riktigt behöver, då ASS-roids gör att
			//skeppet spawnar på motsattsida när den träffar en kant. 
			
			//
			if (position.x > level.screenWidth && prevPosX < position.x) {
				
				position = new Point(800-10, position.y);	
				double x = Math.asin(Math.sin(this.rotation * Math.PI/180.0)) * 180.0/Math.PI;
				System.out.println("before = " + rotation);
				rotate((int)x);
				System.out.println("after = " + rotation);
			
				
			} else if (position.x < level.relX && prevPosX > position.x) {
				position = new Point(0+10, position.y);
				double x = Math.asin(Math.sin(this.rotation * Math.PI/180.0)) * 180.0/Math.PI;
				System.out.println("before = " + rotation);
				rotate((int)x);
				System.out.println("after = " + rotation);
			}
			
			
			if (position.y > level.screenHeight && prevPosY < position.y) {
				position = new Point(position.x, position.y-10);
				
				double x = Math.asin(Math.sin(this.rotation * Math.PI/180.0)) * 180.0/Math.PI;
				System.out.println("before = " + rotation);
				rotateY((int)x);
				System.out.println("after = " + rotation);
			} 
			
			else if (position.y < level.relY && prevPosY > position.y) {
				position = new Point(position.x, position.y+10);
				double x = Math.asin(Math.sin(this.rotation * Math.PI/180.0)) * 180.0/Math.PI;
				System.out.println("before = " + rotation);
				rotateY((int)x);
				System.out.println("after = " + rotation);
			}

	//	}
		/*
		 * if(position.x != prevPos.x){ System.out.println("FALSE"); for(int i =
		 * 5; i > 0; i--){ position = new Point(position.x +
		 * (i*Math.cos(Math.toRadians(rotation))), position.y + (i*
		 * Math.sin(Math.toRadians(rotation)))); } prevPos = position; }
		 */
//		if (leftKey) {
//			rotation = rotation - 5;
//			thrust.rotation = rotation;
//		}
//		if (rightKey) {
//			rotation = rotation + 5;
//			thrust.rotation = rotation;
//		}

	}

}
