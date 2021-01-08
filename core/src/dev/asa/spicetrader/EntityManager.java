package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

//this class does several things:
//	-renders entities (+their hitboxes)
//	-ticks entities
//	-deletes entities if they no longer exist
//	-checks for collisions between entities as appropriate
//	-notifies entities of relevant collisions

public class EntityManager {
	
	private Player player;
	private List<Entity> allEntities;
	private List<CannonBall> allCanBalls;
	private List<Pirate> allPirates;
	private List<Village> allVillages;
	private List<Entity> entitiesToRemove;
	private List<Entity> entitiesToAdd;
	private ShapeRenderer hitboxRenderer;
	private MenuManager menuManager;
	private MainGame game;
	private SpiceTraderMap map;
	private Camera camera;
	
	private List<Vector2> pirateGoals;
	
	public EntityManager(boolean showHitboxes, MenuManager menuManager, MainGame game, SpiceTraderMap map, Camera camera) {
		this.menuManager = menuManager;
		this.game = game;
		this.camera = camera;
		this.map = map;
		
		allEntities = new ArrayList<Entity>();
		allCanBalls = new ArrayList<CannonBall>();
		allPirates = new ArrayList<Pirate>();
		allVillages = new ArrayList<Village>();
		entitiesToRemove = new ArrayList<Entity>();
		entitiesToAdd = new ArrayList<Entity>();
		
		//for showing hitboxes
		hitboxRenderer = new ShapeRenderer();
		hitboxRenderer.setColor(Color.BLUE);
		
		//for making pirates separate
		pirateGoals = new ArrayList<Vector2>();
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
	

	public void process(boolean paused) {
		if(paused)
			return;
		
		//sync camera to player position
		Vector2 playerPos = player.getHitCenter();
		camera.position.x = playerPos.x;
		camera.position.y = playerPos.y;
		//drunk mode
		//camera.rotate(1, 0, 0, 1);
		
		//check if player is dead
		if(player.isDead()) {
			Menu boarded = MenuFactory.createMenu(menuManager, "BoardedMenu");
			menuManager.openMenu(boarded);
		}
		
		//clear pirate goals
		pirateGoals.clear();
		
		//add any entities that need to be added
		addAll(entitiesToAdd);
		entitiesToAdd.clear();
		
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
			remove(e);
		
		//process collisions
		processCollisions();
	}
	
	//Performs the following checks each frame: 
	//	1. Every pirate against player. this collision bounces back pirate and damages player
	//	2. Every pirate against every cannon ball. this deletes cannon ball and calls the strike() event on the pirate
	//	   (eventually, cannon balls will be checked against player as well as villages. Pirate villages will fire at player and can be attacked)
	//	3. Every dock against player. When a player is in dock hit box, the dockable variable on player should be set to the appropriate village.
	//When this method gets too expensive we will have to start only checking entities against entities in the same quadrant or something
	//NOTE: collisions between ships and the map are handled in the validShipPosition method in the map class, which should always be called when a ship moves
	private void processCollisions() {
		for(Pirate p : allPirates) {
			if(!p.isDead()) {
				//1.
				if(Intersector.overlapConvexPolygons(player.getHitbox(), p.getHitbox())) {
					player.strike(1);
					p.bounceBack();
				}
				//2.
				for(CannonBall c : allCanBalls) {
					if(Intersector.overlapConvexPolygons(c.getHitbox(), p.getHitbox())) {
						c.exists = false;
						p.strike(c.getDamage());
					}
				}
			}
		}
		//3.
		Village dockable = null;
		for(Village v : allVillages) {
			if(Intersector.overlapConvexPolygons(v.getDockHitbox(), player.getHitbox())) {
				dockable = v;
			}
		}
		player.setDockable(dockable);
	}
	
	//checks if other pirates share this goal this frame and moves or deletes it if so
	public Vector2 avoidOtherPirates(Vector2 currGoal) {
		//if goal is not shared, return it and add it to list
		if(!pirateGoals.contains(currGoal)) {
			pirateGoals.add(currGoal);
			return currGoal;
		}
		//otherwise pirate cant move this frame
		else 
			return null;
	}
	
	public void addNextTick(Entity e) {
		entitiesToAdd.add(e);
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
		e.setManager(this);
	}
	
	public void addAll(List<Entity> el) {
		for(Entity e : el) {
			add(e);
		}
	}
	
	public void remove(Entity e) {
		allEntities.remove(e);
		if(e instanceof Player) {
			player = null;
		}
		else if(e instanceof Pirate) {
			allPirates.remove((Pirate) e);
		}
		else if(e instanceof CannonBall) {
			allCanBalls.remove((CannonBall) e);
		}
	}
}
