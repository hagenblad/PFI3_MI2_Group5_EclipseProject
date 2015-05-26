
package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Random;
import java.util.TimerTask;
import java.util.Vector;
import java.awt.Polygon;
import java.awt.geom.Area;

import javax.swing.JPanel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef;
	private Ball ball = new Ball();
	private BallLogic ballLogic = new BallLogic(ball);
	
	private TimerClass timer;
	
	private boolean timerController = true;
	
// Boolean = work on mac;
	// work on mac = false;
	//hdmi (== non existing);
	//creates a ball
	
	Level level = new Level();
	
	public boolean start = false;
	int ballXPos = level.screenWidth;
	int ballYPos = level.screenHeight;
	
	int listCount;

	int paddlePosY;
	int paddleBottom;
	int paddleTop;
	int y;
	
	//player ping
	long playerDelay;
	int playerPing;
	int playerDelayint;
	int playerPingSize;
	
	public Polygon polyTRC;
	public Polygon polyTLC;
	public Polygon polyBRC;
	public Polygon polyBLC;
	public Polygon polyBally;
	

	//private int player1lives = 5;
	//private int player2lives = 5;
	

	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users= new Vector<User>();
//	private Vector<User> horizontalUsers = new Vector<User>();
	Font font = new Font("Verdana", Font.BOLD, 20);

	public DrawPanel() {
		
	    
		myFirebaseRef = new Firebase("https://pingispong.firebaseio.com/");
		myFirebaseRef.removeValue(); //Cleans out everything
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);  //Has to be same as on the app. So place specific can't you see the screen you don't know the number
		 myFirebaseRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}
			
			//A user changed some value so update
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList= arg0.getChildren();
				Collections.sort(users);
//				Collections.sort(horizontalUsers);
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0, 5)); //Find the user username has to be unique uses the method compareTo in User
//				int position = Collections.binarySearch(horizontalUsers, new User(arg0.getKey(),0,0,5));
				for (DataSnapshot dataSnapshot : dsList) {					 
					 if (dataSnapshot.getKey().equals("xRel")){
						 users.get(place).setxRel((double)dataSnapshot.getValue());
//						 horizontalUsers.get(position).setxRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("yRel")){
						 users.get(place).setyRel((double)dataSnapshot.getValue());
//						 horizontalUsers.get(position).setyRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("RoundTripTo")){
						 myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					 }
					 // reach the ping variable in firebase
					 if (dataSnapshot.getKey().equals("ping")){
						 users.get(place).setDelay((long)dataSnapshot.getValue());
//						 horizontalUsers.get(position).setDelay((long)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("color")){

					 }
				 }
					
				 repaint();
			}
			
			//We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					int x = r.nextInt(getSize().width);
					int y = r.nextInt(getSize().height); 
					listCount = users.size();
					System.out.println("number of players: " + listCount); //räknar antal spelar och skriver ut i konsollen. (börjar på 0)

					if (listCount ==0){
						User user = new User(arg0.getKey(), level.relX+11, level.relX+11, ballLogic.player1lives); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color blue = Color.decode("#599bb9");
							user.setColor(blue);
							System.out.println("player 1 in");
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#599bb9");
				 		}
					}
					 if (listCount ==1){
						User user = new User(arg0.getKey(), level.screenWidth-11, level.screenWidth-10, ballLogic.player2lives); // create player 2
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color red = Color.decode("#d35959");
							user.setColor(red);
							System.out.println("player 2 in");
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#d35959");

						}
					}	
					 if (listCount == 2){
						 User user = new User(arg0.getKey(),100, ballLogic.relY+10 , ballLogic.player3lives); // create player 3
						 if(!users.contains(user)){
							 users.add(user);
							 user.userHeight = 100;
							 user.userWidth = 10;
							 Color green = Color.decode("#8cba66");
							 user.setColor(green);
							 System.out.println("player 3 in");
							 myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#8cba66");

						 }
					 }
					 if (listCount == 3){
						 User user = new User(arg0.getKey(),100,level.screenHeight-10, ballLogic.player4lives); // create player 4
						 if (!users.contains(user)){
							 users.add(user);
							 user.userHeight = 100;
							 user.userWidth = 10;
							 Color yellow = Color.decode("#e5d672");
							 user.setColor(yellow);
							 System.out.println("player 4 in");
							 myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#e5d672");

						 }
					if(listCount <3){
						try {
							
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					 }
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		});
		 
		 

	}
	
	public void initiateTimer(){
		if(timerController == true){
		System.out.println("About to start timer.");
	    new TimerClass(5);
	    System.out.println("Timer started.");
	    timerController = false;
		}
		}
	
	public void setPlayerBounds(User user){
		
		if(paddlePosY < 100){
			paddlePosY = 100;
		} else {
			paddlePosY = y - playerPingSize;
		}
		
		if(y > level.screenHeight){
			y = level.screenHeight;
		} else{
			y = (int)(user.getyRel()*getSize().height);
		}
		}
	
	
	public void setPlayerHeight(User user){
		playerDelay = user.getDelay();
		playerDelayint = (int) playerDelay;
		
		if(playerDelay > 0 && playerDelay < 600){
		playerPing = 0;
		}
		if(playerDelay > 600 && playerDelay < 900){
		playerPing = 150;
		}

		if(playerDelay > 900 && playerDelay < 1200){
		playerPing = 300;
		}
		if (playerDelay > 1200){
		playerPing = 450;
		} else{ 
		playerPing = (int) playerDelay;
		}
	}
	
	//Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
	
		//TOP LEFT CORNER
		int TLCxpoints[] = {level.relX, level.relX + 60+10, level.relX};
	    int TLCypoints[] = {level.relY, level.relY, level.relY+60+10};
	    int npoints = 3;
	    
	    polyTLC = new Polygon(TLCxpoints, TLCypoints, npoints);
	    
	   //TOP RIGHT CORNER
	    int TRCxpoints[] = {level.screenWidth, level.screenWidth-10-60, level.screenWidth};
	    int TRCypoints[] = {level.relY, level.relY, level.relY+60+10};
	    
	    polyTRC = new Polygon(TRCxpoints, TRCypoints, npoints);
	    
	   //BOT LEFT CORNER
	    
	    int BLCxpoints[] = {level.relX,level.relX+60+10,level.relX};
	    int BLCypoints[] = {level.screenHeight,level.screenHeight,level.screenHeight-60-10};
	    
	    polyBLC = new Polygon(BLCxpoints, BLCypoints, npoints);
	    
	    //BOT RIGHT CORNER
	    int BRCxpoints[] = {level.screenWidth, level.screenWidth-12-60, level.screenWidth};
	    int BRCypoints[] = {level.screenHeight, level.screenHeight, level.screenHeight-60-12};
	    
	    polyBRC = new Polygon(BRCxpoints, BRCypoints, npoints);
		
	    //Ball polygon
	    int ballNpoints = 4;
	    int ballPolysizeX[] = {0 + ballXPos, 0 + ballXPos,10 + ballXPos, 10 + ballXPos};
	    int ballPolysizeY[] = {0 + ballYPos, 10 + ballYPos,10 + ballYPos,0 + ballYPos};
	   
	   polyBally = new Polygon(ballPolysizeX,ballPolysizeY,ballNpoints);
	    
		//super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		g2.setFont(font);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.black);
		
		//Background
		Image img1 = Toolkit.getDefaultToolkit().getImage("src/images/bakgrundis.jpg");
	    g2.drawImage(img1, 0, 0, this); 
		
 
	    g2.finalize();
	    
	    if(start == false){
	    	ballXPos = level.screenWidth/2;
	    	ballYPos = level.screenHeight/2;
	    	
			Color c = new Color(19,156,234);
			g2.setColor(c);
			//g2.fillRect(0,0,1000,700);
			Image imgStart = Toolkit.getDefaultToolkit().getImage("src/images/Startscreen.jpg");
			
			g2.drawImage(imgStart, 0, 0, this);
			c = new Color(100,100,100);
			g2.setColor(c);
			//g.drawString("PING PONG TEMP SCREEN",level.screenWidth/2-30,level.screenHeight/2-40);
			g.drawString("The game will start when two players connects", level.screenWidth/2-150, level.screenHeight/2 + 200);
//			g.drawString("Player 1 connected", 100, 100);
//			g.drawString("Player 2 connected", 700, 100);
			
	    }else{
	    	ballXPos = ball.getXPos();
			ballYPos = ball.getYPos();
			

			//ball
			//g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());
			Image boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
			g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);
			

	    
		//Background
	    }
			Color c = new Color(19,156,234);
			g2.setColor(c);
		//	g2.drawRect (level.relX, level.relY, level.screenWidthForRect, level.screenHeightHeightForRect);	
	   

	    //CORNERS
	    //TOP LEFT CORNER
	 
	    
	    Area areaBall = new Area (polyBally);
	    Area areaTLC = new Area (polyTLC);
	    Area areaBLC = new Area (polyBLC);
	    Area areaTRC = new Area (polyTRC);
	    Area areaBRC = new Area (polyBRC);

//	    g.drawPolygon(polyTLC);
//	    g.fillPolygon (polyTLC);
//	    //BOT LEFT CORNER 
//	    g.drawPolygon(polyBLC);
//	    g.fillPolygon (polyBLC);
//	    //TOP RIGHT CORNER
//	    g.drawPolygon(polyTRC);
//	    g.fillPolygon (polyTRC);
//	    //BOT RIGHT CORNER
//	    g.drawPolygon(polyBRC);
//	    g.fillPolygon (polyBRC);
	    
	    
	    //corner collision
	    if(areaTLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){

	    	ballLogic.cornerBounce();
	    
	    }else if(areaBLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
	    	ballLogic.cornerBounce();
	    
	    }else if(areaTRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){

	    	ballLogic.cornerBounce();
	    
	    }else if(areaBRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){

	    	ballLogic.cornerBounce();
	    }
	    
	   	    
	     try {
	    	   // thread to sleep for 1000 milliseconds
	    	 if(users.size()==1){
	    	   Thread.sleep(3);
	    	 }else if(users.size()>1){
	    		 Thread.sleep(6);
	    		 
	    	 }
	    	   } catch (Exception e) {
	    	   System.out.println(e);
	    	   }
	     
	     c = new Color(100,100,100);
	     g2.setColor(c);
	     
			if(users.size() == 1){
				g.drawString("Player 1 connected", 100, 100);
			}
			
			if(users.size() == 2){
				g.drawString("Player 2 connected", 700, 100);
			}


	    super.repaint();

		//Test
		for (User user : users) {
			if(users.size()>=2){  // defines how many players that needs to be in the game for it to start			
			start = true;
			paddleTop = y - playerPingSize;
			paddleBottom = y;
			
			paddlePosY = y - playerPingSize;
			
//			System.out.println(paddleTop + "paddletop");
//			System.out.println(paddleBottom + "paddleBottom");
			
			y = (int)(user.getyRel()*getSize().height);
			g2.setColor( user.getColor());
			
			// sets appropriate height to players based on ping
			setPlayerHeight(user);
			setPlayerBounds(user);
			
			//Draws paddle from center of finger placement on android
			playerPingSize = user.userHeight + playerPing/4;


		 // defines how many players that needs to be in the game for it to start
			if(users.size()>=1){ 
			start = true;
			
			int x = (int)(user.getxRel()*getSize().height);
			int y = (int)(user.getyRel()*getSize().height);
			g2.setColor(user.getColor());

			//draw out players
			Image player1 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_left.png");
			Image player2 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_right.png");
			//Image player3 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_top.png");
			//Image player4 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_bottom.png");
			
			if(users.indexOf(user) == 0){
			//	g2.fillRect(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);
				ballLogic.comparePosition(user.getxPos()-2, y - (playerPingSize), user.userWidth, playerPingSize);
				g2.drawImage(player1, level.relX+1, y - (playerPingSize), user.userWidth, playerPingSize, this);
		
			}   else if (users.indexOf(user)==1 ){
		//		g2.fillRect(level.screenHeight-11, y - (playerPingSize/2), user.userWidth, playerPingSize);
				ballLogic.comparePosition(user.getxPos(), y - (playerPingSize), user.userWidth, playerPingSize);
				g2.drawImage(player2, level.screenWidth-11, y - (playerPingSize), user.userWidth, playerPingSize, this);
			
			}
			
			else if (users.indexOf(user) == 1){
				//g2.fillRect(1000, user.getyPos(), user.userHeight, user.userWidth);
				ballLogic.comparePosition(1000, user.getyPos(), user.userHeight, user.userWidth);
			   g2.drawImage(player2, user.getxPos(), y - playerPingSize, user.userWidth, playerPingSize, this);
			}
			//System.out.println("User number "+ user +" has the position " + users.indexOf(user));	
				

		//	}
			//g.drawString(user.getId(), user.getxPos(), 15); // This prints out the player names
			c = Color.WHITE;
			String livesLeftPlayerOne = String.valueOf(ballLogic.player1lives);
			String livesLeftPlayerTwo = String.valueOf(ballLogic.player2lives);
			String livesLeftPlayerThree = String.valueOf(ballLogic.player3lives);
			String livesLeftPlayerFour = String.valueOf(ballLogic.player4lives);

			g.drawString(livesLeftPlayerOne + " Lives left ", level.relX-150, 40); // this prints out how many lives player one has left
			g.drawString(livesLeftPlayerTwo + " Lives left ", level.screenWidth+20, 40); // this prints out how many lives player two has left
			//g.drawString(livesLeftPlayerThree, 15, 750); // this prints out how many lives player three has left
			//g.drawString(livesLeftPlayerFour,  750,  15); // this prints out how many lives player four has left
			
			//System.out.println(user.getId() + user.getDelay());
			
			//This prints out the ping to drawpanel
			String userDelay = String.valueOf(user.getDelay());
			g.drawString("PING = " + userDelay, user.getxPos(), 10);
			//System.out.println(user.getId() + user.getDelay());
			
//			if(users.size()>4){
//			start = false;
//		}
			
		}

		
		
	}
	    }
}
}

	
