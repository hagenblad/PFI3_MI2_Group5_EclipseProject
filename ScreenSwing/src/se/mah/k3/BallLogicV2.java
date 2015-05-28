package se.mah.k3;

//import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;
//import java.awt.event.KeyListener;

public class BallLogicV2 extends Polygon {
	static int speed = 0;
	Ball ball = new Ball();
	Level level = new Level();

	
	
	static Point[] ship = { new Point(0, 0), new Point(10, 10),
			new Point(0, 20), new Point(20, 10) };
	static Point[] boost = { new Point(0, 0), new Point(20, 10),
			new Point(0, 20), new Point(-12, 15), new Point(-10, 12),
			new Point(-20, 10), new Point(-10, 8), new Point(-12, 5) };

	//START ROTATION
	static int rot = 330;
	static Point pos = new Point(390, 290);
	// antigravity purpose
	static boolean prevPos = false;
	static int gravity = 0;
	//thrust image
	
	//for getters
	int xPos;
	int yPos;
	
	Polygon thrust = new Polygon(boost, new Point(pos.x -15,pos.y), rot);
	
	
	//BOOLEANS FOR CORNERS
	boolean tlcHit =false;
	boolean trcHit =false;
	boolean blcHit =false;
	boolean brcHit =false;
	
	//PLAYAH LIVES
	public int player1lives = 5;
	public int player2lives = 5;
	public int player3lives = 5;
	public int player4lives = 5;
	
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

	public void move() {
		// added to track which direction it is going
		double prevPosX = position.x;
		double prevPosY = position.y;

	//	System.out.println(" befo= " + rotation);
		rotation = normalizeAngle( (int)rotation);
	//	System.out.println(" afteh= " + rotation);
		int speed = 2;
		


			position = new Point(position.x
					+ (speed * Math.cos(Math.toRadians(rotation))), position.y
					+ (speed * Math.sin(Math.toRadians(rotation))));
			thrust.position = position;
							
			
			//WALLS
			//nedanför följer metoder som vi inte riktigt behöver, då ASS-roids gör att
			//skeppet spawnar på motsattsida när den träffar en kant?
			//ändrade andra variablen > screen.width/height & relX/relY
			//
			
			//Right Wall
			if (position.x > level.screenWidth-5) {
				
				position = new Point(position.x-10, position.y);	
				System.out.println("before = " + rotation);
				rotate();
				player2lives--;
			
				//Left Wall	
			} else if (position.x < level.relX) {
				System.out.println("INNE UH");
				position = new Point(position.x+10, position.y);
				System.out.println("before = " + rotation);
				rotate();
				System.out.println("after = " + rotation);
				player1lives--;
				
				
			}
			
			// Bottom Wall
			if (position.y > level.screenHeight) {
				position = new Point(position.x, position.y-10);
				System.out.println("before = " + rotation);
				rotateY();
				System.out.println("after = " + rotation);
				player4lives--;
			} 
			
			//Top Wall
			else if (position.y < level.relY){
				position = new Point(position.x, position.y+10);
				rotateY();
				System.out.println("after = " + rotation);
				player3lives--;
			}
			
			if (tlcHit == true){
				position = new Point(position.x+10, position.y+10);
				rotateCornerTopLeftBottomRight();
				tlcHit = false;
				
			}
			
			if (brcHit == true){
				position = new Point(position.x-10, position.y-10);
				rotateCornerTopLeftBottomRight();
				brcHit = false;
				
			}
			
			if (blcHit == true){
				position = new Point(position.x+10, position.y-10);
				rotateCornerBottomLeftTopRight();
				blcHit = false;
				
			}
			
			if (trcHit == true){
				position = new Point(position.x-10, position.y+10);
				rotateCornerBottomLeftTopRight();
				trcHit = false;
				
			}
			
			
			
			xPos = (int)position.x;
			yPos = (int)position.y;
	}
		//(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);
		public void paddleOneHit(int x, int y, int width, int height){
			
		
			if(position.x <= x +width+5){
				
				//	System.out.println("Nice X pos");	
				
				if( position.y >= y -5 && position.y <= y + height+5){
					
					System.out.println("player 1 bounce");
					position.x = position.x +10;
					rotate();
					/*int xSpeed = bally.getBallXSpeed(); 
						
					bally.setBallXSpeed(bounceX(xSpeed));
					xSpeed = bally.getBallXSpeed();
					int tempx = bally.getXPos();
					bally.setXPos(tempx += xSpeed);
				*/
				}
			}
		}
		//(level.screenWidth-11, y - (playerPingSize/2), user.userWidth, playerPingSize);
		public void paddleTwoHit(int x, int y, int width, int height){
			
			
			if(position.x >= x-10){
				//if(position.x >= x +width+5){
					
				//	System.out.println("Nice X pos");	
				
				if( position.y >= y -5 && position.y <= y + height+5){
					
					System.out.println("player 2 bounce");
					position.x = position.x -10;
					rotate();
					/*int xSpeed = bally.getBallXSpeed(); 
						
					bally.setBallXSpeed(bounceX(xSpeed));
					xSpeed = bally.getBallXSpeed();
					int tempx = bally.getXPos();
					bally.setXPos(tempx += xSpeed);
				*/
				}
			}
		}
		
		//(level.relY-11, level.screenWidth-11 ,playerPingSize, user.userWidth );
		public void paddleThreeHit(int x, int y, int width, int height){
			
			
				if(position.y <= x+5){
				
				//	System.out.println("Nice X pos");	
				
				if( position.x >= y -5 && position.x <= x + width+5){
					
					System.out.println("player three bounce");
					position.y = position.y -10;
					rotateY();

					
				}
			}
		}
		
		//ships[0].paddleFourHit(y - (playerPingSize/2),level.relX+10, playerPingSize,user.userWidth);
		public void paddleFourHit(int x, int y, int width, int heigth){
			
			
			if(position.y >= y-5){
				
				//	System.out.println("Nice X pos");	
				
				if( position.x >= x -5 && position.x <= x + width+5){
					
					System.out.println("player four bounce");
					position.y = position.y +10;
					rotateY();

					
				}
			}
		}
			
	public void topLeftCornerBounce(){
		tlcHit = true;
		
	}

	public void topRightCornerBounce(){
		trcHit = true;
	}
	
	public void bottomLeftCornerBounce(){
		blcHit = true;	
	}
	
	public void bottomRightCornerBounce(){
	brcHit = true;
		
	}
	
	public int getXPos(){
		return xPos;
	}
	public int getYPos(){
		return yPos;
	}

}
