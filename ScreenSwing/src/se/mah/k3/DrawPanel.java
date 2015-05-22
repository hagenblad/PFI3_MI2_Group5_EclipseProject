
package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Random;
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
	
// Boolean = work on mac;
	// work on mac = false;
	//hdmi (== non existing);
	//creates a ball
	
	Level level = new Level();
	
	private boolean start = false;
	int ballXPos = level.screenWidth;
	int ballYPos = level.screenHeight;
	

	int paddlePosY;
	int paddleBottom;
	int paddleTop;
	int y;
	
	//player ping
	long playerDelay;
	int playerPing;
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
					int listCount = users.size();
					System.out.println("number of players: " + listCount); //räknar antal spelar och skriver ut i konsollen. (börjar på 0)
//					
					if (listCount ==0){
						User user = new User(arg0.getKey(), 140, ballLogic.relX+10, ballLogic.player1lives); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							user.setColor(Color.BLACK);
							System.out.println("player 1 in");
				 		}
					}
					 if (listCount ==1){
						User user = new User(arg0.getKey(), 650, level.screenWidth-10, ballLogic.player2lives); // create player 2
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							user.setColor(Color.RED);
							System.out.println("player 2 in");
						}
					}	
					 if (listCount == 2){
						 User user = new User(arg0.getKey(),100, ballLogic.relY+10 , ballLogic.player3lives); // create player 3
						 if(!users.contains(user)){
							 users.add(user);
								user.userHeight = 100;
								user.userWidth = 10;
							 user.setColor(Color.BLUE);
							 System.out.println("player 3 in");
						 }
					 }
					 if (listCount == 3){
						 User user = new User(arg0.getKey(),100,level.screenHeight-10, ballLogic.player4lives); // create player 4
						 if (!users.contains(user)){
							 users.add(user);
								user.userHeight = 100;
								user.userWidth = 10;
							 user.setColor(Color.GREEN);
							 System.out.println("player 4 in");
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
		
		if(playerDelay > 0 && playerDelay < 600){

		playerPing = 0;

		}

		if(playerDelay > 600 && playerDelay < 900){

		playerPing = 300;

		}


		if(playerDelay > 900 && playerDelay < 1200){

		playerPing = 600;

		}


		if (playerDelay > 1200){

		playerPing = 900;

		} else{ 

		playerPing = (int) playerDelay;

		}
	}

	
	//Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
	
		//TOP LEFT CORNER
		int TLCxpoints[] = {130, 190, 130};
	    int TLCypoints[] = {40, 40, 100};
	    int npoints = 3;
	    
	    polyTLC = new Polygon(TLCxpoints, TLCypoints, npoints);
	    
	   //TOP RIGHT CORNER
	    int TRCxpoints[] = {670, 610, 670};
	    int TRCypoints[] = {40, 40, 100};
	    
	    polyTRC = new Polygon(TRCxpoints, TRCypoints, npoints);
	    
	   //BOT LEFT CORNER
	    
	    int BLCxpoints[] = {130,190,130};
	    int BLCypoints[] = {580,580,520};
	    
	    polyBLC = new Polygon(BLCxpoints, BLCypoints, npoints);
	    
	    //BOT RIGHT CORNER
	    int BRCxpoints[] = {670, 610, 670};
	    int BRCypoints[] = {580, 580, 520};
	    
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
		Image img1 = Toolkit.getDefaultToolkit().getImage("src/images/bakis.jpg");
	    g2.drawImage(img1, -100, 20, 1000, 580, this); 
		
 
	    g2.finalize();
	    if(start == false){
	    	ballXPos = level.screenWidth/2;
	    	ballYPos = level.screenHeight/2;
	    	
			Color c = new Color(19,156,234);
			g2.setColor(c);
			g2.fillRect(0,0,800,640);
			c = new Color(255,255,255);
			g2.setColor(c);
			g.drawString("TEMP SCREEN",level.screenWidth/2-10,level.screenHeight/2-40);
			g.drawString("The game will start when two players connects", level.screenWidth/2-200, level.screenHeight/2);
	    }else{
	    	ballXPos = ball.getXPos();
			ballYPos = ball.getYPos();
			g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());
			
	    
		//Background

		
	    g2.drawRect (130, 40,540,540);	
	    //CORNERS
	    //TOP LEFT CORNER
	    }
	    
	    Area areaBall = new Area (polyBally);
	    Area areaTLC = new Area (polyTLC);
	    Area areaBLC = new Area (polyBLC);
	    Area areaTRC = new Area (polyTRC);
	    Area areaBRC = new Area (polyBRC);

	    g.drawPolygon(polyTLC);
	    g.fillPolygon (polyTLC);
	    //BOT LEFT CORNER 
	    g.drawPolygon(polyBLC);
	    g.fillPolygon (polyBLC);
	    //TOP RIGHT CORNER
	    g.drawPolygon(polyTRC);
	    g.fillPolygon (polyTRC);
	    //BOT RIGHT CORNER
	    g.drawPolygon(polyBRC);
	    g.fillPolygon (polyBRC);
	    
	    
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
	    	   Thread.sleep(3);
	    	   } catch (Exception e) {
	    	   System.out.println(e);
	    	   }

	    super.repaint();
	    
		//Test
		for (User user : users) {
			if(users.size()>=1){  // defines how many players that needs to be in the game for it to start
			start = true;
			
			paddleTop = y - playerPingSize;
			paddleBottom = y;
			
			paddlePosY = y - playerPingSize;
			
			System.out.println(paddleTop + "paddletop");
			System.out.println(paddleBottom + "paddleBottom");
			
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
			if(users.indexOf(user) == 0){
				g2.fillRect(user.getxPos(), paddlePosY/2, user.userWidth, playerPingSize);
				ballLogic.comparePosition(user.getxPos(), paddlePosY/2, user.userWidth, playerPingSize);
		
			}   else if (users.indexOf(user) == 1){
				g2.fillRect(user.getxPos(), paddlePosY/2, user.userWidth, playerPingSize);
				ballLogic.comparePosition(user.getxPos(), paddlePosY/2, user.userWidth, playerPingSize);
			
			}
//				else if (users.indexOf(user) == 2){
//				g2.fillRect(x, user.getyPos(), user.userHeight, user.userWidth);
//				ballLogic.comparePosition(x, user.getyPos(), user.userHeight, user.userWidth);
//			
//			}	else if (users.indexOf(user) == 3){
//				g2.fillRect(x, user.getyPos(), user.userHeight, user.userWidth);
//				ballLogic.comparePosition(x, user.getyPos(), user.userHeight, user.userWidth);
//			}
			//System.out.println("User number "+ user +" has the position " + users.indexOf(user));	
			

			}
			g.drawString(user.getId(), user.getxPos(), 15); // This prints out the player names
			
			String livesLeftPlayerOne = String.valueOf(ballLogic.player1lives);
			String livesLeftPlayerTwo = String.valueOf(ballLogic.player2lives);
			String livesLeftPlayerThree = String.valueOf(ballLogic.player3lives);
			String livesLeftPlayerFour = String.valueOf(ballLogic.player4lives);

			g.drawString(livesLeftPlayerOne, 15, 15); // this prints out how many lives player one has left
			g.drawString(livesLeftPlayerTwo, 750, 15); // this prints out how many lives player two has left
			g.drawString(livesLeftPlayerThree, 15, 750); // this prints out how many lives player three has left
			g.drawString(livesLeftPlayerFour,  750,  15); // this prints out how many lives player four has left
			
			//System.out.println(user.getId() + user.getDelay());
			
			//This prints out the ping to drawpanel
			String userDelay = String.valueOf(user.getDelay());
			g.drawString(userDelay, user.getxPos(), 30);
			//System.out.println(user.getId() + user.getDelay());
			
//			if(users.size()>4){
//			start = false;
//		}
			
		}

		
		
	}
}
}

	