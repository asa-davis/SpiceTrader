package dev.asa.spicetrader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;

public class MainGame extends ApplicationAdapter {
	
	//game settings
	final int NUM_COLS = 16;
	final int NUM_ROWS = 16;
	final int TILE_WIDTH = 16;
	final int TILE_HEIGHT = 16;
	final float ZOOM_LEVEL = 2;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	
	//rendering objects
	SpriteBatch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	
	//game objects
	SpiceTraderMap map;
	
	@Override
	public void create () {
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		atlas = new TextureAtlas("assets/textures.atlas");
		batch = new SpriteBatch();
		SpiceTraderMapGenerator mapGen = new SpiceTraderMapGenerator(atlas);
		map = mapGen.generateMap(NUM_COLS, NUM_ROWS, TILE_WIDTH, TILE_HEIGHT);

		//center camera and player on map
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH / ZOOM_LEVEL, SCREEN_HEIGHT / ZOOM_LEVEL);
		camera.position.x = (float) (NUM_COLS * TILE_WIDTH * 0.5);
		camera.position.y = (float) (NUM_ROWS * TILE_HEIGHT * 0.5);
		
		
		//debug
		System.out.print("	****	screen size: (" + SCREEN_WIDTH + ", " + SCREEN_HEIGHT + ")\n");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.05f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//handle input
		batch.begin();
		//camera.translate(5,0);
		batch.end();
		
		camera.update();
		map.render(camera);
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		atlas.dispose();
	}
	
	//inclusive
	public static int genRandomInt(int min, int max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}
}
