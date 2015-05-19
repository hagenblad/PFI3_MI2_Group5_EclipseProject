
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
					 
					 // tried here to reach the ping variable in firebase
//					 if (dataSnapshot.getKey().equals("ping")){
//						 users.get(place).setDelay((double)dataSnapshot.getValue());
//					 }
				 }
					
				 repaint();

			}
			
			//We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					//int x = 50;
					int y = r.nextInt(getSize().height);
					int listCount = users.size();
					System.out.println("number of players: " + listCount); //räknar antal spelar och skriver ut i konsollen. (börjar på 0)
					
					if (listCount ==0){
						User user = new User(arg0.getKey(),140,y, ballLogic.player1lives);
						if (!users.contains(user)){
							users.add(user);
							user.setColor(Color.BLACK);
							System.out.println("player 1 in");
				 		}
					}
					 if (listCount ==1){
						User user = new User(arg0.getKey(),650,y, ballLogic.player2lives);
						if (!users.contains(user)){
							users.add(user);
							user.setColor(Color.RED);
							System.out.println("player 2 in");
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
		
		//Bakgrund
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
	    try {
	    	   // thread to sleep for 1000 milliseconds
	    	   Thread.sleep(3);
	    	   } catch (Exception e) {
	    	   System.out.println(e);
	    	   }

	    super.repaint();
	    
		//Test
		for (User user : users) {
			if(users.size()>=1){
			start = true;
			
			int y = (int)(user.getyRel()*getSize().height);
			g2.setColor( user.getColor());

			
			//draw out player
			g2.fillRect(user.getxPos(), y , user.userWidth, user.userHeight);
			
				
			ballLogic.comparePosition(user.getxPos(),y,user.userWidth,user.userHeight);

			}
			String livesLeftPlayerOne = String.valueOf(ballLogic.player1lives);
			String livesLeftPlayerTwo = String.valueOf(ballLogic.player2lives);

			g.drawString(user.getId(), user.getxPos(), 15);
			//String playerOneLives = t(player1lives);
			g.drawString(livesLeftPlayerOne, 15, 15);
			g.drawString(livesLeftPlayerTwo, 750, 15);

				 
//			if(users.size()>4){
//				start = false;
//			}
			
		}
		
	}
}

	