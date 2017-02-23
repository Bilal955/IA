/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import java.util.ArrayList;

import characteristics.IFrontSensorResult;
import characteristics.Parameters.Direction;

import java.util.Random;

import robotsimulator.Brain;
import characteristics.IRadarResult;

public class BrainMainCanevas extends Brain {
	// ---VARIABLES---//
	private static final double HEADINGPRECISION = 0.001;
	private Random rand;
	private boolean shoot;
	private boolean front;
	private boolean turning;
	private Direction turningDir;

	// ---CONSTRUCTORS---//
	public BrainMainCanevas() {
		super();
	}

	// ---ABSTRACT-METHODS-IMPLEMENTATION---//
	public void activate() {
		move();
		front = true;
		shoot = true;
		rand = new Random();
		rand.setSeed(10);
		turning = false;
		turningDir = Direction.LEFT;
	}

	public void step() {
		
		System.out.println("Ma life: " + getHealth());
		if(isHeading(getHealth()))
			return;
		
		ArrayList<IRadarResult> res = detectRadar();
		
		for (IRadarResult iRadarResult : res) {
			IRadarResult.Types type = iRadarResult.getObjectType();
			
			// Si je vois un ennemi je lui tire dessus
			if (type == IRadarResult.Types.OpponentMainBot
					|| type == IRadarResult.Types.OpponentSecondaryBot) {
				fire(iRadarResult.getObjectDirection());
				System.out.println("Tirer vers " + iRadarResult.getObjectType().toString());
				front = false;
				return;
			}
			// S'il y a une balle en ma direction je tire aussi
//			double objDirection = iRadarResult.getObjectDirection();
//			if (type == IRadarResult.Types.BULLET && isHeading(objDirection)) {
//				fire(objDirection);
//				return;
//			}
			System.out.println(iRadarResult.getObjectType());
		}
		
		// Si je vois personne à l'horizon je bouge
//		if(nobody)
//			front = true;
		
		// S'il y a un mur je recule
		if (detectFront().getObjectType() == IFrontSensorResult.Types.WALL) {
			turning = false;
		}

		// Soit je tire soit je bouge
		if (shoot) {
			System.out.println("Je tire vers " + getShootingAngle());
			fire(getShootingAngle());
			shoot = false;
		} else {
			if (front) {
				System.out.println("J'avance mais wtf");
				move();
			} else {
				System.out.println("Y a un mur devant je dois reculer");
				moveBack();
			}
			shoot = true;
		}
	}

	public double getShootingAngle() {
		return rand.nextDouble() < 0.5 ? rand.nextDouble() + getHeading()
				: getHeading() - rand.nextDouble();
	}
	
	private boolean isHeading(double dir){
	    return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	  }
}
