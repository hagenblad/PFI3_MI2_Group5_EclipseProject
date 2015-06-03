package se.mah.k3;

//import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Random;
//import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class BallLogicV2 extends Polygon {
	public static int speed = 3;
	Ball ball = new Ball();
	Level level = new Level();

	int iterateSpeed = 0;
	
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
	
	//used to freeze ballspeed
	public boolean freeze = false;
	
	//used to activate ball respawn timer
	public boolean ballTimer = false;
	
	//PLAYAH LIVES
	public int player1lives;
	public int player2lives;
	public int player3lives;
	public int player4lives;
	
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

		
		//System.out.println(respawnBall);
	//	System.out.println(" befo= " + rotation);
		rotation = normalizeAngle( (int)rotation);
	//	System.out.println(" afteh= " + rotation);

		if(freeze == false){
		speed = 4;
		} else if(freeze == true){
		speed = 0;	
		}

		
		//activates respawntimer after goal
		if(ballTimer == true){
			new LivesTimer();
			ballTimer = false;
		}


			position = new Point(position.x
					+ (speed * Math.cos(Math.toRadians(rotation))), position.y
					+ (speed * Math.sin(Math.toRadians(rotation))));
			thrust.position = position;
				
			//SPEED ACCELERATOR
			
			if(iterateSpeed >= 7){
				System.out.println("Speed "+ speed);
				if(speed>=6){
					speed+=1;
				}
				iterateSpeed = 0;
				
			}
			
			
			//Right Wall
			if (position.x > level.screenWidth-5) {
				
				position = new Point(position.x-10, position.y);	
		//		System.out.println("before = " + rotation);
				rotate();
				player2lives--;
				ballRespawn();
				
			
				//Left Wall	
			} else if (position.x < level.relX) {
		//		System.out.println("INNE UH");
				position = new Point(position.x+10, position.y);
		//		System.out.println("before = " + rotation);
				rotate();
		//		System.out.println("after = " + rotation);
				player1lives--;
				ballRespawn();
			}
			
			// Bottom Wall
			if (position.y > level.screenHeight) {
				position = new Point(position.x, position.y-10);
	//			System.out.println("before = " + rotation);
				rotateY();
				player4lives--;
				ballRespawn();
				//		System.out.println("after = " + rotation);
		

			} 
			
			//Top Wall
			else if (position.y < level.relY){
				position = new Point(position.x, position.y+10);
				rotateY();
				player3lives--;
				ballRespawn();
	//			System.out.println("after = " + rotation);
	


			}
			
			if (tlcHit == true){
				position = new Point(position.x+10, position.y+10);
				rotateCornerTopLeftBottomRight();
				tlcHit = false;
				iterateSpeed++;
				
			}
			
			if (brcHit == true){
				position = new Point(position.x-10, position.y-10);
				rotateCornerTopLeftBottomRight();
				brcHit = false;
				iterateSpeed++;
				
			}
			
			if (blcHit == true){
				position = new Point(position.x+10, position.y-10);
				rotateCornerBottomLeftTopRight();
				blcHit = false;
				iterateSpeed++;
				
			}
			
			if (trcHit == true){
				position = new Point(position.x-10, position.y+10);
				rotateCornerBottomLeftTopRight();
				trcHit = false;
				iterateSpeed++;
				
			}
			
			
			
			xPos = (int)position.x;
			yPos = (int)position.y;
	}
		//(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);
		public void paddleOneHit(int x, int y, int width, int height){
			
			float heightParts = height/10;
			
			if(position.x <= x +width+5){
				
				//	System.out.println("Nice X pos");	
				
				if( position.y >= y -5 && position.y <= y + height+5){
					
	//				System.out.println("Nice YYY Pos");

				if(playerOneOut() == false){
					// hits the Left side
					if(position.y < y + heightParts*3){
						position.x = position.x +10;
						rotateLeft();
						System.out.println("Left side");
					}
					
					//Hits the middle part
				    if(position.y > y + (heightParts * 3) && position.y < y + (heightParts*7)){
				    	position.x = position.x +10;
						rotate();
						System.out.println("middle side");
						
					}
					//HIts the Right side
					 if(position.y > y + heightParts * 7){
						 position.x = position.x +10;
						rotateRight();
						System.out.println("Right side");
					}
					 
				}else{
					position.x = position.x +10;
					rotate();
				}
					
					
					
					iterateSpeed++;
					
					

				}

					/*int xSpeed = bally.getBallXSpeed(); 
						
					bally.setBallXSpeed(bounceX(xSpeed));
					xSpeed = bally.getBallXSpeed();
					int tempx = bally.getXPos();
					bally.setXPos(tempx += xSpeed);
				*/
				
			}
		}
		//(level.screenWidth-11, y - (playerPingSize/2), user.userWidth, playerPingSize);
		public void paddleTwoHit(int x, int y, int width, int height){
			
			float heightParts = height/10;
			
			
			if(position.x >= x-10){
				//if(position.x >= x +width+5){
					
				//	System.out.println("Nice X pos");	
				
				if( position.y >= y -5 && position.y <= y + height+5){
					
				if(playerTwoOut() == false){
					// hits the Right side
					if(position.y < y + heightParts*3){
						position.x = position.x -10;
						rotateRight();
						
						System.out.println("Right side");
					}
					
					//Hits the middle part
				    if(position.y > y + (heightParts * 3) && position.y < y + (heightParts*7)){
				    	position.x = position.x -10;
						rotate();
						System.out.println("middle side");
						
					}
					//HIts the left side
					 if(position.y > y + heightParts * 7){
						 position.x = position.x -10;
						rotateLeft();
						System.out.println("Left side");
					}
				}else{
					position.x = position.x -10;
					rotate();
					
				}
					
					
					
					iterateSpeed++;
					
					

				}
			}
		}
		
		// (y-(playerPingSize/2), level.relY+10 ,playerPingSize, user.userWidth );
		public void paddleThreeHit(int x, int y, int width, int height){
			float widthParts = width/10;
			//THE PLAYER ON TOP
			
			//PLAYER ON TOP
			
				if(position.y <= y+height+5){
				
				//	System.out.println("Nice X pos");	
				//Check in bounds
				if( position.x >= x -5 && position.x <= x+width+5){
					
					
				if(playerThreeOut() == false){
					// hits the Right side
					if(position.x < x + widthParts*3){
						
						position.y = position.y +10;
						rotateYLeft();
						
						System.out.println("Left side");
					}
					
					//Hits the middle part
				    if(position.x > x + (widthParts * 3) && position.x < x + (widthParts*7)){
				    	position.y = position.y +10;
						rotateY();
						System.out.println("middle side");
						
					}
					//HIts the left side
					 if(position.x > x + widthParts * 7){
						 position.y = position.y +10;
						rotateYRight();
						System.out.println("Right side");
					}
				}else{
					position.y = position.y +10;
					rotateY();
					
				}
					iterateSpeed++;


				}
			}
		}
		
	//	(y-(playerPingSize/2),level.screenHeight-15 , playerPingSize ,user.userWidth);
		
		public void paddleFourHit(int x, int y, int width, int height){
			float widthParts = width/10;
			
			//THE PLAYER AT THE BOTTOM
			//SHOULD MEASURE IF THE BALL IS BENEATH
			if(position.y >= y-5){
				
				//	System.out.println("Nice X pos");	

				if( position.x >= x -5 && position.x <= x+width+5){

					//System.out.println("player four bounce");
					
				if(playerFourOut() == false){
					// hits the Right side
					if(position.x < x + widthParts*3){
						 position.y = position.y -10;
						rotateYRight();
						System.out.println("Right side");
						
					}
					
					//Hits the middle part
				    if(position.x > x + (widthParts * 3) && position.x < x + (widthParts*7)){
				    	position.y = position.y -10;
						rotateY();
						System.out.println("middle side");
						
					}
					//HIts the left side
					 if(position.x > x + widthParts * 7){
							position.y = position.y -10;
							rotateYLeft();
							System.out.println("Left side");
						
					}
				}else{
					position.y = position.y -10;
					rotateY();
					
				}
					
					iterateSpeed++;
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
	
	public void ballRespawn (){
		Random rand2 = new Random();

		
		//boolean to activate timer
		ballTimer = true;
		
		//freezes ballspeed
		freeze = true;

//		speed = 2;
		position.x = level.relX+ 302;
		position.y = level.relY+ 305;

		//rotate();
		rotation = rand2.nextInt((359 - 0) + 1) -359;
		//System.out.println("respawntimer about to start");
		
		//System.out.println("respawntimer started");
	}
	
	public boolean player1Win (){
		if (player1lives > 0 && player2lives <= 0 && player3lives <= 0 && player4lives <= 0){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean player2Win (){
		if (player2lives > 0 && player1lives <= 0 && player3lives <= 0 && player4lives <= 0){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean player3Win (){
		if (player3lives > 0 && player1lives <= 0 && player2lives <= 0 && player4lives <= 0){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean player4Win (){
		if (player4lives > 0 && player1lives <= 0 && player2lives <= 0 && player3lives <= 0){
			return true;
		} else {
			return false;
		}
	}

	
	public class LivesTimer {
		Toolkit toolkit;
		Timer timer = new Timer();

		public LivesTimer(){
			toolkit = Toolkit.getDefaultToolkit();
			timer = new Timer();
			timer.schedule(new RespawnTimer(), 0,1 * 1000);
		}

		class RespawnTimer extends TimerTask {
			int count = 1;
			public void run(){
				if (count > 0){
					System.out.println("respawntimer started");
					System.out.println(count);
					//freeze = true;
				//	toolkit.beep();
					count--;
				} else {
				//	toolkit.beep();
					//speed = 2;
					System.out.println("respawntimer over");
					timer.cancel();
					freeze = false;
				}
			}
		}
	}
	
	public boolean playerOneOut (){
		if (player1lives == 0){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean playerTwoOut (){
		if (player2lives == 0){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean playerThreeOut (){
		if (player3lives == 0){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean playerFourOut (){
		if (player4lives == 0){
			return true;
		}else {
			return false;
		}
	}
}
