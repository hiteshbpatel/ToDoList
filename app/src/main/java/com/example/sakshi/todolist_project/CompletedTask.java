package com.example.sakshi.todolist_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import static com.example.sakshi.todolist_project.R.id.complete;
import static com.example.sakshi.todolist_project.R.id.listview;

/**
 * Created by sakshi on 8/7/2017.
 */

public class CompletedTask extends AppCompatActivity {


    int pos;
    ListView listView;
    ArrayList<Data> completelist;
    DataHandler handler;
    CustomAdapter customAdapter;
    LinearLayout linearLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completedtask_list);
        listView = (ListView)findViewById(listview);
        completelist = new ArrayList<>();
        handler=new DataHandler(this);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);
        completelist = handler.getcompletedtaskdata();

        if(handler.completedrowcount()==0){
            //Toast.makeText(this, "No Completed Task to display", Toast.LENGTH_SHORT).show();
            TextView textView = new TextView(CompletedTask.this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(             //select linearlayoutparam- set the width & height
                    ViewGroup.LayoutParams.MATCH_PARENT, 48));

            textView.setGravity(Gravity.CENTER);
            textView.setText(R.string.no_complete_task);
            linearLayout.addView(textView);
        }
        customAdapter=new CustomAdapter(this,completelist,1);
        listView.setAdapter(customAdapter);
        registerForContextMenu(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        menu.setHeaderTitle("Select Action");
        menu.add(0,1,1,"Set Incomplete");             //adding sub menus
        menu.add(0,2,2,"Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position= pos;
        if(item.getItemId()==1 && item.getGroupId()==0){
            handler.changestatus(completelist.get(position).getId());
            completelist = handler.getcompletedtaskdata();
            if(handler.completedrowcount()==0){
                //Toast.makeText(this, "No Completed Task to display", Toast.LENGTH_SHORT).show();
                TextView textView = new TextView(CompletedTask.this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(             //select linearlayoutparam- set the width & height
                        ViewGroup.LayoutParams.MATCH_PARENT, 48));

                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText("No completed tasks");
                linearLayout.addView(textView);
            }

            customAdapter=new CustomAdapter(this,completelist,1);
            listView.setAdapter(customAdapter);
            return true;
        }
        if(item.getItemId()==2 && item.getGroupId()==0){
            boolean status = handler.deletetask(completelist.get(position).getId());
                if(status){
                    Toast.makeText(CompletedTask.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                    completelist = handler.getcompletedtaskdata();
                    customAdapter=new CustomAdapter(CompletedTask.this,completelist,1);
                    listView.setAdapter(customAdapter);

                    if(handler.completedrowcount()==0){
                        //TODO: add image also here
                        TextView textView = new TextView(CompletedTask.this);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(             //select linearlayoutparam- set the width & height
                                ViewGroup.LayoutParams.MATCH_PARENT, 48));
                        textView.setGravity(Gravity.CENTER_VERTICAL);
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setText("No completed tasks");
                        linearLayout.addView(textView);

                    }

                }else{
                    Toast.makeText(CompletedTask.this, "Error in deleting Task", Toast.LENGTH_SHORT).show();
                }
            return  true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.complete_menu,menu);
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
                listView.invalidate();
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
        return super.onOptionsItemSelected(item);
    }
}
