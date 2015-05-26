package se.mah.k3;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

public class TimerClass {
	Toolkit toolkit;

	  Timer timer;
	  DrawPanel dP = new DrawPanel();
	  
//	 public boolean start = false;
	  
	  public TimerClass(int seconds) {
		    toolkit = Toolkit.getDefaultToolkit();
		    timer = new Timer();
		    timer.schedule(new RemindTask(), seconds * 1000);

	  }
	  
	  class RemindTask extends TimerTask {
		    public void run() {
		      dP.start = true;
		      System.out.println("Timer completed");
		     // toolkit.beep();
		      //timer.cancel(); //Not necessary because we call System.exit
		      timer.cancel(); //Stops the AWT thread (and everything else)
		    }
		  }
}


