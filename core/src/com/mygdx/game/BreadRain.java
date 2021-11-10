package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.util.Iterator;

public class BreadRain extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound bucketSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int dropNumber;
	private BitmapFont font;
	private int lives = 3;

	@Override
	public void create () {

		font = new BitmapFont();
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("bread.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		bucketSound = Gdx.audio.newSound(Gdx.files.internal("bucketSound.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rainMusic.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		do {
			raindrop.x = MathUtils.random(0, 800 - 64);
			raindrop.y = 480;
			raindrop.width = 64;
			raindrop.height = 64;
			raindrops.add(raindrop);
			lastDropTime = TimeUtils.nanoTime();
			System.out.println(bucket.x - raindrop.x);
		}while(Math.abs(bucket.x - raindrop.x) > 600);

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, Integer.toString(dropNumber), 700, 440);
		font.draw(batch, Integer.toString(lives), 100, 440);
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 5.5;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 5.5;

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 3;
			if(raindrop.y + 64 < 0){
				iter.remove();
				lives -= 1;
				if (lives == 0){
					Gdx.app.exit();
				}
			}
			if(raindrop.overlaps(bucket)) {
				bucketSound.play();
				dropNumber++;
				iter.remove();
			}
		}


	}

	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		bucketSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
