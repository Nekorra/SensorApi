package com.fabiorogeriosj.plugin;

import java.util.List;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.hardware.*;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;

public class SensorApi extends CordovaPlugin implements SensorEventListener {

    JSONObject sensorData;
    String TYPE_SENSOR;

    private SensorManager sensorManager;// Sensor manager
    Sensor mSensor;                     // Compass sensor returned by sensor manager

    private CallbackContext callbackContext;

    /**
     * Constructor.
     */
    public SensorApi() {
        this.sensorData = new JSONObject();
        this.TYPE_SENSOR = "";
    }
    
    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.sensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action                The action to execute.
     * @param args                  JSONArry of arguments for the plugin.
     * @param callbackS=Context     The callback id used when calling back into JavaScript.
     * @return                      True if the action was valid.
     * @throws JSONException 
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("start")) {
            this.TYPE_SENSOR = args.getString(0);
            this.start(this.TYPE_SENSOR);
        }
        else if (action.equals("stop")) {
            this.stop();
        }
        else if (action.equals("getState")) {

            // If its stopped then user needs to enable sensor using "start" method
            this.TYPE_SENSOR = args.getString(0);

            if (!this.sensorData.has(this.TYPE_SENSOR)) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Sensors disabled, run start method before getState"));
            }
            // If not running, then this is an async call, so don't worry about waiting
            if (this.sensorData.has(this.TYPE_SENSOR)) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, getValue(this.TYPE_SENSOR)));

                /*
                 if (r == Sensors.ERROR_FAILED_TO_START) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, Sensors.ERROR_FAILED_TO_START));
                    return true;
                }
            
                 */
                
                // Set a timeout callback on the main thread.
                /*

                Handler handler = new Handler(Looper.getMainLooper()); 
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Sensors.this.timeout();
                        }
                    }, 2000);
                 */
                
            } else {
            }
        } else {
            // Unsupported action
            return false;
        }
        return true;
    }

    /**
     * Called when listener is to be shut down and object is being destroyed.
     */
    public void onDestroy() {
        this.stop();
    }

    /**
     * Called when app has navigated and JS listeners have been destroyed.
     */
    public void onReset() {
        this.stop();
    }

    public void stop() {
        this.sensorManager.unregisterListener(this);
        this.sensorData = new JSONObject();
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    /**
     * Start listening for compass sensor.
     *
     * @return          status of listener
     */
    public int start(String sens) {
        
        // Get sensor from sensor manager
        @SuppressWarnings("deprecation")
        List<Sensor> list = new ArrayList<Sensor>();
        if(sens.equals("PROXIMITY")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
        } else if(sens.equals("ACCELEROMETER")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        } else if(sens.equals("GRAVITY")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        } else if(sens.equals("GYROSCOPE")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        } else if(sens.equals("GYROSCOPE_UNCALIBRATED")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        } else if(sens.equals("LINEAR_ACCELERATION")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        } else if(sens.equals("ROTATION_VECTOR")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        } else if(sens.equals("SIGNIFICANT_MOTION")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_SIGNIFICANT_MOTION);
        } else if(sens.equals("STEP_COUNTER")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_STEP_COUNTER);
        } else if(sens.equals("STEP_DETECTOR")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_STEP_DETECTOR);
        } else if(sens.equals("GAME_ROTATION_VECTOR")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR);
        } else if(sens.equals("GEOMAGNETIC_ROTATION_VECTOR")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        } else if(sens.equals("MAGNETIC_FIELD")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        } else if(sens.equals("MAGNETIC_FIELD_UNCALIBRATED")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        } else if(sens.equals("ORIENTATION")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        } else if(sens.equals("AMBIENT_TEMPERATURE")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else if(sens.equals("LIGHT")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_LIGHT);
        } else if(sens.equals("PRESSURE")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
        } else if(sens.equals("RELATIVE_HUMIDITY")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_RELATIVE_HUMIDITY);
        } else if(sens.equals("TEMPERATURE")){
            list = this.sensorManager.getSensorList(Sensor.TYPE_TEMPERATURE);
        }

        // If found, then register as listener
        if (list != null && list.size() > 0) {
            this.mSensor = list.get(0);
            this.sensorManager.registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            this.sensorData.put(sens, ""); 
        }
    }

    /**
     * Stop listening to compass sensor.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    /**
     * Called after a delay to time out if the listener has not attached fast enough.
     */

     /*
          private void timeout() {
        if (this.status == Sensors.STARTING) {
            this.setStatus(Sensors.ERROR_FAILED_TO_START);
            if (this.callbackContext != null) {
                this.callbackContext.error("Compass listener failed to start.");
            }
        }
    }

      */


    /**
     * Sensor listener event.
     *
     * @param SensorEvent event
     */
    public void onSensorChanged(SensorEvent event) {
        try {
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("ACCELEROMETER", value);
   
            }
            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("AMBIENT_TEMPERATURE", value);
                

            }
            if (sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("GAME_ROTATION_VECTOR", value);
                
    
            }
            if (sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("GEOMAGNETIC_ROTATION_VECTOR", value);
                
  
            }
            if (sensor.getType() == Sensor.TYPE_GRAVITY) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("GRAVITY", value);
                
     
            }
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("GYROSCOPE", value);
                
  
            }
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("GYROSCOPE_UNCALIBRATED", value);
                
        
            }
            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("LIGHT", value);
                

            }
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    this.value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("LINEAR_ACCELERATION", value);
                
   
            }
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    this.value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("MAGNETIC_FIELD", value);
                
   
            }
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("MAGNETIC_FIELD_UNCALIBRATED", value);
 
            }
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("ORIENTATION", value);
                
    
            }
            if (sensor.getType() == Sensor.TYPE_PRESSURE) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("PRESSURE", value);
                

            }
            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("PROXIMITY", value);
  
            }
            if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("RELATIVE_HUMIDITY", value);
   
            }
            if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("ROTATION_VECTOR", value);
                
    
            }
            if (sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("SIGNIFICANT_MOTION", value);
                

            }
            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("STEP_COUNTER", value);
    
            }
            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("STEP_DETECTOR", value);
                
  
            }
            if (sensor.getType() == Sensor.TYPE_TEMPERATURE) {
                JSONArray value = new JSONArray();
                for(int i=0;i < event.values.length;i++){
                    value.put(Float.parseFloat(event.values[i]+""));
                }
                this.sensorData.put("TEMPERATURE", value);
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the most recent distance. 
     *
     * @return          distance 
     */
    public int[] getValue(String sens) {
        return this.value.getJSONObject(sens);
    }



 

}