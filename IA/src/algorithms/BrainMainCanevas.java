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
	private int shoot;
	private int shootEnemy;
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
		shoot = 0;
		shootEnemy = 0;
		rand = new Random();
		rand.setSeed(10);
		turning = false;
		avoid = false;
		id = gene++;
	}

	public void step() {
		if (isHeading(getHealth()))
			return;

		boolean nobody = true;
		IFrontSensorResult.Types frontType = detectFront().getObjectType();
		ArrayList<IRadarResult> res = detectRadar();
		if(!frontType.toString().equals("NOTHING"))
			System.out.println("> " + id + " front: " + frontType + " avance: " + front);

		if (turning) {
			stepTurn(Direction.LEFT);
			if (getHeading() != 0.0 && isEndTurn(Math.PI)) {
				System.out.println("Stop turning");
				turning = false;
				if(rand.nextInt(5) == 1)
					avoid = true;
			}
			return;
		}

		if (avoid) {
			stepTurn(Direction.LEFT);
			if (getHeading() != 0.0 && isEndTurn(Math.PI / 2)) {
				System.out.println(">" + id + " arretes de tourner");
				avoid = false;
			}
			return;
		}

		if (frontType == IFrontSensorResult.Types.Wreck) {
			moveBack();
			avoid = true;
			return;
		}

		int nbEnemy = 0;
		for (IRadarResult iRadarResult : res) {
			IRadarResult.Types type = iRadarResult.getObjectType();
			if (type == IRadarResult.Types.OpponentMainBot || type == IRadarResult.Types.OpponentSecondaryBot) {
				nbEnemy++;
				nobody = false;
			}
		}
		if (nbEnemy >= 2) {
			moveBack();
			return;
		}

		for (IRadarResult iRadarResult : res) {
			IRadarResult.Types type = iRadarResult.getObjectType();

			if (type == IRadarResult.Types.Wreck && isHeading(iRadarResult.getObjectDirection())) {
				System.out.println(">" + id + "j 'essaye d'eviter WRECK");
				moveBack();
				avoid = true;
				return;
			}

			// Si je vois un ennemi je lui tire dessus tout en reculant
			if (type == IRadarResult.Types.OpponentMainBot || type == IRadarResult.Types.OpponentSecondaryBot) {
				if (shootEnemy % 4 == 0)
					moveBack();
				else
					fire(iRadarResult.getObjectDirection());
				shootEnemy++;
				return;
			}

			// S'il y a une balle en ma direction je tire aussi
			double objDirection = iRadarResult.getObjectDirection();
			if (type == IRadarResult.Types.BULLET && isHeading(objDirection)) {
				if (shoot % 3 == 0)
					moveBack();
				else
					fire(objDirection);
				// if (rand.nextBoolean())
				// avoid = true;
				shoot++;
				return;
			}
		}

		// Si je vois personne à l'horizon je bouge
		if (nobody)
			front = true;

		// S'il y a un mur je recule
		if (frontType == IFrontSensorResult.Types.WALL) {
			turning = true;
		} else if (frontType == IFrontSensorResult.Types.TeamMainBot
				|| frontType == IFrontSensorResult.Types.TeamSecondaryBot) {
			System.out.println("> " + id + " front: " + frontType);
			avoid = true;
			shoot = 1; // pas tirer sur ses partenaires
		}

		// Soit je tire soit je bouge
		if (shoot % 3 == 0) {
			// fire(getShootingAngle());
			move();
		} else {
			if (front)
				move();
			else
				moveBack();
		}
		shoot++;
	}

	public double getShootingAngle() {
		double angle = rand.nextDouble() < 0.5 ? rand.nextDouble() + getHeading() : getHeading() - rand.nextDouble();
		ArrayList<IRadarResult> res = detectRadar();
		for (IRadarResult e : res) {
			IRadarResult.Types type = e.getObjectType();

			if (type == IRadarResult.Types.TeamMainBot || type == IRadarResult.Types.TeamSecondaryBot)
				if (isHeading(e.getObjectDirection(), angle))
					angle = angle + Math.PI / 8;
		}
		return angle;
	}

	private boolean isHeading(double dir) {
		return Math.abs(Math.sin(getHeading() - dir)) < HEADINGPRECISION;
	}

	private boolean isHeading(double dir, double heading) {
		return Math.abs(Math.sin(heading - dir)) < HEADINGPRECISION;
	}

	public boolean isEndTurn(double val) {
		return Math.abs(Math.sin((getHeading() % val) - val)) < HEADINGPRECISION
				|| Math.abs(Math.sin(getHeading() % val)) < HEADINGPRECISION;
	}
}
