package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;

//this class does several things:
//	-renders entities (+their hitboxes)
//	-ticks entities
//	-deletes entities if they no longer exist
//	-checks for collisions between entities as appropriate
//	-notifies entities of relevant collisions

public class EntityManager {
	
	Player player;
	private List<Entity> allEntities;
	private List<CannonBall> allCanBalls;
	private List<Pirate> allPirates;
	private List<Entity> entitiesToRemove;
	private boolean showHitboxes;
	private SpriteBatch batch;
	
	private ShapeRenderer hitboxRenderer;
	
	public EntityManager(boolean showHitboxes) {
		allEntities = new ArrayList<Entity>();
		allCanBalls = new ArrayList<CannonBall>();
		allPirates = new ArrayList<Pirate>();
		entitiesToRemove = new ArrayList<Entity>();
		batch = new SpriteBatch();
		this.showHitboxes = showHitboxes;
		
		//hitboxes
		if(showHitboxes) {
			hitboxRenderer = new ShapeRenderer();
			hitboxRenderer.setColor(Color.BLUE);
		}
	}
	
	public void render(Matrix4 projectionMatrix) {
		batch.setProjectionMatrix(projectionMatrix);
		batch.begin();
		for(Entity e : allEntities)
			e.draw(batch);
		batch.end();
		
		//draw hitboxes if enabled
		if(showHitboxes) {
			hitboxRenderer.setProjectionMatrix(projectionMatrix);
			hitboxRenderer.begin(ShapeType.Line);
			for(Entity e : allEntities)
				e.drawHitbox(hitboxRenderer);
			hitboxRenderer.end();
		}
	}
	

	public void process() {
		//process collisions
		this.processCollisions();
		
		//check for deleted entities and tick the others
		entitiesToRemove.clear();
		for(Entity e : allEntities) {
			if(e.exists)
				e.tick();
			else
				entitiesToRemove.add(e);
		}
		
		//delete entities which no longer exist
		for(Entity e : entitiesToRemove)
			this.remove(e);
	}
	
	//Performs the following checks each frame: 
	//	1. Player against every pirate. this starts the "you have been boarded" event
	//	2. Every cannon ball against every pirate. this deletes cannonball and calls the strike() event on the pirate
	//	   (eventually, cannon balls will be checked against player as well as villages. Pirate villages will fire at player and can be attacked)
	//When this method gets too expensive we will have to start only checking entities against entities in the same quadrant or something
	private void processCollisions() {
		for(Pirate p : this.allPirates) {
			//1.
			if(Intersector.overlapConvexPolygons(player.getHitbox(), p.getHitbox())) {
				System.out.println("You have been boarded!!");
			}
			//2.
			for(CannonBall c : this.allCanBalls) {
				if(Intersector.overlapConvexPolygons(c.getHitbox(), p.getHitbox())) {
					c.exists = false;
					System.out.println("You hit a pirate!");
					p.strike();
				}
			}
		}
	}

	public void add(Entity e) {
		allEntities.add(e);
		if(e instanceof Player) {
			this.player = (Player) e;
		}
		else if(e instanceof Pirate) {
			this.allPirates.add((Pirate) e);
		}
		else if(e instanceof CannonBall) {
			this.allCanBalls.add((CannonBall) e);
		}
	}
	
	public void addAll(List<Entity> el) {
		for(Entity e : el)
			this.add(e);
	}
	
	public void remove(Entity e) {
		this.allEntities.remove(e);
		if(e instanceof Player) {
			this.player = null;
		}
		else if(e instanceof Pirate) {
			this.allPirates.remove((Pirate) e);
		}
		else if(e instanceof CannonBall) {
			this.allCanBalls.remove((CannonBall) e);
		}
	}
	
	public void dispose() {
		batch.dispose();
	}
}
