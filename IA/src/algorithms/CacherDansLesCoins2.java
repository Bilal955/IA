package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import characteristics.IRadarResult;
import characteristics.Parameters;
import characteristics.Parameters.Direction;
import robotsimulator.Brain;

public class CacherDansLesCoins2 extends Brain {


	private double xFinalHaut = 0+2*Parameters.teamASecondaryBotRadius;
	private double yFinalHaut = 0+2*Parameters.teamASecondaryBotRadius;

	private boolean okEnHaut = false;
	private boolean okAGauche = false;

	//---PARAMETERS---//
	private static final double HEADINGPRECISION = 0.001;
	private static final double ANGLEPRECISION = 0.1;
	private static final int UNDEFINED = 0xBADC0DE;

	private static final int enHaut = 0x343589;
	private static final int enBas = 0x10989;

	//---VARIABLES---//
	private boolean turnNorthTask,turnLeftTask;
	private double endTaskDirection;
	private double myX,myY;
	private boolean isMoving;
	private int whoAmI;

	private ArrayList<IRadarResult> radarResults;

	@Override
	public void activate() {
		for(IRadarResult r : detectRadar()) {
			if( (r.getObjectType() == IRadarResult.Types.TeamSecondaryBot) ) {
				if(r.getObjectDirection() < 0)
					whoAmI = enBas;
				else
					whoAmI = enHaut;
			}
		}
		for (IRadarResult o: detectRadar())
			if (isSameDirection(o.getObjectDirection(),Parameters.NORTH)) 
				whoAmI = UNDEFINED;
		if (whoAmI == enHaut){
			myX=Parameters.teamASecondaryBot1InitX;
			myY=Parameters.teamASecondaryBot1InitY;
		} else {
			myX=Parameters.teamASecondaryBot2InitX;
			myY=Parameters.teamASecondaryBot2InitY;
		}


		//INIT
		turnNorthTask=true;
		turnLeftTask=false;
		isMoving=false;
	}

	public double getDirectionToAngle(double dir, double x2, double y2) {
		double angle = Math.atan2(y2, x2) - Math.atan(dir);
		if (angle < 0) 
			angle += 2 * Math.PI;
		return angle;
	}
	
	@Override
	public void step() {
		// RADAR
		radarResults = detectRadar();

		// ODOMETRY CODE
		if (isMoving) {
			myX += Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
			myY += Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
			isMoving=false;
			return;
		}

		//DEBUG MESSAGE
		if(imHaut()) {
			MyPoint objectif = new MyPoint(1, 1);
			MyPoint ptMe = new MyPoint(myX, myY);
			double dir = -getAngle(ptMe, objectif);
			System.out.println(getHeading());
			
			if(isHeading2(dir)) {
				System.out.println("JE SUIS HEADING");
				myMove();
				return;
			}
			else if(dir > 0) {
				stepTurn(Direction.RIGHT);
				return;
			}
			else {
				stepTurn(Direction.LEFT);
				return;
			}
		}
		else {

		}

	}
	
	
	 public double getAngle(MyPoint me, MyPoint dest) {
		 Vector2d vMe = new Vector2d(me);
		 Vector2d vDest= new Vector2d(dest);
		 double angle = vMe.angle(vDest);
		 System.out.println(me + " | " + dest + " | " +angle + " | "+getHeading());
		 return angle;
     }



	private boolean imHaut() {
		return whoAmI == enHaut;
	}

	private void myMove(){
		isMoving=true;
		move();
	}

	// Je vais dans le direction dir
	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}

	private boolean isHeading2(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<Parameters.teamAMainBotStepTurnAngle;
	}

	private boolean isSameDirection(double dir1, double dir2){
		return Math.abs(dir1-dir2)<ANGLEPRECISION;
	}
	

}
