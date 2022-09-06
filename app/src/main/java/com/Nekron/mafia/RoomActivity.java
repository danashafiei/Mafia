package com.Nekron.mafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.NotificationChannel;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.app.NotificationManager;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import com.Nekron.mafia.apputil.GetRoleAdapter;
import com.Nekron.mafia.apputil.PostAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import com.Nekron.mafia.apputil.AppConfig;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.IRtcEngineEventHandler;
import com.Nekron.mafia.agora.media.RtcTokenBuilder;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomActivity extends AppCompatActivity {
    static List<String> userInRoom = new ArrayList<>();
    static List<String> check = new ArrayList<>();

    static List<String> userRole = new ArrayList<>();
    private String showMafias = "";
    private String jsonResponseUserName, jsonResponseRoomName, jsonResponseRole;
    private RecyclerView recyclerView, tvRole;
    private SwipeRefreshLayout swipeRefreshLayout, refreshRole;
    private PostAdapter adapter;
    private GetRoleAdapter getRoleAdapter;
    private RtcEngine mRtcEngine;
    private AppConfig appConfig;
    private Button leaveBtn, startGameBtn,refreshRoleBtn;
    private Switch ndSwitch;
    private View childView;
    private int recyclerViewItemPosition;

    private ProgressBar leaveGameProgress, startGameProgress;

    private String appId = "40b2416f0ab249f48573fb3446da068c";
    private String appCertificate ="3bf2af58c52b4f5c8992589e11c0c8c8";
    private int expirationTimeInSeconds = 3600;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshRole = findViewById(R.id.refresh_role);
        LinearLayoutManager llm = new LinearLayoutManager(RoomActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        appConfig = new AppConfig(this);
        adapter = new PostAdapter(RoomActivity.this, userInRoom);

        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        tvRole = findViewById(R.id.tv_role);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RoomActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tvRole.setHasFixedSize(true);
        tvRole.setLayoutManager(linearLayoutManager);
        getRoleAdapter = new GetRoleAdapter(RoomActivity.this, userRole);
        tvRole.setAdapter(getRoleAdapter);
        getRoleAdapter.notifyDataSetChanged();





        leaveBtn = findViewById(R.id.leave_the_room_btn);
        startGameBtn =  findViewById(R.id.start_game_btn);
        refreshRoleBtn = findViewById(R.id.refresh_role_btn);
        ndSwitch = findViewById(R.id.switch1);
        leaveGameProgress = findViewById(R.id.leave_game_progress);
        startGameProgress = findViewById(R.id.start_game_progress);

        leaveGameProgress.setVisibility(View.INVISIBLE);
        startGameProgress.setVisibility(View.INVISIBLE);
        GetData();

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        }catch (Exception e){
            throw new RuntimeException("check the error");
        }
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate, appConfig.getRoomName(), appConfig.getUsernameOfUser(), RtcTokenBuilder.Role.Role_Publisher, timestamp);
        mRtcEngine.joinChannelWithUserAccount(result, appConfig.getRoomName(), appConfig.getUsernameOfUser());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager  =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(RoomActivity.this, new GestureDetector.OnGestureListener() {

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
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && gestureDetector.onTouchEvent(e)){
                    recyclerViewItemPosition = rv.getChildAdapterPosition(childView);
                    //Toast.makeText(RoomActivity.this, userInRoom.get(recyclerViewItemPosition), Toast.LENGTH_LONG).show();
                    if ((appConfig.getYourRole().equals("mafia") || appConfig.getYourRole().equals("godfather")) && appConfig.getRoomAdmin()){
                        popupMenu_admin_and_mafia(childView, userInRoom.get(recyclerViewItemPosition));
                    }else if (appConfig.getYourRole().equals("doctor") && appConfig.getRoomAdmin()){
                        popupMenu_admin_and_doctor(childView, userInRoom.get(recyclerViewItemPosition));

                    }else if (appConfig.getYourRole().equals("sheriff") && appConfig.getRoomAdmin()){
                        popupMenu_admin_and_sheriff(childView, userInRoom.get(recyclerViewItemPosition));

                    }else if (appConfig.getRoomAdmin()){
                        popupMenu_admin(childView, userInRoom.get(recyclerViewItemPosition));

                    }else if(appConfig.getYourRole().equals("mafia")){
                        popupMenu_mafia(childView, userInRoom.get(recyclerViewItemPosition));

                    }else if(appConfig.getYourRole().equals("doctor")){
                        popupMenu_doctor(childView, userInRoom.get(recyclerViewItemPosition));

                    }else if(appConfig.getYourRole().equals("sheriff")){
                        popupMenu_sheriff(childView, userInRoom.get(recyclerViewItemPosition));

                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appConfig.isGameStarted()){
                    Cue.init().with(RoomActivity.this).setMessage("بازی در حال انجام است!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();
                }else{
                    StartGame();
                }
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveTheRoom();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userInRoom = new ArrayList<String>();
                GetData();



            }
        });
        refreshRole.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userRole = new ArrayList<String>();
                getRoles();
            }
        });
        refreshRoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRole = new ArrayList<String>();
                getRoles();
                if (appConfig.getYourRole().equals("mafia") || appConfig.getYourRole().equals("godfather")){
                    showMafia();
                    addNotification();

                }

            }
        });
        ndSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    dayOrNight("night");
                }else{
                    dayOrNight("day");
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        backButtonHandler();
        return;
    }
    public void backButtonHandler(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RoomActivity.this);
        alertDialog.setTitle("Leave the room?");
        alertDialog.setMessage("are you sure you want to leave the room?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                leaveTheRoom();
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
    public void leaveTheRoom(){
        leaveGameProgress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/leave_the_room.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                leaveGameProgress.setVisibility(View.INVISIBLE);
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("deleted")){
                    appConfig.updateGameStartedStatus(false);
                    appConfig.saveYourRole("");
                    appConfig.saveRoomAdmin(false);
                    userRole.clear();
                    Intent intent = new Intent(RoomActivity.this, MainActivity.class);
                    intent.putExtra("username", appConfig.getUsernameOfUser());
                    startActivity(intent);
                    finish();
                }else if (response.equals("error")){

                }else{

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                leaveGameProgress.setVisibility(View.INVISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("roomName", appConfig.getRoomName().toString());
                params.put("username", appConfig.getUsernameOfUser().toString());

                return params;
            }
        };
        Volley.newRequestQueue(RoomActivity.this).add(request);
    }
    public void GetData(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://danashafiei.ir/get_user_in_room.php", null,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("from onResponse()", response.toString());

                if(response.toString().equals("user_not_found")){

                }

                try{

                    userInRoom.clear();
                    for (int i = 0; i <= response.length(); i++){
                        jsonResponseRoomName = "";
                        jsonResponseUserName = "";
                        JSONObject userObject = (JSONObject) response.get(i);
                        jsonResponseRoomName = userObject.getString("room_name");
                        jsonResponseUserName = userObject.getString("username");


                        if (jsonResponseRoomName.equals(appConfig.getRoomName())){
                            userInRoom.add(jsonResponseUserName);
                            adapter.update(userInRoom);


                        }

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

                Toast.makeText(RoomActivity.this, "server error", Toast.LENGTH_LONG).show();


            }
        });
        requestQueue.add(request);

    }

    public void StartGame(){
        startGameProgress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                startGameProgress.setVisibility(View.INVISIBLE);
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("success")){
                    appConfig.updateGameStartedStatus(true);
                    getRoles();
                }else if (response.equals("error_set_role")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();
                }else if (response.equals("your_not_admin")){
                    Cue.init().with(RoomActivity.this).setMessage("فقط ادمین میتواند بازی را شروع کند!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();
                }else if (response.equals("server_error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();
                }else{
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startGameProgress.setVisibility(View.INVISIBLE);
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("key", "start_game");

                return params;
            }
        };
        Volley.newRequestQueue(RoomActivity.this).add(request);

    }
    public void getRoles(){
        startGameProgress.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://danashafiei.ir/get_roles.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                startGameProgress.setVisibility(View.INVISIBLE);
                Log.d("from onResponse()", response.toString());

                try {
                    userRole.clear();
                    for(int i = 0; i < response.length(); i++){
                        jsonResponseRole = "";
                        jsonResponseUserName = "";
                        jsonResponseRoomName = "";
                        JSONObject userObject = (JSONObject) response.get(i);
                        jsonResponseRoomName = userObject.getString("room_name");
                        jsonResponseUserName = userObject.getString("username");
                        jsonResponseRole = userObject.getString("set_role");
                        if (jsonResponseRoomName.equals(appConfig.getRoomName()) && jsonResponseUserName.equals(appConfig.getUsernameOfUser())){
                            userRole.add(jsonResponseRole);
                            appConfig.saveYourRole(jsonResponseRole);
                            getRoleAdapter.update(userRole);

                        }

                        refreshRole.setRefreshing(false);
                    }
                    tvRole.hasPendingAdapterUpdates();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startGameProgress.setVisibility(View.INVISIBLE);
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        });
        requestQueue.add(request);
    }
    public void showMafia(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://danashafiei.ir/get_roles.php", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("from onResponse()", response.toString());


                try {
                    showMafias = "";
                    for(int i = 0; i < response.length(); i++){
                        jsonResponseRole = "";
                        jsonResponseUserName = "";
                        jsonResponseRoomName = "";
                        JSONObject userObject = (JSONObject) response.get(i);
                        jsonResponseRoomName = userObject.getString("room_name");
                        jsonResponseUserName = userObject.getString("username");
                        jsonResponseRole = userObject.getString("set_role");
                        if (jsonResponseRoomName.equals(appConfig.getRoomName()) && (jsonResponseRole.equals("mafia") || jsonResponseRole.equals("godfather"))){
                            showMafias += jsonResponseUserName + "\t";


                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }
    public void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(RoomActivity.this, "My Notification");
        builder.setSmallIcon(R.drawable.ic_launcher1_background);
        builder.setContentTitle("mafias :");
        builder.setContentText(showMafias);
        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(RoomActivity.this);
        notificationManager.notify(1, builder.build());
    }

    private void popupMenu_mafia(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.mafia_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnKill:
                        kill_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_doctor(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.doctor_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnSave:
                        save_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_sheriff(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.sheriff_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnCheckMafia:
                        check_mafia_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_admin(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnDelete:
                        deleteUser(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_admin_and_mafia(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.d_k_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnDeleteK:
                        deleteUser(username);
                        break;
                    case R.id.btnKillA:
                        kill_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_admin_and_doctor(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.d_s_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnDeleteS:
                        deleteUser(username);
                        break;
                    case R.id.btnSaveA:
                        save_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void popupMenu_admin_and_sheriff(View v, String username){
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.getMenuInflater().inflate(R.menu.d_c_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnDeleteC:
                        deleteUser(username);
                        break;
                    case R.id.btnCheckMafiaA:
                        check_mafia_user(username);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void dayOrNight(String ND){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    Cue.init().with(RoomActivity.this).setMessage("حالت"+ND+"فعال شد").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.SUCCESS).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if(response.equals("game_not_started")){
                    Cue.init().with(RoomActivity.this).setMessage("بازی هنوز شروع نشده!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("not_admin")){
                    Cue.init().with(RoomActivity.this).setMessage("فقط ادمین میتواند حالت شب و روز را فعال کند.").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else{
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("ND", ND);
                params.put("key", "ND");
                return params;
            }
        };
        requestQueue.add(request);
    }
    public void kill_user(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    Cue.init().with(RoomActivity.this).setMessage("در خواست ثبت شد،نتیجه در روز مشخص می شود").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.SUCCESS).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("already")){
                    Cue.init().with(RoomActivity.this).setMessage("قبلا عملیات کشتن را انجام داده اید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if(response.equals("error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("is_day")){
                    Cue.init().with(RoomActivity.this).setMessage("فقط در شب میتوانید یک نفر را بکشید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("server_error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("key", "kill_user");
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("username1", username);

                return params;
            }

        };
        requestQueue.add(request);



    }
    public void save_user(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    Cue.init().with(RoomActivity.this).setMessage("در خواست ثبت شد،نتیجه در روز مشخص می شود").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.SUCCESS).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("wait")){
                    Cue.init().with(RoomActivity.this).setMessage("صبر کنید که مافیا یک نفر را بکشد!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if(response.equals("error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("is_day")){
                    Cue.init().with(RoomActivity.this).setMessage("فقط در شب میتوانید یک نفر را نجات دهید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("server_error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("key", "save_user");
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("username1", username);

                return params;
            }

        };
        requestQueue.add(request);


    }
    public void check_mafia_user(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    Cue.init().with(RoomActivity.this).setMessage("کاربر مافیا است").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.SUCCESS).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("no")){
                    Cue.init().with(RoomActivity.this).setMessage("کاربر مافیا نمی باشد").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if(response.equals("error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("is_day")){
                    Cue.init().with(RoomActivity.this).setMessage("فقط در شب میتوانید استعلام بگیرید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("server_error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("key", "check_mafia");
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("username1", username);

                return params;
            }

        };
        requestQueue.add(request);

    }
    public void deleteUser(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://danashafiei.ir/api.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    Cue.init().with(RoomActivity.this).setMessage("کاربر با موفقیت حذف شد").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.SUCCESS).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if(response.equals("error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکلی پیش امده دوباره تلاش کنید!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }else if (response.equals("server_error")){
                    Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                            .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                            .setPadding(30).setTextSize(20).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cue.init().with(RoomActivity.this).setMessage("مشکل در برقراری ارتباط با سرور!").setGravity(Gravity.CENTER_VERTICAL)
                        .setType(Type.WARNING).setDuration(Duration.LONG).setBorderWidth(5).setCornerRadius(10).setCustomFontColor(Color.parseColor("#000000"),Color.parseColor("#000000"),Color.parseColor("#000000"))
                        .setPadding(30).setTextSize(20).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("key", "delete_user");
                params.put("roomName", appConfig.getRoomName());
                params.put("username", appConfig.getUsernameOfUser());
                params.put("username1", username);

                return params;
            }

        };
        requestQueue.add(request);

    }
   public void check_state(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("from onResponse()", response);
                response = response.trim();
                if (response.equals("ok")){
                    check_state1();

                }else{

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("key", "check");
                params.put("roomName", appConfig.getRoomName());
                


                return params;
            }

        };
        requestQueue.add(request);
   }
   public void check_state1(){
       RequestQueue requestQueue = Volley.newRequestQueue(this);
       JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://danashafiei.ir/get_roles.php", null, new Response.Listener<JSONArray>() {
           @Override
           public void onResponse(JSONArray response) {
               Log.d("from onResponse()", response.toString());


               try {

                   for(int i = 0; i < response.length(); i++){
                       jsonResponseRole = "";
                       jsonResponseUserName = "";
                       jsonResponseRoomName = "";
                       JSONObject userObject = (JSONObject) response.get(i);
                       jsonResponseRoomName = userObject.getString("room_name");
                       jsonResponseUserName = userObject.getString("username");
                       jsonResponseRole = userObject.getString("set_role");
                       if (jsonResponseRoomName.equals(appConfig.getRoomName()) && jsonResponseRole.equals(null)){
                           userInRoom.add(jsonResponseUserName + " -> deleted");



                       }
                       swipeRefreshLayout.setRefreshing(false);

                   }

               }catch (JSONException e){
                   e.printStackTrace();
               }

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });
       requestQueue.add(request);
   }
    protected void onDestroy(){
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}