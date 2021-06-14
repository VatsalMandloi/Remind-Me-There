package com.vatsal.remindmethere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//import static com.vatsal.remindmethere.MapsActivity.GEOFENCE_ID;

public class MainActivity<recyclerView> extends AppCompatActivity {
    DatabaseHelper db;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<geofences> List;
    GeofenceHelper geofenceHelper;
    GeofencingClient geofencingClient;
    public Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        geocoder  = new Geocoder(this, Locale.getDefault());
        List = new ArrayList<>();
        List = db.getAllGeofences();
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        //    Intent intent=getIntent();
        // List = intent.getStringArrayListExtra("mylist");

//Location location = geofencingEvent.getTriggeringLocation();
//ListLocation.add(location.toString());
        //   List<Geofence> geofenceList = Geofenceschanges();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(List);

        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.onClickListner() {

            @Override
           public void onSeekbarChange(String id, int radius,int pos){
               geofences current = List.get(pos);
               String location = current.getLocation();
                db.updateGeofenceRadius(id, radius);
                addGeofence(id, radius, location);

            }

            @Override
            public void onSwitchClick(String id, boolean b, int pos) {
                geofences current = List.get(pos);
                String msg;
                 int toggle;
                 if(b){toggle=1; msg = "Turned On successfully";}
                 else{toggle = 0; msg = "turned off Successfully";}
                  db.updateGeofenceToggle(id, toggle);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                current.setToggle(toggle);
                geofenceToggle(id, current.getRadius(), current.getLocation(), toggle);

            }

            @Override
            public void onClick(int position, View v) {
                geofences current = List.get(position);
                List <String> list = new ArrayList<>();




                // Create the object of
                // AlertDialog Builder class
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(MainActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Are you sure, you want to delete Remainder ?");

                // Set Alert Title
                builder.setTitle("Delete Remainder !");

                // Set Cancelable false
                // for when the user clicks on the outside
                // the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name
                // OnClickListener method is use of
                // DialogInterface interface.

                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        db.deleteGeofence(current.getID());
                                        List.remove(position);
                                        recyclerAdapter.notifyItemRemoved(position);
                                        list.add(current.getID());
                                        geofencingClient.removeGeofences(list);

                                    }
                                });

                // Set the Negative button with No name
                // OnClickListener method is use
                // of DialogInterface interface.
                builder
                        .setNegativeButton(
                                "No",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        // If user click no
                                        // then dialog box is canceled.
                                        dialog.cancel();
                                    }
                                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }

    private void geofenceToggle(String id, int radius, String location, int toggle){
        if(toggle==1){ addGeofence(id, radius, location);}
        if (toggle==0){
            List <String> list = new ArrayList<>();
            list.add(id);
            geofencingClient.removeGeofences(list);
        }
    }

    @SuppressLint("MissingPermission")
    private void addGeofence(String id, int radius, String location) {
       LatLng latLng = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            Address address=addresses.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
        } catch (IOException e){
            e.printStackTrace();
        }

     if(latLng==null){
         Toast.makeText(geofenceHelper, "Latlng is null", Toast.LENGTH_SHORT).show();
     }
else{
        Toast.makeText(geofenceHelper, "Remainder updated successfully at "+location, Toast.LENGTH_SHORT).show();
        Geofence geofence = geofenceHelper.getGeofence(id, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                       // Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }}
}