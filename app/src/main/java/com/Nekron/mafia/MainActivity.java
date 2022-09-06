package com.Nekron.mafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.Nekron.mafia.apputil.AppConfig;

import com.Nekron.mafia.apputil.ShowRoomsAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private TextView showUserName, tvResponse;
    static List<String> rooms = new ArrayList<>();
    private String jsonResponseRoomName;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShowRoomsAdapter showRoomsAdapter;

    private Button btnMenu,  btnCreateRoom, btnSearchRoom;
    private AppConfig appConfig;
    private EditText roomName, roomNameSearch;
    private ProgressBar progress_home;

    private View childView;
    private int recyclerViewItemPosition;



    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH
    };
    private boolean checkSelfPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appConfig = new AppConfig(this);
        recyclerView = findViewById(R.id.recyclerView_rooms);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_rooms);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        showRoomsAdapter = new ShowRoomsAdapter(MainActivity.this, rooms);
        recyclerView.setAdapter(showRoomsAdapter);
        showRoomsAdapter.notifyDataSetChanged();

        showUserName = findViewById(R.id.show_userName);
        btnMenu = findViewById(R.id.btnMenu);
        btnCreateRoom = findViewById(R.id.create_room);
        btnSearchRoom = findViewById(R.id.search_room_by_name_btn);
        roomName = findViewById(R.id.room_name);
        roomNameSearch = findViewById(R.id.search_room_by_name);
        tvResponse = findViewById(R.id.tvResponse_home);
        progress_home = findViewById(R.id.progressBar_home);



        progress_home.setVisibility(View.INVISIBLE);



        String username = getIntent().getStringExtra("username");

        showUserName.setText(username);

        GetRooms();

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu(v);
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {

                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    return false;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (childView != null && gestureDetector.onTouchEvent(motionEvent)){
                    recyclerViewItemPosition = recyclerView.getChildAdapterPosition(childView);
                    popupMenu_joinRoom(childView, rooms.get(recyclerViewItemPosition));
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomName.length() < 3){
                    roomName.setError("Room Name most be minimum 3 characters and maximum 50 characters!");
                }else if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)){
                    progress_home.setVisibility(View.VISIBLE);

                    StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progress_home.setVisibility(View.INVISIBLE);
                            Log.d("from onResponse()", response);
                            response = response.trim();
                            if (response.equals("room_created")){

                                StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/add_admin_user_in_room.php", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progress_home.setVisibility(View.INVISIBLE);
                                        Log.d("from onResponse()", response);
                                        response = response.trim();
                                        if (response.equals("user_is_admin")){

                                            appConfig.saveRoomName(roomName.getText().toString());
                                            appConfig.saveRoomAdmin(true);
                                            Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                                            intent.putExtra("roomName", roomName.getText().toString());
                                            startActivity(intent);
                                            finish();


                                        }else if(response.equals("error!")){
                                            tvResponse.setText("Error join in room, please try again below");
                                            tvResponse.setTextColor(Color.YELLOW);
                                            tvResponse.setTextSize(12);

                                        } else if (response.equals("user_not_added")){

                                            tvResponse.setText("Error join in room, please try again below");
                                            tvResponse.setTextColor(Color.YELLOW);
                                            tvResponse.setTextSize(12);

                                        }else if (response.equals("server_error")){

                                            tvResponse.setText("Error connect to server!");
                                            tvResponse.setTextColor(Color.RED);
                                        }else{

                                            tvResponse.setText("Error!");
                                            tvResponse.setTextColor(Color.RED);
                                        }




                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progress_home.setVisibility(View.INVISIBLE);

                                        tvResponse.setText("Error in server or Your is offline!");
                                        tvResponse.setTextColor(Color.RED);
                                        tvResponse.setTextSize(12);

                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError{
                                        Map<String, String> params = new HashMap<>();
                                        params.put("roomName", roomName.getText().toString());
                                        params.put("username", appConfig.getUsernameOfUser().toString());

                                        return params;
                                    }
                                };
                                Volley.newRequestQueue(MainActivity.this).add(request);

                            }else if (response.equals("room_not_create")){

                                tvResponse.setText("Room already exists!");
                                tvResponse.setTextColor(Color.YELLOW);
                            }else if (response.equals("server_error")){

                                tvResponse.setText("Error connect to server!");
                                tvResponse.setTextColor(Color.RED);
                            }else{

                                tvResponse.setText("Error!");
                                tvResponse.setTextColor(Color.RED);
                            }




                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progress_home.setVisibility(View.INVISIBLE);

                            tvResponse.setText("Error in server or Your is offline!");
                            tvResponse.setTextColor(Color.RED);
                            tvResponse.setTextSize(12);

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError{
                            Map<String, String> params = new HashMap<>();
                            params.put("roomName", roomName.getText().toString());
                            params.put("key", "c_room");

                            return params;
                        }
                    };
                    Volley.newRequestQueue(MainActivity.this).add(request);


                }
            }
        });

        btnSearchRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (roomNameSearch.length() < 3){
                   roomNameSearch.setError("Room Name most be minimum 3 characters and maximum 50 characters!");
               }else if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)){
                   progress_home.setVisibility(View.VISIBLE);

                   StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/add_user_in_room.php", new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           progress_home.setVisibility(View.INVISIBLE);
                           Log.d("from onResponse()", response);
                           response = response.trim();
                           if (response.equals("user_added")){
                               appConfig.saveRoomName(roomNameSearch.getText().toString());
                               Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                               intent.putExtra("roomName", roomNameSearch.getText().toString());
                               startActivity(intent);
                               finish();

                           }else if (response.equals("user_not_added")){
                               tvResponse.setText("Could not join the room,please try again.");
                               tvResponse.setTextColor(Color.YELLOW);
                               tvResponse.setTextSize(12);

                           }else if (response.equals("room_not_exist")){
                               tvResponse.setText("room not exist!");
                               tvResponse.setTextColor(Color.YELLOW);

                           }else if(response.equals("the_room_is_full")){
                               tvResponse.setText("room is full!");
                               tvResponse.setTextColor(Color.YELLOW);

                           }else if (response.equals("server_error")){
                               tvResponse.setText("Error connect to server!");
                               tvResponse.setTextColor(Color.RED);

                           }else{
                               tvResponse.setText("Error!");
                               tvResponse.setTextColor(Color.RED);

                           }

                       }
                   }
                           , new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           progress_home.setVisibility(View.INVISIBLE);

                           tvResponse.setText("Error in server or Your is offline!");
                           tvResponse.setTextColor(Color.RED);
                           tvResponse.setTextSize(12);

                       }
                   }){
                       @Override
                       protected Map<String, String> getParams() throws AuthFailureError{
                           Map<String, String> params = new HashMap<>();
                           params.put("roomName", roomNameSearch.getText().toString());
                           params.put("username", appConfig.getUsernameOfUser());

                           return params;
                       }
                   };
                   Volley.newRequestQueue(MainActivity.this).add(request);
               }

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rooms = new ArrayList<>();
                GetRooms();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    @Override
    public void onBackPressed(){
        backButtonHandler();
        return;
    }
    public void backButtonHandler(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Leave the App?");
        alertDialog.setMessage("are you sure you want to leave the App?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    private void popupMenu(View v){

        PopupMenu popUp = new PopupMenu(this, v);
        popUp.getMenuInflater().inflate(R.menu.menu_option, popUp.getMenu());
        popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btnExit:
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.btnLogOut:
                        appConfig.updateUserLoginStatus(false);
                        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent1);
                        finish();
                        break;


                    default:
                        break;
                }
                return false;
            }
        });
        popUp.show();

    }
    private void popupMenu_joinRoom(View v, String room_name){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.j_room, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btn_j_room:
                        joinInRoom(room_name);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    public void GetRooms(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://danashafiei.ir/get_rooms.php", null,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("from onResponse()", response.toString());

                if(response.toString().equals("user_not_found")){

                }

                try{

                    rooms.clear();
                    for (int i = 0; i <= response.length(); i++){
                        jsonResponseRoomName = "";

                        JSONObject userObject = (JSONObject) response.get(i);
                        jsonResponseRoomName = userObject.getString("room_name");
                        rooms.add(jsonResponseRoomName);
                        showRoomsAdapter.update(rooms);


                        swipeRefreshLayout.setRefreshing(false);

                    }
                    recyclerView.hasPendingAdapterUpdates();


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(MainActivity.this).setMessage("هیچ اتاق بازی یافت نشد!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();
            }
        });
        requestQueue.add(request);

    }
    public void joinInRoom(String room_name){
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)){
            progress_home.setVisibility(View.VISIBLE);

            StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/add_user_in_room.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress_home.setVisibility(View.INVISIBLE);
                    Log.d("from onResponse()", response);
                    response = response.trim();
                    if (response.equals("user_added")){
                        appConfig.saveRoomName(roomNameSearch.getText().toString());
                        Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                        intent.putExtra("roomName", roomNameSearch.getText().toString());
                        startActivity(intent);
                        finish();

                    }else if (response.equals("user_not_added")){
                        tvResponse.setText("Could not join the room,please try again.");
                        tvResponse.setTextColor(Color.YELLOW);
                        tvResponse.setTextSize(12);

                    }else if (response.equals("room_not_exist")){
                        tvResponse.setText("room not exist!");
                        tvResponse.setTextColor(Color.YELLOW);

                    }else if(response.equals("the_room_is_full")){
                        tvResponse.setText("room is full!");
                        tvResponse.setTextColor(Color.YELLOW);

                    }else if (response.equals("server_error")){
                        tvResponse.setText("Error connect to server!");
                        tvResponse.setTextColor(Color.RED);

                    }else{
                        tvResponse.setText("Error!");
                        tvResponse.setTextColor(Color.RED);

                    }

                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress_home.setVisibility(View.INVISIBLE);

                    tvResponse.setText("Error in server or Your is offline!");
                    tvResponse.setTextColor(Color.RED);
                    tvResponse.setTextSize(12);

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError{
                    Map<String, String> params = new HashMap<>();
                    params.put("roomName", room_name);
                    params.put("username", appConfig.getUsernameOfUser());

                    return params;
                }
            };
            Volley.newRequestQueue(MainActivity.this).add(request);
        }
    }



}