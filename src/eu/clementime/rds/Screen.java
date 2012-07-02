package eu.clementime.rds;

import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class Screen extends BaseGameActivity  {

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
//	private GameToolsManager gameTools;
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
//	private boolean initScene = false;
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
//		gameTools = new GameToolsManager(context, CAMERA_WIDTH, CAMERA_HEIGHT);
//		inventory = new Inventory(camera, font, context, dbh, CAMERA_WIDTH, CAMERA_HEIGHT, world.language);
//		talkScreen = new Talk(camera, talkFont, talkFont2, context, dbh, world.screenId, world.language);
//		frame = new Information(camera, talkFont);
//		actionsManager = new ActionsManager(camera); // hand, magnify glass, dialog bubble, exit
//		map = new Map(dbh, CAMERA_WIDTH, CAMERA_HEIGHT);
//		
//		mEngine.getTextureManager().loadTexture(inventory.loadImages());
//		mEngine.getTextureManager().loadTexture(talkScreen.loadImages(context));
//		mEngine.getTextureManager().loadTexture(frame.loadImages(context));
//		mEngine.getTextureManager().loadTexture(gameTools.loadGameItems());
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
		
//		initScene = true; // do not do anything during scene initialisation
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
//			scene.setOnSceneTouchListener(this);
//			scene.setOnAreaTouchListener(this);
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
			
			
			// TODO: remove following line
			camera.setCenter(550, CAMERA_HEIGHT / 2);
//
//		} catch (Exception e) {
//			Log.e("Clementime", "Screen/initNewScene(): raise error: " + e.getMessage());
//		}
//		
//		initScene = false;
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
