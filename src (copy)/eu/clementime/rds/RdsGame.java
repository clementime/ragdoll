package eu.clementime.rds;

import static eu.clementime.rds.Constants.DEVELOPMENT;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import eu.clementime.rds.R;

public class RdsGame extends Application {

    public DatabaseHandler dbHandler;
    //public int load;
	
    // OnCreate is called only when phone is switched off/on (or when application is downloaded on phone)
    @Override
    public void onCreate() {
    	
		Log.i("Clementime","RdsGame/onCreate()");
		
    	super.onCreate(); 
    	
     	String dbDir = getFilesDir().getAbsolutePath().replace("files", "databases");
     	
    	if (DEVELOPMENT) dbHandler = new DatabaseHandler(getApplicationContext(), dbDir, "samdata.sqlite", getResources().openRawResource(R.raw.samdata), getResources().openRawResource(R.raw.current_samdata));
    	else dbHandler = new DatabaseHandler(getApplicationContext(), dbDir, "samdata.sqlite", getResources().openRawResource(R.raw.samdata));
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

