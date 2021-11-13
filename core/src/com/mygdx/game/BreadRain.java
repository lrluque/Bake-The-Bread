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
	private Sound liveSound;
	private Sound liveLostSound;
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
	private double acceleration = 0.45;
	private double velocity = 6;
	private double xAcceleration = 0;
	private double friction = 0.025;
	private String[] BucketSound = {"bucketSound", "bucketSound2", "bucketSound3"};
	double breadVelocity = 3;
	private int breadCounter = 0;

	@Override
	public void create () {
		font = new BitmapFont();
		breadImage = new Texture(Gdx.files.internal("bread.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		liveImage = new Texture(Gdx.files.internal("live.png"));
		noLiveImage = new Texture(Gdx.files.internal("nolive.png"));
		backgroundTexture = new Texture(Gdx.files.internal("background.png"));
		backgroundSprite = new Sprite(backgroundTexture);


		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
		liveSound = Gdx.audio.newSound(Gdx.files.internal("live.mp3"));
		liveLostSound = Gdx.audio.newSound(Gdx.files.internal("liveLost.mp3"));


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

		raindrops = new Array<>();
		spawnBread();
		lives = new Array<>();
		fallingLives();

	}

	private void spawnBread() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
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
		if (breadCounter == 20 && velocity < 7){
			breadCounter = 0;
			breadVelocity += 0.5;
		}
		bucketSound = Gdx.audio.newSound(Gdx.files.internal(BucketSound[MathUtils.random(0,2)] +".mp3"));
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

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) xAcceleration = -acceleration;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) xAcceleration = acceleration;
		if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (velocity > 0) {
				xAcceleration = -0.02;
			} else if (velocity < 0) {
				xAcceleration = +0.02;
			}
		}
		velocity += xAcceleration;
		velocity *= 1 - friction;
		bucket.x += velocity;

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		if(TimeUtils.nanoTime() - lastDropTime > 1200000000){
			spawnBread();
		}
		if(TimeUtils.nanoTime() - lastDropTimeLive > 1000000000) fallingLives();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= breadVelocity;
			if(raindrop.y + 64 < 0){
				iter.remove();
				liveLostSound.play();
				liveCounter -= 1;
				if (liveCounter == 0){
					Gdx.app.exit();
				}
			}
			if(raindrop.overlaps(bucket)) {
				bucketSound.play();
				dropNumber++;
				breadCounter++;
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
					liveSound.play();
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
