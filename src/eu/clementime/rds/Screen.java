package eu.clementime.rds;

//import static eu.clementime.rds.Constants.ACTION_LOOK;
//import static eu.clementime.rds.Constants.ACTION_TAKE;
//import static eu.clementime.rds.Constants.ACTION_TALK;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT;
//import static eu.clementime.rds.Constants.CLICK_AGAIN_INVENTORY;
//import static eu.clementime.rds.Constants.CLICK_BAG;
//import static eu.clementime.rds.Constants.CLICK_INVENTORY;
//import static eu.clementime.rds.Constants.CLICK_OFF;
//import static eu.clementime.rds.Constants.CLICK_ZOOM;
//import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_IN_INVENTORY;
//import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_ON_SCREEN;
//import static eu.clementime.rds.Constants.DB_TABLE_COMBINATION;
//import static eu.clementime.rds.Constants.DIRECTION_LEFT;
//import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
//import static eu.clementime.rds.Constants.INFORMATION_CLOSING_HEIGHT;
//import static eu.clementime.rds.Constants.INVBOX_ALPHA_LAYER;
//import static eu.clementime.rds.Constants.INVENTORY_POSX_ZOOM_ITEM;
//import static eu.clementime.rds.Constants.INVENTORY_POSY_NORMALVIEW;
//import static eu.clementime.rds.Constants.INVENTORY_POSY_ZOOM_ITEM;
//import static eu.clementime.rds.Constants.INV_ALPHA_LAYER;
//import static eu.clementime.rds.Constants.MODE_ACTION;
//import static eu.clementime.rds.Constants.MODE_ACTION_WAIT;
//import static eu.clementime.rds.Constants.MODE_ANIM_RUNNING;
//import static eu.clementime.rds.Constants.MODE_DROP;
//import static eu.clementime.rds.Constants.MODE_INVENTORY;
//import static eu.clementime.rds.Constants.MODE_SCREEN_OFF;
//import static eu.clementime.rds.Constants.MODE_ZOOM;
//import static eu.clementime.rds.Constants.STATUS_ANIM;
//import static eu.clementime.rds.Constants.STATUS_INVENTORY;
//import static eu.clementime.rds.Constants.STATUS_MAP;
//import static eu.clementime.rds.Constants.STATUS_SCREEN;
//import static eu.clementime.rds.Constants.STATUS_TALK;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM_IN_USE;

import java.util.ArrayList;
import java.util.ListIterator;

import org.anddev.andengine.collision.RectangularShapeCollisionChecker;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class Screen extends BaseGameActivity implements IOnAreaTouchListener,
IOnSceneTouchListener {

//	/*
//	/* Android & General objects
//	/*******************************************************/
	private RdsGame app;
	private DatabaseHandler dbh;
	private Context context;
	
	private CharSequence text = "An error occurred during the game\nPlease contact xxx\nPlease send logs to zzz";
	private int duration = Toast.LENGTH_LONG;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
	private static int CAMERA_WIDTH = 0;
	private static int CAMERA_HEIGHT = 0;
	
	private int MARGIN = 0;
//	
//	/*
//	/* AndEngine objects (loaded at game start)
//	/*******************************************************/
//	private final ClickDetector clickDetector = new ClickDetector(this);
	private Camera camera;
	private Scene scene = new Scene();
	private Doll doll;
//	private PhysicsHandler phDoll;
//	private PhysicsHandler phAnimRunning;
//	
//	/*
//	/* changeable objects & screen variables (loaded at each screen)
//	/*******************************************************/
	private Background currentBg;
	private Background nextBg;
//	
//	private float minChasingX = 0;
//	private float maxChasingX = 0;
//	
//	/*
//	/* persistent objects (loaded at game start)
//	/*******************************************************/
//	private Inventory inventory;
//	private Talk talkScreen;
//	private Information frame;
	private GameTools gameTools;
//	private ActionsManager actionsManager;
//	private Map map;
//	
//	private World world;
//	

//
//	private BitmapTextureAtlas loadingBTA;
//	private TextureRegion loadingTR;
//	
//	private BitmapTextureAtlas settingsBTA;
//	private TextureRegion settingsTR;
//	
//	private BitmapTextureAtlas mapBTA;
//	private TextureRegion mapTR;
//	
	private BitmapTextureAtlas defaultFontBTA;
	private BitmapTextureAtlas defaultFont2BTA;
	private BitmapTextureAtlas fontBTA;
	private Font defaultFont;
	private Font defaultFont2;
	private Font font;
//
//	/*
//	/* touched objects, variables & booleans => keep trace at runtime
//	/*****************************************************************/	
//	private AnimatedSprite touchedAction = null;
//	private Exit touchedExit = null;
//	private InventoryItem touchedInventoryItem = null;
//	private InventoryItem touchedZoomItem = null;
//	private ScreenItem touchedItem = null;
//	private ScreenItem itemToBeRemoved = null;
//	private Sprite touchedMapItem = null;
//	private Area touchedArea = null;
//	private PlayingAnimation touchedChar = null;
//
//	private boolean movingArrowPressed = false;
//	private boolean actionsManagerOutsideBorders = false;
//	private boolean dollIsChased = true;
//	
//	// doll moving
//	private float touchedX = 0;
//	private int dollDirection = 1;	
//	private float dollYVelocityLeft = 0;
//	private float dollYVelocityRight = 0;
//	private int dollYVelocity = 0;
//
//	// status
//	private int status = STATUS_SCREEN;
//	private int mode = MODE_SCREEN_OFF;
//	private int clickCheck = CLICK_OFF;
//
//	// animation sequences
//	private int pendingTriggerId = 0;
//	private int simultaneousTriggerId = 0;
//	private int runningAnimId = 0;
//	
//	// switch screen
//	private int firstScreenTriggerId = 0;
//	private int[] startingPosition;
	private boolean initScene = false;
//	
//	private float downTime;
	private ChangeableText errorMessage;
//	private Sprite settings;
//	private Sprite mapSprite;
//	
//	//***************************************
//	// DEV TOOLS - IN DEVELOPMENT ONLY
//	//***************************************
//	private DevToolsManager devFrame;
//	private Backup devTools;
//	private Text devOpen;
	private BitmapTextureAtlas bigFontBTA;
	private Font bigFont;
//	private int load = 0;
//	// DELAY log displayed in loop
//	//*****************************
//	boolean displayLog = false;
//	private float lastLog = 0;
//	private float nextLog = 0;
	
    @Override
    public Engine onLoadEngine() {   	
		
		Log.i("Clementime", "Screen/onLoadEngine()");
		
		try {
	    	context = getApplicationContext();
	    	getWindowManager().getDefaultDisplay().getMetrics(dm);
	    	
	       	CAMERA_WIDTH = dm.widthPixels;
	    	CAMERA_HEIGHT = dm.heightPixels;

	    	camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    	
	    	MARGIN = (CAMERA_HEIGHT - BACKGROUND_MAX_HEIGHT)/2;
	
		} catch (Exception e){
			Log.e("Clementime", "Screen/onLoadEngine(): cannot set camera - " + e.getMessage());			
		}

		//EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camera);
    	engineOptions.getTouchOptions().setRunOnUpdateThread(true);		

		return new Engine(engineOptions);
    }
    
	@Override
	public void onLoadResources() {	
		
		Log.i("Clementime", "Screen/onLoadResources()");
		
		try {
	//		loadingBTA = new BitmapTextureAtlas(512, 512, TextureOptions.DEFAULT);
	//		loadingTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(loadingBTA, this, R.drawable.loading, 0, 0);
	//
	//		this.mEngine.getTextureManager().loadTexture(loadingBTA);
			loadResources();			

		} catch (Exception e){
			Log.e("Clementime", "Screen/onLoadResources(): cannot load resources - " + e.getMessage());			
		}
		

	}	
	
	@Override
	public Scene onLoadScene() {
			
		this.mEngine.registerUpdateHandler(new FPSLogger());	
		
		try {
			
			setLoop();
	//		
	//		/*
	//		/* starting game, persistent objects are set up
	//		/**************************************************/
	//		
	//		// Doll
			doll = new Doll(context, mEngine, scene);
			
	//		doll = new AnimatedSprite(0, 0, dollTR);
	//		doll.stopAnimation(16);
	//			
	//		phDoll = new PhysicsHandler(doll);
	//		doll.registerUpdateHandler(phDoll);
	//
	//		/*
	//		/* prepare background, because items are needed in inventory
	//		/******************************************************************************/
			loadNewScreen();
	//
	//		/*
	//		/* setup inventory, talk screen, information frame, game tools (arrows, pointer...)
	//		/******************************************************************************/
	//        if (DEVELOPMENT) devFrame.setup();
	//        inventory.setup(nextBg.items); // TODO: inventory shouldn't be part of next scene
	//        talkScreen.setup();
	//        frame.setup();
	//        map.setup();
	//        
	//        gameTools.setupGameItems();
	//		actionsManager.setup();	
	//		
	//		attachPersistentObjects();
	//		
	//		/*
	//		/* then set up background, items, animations and foreground of current screen
	//		/* (changed when new screen is loaded)
	//		/******************************************************************************/
			currentBg = nextBg;
			initNewScene();	
			
			Log.i("Clementime", "Screen/onLoadScene()");
	
	//		scene.attachChild(new Sprite(0, 0, loadingTR));

		} catch (Exception e){
			Log.e("Clementime", "Screen/onLoadScene(): cannot load scene - " + e.getMessage());
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		return scene;
	}
	
	@Override
	public void onLoadComplete() {
		
	}
	
	private void loadResources() {
		
        FontFactory.setAssetBasePath("font/");
        
        //**********************
		// FONTS
        //**********************
        fontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		font = FontFactory.createFromAsset(fontBTA, this, "arfmoochikncheez.ttf", 20, true, Color.BLACK);
		mEngine.getTextureManager().loadTexture(fontBTA);
		getFontManager().loadFont(font);        
        
		defaultFontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		defaultFont = new Font(defaultFontBTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.BLACK);
		mEngine.getTextureManager().loadTexture(defaultFontBTA);
		getFontManager().loadFont(defaultFont);
		
		defaultFont2BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		defaultFont2 = new Font(defaultFont2BTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.RED);
		mEngine.getTextureManager().loadTexture(defaultFont2BTA);
		getFontManager().loadFont(defaultFont2);
		
		

        //**********************
		// SCREEN
        //**********************	
//		world = new World(dbh, context);
//		
		gameTools = new GameTools(context, CAMERA_WIDTH, CAMERA_HEIGHT, MARGIN);
//		inventory = new Inventory(camera, font, context, dbh, CAMERA_WIDTH, CAMERA_HEIGHT, world.language);
//		talkScreen = new Talk(camera, talkFont, talkFont2, context, dbh, world.screenId, world.language);
//		frame = new Information(camera, talkFont);
//		actionsManager = new ActionsManager(camera); // hand, magnify glass, dialog bubble, exit
//		map = new Map(dbh, CAMERA_WIDTH, CAMERA_HEIGHT);
//		
//		mEngine.getTextureManager().loadTexture(inventory.loadImages());
//		mEngine.getTextureManager().loadTexture(talkScreen.loadImages(context));
//		mEngine.getTextureManager().loadTexture(frame.loadImages(context));
		gameTools.loadGameItems(mEngine, scene);
//		mEngine.getTextureManager().loadTexture(actionsManager.loadImages(context));
//		mEngine.getTextureManager().loadTexture(map.loadImages(context));
//		
//		// case inventoryBTAs are null
//		if (!inventory.isEmpty()) {
//			BitmapTextureAtlas[] inventoryBTA = inventory.loadItems();
//			mEngine.getTextureManager().loadTextures(inventoryBTA[0], inventoryBTA[1]);
//		}
//
//		// doll
//		dollBTA = new BitmapTextureAtlas(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//		dollTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, this, R.drawable.doll, 0, 0, 4, 5);
//		
//		// settings
//		settingsBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
//		settingsTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(settingsBTA, this, R.drawable.settings, 0, 0);
//		
//		// map
//		mapBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
//		mapTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(mapBTA, this, R.drawable.settings, 0, 0);
//
//		mEngine.getTextureManager().loadTextures(dollBTA, settingsBTA, mapBTA);
		
		//***************************************
		// DEV TOOLS - IN DEVELOPMENT ONLY
		//***************************************

//		if (DEVELOPMENT) {
//			bigFontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//			bigFont = new Font(bigFontBTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, Color.RED);
//			mEngine.getTextureManager().loadTexture(bigFontBTA);
//			getFontManager().loadFont(bigFont);
//			
//	    	devFrame = new DevToolsManager(camera, font, bigFont, dbh, devTools);
//			devOpen = new Text(480, 10, this.bigFont, "X", HorizontalAlign.LEFT);
//			devOpen.setPosition(480/2-devOpen.getWidth()/2, 10);
//		}
	}
	
	private void loadNewScreen() {
		Log.i("Clementime", "Screen/loadNewScreen()");

		//******************************************************************************
		// items, areas, animations and talk screen chars (scene depending objects)
		//*****************************************************************************
		//screenFeatures = world.getScreenFeatures(lastExitId);
		
		// get position of doll from player table - on x beginning position is set to -1
//		startingPosition = world.getDollPosition();
//		
//		// get new position from database if:
//		// 1. doll comes from another exit (exit >= 1)
//		// 2. game begins (exit=0 & x beginning position isn't set by anterior playing => -1)
//		if (touchedExit != null) {
//			startingPosition[0] = touchedExit.startingX; 
//			startingPosition[1] = touchedExit.startingY; 
//			world.screenId = touchedExit.toScreen;
//			
//			Log.i("Clementime", "Screen/loadNewScreen(): enter a new screen from 'last' exit: " + touchedExit.id);
//		} else if (startingPosition[0] == -1) {
//			int[] features = world.getFirstScreenFeatures();
//			startingPosition[0] = features[1]; 
//			startingPosition[1] = features[2]; 
//			firstScreenTriggerId = features[0];
//			
//			Log.i("Clementime", "Screen/loadNewScreen(): starting new game ");
//		} else if(touchedMapItem != null) {
//			world.screenId = Integer.parseInt(touchedMapItem.getUserData().toString());
//			int[] features = world.getScreenFeatures(world.screenId);
//			startingPosition[0] = features[1]; 
//			startingPosition[1] = features[2]; 
//			firstScreenTriggerId = features[0];
//			
//			touchedMapItem = null;
//		
//		} else Log.i("Clementime", "Screen/loadNewScreen(): player in screen: " + world.screenId);
//		
		// create a new scene handler for sprites (background, items, animations...), areas, borders of scene, etc...
		//nextBg = new Background(dbh, context, world.screenId, world.language, CAMERA_WIDTH, CAMERA_HEIGHT);
		nextBg = new Background(dbh, context, 1, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		// load images
		nextBg.loadBackground(mEngine, scene, MARGIN);
		nextBg.loadItems(mEngine, scene, MARGIN);
		nextBg.loadAnimations(mEngine, scene, MARGIN);
//		nextBg.loadChars();

//		if (!nextBg.hasNoCharacter()) {
//			mEngine.getTextureManager().loadTexture(nextBg.charsBTA);			
//		}
//		
//		nextBg.loadAreas();
//		nextBg.loadExits(actionsManager.exitLeftTR, actionsManager.exitRightTR);
//		
//		talkScreen.chars = nextBg.chars;
	}
	
	public void initNewScene() {
		
		Log.i("Clementime", "Screen/initNewScene()");
		
		initScene = true; // do not do anything during scene initialisation
//		
//		try {
//			// hide objects when changing screen
//			showPersistentObjects(false);
//			
//			//setLoop();
//					
//			dollYVelocityRight = world.getYVelocity(world.screenId);
//			dollYVelocityLeft = -dollYVelocityRight;
//			
			scene.setOnSceneTouchListener(this);
			scene.setOnAreaTouchListener(this);
//					
//			// TODO: choose if XMin/Max should be in gameTools or sideScreen class
//			gameTools.xMin = currentScreen.xMin;
//			gameTools.xMax = currentScreen.xMax;
//			
//			// variables to chase doll or animation with the camera
//			minChasingX = CAMERA_WIDTH / 2;
//			maxChasingX = currentScreen.bgImage.getWidth() - CAMERA_WIDTH / 2;	
//			
//			// background & foreground
//			//scene.grounds[0].setPosition(0, 0);
//			//if (scene.grounds[1] != null) scene.grounds[1].setPosition(0, 0);	
//
//			/*
//			/* set persistent objects in position
//			/********************************************/		
//			doll.setPosition(startingPosition[0], startingPosition[1] + MARGIN);
//			
//			// set camera focus on doll at start
//			if (startingPosition[0] >= currentScreen.bgImage.getWidth() - CAMERA_WIDTH/2)	camera.setCenter(currentScreen.bgImage.getWidth() - CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
//			else if (startingPosition[0] <= CAMERA_WIDTH/2)								camera.setCenter(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);			
//			else 																		camera.setCenter(startingPosition[0] + doll.getWidth() / 2, CAMERA_HEIGHT/2);
//
//			setToolsInPosition();

//			showPersistentObjects(true);
//			showStaticAnims();
//			showExits();
//			
//			if (touchedExit != null) {
//				if (touchedExit.afterTrigger != 0) launchTrigger(touchedExit.afterTrigger, false);
//				touchedExit = null;				
//			} else if (firstScreenTriggerId != 0) {
//				launchTrigger(firstScreenTriggerId, false);
//				firstScreenTriggerId = 0;
//			} else {
////				status = STATUS_SCREEN;
////				mode = MODE_SCREEN_OFF;
//			}
//
//
			scene.sortChildren();	
//
//		} catch (Exception e) {
//			Log.e("Clementime", "Screen/initNewScene(): raise error: " + e.getMessage());
//		}
//		
		initScene = false;
	}
	
	private void setLoop() {
		
		Log.i("Clementime", "Screen/setLoop()");

		// checks during runtime
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }
	
			@Override
			public void onUpdate(final float pSecondsElapsed) {
	

			}
		});	
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if (initScene) Log.i("Clementime", "Screen/onAreaTouched(): scene initialisation - touchArea desactivated"); 
		else {
			Log.i("Clementime", "Screen/onAreaTouched(): touch " + pTouchArea.toString());
			
			try {	
//				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
//					
//					//****************************************
//					//   INVENTORY (to zoom or move an item)
//					//**************************************** 
//					// DO NOT remove from ACTION_DOWN
//					if (pTouchArea instanceof InventoryItem) {
//						
//						if (mode == MODE_INVENTORY || mode == MODE_ZOOM) {
//							touchedInventoryItem = (InventoryItem)pTouchArea;
//		
//							if (mode == MODE_INVENTORY) clickCheck = CLICK_INVENTORY;
//							// accept consecutive clicks
//							else clickCheck = CLICK_AGAIN_INVENTORY;
//						}	
//					}
//				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
//		    		//************************************
//		    		//     CLOSE TALK FRAME
//		    		//************************************   
//					if (status == STATUS_TALK) {
//						if (!talkScreen.talkScreenBg.isVisible()) {
//							talkScreen.clean();
//							status = STATUS_SCREEN;
//							mode = MODE_SCREEN_OFF;
//						}
//		    		//************************************
//		    		//     CLOSE MAP
//		    		//************************************   
//					} else if (status == STATUS_MAP) {
//						
//						if (pTouchArea instanceof Sprite) {	
//							touchedMapItem = (Sprite)pTouchArea;
//		
//							if (map.items.contains(touchedMapItem)) {					
//								map.hide();
//							}
//						}
//					}
//					
//		    		//************************************
//		    		//     CLOSE INFO FRAME
//		    		//************************************   
//					if (status == STATUS_ANIM) {
//						if (pTouchArea == frame.lookBg) {
//		
//							// avoid closing frame when touching the decorated part
//							if (pSceneTouchEvent.getY() <= INFORMATION_CLOSING_HEIGHT) {
//								frame.hide();
//								frame.lookBg.detachChildren();
//						        scene.unregisterTouchArea(frame.lookBg);
//								if (touchedZoomItem != null) displayZoomView();
//							}
//							
//						}
//						
//		    		//************************************
//		    		//     STATUS SCREEN
//					//************************************   
//					} else if (status == STATUS_SCREEN) {
//		
//			    		//************************************
//			    		//     MOVING ARROWS OR ACTION
//						//************************************   
//						if (pTouchArea instanceof AnimatedSprite) {		
//							
//							//*****************
//							// MOVING ARROWS
//							//*****************
//							
//							if (pTouchArea == gameTools.leftArrow || pTouchArea == gameTools.rightArrow) {
//		
//								if (pTouchArea == gameTools.leftArrow) {
//									
//									if (phDoll.getVelocityX() != 0 && dollDirection == DIRECTION_RIGHT) {
//										phDoll.setVelocityX(0);
//								    	doll.stopAnimation(16);
//										gameTools.rightArrow.stopAnimation(4);
//									}
//									touchedX = currentScreen.xMin;
//									gameTools.leftArrow.animate(120, true);
//									
//								} else if (pTouchArea == gameTools.rightArrow) {
//									
//									if (phDoll.getVelocityX() != 0 && dollDirection == DIRECTION_LEFT) {
//										phDoll.setVelocityX(0);
//								    	doll.stopAnimation(16);
//										gameTools.leftArrow.stopAnimation(4);
//									}
//									touchedX = currentScreen.xMax;
//									gameTools.rightArrow.animate(120, true);
//								}
//								moveDoll();
//								movingArrowPressed = true;
//						
//							} else {
//								//************************************
//					    		//     CHARACTER
//								//************************************						
//								if (pTouchArea instanceof PlayingAnimation) {							
//										mode = MODE_ACTION_WAIT;
//										touchedItem = null;
//										touchedArea = null;
//										touchedChar = (PlayingAnimation)pTouchArea;
//										touchedX = touchedChar.getX() + touchedChar.getWidth()/2;
//										
//										actionsManager.activate(touchedChar.getX(), touchedChar.getY(), touchedChar.getWidth(), touchedChar.getHeight(),
//												               world.getCharStates(touchedChar.id), false);
//		
//								} else {
//									mode = MODE_ACTION;
//									
//									//***********************************
//									//     EXIT
//									//***********************************
//									if (pTouchArea instanceof Exit) {
//										touchedExit = (Exit)pTouchArea;
//										touchedX = touchedExit.getX() + touchedExit.getWidth()/2;
//									
//									//***********************************
//									//     ACTION HANDLER
//									//***********************************
//									} else {
//										touchedAction = (AnimatedSprite)pTouchArea;
//										if (touchedX > currentScreen.xMin && touchedX < currentScreen.xMax && touchedAction != actionsManager.look) moveDoll();
//										else actionsManagerOutsideBorders = true;	
//									}
//								}
//							}					
//						//************************************
//			    		//     SCREEN ITEM
//						//************************************ 
//						} else if (pTouchArea instanceof ScreenItem) {
//							
//							// mode screen or another item has an action pending
//							
//							mode = MODE_ACTION_WAIT;
//							touchedChar = null;
//							touchedArea = null;
//							touchedItem = (ScreenItem)pTouchArea;
//							touchedX = touchedItem.getX() + touchedItem.getWidth()/2;
//								
//							actionsManager.activate(touchedItem.getX(), touchedItem.getY(), touchedItem.getWidth(), touchedItem.getHeight(),
//									               world.getItemStates(touchedItem.id), false);	
//						} if (pTouchArea instanceof Sprite) {
//							
//				    		//************************************
//				    		//     OPEN INVENTORY
//				    		//************************************
//							if (pTouchArea == inventory.bag) {
//		
//					    		if (!inventory.items.isEmpty()) {
//					    			status = STATUS_INVENTORY;
//									mode = MODE_INVENTORY;
//									displayInventory();
//					    		} else {
//					    			displayInfoFrame(world.getGeneralText("empty_inventory"));
//					    			clickCheck = CLICK_BAG; // avoid doll moving when touching bag in MODE_SCREEN
//					    		}
//				    		//************************************
//				    		//     OPEN DEV
//				    		//************************************
//						    } else if (pTouchArea == settings) {
//						    	dbh.deleteDatabase();
//						    	this.finish();
//				    		//************************************
//				    		//     OPEN MAP
//				    		//************************************
//						    } else if (pTouchArea == mapSprite) {
//					    		status = STATUS_MAP;
//					    		map.background.setPosition(camera.getMinX(), map.background.getY());
//					    		map.display();
//							} 
//						}
//						
//					} else if (status == STATUS_INVENTORY) {
//						
//						//TODO: if possible, close inventory when touching screen (and not only bag)
//						// close inventory
//					    if (pTouchArea == inventory.bag) {
//					    	
//					    	// inventory is open
//							if (mode == MODE_INVENTORY) {			
//								status = STATUS_SCREEN;
//								mode = MODE_SCREEN_OFF;
//								clickCheck = CLICK_BAG; // avoid doll moving when touching bag in MODE_SCREEN
//								hideInventory();
//							}
//					    }
//					}	
//					
//					if (pTouchArea == inventory.zoomView && inventory.zoomView.isVisible()) clickCheck = CLICK_ZOOM;
//					
//					else if (pTouchArea == devOpen) {
//				    	if (!devFrame.mask.isVisible()) {
//					        scene.setChildScene(devFrame);
//					        devFrame.display();	    		
//				    	} else {
//							world.save(doll.getX(), doll.getY());
//				    		load = devFrame.hide();
//				    		if (load > 0) {
//				    			devTools.setLoad(load);
//						    	Intent refresh = new Intent(this, Screen.class);
//						    	startActivity(refresh);
//						    	this.finish();
//				    		}
//				    	}
//				    }
//				}
			} catch (Exception e) {
				Log.e("Clementime", "Screen/onAreaTouched(): raise error: " + e.getMessage());
			}
		}
		return false;
	}
	
	/****************************/
	/* EVENT HANDLING METHODS */
	/****************************/

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
//		clickDetector.onSceneTouchEvent(null, pSceneTouchEvent);
		
		if (initScene) Log.i("Clementime", "Screen/onSceneTouchEvent(): scene initialisation - sceneTouchEvent desactivated"); 
		else {
			
			Log.d("Clementime", "Screen/onSceneTouchEvent(): status ");
			
			try {
				//************************************
				//     ANIMATED CIRCLE
				//************************************ 
				// show animated circle in modes action or screen only
//				if (status == STATUS_SCREEN) {
					float xCircle = pSceneTouchEvent.getX() - gameTools.animatedCircle.getWidth() / 2;
			    	float yCircle = pSceneTouchEvent.getY() - gameTools.animatedCircle.getHeight() / 2;
					gameTools.showAnimatedCircle(xCircle, yCircle);	
//				}	
				
//				//**********************************************
//				//     INVENTORY ITEM zooming and dropping
//				//********************************************** 
//				if (status == STATUS_INVENTORY && touchedInventoryItem != null && touchedInventoryItem != touchedZoomItem) {		
//		
//					//TODO: right/left handed (-50 = right handed)
//		        	// coordinates are set from top left to centre of the sprite
//					float x = pSceneTouchEvent.getX() - touchedInventoryItem.big.getWidth() / 2  - 50;
//		        	float y = pSceneTouchEvent.getY() - touchedInventoryItem.big.getHeight() / 2;
//		        	
//		        	//TODO: avoid mode drop when click zoom & dropping
//		        	
//		        	// mode DROP launched when an item is dragged outside the "list" of inventory items
//		        	if (mode == MODE_INVENTORY && (pSceneTouchEvent.getY() < INVENTORY_POSY_NORMALVIEW
//		        								|| pSceneTouchEvent.getX() > camera.getMinX() + inventory.items.size() * inventory.boxPlusInterval)) {
//		        		mode = MODE_DROP;
//		        		hideInventory();
//		       		}
//		        	
//					switch(pSceneTouchEvent.getAction()) {
//			        case TouchEvent.ACTION_DOWN:   
//			        	downTime = pSceneTouchEvent.getMotionEvent().getDownTime();
//			        	
//		            	touchedInventoryItem.setAlpha(INVBOX_ALPHA_LAYER);
//		            	touchedInventoryItem.small.setAlpha(INV_ALPHA_LAYER);       		
//		            	touchedInventoryItem.big.setVisible(true);	
//		                break;
//		            case TouchEvent.ACTION_MOVE:
//		            	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200 && pSceneTouchEvent.getMotionEvent().getEventTime() < downTime + 400) {
//		                	touchedInventoryItem.big.setZIndex(ZINDEX_INV_ITEM_IN_USE);
//		            		scene.sortChildren();
//		            	}
//		            	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200)
//		            		touchedInventoryItem.big.setPosition(x, y);
//		                break;
//		                
//		    		//********************************************************
//		    		//     COMBINATIONS: item dropped on something else
//		    		//********************************************************  
//			        case TouchEvent.ACTION_UP:
//			        	
//		        		int[] combinationResult = {0,0,0,0,0};	        	
//			        	
//		        		//************************************
//		        		//     ITEM DROPPED ON SCREEN
//		        		//************************************        		
//		        		if (mode == MODE_DROP) {
//		
//				        	ListIterator<ScreenItem> it = currentScreen.items.listIterator();
//				        	
//				        	boolean lookForCombination = true;
//		    				String text = "";
//		    				
//				    		while(it.hasNext() && lookForCombination) {
//				    			
//				    			ScreenItem item = it.next();
//				    			
//				    			if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, item)) {   	    		
//		
//				    				combinationResult = world.combineItems(touchedInventoryItem.id, item.id, DB_COMBINATION_VALUE_ON_SCREEN);
//				    				
//					    			if (combinationResult[0] != 0) {
//					    				
//					    				lookForCombination = false;
//					    				
//						    			text = world.getCombinationText(combinationResult[0]);
//					    				
//					    				//******************************************** 
//					    				//   LAUNCH TRIGGERS  if needed
//					    				//********************************************
//				    					// before removing item from inventory, in case trigger use item from inventory
//					    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);
//		
//				    					ListIterator<Integer> itTriggers = triggers.listIterator();
//				    					
//				    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(), false);
//					    			} // else text = world.getCombinationText(0); 
//		
//					    			if (combinationResult[1] != 0)	{
//					    				//************************************** 
//					    				//   REMOVE ITEMS if needed
//					    				//**************************************		    				
//				    					hideInventoryItem(touchedInventoryItem);
//		
//					    				//************************************** 
//					    				//   CREATE NEW ITEM if needed
//					    				//************************************** 			    				
//					    				// a combination on screen can change an item on screen or inventory (or just launch a trigger, if combinationResult[1] == 0)
//					    				if (combinationResult[1] != 0) {
//						    				if (combinationResult[2] == DB_COMBINATION_VALUE_ON_SCREEN) {
//						    					//TODO: doesn't work
//							    				ScreenItem newItem = addItemOnScreen(combinationResult[1]);			    					
//												scene.registerTouchArea(newItem);
//						    				} else {
//							    				//InventoryItem newItem = addItemInInventory(combinationResult[1]); // should be done after redrawing
//							    				addItemInInventory(combinationResult[1]); // should be done after redrawing
//						    				}			    					
//					    				}
//						    		}
//					    		}
//				    			
//		
//				    		}
//				    		if (text == "") text = world.getCombinationText(0); 
//			    			if (!frame.lookBg.isVisible()) displayInfoFrame(text);
//			    			
//				        	clickCheck = CLICK_OFF;
//				        	if (status != STATUS_ANIM) status = STATUS_SCREEN;
//				        	
//				       	//************************************
//			        	//   ITEM COMBINATION (in inventory)
//			        	//************************************        		    	 
//			        	} else if (mode == MODE_ZOOM) {
//		
//				    		if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, touchedZoomItem.big)) {
//				    		//if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, touchedZoomItem.zoom)) {
//				    			
//				    				
//				    			combinationResult = world.combineItems(touchedInventoryItem.id, touchedZoomItem.id, DB_COMBINATION_VALUE_IN_INVENTORY);
//				    			if (combinationResult[0] != 0)	{
//				    				
//				    				// clean zoom view, detach all sprites, update db and remove items
//				    				// don't detach sprites because it cause andEngine to crash
//				    				inventory.zoomView.detachChildren();
//			    					hideInventoryItem(touchedInventoryItem);
//			    					hideInventoryItem(touchedZoomItem);
//			    												
//				    				//******************************************** 
//				    				//   LAUNCHING TRIGGERS  if needed
//				    				//********************************************
//									//TODO: check if change is working
//				    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);
//		
//			    					ListIterator<Integer> itTriggers = triggers.listIterator();
//			    					
//			    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(),false);							
//				    				
//				    				inventory.redrawInventory(); // change position of boxes							
//		
//				    				InventoryItem newItem = addItemInInventory(combinationResult[1]); // should be done after redrawing
//									float xPos = camera.getMinX() + INVENTORY_POSX_ZOOM_ITEM - newItem.big.getWidth()/2;
//									float yPos = INVENTORY_POSY_ZOOM_ITEM - newItem.big.getHeight()/2;
//									newItem.big.setPosition(xPos, yPos);
//									newItem.big.setVisible(true);
//		//							float xPos = camera.getMinX() + INVENTORY_POSX_ZOOM_ITEM - newItem.zoom.getWidth()/2;
//		//							float yPos = INVENTORY_POSY_ZOOM_ITEM - newItem.zoom.getHeight()/2;
//		//							newItem.zoom.setPosition(xPos, yPos);
//		//							newItem.zoom.setVisible(true);
//									scene.registerTouchArea(newItem);
//									touchedZoomItem = newItem;
//									
//									inventory.zoomView.attachChild(inventory.textFormat(world.getCombinationText(combinationResult[0])));
//		
//				    			} else {
//				    				inventory.cleanText();
//									inventory.zoomView.attachChild(inventory.textFormat(world.getCombinationText(0)));		    			}
//			    			}
//			    		}
//		
//			        	hideBigItem();
//		
//			        	// if still positioned in CLICK_INVENTORY, clickCheck has to be reinitialised
//			        	// because it means that an item was moved but not clicked or used
//			        	if (clickCheck == CLICK_INVENTORY) clickCheck = CLICK_OFF;   
//				        
//			        	break;
//			    	}
//				//******************************************
//				//     NOT IN INVENTORY - other actions
//				//******************************************  
//				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
//		    		//************************************
//		    		//     CLOSE TALK FRAME
//		    		//************************************   
//					if (status == STATUS_TALK) {
//						if (!talkScreen.talkScreenBg.isVisible()) {
//							talkScreen.clean();
//							status = STATUS_SCREEN;
//							mode = MODE_SCREEN_OFF;
//						}	
//					}
//					
//					if (status == STATUS_SCREEN) {
//		
//						//**************************
//						//     ACTION
//						//**************************
//						// player activate an action or exit
//						if (mode == MODE_ACTION) {
//							if (touchedAction == actionsManager.look)		{
//								actionsManager.freeze(ACTION_LOOK);
//								look();
//							}
//							else if (touchedAction == actionsManager.take)			actionsManager.freeze(ACTION_TAKE);
//							else if (touchedAction == actionsManager.talk)		actionsManager.freeze(ACTION_TALK);
//							else { // exit
//								
//								touchedExit.stopAnimation();
//						    	gameTools.leftArrow.stopAnimation(4);
//						    	gameTools.rightArrow.stopAnimation(4);
//		
//								if (touchedExit.beforeTrigger == 0) moveDoll(); // no trigger on exit, move doll until 
//								else launchTrigger(touchedExit.beforeTrigger, false);
//		
//								//actionsManager.freeze(ACTION_EXIT);
//								
//								//int exitFeatures[] = world.getExitFeatures(touchedArea.id);
//								//if (touchedExit.beforeTrigger != 0) exit();
//							}
//							
//							touchedX = pSceneTouchEvent.getX();
//						//*********************************
//						//     SPECIAL AREAS ON SCREEN
//						//*********************************
//						// player touch the screen - check if an area is touched
//						} else if (clickCheck != CLICK_BAG) {
//		
//							Area area = null;
//							
//							area = checkAreas(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
//		
//							if (area != null) {
//								Log.d("Clementime", "Screen/onSceneTouchEvent(): activated area: " + area.id);
//								
//								mode = MODE_ACTION_WAIT;
//								touchedX = area.x + area.width/2;
//								
//								actionsManager.activate(area.x, area.y, area.width, area.height, world.getAreaStates(area.id), true);
//								
//								touchedItem = null;
//								touchedChar = null;
//								touchedArea = area;
//							}
//						}
//		
//			    		//************************************
//			    		//     SIMPLE MOVE DOLL
//			    		//************************************
//						if (mode == MODE_SCREEN_OFF) {
//							actionsManager.deactivate();
//		
//							// avoid doll moving when closing inventory or frame
//							if (clickCheck != CLICK_BAG) {
//								
//								// avoid doll stopping if moving arrow just pressed
//								if (!movingArrowPressed) {
//							    	gameTools.leftArrow.stopAnimation(4);
//							    	gameTools.rightArrow.stopAnimation(4);
//									touchedX = pSceneTouchEvent.getX();
//									moveDoll();						
//								} else movingArrowPressed = false;
//								
//							} else clickCheck = CLICK_OFF;				
//							
//						} else {
//					    	gameTools.leftArrow.stopAnimation(4);
//					    	gameTools.rightArrow.stopAnimation(4);
//							
//							if (mode == MODE_ACTION_WAIT) mode = MODE_SCREEN_OFF;					
//						}				
//		    		//************************************
//		    		//     CLOSE MAP
//		    		//************************************   
//					} else if (status == STATUS_MAP && touchedMapItem != null) {
//						goToNextScreen();
//					} else { 
//		
//						actionsManager.deactivate();
//				    	gameTools.leftArrow.stopAnimation(4);
//				    	gameTools.rightArrow.stopAnimation(4);
//		
//						if (status == STATUS_ANIM) {
//					    	// --------------- IF an animation is not running ---------------
//					    	// frame has been recently closed, come back to mode screen here
//					    	// (not before to avoid touch reacting to areas when closing frame) 
//							if (mode != MODE_ANIM_RUNNING && !frame.lookBg.isVisible() && !inventory.zoomView.isVisible()) {
//								status = STATUS_SCREEN;
//								mode = MODE_SCREEN_OFF;						
//							}					
//						} else { // stop doll
//							phDoll.setVelocityX(0);
//					    	doll.stopAnimation(16);				
//						}
//		
//					}
//				}
			} catch (Exception e) {
				Log.e("Clementime", "Screen/onSceneTouchEvent(): raise error: " + e.getMessage());
			}
		}
		return true;
	}
	
	/****************************/
	/* GENERAL (SCREEN) METHODS */
	/****************************/

	@Override
	public void onResume() {
		
		Log.i("Clementime","Screen/onResume()");
		
		super.onResume();
		
		app = (RdsGame)getApplicationContext();
		dbh = app.dbHandler;
		dbh.checkNewGame();

//		devTools = new Backup(dbh, context);
		
		try {
			if (dbh.checkDatabase()) dbh.open();
			else {
	  			Log.w("Clementime", "Screen/onResume(): cannot open database. Close RDS"); 
				this.finish();
			}		
		} catch (Exception e) {
  			Log.w("Clementime", "Screen/onResume(): cannot open database. Close RDS"); 
			this.finish();		
		}
		
		//TODO: load
//		if (DEVELOPMENT) {
//			if (devTools.getLoad() == 2) {
//				Log.i("Clementime","Screen/onResume(): --- DEV ON --- load saved version");
//				devTools.loadStateFiles("");
//				devTools.loadPlayerData("");				
//			} else if (!dbh.newGame)  {
//				Log.i("Clementime","Screen/onResume(): --- DEV ON --- load current version");
//				devTools.loadStateFiles("_current");
//				devTools.loadPlayerData("_current");
//			} else Log.i("Clementime","Screen/onResume(): --- DEV ON ---  start a new game");	
//		}
//		else if (!dbh.newGame)	Log.i("Clementime","Screen/onResume(): keep current version");					
//		else					Log.i("Clementime","Screen/onResume(): start a new game");	
	}
	
	@Override
	public void onPause() {
		
		Log.i("Clementime", "Screen/onPause()");
		
		super.onPause();
		if(dbh.db.isOpen()) {
//			// if screen is loaded, don't change player savings info as it will cancel loading 
//			if (load == 0) world.save(doll.getX(), doll.getY());
//			
//			if (DEVELOPMENT && load == 0 && touchedExit == null   ) {
//				devTools.createStateFile("_current");
//				devTools.createStateFiles("_current");
//				devTools.savePlayerData("_current");
//				Log.i("Clementime","Screen/onPause(): --- DEV ON --- save current version");
//			}
//			
			dbh.close();
		}
	}
}
