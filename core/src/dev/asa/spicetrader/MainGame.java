package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MainGame extends ApplicationAdapter {
	
//	--GAME SETTINGS--
	
	//TODO: MAKE THIS VARIABLE CONTROL MAP HITBOXES
	final boolean SHOW_HITBOXES = true;
	//fixes screen tearing
	final boolean ROUND_CAMERA_POS = false;
	final int TILE_WIDTH = 16;
	final int TILE_HEIGHT = 16;
	final float ZOOM_LEVEL = 3;
	//map settings
	final int MAP_SIZE = 64;//use even numbers plz
	final int SMOOTHING_ITERATIONS = 5;
	final int SEA_LEVEL_OFFSET = 2;


//	--GAME VARIABLES--
	
	private Vector2 screenSize;
	private Vector2 screenCenter;
	int frameCounter = 0;
	
	//rendering objects
	SpriteBatch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	ShapeRenderer hitboxRenderer;
	
	//game objects
	SpiceTraderMap map;
	Player player;
	//tracks all entities in game. ships, cannon balls, etc. must be added to this list when they are created. 
	//used to render and update everything
	List<Entity> allEntities;
	//tracks which entities need to be remove. is cleared each frame
	List<Entity> entitiesToRemove;
	
	@Override
	public void create () {
		screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCenter = new Vector2((float) (MAP_SIZE * TILE_WIDTH * 0.5), (float) (MAP_SIZE * TILE_HEIGHT * 0.5));
		
		//rendering objects
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenSize.x / ZOOM_LEVEL, screenSize.y / ZOOM_LEVEL);
		camera.position.x = screenCenter.x;
		camera.position.y = screenCenter.y;
		atlas = new TextureAtlas("assets/textures.atlas");
		batch = new SpriteBatch();
		
		//hitboxes
		if(SHOW_HITBOXES) {
			hitboxRenderer = new ShapeRenderer();
			hitboxRenderer.setColor(Color.BLUE);
		}
		
		//map
		SpiceTraderMapGenerator mapGen = new SpiceTraderMapGenerator(atlas);
		try {
			map = mapGen.generateMap(MAP_SIZE, TILE_WIDTH, TILE_HEIGHT, SMOOTHING_ITERATIONS, SEA_LEVEL_OFFSET);
		} catch (Exception e) {
			//in case of bad map settings or mapGen unable to find valid map
			e.printStackTrace();
			this.dispose();
			System.exit(0);
		}

		//Entities
		allEntities = new ArrayList<Entity>();
		EntityFactory entFactory = new EntityFactory(atlas, map, screenCenter, allEntities);
		entitiesToRemove = new ArrayList<Entity>();
		
		//player
		player = entFactory.getPlayer();
		
		//pirates
		entFactory.addPiratesRandomly(10);
		
		//debug
		System.out.println(" *** SCREEN SIZE: " + screenSize.x + "x" + screenSize.y + " ***");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.05f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//render map
		map.render(camera);
		
		//render entities
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Entity e : allEntities) 
			e.draw(batch);
		
		batch.end();
		
		//draw hitboxes if enabled - note: drawing of map hitboxes is always enabled for now.
		if(SHOW_HITBOXES) {
			hitboxRenderer.setProjectionMatrix(camera.combined);
			hitboxRenderer.begin(ShapeType.Line);
			for(Entity e : allEntities)
				e.drawHitbox(hitboxRenderer);
			hitboxRenderer.end();
		}
		map.setProjectionMatrix(camera.combined);
		
		//handle input
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			Vector2 playerPos = player.moveForward();
			camera.position.x = playerPos.x;
			camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			Vector2 playerPos = player.moveBackward();
			camera.position.x = playerPos.x;
			camera.position.y = playerPos.y;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.turnCW();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.turnCCW();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			Sprite cannonBallSprite = atlas.createSprite("ships/cannon_ball");
			allEntities.add(player.fireCannonLeft(cannonBallSprite));
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			Sprite cannonBallSprite = atlas.createSprite("ships/cannon_ball");
			allEntities.add(player.fireCannonRight(cannonBallSprite));
		}
		
		//process all entities, deleting if they don't exist anymore and updating if they do
		entitiesToRemove.clear();
		for(Entity e : allEntities) {
			if(e.exists)
				e.tick();
			else
				entitiesToRemove.add(e);
		}
		for(Entity e : entitiesToRemove)
			allEntities.remove(e);
		
		//round camera position to nearest 1/zoom_level of a pixel - this fixes screen tearing but introduces a weird jiggling effect
		if(ROUND_CAMERA_POS) {
			camera.position.x = Utils.roundToNearestFraction(camera.position.x, ZOOM_LEVEL);
			camera.position.y = Utils.roundToNearestFraction(camera.position.y, ZOOM_LEVEL);
		}

		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		atlas.dispose();
	}
}
