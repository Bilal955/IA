package algorithms;

import java.util.ArrayList;

import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;

public class CacherDansLesCoins extends Brain {

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
			if(iAmInPositionRobotHaut()) {
				// System.out.println("HELLO DUDE ------------------");
				return;
			} else {
				// System.out.println("X = "+myX+", Y = "+myY);
				if(pasEncoreEnHaut()) {
					if(turnWhile90DegreAGauche())
						return;
					else {
						myMove();
						return;
					}
				}
				else {
					okEnHaut = true;
					if(pasEncoreAGauche()) {
						if(turnWhileNotAGauche())
							return;
						else {
							myMove();
							return;
						}
					}
					else {
						okAGauche = true;
						return;
					}
				}
			} 
		}
		else {

		}

	}



	private boolean iAmInPositionRobotHaut() {
		return okAGauche && okEnHaut;
	}

	private boolean pasEncoreAGauche() {
		return myX > xFinalHaut;
	} 
	private boolean pasEncoreEnHaut() {
		return myY > yFinalHaut;
	}

	private boolean turnWhile90DegreAGauche() {
		if(super.getHeading() > -Math.PI/2) { // Plus le base est eleve, plus il tourne
			stepTurn(Parameters.Direction.LEFT);
			return true;
		}
		else {
			return false;
		}
	}


	private boolean turnWhileNotAGauche() {
		if(super.getHeading() > -Math.PI) { // Plus le base est eleve, plus il tourne
			stepTurn(Parameters.Direction.LEFT);
			return true;
		}
		else {
			return false;
		}
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


	private boolean isSameDirection(double dir1, double dir2){
		return Math.abs(dir1-dir2)<ANGLEPRECISION;
	}

}
