package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;

//this class will track all entities and tell them when they collide.
//RULES
//	-Whenever an entity is created, it must tell the manager and pass it's hitbox
//	-Whenever an entity moves, it must tell the manager. 
//	-Whenever an entity collides with another entity, both must be notified and passed references to eachother. 
//	-A ship may ask the manager what collisions would occur if it occupied a certain space. pirates will do this to avoid running into other pirates.
//	-When a cannon ball collides with anything, it should delete itself.
//	-When a ship collides with a cannon ball, it should damage itself.
//	-When a player collides with a pirate, the "you have been boarded!" menu appears and the game pauses. If the player fails to fight off the pirates they lose/game over

//currently we check for collisions against every entity in game every time. with many cannon balls and ships in the game, this will become too inefficient.
//in the future, the map should be divided into quadrants, and collision checks should only be performed against entities in the same quadrant

public class EntityCollisionManager {
	
	private List<Entity> entities;
	
	public EntityCollisionManager() {
		entities = new ArrayList<Entity>();
	}
	
	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	
	public List<Entity> getCollisions(Entity e1) {
		List<Entity> collisions = new ArrayList<Entity>();
		//for every entity
		for(Entity e2 : this.entities) {
			//check collisions except for against the entity that was passed to this method
			if(!e2.equals(e1)) {
				if(Intersector.overlapConvexPolygons(e2.getHitbox(), e1.getHitbox())) {
					collisions.add(e2);
				}
			}
		}
		return collisions;
	}
}
