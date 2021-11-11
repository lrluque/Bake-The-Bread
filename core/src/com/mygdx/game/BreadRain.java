package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class BreadRain extends ApplicationAdapter {
	private Texture breadImage;
	private Texture bucketImage;
	private Texture liveImage;
	private Texture noLiveImage;
	private Texture backgroundTexture;
	private Sprite backgroundSprite;
	private Sound bucketSound;
	private Music backgroundMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Array<Rectangle> lives;
	private long lastDropTime;
	private long lastDropTimeLive;
	private int dropNumber;
	private BitmapFont font;
	private int liveCounter = 3;



	@Override
	public void create () {
		font = new BitmapFont();
		breadImage = new Texture(Gdx.files.internal("bread.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		liveImage = new Texture(Gdx.files.internal("live.png"));
		noLiveImage = new Texture(Gdx.files.internal("nolive.png"));
		backgroundTexture = new Texture(Gdx.files.internal("background.png"));
		backgroundSprite = new Sprite(backgroundTexture);


		bucketSound = Gdx.audio.newSound(Gdx.files.internal("bucketSound.mp3"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));


		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<Rectangle>();
		spawnBread();
		lives = new Array<Rectangle>();
		fallingLives();

	}

	private void spawnBread() {
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

	private void renderBackground() {
		backgroundSprite.draw(batch);
	}


	private void fallingLives(){
		int probability = (int) (10000 * Math.random());
		Rectangle live = new Rectangle();
		if (probability <= 5){
			live.x = MathUtils.random(0, 800 - 64);
			live.y = 480;
			live.width = 64;
			live.height = 64;
			lives.add(live);
			lastDropTimeLive = TimeUtils.nanoTime();
		}
	}


	@Override
	public void render () {

		ScreenUtils.clear(0, 0, 0.2f, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderBackground();
		for(Rectangle raindrop: raindrops) {
			batch.draw(breadImage, raindrop.x, raindrop.y);
		}
		for(Rectangle live: lives){
			batch.draw(liveImage, live.x, live.y);
		}
		batch.draw(breadImage, 610, 420);
		font.draw(batch, Integer.toString(dropNumber), 700, 440);
		batch.draw(bucketImage, bucket.x, bucket.y);
		if (liveCounter == 3){
			batch.draw(liveImage, 40, 400);
			batch.draw(liveImage, 110, 400);
			batch.draw(liveImage, 180, 400);
		}
		if (liveCounter == 2){
			batch.draw(liveImage, 40, 400);
			batch.draw(liveImage, 110, 400);
			batch.draw(noLiveImage, 180, 400);
		}
		if (liveCounter == 1){
			batch.draw(liveImage, 40, 400);
			batch.draw(noLiveImage, 110, 400);
			batch.draw(noLiveImage, 180, 400);
		}

		batch.end();

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 6;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 6;


		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000){

			spawnBread();
		}
		if(TimeUtils.nanoTime() - lastDropTimeLive > 1000000000) fallingLives();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 3;
			if(raindrop.y + 64 < 0){
				iter.remove();
				liveCounter -= 1;
				if (liveCounter == 0){
					Gdx.app.exit();
				}
			}
			if(raindrop.overlaps(bucket)) {
				bucketSound.play();
				dropNumber++;
				iter.remove();
			}
		}

		for (Iterator<Rectangle> iter = lives.iterator(); iter.hasNext(); ) {
			Rectangle live = iter.next();
			live.y -= 3;
			if(live.y + 64 < 0){
				iter.remove();
			}
			if(live.overlaps(bucket)) {
				if (liveCounter < 3) {
					bucketSound.play();
					liveCounter++;
					iter.remove();
				}else{
					bucketSound.play();
					iter.remove();
				}
			}
		}


	}

	@Override
	public void dispose () {
		breadImage.dispose();
		bucketImage.dispose();
		bucketSound.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}
}
