package org.dyn4j;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

public abstract class SimulationFrame extends JFrame
{

	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw to */
	protected final Canvas canvas;
	
	/** The dynamics engine */
	protected final World world;
	
	/** The pixels per meter scale factor */
	protected final double scale;
	
	/** True if the simulation is exited */
	private boolean stopped;
	
	/** True if the simulation is paused */
	private boolean paused;
	
	/** The time stamp for the last iteration */
	private long last;
	
	/**
	 * Constructor.
	 * <p>
	 * By default creates a 800x600 canvas.
	 * @param name the frame name
	 * @param scale the pixels per meter scale factor
	 */
	public SimulationFrame(String name, double scale) 
	{
		super(name);
		
		// set the scale
		this.scale = scale;
		
		// create the world
		this.world = new World();
		
		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add a window listener
		this.addWindowListener(new WindowAdapter() 
		{
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) 
			{
				// before we stop the JVM stop the simulation
				stop();
				super.windowClosing(e);
			}
		});
		
		// create the size of the window
		Dimension size = new Dimension(1300, 700);
		
		// create a canvas to paint to 
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		
		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);
		
		// size everything
		this.pack();
		
		// setup the world
		this.initializeWorld();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected abstract void initializeWorld();
	
	/**
	 * Start active rendering the simulation.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	private void start()
	{
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT
		Thread thread = new Thread()
		{
			public void run() 
			{
				// perform an infinite loop stopped
				// render as fast as possible
				while (!isStopped()) 
				{
					if (!paused) 
					{
						gameLoop();
					}
					// you could add a Thread.yield(); or
					// Thread.sleep(long) here to give the
					// CPU some breathing room
					/*try {
						Thread.sleep(5);
					} catch (InterruptedException e) {}*/
				}
			}
		};
		// set the game loop thread to a daemon thread so that
		// it cannot stop the JVM from exiting
		thread.setDaemon(true);
		// start the game loop
		thread.start();
	}
	
	/**
	 * The method calling the necessary methods to update
	 * the game, graphics, and poll for input.
	 */
	private void gameLoop() 
	{
		// get the graphics object to render to
		Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
		
		// by default, set (0, 0) to be the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up
		//this.transform(g);
		
		// reset the view
		this.clear(g);
		
		// get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
    	// convert from nanoseconds to seconds
    	double elapsedTime = (double)diff ;
		
		// render anything about the simulation (will render the World objects)
		this.render(g, elapsedTime);
        
        // update the World
        this.update(g, elapsedTime);
		
		// dispose of the graphics object
		g.dispose();
		
		// blit/flip the buffer
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost())
		{
			strategy.show();
		}
		
		// Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Performs any transformations to the graphics.
	 * <p>
	 * By default, this method puts the origin (0,0) in the center of the window
	 * and points the positive y-axis pointing up.
	 * @param g the graphics object to render to
	 */
	
	
	/**
	 * Clears the previous frame.
	 * @param g the graphics object to render to
	 */
	protected void clear(Graphics2D g) 
	{
		final int w = this.canvas.getWidth();
		final int h = this.canvas.getHeight();
		
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.BLUE);
		g.fillRect(90, 80, 1115, 540);
		g.setColor(Color.WHITE);
		g.drawLine(632, 80, 632, 610);
		g.drawOval(582, 310, 100, 100);
		g.fillRect(1120, 225, 1130, 260);
		g.fillRect(85, 225, 95, 260);
	}
	
	/**
	 * Renders the example.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 */
	protected void render(Graphics2D g, double elapsedTime)
	{
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Point prev = MouseInfo.getPointerInfo().getLocation();
		double last = System.nanoTime();
		SimulationBody puck = (SimulationBody) this.world.getBody(5);
		SimulationBody mallet1 = (SimulationBody) this.world.getBody(4);
		
		Vector2 puckCentre = puck.getWorldCenter();
		Point pC = new Point((int) puckCentre.x,(int) puckCentre.y);
		Vector2 malletCentre = mallet1.getWorldCenter();
		Point mC = new Point((int) malletCentre.x,(int) malletCentre.y);
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) 
		{
			// get the object
			SimulationBody body = (SimulationBody) this.world.getBody(i);
				
			Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		
			if(i==4 && pC.distance(mC)<=60)
			{
				Vector2 force = new Vector2((pC.x-mC.x)*1000000,(pC.y-mC.y)*1000000);
				for(int c=0;c<10;c++)
					puck.applyForce(force);
			}
			
			else if(i == 4 && mousePoint.y >= 150 && mousePoint.y <= 630 && mousePoint.x >= 180 && mousePoint.x <= 1230)
			{
				double time = System.nanoTime();
		        double diff = time - last;	
		        
		        if(!(mousePoint.distance(pC)<=60))
				{
		        	mallet1.translateToOrigin();
		        	mallet1.translate(mousePoint.x - 55, mousePoint.y - 40);
					mallet1.setLinearVelocity((mousePoint.x - prev.x)/diff * 1000, (mousePoint.y - prev.y)/diff * 1000);
				}
		        
		        else
		        {
		        	Vector2 force = new Vector2((pC.x-mC.x)*1000000,(pC.y-mC.y)*1000000);
					for(int c=0;c<10;c++)
						puck.applyForce(force);
					
		        }
				
				prev.x = mousePoint.x;
				prev.y = mousePoint.y;
				last = time;
			}
			
			//draw the object
			this.render(g, elapsedTime, body);
		}
	}
	
	/**
	 * Renders the body.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 * @param body the body to render
	 */
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body)
	{
		// draw the object
		body.render(g, this.scale);
	}
	
	/**
	 * Updates the world.
	 * @param g the graphics object to render to
	 * @param elapsedTime the elapsed time from the last update
	 */
	protected void update(Graphics2D g, double elapsedTime)
	{
        // update the world with the elapsed time
        this.world.update(elapsedTime);
	}
	
	/**
	 * Stops the simulation.
	 */
	public synchronized void stop() 
	{
		this.stopped = true;
	}
	
	/**
	 * Returns true if the simulation is stopped.
	 * @return boolean true if stopped
	 */
	public boolean isStopped()
	{
		return this.stopped;
	}
	
	/**
	 * Pauses the simulation.
	 */
	public synchronized void pause() {
		this.paused = true;
	}
	
	/**
	 * Returns true if the simulation is paused.
	 * @return boolean true if paused
	 */
	public boolean isPaused() 
	{
		return this.paused;
	}
	
	/**
	 * Starts the simulation.
	 */
	public void run()
	{
		// set the look and feel to the system look and feel
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		
		// show it
		this.setVisible(true);
		
		// start it
		this.start();
	}
}
