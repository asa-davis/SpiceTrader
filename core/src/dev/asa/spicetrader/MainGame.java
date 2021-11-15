package dev.asa.spicetrader;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import dev.asa.spicetrader.UI.MenuManager;
import dev.asa.spicetrader.items.ItemFactory;
import dev.asa.spicetrader.entities.LandEntity;
import dev.asa.spicetrader.map.SpiceTraderMap;
import dev.asa.spicetrader.map.SpiceTraderMapGenerator;
import dev.asa.spicetrader.entities.Player;
import dev.asa.spicetrader.entities.Entity;
import dev.asa.spicetrader.entities.EntityFactory;
import dev.asa.spicetrader.entities.EntityManager;

public class MainGame extends ApplicationAdapter {
	
//	--GAME SETTINGS--
	
	private enum DisplayMode { WINDOWED, FULLSCREEN, BORDERLESS_WINDOWED}
	static final DisplayMode DISPLAY_MODE = DisplayMode.WINDOWED;
	
	static final boolean SHOW_HITBOXES = false;
	static final boolean SHOW_GRID = false;
	static final boolean ROUND_CAMERA_POS = true;	//fixes texture bleeding but makes player sprite appear to shake
	
	static final int TILE_WIDTH = 16;
	static final int TILE_HEIGHT = 16;
	static final float ZOOM_LEVEL = 3;	//use 3 for gameplay
	
	//map settings
	static final int MAP_SIZE = 300; //400 seems pretty good for games.
	static final int SMOOTHING_ITERATIONS = 5;
	static final int SEA_LEVEL_OFFSET = 2;
	static final int VILLAGE_RATIO = 7;							//higher = less villages
	static final int MERCHANT_RATIO = 5;
	static final int SHOP_RATIO = 15;
	static final int MIN_DIST_BETWEEN_VILLAGE_MERCHANT = 8;
	static final float MIN_PIRATEVILLAGE_PROBABILITY = 0.1f; 	//probability of pirate villages generating near center of map
	static final float MAX_PIRATEVILLAGE_PROBABILITY = 0.85f; 	//probability of pirate villages generating near edges of map
	static final int MIN_DIST_BETWEEN_PIRATE_FRIENDLY = 4;		//min dist in tiles between pirate villages and friendly villages

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
	private InputHandler inputHandler;
	private SpiceTraderMapGenerator mapGen;
	private MenuManager menuManager;
	
	//fonts: smallest to largest
	private BitmapFont[] fonts;
	
	@Override
	public void create () {

		//handle display setting
		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
		if (DISPLAY_MODE == DisplayMode.FULLSCREEN) {
		    Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
		    Gdx.graphics.setFullscreenMode(mode);
		} else if (DISPLAY_MODE == DisplayMode.BORDERLESS_WINDOWED) {
		    Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
		}
		
		paused = false;
		
		//fetch screen size, compute screen center
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

		//Items
		ItemFactory itemFactory = new ItemFactory(atlas);

		//Entities
		List<Entity> allEnts = new ArrayList<Entity>();
		EntityFactory entFac = new EntityFactory(map, atlas, screenCenter, itemFactory);
		
		//player
		Player player = entFac.createPlayer();
		allEnts.add(player);

		//landEntities
		List<LandEntity> landEntities = new ArrayList<>();
		landEntities.addAll(entFac.createVillages(VILLAGE_RATIO));
		landEntities.addAll(entFac.createMerchants(MERCHANT_RATIO, MIN_DIST_BETWEEN_VILLAGE_MERCHANT));
		landEntities.addAll(entFac.createShops(SHOP_RATIO));
		landEntities.addAll(entFac.createPirateVillages(MIN_PIRATEVILLAGE_PROBABILITY, MAX_PIRATEVILLAGE_PROBABILITY, MIN_DIST_BETWEEN_PIRATE_FRIENDLY));
		allEnts.addAll(landEntities);
		map.addVillages(landEntities);

		//menus
		menuManager = new MenuManager(atlas, screenSize, this, fonts, player); 

		//entity manager
		entManager = new EntityManager(SHOW_HITBOXES, menuManager, this, map, camera);
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
		menuManager.tick();
		inputHandler.process(paused);
		entManager.process(paused);
			
		//round camera position to nearest 1/zoom_level of a pixel - this fixes screen tearing but introduces a weird jiggling effect
		if(ROUND_CAMERA_POS) {
			camera.position.x = Utils.roundToNearestFraction(camera.position.x, 1/ZOOM_LEVEL);
			camera.position.y = Utils.roundToNearestFraction(camera.position.y, 1/ZOOM_LEVEL);
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

	public Vector2 getScreenSize() {
		return screenSize;
	}
}
