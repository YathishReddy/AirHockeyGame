package org.dyn4j;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;

import org.dyn4j.ExampleGraphics2D.GameObject;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

public final class Billiards extends SimulationFrame 
{

	public Billiards() 
	{
		super("Air Hockey 2K16", 1.0);
	}
	
	protected void initializeWorld()
	{

		this.world.setGravity(World.ZERO_GRAVITY);
				
		SimulationBody up = new SimulationBody(Color.darkGray);
		up.addFixture(Geometry.createRectangle(1125, 22.5));
		up.translate(652.5, 80);
		up.setMass(MassType.INFINITE);
		world.addBody(up);
		
		SimulationBody bottom = new SimulationBody(Color.darkGray);
		bottom.addFixture(Geometry.createRectangle(1125, 22.5));
		bottom.translate(652.5, 620);
		bottom.setMass(MassType.INFINITE);
		world.addBody(bottom);
		
		SimulationBody left = new SimulationBody(Color.darkGray);
		left.addFixture(Geometry.createRectangle(22.5, 517.5));
		left.translate(101.25, 350);
		left.setMass(MassType.INFINITE);
		world.addBody(left);
		
		SimulationBody right = new SimulationBody(Color.darkGray);
		right.addFixture(Geometry.createRectangle(22.5, 517.5));
		right.translate(1203.75, 350);
		right.setMass(MassType.INFINITE);
		world.addBody(right);
	
		
		SimulationBody mallet1 = new SimulationBody(Color.gray);
		mallet1.addFixture(Geometry.createCircle(38.25), 200, 1, 1);
		mallet1.translate(180.25, 350);
		mallet1.setLinearVelocity(30000, 40000);
		mallet1.setMass(MassType.NORMAL);
		mallet1.setLinearDamping(0.0);
		mallet1.setAngularDamping(0.0);
		mallet1.setAutoSleepingEnabled(false);
		this.world.addBody(mallet1);
		
		SimulationBody puck = new SimulationBody(Color.red);
		puck.addFixture(Geometry.createCircle(20), 1, 1, 1);
		puck.translate(1005, 350);
		puck.setLinearVelocity(-450, 1450); 		  
		puck.setMass(MassType.NORMAL);
		puck.setLinearDamping(0.0);
		puck.setAngularDamping(0.0);
		puck.setAutoSleepingEnabled(false);
		this.world.addBody(puck);
	}

	protected void render(Graphics2D g, double elapsedTime)
	{
		g.translate(0, 0);
		super.render(g, elapsedTime);
	}
	
	public static void main(String[] args) 
	{
		Billiards simulation = new Billiards();
		simulation.run();
	}
}
