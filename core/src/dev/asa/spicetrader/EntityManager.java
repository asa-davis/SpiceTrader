package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	private List<Village> allVillages;
	private List<Entity> entitiesToRemove;
	private ShapeRenderer hitboxRenderer;
	MenuManager menuManager;
	MainGame game;
	
	public EntityManager(boolean showHitboxes, MenuManager menuManager, MainGame game) {
		allEntities = new ArrayList<Entity>();
		allCanBalls = new ArrayList<CannonBall>();
		allPirates = new ArrayList<Pirate>();
		allVillages = new ArrayList<Village>();
		entitiesToRemove = new ArrayList<Entity>();
		this.menuManager = menuManager;
		this.game = game;
		
		//for showing hitboxes
		hitboxRenderer = new ShapeRenderer();
		hitboxRenderer.setColor(Color.BLUE);
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera, boolean showHitboxes) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Entity e : allEntities)
			e.draw(batch);
		batch.end();
		
		//draw hitboxes if enabled
		if(showHitboxes) {
			hitboxRenderer.setProjectionMatrix(batch.getProjectionMatrix());
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
	//	1. Player against every pirate. this starts the "you have been boarded" event and pauses gameplay
	//	2. Every cannon ball against every pirate. this deletes cannonball and calls the strike() event on the pirate
	//	   (eventually, cannon balls will be checked against player as well as villages. Pirate villages will fire at player and can be attacked)
	//When this method gets too expensive we will have to start only checking entities against entities in the same quadrant or something
	private void processCollisions() {
		for(Pirate p : this.allPirates) {
			//1.
			if(Intersector.overlapConvexPolygons(player.getHitbox(), p.getHitbox())) {
				game.pause();
				menuManager.showBoardedMenu();
			}
			//2.
			for(CannonBall c : this.allCanBalls) {
				if(Intersector.overlapConvexPolygons(c.getHitbox(), p.getHitbox())) {
					c.exists = false;
					p.strike();
				}
			}
		}
	}

	public void add(Entity e) {
		allEntities.add(e);
		if(e instanceof Player)
			player = (Player) e;
		if(e instanceof CannonBall)
			allCanBalls.add((CannonBall) e);
		if(e instanceof Pirate)
			allPirates.add((Pirate) e);
		if(e instanceof Village)
			allVillages.add((Village) e);
	}
	
	public void addAll(List<Entity> el) {
		for(Entity e : el) {
			this.add(e);
		}
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
}
