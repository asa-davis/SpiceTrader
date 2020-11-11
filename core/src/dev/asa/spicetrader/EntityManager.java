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
	
	private List<Entity> allEntities;
	private List<Entity> entitiesToRemove;
	private boolean showHitboxes;
	private SpriteBatch batch;
	
	private ShapeRenderer hitboxRenderer;
	
	public EntityManager(boolean showHitboxes) {
		allEntities = new ArrayList<Entity>();
		entitiesToRemove = new ArrayList<Entity>();
		batch = new SpriteBatch();
		showHitboxes = showHitboxes;
		
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
	
	//ticks and deletes entities
	public void process() {
		entitiesToRemove.clear();
		for(Entity e : allEntities) {
			if(e.exists)
				e.tick();
			else
				entitiesToRemove.add(e);
		}
		for(Entity e : entitiesToRemove)
			allEntities.remove(e);
	}
	
	public void add(Entity e) {
		allEntities.add(e);
	}
	
	public void addAll(List<Entity> e) {
		allEntities.addAll(e);
	}
	
	//currently we check for collisions against every entity in game every time. with many cannon balls and ships in the game, this will become too inefficient.
	//in the future, the map should be divided into quadrants, and collision checks should only be performed against entities in the same quadrant
	public List<Entity> getCollisions(Entity e1) {
		List<Entity> collisions = new ArrayList<Entity>();
		//for every entity
		for(Entity e2 : allEntities) {
			//check collisions except for against the entity that was passed to this method
			if(!e2.equals(e1)) {
				if(Intersector.overlapConvexPolygons(e2.getHitbox(), e1.getHitbox())) {
					collisions.add(e2);
				}
			}
		}
		return collisions;
	}
	
	public void dispose() {
		batch.dispose();
	}
}
