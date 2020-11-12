package dev.asa.spicetrader;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

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
	
	//rendering objects
	TextureAtlas atlas;
	OrthographicCamera camera;
	
	//game objects
	SpiceTraderMap map;
	EntityManager entManager;
	EntityFactory entFactory;
	InputHandler inputHandler;
	
	@Override
	public void create () {
		screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCenter = new Vector2((float) (MAP_SIZE * TILE_WIDTH * 0.5), (float) (MAP_SIZE * TILE_HEIGHT * 0.5));
		System.out.println(" *** SCREEN SIZE: " + screenSize.x + "x" + screenSize.y + " ***");
		
		//rendering objects
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenSize.x / ZOOM_LEVEL, screenSize.y / ZOOM_LEVEL);
		camera.position.x = screenCenter.x;
		camera.position.y = screenCenter.y;
		atlas = new TextureAtlas("assets/textures.atlas");
		
		//Map
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
		entManager = new EntityManager(SHOW_HITBOXES);
		entFactory = new EntityFactory(atlas, map, screenCenter, entManager);

		//player
		Player player = entFactory.getPlayer();
		
		//pirates
		entFactory.addPiratesRandomly(10);
		
		//input
		inputHandler = new InputHandler(player, camera, entManager);		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.05f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//render and tick map
		map.render(camera);
		map.tick();
		
		//render and process entities
		entManager.render(camera.combined);
		entManager.process();
		
		//handle input
		inputHandler.process();
		
		//round camera position to nearest 1/zoom_level of a pixel - this fixes screen tearing but introduces a weird jiggling effect
		if(ROUND_CAMERA_POS) {
			camera.position.x = Utils.roundToNearestFraction(camera.position.x, ZOOM_LEVEL);
			camera.position.y = Utils.roundToNearestFraction(camera.position.y, ZOOM_LEVEL);
		}

		camera.update();
	}
	
	@Override
	public void dispose () {
		entManager.dispose();
		atlas.dispose();
	}
}
