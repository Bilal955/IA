package algorithms;

import java.util.ArrayList;

import characteristics.IRadarResult;
import characteristics.Parameters;
import characteristics.Parameters.Direction;
import robotsimulator.Brain;

public class CacherDansLesCoins extends Brain {

	private double xFinalHaut = 0 + 2*Parameters.teamASecondaryBotRadius;
	private double yFinalHaut = 0 + 10 + Parameters.teamASecondaryBotRadius;
	private double yFinalBas = 1940;
	private double yMil = yFinalBas/2;

	private boolean okEnHaut = false;
	private boolean okEnBas = false;
	private boolean okAGauche = false;

	//---PARAMETERS---//
	private static final double HEADINGPRECISION = 0.001;
	private static final double ANGLEPRECISION = 0.1;

	private static final int enHaut = 0x343589;
	private static final int enBas = 0x10989;

	//---VARIABLES---//
	private double myX,myY;
	private boolean isMoving;
	private int whoAmI;

	private ArrayList<IRadarResult> radarResults;


	private boolean haveToMoveDroit; // TRUE --> moveBack

	@Override
	public void activate() {

		/* Detection de qui est le robot courant */
		for(IRadarResult r : detectRadar())
			if( (r.getObjectType() == IRadarResult.Types.TeamSecondaryBot) ) 
				whoAmI = r.getObjectDirection() < 0 ? enBas : enHaut;
		if (whoAmI == enHaut){
			haveToMoveDroit = false;
			myX=Parameters.teamASecondaryBot1InitX;
			myY=Parameters.teamASecondaryBot1InitY;
		} else {
			haveToMoveDroit = true;
			myX=Parameters.teamASecondaryBot2InitX;
			myY=Parameters.teamASecondaryBot2InitY;
		}

		//INIT
		isMoving=false;
	}



	private void myMove(){
		isMoving=true;
		myX += Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
		myY += Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
		move();
	}


	private void myMoveBack() {
		isMoving=true;
		myX -= Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
		myY -= Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
		moveBack();
	}

	@Override
	public void step() {
		// RADAR
		radarResults = detectRadar();

		// ODOMETRY CODE
		//		if (isMoving) {
		//			myX += Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
		//			myY += Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
		//			isMoving=false;
		//			return;
		//		}

		// Se cache dans les coins
		if(!goToPosition())
			return;

		// Est cache
		moveBasEnHaut();
	}


	private void moveBasEnHaut() {
		if( turnWhileVers(-Math.PI/2, Direction.RIGHT))
			return;


		System.out.println("HERE");
		if(imHaut()) {
			System.out.println("Y = "+myY+", X = "+myX);
			if( myY > yMil - (Parameters.teamASecondaryBotRadius+10) || myY < 50 )
				haveToMoveDroit = !haveToMoveDroit;

		} else {
			if(myY < yMil + (Parameters.teamASecondaryBotRadius+10) || myY >  yFinalBas-50)
				haveToMoveDroit = !haveToMoveDroit;
		}

		if(haveToMoveDroit) {
			System.out.println("Je move");
			myMove();
		}
		else {
			System.out.println("Je moveBack");
			myMoveBack();
		}

	}

	public boolean isEqual(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.001;
	}



	// Return true je dois rien faire false je suis OK
	private boolean turnWhileVers(double dir, Direction sens) { // Par la droite ou par la gauche
		if(!isHeading(dir)) { // Plus est eleve, plus il tourne
			stepTurn(sens);
			return true;
		} else {
			return false;
		}
	}




	// Je vais dans le direction dir
	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}


	private boolean isSameDirection(double dir1, double dir2){
		return Math.abs(dir1-dir2)<ANGLEPRECISION;
	}






	public boolean  goToPosition() {
		if(imHaut()) {
			if(iAmInPositionRobotHaut()) {
				return true;
			} else {
				if(pasEncoreEnHaut()) {
					if(turnWhile90DegreAGauche())
						;
					else {
						myMove();
					}
				}
				else {
					okEnHaut = true;
					if(pasEncoreAGauche()) {
						if(!turnWhileNotAGauche())
							myMove();
					}
					else {
						okAGauche = true;
					}
				}
			} 
			return false;
		}
		else {
			if(iAmInPositionRobotBas()) {
				return true;
			} else {
				if(pasEncoreEnBas()) {
					if(!turnWhile90DegreADroite())
						myMove();
				}
				else {
					okEnBas = true;
					if(pasEncoreAGauche()) {
						if(!turnWhileNotAGauche())
							myMove();
					}
					else {
						okAGauche = true;
					}
				}
			} 
			return false;
		}
	}


	private boolean pasEncoreEnBas() {
		return myY < yFinalBas - 50; ///////// TODO
	}
	private boolean pasEncoreEnHaut() {
		return myY > yFinalHaut + 50; //////// TODO
	}

	private boolean pasEncoreAGauche() {
		return myX > xFinalHaut;
	} 

	private boolean iAmInPositionRobotHaut() {
		return okAGauche && okEnHaut;
	}

	private boolean iAmInPositionRobotBas() {
		return okAGauche && okEnBas;
	}






	private boolean turnWhileNotVersLeHaut(Direction dir) {
		if(!isHeading(-Math.PI/2)) { // Plus le base est eleve, plus il tourne
			stepTurn(dir);
			return true;
		}
		else {
			return false;
		}
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

	private boolean turnWhile90DegreADroite() {
		if(super.getHeading() < Math.PI/2) { // Plus le base est eleve, plus il tourne
			stepTurn(Parameters.Direction.RIGHT);
			return true;
		}
		else {
			return false;
		}
	}

	private boolean turnWhileNotAGauche() {
		if(!isHeading(-Math.PI)) { // Plus le base est eleve, plus il tourne
			Parameters.Direction dir = imHaut() ? Parameters.Direction.LEFT : Parameters.Direction.RIGHT;
			stepTurn(dir);
			return true;
		}
		return false;
	}



	private boolean imHaut() {
		return whoAmI == enHaut;
	}

}
