
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

import java.util.HashMap;

public class MainAlarmActivity extends Activity {

    public static final String TAG = MainAlarmActivity.class.getName();
    private HashMap<String, Integer> alarmMap = new HashMap<String, Integer>();
    int mRepeat = 0;
    // references to our images
    private String[] mThumbIds = {
            "0:00", "0:10", "0:20", "0:30", "0:40", "0:50",
            "0:05", "0:15", "0:25", "0:35", "0:45", "0:55",
            "1:00", "1:10", "1:20", "1:30", "1:40", "1:50",
            "2:00", "2:10", "2:20", "2:30", "2:40", "2:50",
            "3:00", "3:10", "3:20", "3:30", "3:40", "3:50",
            "4:00", "4:10", "4:20", "4:30", "4:40", "4:50",
            "5:00", "5:10", "5:20", "5:30", "5:40", "5:50",
            "6:00", "6:10", "6:20", "6:30", "6:40", "6:50",
            "7:00", "7:10", "7:20", "7:30", "7:40", "7:50",
            "8:00", "8:10", "8:20", "8:30", "8:40", "8:50",
            "9:00", "9:10", "9:20", "9:30", "9:40", "9:50",
            "10:00", "10:10", "10:20", "10:30", "10:40", "10:50",
            "11:00", "11:10", "11:20", "11:30", "11:40", "11:50",
            "12:00", "12:10", "12:20", "12:30", "12:40", "12:50",
            "13:00", "13:10", "13:20", "13:30", "13:40", "13:50",
            "14:00", "14:10", "14:20", "14:30", "14:40", "14:50",
            "15:00", "15:10", "15:20", "15:30", "15:40", "15:50",
            "16:00", "16:10", "16:20", "16:30", "16:40", "16:50",
            "17:00", "17:10", "17:20", "17:30", "17:40", "17:50",
            "18:00", "18:10", "18:20", "18:30", "18:40", "18:50",
            "19:00", "19:10", "19:20", "19:30", "19:40", "19:50",
            "20:00", "20:10", "20:20", "20:30", "20:40", "20:50",
            "21:00", "21:10", "21:20", "21:30", "21:40", "21:50",
            "22:00", "22:10", "22:20", "22:30", "22:40", "22:50",
            "23:00", "23:10", "23:20", "23:30", "23:40", "23:50",
    };
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        mSp = getSharedPreferences(Constants.PREFS, MODE_WORLD_WRITEABLE);
        String alarmPref = mSp.getString(Constants.P_ALARM, null);

        for (String string : mThumbIds) {
            alarmMap.put(string, -1);
        }
        if (alarmPref != null) {
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
                viewholder.minute00Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute10Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute20Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute30Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute40Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute50Text.setTag(R.id.TAG_HOUR, position + "");
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
                viewholder.text.setText(position + "");
                viewholder.minute00Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute10Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute20Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute30Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute40Text.setTag(R.id.TAG_HOUR, position + "");
                viewholder.minute50Text.setTag(R.id.TAG_HOUR, position + "");
            }

            setClicked(viewholder.minute00Text, -1);
            setClicked(viewholder.minute10Text, -1);
            setClicked(viewholder.minute20Text, -1);
            setClicked(viewholder.minute30Text, -1);
            setClicked(viewholder.minute40Text, -1);
            setClicked(viewholder.minute50Text, -1);

            checkedLayouts(viewholder, position);

            viewholder.text.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
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
            return convertView;
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
