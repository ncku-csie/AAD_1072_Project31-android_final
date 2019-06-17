package com.example.afinal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences restaurant_list;
    public String[] str;
    public String[] str_view;
    int edit_position;
    TextView random_restaurant;
    String mText;
    Handler mHandler;
    long time =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("What to eat today...");

        restaurant_list = getSharedPreferences("restaurant",0);

        int i;
        int count = restaurant_list.getInt("restaurant_counter", -1);

        if(count<0){
            restaurant_list.edit()
                    .putInt("restaurant_counter", 0)
                    .commit();
        }
        else{

            str = new String[count];
            str_view = new String[count];
            for(i=0;i<count;i++) {
                str[i] = restaurant_list.getString("restaurant_data_"+Integer.toString(i), "NULL");
                str_view[i] = restaurant_list.getString("restaurant_data_view_"+Integer.toString(i), "NULL");
            }
        }

        random_restaurant = findViewById(R.id.random_restaurant);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    random_restaurant.setText(mText);
                }
            }
        };

        Button to_add_new = findViewById(R.id.to_add_new);



    }

    public void updateTextView(String text) {
        mText = text;
        mHandler.sendEmptyMessage(0);
    }



    public void jumpToNewRestaurant(View view){
        setContentView(R.layout.add_restaurant);
        this.setTitle("Add a new restaurant...");
    }

    public void jumpToRestaurantList(View view){

        /*boolean isFirstXml=evaluatingConditionFunction();
        LayoutInflater inflator=getLayoutInflater();
        View view=inflator.inflate(isFirstXml?R.layout.myfirstxml:R.layout.myseconxml, null, false);*/
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        //setContentView(view);

        setContentView(R.layout.restaurant_list);

        this.setTitle("My restaurant list...");

        restaurant_list = getSharedPreferences("restaurant",0);

        int count = restaurant_list.getInt("restaurant_counter", -1);


        ListView listview = findViewById(R.id.listview);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                str_view);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(onClickListView);       //指定事件 Method

    }

    public AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Toast 快顯功能 第三個參數 Toast.LENGTH_SHORT 2秒  LENGTH_LONG 5秒
            Toast.makeText(MainActivity.this,"You can edit your restaurant record here.", Toast.LENGTH_SHORT).show();
            setTitle("Edit page...");
            setContentView(R.layout.edit_restaurant);


            Button to_main_page= findViewById(R.id.to_main_page);
            Button confirm_new_restaurant= findViewById(R.id.confirm_new_restaurant);

            Switch hk = findViewById(R.id.hongkong);
            Switch pt = findViewById(R.id.portugal);
            Switch tw = findViewById(R.id.taiwan);
            Switch jp = findViewById(R.id.japan);
            Switch it = findViewById(R.id.italy);
            Switch us = findViewById(R.id.usa);

            String tem[] = str[position].split("_");
            if(tem[1].equals("1")){hk.setChecked(true);}else{hk.setChecked(false);}
            if(tem[2].equals("1")){pt.setChecked(true);}else{pt.setChecked(false);}
            if(tem[3].equals("1")){tw.setChecked(true);}else{tw.setChecked(false);}
            if(tem[4].equals("1")){jp.setChecked(true);}else{jp.setChecked(false);}
            if(tem[5].equals("1")){it.setChecked(true);}else{it.setChecked(false);}
            if(tem[6].equals("1")){us.setChecked(true);}else{us.setChecked(false);}

            EditText restaurant_name = findViewById(R.id.restaurant_name);
            restaurant_name.setText(tem[0]);
            edit_position = position;

        }
    };

    public void EditRestaurant(View view){

        EditText restaurant_name = findViewById(R.id.restaurant_name);
        restaurant_list = getSharedPreferences("restaurant",0);

        Switch hk = findViewById(R.id.hongkong);
        Switch pt = findViewById(R.id.portugal);
        Switch tw = findViewById(R.id.taiwan);
        Switch jp = findViewById(R.id.japan);
        Switch it = findViewById(R.id.italy);
        Switch us = findViewById(R.id.usa);

        String restaurant_data;
        String restaurant_data_view;

        restaurant_data = restaurant_name.getText().toString();

        if(restaurant_data.equals("")){
            Toast.makeText(MainActivity.this,"Enter the name you fool.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(hk.isChecked()==false&&hk.isChecked()==pt.isChecked()&&
                hk.isChecked()==tw.isChecked()&&hk.isChecked()==jp.isChecked()&&
                hk.isChecked()==it.isChecked()&&hk.isChecked()==us.isChecked()){
            Toast.makeText(MainActivity.this,"Pick a type you fool.", Toast.LENGTH_SHORT).show();
            return;
        }

        restaurant_data = RestaurantDataEncode(restaurant_data,hk.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,pt.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,tw.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,jp.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,it.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,us.isChecked());

        restaurant_data_view = RestaurantDataDecode(restaurant_data);

        restaurant_list.edit()
                .putString("restaurant_data_"+Integer.toString(edit_position), restaurant_data)
                .putString("restaurant_data_view_"+Integer.toString(edit_position), restaurant_data_view)
                .commit();

        str[edit_position] = restaurant_data;
        str_view[edit_position] = restaurant_data_view;

        Toast.makeText(MainActivity.this,"Edit successful.", Toast.LENGTH_SHORT).show();


        jumpToRestaurantList(view);

    }

    public void DeleteRestaurant(View view){


        EditText restaurant_name = findViewById(R.id.restaurant_name);
        restaurant_list = getSharedPreferences("restaurant",0);

        String restaurant_data;
        String restaurant_data_view;

        int count = restaurant_list.getInt("restaurant_counter", -1);

        str = new String[count-1];
        str_view = new String[count-1];

        for(int i=0;i<edit_position;i++){

            restaurant_data = restaurant_list.getString("restaurant_data_"+Integer.toString(i), "NULL");
            restaurant_data_view = restaurant_list.getString("restaurant_data_view_"+Integer.toString(i), "NULL");

            str[i] = restaurant_data;
            str_view[i] = restaurant_data_view;

        }

        for(int i=edit_position;i<count-1;i++){

            restaurant_data = restaurant_list.getString("restaurant_data_"+Integer.toString(i+1), "NULL");
            restaurant_data_view = restaurant_list.getString("restaurant_data_view_"+Integer.toString(i+1), "NULL");

            str[i] = restaurant_data;
            str_view[i] = restaurant_data_view;

            restaurant_list.edit()
                    .putString("restaurant_data_"+Integer.toString(i), restaurant_data)
                    .putString("restaurant_data_view_"+Integer.toString(i), restaurant_data_view)
                    .commit();

        }

        restaurant_list.edit()
                .putInt("restaurant_counter", count-1)
                .commit();

        Toast.makeText(MainActivity.this,"Delete successful.", Toast.LENGTH_SHORT).show();

        jumpToGetRestaurant(view);

    }




    public void jumpToGetRestaurant(View view){


        Intent intent = getIntent();
        finish();
        startActivity(intent);

        /*setContentView(R.layout.activity_main);
        this.setTitle("What to eat today...");
        restaurant_list = getSharedPreferences("restaurant",0);

        Button to_add_new = findViewById(R.id.to_add_new);
        TextView random_restaurant = findViewById(R.id.random_restaurant);

        random_restaurant.setText("   Let's go get some food...");*/

    }


    public void GetRestaurant(View view){

        int n1,n2,n3;
        Random rand;

        if(System.currentTimeMillis()<time + 1000*20*1){
            Toast.makeText(MainActivity.this,"This app CANNOT help you if you don't follow the rules...", Toast.LENGTH_SHORT).show();
        }
        time = System.currentTimeMillis();


        Switch hk = findViewById(R.id.hongkong);
        Switch pt = findViewById(R.id.portugal);
        Switch tw = findViewById(R.id.taiwan);
        Switch jp = findViewById(R.id.japan);
        Switch it = findViewById(R.id.italy);
        Switch us = findViewById(R.id.usa);

        int count = str.length;
        String ans[] = new String[str.length];

        int i;
        int ans_count = 0;
        for (i=0;i<count;i++){
            String tem[] = str[i].split("_");
            int flag = 0;
            if(hk.isChecked()&&tem[1].equals("1")){flag=1;}
            if(pt.isChecked()&&tem[2].equals("1")){flag=1;}
            if(tw.isChecked()&&tem[3].equals("1")){flag=1;}
            if(jp.isChecked()&&tem[4].equals("1")){flag=1;}
            if(it.isChecked()&&tem[5].equals("1")){flag=1;}
            if(us.isChecked()&&tem[6].equals("1")){flag=1;}
            if(flag==1) {
                ans[ans_count] = tem[0];
                ans_count++;
            }
        }


        TextView random_restaurant = findViewById(R.id.random_restaurant);
        TextView second_choice = findViewById(R.id.second_choice);
        TextView third_choice = findViewById(R.id.third_choice);
        if(ans_count==0){
            random_restaurant.setText("   Cannot find any suitable restaurant...");
            n1 = -1;
        }
        else{

            rand = new Random();
            n1 = rand.nextInt(ans_count);
            random_restaurant.setText("   I would say let's go to "+ ans[n1] +"~   ");

        }


        if(ans_count<2){
            second_choice.setText("Cannot find any suitable restaurant...");
            n2 = -1;
        }
        else{

            rand = new Random();
            n2 = rand.nextInt(ans_count);
            while(n2 == n1){
                rand = new Random();
                n2 = rand.nextInt(ans_count);
            }
            second_choice.setText("Or let's go to "+ ans[n2] +"~   ");

        }

        if(ans_count<3){
            third_choice.setText("Cannot find any suitable restaurant...");
        }
        else{

            rand = new Random();
            n3 = rand.nextInt(ans_count);
            while(n3 == n1 || n3 == n2){
                rand = new Random();
                n3 = rand.nextInt(ans_count);
            }
            third_choice.setText("Or let's go to "+ ans[n3] +"~   ");

        }







        return;
    }

    public void addNewRestaurant(View view){

        EditText restaurant_name = findViewById(R.id.restaurant_name);

        Switch hk = findViewById(R.id.hongkong);
        Switch pt = findViewById(R.id.portugal);
        Switch tw = findViewById(R.id.taiwan);
        Switch jp = findViewById(R.id.japan);
        Switch it = findViewById(R.id.italy);
        Switch us = findViewById(R.id.usa);

        restaurant_list = getSharedPreferences("restaurant",0);

        int count;
        count = restaurant_list.getInt("restaurant_counter", -1) + 1;

        String restaurant_data;
        String restaurant_data_view;

        restaurant_data = restaurant_name.getText().toString();

        if(restaurant_data.equals("")){
            Toast.makeText(MainActivity.this,"Enter the name you fool.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(hk.isChecked()==false&&hk.isChecked()==pt.isChecked()&&
                hk.isChecked()==tw.isChecked()&&hk.isChecked()==jp.isChecked()&&
                hk.isChecked()==it.isChecked()&&hk.isChecked()==us.isChecked()){
            Toast.makeText(MainActivity.this,"Pick a type you fool.", Toast.LENGTH_SHORT).show();
            return;
        }

        restaurant_data = RestaurantDataEncode(restaurant_data,hk.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,pt.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,tw.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,jp.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,it.isChecked());
        restaurant_data = RestaurantDataEncode(restaurant_data,us.isChecked());

        restaurant_data_view = RestaurantDataDecode(restaurant_data);

        restaurant_list.edit()
                .putInt("restaurant_counter", count)
                .putString("restaurant_data_"+Integer.toString(count-1), restaurant_data)
                .putString("restaurant_data_view_"+Integer.toString(count-1), restaurant_data_view)
                .commit();

        int i;
        str = new String[count];
        str_view = new String[count];
        for(i=0;i<count;i++) {
            str[i] = restaurant_list.getString("restaurant_data_"+Integer.toString(i), "NULL");
            str_view[i] = restaurant_list.getString("restaurant_data_view_"+Integer.toString(i), "NULL");
        }

        Toast.makeText(MainActivity.this,"New restaurant available~", Toast.LENGTH_SHORT).show();


    }

    public void clearAllShare(View view){

        restaurant_list = getSharedPreferences("restaurant",0);

        restaurant_list.edit().clear().commit();

        if(restaurant_list.getInt("restaurant_counter", -1)==-1){
            restaurant_list.edit()
                    .putInt("restaurant_counter", 0)
                    .commit();
        }

        str = new String[0];

    }




    public String RestaurantDataEncode(String restaurant_data,boolean checked){
        if(checked){
            restaurant_data = restaurant_data +"_1";
        }
        else{
            restaurant_data = restaurant_data +"_0";
        }
        return restaurant_data;
    }

    public String RestaurantDataDecode(String restaurant_data){

        String output;
        boolean flag = false;

        String[] tem = restaurant_data.split("_");
        output = tem[0];

        if(tem[1]=="0"&&tem[1]==tem[2]&&tem[2]==tem[3]&&tem[3]==tem[4]&&tem[4]==tem[5]){
            return output;
        }
        else{
            output = output + " (";
            if(tem[1].equals("1")){output = output + "港式";flag=true;}
            if(tem[2].equals("1")){if(flag==true){output = output + ",";}output = output + "葡式";flag=true;}
            if(tem[3].equals("1")){if(flag==true){output = output + ",";}output = output + "台式";flag=true;}
            if(tem[4].equals("1")){if(flag==true){output = output + ",";}output = output + "日式";flag=true;}
            if(tem[5].equals("1")){if(flag==true){output = output + ",";}output = output + "​義式";flag=true;}
            if(tem[6].equals("1")){if(flag==true){output = output + ",";}output = output + "美式";}
            output = output + ")";
            //output = tem[1];
            return output;
        }
    }


}
