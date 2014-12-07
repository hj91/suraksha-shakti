/* @author= Harshad Joshi */

package com.harshad.gps;


import java.io.IOException;
import java.util.List;
import java.util.Locale;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

@SuppressLint("ShowToast")
public class MainActivity extends Activity implements LocationListener {
  private TextView latituteField;
  private TextView longitudeField;
  private LocationManager locationManager;
  private String provider;
  String str3,str4,str5,str6;
  EditText addrtxt;
  String Addrtxt;
  //String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
  String loco;
  
  
  

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    latituteField = (TextView) findViewById(R.id.TextView02);
    longitudeField = (TextView) findViewById(R.id.TextView04);
    //addrtxt=(EditText) findViewById(R.id.editText1);  
   // Addrtxt=(TextView) findViewById(R.id.Tex
    
   // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	//boolean enabled=service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

    		// Check if enabled and if not send user to the GSP settings
    		// Better solution would be to display a dialog and suggesting to 
    		// go to the settings
    		if (!enabled) {
    		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    		  startActivity(intent);
    		} 
    	//	else
    	//	{
    			//enabled=service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    			//Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    			//startActivity(intent);
    		
    	//	}
    		
    // Define the criteria how to select the locatioin provider -> use
    // default
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, false);
    Location location = locationManager.getLastKnownLocation(provider);

    // Initialize the location fields
    if (location != null) {
      System.out.println("Provider " + provider + " has been selected.");
      onLocationChanged(location);
    } else {
      latituteField.setText ("Location not available");
      longitudeField.setText ("Location not available");
    }
  }

  /* Request updates at startup */
  @Override
  protected void onResume() {
    super.onResume();
    locationManager.requestLocationUpdates(provider, 400, 1, this);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this);
    //latituteField.setText ("");
    //longitudeField.setText ("");
  }

  @Override
  public void onLocationChanged(Location location) {
    float lat =  (float) (location.getLatitude());
    float lng = (float) (location.getLongitude());
  //  List<Address> addresses;
	
    latituteField.setText(Float.toString(lat));
    longitudeField.setText(Float.toString(lng));
    String str1= String.valueOf(lat); //Float.toString(lat); //
    String str2= String.valueOf(lng); //Float.toString(lng);  //
    str3="I am at this location "+" Latitude "+str1+" "+"Longitude "+str2;
    Log.i(str3,"did  i reach ");
    Toast.makeText(this, "i am in onLocationChanged and value is "+str3,Toast.LENGTH_SHORT).show();
    String mylocation = str1 + "," + str2;
    loco=mylocation.toString();
    str5=str1;
    str6=str2;
    try{
        GetAddress(str1,str2);

    } catch(Exception e)
    {
    	Toast.makeText(this,"Error is GetAddress()", 1000).show();
    	Toast.makeText(this, e.toString(), 1000).show();
    }
    


   
  }  
  
  @SuppressLint("ShowToast")
public String GetAddress(String lat, String lon)
	{
		Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
		String ret = "";
		
		try {
			List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(str5), Double.parseDouble(str6), 5);
			if(addresses != null && addresses.size() > 0  ) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				
				}
				ret = strReturnedAddress.toString();
				str4=ret;
				Toast.makeText(this, str4, Toast.LENGTH_LONG).show();
				appendLog(str4);
			}
			else{
				Toast.makeText(this,str4,10000).show();
				Toast.makeText(this,"No Address returned!",1000).show();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = "Can't get Address!";
		}
		return ret;
	}
  //added on 7 december 2014 - harshad joshi
  public void appendLog(String text)
  {       
     File logFile = new File("sdcard/gpslog.txt"); //this is hardcoded for testing. ToDo - add dynamic determination of storage
     if (!logFile.exists())
     {
        try
        {
           logFile.createNewFile();
        } 
        catch (IOException e)
        {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
     }
     try
     {
        //BufferedWriter for performance, true to set append to file flag
        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
        buf.append(text);
        buf.newLine();
        buf.close();
     }
     catch (IOException e)
     {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }
  }
  
  public void onClick(View v)
  {
	  try
	  {
		  
	
	  Toast.makeText(this, "value of str in onClick is "+str3,Toast.LENGTH_SHORT).show();
	  Context context = getApplicationContext();
      
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context); 
      Intent i = new Intent(android.content.Intent.ACTION_SEND);

      i.setType("text/plain");
      i.putExtra(Intent.EXTRA_SUBJECT, settings.getString(loco," http://maps.google.com/maps?q=loc:"+loco)); //, getString(R.id.TextView02).toString()+getString(R.id.TextView04).toString()));
      i.putExtra(Intent.EXTRA_TEXT, settings.getString("message_body",str4+" "+str3+" http://maps.google.com/maps?q=loc:"+loco));      
      startActivity(Intent.createChooser(i, getString(R.id.TextView04)));
      
      
	  	//sendSmsMessage(addrtxt.getText().toString(),str3);
	  	//sendSmsMessage(addrtxt.getText().toString(),deviceId);
  		Toast.makeText(this, "I am sharing data", Toast.LENGTH_LONG).show();
  		
	  }
	  catch(Exception e)
	  {
		  Toast.makeText(this, "I caught an exception" +e.toString(),Toast.LENGTH_SHORT).show();

	  }
  	
  }
  
  public void sendSmsMessage(String straddr, String strmsg)
	{
		SmsManager smsMgr=SmsManager.getDefault();
		smsMgr.sendTextMessage(straddr, null, strmsg, null, null);
		Toast.makeText(this, "SMS Sent", Toast.LENGTH_LONG).show();
	}
	
  	

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

      
     @Override 
  public void onProviderEnabled(String provider) {
    Toast.makeText(this, "Enabled new provider " + provider,
        Toast.LENGTH_SHORT).show();

  }

    
  @Override
  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider,
        Toast.LENGTH_SHORT).show();
  }
  
  public void onClickEvent(View v)
  {
 	 latituteField.setText("");
 	 longitudeField.setText("");
 	 super.onStop();
	  finish();
 	 System.exit(0);
 	     	 
  }
  } 
