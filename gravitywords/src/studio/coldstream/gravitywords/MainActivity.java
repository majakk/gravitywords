package studio.coldstream.gravitywords;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.StrokeFont;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.content.Intent;
import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends BaseExample implements IAccelerometerListener, IOnSceneTouchListener, IOnAreaTouchListener, IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String TAG = "G_MAIN";

	private static final int CAMERA_WIDTH = 640;
	private static final int CAMERA_HEIGHT = 480;
	private static final int TILE_TEXTURE_SIZE = 64;
	private static final int TIMER_TIME = 30;
	private static final int MAX_LETTERS = 24;
	
	protected static final int MENU_RESET = 1000;
	protected static final int MENU_QUIT = MENU_RESET + 1;
	protected static final int MENU_OK = MENU_RESET + 2;
	protected static final int MENU_RESUME = MENU_RESET + 3;
	protected static final int MENU_SCORES = MENU_RESET + 4;

	private boolean DEBUG_MODE = false;
	
	private static final int FONT_SIZE = 32;
	// ===========================================================
	// Fields
	// ===========================================================
	private Camera mCamera;
	
	private Texture mTexture;

	private TiledTextureRegion mBoxFaceTextureRegion;
	private TextureRegion mBackground;
	
	private Texture mFontTexture;
	private Texture mFontTexture2;
	private Texture mFontTexture3;
	private Font mFont;
	private Font mFont2;
	private Font mFont3;
		
	private Texture mStrokeFontTexture;
	private StrokeFont mStrokeFont;

	private int mFaceCount = 0;
	private int nextLetterIndex = 0;
	private int maxLetters = MAX_LETTERS;

	private PhysicsWorld mPhysicsWorld;
	
	private float mGravityX;
	private float mGravityY;
	
	private ScoreBoard scoreBoard;
	
	protected Scene mMainScene;
	protected MenuScene mMenuScene;
	protected MenuScene mGameOverScene;
	
	private List<String> dict;
	private List<letterindexer> word;
	
	private String alfabet = "abcdefghijklmnopqrstuvwxyz";
	private int[] alfaValues = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
	private String alfaDist = "EEEEEEEEEEEEAAAAAAAAA" +
			"IIIIIIIIIOOOOOOOONNNNNNRRRRRRTTTTTTLLLLSSSSUUUU" +
			"DDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ";
	
	private Random myRand;
	private boolean correctFlag = false;
	private boolean countDownFlag = false;
	private boolean idleFlag = false;
	private boolean cDownFlag = false;
	private boolean letterFlag = false;
	//float startTime;
	private int ticker = 0;
	private float endTimeStamp;
	
	private int level = 1; //Actual Level	
	private long score = 0; //Actual Score
	
	HighScore mhl;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show();
		
		dict = new ArrayList<String>();
		word = new ArrayList<letterindexer>();
		
		if(DEBUG_MODE) Log.d(TAG, "Loading...");
        final Resources resources = this.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.ospd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int counter = 0;

        try {
            String line;
            while ((line = reader.readLine()) != null) {            	
                dict.add(line);                
                if(counter++ % 10000 == 0)
                	if(DEBUG_MODE) Log.e(TAG, "Added word: " + line);
            }
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(DEBUG_MODE) Log.d(TAG, "DONE loading!");
		
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		
		myRand = new Random(2);		
		
		mhl = new HighScore();
		
		endTimeStamp = 0.0f;
		
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTexture = new Texture(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);		
		this.mBoxFaceTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "alfabet.png", 0, 0, 4, 8); // 64x32
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		this.mTexture = new Texture(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBackground = TextureRegionFactory.createFromAsset(mTexture, this, "pinkice.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/*Text*/
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontTexture2 = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFontTexture3 = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.CYAN);
		this.mFont2 = new Font(this.mFontTexture2, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.YELLOW);
		this.mFont3 = new Font(this.mFontTexture3, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 36, true, Color.WHITE);
		this.mStrokeFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mStrokeFont = new StrokeFont(this.mStrokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), FONT_SIZE, true, Color.GRAY, 1, Color.BLACK);
		this.mEngine.getTextureManager().loadTextures(this.mFontTexture, this.mFontTexture2, this.mFontTexture3, this.mStrokeFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mFont, this.mFont2, this.mFont3, this.mStrokeFont);
				
		/*HUD*/		
		this.scoreBoard = new ScoreBoard(0, 2, this.mStrokeFont, null);
		mCamera.setHUD(this.scoreBoard);

		this.enableAccelerometerSensor(this);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mMenuScene = this.createMenuScene();
		this.mGameOverScene = this.createGameOverScene();
		
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		this.mMainScene = new Scene(2);
		this.mMainScene.setBackground(new ColorBackground(0.2f, 0.1f, 0.3f));
		this.mMainScene.setOnSceneTouchListener(this);
		
		final Sprite back = new Sprite(0,0,CAMERA_WIDTH, CAMERA_HEIGHT, mBackground);		

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 42, CAMERA_WIDTH, 42);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 42);
		final Shape left = new Rectangle(0, 0, 4, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 4, 0, 4, CAMERA_HEIGHT);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mMainScene.getFirstChild().attachChild(back);
		this.mMainScene.getFirstChild().attachChild(ground);
		this.mMainScene.getFirstChild().attachChild(roof);
		this.mMainScene.getFirstChild().attachChild(left);
		this.mMainScene.getFirstChild().attachChild(right);
		
		final ChangeableText hintText = new ChangeableText(CAMERA_WIDTH / 6, CAMERA_HEIGHT / 3, mFont3, "Tap screen to drop tiles...");
		hintText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		hintText.setScale(1.0f);
		this.mMainScene.getFirstChild().attachChild(hintText);
		hintText.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(6.0f),				
				new AlphaModifier(1.5f, 1.0f, 0.0f),				
				new DelayModifier(1.0f)
		));
		
		final ChangeableText hintText2 = new ChangeableText(CAMERA_WIDTH / 9, CAMERA_HEIGHT / 3, mFont3, "Create words by tapping tiles");
		hintText2.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		hintText2.setAlpha(0.0f);
		hintText2.setScale(1.0f);
		this.mMainScene.getFirstChild().attachChild(hintText2);
		hintText2.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(9.0f),
				new AlphaModifier(1.5f, 0.0f, 1.0f),
				new DelayModifier(4.0f),				
				new AlphaModifier(1.5f, 1.0f, 0.0f),				
				new DelayModifier(1.0f)
		));
		
		final ChangeableText hintText3 = new ChangeableText(CAMERA_WIDTH / 9, CAMERA_HEIGHT / 3, mFont3, "Steady and ready for points!");
		hintText3.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		hintText3.setAlpha(0.0f);
		hintText3.setScale(1.0f);
		this.mMainScene.getFirstChild().attachChild(hintText3);
		hintText3.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(18.0f),
				new AlphaModifier(1.5f, 0.0f, 1.0f),
				new DelayModifier(4.0f),				
				new AlphaModifier(1.5f, 1.0f, 0.0f),				
				new DelayModifier(1.0f)
		));
		
		
		this.mMainScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mMainScene.setOnAreaTouchListener(this);
		
		this.mMainScene.registerUpdateHandler(new TimerHandler(0.1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				if(correctFlag){
					countDownFlag = true;
					correctFlag = false;
					ticker = 0;					
				}
				if(countDownFlag){
					ticker++;					
												
					if(ticker == 30){	
						for(int i = 0; i < word.size(); i++){
							word.get(i).face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.25f), new ScaleModifier(0.25f, 1.25f, 1)));
						}
					}
					else if(ticker == 40){		
						for(int i = 0; i < word.size(); i++){
							word.get(i).face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
						}
					}
					else if(ticker == 50){		
						for(int i = 0; i < word.size(); i++){
							word.get(i).face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.75f), new ScaleModifier(0.25f, 1.75f, 0.01f)));
						}						
					}					
					else if(ticker == 55){						
						collectScore();	
						correctFlag = false;
						countDownFlag = false; //False After pointstext has been removed from HUD?
					}
					else if(ticker > 55){
						correctFlag = false;
						countDownFlag = false; //False After pointstext has been removed from HUD?
					}					
					
					
					
				}
				
				if(MainActivity.this.maxLetters < 9){
					if(DEBUG_MODE) Log.d(TAG, "LESS THAN 9 TILES!!!");
					letterFlag = true;
				}
				else{
					letterFlag = false;
				}					
				
				if(idleFlag){
					endTimeStamp = MainActivity.this.mEngine.getSecondsElapsedTotal();
					idleFlag = false;
					cDownFlag = true;
					if(DEBUG_MODE) Log.d(TAG, "TIMER STARTED...");
				}
				
				if(cDownFlag && letterFlag && (endTimeStamp + TIMER_TIME) < MainActivity.this.mEngine.getSecondsElapsedTotal()){
					if(DEBUG_MODE) Log.d(TAG, "TIME IS UP!");
					cDownFlag = false;
					saveScore();
					mMainScene.setChildScene(mGameOverScene, false, true, true);					
				}
				
				if(cDownFlag && letterFlag)
					MainActivity.this.scoreBoard.TimerText.setText("[" + (int)(TIMER_TIME - (MainActivity.this.mEngine.getSecondsElapsedTotal() - endTimeStamp)) + "]");
					
			}
		}));

		return this.mMainScene;
	}

	@Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final AnimatedSprite face = (AnimatedSprite) pTouchArea;
			this.jumpFace(face);
			MainActivity.this.idleFlag = true;
			return true;
		}

		return false;
	}

	@Override
	public void onLoadComplete() {

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				if((this.maxLetters - this.mFaceCount) > 0 && pSceneTouchEvent.getX() > 4 && pSceneTouchEvent.getX() < CAMERA_WIDTH - 4 - TILE_TEXTURE_SIZE &&
						pSceneTouchEvent.getY() > 42 && pSceneTouchEvent.getY() < CAMERA_HEIGHT - 42 - TILE_TEXTURE_SIZE){
					this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
					MainActivity.this.idleFlag = true;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		this.mGravityX = pAccelerometerData.getY();
		this.mGravityY = pAccelerometerData.getX();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void addFace(final float pX, final float pY) {
		final Scene scene = this.mEngine.getScene();
				
		this.mFaceCount++; //this will always count the number of pieces on screen (Not the same as available pieces?)
		this.nextLetterIndex++; //next letter
		
		final AnimatedSprite face;
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion.clone());
		
		//int currentRand = myRand.nextInt(26);
		int currentRand = myRand.nextInt(alfaDist.length());
		if(DEBUG_MODE) Log.d(TAG, Integer.toString(currentRand));
		if(DEBUG_MODE) Log.d(TAG, alfaDist.toLowerCase().substring(currentRand, currentRand+1));
		currentRand = alfabet.indexOf(alfaDist.toLowerCase().substring(currentRand, currentRand+1));
		
		if(DEBUG_MODE) Log.d(TAG, Integer.toString(currentRand));
		
		face.setCurrentTileIndex(currentRand);
		
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
				
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

		//face.animate(new long[]{200,200}, 0, 1, true);
		scene.registerTouchArea(face);
		scene.getLastChild().attachChild(face);
		
		this.scoreBoard.PiecesText.setText("Tiles: " + Integer.toString(maxLetters - this.mFaceCount) + "/" + Integer.toString(maxLetters));
	}

	private void jumpFace(final AnimatedSprite face) {		
		//String carrying the word
		final StringBuilder sb = new StringBuilder();
			
		if(DEBUG_MODE) Log.d(TAG, Integer.toString(face.getCurrentTileIndex()));
		
		
		if(word.size() > 0){
			boolean foundflag = false;
			
			for(int i = 0; i < word.size(); i++){
				if(word.get(i).face == face){ //face sprite exists?					
					word.remove(i);
					face.setColor(1.0f, 1.0f, 1.0f); // make transparent again
					foundflag = true;
				}				
			}
			if(!foundflag && word.size() < 8){
				word.add(new letterindexer(face.getCurrentTileIndex(), alfabet.charAt(face.getCurrentTileIndex()), face));				
			}
			
		}
		else{
			word.add(new letterindexer(face.getCurrentTileIndex(), alfabet.charAt(face.getCurrentTileIndex()), face));			
		}
		
		
		//Build word string
		for(int i = 0; i < word.size(); i++){
			sb.append(word.get(i).letter);
		}
		
		//String valid?
		if(dict.contains(sb.toString())){
			for(int i = 0; i < word.size(); i++){
				word.get(i).face.setColor(0.3f, 1.0f, 0.3f);
				
			}
			//Start a timer
			correctFlag = true;
			countDownFlag = false;			
		}
		else{
			correctFlag = false;
			countDownFlag = false;
			for(int i = 0; i < word.size(); i++){
				word.get(i).face.setColor(1.0f, 0.3f, 0.3f);
				word.get(i).face.setScale(1.0f);
				
			}		
		}
		
		//TODO explore game over condition...
		
			
		face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
				
		if(DEBUG_MODE)
			this.scoreBoard.WordText.setText(" Word: " + sb.toString().toUpperCase() + " (" + Integer.toString(word.size()) + ")" );	
		else
			this.scoreBoard.WordText.setText(" Word: " + sb.toString().toUpperCase() );
	}
	
	void collectScore(){
		/* Calculate Score */
		int newscore = 0;
		for(int i = 0; i < this.word.size(); i++){
			final ChangeableText pointsText = new ChangeableText(this.word.get(i).face.getX(), this.word.get(i).face.getY(), mFont, "+" + Integer.toString(alfaValues[this.word.get(i).index]));
			pointsText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			this.scoreBoard.attachChild(pointsText);
						
			final Path path = new Path(2).to(this.word.get(i).face.getX(), this.word.get(i).face.getY()).to(this.word.get(i).face.getX(), this.word.get(i).face.getY() - 64);
			pointsText.registerEntityModifier(new ParallelEntityModifier(
					new AlphaModifier(2.8f, 1.0f, 0.0f),
					new PathModifier(3.0f,path),
					new ScaleModifier(3.0f, 1.0f, 1.75f)
			));
			newscore = newscore + alfaValues[this.word.get(i).index];			
		}		
		
		/* Bonus ?? */
		if(this.word.size() > 2){
			float minX = 1000.0f;
			float maxX = 0.0f;
			float minY = 1000.0f;
			float maxY = 0.0f;
			for(int i = 0; i < this.word.size(); i++){
				if(minX > this.word.get(i).face.getX())
					minX = this.word.get(i).face.getX();
				
				if(maxX < this.word.get(i).face.getX())
					maxX = this.word.get(i).face.getX();
	
				if(minY > this.word.get(i).face.getY())
					minY = this.word.get(i).face.getY();
				
				if(maxY < this.word.get(i).face.getY())
					maxY = this.word.get(i).face.getY();			
			}
			
			if(maxX - minX < 64 || maxY - minY < 64){			
				float midX = 200;
				float midY = 200;
				final ChangeableText pointsText = new ChangeableText(midX, midY, mFont2, "BONUS x 2");
				//pointsText.setColor(0.3f, 0.7f, 0.7f);
				pointsText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				this.scoreBoard.attachChild(pointsText);
							
				final Path path = new Path(2).to(midX, midY).to(midX, midY - 64);
				pointsText.registerEntityModifier(new ParallelEntityModifier(
						new AlphaModifier(2.8f, 1.0f, 0.0f),
						new PathModifier(3.0f,path),
						new ScaleModifier(3.0f, 1.25f, 2.00f)
				));
				newscore = newscore * 2;
			}
		}
			
		score = score + newscore;
		
		this.scoreBoard.ScoreText.setText(" Score: " + Long.toString(score));
		
		//Evil Decrease of maxLetters
		if(maxLetters > 2)
			maxLetters--;
		
		/* Level Up? */
		if(score >= (100 * level) + (20 * (level - 1))){
			level++;
			DecimalFormat maxDigitsFormatter = new DecimalFormat("000");
			this.scoreBoard.LevelText.setText("Level: " + maxDigitsFormatter.format(level));
			maxLetters = MAX_LETTERS;
		}
		
		
		
		this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {            		
                    /* Now it is save to remove an entity! */                   
            		for(int i = 0; i < MainActivity.this.word.size(); i++){            			
            			final PhysicsConnector facePhysicsConnector = mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(word.get(i).face);

            			mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
            			mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());
        
            			MainActivity.this.mEngine.getScene().getLastChild().detachChild(word.get(i).face);
            			MainActivity.this.mEngine.getScene().unregisterTouchArea(word.get(i).face);
            			
            			MainActivity.this.mFaceCount--;
            		}
            		word.clear();
            		if(DEBUG_MODE) Log.d(TAG, "Size after deletion: " + Integer.toString(word.size()));
            		MainActivity.this.scoreBoard.WordText.setText(" Word:                     ");
            		MainActivity.this.scoreBoard.PiecesText.setText("Tiles: " + Integer.toString(MainActivity.this.maxLetters - MainActivity.this.mFaceCount) + "/" + Integer.toString(MainActivity.this.maxLetters));
            		
            }
		});	
		
		return;
	}
	
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);
		
		menuScene.setBackgroundEnabled(false);
		
		final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(1.0f, 1.0f, 1.0f); // Whatever color you fancy
		Rect.setAlpha(0.4f);		
		//Rect.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.getFirstChild().attachChild(Rect);

		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESUME, this.mFont3, "RESUME"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resetMenuItem);
		
		final IMenuItem restartMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESET, this.mFont3, "RESTART"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(restartMenuItem);		
		
		final IMenuItem scoresMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_SCORES, this.mFont3, "HIGH SCORES"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(scoresMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont3, "QUIT"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);
		
		menuScene.buildAnimations();				

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}
	
	protected MenuScene createGameOverScene() {
		final MenuScene gameOverScene = new MenuScene(this.mCamera);
		
		//New highscore??
		
		gameOverScene.setBackgroundEnabled(false);
		
		final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(1.0f, 1.0f, 1.0f); // Whatever color you fancy
		Rect.setAlpha(0.4f);		
		//Rect.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.getFirstChild().attachChild(Rect);
		
		final ChangeableText goText = new ChangeableText(CAMERA_WIDTH / 3, CAMERA_HEIGHT / 5, mFont3, "GAME OVER");
		goText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		goText.setScale(2.0f);
		gameOverScene.getFirstChild().attachChild(goText);

		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESET, this.mFont3, "RETRY"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.addMenuItem(resetMenuItem);
		
		final IMenuItem scoresMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_SCORES, this.mFont3, "HIGH SCORES"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.addMenuItem(scoresMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont3, "QUIT"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.addMenuItem(quitMenuItem);
		
		gameOverScene.buildAnimations();				

		gameOverScene.setOnMenuItemClickListener(this);
		return gameOverScene;
	}
	
	public boolean saveScore(){
		SimpleDateFormat yyyy = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        //yyyy.setTimeZone(TimeZone.getTimeZone("gmt"));
        String newDate = new String(yyyy.format(new Date()));
        
        //if(DEBUG_MODE) Log.d(TAG, newDate );
		
		MainActivity.this.mhl.addHighScore(MainActivity.this.getBaseContext(), newDate, MainActivity.this.level, MainActivity.this.score);
		
		return true; 
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(DEBUG_MODE) Log.d(TAG, "Menu Button Pressed" );
			
			
			/* Attach the menu. */
			this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
			
			//MainActivity.this.finish();
			
			//Menu button code here
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
	
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_RESET:
				/* Restart the animation. */
				//this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();		
				Intent mainIntent = new Intent(MainActivity.this,MainActivity.class);
				MainActivity.this.startActivity(mainIntent);
				MainActivity.this.finish();
				//this.mMainScene.setChildScene(analogOnScreenControl);
				return true;
			case MENU_RESUME:
				/* Restart the animation. */
				//this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();		
				
				//this.mMainScene.setChildScene(analogOnScreenControl);
				return true;
			case MENU_QUIT:
				/* End Activity. */
				//this.finish();
				this.mMainScene.clearChildScene();		
				//Intent mainIntent2 = new Intent(MainActivity.this,MainMenu.class);
				//MainActivity.this.startActivity(mainIntent2);
				MainActivity.this.finish();
				return true;
			case MENU_OK:
				this.mMainScene.clearChildScene();		
				//this.mMenuScene.clearChildScene();	
				//this.mMenuScene.reset();
				
				//this.mMainScene.reset();
				//this.mMainScene.setChildScene(analogOnScreenControl);
				
				//this.analogOnScreenControl.clearTouchAreas();
				
				return true;
			case MENU_SCORES:
				//this.mMainScene.clearChildScene();
				
				if(DEBUG_MODE) saveScore();		
				
				Intent mainIntent2 = new Intent(MainActivity.this, HighScoreActivity.class);
				MainActivity.this.startActivity(mainIntent2);
				//this.mMenuScene.clearChildScene();	
				//this.mMenuScene.reset();
				
				//this.mMainScene.reset();
				//this.mMainScene.setChildScene(analogOnScreenControl);
				
				//this.analogOnScreenControl.clearTouchAreas();
				
				return true;
			default:
				return false;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
    
    public class letterindexer {
    	public int index;
    	public char letter;
    	AnimatedSprite face;
    	
    	letterindexer(int i, char c, AnimatedSprite f){
    		index = i;
    		letter = c;
    		face = f;
    	}    	
    }
    
 public class ScoreBoard extends HUD {
    	private final ChangeableText WordText;
        private final ChangeableText ScoreText; 
        private final ChangeableText TimerText; 
        private final ChangeableText PiecesText;
        private final ChangeableText LevelText;        
     
        public ScoreBoard(float pX, float pY, Font pFont, final Camera pCamera) {
                    	
        	ScoreText = new ChangeableText(pX, pY, pFont, " Score: 0        ");
        	this.attachChild(ScoreText);
        	
        	WordText = new ChangeableText(pX, 440, pFont, " Word:                     ");
            this.attachChild(WordText);    
            
            TimerText = new ChangeableText(300, pY, pFont, "[0] ");
        	this.attachChild(TimerText);
            
            PiecesText = new ChangeableText(452, 440, pFont, "Tiles: " + Integer.toString(MainActivity.this.maxLetters - MainActivity.this.mFaceCount) + "/" + Integer.toString(MainActivity.this.maxLetters));
            this.attachChild(PiecesText); 
            
            LevelText = new ChangeableText(478, pY, pFont, "Level: 001");
            this.attachChild(LevelText); 
           
        }       
     
    }   
    
}
