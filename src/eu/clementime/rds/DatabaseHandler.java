package eu.clementime.rds;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import static eu.clementime.rds.Constants.DEVELOPMENT;

public class DatabaseHandler extends SQLiteOpenHelper {
 
	private final String path;
	private final String fileName;
	private final String startingDbFileName;
	private final String currentDbFileName;
	
	public SQLiteDatabase db;
	
	private final Context myContext;
	public boolean newGame = false;
	
	private String className = "DatabaseHandler";
	
	public DatabaseHandler(Context context, String dbPath, String dbFile, InputStream data) {
		super(context, dbFile, null, 1);
		
		Log.i("Clementime", className + "/Constructor()");
		
	    this.myContext = context;
	    
	    this.path = dbPath + "/";
	    this.fileName = dbFile;
	    this.startingDbFileName = "starting_" + dbFile;
	    this.currentDbFileName = "current_" + dbFile;
	    
	    storeStartingDatabase(data);
	}
	
	// only in development	
	public DatabaseHandler(Context context, String dbPath, String dbFile, InputStream data, InputStream currentData) {
		super(context, dbFile, null, 1);
		
		Log.i("Clementime", className + "/Constructor()");

	    this.myContext = context;
	    
	    this.path = dbPath + "/";
	    this.fileName = dbFile;
	    this.startingDbFileName = "starting_" + dbFile;
	    this.currentDbFileName = "current_" + dbFile;
	    
	    storeStartingDatabase(data);
	    storeCurrentDatabase(currentData);    
	}
	
	public void checkNewGame() {
		
		Log.i("Clementime", className + "/checkNewGame()");
	    
		File file = new File(this.path + this.fileName);
	    
		if (!file.exists()) {
			Log.v("Clementime", className + "/constructor: file " + this.path + this.fileName + " doesn't exist");
			newGame = true;
		} else {
			Log.v("Clementime", className + "/constructor: file " + this.path + this.fileName + " exist");
			newGame = false;
		}		
	}
	 
	@Override
	public void onCreate(SQLiteDatabase db) {
	}
	 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	 
	public boolean checkDatabase() {
		
		Log.i("Clementime", className + "/checkDatabase()");

		boolean checkedDatabaseFile = false;
		
	    File file = new File(this.path + this.fileName);
	    	    
//		if (!file.exists()) {		
		if (newGame) {		
			try {    	
		  		this.getWritableDatabase();
				Log.v("Clementime", className + "/checkDatabase(): copy database " + this.path + this.fileName + " from " + this.startingDbFileName);
				this.copyDatabase(this.startingDbFileName);
			} catch (Exception e) {
	  			Log.w("Clementime", className + "/checkDatabase(): error while copying database" + this.path + this.fileName + e.getMessage()); 			
	      	}	
		}
		
		if (DEVELOPMENT) {
	  		this.getWritableDatabase();
			Log.v("Clementime", className + "/checkDatabase(): copy database " + this.path + this.fileName + " from " + this.startingDbFileName);
			this.copyDatabase(this.startingDbFileName);	
		}
		
		if (file.length() < 5000) {
			Log.w("Clementime", className + "/checkDatabase(): Database " + file.getAbsolutePath() + " corrupted. Cannot open Sam");
			checkedDatabaseFile = false;
		} else checkedDatabaseFile = true; 
			
	  			//TODO: remove these 2 lines:
	  	try	{copyDatabaseOnSDCard(this.fileName);}
	  	catch (Exception e) {Log.w("Clementime", className + "/checkDatabase(): copy database ");}
	  			//checkedDatabaseFile = this.copyFromAssets();
	    
		if (!checkedDatabaseFile) {
			file.delete();
			Log.w("Clementime", className + "/Constructor: Database deleted from " + this.path + this.fileName);
		}
		
	    return checkedDatabaseFile;
	}
	
	public DatabaseHandler open() throws SQLException {
		
		Log.i("Clementime", className + "/open()");
		
		try {
			this.db = SQLiteDatabase.openDatabase(this.path + this.fileName, null, SQLiteDatabase.OPEN_READWRITE);
  		} catch (Exception SQLException) {
  			Log.w("Clementime", className + "/open() : failed to open database" + this.path + this.fileName);
      	}
		
		return this;
	}

	private void storeStartingDatabase(InputStream data) {
		
		Log.i("Clementime", className + "/storeStartingDatabase()");
		
		try {
			Log.v("Clementime", className + "/storeStartingDatabase(): store starting database with no player change in " + this.path + this.startingDbFileName);
			OutputStream output = new FileOutputStream(this.path + this.startingDbFileName);
		
			byte[] buffer = new byte[1024];
			int length;
	
			while ((length = data.read(buffer))>0)	output.write(buffer, 0, length);
			
			output.flush();
			output.close();
			data.close();

        } catch (Exception e) {
  			Log.w("Clementime", className + "/storeStartingDatabase(): abort copying. Exception " + e.getMessage());
        }	
	}
	
	private void storeCurrentDatabase(InputStream currentData) {
		
		Log.i("Clementime", className + "/storeCurrentDatabase()");
		
		try {
			Log.v("Clementime", className + "/storeCurrentDatabase(): store current database in " + this.path + this.currentDbFileName);
			OutputStream output = new FileOutputStream(this.path + this.currentDbFileName);
		
			byte[] buffer = new byte[1024];
			int length;
	
			while ((length = currentData.read(buffer))>0)	output.write(buffer, 0, length);
			
			output.flush();
			output.close();
			currentData.close();

        } catch (Exception e) {
  			Log.w("Clementime", className + "/storeCurrentDatabase(): abort copying. Exception " + e.getMessage());
        }	
	}
	
	private void copyDatabase(String databaseFileName) {
		
		Log.i("Clementime", className + "/copyDatabase()");
		
		try {
			Log.v("Clementime", className + "/copyStartingDatabase(): load database from starting database");
			
		  	InputStream input = new FileInputStream(new File(this.path + databaseFileName));
			OutputStream output = new FileOutputStream(this.path + this.fileName);
		
			byte[] buffer = new byte[1024];
			int length;
	
			while ((length = input.read(buffer))>0)	output.write(buffer, 0, length);
			
			output.flush();
			output.close();
			input.close();

        } catch (Exception e) {
  			Log.w("Clementime", className + "/copyFromAssets(): abort copying. Exception " + e.getMessage());
        }
		
	}
	
	public void deleteDatabase() {
		
		Log.i("Clementime", className + "/deleteDatabase()");
		
		//this.db.close();
		
	  	try{
		    File file = new File(this.path + this.fileName);
		    
			if (file.delete()) Log.i("Clementime", className + "/delete(): Delete database");   
			else Log.w("Clementime", className + "/delete(): Failed to delete database");	
			
	  	} catch(Exception e) {
	  		Log.w("Clementime", className + "/delete(): Failed to delete database: " + e);
	  	}

	}
		
	public void save(int SavingId) {
		
		Log.i("Clementime", className + "/save()");
		
  		try {
  			copyDatabaseOnSDCard(this.fileName);
			Log.i("Clementime", className + "/save(): save database in S" + SavingId);	

  		} catch (IOException e) {
  			throw new Error("Error while saving database");
      	}
			
	}
	
	private void copyDatabaseOnSDCard(String dbName) throws IOException {
		
		Log.i("Clementime", className + "/copyDatabaseOnSDCard()");
		 	
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	
	  	if (sdCard.canWrite()) {
		  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
		  	if (!dir.exists()) dir.mkdirs();
		  	File file = new File(dir, dbName);
		  	
			File inFileName = new File("/data/data/" + myContext.getPackageName() + "/databases/" + dbName);		
		  	InputStream myInput = new FileInputStream(inFileName);
		  	OutputStream myOutput = new FileOutputStream(file);	  		

			byte[] buffer = new byte[1024];
			int length;
			
		  	while ((length = myInput.read(buffer))>0) myOutput.write(buffer, 0, length);
			
			myOutput.flush();
		 	myOutput.close();
		  	myInput.close();
	  	} else { 	
		  	if (Environment.MEDIA_MOUNTED.equals(sdCard)) {
		  		Log.i("Clementime", className + "/copyDatabase(): sdcard mounted and writable");
		  	}
		  	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCard)) {
		  		Log.i("Clementime", className + "/copyDatabase(): sdcard mounted readonly");
		  	}
		  	else {
		  		Log.i("Clementime", className + "/copyDatabase(): sdcard: " + sdCard);
		  	}
		}
	}

	@Override
	public synchronized void close() {
		
		Log.i("Clementime", className + "/close()");
 
		try {
    	    if(db.isOpen()) db.close();
    	    else Log.w("Clementime", className + "/close() : Database " + this.path + this.fileName + " was already closed");
    	    super.close();
    	} catch (Exception e) {
  			Log.w("Clementime", className + "/close() : failed to close database: " + e.getMessage());
      	}

 	}
}