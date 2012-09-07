package eu.clementime.rds;

import static eu.clementime.rds.Constants.DEVELOPMENT;
import static eu.clementime.rds.Constants.DATABASE_FILE;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import eu.clementime.rds.R;

/**
* This class contains the database handler in order to have it during the whole game.
* @author Cl&eacute;ment
* @version 1.0
*/
public class RdsGame extends Application {

    public DatabaseHandler dbHandler;
	
    // OnCreate is called only when phone is switched off/on (or when application is downloaded on phone)
    @Override
    public void onCreate() {
    	
		Log.i("Clementime","RdsGame/onCreate()");
		
    	super.onCreate(); 
    	
     	String dbDir = getFilesDir().getAbsolutePath().replace("files", "databases");
     	
    	if (DEVELOPMENT) dbHandler = new DatabaseHandler(getApplicationContext(), dbDir, DATABASE_FILE, getResources().openRawResource(R.raw.rdsgame), getResources().openRawResource(R.raw.current_rdsgame));
    	else dbHandler = new DatabaseHandler(getApplicationContext(), dbDir, DATABASE_FILE, getResources().openRawResource(R.raw.rdsgame));
    }  

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);	
    }

    @Override
    public void onLowMemory() {
    	super.onLowMemory();
    }
}

