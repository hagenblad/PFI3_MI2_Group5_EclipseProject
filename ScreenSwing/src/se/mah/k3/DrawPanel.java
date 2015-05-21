
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
	
	//private int player1lives = 5;
	//private int player2lives = 5;
	public Polygon polyTRC;
	public Polygon polyTLC;
	public Polygon polyBRC;
	public Polygon polyBLC;
	public Polygon polyBally;
	

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
						User user = new User(arg0.getKey(), 650, ballLogic.screenWidth-10, ballLogic.player2lives); // create player 2
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
						 User user = new User(arg0.getKey(),100,ballLogic.screenHeight-10, ballLogic.player4lives); // create player 4
						 if (!users.contains(user)){
							 users.add(user);
								user.userHeight = 100;
								user.userWidth = 10;
							 user.setColor(Color.GREEN);
							 System.out.println("player 4 in");
						 }
					 }
				}
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		});

	}

	
	//Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {
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
	    	ballXPos = ballLogic.screenWidth/2;
	    	ballYPos = ballLogic.screenHeight/2;
	    	g.drawString("PING PONG", ballLogic.screenWidth/2-20, ballLogic.screenHeight/2-5);
	    }else{
	    	ballXPos = ball.getXPos();
			ballYPos = ball.getYPos();
			
	    }
	    
		g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());

	    g2.drawRect (130, 40,540,540);	
	    //CORNERS
	    //TOP LEFT CORNER
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
	    
	    
	    g.drawPolygon(polyBally);
	    g.fillPolygon (polyBally);
	    
	    
	    
	    //Collision between corners and ball polygon
	    
	    
	    Area areaBall = new Area (polyBally);
	    Area areaTLC = new Area (polyTLC);
	    Area areaBLC = new Area (polyBLC);
	    Area areaTRC = new Area (polyTRC);
	    Area areaBRC = new Area (polyBRC);
	    
	    
	    
	    
//	    areaBall.intersect(new Area (polyTLC));
//	    areaBall.intersect(new Area (polyBLC));
//	    areaBall.intersect(new Area (polyTRC));
//	    areaBall.intersect(new Area (polyBRC));
	    
	    
	    
	    areaTLC.intersect(areaBall);
	    if (!areaTLC.isEmpty()){
	    	// insert bounce method
	    	System.out.println("intercects TLC");
	    }
	    
	    
	    areaBLC.intersect(areaBall);
	    if (!areaBLC.isEmpty()){
	    	
	    	System.out.println("intercects BLC");
	    }
	   
	    areaTRC.intersect(areaBall);
	    if (!areaTRC.isEmpty()){
	    	
	    	System.out.println("intercects TRC");
	    }
	    
	    areaBRC.intersect(areaBall);
	    if (!areaBRC.isEmpty()){
	    	
	    	System.out.println("intercects BRC");
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
		 // defines how many players that needs to be in the game for it to start
			if(users.size()>=1){ 
			start = true;
			
			int x = (int)(user.getxRel()*getSize().height);
			int y = (int)(user.getyRel()*getSize().height);
			g2.setColor(user.getColor());

			//draw out players
			if(users.indexOf(user)<2){
				g2.fillRect(user.getxPos(), y, user.userWidth, user.userHeight);
				ballLogic.comparePosition(user.getxPos(), y, user.userWidth, user.userHeight);
			}
			
			else if (users.indexOf(user)>1){
				g2.fillRect(x, user.getyPos(), user.userHeight, user.userWidth);
				ballLogic.comparePosition(x, user.getyPos(), user.userHeight, user.userWidth);
			}
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
			
			//This prints out the ping to drawpanel
			String userDelay = String.valueOf(user.getDelay());
			g.drawString(userDelay, user.getxPos(), 30);
			//System.out.println(user.getId() + user.getDelay());
			
//			if(users.size()>4){
//			start = false;
//		}
			
		}
		
//		for (User user : horizontalUsers){
//			if (horizontalUsers.size()>=1){
//				start = true;
//				
//				int x = (int)(user.getxRel()*getSize().height);
//				int y = (int)(user.getyRel()*getSize().height);
//				g2.setColor(user.getColor());
//				
//				g2.fillRect(x, user.getyPos(), user.userWidth, user.userHeight);
//				
//				ballLogic.comparePosition(x, user.getyPos(), user.userWidth, user.userHeight);
//			}
//			g.drawString(user.getId(), 15, user.getyPos());
//			
//			String livesLeftPlayerThree = String.valueOf(ballLogic.player3lives);
//			String livesLeftPlayerFour = String.valueOf(ballLogic.player4lives);
//			
//			g.drawString(livesLeftPlayerThree, 15, 750); // this prints out how many lives player three has left
//			g.drawString(livesLeftPlayerFour,  750,  15); // this prints out how many lives player four has left
//			
//			String userDelay = String.valueOf(user.getDelay());
//			g.drawString(userDelay, 30, user.getyPos());
//		}
		
	}
}

	