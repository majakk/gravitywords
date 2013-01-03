package studio.coldstream.gravitywords;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HighScoreActivity extends Activity{
	
	ListView list_main;
	HighScore mhl;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mhl = new HighScore();
		showHighScoreView();
		//showTestView();
		
    }
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Intent mainIntent3 = new Intent(HighScoreActivity.this, HighScoreActivity.class);
		HighScoreActivity.this.startActivity(mainIntent3);
		HighScoreActivity.this.finish();
	}
		
	private void showHighScoreView(){
		setContentView(R.layout.highscoretable);
		
		List<String> listan1 = new LinkedList<String>();
		List<String> listan2 = new LinkedList<String>();
		List<String> listan3 = new LinkedList<String>();
		List<String> listan4 = new LinkedList<String>();
		
		/* TODO: Load Data from preferences */
		for(int i = 0; i < 10; i++){			
			listan1.add(Integer.toString(i+1) + ".");
			if(mhl.getHighDate(this.getBaseContext(), i) != "-")
				listan2.add(mhl.getHighDate(this.getBaseContext(), i));
			else
				listan2.add("");
			
			if(mhl.getHighLevel(this.getBaseContext(), i) > 0)
				listan3.add("Level " + Integer.toString(mhl.getHighLevel(this.getBaseContext(), i)));
			else
				listan3.add("");
			
			if(mhl.getHighScore(this.getBaseContext(), i) > 0)
				listan4.add(Long.toString(mhl.getHighScore(this.getBaseContext(), i)));
			else
				listan4.add("");
		}
		
		/* Push Data to Adapter */
		list_main = (ListView)findViewById(R.id.ListView);
        list_main.setAdapter(new HofAdapter(this, listan1, listan2, listan3, listan4));
        list_main.invalidate();
		
	}
	
	public class HofViewHolder{
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
	}
	
	public class HofAdapter extends BaseAdapter{
        Context mContext;
        List<String> listan1;
        List<String> listan2;
        List<String> listan3;
        List<String> listan4;
        public HofAdapter(Context k, List<String> a, List<String> b, List<String> c, List<String> d){
            mContext = k;
            listan1 = new LinkedList<String>();
            listan1 = a;
            listan2 = new LinkedList<String>();
            listan2 = b;
            listan3 = new LinkedList<String>();
            listan3 = c;
            listan4 = new LinkedList<String>();
            listan4 = d;
        }
        @Override
        public int getCount() {
            return listan1.size();
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HofViewHolder v;
            if(convertView==null){
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.tablerow, null);
              
                v = new HofViewHolder();
                v.text1 = (TextView)convertView.findViewById(R.id.hof_name_text);
                v.text2 = (TextView)convertView.findViewById(R.id.hof_date_text);
                v.text3 = (TextView)convertView.findViewById(R.id.hof_resource_text);               
                v.text4 = (TextView)convertView.findViewById(R.id.hof_value_text);
               
                convertView.setTag(v);
            }
            else
            {
                v = (HofViewHolder) convertView.getTag();
            }
            v.text1.setText(listan1.get(position).toString());
            v.text2.setText(listan2.get(position).toString());
            v.text3.setText(listan3.get(position).toString());
            v.text4.setText(listan4.get(position).toString());
            if(position % 2 == 1){
            	v.text1.setBackgroundColor(Color.rgb(102, 18, 16));
            	v.text2.setBackgroundColor(Color.rgb(102, 18, 16));
            	v.text3.setBackgroundColor(Color.rgb(102, 18, 16));
            	v.text4.setBackgroundColor(Color.rgb(102, 18, 16));
            }
            else{
            	v.text1.setBackgroundColor(Color.rgb(80, 16, 14));
            	v.text2.setBackgroundColor(Color.rgb(80, 16, 14));
            	v.text3.setBackgroundColor(Color.rgb(80, 16, 14));
            	v.text4.setBackgroundColor(Color.rgb(80, 16, 14));
            }
            
            return convertView;
        }
              
		@Override
		public Object getItem(int position) {		
			return null;
		}
		@Override
		public long getItemId(int position) {	
			return 0;
		}
	};
	
	
	
	
	
}
