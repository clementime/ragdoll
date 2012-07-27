package eu.clementime.rds;

import static eu.clementime.rds.Constants.DB_FIELD_DISPLAY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class Backup {

	DatabaseHandler dbh;
	Context context;
	
  	String[] outFileName = {"comb", "item_display", "item_take", "item_look", "item_takeable", "area_look", "char_talk", "char_look", "anim_display", "leave_screen", "start_screen"};
  	String[] tableName = {"combination", "item", "screen_item", "screen_item", "screen_item", "screen_area", "character", "character", "playing_animation", "exit", "exit"}; 
  	String[] fieldName = {"state", DB_FIELD_DISPLAY, "take_state", "look_state", "takeable", "look_state", "talk_state", "look_state", "display", "before_trigger_id", "after_trigger_id"};
  	String[] idFieldName = {"_id", "_id", "item_id", "item_id", "item_id", "_id", "_id", "_id", "_id", "_id", "_id"};
	
	public Backup(DatabaseHandler dbh, Context context) {
		this.dbh = dbh;
		this.context = context;
	}
	
	// create file states.sql on sdCard to manually update database if needed
	public void createStateFile(String extension) {
		
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	String outFileName = "states.sql"; 	

		String query;
		int id;
		int state;
	  	
		try {	  	
		  	if (sdCard.canWrite()) {
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();
			  	
			    BufferedWriter out = new BufferedWriter(new FileWriter(dir + "/" + outFileName + extension));

			    //********************************
			    // COMBINATION - state
			    //********************************
				query = " select _id, state from combination ";
				
				Cursor c = dbh.db.rawQuery(query, new String [] {});
				
			    out.write("-- combinations/state");
			    
			    while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("state"));
				    out.write("UPDATE combination set state = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
			    
				c.close();

			    //********************************
			    // ITEM - display
			    //********************************
				query = " select _id, " + DB_FIELD_DISPLAY + " from item ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- items/display");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex(DB_FIELD_DISPLAY));
				    out.write("UPDATE item set " + DB_FIELD_DISPLAY + " = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}		
			    
				c.close();
				
			    //********************************
			    // SCREEN ITEM - take_state
			    //********************************
				query = " select item_id, take_state from screen_item ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- screen items/take_state");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("item_id"));
					state = c.getInt(c.getColumnIndex("take_state"));
				    out.write("UPDATE screen_item set take_state = " + state + " where item_id = " + id + ";" + System.getProperty("line.separator"));
				}		
			    
				c.close();
				
			    //********************************
			    // SCREEN ITEM - look_state
			    //********************************
				query = " select item_id, look_state from screen_item ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- screen items/look_state");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("item_id"));
					state = c.getInt(c.getColumnIndex("look_state"));
				    out.write("UPDATE screen_item set look_state = " + state + " where item_id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // SCREEN ITEM - takeable
			    //********************************
				query = " select item_id, takeable from screen_item ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- screen items/takeable");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("item_id"));
					state = c.getInt(c.getColumnIndex("takeable"));
				    out.write("UPDATE screen_item set takeable = " + state + " where item_id = " + id + ";" + System.getProperty("line.separator"));
				}
			    
				c.close();
				
			    //********************************
			    // SCREEN AREA - look_state
			    //********************************
				query = " select _id, look_state from screen_area ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- screen areas/look_state");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("look_state"));
				    out.write("UPDATE screen_area set look_state = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // CHARACTER - look_state
			    //********************************
				query = " select _id, look_state from character ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- character/look_state");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("look_state"));
				    out.write("UPDATE character set look_state = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // CHARACTER - talk_state
			    //********************************
				query = " select _id, talk_state from character ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- character/talk_state");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("talk_state"));
				    out.write("UPDATE character set talk_state = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // ANIMATION - display
			    //********************************
				query = " select _id, display from playing_animation ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- playing_animation/display");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("display"));
				    out.write("UPDATE playing_animation set display = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // EXIT - before_trigger_id
			    //********************************
				query = " select _id, before_trigger_id from exit ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- exit/before_trigger_id");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("before_trigger_id"));
				    out.write("UPDATE exit set before_trigger_id = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				
			    //********************************
			    // EXIT - after_trigger_id
			    //********************************
				query = " select _id, after_trigger_id from exit ";
				
				c = dbh.db.rawQuery(query, new String [] {});

			    out.write("-- exit/after_trigger_id");
			    
				while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					state = c.getInt(c.getColumnIndex("after_trigger_id"));
				    out.write("UPDATE exit set after_trigger_id = " + state + " where _id = " + id + ";" + System.getProperty("line.separator"));
				}
				
				c.close();
				out.close();
			    
			} else { 	
			  	if (Environment.MEDIA_MOUNTED.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStatesFile(): sdcard mounted and writable");
			  	}
			  	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStatesFile(): sdcard mounted readonly");
			  	}
			  	else {
			  		Log.i("Clementime", "DatabaseHandler/createStatesFile(): sdcard: " + sdCard);
			  	}
			}

		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/createStatesFile(): cannot write states");
		}
	}
	
	// create files to load when saving with a copy on sdCard
	public void createStateFiles(String extension) {
		
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();

	  	BufferedWriter out; // copy on sdCard
	  	BufferedWriter out2; // to load 

		String query;
		int id;
		int state;
	  	
		try {	  	
		  	if (sdCard.canWrite()) {
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();
			  	
				for (int i = 0; i < outFileName.length; i++) {
				    Log.i("Clementime", "DevTools/createStateFiles(): " + outFileName[i] + extension);

					out = new BufferedWriter(new FileWriter(dir + "/" + outFileName[i] + extension + ".sql"));
				    out2 = new BufferedWriter(new FileWriter("/data/data/" +context.getPackageName() + "/databases/" + outFileName[i] + extension + ".sql"));
				    
					query = " select " + idFieldName[i] + ", " + fieldName[i] + " from " + tableName[i];
					
					Cursor c = dbh.db.rawQuery(query, new String [] {});
					
				    out.write("-- " + tableName[i] + "/" + fieldName[i] + System.getProperty("line.separator"));
				    
				    while (c.moveToNext()) {
						// retrieve combination states
						id = c.getInt(c.getColumnIndex(idFieldName[i]));
						state = c.getInt(c.getColumnIndex(fieldName[i]));
					    out.write(id + ";" + state + System.getProperty("line.separator"));
					    out2.write(id + ";" + state + System.getProperty("line.separator"));

					    Log.v("Clementime", "DevTools/createStateFiles(): " + tableName[i] + "-" + fieldName[i] + "-" + idFieldName[i] + ": " + id + "/ " + fieldName[i] + ": " + state);

					}
			
					c.close();
					out.close();
					out2.close();			  		
			  	}
			    
			} else { 	
			  	if (Environment.MEDIA_MOUNTED.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard mounted and writable");
			  	}
			  	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard mounted readonly");
			  	}
			  	else {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard: " + sdCard);
			  	}
			}

		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/createStateFiles(): cannot write states");
		}
	}

	public void savePlayerData(String extension) {
		
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	String outFileName = "player_data"; 	
	  	BufferedWriter out;
	  	BufferedWriter out2;

		String query;
		int id;
		String ref = "";
		String value = "";
	  	
		try {	  	
		  	if (sdCard.canWrite()) {
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();
			  	
			    Log.i("Clementime", "DevTools/savePlayerData(): write player data");

				out = new BufferedWriter(new FileWriter(dir + "/" + outFileName + extension + ".sql"));
			    out2 = new BufferedWriter(new FileWriter("/data/data/" +context.getPackageName() + "/databases/" + outFileName + extension + ".sql"));
			    
				query = " select _id, reference, value from player_saving ";
				
				Cursor c = dbh.db.rawQuery(query, new String [] {});
				
			    out.write("-- player data" + System.getProperty("line.separator"));
			    
			    while (c.moveToNext()) {
					// retrieve combination states
					id = c.getInt(c.getColumnIndex("_id"));
					ref = c.getString(c.getColumnIndex("reference"));
					value = c.getString(c.getColumnIndex("value"));
				    out.write(id + ";" + ref + ";" + value + System.getProperty("line.separator"));
				    out2.write(id + ";" + ref + ";" + value + System.getProperty("line.separator"));

				    Log.v("Clementime", "DevTools/savePlayerData(): id: " + id + "/ref: " + ref + "/value: " + value);
				}
		
				c.close();
				out.close();
				out2.close();				    
			} else { 	
			  	if (Environment.MEDIA_MOUNTED.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/savePlayerData(): sdcard mounted and writable");
			  	}
			  	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/savePlayerData(): sdcard mounted readonly");
			  	}
			  	else {
			  		Log.i("Clementime", "DatabaseHandler/savePlayerData(): sdcard: " + sdCard);
			  	}
			}

		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/savePlayerData(): cannot write data");
		}
	}
	
	public void loadPlayerData(String extension) {
	  	
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	String outFileName = "player_data"; 
	  	
		try {	  				
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();
									
		  		Log.i("Clementime", "DevTools/loadPlayerData(): load data ");
		  		
				FileInputStream fstream = new FileInputStream("/data/data/" + context.getPackageName() + "/databases/" + outFileName + extension + ".sql");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				String strLine;
				ContentValues args;
				String where = "";
				String[] line;	
				
				while ((strLine = br.readLine()) != null)   {
										
					line = strLine.split(";");
				
					where = " _id = " + line[0] + " and reference = '" + line[1] + "'";	
					
				    args = new ContentValues();
					args.put("value", line[2]);
		
				    dbh.db.update("player_saving", args, where, null);
			  		Log.v("Clementime", "DevTools/loadPlayerData(): update player_saving set " + line[1] + " = " + line[2] + " where _id = " + line[0]);
				}
		
				in.close();	
			    
		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/loadPlayerData(): cannot load data");
		}
	}
	
	public void loadStateFiles(String extension) {
	  	
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	
		try {	  				
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();

				for (int i = 0; i < outFileName.length; i++) {
										
			  		Log.i("Clementime", "DevTools/loadStateFiles(): load " + tableName[i]);
			  		
					FileInputStream fstream = new FileInputStream("/data/data/" + context.getPackageName() + "/databases/" + outFileName[i] + extension + ".sql");
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					
					String strLine;
					ContentValues args;
					String where = "";
					String[] line;	
					
					while ((strLine = br.readLine()) != null)   {
											
						line = strLine.split(";");
					
						where = idFieldName[i] + " = " + line[0];	
						
					    args = new ContentValues();
						args.put(fieldName[i], line[1]);
			
					    dbh.db.update(tableName[i], args, where, null);
				  		Log.v("Clementime", "DevTools/loadStateFiles(): update " + tableName[i] + " set " + fieldName[i] + " = " + line[1] + " where " + idFieldName[i] + " = " + line[0]);

					}
			
					in.close();					
				}

			    
		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/loadStateFiles(): cannot load states");
		}
	}

	public void setLoad(int load) {
		
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();

	  	BufferedWriter out; // on sdCard
	  	
		try {	  	
		  	if (sdCard.canWrite()) {
			  	File dir = new File (sdCard.getAbsolutePath() + "/sam");
			  	if (!dir.exists()) dir.mkdirs();
				out = new BufferedWriter(new FileWriter(dir + "/load.sql"));				
			    out.write("" + load);
				out.close();
			    
			} else { 	
			  	if (Environment.MEDIA_MOUNTED.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard mounted and writable");
			  	}
			  	else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(sdCard)) {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard mounted readonly");
			  	}
			  	else {
			  		Log.i("Clementime", "DatabaseHandler/createStateFiles(): sdcard: " + sdCard);
			  	}
			}

		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/setLoad(): cannot write load");
		}
	}
	
	public int getLoad() {
	  	
		int load = 0;
		
	  	// write on sdCard
	  	File sdCard = Environment.getExternalStorageDirectory();
	  	
		try {	  				

								
	  		Log.i("Clementime", "DevTools/getLoad()");
	  		
		  	//File fileName = new File (sdCard.getAbsolutePath() + "/sam" + "/load.sql");

			FileInputStream fstream = new FileInputStream(sdCard.getAbsolutePath() + "/sam" + "/load.sql");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
		  		Log.d("Clementime", "DevTools/getLoad(): load " + strLine);
		  		load = Integer.parseInt(strLine);
			}
	
			in.close();					

			    
		} catch (IOException e) {
	  		Log.i("Clementime", "DevTools/loadStateFiles(): cannot find load ");
		}
		
		setLoad(0);
		
		return load;
	}
}
