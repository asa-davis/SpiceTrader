package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MainGame extends ApplicationAdapter {
	
//	--GAME SETTINGS--
	
	static final boolean SHOW_HITBOXES = true;
	static final boolean SHOW_GRID = true;
	//fixes texture bleeding?
	static final boolean ROUND_CAMERA_POS = false;
	static final int TILE_WIDTH = 16;
	static final int TILE_HEIGHT = 16;
	static final float ZOOM_LEVEL = 3;
	//map settings
	static final int MAP_SIZE = 100;//use even numbers plz - greater than 32
	static final int SMOOTHING_ITERATIONS = 5;
	static final int SEA_LEVEL_OFFSET = 2;
	static final int NUM_VILLAGES = 4;
	static final int NUM_PIRATES = 30;


//	--GAME VARIABLES--
	
	private Vector2 screenSize;
	private Vector2 screenCenter;
	private boolean paused;
	
	//rendering objects
	private SpriteBatch batch;
	private TextureAtlas atlas;
	private OrthographicCamera camera;
	
	//game objects
	private SpiceTraderMap map;
	private EntityManager entManager;
	private EntityFactory entFactory;
	private InputHandler inputHandler;
	private SpiceTraderMapGenerator mapGen;
	private MenuManager menuManager;
	
	//fonts: smallest to largest
	private BitmapFont[] fonts;
	
	@Override
	public void create () {
		paused = false;
		
		screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCenter = new Vector2((float) (MAP_SIZE * TILE_WIDTH * 0.5), (float) (MAP_SIZE * TILE_HEIGHT * 0.5));
		System.out.println(" *** SCREEN SIZE: " + screenSize.x + "x" + screenSize.y + " ***");
		
		//rendering objects
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenSize.x / ZOOM_LEVEL, screenSize.y / ZOOM_LEVEL);
		camera.position.x = screenCenter.x;
		camera.position.y = screenCenter.y;
		atlas = new TextureAtlas("assets/textures.atlas");
		batch = new SpriteBatch();
		
		//font
		fonts = Utils.getPixelFonts();
		
		//Map
		mapGen = new SpiceTraderMapGenerator(atlas);
		try {
			map = mapGen.generateMap(MAP_SIZE, TILE_WIDTH, TILE_HEIGHT, SMOOTHING_ITERATIONS, SEA_LEVEL_OFFSET);
		} catch (Exception e) {
			//in case of bad map settings or mapGen unable to find valid map
			e.printStackTrace();
			Gdx.app.exit();
		}
		
		
		//menus
		menuManager = new MenuManager(atlas, screenSize, this, fonts); 
		
		//Entities
		entManager = new EntityManager(SHOW_HITBOXES, menuManager, this, camera);
		entFactory = new EntityFactory(atlas, map, screenCenter);
		List<Entity> allEnts = new ArrayList<Entity>();
		
		//player
		Player player = entFactory.getPlayer();
		allEnts.add(player);
		menuManager.setPlayer(player);
		
		//pirates
		List<Pirate> pirates = entFactory.getRandomPirates(NUM_PIRATES);
		allEnts.addAll(pirates);
		
		//villages
		VillageFactory villFac = new VillageFactory(map, atlas);
		try {
			List<Village> villages = villFac.getVillages(NUM_VILLAGES);
			allEnts.addAll(villages);
			map.addVillages(villages);
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
		
		entManager.addAll(allEnts);
		
		//input
		inputHandler = new InputHandler(player, entManager, menuManager, screenSize.y);		
	}

	@Override
	public void render () {
		Gdx.graphics.setTitle("" + Gdx.graphics.getFramesPerSecond());
		Gdx.gl.glClearColor(0.2f, 0.05f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update all game objects
		map.tick(paused);
		inputHandler.process(paused);
		entManager.process(paused);
			
		menuManager.tick();
		//round camera position to nearest 1/zoom_level of a pixel - this fixes screen tearing but introduces a weird jiggling effect
		if(ROUND_CAMERA_POS) {
			camera.position.x = Utils.roundToNearestFraction(camera.position.x, ZOOM_LEVEL);
			camera.position.y = Utils.roundToNearestFraction(camera.position.y, ZOOM_LEVEL);
		}
		camera.update();
		
		//render all game objects
		map.render(camera, SHOW_HITBOXES, SHOW_GRID);
		entManager.render(batch, camera, SHOW_HITBOXES);
		menuManager.draw(batch);
	}
	
	@Override
	public void dispose () {
		for(int i = 0; i < fonts.length; i++) {
			fonts[i].dispose();
		}
		batch.dispose();
		atlas.dispose();
	}
	
	@Override
	public void pause() {
		paused = true;
	}
	
	@Override
	public void resume() {
		paused = false;
	}
}
