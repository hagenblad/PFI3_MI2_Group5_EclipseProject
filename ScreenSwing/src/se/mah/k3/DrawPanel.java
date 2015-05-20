
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
	
	private boolean start = false;
	int ballXPos = ballLogic.screenWidth;
	int ballYPos = ballLogic.screenHeight;
	

	public Polygon polyTRC;
	public Polygon polyTLC;
	public Polygon polyBRC;
	public Polygon polyBLC;
	public Polygon polyBally;
	

	//private int player1lives = 5;
	//private int player2lives = 5;

	

	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users = new Vector<User>();
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
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0, 5)); //Find the user username has to be unique uses the method compareTo in User
				 for (DataSnapshot dataSnapshot : dsList) {					 
					 if (dataSnapshot.getKey().equals("xRel")){
						 users.get(place).setxRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("yRel")){
						 users.get(place).setyRel((double)dataSnapshot.getValue());
					 }
					 if (dataSnapshot.getKey().equals("RoundTripTo")){
						 myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					 }
					 // reach the ping variable in firebase
					 if (dataSnapshot.getKey().equals("ping")){
						 users.get(place).setDelay((long)dataSnapshot.getValue());
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
					
					if (listCount ==0){
						User user = new User(arg0.getKey(),140,y, ballLogic.player1lives); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.setColor(Color.BLACK);
							System.out.println("player 1 in");
				 		}
					}
					 if (listCount ==1){
						User user = new User(arg0.getKey(),650,y, ballLogic.player2lives); // create player 2
						if (!users.contains(user)){
							users.add(user);
							user.setColor(Color.RED);
							System.out.println("player 2 in");
						}
					}	
					 if (listCount == 2){
						 User user = new User(arg0.getKey(), x, 50, ballLogic.player3lives); // create player 3
						 if(!users.contains(user)){
							 users.add(user);
							 user.setColor(Color.BLUE);
							 System.out.println("player 3 in");
						 }
					 }
					 if (listCount == 3){
						 User user = new User(arg0.getKey(), x, 650, ballLogic.player4lives); // create player 4
						 if (!users.contains(user)){
							 users.add(user);
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
		//TOP LEFT CORNER
//		int TLCxpoints[] = {ballLogic.relX+10, ballLogic.relX+ 100, ballLogic.relX +10};
		 //   int TLCypoints[] = {ballLogic.relY, ballLogic.relY, ballLogic.relY + 100};
		 
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
	    	ballXPos = ballLogic.screenWidth/2;
	    	ballYPos = ballLogic.screenHeight/2;
	    	g.drawString("PING PONG", ballLogic.screenWidth/2-20, ballLogic.screenHeight/2-5);
	    }else{
	    	ballXPos = ball.getXPos();
			ballYPos = ball.getYPos();
			
	    }

		g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());
		
	    Area areaBall = new Area (polyBally);
	    Area areaTLC = new Area (polyTLC);
	    Area areaBLC = new Area (polyBLC);
	    Area areaTRC = new Area (polyTRC);
	    Area areaBRC = new Area (polyBRC);

		
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
	    
	    
	    //g.drawPolygon(polyBally);
	   // g.fillPolygon (polyBally);
	    
	    
	    
	    //Collision between corners and ball polygon
	    
	    

	    
	    areaTLC.intersect(areaBall);
	    if (!areaTLC.isEmpty()){
	    	// insert bounce method
	    	
	    	//System.out.println("intercects TLC");
	    	//Corner bounce-check
	    	ballLogic.cornerBounce();
	    
	    }
	    
	    
	    areaBLC.intersect(areaBall);
	    if (!areaBLC.isEmpty()){
	    	
	    //	System.out.println("intercects BLC");
	    	ballLogic.cornerBounce();
	    }
	   
	    areaTRC.intersect(areaBall);
	    if (!areaTRC.isEmpty()){
	    	
	    //	System.out.println("intercects TRC");
	    	ballLogic.cornerBounce();
	    }
	    
	    areaBRC.intersect(areaBall);
	    if (!areaBRC.isEmpty()){
	    	
	    //	System.out.println("intercects BRC");
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
			
			int y = (int)(user.getyRel()*getSize().height);
			g2.setColor( user.getColor());

			
			//draw out player
			g2.fillRect(user.getxPos(), y , user.userWidth, user.userHeight);
			
				
			ballLogic.comparePosition(user.getxPos(),y,user.userWidth,user.userHeight);

			}
			String livesLeftPlayerOne = String.valueOf(ballLogic.player1lives);
			String livesLeftPlayerTwo = String.valueOf(ballLogic.player2lives);

			g.drawString(user.getId(), user.getxPos(), 15); // This prints out the player names
			//String playerOneLives = t(player1lives);
			g.drawString(livesLeftPlayerOne, 15, 15); // this prints out how many lives player one has left
			g.drawString(livesLeftPlayerTwo, 750, 15); // this prints out how many lives player two has left
			
			//System.out.println(user.getId() + user.getDelay());
			//This prints out the ping to drawpanel
			String userDelay = String.valueOf(user.getDelay());
			g.drawString(userDelay, user.getxPos(), 30);

				 
//			if(users.size()>4){
//				start = false;
//			}
			
		}
		
	}
}

	