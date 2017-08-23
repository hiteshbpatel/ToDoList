package com.example.sakshi.todolist_project;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.key;
import static android.R.id.empty;
import static com.example.sakshi.todolist_project.R.id.desc;
import static com.example.sakshi.todolist_project.R.id.task;

public class MainActivity extends AppCompatActivity  {
    DataHandler handler;
    ListView listview;
    DatePicker date;
    LinearLayout linearLayout;
    ArrayList<Data> completelist;
    CustomAdapter customAdapter;
    int pos;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView)findViewById(R.id.listview);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout1);
        completelist = new ArrayList<>();
        handler=new DataHandler(this);
        refreshList(0);
        setLanguage();
        if(handler.rowcount()==0){

            Toast.makeText(this, R.string.no_data_to_display, Toast.LENGTH_SHORT).show();
        }
        registerForContextMenu(listview);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Data data = completelist.get(position);
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_populate);
                dialog.show();

                final EditText title = (EditText)dialog.findViewById(R.id.populated_title);
                final EditText description = (EditText)dialog.findViewById(R.id.populated_description);
                final EditText date = (EditText)dialog.findViewById(R.id.populate_date);
                ImageView datepicker = (ImageView) dialog.findViewById(R.id.show_datepicker);
                Button cancel = (Button)dialog.findViewById(R.id.cancel);
                Button update = (Button)dialog.findViewById(R.id.update);
                title.setText(data.getTitle());
                description.setText(data.getDescription());
                date.setText(data.getDate());
               /* datepicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Hello image", Toast.LENGTH_SHORT).show();
                        DatePicker datePicker = new DatePicker(MainActivity.this);
                        datePicker.setEnabled(true);


                    }
                });*/
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newtitle = title.getText().toString().trim();
                        String newdesc = description.getText().toString().trim();
                        String newdate = date.getText().toString().trim();

                        boolean status = handler.updatedata(newtitle,newdesc,newdate,completelist.get(position).getId());
                        dialog.dismiss();
                        refreshList(2);
                        if(!status){
                            Toast.makeText(MainActivity.this, R.string.cant_update_task, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getPosition(position);
               return false;

            }
        });


    }
    public void getPosition(int position){
        pos = position;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.select_action);
        menu.add(0,1,1,R.string.set_complete);
        menu.add(0,2,2,R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position= pos;

        if(item.getItemId()==1 && item.getGroupId()==0){
            handler.changestatus(completelist.get(position).getId());
            refreshList(0);
            return true;
        }
        if(item.getItemId()==2 && item.getGroupId()==0){
            boolean status = handler.deletetask(completelist.get(position).getId());
            if(status){
                Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
               refreshList(0);
            }else{
                Toast.makeText(MainActivity.this, R.string.error_in_deleting_task, Toast.LENGTH_SHORT).show();
            }
            return  true;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                customAdapter.filter(searchQuery.toString().trim());
                listview.invalidate();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.search){
            return true;
        }

        if(id==R.id.settings){
            Intent intent = new Intent(getApplicationContext(),Settings.class);
            startActivity(intent);
        }
        if(id==R.id.add){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);
            dialog.setTitle(R.string.Add_Task);
            dialog.show();

            final EditText title = (EditText)dialog.findViewById(R.id.title);
            final EditText description = (EditText)dialog.findViewById(desc);
            date = (DatePicker)dialog.findViewById(R.id.datepicker);
            final Calendar calendar = Calendar.getInstance();
            Button save = (Button)dialog.findViewById(R.id.save);
            Button cancel = (Button)dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String gettitle = title.getText().toString().trim();
                    String getdesc = description.getText().toString().trim();
                    int day = date.getDayOfMonth();
                    int month = date.getMonth()+1;
                    int year = date.getYear();
                    if(gettitle.isEmpty()){
                        title.setError("Title can not be empty. Enter Title First.");
                    }
                    if(gettitle.length()>=20){
                        title.setError("Title should be less than 20 characters");
                    }
                    if(getdesc.length()>=40){
                        description.setError("Description should be less than 40 characters");
                    }
                    String inputDateStr = String.format("%s/%s/%s", day, month, year);
                    Date inputDate = null;
                    try {
                        inputDate = new SimpleDateFormat("yyyy/MM/dd").parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.setTime(inputDate);
                    final String getfinaldate = year+"-"+month+"-"+day;
                    if(gettitle.length()<20 && getdesc.length()<40 && !gettitle.isEmpty()){
                        boolean r=handler.insert_Data(gettitle,getdesc,getfinaldate);
                        if(r==true)
                        {
                            Toast.makeText(MainActivity.this, R.string.task_inserted, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            refreshList(2);

                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
        if(id==R.id.complete){
            Intent intent = new Intent(MainActivity.this,CompletedTask.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList(0);
        setLanguage();
    }

    public void refreshList(int a){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //getting values from shared preference
        String sortorder = sharedPreferences.getString("sort","New Tasks first");
        completelist = handler.getAlldata(sortorder);
        customAdapter=new CustomAdapter(this,completelist,a);
        listview.setAdapter(customAdapter);
    }
    public void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
    public void setLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //getting values from shared preference
        String language = sharedPreferences.getString("language","English");
        switch (language){
            case "English":
                setLocale("en");
                break;
            case "Hindi":
                setLocale("hi");
                break;
            case "French":
                setLocale("fr");
                break;
        }
    }
}
