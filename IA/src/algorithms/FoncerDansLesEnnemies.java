package algorithms;

import java.util.ArrayList;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;
import characteristics.Parameters.Direction;
import robotsimulator.Brain;

public class FoncerDansLesEnnemies extends Brain {

	private static IFrontSensorResult.Types WALL = IFrontSensorResult.Types.WALL;
	private static IFrontSensorResult.Types TEAMMAIN = IFrontSensorResult.Types.TeamMainBot;

	private double endTaskDirection;


	private ArrayList<IRadarResult> radarResults;

	@Override
	public void activate() {
		endTaskDirection=(Math.random()-0.5)*0.5*Math.PI;
		endTaskDirection+=getHeading();
	}



	private int cpt = 0;
	private double lastDir;
	private boolean firstLastDir = true;

	@Override
	public void step() {
		radarResults = detectRadar();


		boolean ennemyFind = false;
		double dir = 0;
		for(IRadarResult r : radarResults) {
			if( (r.getObjectType() == IRadarResult.Types.OpponentMainBot) ) {
				ennemyFind = true;
				dir = r.getObjectDirection();
				if(firstLastDir) {
					lastDir = dir;
					firstLastDir = false;
				}
				else {
					if(Math.abs(dir-lastDir) < 0.1)
						break;
				}
			}
		}

		if(ennemyFind) {
			System.out.println(this+" ;;; DIR = "+dir + " :: EndTask = "+endTaskDirection);
			if(isHeading(dir)) {
				move();
				return;
			}
			else if(dir > 0) {
				endTaskDirection=getHeading()-0.5*Math.PI;
				stepTurn(Direction.RIGHT);
				return;
			}
			else {
				endTaskDirection=getHeading()+0.5*Math.PI;
				stepTurn(Direction.LEFT);
				return;
			}
		}

		move();
	}

	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<Parameters.teamAMainBotStepTurnAngle;
	}


}
