package studio.coldstream.gravitywords;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScore {
	protected static final int HIGH_COUNT = 10;
	
	
	String[] highDate;
	int[] highLevel;
	long[] highScore;
	
	SharedPreferences app_preferences;
	SharedPreferences prefsPrivate;
	SharedPreferences.Editor editor;
	SharedPreferences.Editor prefsEditor;
	
	public HighScore(){
		highDate = new String[HIGH_COUNT];		
		highLevel = new int[HIGH_COUNT];
		highScore = new long[HIGH_COUNT];
	}
	
	public void addHighScore(Context c, String newdate, int newlevel, long newscore){		
		loadHighScore(c);
		SharedPreferences prefsPrivate = c.getSharedPreferences("preferences", Context.MODE_PRIVATE);				       
        SharedPreferences.Editor prefsEditor = prefsPrivate.edit();
        StringBuilder sb1 = new StringBuilder(10);
        StringBuilder sb2 = new StringBuilder(10);  
        StringBuilder sb3 = new StringBuilder(10);
        for(int i = 0; i < HIGH_COUNT; i++){
        	sb1.append("hd" + i);
        	sb2.append("hl" + i);
        	sb3.append("hs" + i);
        	if(newscore > highScore[i]){
        		prefsEditor.putString(sb1.toString(), newdate);
        		prefsEditor.putInt(sb2.toString(), newlevel);
        		prefsEditor.putLong(sb3.toString(), newscore);
        		newdate = highDate[i];
        		newlevel = highLevel[i];
        		newscore = highScore[i];
        	}
        		
        }			               
        prefsEditor.commit(); // Very important*/
	}	
	
	public String getHighDate(Context c, int pos){
		loadHighScore(c);
		return highDate[pos];
	}
	
	public int getHighLevel(Context c, int pos){
		loadHighScore(c);
		return highLevel[pos];
	}
	
	public long getHighScore(Context c, int pos){
		loadHighScore(c);
		return highScore[pos];
	}
	
	private void loadHighScore(Context c){
		SharedPreferences prefsPrivate = c.getSharedPreferences("preferences", Context.MODE_PRIVATE);
		StringBuilder sb1 = new StringBuilder(10);
	    StringBuilder sb2 = new StringBuilder(10);  
	    StringBuilder sb3 = new StringBuilder(10);
		for(int i = 0; i < HIGH_COUNT; i++){
			sb1.append("hd" + i);
        	sb2.append("hl" + i);
        	sb3.append("hs" + i);
			highDate[i] = prefsPrivate.getString(sb1.toString(), "-");
			highLevel[i] = prefsPrivate.getInt(sb2.toString(), 0);
			highScore[i] = prefsPrivate.getLong(sb3.toString(), 0);
		}
		return;
	}	
	
}
