/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import java.util.ArrayList;
import java.util.Random;

import robotsimulator.Brain;
import characteristics.IRadarResult;
import characteristics.IFrontSensorResult;
import characteristics.Parameters.Direction;

public class BrainMainCanevas extends Brain {
	// ---VARIABLES---//
	private boolean shoot;
	private Random rand;
	private boolean front;
	private int nbFront;
	private boolean descente, montee, turningLeft, turningRight;

	// ---CONSTRUCTORS---//
	public BrainMainCanevas() {
		super();
	}

	// ---ABSTRACT-METHODS-IMPLEMENTATION---//
	public void activate() {
		shoot = true;
		move();
		front = true;
		nbFront = 3000;
		rand = new Random();
		rand.setSeed(10);
		descente = true;
		montee = false;
		turningLeft = false;
		turningRight = true;
	}

	public void step() {
		ArrayList<IRadarResult> res = detectRadar();
		boolean rien = false;

		for (IRadarResult iRadarResult : res) {
			IRadarResult.Types type = iRadarResult.getObjectType();
			if (type == IRadarResult.Types.OpponentMainBot
					|| type == IRadarResult.Types.OpponentSecondaryBot
					|| type == IRadarResult.Types.BULLET) {
				fire(iRadarResult.getObjectDirection());
				return;
			}
		}
		IFrontSensorResult.Types front = detectFront().getObjectType();
		if (front == IFrontSensorResult.Types.WALL) {
			System.out.println("Dir: " + getHeading());
			if (descente) {
				// stepTurn(Direction.LEFT);
				// descente = false;
				// montee = true;
				// turningLeft = true;
				// turningRight = false;
				if(getHeading() <= 0) {
					stepTurn(Direction.RIGHT);
				} else {
					descente = false;
					montee = true;
					move();
				}
			} else {
				// stepTurn(Direction.RIGHT);
				// descente = true;
				// montee = false;
				// turningRight = true;
				// turningLeft = false;
				if(getHeading() >= 0) {
					stepTurn(Direction.LEFT);
				} else {
					descente = true;
					montee = false;
					move();
				}
			}
		} else {
			System.out.println("tL " + turningLeft + " tR " + turningRight);
			if (turningLeft) {
				if (getHeading() <= -0.5 * Math.PI) {
					stepTurn(Direction.LEFT);
				} else {
					turningLeft = false;
					move();
				}
				return;
			}
			if (turningRight) {
				if (getHeading() <= 0.5 * Math.PI) {
					stepTurn(Direction.RIGHT);
				} else {
					turningRight = false;
					move();
				}
				return;
			}
			move();
		}
		/*
		 * if (!type.toString().equals("TeamMainBot") &&
		 * !type.toString().equals("TeamSecondaryBot") &&
		 * !type.toString().equals("BULLET")) System.out.println(type); if (type
		 * == IRadarResult.Types.OpponentMainBot || type ==
		 * IRadarResult.Types.OpponentSecondaryBot) {
		 * fire(iRadarResult.getObjectDirection()); return; } else { rien =
		 * true; }
		 */
		/*
		 * if (rien) { nbFront--; if (front) move(); else moveBack(); if
		 * (nbFront == 0) { nbFront = 3000; front = !front; } }
		 */

		shoot = !shoot;
		rien = false;
	}

	public double getShootingAngle() {
		return rand.nextDouble() < 0.5 ? rand.nextDouble() + getHeading()
				: getHeading() - rand.nextDouble();
	}
}
