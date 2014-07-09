package com.example.syvokdei.vistimote;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.utils.L;

import java.util.List;

class BeaconInfo
{
    String name;
    boolean here;
}

public class KasKurDirba extends Activity {

    private static final String TAG = KasKurDirba.class.getSimpleName();
    private static final int NOTIFICATION_ID = 123;

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

    private BeaconManager beaconManager;
    private NotificationManager notificationManager;

    private NotificationManager notManager;

    private boolean virtuve=false,care=false,studziai=false,here=false;

    private final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    private Beacon active;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kas_kur_dirba);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        active=null;


        notManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        beaconManager=new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                //Log.d(TAG, "Ranged beacons: " + beacons);

                if(active==null) {
                    for (Beacon a : beacons) {
                        if(a.getMacAddress().equals("EC:32:23:28:3F:03")||a.getMacAddress().equals("E8:B1:EF:29:62:86")||a.getMacAddress().equals("E6:1B:ED:06:B0:72")) {
                            here = Math.min(Utils.computeAccuracy(a), 1.0) < 1.0;
                            if (here) {
                                active = a;
                                break;
                            } else {
                                active = null;
                                here=false;
                            }
                        }
                    }
                }
                if(active!=null) {
                    here = Math.min(Utils.computeAccuracy(active), 1.0) < 1.0;
                    if(!here)
                        active=null;
                }
                if(here) {
                    Log.e(TAG,"Is here.");
                    if (active.getMacAddress().equals("EC:32:23:28:3F:03") && !virtuve) //PURPLE/VIOLETINIS (virtuve)
                    {
                        virtuve = true;
                        Notification n = new Notification.Builder(getApplicationContext())
                                .setContentTitle("Sveiki atvyke i virtuve!")
                                .setContentText("Kavai 50% nuolaida")
                                .setSmallIcon(R.drawable.beacon_gray).setSound(alarmSound).setVibrate(new long[]{0, 100, 200, 300}).build();
                        notManager.notify(0, n);
                    }

                    else

                    if (active.getMacAddress().equals("E8:B1:EF:29:62:86") && !care) //CYAN/MELYNAS (visma care)
                    {
                        care = true;
                        Notification n = new Notification.Builder(getApplicationContext())
                                .setContentTitle("Sveiki atvyke pas Visma Care!")
                                .setContentText("Saldainiu nebeturime")
                                .setSmallIcon(R.drawable.beacon_gray).setSound(alarmSound).setVibrate(new long[]{0, 100, 200, 300}).build();
                        notManager.notify(1, n);
                    }

                    else

                    if (active.getMacAddress().equals("E6:1B:ED:06:B0:72") && !studziai) //CREAM/SVIESUS (studentai)
                    {
                        studziai = true;
                        Notification n = new Notification.Builder(getApplicationContext())
                                .setContentTitle("Sveiki atvyke pas studentus!")
                                .setContentText("Pls fix project, Ernestas")
                                .setSmallIcon(R.drawable.beacon_gray).setSound(alarmSound).setVibrate(new long[]{0, 100, 200, 300}).build();
                        notManager.notify(2, n);
                    }
                    else
                        active=null;
                }
                else if(virtuve||care||studziai) {
                    Log.e(TAG,"Is NOT here.");
                    if (virtuve) {
                        notManager.cancel(0);
                        virtuve = false;
                    } else if (care) {
                        notManager.cancel(1);
                        care = false;
                    } else if (studziai) {
                        notManager.cancel(2);
                        studziai = false;
                    }
                }
            }
        });
    }

    @Override protected void onStart()
    {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.d(TAG, "Error while starting monitoring");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }
}
