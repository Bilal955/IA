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
	private boolean avoid;
	private int id;
	private static int gene = 1;

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
		avoid = false;
		id = gene++;
	}

	public void step() {
		if (isHeading(getHealth()))
			return;

		boolean nobody = false;
		ArrayList<IRadarResult> res = detectRadar();
		if (turning) {
			// System.out.println("Turning angle " + getHeading());
			stepTurn(Direction.LEFT);
			if(getHeading() != 0.0 && isEndTurn(Math.PI)) {
				System.out.println("Stop turning");
				turning = false;
			}
			return;
		}
		
		if(avoid) {
			System.out.println("Je evite");
			stepTurn(Direction.LEFT);
			if(getHeading() != 0.0 && isEndTurn(Math.PI/2)) {
				System.out.println("Arreter de tourner");
				avoid = false;
			}
			return;
		}
		
		if(detectFront().getObjectType() == IFrontSensorResult.Types.BULLET) {
			System.out.println("Je recule y a une balle");
			moveBack();
			return;
		}

		for (IRadarResult iRadarResult : res) {
			IRadarResult.Types type = iRadarResult.getObjectType();
			
			if(type == IRadarResult.Types.Wreck && isHeading(iRadarResult.getObjectDirection())) {
				System.out.println("J'essaye d'eviter quelqu'un + " + id);
				// stepTurn(Direction.LEFT);
				moveBack();
				// front = false;
				avoid = true;
				return;
			}

			// Si je vois un ennemi je lui tire dessus
			if (type == IRadarResult.Types.OpponentMainBot || type == IRadarResult.Types.OpponentSecondaryBot) {
				fire(iRadarResult.getObjectDirection());
				// System.out.println("Tirer vers " + iRadarResult.getObjectType().toString());
				// front = false;
				return;
			}
			// S'il y a une balle en ma direction je tire aussi
			// double objDirection = iRadarResult.getObjectDirection();
			// if (type == IRadarResult.Types.BULLET && isHeading(objDirection))
			// {
			// fire(objDirection);
			// return;
			// }
			// System.out.println(iRadarResult.getObjectType());
		}

		// Si je vois personne à l'horizon je bouge
		if(nobody)
			front = true;

		// S'il y a un mur je recule
		if (detectFront().getObjectType() == IFrontSensorResult.Types.WALL) {
			turning = true;
		}

		// Soit je tire soit je bouge
		if (shoot) {
			// System.out.println("Je tire vers " + getShootingAngle());
			fire(getShootingAngle());
			shoot = false;
		} else {
			if (front) {
				// System.out.println("J'avance mais wtf");
				move();
			} else {
				// System.out.println("Y a un mur devant je dois reculer");
				moveBack();
			}
			shoot = true;
		}
	}

	public double getShootingAngle() {
		return rand.nextDouble() < 0.5 ? rand.nextDouble() + getHeading() : getHeading() - rand.nextDouble();
	}

	private boolean isHeading(double dir) {
		return Math.abs(Math.sin(getHeading() - dir)) < HEADINGPRECISION;
	}
	
	public boolean isEndTurn(double val) {
		return Math.abs(Math.sin((getHeading()%val) - val)) < HEADINGPRECISION;
	}
}
