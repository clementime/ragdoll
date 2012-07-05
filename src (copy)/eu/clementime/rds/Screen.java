package eu.clementime.rds;

import static eu.clementime.rds.Constants.ACTION_LOOK;
import static eu.clementime.rds.Constants.ACTION_TAKE;
import static eu.clementime.rds.Constants.ACTION_TALK;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT;
import static eu.clementime.rds.Constants.CLICK_AGAIN_INVENTORY;
import static eu.clementime.rds.Constants.CLICK_BAG;
import static eu.clementime.rds.Constants.CLICK_DOLL;
import static eu.clementime.rds.Constants.CLICK_INVENTORY;
import static eu.clementime.rds.Constants.CLICK_OFF;
import static eu.clementime.rds.Constants.CLICK_ZOOM;
import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_IN_INVENTORY;
import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_ON_SCREEN;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_OUT;
import static eu.clementime.rds.Constants.DB_TABLE_COMBINATION;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.INVBOX_ALPHA_LAYER;
import static eu.clementime.rds.Constants.INVENTORY_POSX_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INVENTORY_POSY_NORMALVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSY_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INV_ALPHA_LAYER;
import static eu.clementime.rds.Constants.MODE_ACTION_WAIT;
import static eu.clementime.rds.Constants.MODE_ACTION_WALK;
import static eu.clementime.rds.Constants.MODE_ANIM_ACTION;
import static eu.clementime.rds.Constants.MODE_ANIM_RUNNING;
import static eu.clementime.rds.Constants.MODE_ANIM_TALK;
import static eu.clementime.rds.Constants.MODE_INVENTORY_DROP;
import static eu.clementime.rds.Constants.MODE_INVENTORY_OPEN;
import static eu.clementime.rds.Constants.MODE_INVENTORY_ZOOM;
import static eu.clementime.rds.Constants.NO_END_LOOP;
import static eu.clementime.rds.Constants.STATUS_ACTION;
import static eu.clementime.rds.Constants.STATUS_ANIM;
import static eu.clementime.rds.Constants.STATUS_INVENTORY;
import static eu.clementime.rds.Constants.STATUS_MAP;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM_IN_USE;

import static eu.clementime.rds.Global.CAMERA_WIDTH;
import static eu.clementime.rds.Global.CAMERA_HEIGHT;
import static eu.clementime.rds.Global.MARGIN_Y;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import org.anddev.andengine.collision.RectangularShapeCollisionChecker;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class Screen extends BaseGameActivity implements IOnAreaTouchListener,
IOnSceneTouchListener, IClickDetectorListener, IAccelerometerListener {

	//*
	//* Android & General objects
	//*******************************************************/
	private RdsGame app;
	private DatabaseHandler dbh;
	private Context context;
	
	private CharSequence text = "An error occurred during the game\nPlease contact xxx\nPlease send logs to zzz";
	private int duration = Toast.LENGTH_LONG;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
	//*
	//* AndEngine objects (loaded at game start)
	//*******************************************************/
	private final ClickDetector clickDetector = new ClickDetector(this);
	private Camera camera;
	private Scene scene = new Scene();
	private PhysicsHandler phAnimRunning;
	private Doll doll;

	//*
	//* changeable objects & screen variables (loaded at each screen)
	//*******************************************************/
	private Background currentBg;
	private Background nextBg;
	
	private float minChasingX = 0;
	private float maxChasingX = 0;
	
	//*
	//* persistent objects (loaded at game start)
	//*******************************************************/
	private Inventory inventory;
//	private Information frame;
	private GameTools gameTools;
//	private Map map;
	
	private World world;
	
	//*
	//* touched objects, variables & booleans => keep trace at runtime
	//*****************************************************************/	
	private AnimatedSprite touchedAction = null;
	private Exit touchedExit = null;
	private InventoryItem touchedInventoryItem = null;
	private InventoryItem touchedZoomItem = null;
	private ScreenItem touchedItem = null;
	private ScreenItem itemToBeRemoved = null;
//	private Sprite touchedMapItem = null;
	private Area touchedArea = null;
//	private PlayingAnimation touchedAnimation = null;
	private Anim touchedAnimation = null;

	private boolean movingArrowPressed = false;
	private boolean actionsManagerOutsideBorders = false;

	// doll moving
	private float touchedX = 0;

	// status
	private int status = STATUS_ACTION;
	private int mode = MODE_ACTION_WALK;
	private int clickCheck = CLICK_OFF;

	// animation sequences
	private int pendingTriggerId = 0;
	private int simultaneousTriggerId = 0;
	private int runningAnimId = 0;
	
	// switch screen
	private int firstScreenTriggerId = 0;
	private int[] startingPosition;
	private boolean deactivateTouchEvents = false;
	
	private float downTime;
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
	
	/**************************************/
	/* FIRST LOAD                         */
	/**************************************/
	
    @Override
    public Engine onLoadEngine() {   	
		
		Log.i("Clementime", "Screen/onLoadEngine()");
		
		try {
	    	context = getApplicationContext();
	    	getWindowManager().getDefaultDisplay().getMetrics(dm);
	    	
	       	CAMERA_WIDTH = dm.widthPixels;
	    	CAMERA_HEIGHT = dm.heightPixels;
	    	MARGIN_Y = (CAMERA_HEIGHT - BACKGROUND_MAX_HEIGHT)/2;	    	

	    	camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	
		} catch (Exception e){
			Log.e("Clementime", "Screen/onLoadEngine(): cannot set camera - " + e.getMessage());			
		}

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		//EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camera);
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
	
	private void loadResources() {
		
		/*
		/* starting game, persistent objects are set up
		/**************************************************/
		
		Log.i("Clementime", "Screen/loadResources()");

        //**********************	
		// DOLL
        //**********************
		doll = new Doll(dbh, context, mEngine, scene);		

        //**********************
		// SCREEN
        //**********************	
		world = new World(dbh, context, doll.getScreen());
		gameTools = new GameTools(dbh, context, mEngine, scene);
		inventory = new Inventory(camera, context, dbh, mEngine, scene);
		
        //**********************
		// FONTS
        //**********************
		getFontManager().loadFont(gameTools.font);
		getFontManager().loadFont(gameTools.defaultFont);
		getFontManager().loadFont(gameTools.defaultFont2);
		
//		talk = new Talk(camera, talkFont, talkFont2, context, dbh, world.screenId, world.language);
//		frame = new Information(camera, talkFont);
//		map = new Map(dbh, CAMERA_WIDTH, CAMERA_HEIGHT);
//		
//		// settings
//		settingsBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
//		settingsTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(settingsBTA, this, R.drawable.settings, 0, 0);
//		
//		// map
//		mapBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
//		mapTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(mapBTA, this, R.drawable.settings, 0, 0);
		
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
	
	@Override
	public Scene onLoadScene() {
			
		this.mEngine.registerUpdateHandler(new FPSLogger());	
		
		try {
			
			setLoop();
			
			/*
			/* prepare background here, because items are needed in inventory
			/******************************************************************************/
			loadNewScreen();
	
			/*
			/* setup inventory, talk screen, information frame, game tools (arrows, pointer...)
			/******************************************************************************/
	//        if (DEVELOPMENT) devFrame.setup();
	//        inventory.setup(nextBg.items); // TODO: inventory shouldn't be part of next scene
	//        talk.setup();
	//        frame.setup();
	//        map.setup();

			/*
			/* then set up background, items, animations and foreground of current screen
			/* (changed when new screen is loaded)
			/******************************************************************************/
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
	
	private void setLoop() {
		
		Log.i("Clementime", "Screen/setLoop()");

		// checks during runtime
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }
	
			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				float pointToChase = -1;
				boolean chaseAnim = false;

				if ((!checkStopDoll() && doll.ph.getVelocityX() != 0) || chaseAnim) {	
				
					//****************************************
					//     MOVE SPRITES WITH CAMERA
					//****************************************
					setToolsInPosition();
					
					if (doll.isChased) pointToChase = doll.image.getX() + doll.centerX; // chase doll
					
		    		//************************************
		    		//    CHASE DOLL or ANIM
		    		//************************************			
					if (pointToChase != -1 && pointToChase > minChasingX && pointToChase < maxChasingX) 
						camera.setCenter(pointToChase, CAMERA_HEIGHT / 2);					
				
				}	
			}
		});	
	}	

	private boolean checkStopDoll() {
		
		// if doll is moving, it can be stopped at runtime without player interaction if:
		// it reaches borders or it reaches touchedX (if moving arrows aren't activated)
		
		boolean stop = false;
		float stopX = 0;

		//Log.d("Clementime", "**************" + doll.image.getX() + "-" + doll.image.getY());

		// stop doll when reaching borders - if not during an animation
    	if ((doll.image.getX() < currentBg.xMin || doll.image.getX() > currentBg.xMax) && mode != MODE_ANIM_RUNNING) {	
			
			doll.ph.setVelocity(0,0);
	    	doll.image.stopAnimation(16);
	    	gameTools.leftArrow.stopAnimation(4);
	    	gameTools.rightArrow.stopAnimation(4);
	    	
	    	// avoid doll being stuck on one border
	    	if (doll.image.getX() < currentBg.xMin) doll.image.setPosition(currentBg.xMin, doll.image.getY());
	    	if (doll.image.getX() > currentBg.xMax) doll.image.setPosition(currentBg.xMax, doll.image.getY());
	    	
	    // stop doll at touchedX if the moving arrows aren't playing
		} else if (!gameTools.leftArrow.isAnimationRunning() && !gameTools.rightArrow.isAnimationRunning()) {
						
			stopX = touchedX - doll.centerX;		

			// if looking or talking to an item until which the doll can't move 
			if (mode == MODE_ANIM_ACTION && actionsManagerOutsideBorders) {
				gameTools.animatedCircle.stopAnimation(11);
				if (touchedAction == gameTools.am.talk)	talk();
				
				gameTools.am.deactivate();
								
				touchedItem = null;
//				touchedAnimation = null;
				touchedAction = null;
				touchedArea = null;	
				
				actionsManagerOutsideBorders = false;
			}
			
			// stop doll if moving arrows aren't playing
			if (doll.walkDirection == DIRECTION_RIGHT && doll.image.getX() >= stopX || doll.walkDirection == DIRECTION_LEFT && doll.image.getX() <= stopX) {   	

				doll.ph.setVelocity(0,0);
		    	doll.image.stopAnimation(16);
		    	gameTools.leftArrow.stopAnimation(4);
		    	gameTools.rightArrow.stopAnimation(4);

		    	// reasons why doll is stopping (other than simple moving on screen)
		    	if (mode == MODE_ANIM_ACTION) {

					gameTools.animatedCircle.stopAnimation(11);
					
			    	if (touchedExit != null) {
//						if (touchedExit.beforeTrigger == 0) goToNextScreen();
						
			    	} else if (touchedAction == gameTools.am.take)	take();
					else if (touchedAction == gameTools.am.talk)	talk();
					
					gameTools.am.deactivate();
					
					if (itemToBeRemoved != null) {								
						currentBg.hideShowItem(scene, itemToBeRemoved);
						touchedZoomItem = inventory.addItem(itemToBeRemoved.id, mEngine, scene);
						itemToBeRemoved = null;
					}
					
					touchedItem = null;
//					touchedAnimation = null;
					touchedAction = null;
					touchedArea = null;		    	
				}
				
			}
		}		
		
		if (doll.ph.getVelocityX() == 0 && doll.ph.getVelocityY() == 0) stop = true;
		
		return stop;
	}	
	
	@Override
	public void onLoadComplete() {
		
	}
	
	/**************************************/
	/* LOAD NEW SCREEN                    */
	/**************************************/
	
	private void loadNewScreen() {
		
		Log.i("Clementime", "Screen/loadNewScreen()");

		//******************************************************************************
		// items, areas, animations and talk screen chars (scene depending objects)
		//*****************************************************************************
		//screenFeatures = world.getScreenFeatures(lastExitId);
		
		// get position of doll from player table - on x beginning position is set to -1
		startingPosition = doll.getPosition();
		
		// get new position from database if:
		// 1. doll comes from another exit (exit >= 1)
		// 2. game begins (exit=0 & x beginning position isn't set by anterior playing => -1)
		if (touchedExit != null) {
			startingPosition[0] = touchedExit.startingX; 
			startingPosition[1] = touchedExit.startingY; 
			createNewBackground(touchedExit.toScreen);
			
			Log.i("Clementime", "Screen/loadNewScreen(): enter a new screen from 'last' exit: " + touchedExit.id);
		} else if (startingPosition[0] == -1) {
			int[] features = world.getFirstScreenFeatures();
			startingPosition[0] = features[1]; 
			startingPosition[1] = features[2]; 
			firstScreenTriggerId = features[0];
			createNewBackground(1);
			
			Log.i("Clementime", "Screen/loadNewScreen(): starting new game ");
//		} else if(touchedMapItem != null) {
//			int[] features = world.getScreenFeatures(world.screenId);
//			startingPosition[0] = features[1]; 
//			startingPosition[1] = features[2]; 
//			firstScreenTriggerId = features[0];
//			
//			touchedMapItem = null;
//			createNewBackground(Integer.parseInt(touchedMapItem.getUserData().toString()));
			
		} else {
			createNewBackground(doll.getScreen());
			
			Log.i("Clementime", "Screen/loadNewScreen(): player in screen: " + nextBg.screenId);
		}
	}
	
	private void createNewBackground(int screenId) {
		
		nextBg = new Background(dbh, context, screenId, mEngine, scene, gameTools.am.exitLeftTR, gameTools.am.exitRightTR);

		
	}
	
	public void initNewScene() {
		
		Log.i("Clementime", "Screen/initNewScene()");
		
		deactivateTouchEvents = true; // do not do anything during scene initialisation
		
		try {
			// hide objects when changing screen
			showPersistentObjects(false);

			doll.getYVelocity(currentBg.screenId);	// set standard velocity for current screen
			
			scene.setOnSceneTouchListener(this);
			scene.setOnAreaTouchListener(this);
			
			// variables to chase doll or animation with the camera
			minChasingX = CAMERA_WIDTH / 2;
			maxChasingX = currentBg.bgImage.getWidth() - CAMERA_WIDTH / 2;	
			
			/*
			/* set persistent objects in position
			/********************************************/		
			doll.image.setPosition(startingPosition[0], startingPosition[1] + MARGIN_Y);
			
			// set camera focus on doll at start
			if (startingPosition[0] >= currentBg.bgImage.getWidth() - CAMERA_WIDTH/2)	camera.setCenter(currentBg.bgImage.getWidth() - CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
			else if (startingPosition[0] <= CAMERA_WIDTH/2)								camera.setCenter(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);			
			else 																		camera.setCenter(startingPosition[0] + doll.centerX, CAMERA_HEIGHT/2);

			setToolsInPosition();

			showPersistentObjects(true);
			currentBg.showStaticAnims(scene);
			currentBg.showExits(scene);
//			
//			if (touchedExit != null) {
//				if (touchedExit.afterTrigger != 0) launchTrigger(touchedExit.afterTrigger, false);
//				touchedExit = null;				
//			} else if (firstScreenTriggerId != 0) {
//				launchTrigger(firstScreenTriggerId, false);
//				firstScreenTriggerId = 0;
//			} else {
//				status = STATUS_ACTION;
//				mode = MODE_ACTION_WALK;
//			}
//
//
			scene.sortChildren();	

		} catch (Exception e) {
			Log.e("Clementime", "Screen/initNewScene(): raise error: " + e.getMessage());
		}
		
		deactivateTouchEvents = false;
	}
	
	private void setToolsInPosition() {

//		// move info frame with the doll
//		if (frame.lookBg.isVisible()) frame.lookBg.setPosition(camera.getMinX() + INFORMATION_POSX, frame.lookBg.getY());

		// moving arrows are shown if there is a possibility to travel within the screen
		gameTools.checkBorders(camera.getMinX(), camera.getMaxX(), currentBg.xMin, currentBg.xMax);			

//		settings.setPosition(camera.getMinX() + 400, settings.getY());
//		mapSprite.setPosition(camera.getMinX() + 400, mapSprite.getY());
//		if (DEVELOPMENT) devOpen.setPosition(camera.getMinX() + 480/2-devOpen.getWidth()/2, devOpen.getY());
	}
	
	private void showPersistentObjects(boolean choice) {
		
		Log.i("Clementime", "Screen/showPersistentObjects(): " + choice);
		
		doll.setVisible(choice);
		gameTools.setVisible(choice);
	}
	
	/**************************************/
	/* TOUCH DETECTION && GESTURE METHODS */
	/**************************************/

	//**********************//
	//*** EVENT REMINDER ***//
	// EVENT ORDER :
	// AreaTouched DOWN
	// SceneTouchEvent DOWN
	// AreaTouched UP
	// OnClick
	// SceneTouchEvent UP
	//**********************//
	// if no area is touched, AreaTouched isn't called
	// if no click happens, OnClick isn't called
	//**********************//

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if (deactivateTouchEvents) Log.i("Clementime", "Screen/onAreaTouched(): scene initialisation - touchArea desactivated"); 
		else {
			Log.i("Clementime", "Screen/onAreaTouched(): status " + status + " - mode " + mode + " - + + touch " + pTouchArea.toString());
			
			try {	
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					
					//**************************************** !!! DO NOT remove from ACTION_DOWN !!!
					//   INVENTORY (to zoom or move an item)
					//****************************************
					if (pTouchArea instanceof InventoryItem) selectInventoryItem((InventoryItem)pTouchArea);					

				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		    		//************************************
		    		//     CLOSE TALK FRAME
		    		//************************************   
					if (status == STATUS_ANIM && mode == MODE_ANIM_TALK) closeTalk();

		    		//************************************
		    		//     CLOSE MAP
		    		//************************************   
					else if (status == STATUS_MAP && pTouchArea instanceof Sprite) closeMap();

		    		//************************************
		    		//     CLOSE TALK
		    		//************************************   
//					if (status == STATUS_ANIM && pTouchArea == frame.lookBg) closeInfoFrame();
		
		    		//****************************************************************************************
		    		//     STATUS ACTION		>> doll can walk & try to take/look/talk, or open inventory
					//****************************************************************************************   
					else if (status == STATUS_ACTION) {
		
						if (pTouchArea instanceof AnimatedSprite) {		
				    		//************************************
				    		//     DOLL		>> OPEN INVENTORY
				    		//************************************
							if (pTouchArea == doll.image) openInventory();

							//***********************************
							//		MOVING ARROWS
							//***********************************						
							else if (pTouchArea == gameTools.leftArrow)		activateMovingArrow(DIRECTION_LEFT);
							else if (pTouchArea == gameTools.rightArrow)	activateMovingArrow(DIRECTION_RIGHT);
							
							//**********************************************
				    		//     ACTION MANAGER on CHARACTER or ANIM
							//**********************************************							
							else if (pTouchArea instanceof Anim) activateActionOnAnim((Anim)pTouchArea);
	
							else {
								mode = MODE_ANIM_ACTION;
								
								//***********************************
								//     EXIT
								//***********************************
								if (pTouchArea instanceof Exit) {
									touchedExit = (Exit)pTouchArea;
									touchedX = touchedExit.getX() + touchedExit.getWidth()/2;
								
								//**************************************************************************************
								//     ACTION MANAGER	>> move doll towards item/anim if talk/take and inside borders
								//**************************************************************************************
								} else {
									touchedAction = (AnimatedSprite)pTouchArea;
									if (touchedX > currentBg.xMin && touchedX < currentBg.xMax && touchedAction != gameTools.am.look) doll.move(status, touchedX);
									else actionsManagerOutsideBorders = true;	
								}
							}					
						//******************************************************
			    		//     SCREEN ITEM >> activate ACTION MANAGER on ITEM
						//******************************************************
						} else if (pTouchArea instanceof ScreenItem) activateActionOnItem((ScreenItem)pTouchArea);
							
						else if (pTouchArea instanceof Sprite) {						

				    		//************************************
				    		//     OPEN DEV
				    		//************************************
//						    if (pTouchArea == settings) {
//						    	dbh.deleteDatabase();
//						    	this.finish();
				    		//************************************
				    		//     OPEN MAP
				    		//************************************
//						    } else if (pTouchArea == mapSprite) {
//					    		status = STATUS_MAP;
//					    		map.background.setPosition(camera.getMinX(), map.background.getY());
//					    		map.display();
//							} 
						}
					}
		    		//************************************
		    		//     CLOSE ZOOM
		    		//************************************
					else if (pTouchArea == inventory.zoomView && inventory.zoomView.isVisible()) clickCheck = CLICK_ZOOM;
					
//					else if (pTouchArea == devOpen) {
//				    	if (!devFrame.mask.isVisible()) {
//					        scene.setChildScene(devFrame);
//					        devFrame.display();	    		
//				    	} else {
//							world.save(doll.image.getX(), doll.image.getY());
//				    		load = devFrame.hide();
//				    		if (load > 0) {
//				    			devTools.setLoad(load);
//						    	Intent refresh = new Intent(this, Screen.class);
//						    	startActivity(refresh);
//						    	this.finish();
//				    		}
//				    	}
//				    }
				}
			} catch (Exception e) {
				Log.e("Clementime", "Screen/onAreaTouched(): raise error: " + e.getMessage());
			}
		}
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		clickDetector.onSceneTouchEvent(null, pSceneTouchEvent);
		
		if (deactivateTouchEvents) Log.i("Clementime", "Screen/onSceneTouchEvent(): scene initialisation - sceneTouchEvent desactivated"); 
		else {
			
			Log.d("Clementime", "Screen/onSceneTouchEvent(): status " + status + " - mode " + mode);
			
			try {
				//************************************
				//     ANIMATED CIRCLE
				//************************************ 
				// show animated circle when status is ACTION
				if (status == STATUS_ACTION) {
					float xCircle = pSceneTouchEvent.getX() - gameTools.animatedCircle.getWidth() / 2;
			    	float yCircle = pSceneTouchEvent.getY() - gameTools.animatedCircle.getHeight() / 2;
					gameTools.showAnimatedCircle(xCircle, yCircle);	
				}	
				
				//**********************************************
				//     INVENTORY ITEM zooming and dropping
				//********************************************** 
				if (status == STATUS_INVENTORY && touchedInventoryItem != null && touchedInventoryItem != touchedZoomItem) {
					
					manageInventory(pSceneTouchEvent);			
 
				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {

					//******************************************
					//     CLOSE TALK FRAME
					//******************************************
					if (status == STATUS_ANIM && mode == MODE_ANIM_TALK) closeTalk();

					//******************************************
					//     TOUCH SCREEN IN ACTION MODE
					//******************************************
					else if (status == STATUS_ACTION)	manageAction(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());

					//******************************************
					//     CLOSE INVENTORY
					//******************************************
					else if (status == STATUS_INVENTORY && mode == MODE_INVENTORY_OPEN) closeInventory();
					
		    		// CLOSE MAP
//					else if (status == STATUS_MAP && touchedMapItem != null) {
//						goToNextScreen();
//					}
					else { 
						gameTools.am.deactivate();
				    	gameTools.leftArrow.stopAnimation(4);
				    	gameTools.rightArrow.stopAnimation(4);
		
						if (status == STATUS_ANIM) {
					    	// --------------- IF an animation is not running ---------------
					    	// frame has been recently closed, come back to mode screen here
					    	// (not before to avoid touch reacting to areas when closing frame) 
//							if (mode != MODE_ANIM_RUNNING && !frame.lookBg.isVisible() && !inventory.zoomView.isVisible()) {
//								status = STATUS_ACTION;
//								mode = MODE_ACTION_WALK;						
//							}					
						} else { // stop doll
							doll.ph.setVelocityX(0);
					    	doll.image.stopAnimation(16);				
						}
		
					}
				}
			} catch (Exception e) {
				Log.e("Clementime", "Screen/onSceneTouchEvent(): raise error: " + e.getMessage());
			}
		}
		return true;
	}
	  
	@Override
	public void onClick(final ClickDetector pClickDetector, final TouchEvent pTouchEvent) {

		if (deactivateTouchEvents) Log.i("Clementime", "Screen/onClick(): scene initialisation - click desactivated"); 
		else {
			try {
				
				if (clickCheck == CLICK_INVENTORY)				zoomOnItem();
				else if (clickCheck == CLICK_AGAIN_INVENTORY)	switchZoomedItem();
				else if (clickCheck == CLICK_ZOOM)				closeZoom();
				
				else Log.i("Clementime", "Screen/onClick(): clic outside inventory");
				
			} catch (Exception e) {
				Log.e("Clementime", "Screen/onClick(): raise error: " + e.getMessage());
			}
		}
	}
		
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		inventory.moveItemList(camera.getMinX(), pAccelerometerData.getX());
	}
	
	/**************************************/
	/* ON AREA/ON SCENE TOUCH SUB METHODS */
	/**************************************/
	
	private void manageAction(float x, float y) {

		Log.d("Clementime", "Screen/manageAction(): status " + status + " - mode " + mode);

		//**************************
		//     ACTION
		//**************************
		// player activate an action or exit
		if (mode == MODE_ANIM_ACTION) {
			if (touchedAction == gameTools.am.look)	{
				gameTools.am.freeze(ACTION_LOOK);
//				look();
			}
			else if (touchedAction == gameTools.am.take)		gameTools.am.freeze(ACTION_TAKE);
			else if (touchedAction == gameTools.am.talk)		gameTools.am.freeze(ACTION_TALK);
			else { // exit
				
				touchedExit.stopAnimation();
		    	gameTools.leftArrow.stopAnimation(4);
		    	gameTools.rightArrow.stopAnimation(4);

//				if (touchedExit.beforeTrigger == 0) doll.move(status, touchedX); // no trigger on exit, move doll until 
//				else launchTrigger(touchedExit.beforeTrigger, false);
			}
			
			touchedX = x;
		//*********************************
		//     SPECIAL AREAS ON SCREEN
		//*********************************
		// player touch the screen - check if an area is touched
		} else if (clickCheck != CLICK_BAG) {

			Area area = null;
			
			area = currentBg.checkAreas(x, y);

			if (area != null) {
				Log.d("Clementime", "Screen/onSceneTouchEvent(): activated area: " + area.id);
				
				mode = MODE_ACTION_WAIT;
				touchedX = area.x + area.width/2;
				
				gameTools.am.activate(area.x, area.y, area.width, area.height, currentBg.getAreaStates(area.id), true);
				
				touchedItem = null;
				touchedAnimation = null;
				touchedArea = area;
			}
		}

		//************************************
		//     SIMPLE MOVE DOLL
		//************************************
		if (mode == MODE_ACTION_WALK) {

			gameTools.am.deactivate();

			// avoid doll moving when closing inventory or frame
			if (clickCheck != CLICK_BAG) {
				
				// avoid doll stopping if moving arrow just pressed
				if (!movingArrowPressed) {
			    	gameTools.leftArrow.stopAnimation(4);
			    	gameTools.rightArrow.stopAnimation(4);
					touchedX = x;
					doll.move(status, touchedX);						
				} else movingArrowPressed = false;
				
			} else clickCheck = CLICK_OFF;				
			
		} else {
	    	gameTools.leftArrow.stopAnimation(4);
	    	gameTools.rightArrow.stopAnimation(4);
			
			if (mode == MODE_ACTION_WAIT) mode = MODE_ACTION_WALK;					
		}				
	}
	
	private void manageInventory(TouchEvent pSceneTouchEvent) {
		
		//**********************************************
		//     INVENTORY ITEM zooming and dropping
		//********************************************** 
			//TODO: right/left handed (-50 = right handed)
        	// coordinates are set from top left to centre of the sprite
			float x = pSceneTouchEvent.getX() - touchedInventoryItem.big.getWidth() / 2  - 50;
        	float y = pSceneTouchEvent.getY() - touchedInventoryItem.big.getHeight() / 2;
        	
        	//TODO: avoid mode drop when click zoom & dropping
        	
        	// mode DROP launched when an item is dragged outside the "list" of inventory items
        	if (mode == MODE_INVENTORY_OPEN && (pSceneTouchEvent.getY() < INVENTORY_POSY_NORMALVIEW
        								|| pSceneTouchEvent.getX() > camera.getMinX() + inventory.items.size() * inventory.boxPlusInterval)) {
        		mode = MODE_INVENTORY_DROP;
        		hideInventory();
       		}
        	
			switch(pSceneTouchEvent.getAction()) {
	        case TouchEvent.ACTION_DOWN:   
	        	downTime = pSceneTouchEvent.getMotionEvent().getDownTime();
	        	
            	touchedInventoryItem.setAlpha(INVBOX_ALPHA_LAYER);
            	touchedInventoryItem.small.setAlpha(INV_ALPHA_LAYER);       		
            	touchedInventoryItem.big.setVisible(true);	
                break;
            case TouchEvent.ACTION_MOVE:
            	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200 && pSceneTouchEvent.getMotionEvent().getEventTime() < downTime + 400) {
                	touchedInventoryItem.big.setZIndex(ZINDEX_INV_ITEM_IN_USE);
            		scene.sortChildren();
            	}
            	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200)
            		touchedInventoryItem.big.setPosition(x, y);
                break;
                
    		//********************************************************
    		//     COMBINATIONS: item dropped on something else
    		//********************************************************  
	        case TouchEvent.ACTION_UP:
	        	
        		int[] combinationResult = {0,0,0,0,0};	        	
	        	
        		//************************************
        		//     ITEM DROPPED ON SCREEN
        		//************************************        		
        		if (mode == MODE_INVENTORY_DROP) {

		        	ListIterator<ScreenItem> it = currentBg.items.listIterator();
		        	
		        	boolean lookForCombination = true;
    				
		    		while(it.hasNext() && lookForCombination) {
		    			
		    			ScreenItem item = it.next();
		    			
		    			if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, item)) {   	    		

		    				combinationResult = world.combineItems(touchedInventoryItem.id, item.id, DB_COMBINATION_VALUE_ON_SCREEN);
		    				
			    			if (combinationResult[0] != 0) {
			    				
			    				lookForCombination = false;
			    				
			    				//******************************************** 
			    				//   LAUNCH TRIGGERS  if needed
			    				//********************************************
		    					// before removing item from inventory, in case trigger use item from inventory
			    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);

		    					ListIterator<Integer> itTriggers = triggers.listIterator();
		    					
		    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(), false);
			    			}

			    			if (combinationResult[1] != 0)	{
			    				//************************************** 
			    				//   REMOVE ITEMS if needed
			    				//**************************************		    				
		    					inventory.hideItem(touchedInventoryItem);

			    				//************************************** 
			    				//   CREATE NEW ITEM if needed
			    				//************************************** 			    				
			    				// a combination on screen can change an item on screen or inventory (or just launch a trigger, if combinationResult[1] == 0)
			    				if (combinationResult[1] != 0) {
				    				if (combinationResult[2] == DB_COMBINATION_VALUE_ON_SCREEN) {
				    					//TODO: doesn't work
					    				ScreenItem newItem = currentBg.addItemOnScreen(combinationResult[1], mEngine, scene);			    					
										scene.registerTouchArea(newItem);
				    				} else {
					    				inventory.addItem(combinationResult[1], mEngine, scene); // should be done after redrawing
				    				}			    					
			    				}
				    		}
			    		}
		    			

		    		}

	    			
		        	clickCheck = CLICK_OFF;
		        	if (status != STATUS_ANIM) status = STATUS_ACTION;
		        	
		       	//************************************
	        	//   ITEM COMBINATION (in inventory)
	        	//************************************        		    	 
	        	} else if (mode == MODE_INVENTORY_ZOOM) {

		    		if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, touchedZoomItem.big)) {
		    				
		    			combinationResult = world.combineItems(touchedInventoryItem.id, touchedZoomItem.id, DB_COMBINATION_VALUE_IN_INVENTORY);
		    			if (combinationResult[0] != 0)	{
		    				
		    				// clean zoom view, detach all sprites, update db and remove items
		    				// don't detach sprites because it cause andEngine to crash
		    				inventory.zoomView.detachChildren();
	    					inventory.hideItem(touchedInventoryItem);
	    					inventory.hideItem(touchedZoomItem);
	    												
		    				//******************************************** 
		    				//   LAUNCHING TRIGGERS  if needed
		    				//********************************************
							//TODO: check if change is working
		    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);

	    					ListIterator<Integer> itTriggers = triggers.listIterator();
	    					
	    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(),false);							
		    				
		    				inventory.redrawInventory(); // change position of boxes							

		    				InventoryItem newItem = inventory.addItem(combinationResult[1], mEngine, scene); // should be done after redrawing
							float xPos = camera.getMinX() + INVENTORY_POSX_ZOOM_ITEM - newItem.big.getWidth()/2;
							float yPos = INVENTORY_POSY_ZOOM_ITEM - newItem.big.getHeight()/2;
							newItem.big.setPosition(xPos, yPos);
							newItem.big.setVisible(true);
							scene.registerTouchArea(newItem);
							touchedZoomItem = newItem;
		    			}
	    			}
	    		}

	        	hideBigItem();

	        	// if still positioned in CLICK_INVENTORY, clickCheck has to be reinitialised
	        	// because it means that an item was moved but not clicked or used
	        	if (clickCheck == CLICK_INVENTORY) clickCheck = CLICK_OFF;   
		        
	        	break;
	    	}
	}
	
	private void activateMovingArrow(int direction) {
		
		if (direction == DIRECTION_LEFT) {
			
			if (doll.ph.getVelocityX() != 0 && doll.walkDirection == DIRECTION_RIGHT) {
				doll.ph.setVelocityX(0);
		    	doll.image.stopAnimation(16);
				gameTools.rightArrow.stopAnimation(4);
			}
			touchedX = currentBg.xMin;
			gameTools.leftArrow.animate(120, true);
			
		} else {
			
			if (doll.ph.getVelocityX() != 0 && doll.walkDirection == DIRECTION_LEFT) {
				doll.ph.setVelocityX(0);
		    	doll.image.stopAnimation(16);
				gameTools.leftArrow.stopAnimation(4);
			}
			touchedX = currentBg.xMax;
			gameTools.rightArrow.animate(120, true);
		}
		doll.move(status, touchedX);
		movingArrowPressed = true;
	}
	
	private void closeTalk() {
		//************************************
		//     CLOSE TALK FRAME
		//************************************   
		//if (status == STATUS_TALK) {
		if (status == STATUS_ANIM && mode == MODE_ANIM_TALK) {
//			if (!talk.talkBg.isVisible()) {
//				talk.clean();
				status = STATUS_ACTION;
				mode = MODE_ACTION_WALK;
//			}	
		}
	}

	private void closeMap() {

//		touchedMapItem = (Sprite)pTouchArea;
//
//		if (map.items.contains(touchedMapItem)) {					
//			map.hide();
//		}

	}
	
	private void closeInfoFrame() {
//		// avoid closing frame when touching the decorated part
//		if (pSceneTouchEvent.getY() <= INFORMATION_CLOSING_HEIGHT) {
//			frame.hide();
//			frame.lookBg.detachChildren();
//	        scene.unregisterTouchArea(frame.lookBg);
//			if (touchedZoomItem != null) displayZoomView();
//		}	
	}
	
	private void openInventory() {
		
		doll.ph.setVelocity(0,0);
    	doll.image.stopAnimation(16);
		
		if (!inventory.items.isEmpty()) {
			status = STATUS_INVENTORY;
			mode = MODE_INVENTORY_OPEN;
			displayInventory();
			clickCheck = CLICK_DOLL;
		} else {
			doll.sayNo();
//			displayInfoFrame(world.getGeneralText("empty_inventory"));
		}	
	}
	
	private void closeInventory() {
		
		// avoid closing inventory right after opening it
		if (clickCheck != CLICK_DOLL && clickCheck != CLICK_ZOOM) {
			status = STATUS_ACTION;
			mode = MODE_ACTION_WALK;
			hideInventory();								
		} else clickCheck = CLICK_OFF;
	}
	
	private void activateActionOnAnim(Anim anim) {
		
		mode = MODE_ACTION_WAIT;
		touchedItem = null;
		touchedArea = null;
		touchedAnimation = anim;
		touchedX = touchedAnimation.getX() + touchedAnimation.getWidth()/2;
		
		gameTools.am.activate(touchedAnimation.getX(), touchedAnimation.getY(), touchedAnimation.getWidth(), touchedAnimation.getHeight(),
				               currentBg.getAnimStates(touchedAnimation.id), false);
	}
	
	private void activateActionOnItem(ScreenItem screenItem) {
		
		// mode screen or another item has an action pending
		mode = MODE_ACTION_WAIT;
		touchedAnimation = null;
		touchedArea = null;
		touchedItem = screenItem;
		touchedX = touchedItem.getX() + touchedItem.getWidth()/2;

		gameTools.am.activate(touchedItem.getX(), touchedItem.getY(), touchedItem.getWidth(), touchedItem.getHeight(),
				               currentBg.getItemStates(touchedItem.id), false);	
	}
	
	private void selectInventoryItem(InventoryItem item) {
		if (mode == MODE_INVENTORY_OPEN || mode == MODE_INVENTORY_ZOOM) {
			touchedInventoryItem = item;

			if (mode == MODE_INVENTORY_OPEN) clickCheck = CLICK_INVENTORY;
			// accept consecutive clicks
			else clickCheck = CLICK_AGAIN_INVENTORY;
		}	
	}

	/**************************************/
	/* ON CLICK SUB METHODS               */
	/**************************************/
	
	private void zoomOnItem() {
		
		Log.i("Clementime", "Screen/onClick(): clic on inventory");
		
		mode = MODE_INVENTORY_ZOOM;
		touchedZoomItem = touchedInventoryItem;
		inventory.displayZoomView(camera.getMinX(), touchedZoomItem, scene);
		
		clickCheck = CLICK_OFF;	
	}
	
	private void switchZoomedItem() {
		// accept consecutive clicks (= zoom again when already in zoom mode)
		
		Log.i("Clementime", "Screen/onClick(): clic again on inventory");

		// clean zoom image before drawing another
		touchedZoomItem.big.setPosition(-200, 0);
		touchedZoomItem.setAlpha(1);
		touchedZoomItem.small.setAlpha(1);
		touchedZoomItem = touchedInventoryItem;
		
		inventory.displayZoomItem(camera.getMinX(), touchedZoomItem);

		clickCheck = CLICK_OFF;
		
	}
	
	private void closeZoom() {
		
		Log.i("Clementime", "Screen/closeZoom()");

    	if (status == STATUS_INVENTORY) mode = MODE_INVENTORY_OPEN;	
		inventory.hideZoomView(touchedZoomItem, scene);
		touchedZoomItem = null;
		clickCheck = CLICK_OFF;
		
	}
	
	/*************************************/
	/* ACTION METHODS */
	/*************************************/

	private void take() {
		
		Log.i("Clementime", "Screen/take()");
		
		if (world.isItemTakeable(touchedItem.id)) itemToBeRemoved = touchedItem;

		mode = MODE_ACTION_WALK;
	}

	private void look() {

		Log.i("Clementime", "Screen/look()");

		String text = "";
		
//		if (touchedItem != null)
//			text = world.getDesc(touchedItem.id, OBJECT_TYPE_ITEM, DB_DESCRIPTION_ACTION_LOOK);
//		else if (touchedArea != null)
//			text = world.getDesc(touchedArea.id, OBJECT_TYPE_AREA, DB_DESCRIPTION_ACTION_AREA_LOOK);
//		else if (touchedAnimation != null)
//			text = world.getDesc(touchedAnimation.id, OBJECT_TYPE_CHAR, DB_DESCRIPTION_ACTION_CHAR_LOOK);
//
//		displayInfoFrame(text);
	}

	private void talk() {

		Log.i("Clementime", "Screen/talk()");

//		status = STATUS_TALK;
//		talkScreen.display(touchedAnimation.id);
//      scene.setChildScene(talkScreen);
	}
	
	/*************************************/
	/* INVENTORY METHODS */
	/*************************************/
	
	public void displayInventory() {

		Log.i("Clementime", "Screen/displayInventory()");
		
		this.enableAccelerometerSensor(this);
		inventory.display(camera.getMinX(), scene);
	}
	
	public void hideInventory() {
		
		Log.i("Clementime", "Screen/hideInventory()");
		
		this.disableAccelerometerSensor();
		inventory.hide();	
	}
	
	/****************************/
	/* GENERAL GAME METHODS */
	/****************************/
	
	private void launchTrigger(int triggerId, boolean simultaneous) {

		//********************************************************************************************************
		// results[0]: next trigger			- if there is another trigger launched following this one, id of the trigger, 0 if not 		
		// results[1]: animation on screen	- if the trigger is to animate a screenAnim, id of the screeAnim
		// results[2]: doll moving			- if the trigger is to animate the doll, x where the doll has to be moved
		// results[3]: doll is hidden		- if doll is moving, it can be off screen
		// results[4]: text activated		- if an animation text is activated, id of this text
		// results[5]: add item to inventory- id of item to add in inventory
		// results[6]: doll moving y velocity
		// results[7]: hide/show screen item
		// results[8]: hide/show animation
		// results[9]: simultaneous trigger		- if there is another trigger to launch at the same time, id of the trigger
		//********************************************************************************************************
		
		if (simultaneous) Log.i("Clementime", "Screen/launchTrigger(): launch simultaneous trigger " + triggerId);
		else Log.i("Clementime", "Screen/launchTrigger(): launch pending trigger " + triggerId);
		
		simultaneousTriggerId = 0;
		
		// check and launch any trigger or trigger sequence
		int[] triggerResult = world.activateTrigger(triggerId);

		// the trigger launch an animation
		if (triggerResult[1] != 0) {
			launchAnim(triggerResult[1]);
			//animationToWaitForId = triggerResult[1];
			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
		}
			
		// the trigger move the doll (special animation)
		if (triggerResult[2] != 0) {
			Log.d("Clementime", "Screen/launchTrigger(): move Doll ");
			
			touchedX = triggerResult[2] + doll.centerX; // doll.getWidth()/2 is removed when check stop doll 

			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
			
			doll.YVelocity = triggerResult[6];
			doll.move(status, touchedX);
			doll.isChased = true; // to come back to chase doll you have to move it
		}

		// the trigger display a text
		if (triggerResult[4] != 0) {
			//displayInfoFrame(world.getDesc(triggerResult[4], OBJECT_TYPE_ANIM, 0));
			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
		}
			
		if (triggerResult[7] != 0) currentBg.hideShowItemById(triggerResult[7], scene);
		if (triggerResult[8] != 0) currentBg.hideShowAnimation(triggerResult[8]);

		// TODO: [5] = change inventory
		if (triggerResult[5] != 0) {
			int pos = world.isItemDisplayed(triggerResult[5]);

			if (pos == DB_INVENTORY_VALUE_IN) {
				inventory.redrawInventory(); // change position of boxes							
				touchedZoomItem = inventory.addItem(triggerResult[5], mEngine, scene); // should be done after redrawing
			} else if (pos == DB_INVENTORY_VALUE_OUT) {
				inventory.removeItem(triggerResult[5]);
				inventory.redrawInventory();
			}
		}
		
		if (triggerResult[3] != 0)		doll.setVisible(false);
		else if (triggerResult[2] != 0)	doll.setVisible(true); // to display doll when hidden you have to move it
		
		// be careful: you can't have a pending trigger on a simultaneous trigger
		// because you can't know which pending trigger has to be triggered first
		if (!simultaneous) pendingTriggerId = triggerResult[0];
		simultaneousTriggerId = triggerResult[9];
	}
	
	private void launchAnim(int animId) {
		
		//****************************************************************
		// features[0] = first_frame
		// features[1] = last_frame
		// features[2] = frame_duration
		// features[3] = loop
		// features[4] = x_velocity
		// features[5] = y_velocity
		// features[6] = doll_is_hidden
		//****************************************************************
		Map<String, Integer> animFeatures = world.getAnimFeatures(animId);
		
		Log.i("Clementime","Screen/launchanim(): launch animation " + animId);
		
		ListIterator<Anim> it = currentBg.anims.listIterator();
		
		while(it.hasNext()){
			Anim animation = it.next();

			if (animId == animation.id) {

				// to use animate function with first/last frame, you have to initialise a long[] sized by the number of frames 
				long[] frameDuration = new long[animFeatures.get("last_frame")-animFeatures.get("first_frame")+1];
				for (int i = 0; i <= animFeatures.get("last_frame") - animFeatures.get("first_frame"); i++) frameDuration[i] = animFeatures.get("frame_duration");

				if (animFeatures.get("loop") == NO_END_LOOP)	animation.animate(frameDuration, animFeatures.get("first_frame"), animFeatures.get("last_frame"), true);
				else animation.animate(frameDuration, animFeatures.get("first_frame"), animFeatures.get("last_frame"), animFeatures.get("loop"));
				
				// if static (no x velocity and no y velocity), check if x/y position has to change, stored in move_to fields
				if (animFeatures.get("x_velocity") == 0 &&  animFeatures.get("y_velocity") == 0) {
					if (animation.moveToX != 0 || animation.moveToY != 0) {
						animation.setPosition(animation.moveToX, animation.moveToY);					
					}
				// move animation on screen
				} else {
					phAnimRunning = new PhysicsHandler(animation);
					animation.registerUpdateHandler(phAnimRunning);
					phAnimRunning.setVelocity(animFeatures.get("x_velocity"), animFeatures.get("y_velocity"));
					if (animation.toChase) doll.isChased = false;			
				}

				if (animFeatures.get("doll_is_hidden") == 1) doll.setVisible(false);
				animation.setVisible(true);
								
				runningAnimId = animation.id;
			}
		}
	}
	
	private void hideBigItem() {
		
		Log.i("Clementime", "Screen/hideBigItem()");
		
    	touchedInventoryItem.big.setVisible(false);
       	touchedInventoryItem.big.setPosition(-200, 0); // out of screen
       	if (touchedInventoryItem != touchedZoomItem) {
       		touchedInventoryItem.setAlpha(1);
       		touchedInventoryItem.small.setAlpha(1);
       	}
    	touchedInventoryItem.big.setZIndex(ZINDEX_INV_ITEM);
		scene.sortChildren();
       	touchedInventoryItem = null;
	}
	
	/****************************/
	/* GENERAL SCREEN METHODS */
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
//			if (load == 0) world.save(doll.image.getX(), doll.image.getY());
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
