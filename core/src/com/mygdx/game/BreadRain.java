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
	private Texture bucketImageSemiFull;
	private Texture bucketImageSemiFull2;
	private Texture bucketImageFull;
	private Texture liveImage;
	private Texture noLiveImage;
	private Texture backgroundTexture;
	private Sprite backgroundSprite;
	private Sound bucketSound;
	private Music backgroundMusic;
	private Music fire;
	private Sound liveSound;
	private Sound oven;
	private Sound liveLostSound;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Array<Rectangle> lives;
	private long lastDropTime;
	private long lastDropTimeLive;
	private BitmapFont font;
	private int liveCounter = 3;
	private double acceleration = 0.45;
	private double velocity = 6;
	private double xAcceleration = 0;
	private double friction = 0.025;
	private String[] BucketSound = {"bucketSound", "bucketSound2", "bucketSound3"};
	private double breadVelocity = 2;
	private int breadCounter = 0;
	private int backgroundCounter = 0;
	private int randomBackground;
	private int bucketCounter = 0;
	private int levelCounter = 0;

	@Override
	public void create () {
		font = new BitmapFont();
		breadImage = new Texture(Gdx.files.internal("bread.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		bucketImageSemiFull = new Texture(Gdx.files.internal("bucketsemifull.png"));
		bucketImageSemiFull2 = new Texture(Gdx.files.internal("bucketsemifull2.png"));
		bucketImageFull = new Texture(Gdx.files.internal("bucketfull.png"));
		liveImage = new Texture(Gdx.files.internal("live.png"));
		noLiveImage = new Texture(Gdx.files.internal("nolive.png"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
		fire = Gdx.audio.newMusic(Gdx.files.internal("fire.mp3"));
		oven = Gdx.audio.newSound(Gdx.files.internal("oven.mp3"));
		liveSound = Gdx.audio.newSound(Gdx.files.internal("live.mp3"));
		liveLostSound = Gdx.audio.newSound(Gdx.files.internal("liveLost.mp3"));
		randomBackground = MathUtils.random(1, 4);
		backgroundTexture = new Texture(Gdx.files.internal("background" + randomBackground + ".png"));
		backgroundSprite = new Sprite(backgroundTexture);

		backgroundMusic.setLooping(true);
		backgroundMusic.play();
		fire.setLooping(true);
		fire.setVolume(0.5f);
		fire.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 23;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<>();
		spawnBread();
		lives = new Array<>();
		fallingLives();

	}

	private void spawnBread() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 500);
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
			live.x = MathUtils.random(0, 500);
			live.y = 480;
			live.width = 64;
			live.height = 64;
			lives.add(live);
			lastDropTimeLive = TimeUtils.nanoTime();
		}
	}


	@Override
	public void render () {
		backgroundCounter++;
		if (backgroundCounter > 6) {
			randomBackground = MathUtils.random(1, 4);
			backgroundCounter = 0;
			backgroundTexture = new Texture(Gdx.files.internal("background" + randomBackground + ".png"));
			backgroundSprite = new Sprite(backgroundTexture);
		}
		if (levelCounter >= 5 && velocity < 7){
			levelCounter = 5 - levelCounter;
			breadVelocity += 0.25;
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
		batch.draw(breadImage, 460, 420);
		font.draw(batch, Integer.toString(breadCounter), 550, 440);
		if (bucketCounter == 0) {
			batch.draw(bucketImage, bucket.x, bucket.y);
		}else if (bucketCounter > 0 && bucketCounter < 5 ){
			batch.draw(bucketImageSemiFull, bucket.x, bucket.y);
		}else if (bucketCounter > 4 && bucketCounter < 10){
			batch.draw(bucketImageSemiFull2, bucket.x, bucket.y);
		}else{
			batch.draw(bucketImageFull, bucket.x, bucket.y);
		}

		if (liveCounter == 3){
			batch.draw(liveImage, 40, 420);
			batch.draw(liveImage, 110, 420);
			batch.draw(liveImage, 180, 420);
		}
		if (liveCounter == 2){
			batch.draw(liveImage, 40, 420);
			batch.draw(liveImage, 110, 420);
			batch.draw(noLiveImage, 180, 420);
		}
		if (liveCounter == 1){
			batch.draw(liveImage, 40, 420);
			batch.draw(noLiveImage, 110, 420);
			batch.draw(noLiveImage, 180, 420);
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
		if(bucket.x > 645) bucket.x = 645;

		if(TimeUtils.nanoTime() - lastDropTime > 1900000000){
			spawnBread();
		}
		if(TimeUtils.nanoTime() - lastDropTimeLive > 1000000000) fallingLives();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= breadVelocity;
			if(raindrop.y - 20 < 0){
				iter.remove();
				liveLostSound.play();
				liveCounter -= 1;
				if (liveCounter == 0){
					Gdx.app.exit();
				}
			}
			if(raindrop.overlaps(bucket)) {
				if (bucketCounter < 10) {
					bucketSound.play();
					bucketCounter++;
					iter.remove();
				}else{
					Gdx.app.exit();
					System.out.println("You lost!");
				}
			}
		}

		for (Iterator<Rectangle> iter = lives.iterator(); iter.hasNext(); ) {
			Rectangle live = iter.next();
			live.y -= breadVelocity;
			if(live.y - 20 < 0){
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

		if (bucket.x == 645 && bucketCounter > 0){
			breadCounter += bucketCounter;
			levelCounter += bucketCounter;
			bucketCounter = 0;
			oven.play();
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
