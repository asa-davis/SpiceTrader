package dev.asa.spicetrader;

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

public class MainGame extends ApplicationAdapter {
	
	//game settings
	final boolean SHOW_HITBOXES = true;
	final int TILE_WIDTH = 16;
	final int TILE_HEIGHT = 16;
	final float ZOOM_LEVEL = 3;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private float CENTER_SCREEN_X;
	private float CENTER_SCREEN_Y;
	
	//map settings
	final int SMOOTHING_ITERATIONS = 7;
	final int SEA_LEVEL_OFFSET = 2;
	final int NUM_COLS = 64;
	final int NUM_ROWS = 64;
	
	//rendering objects
	SpriteBatch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	ShapeRenderer hitboxRenderer;
	
	//game objects
	SpiceTraderMap map;
	Ship player;
	
	@Override
	public void create () {
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		CENTER_SCREEN_X = (float) (NUM_COLS * TILE_WIDTH * 0.5);
		CENTER_SCREEN_Y = (float) (NUM_ROWS * TILE_HEIGHT * 0.5);
		
		//rendering objects
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH / ZOOM_LEVEL, SCREEN_HEIGHT / ZOOM_LEVEL);
		camera.position.x = CENTER_SCREEN_X;
		camera.position.y = CENTER_SCREEN_Y;
		
		atlas = new TextureAtlas("assets/textures.atlas");
		batch = new SpriteBatch();
		
		if(SHOW_HITBOXES) {
			hitboxRenderer = new ShapeRenderer();
			hitboxRenderer.setColor(Color.BLUE);
		}
		
		//map
		SpiceTraderMapGenerator mapGen = new SpiceTraderMapGenerator(atlas);
		map = mapGen.generateMap(NUM_COLS, NUM_ROWS, TILE_WIDTH, TILE_HEIGHT, SMOOTHING_ITERATIONS, SEA_LEVEL_OFFSET);

		//player
		Sprite playerSprite = atlas.createSprite("ships/player");
		player = new Ship(CENTER_SCREEN_X - (playerSprite.getWidth() / 2), CENTER_SCREEN_Y - (playerSprite.getHeight() / 2), playerSprite, 2, 2, 180);
		
		//debug
		System.out.print("	****	screen size: (" + SCREEN_WIDTH + ", " + SCREEN_HEIGHT + ")\n");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.05f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//handle input
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			Vector2 move = player.moveForward();
			camera.translate(move);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			Vector2 move = player.moveBackward();
			camera.translate(move);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.turnCW();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.turnCCW();
		}
			
		camera.update();
		
		//render everything
		map.render(camera);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.draw(batch);
		batch.end();
		
		if(SHOW_HITBOXES) {
			hitboxRenderer.setProjectionMatrix(camera.combined);
			hitboxRenderer.begin(ShapeType.Line);
			player.drawHitbox(hitboxRenderer);
			hitboxRenderer.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		atlas.dispose();
	}
}
