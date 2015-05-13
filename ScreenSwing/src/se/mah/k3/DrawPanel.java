
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
	
	//creates a ball
	private Ball ball = new Ball();
	private int start = 0;
	int ballXSpeed = ball.screenWidth;
	int ballYSpeed = ball.screenHeight;
	
	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users = new Vector<User>();
	Font font = new Font("Verdana", Font.BOLD, 20);
//	  private int x1 = 50;
//	  private int x2 = 250;
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
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0, 5)); //Find the user usernama has to be unique uses the method compareTo in User
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
					System.out.println(listCount);//räknar antal spelar och skriver ut i konsollen. (börjar på 0)
					
					if (listCount ==0){
						User user = new User(arg0.getKey(),50,y, 5);
						if (!users.contains(user)){
							users.add(user);
							user.setColor(Color.BLACK);
							System.out.println("player 1 in");
				 		}
					}
					 if (listCount ==1){
						User user = new User(arg0.getKey(),1500,y, 5);
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
	 //   g2.drawImage(img1, 0, 0, this);
	    g2.finalize();
	    
		g2.fillOval(ballXSpeed, ballYSpeed, ball.size, ball.size);
		ballXSpeed = ball.getBallXSpeed();
		ballYSpeed = ball.getBallYSpeed();
		g.drawString("ScreenNbr: "+Constants.screenNbr, 10,  20);
		
		//g2.drawRect (ball.relX,ball.relY,ball.screenWidth,ball.screenHeight); //Spelplan
	    	g2.drawRect (130, 40,540,540);	
		
	    super.repaint();
	    
		//Test
		for (User user : users) {
			//int x = 50;
			int y = (int)(user.getyRel()*getSize().height);
			String livesLeft = String.valueOf(user.getLives());
			g2.setColor( user.getColor());
			
			g2.fillRect(user.getxPos(),y, 10, 100);
			
			//g2.setColor(Color.BLACK);
			g.drawString(user.getId(), user.getxPos(), 20);
			g.drawString(livesLeft, user.getxPos(), 40);
			

		}
		
	}
}

	