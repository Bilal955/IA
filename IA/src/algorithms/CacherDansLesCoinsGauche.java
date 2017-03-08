package algorithms;

import java.util.ArrayList;

import characteristics.IRadarResult;
import characteristics.Parameters;
import characteristics.Parameters.Direction;
import robotsimulator.Brain;

public class CacherDansLesCoinsGauche extends Brain {

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
	private int whoAmI;
	private boolean imRobotDuHaut;

	private ArrayList<IRadarResult> radarResults;


	private boolean haveToMoveDroit; // TRUE --> moveBack

	@Override
	public void activate() {

		/* Detection de qui est le robot courant */
		for(IRadarResult r : detectRadar())
			if( (r.getObjectType() == IRadarResult.Types.TeamSecondaryBot) ) 
				whoAmI = r.getObjectDirection() < 0 ? enBas : enHaut;
		imRobotDuHaut = (whoAmI == enHaut);
		if (whoAmI == enHaut){
			haveToMoveDroit = false;
			myX=Parameters.teamASecondaryBot1InitX;
			myY=Parameters.teamASecondaryBot1InitY;
		} else {
			haveToMoveDroit = true;
			myX=Parameters.teamASecondaryBot2InitX;
			myY=Parameters.teamASecondaryBot2InitY;
		}
	}

	private void myMove(){
		myX += Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
		myY += Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
		move();
	}
	private void myMoveBack() {
		myX -= Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
		myY -= Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
		moveBack();
	}

	@Override
	public void step() {
		// RADAR
		radarResults = detectRadar();

		// Se cache dans les coins
		if(!iAmInMyPosition())
			return;
		// Une fois qu'il est proche du mur cache, mvt de haut en bas
		moveBasEnHaut();
	}


	private void moveBasEnHaut() {
		if(!turnVersThisPositionIsOk(-Math.PI/2, Direction.RIGHT))
			return;
		// Changement de dir une fois qu'on a atteint une limite
		if(imRobotDuHaut) { 
			if( myY > yMil - (Parameters.teamASecondaryBotRadius+10) || myY < 50 )
				haveToMoveDroit = !haveToMoveDroit;
		} else {
			if(myY < yMil + (Parameters.teamASecondaryBotRadius+10) || myY >  yFinalBas-50)
				haveToMoveDroit = !haveToMoveDroit;
		}
		// Mouvement
		if(haveToMoveDroit) 
			myMove();
		else 
			myMoveBack();
	}

	// Return true je dois rien faire false je suis OK
	private boolean turnVersThisPositionIsOk(double dir, Direction sens) { // Par la droite ou par la gauche
		if(!isHeading(dir)) { // Plus est eleve, plus il tourne
			stepTurn(sens);
			return false;
		} else {
			return true;
		}
	}

	public boolean doubleAreEquals(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.001;
	}

	// Je vais dans le direction dir
	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}

	private boolean isSameDirection(double dir1, double dir2){
		return Math.abs(dir1-dir2)<ANGLEPRECISION;
	}


	
	// GO dans le coin en haut a gauche ou en bas a gauche
	public boolean  iAmInMyPosition() {
		
		boolean pasEncoreEnBas = myY < yFinalBas - 50;
		boolean pasEncoreEnHaut = myY > yFinalHaut + 50; 
		boolean pasEncoreAGauche = myX > xFinalHaut;
		
		Parameters.Direction dirCur = imRobotDuHaut ? Parameters.Direction.LEFT : Parameters.Direction.RIGHT;
		if(imRobotDuHaut) {
			if(okAGauche && okEnHaut) {
				return true;
			} else {
				if(pasEncoreEnHaut) {
					if(turnVersThisPositionIsOk(-Math.PI/2, Direction.LEFT))
						myMove();
				}
				else {
					okEnHaut = true;
					if(pasEncoreAGauche) {
						if(turnVersThisPositionIsOk(-Math.PI, dirCur))
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
			if(okAGauche && okEnBas) {
				return true;
			} else {
				if(pasEncoreEnBas) {
					if(turnVersThisPositionIsOk(Math.PI/2, Direction.RIGHT))
						myMove();
				}
				else {
					okEnBas = true;
					if(pasEncoreAGauche) {
						if(turnVersThisPositionIsOk(-Math.PI, dirCur))
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


}
