
package com.amachan.myalarm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amachan.alarm.MyAlarmManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAlarmActivity extends Activity {

    public static final String TAG = MainAlarmActivity.class.getName();
    private HashMap<String, Integer> alarmMap = new HashMap<String, Integer>();
    int mRepeat = 0;
    // references to our images
    private String[] mThumbIds;
    private SharedPreferences mSp;

    public void generateTime(){
        int startTime = 0;
        int currentTime = 0;
        ArrayList<String> time = new ArrayList<String>();
        while(currentTime < (24 * 60)) {
            int hours = currentTime / 60;
            int minutes = currentTime % 60;
            time.add(hours + ":" + String.format("%02d", minutes));
            currentTime += 10;
        }
        mThumbIds = time.toArray(new String[time.size()]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        mSp = getSharedPreferences(Constants.PREFS, MODE_WORLD_WRITEABLE);
        String alarmPref = mSp.getString(Constants.P_ALARM, null);
        generateTime();
        for (String string : mThumbIds) {
            alarmMap.put(string, -1);
        }
        if (alarmPref != null && !alarmPref.isEmpty()) {
            String[] values = alarmPref.split(",");
            for (String string : values) {
                String[] alarms = string.split("-");
                alarmMap.put(alarms[0], Integer.parseInt(alarms[1]));
            }
        }

        // Button
        TextView allText = (TextView) findViewById(R.id.all_text);
        TextView weekdayText = (TextView) findViewById(R.id.weekday_text);
        TextView weekendText = (TextView) findViewById(R.id.weekend_text);

        final LinearLayout allSelectLayout = (LinearLayout) findViewById(R.id.all_select_layout);
        final LinearLayout weekdaySelectLayout = (LinearLayout) findViewById(R.id.weekday_select_layout);
        final LinearLayout weekendSelectLayout = (LinearLayout) findViewById(R.id.weekend_select_layout);

        // ListView
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new AlarmListAdapter(this));
        // GridView
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new AlarmAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainAlarmActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        allText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelectLayout.setBackgroundColor(getResources().getColor(R.color.holo_blue));
                weekdaySelectLayout.setBackgroundColor(Color.WHITE);
                weekendSelectLayout.setBackgroundColor(Color.WHITE);
                mRepeat = 0;
            }
        });
        weekdayText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelectLayout.setBackgroundColor(Color.WHITE);
                weekdaySelectLayout.setBackgroundColor(getResources().getColor(R.color.holo_blue));
                weekendSelectLayout.setBackgroundColor(Color.WHITE);
                mRepeat = 1;
            }
        });
        weekendText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelectLayout.setBackgroundColor(Color.WHITE);
                weekdaySelectLayout.setBackgroundColor(Color.WHITE);
                weekendSelectLayout.setBackgroundColor(getResources().getColor(R.color.holo_blue));
                mRepeat = 2;
            }
        });

    }

    @Override
    public void onStop() {
        String alarmPref = "";
        for (String string : mThumbIds) {
            int repeat = alarmMap.get(string);
            if (repeat != -1) {
                if (alarmPref != null && !alarmPref.isEmpty()) {
                    alarmPref = alarmPref + "," + string + "-" + repeat;
                } else {
                    alarmPref = string + "-" + repeat;
                }

            }
        }
        mSp.edit().putString(Constants.P_ALARM, alarmPref).commit();
        super.onStop();
    }

    protected void cancelAlarm(String alarmTime) {
        MyAlarmManager.cancelAlarm(getApplicationContext(), alarmTime);
    }

    protected void setAlarm(String alarmTime) {
        MyAlarmManager.setAlarm(getApplicationContext(), alarmTime, mRepeat);
    }

    public class AlarmAdapter extends BaseAdapter {
        private Context mContext;

        class ViewHolder {
            public TextView text;
        }

        public AlarmAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewholder;
            if (convertView == null) { // if it's not recycled, initialize some
                                       // attributes
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.grid_item, parent, false);
                viewholder = new ViewHolder();
                viewholder.text = (TextView) convertView.findViewById(R.id.alarm_time);
                viewholder.text.setText(mThumbIds[position]);
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
                viewholder.text.setText(mThumbIds[position]);
            }
            if (alarmMap.get(mThumbIds[position]) != -1) {
                viewholder.text.setTextColor(Color.WHITE);
                viewholder.text.setBackgroundColor(Color.RED);
                viewholder.text.setTag(true);
            } else {
                viewholder.text.setTextColor(Color.BLACK);
                viewholder.text.setBackgroundColor(Color.WHITE);
                viewholder.text.setTag(false);
            }
            viewholder.text.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    TextView text = (TextView) v;
                    String alarmTime = text.getText().toString();
                    Boolean set = (Boolean) v.getTag();
                    if (!set) {
                        text.setTextColor(Color.WHITE);
                        text.setBackgroundColor(Color.RED);
                        text.setTag(mRepeat);
                        alarmMap.put(alarmTime, mRepeat);
                        setAlarm(alarmTime);
                    } else {
                        text.setTextColor(Color.BLACK);
                        text.setBackgroundColor(Color.WHITE);
                        text.setTag(-1);
                        alarmMap.put(alarmTime, -1);
                        cancelAlarm(alarmTime);
                    }

                }
            });
            return convertView;
        }

    }

    public class AlarmListAdapter extends BaseAdapter {
        private Context mContext;

        class ViewHolder {
            public TextView text;
            public TextView minute00Text;
            public TextView minute10Text;
            public TextView minute20Text;
            public TextView minute30Text;
            public TextView minute40Text;
            public TextView minute50Text;
        }

        public AlarmListAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return 24;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView " + position);
            ViewHolder viewholder;
            if (convertView == null) { // if it's not recycled, initialize some
                                       // attributes
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                viewholder = new ViewHolder();
                viewholder.text = (TextView) convertView.findViewById(R.id.hour);
                viewholder.minute00Text = (TextView) convertView.findViewById(R.id.minutes0);
                viewholder.minute10Text = (TextView) convertView.findViewById(R.id.minutes10);
                viewholder.minute20Text = (TextView) convertView.findViewById(R.id.minutes20);
                viewholder.minute30Text = (TextView) convertView.findViewById(R.id.minutes30);
                viewholder.minute40Text = (TextView) convertView.findViewById(R.id.minutes40);
                viewholder.minute50Text = (TextView) convertView.findViewById(R.id.minutes50);
                viewholder.text.setText(position + "");
                viewholder.minute00Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });
                viewholder.minute10Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });
                viewholder.minute20Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });
                viewholder.minute30Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });
                viewholder.minute40Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });
                viewholder.minute50Text.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked(v);
                    }
                });

                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
                viewholder.text.setText(position + "");
            }

            textFunc(viewholder.minute00Text, position);
            textFunc(viewholder.minute10Text, position);
            textFunc(viewholder.minute20Text, position);
            textFunc(viewholder.minute30Text, position);
            textFunc(viewholder.minute40Text, position);
            textFunc(viewholder.minute50Text, position);


            checkedLayouts(viewholder, position);

            return convertView;
        }

        private void textFunc(TextView tv, int position){
            tv.setTag(R.id.TAG_HOUR, position + "");
            setClicked(tv, -1);
        }

        private void setClicked(TextView text, int repeat) {
            if (repeat == -1) {
                text.setTextColor(Color.BLACK);
                text.setBackgroundColor(Color.WHITE);
                text.setTag(R.id.TAG_SET, -1);
            } else {
                Log.d(TAG, "setClicked " + text.getText() + " - repeat " + repeat);
                text.setTextColor(Color.WHITE);

                text.setBackgroundColor(getRepeatColor(repeat));
                text.setTag(R.id.TAG_SET, repeat);
            }

        }

        private int getRepeatColor(int repeat) {
            if (repeat == 0) {
                return getResources().getColor(R.color.violet);
            } else if (repeat == 1) {
                return getResources().getColor(R.color.red);
            } else {
                return getResources().getColor(R.color.pink);
            }
        }

        private void checkedLayouts(ViewHolder viewholder, int position) {
            if (alarmMap.get(position + ":00") != -1) {
                setClicked(viewholder.minute00Text, alarmMap.get(position + ":00"));
            }
            if (alarmMap.get(position + ":10") != -1) {
                setClicked(viewholder.minute10Text, alarmMap.get(position + ":10"));
            }
            if (alarmMap.get(position + ":20") != -1) {
                setClicked(viewholder.minute20Text, alarmMap.get(position + ":20"));
            }
            if (alarmMap.get(position + ":30") != -1) {
                setClicked(viewholder.minute30Text, alarmMap.get(position + ":30"));
            }
            if (alarmMap.get(position + ":40") != -1) {
                setClicked(viewholder.minute40Text, alarmMap.get(position + ":40"));
            }
            if (alarmMap.get(position + ":50") != -1) {
                setClicked(viewholder.minute50Text, alarmMap.get(position + ":50"));
            }

        }

        protected void clicked(View v) {
            TextView text = (TextView) v;
            String alarmTime = text.getText().toString();
            String hours = (String) v.getTag(R.id.TAG_HOUR);
            Integer set = (Integer) v.getTag(R.id.TAG_SET);
            String time = hours + ":" + alarmTime;
            Log.d(TAG, "clicked - time " + time);
            if (set == -1) {
                setClicked(text, mRepeat);
                alarmMap.put(time, mRepeat);
                setAlarm(time);
            } else {
                setClicked(text, -1);
                alarmMap.put(time, -1);
                cancelAlarm(time);
            }

        }
    }

}
