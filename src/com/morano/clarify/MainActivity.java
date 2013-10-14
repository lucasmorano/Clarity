package com.morano.clarify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ToggleButton 	btnSwitch;
    private Camera 			camera;
    private boolean 		isFlashOn;
    private Boolean 		hasFlash;
    private Parameters 		params;
    private RelativeLayout 	layout;
 
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (RelativeLayout) findViewById(R.id.layoutId);
		btnSwitch = (ToggleButton) findViewById(R.id.toggle);
		btnSwitch.setChecked(true);
		
		if(hasFlash == null){
			hasFlash = doesThisDeviceSupportFlash();
		}
		
		if (!hasFlash) {
			displayError();
			return;
		}

		btnSwitch.setOnClickListener(btnSwitchListener);
	}
    
    View.OnClickListener btnSwitchListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (btnSwitch.isChecked() && isFlashOn) {
				turnOffFlash();
			} else {
				turnOnFlash();
			}
		};
    };
    
    
    
	/**
	 * Display an alert dialog indicating there's no flash light on the device 
	 */
	private void displayError() {
		AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
				.create();
		alert.setTitle("Error");
		alert.setMessage("Sorry, your device doesn't support flash light!");
		alert.setButton(RESULT_OK, "OK", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert.show();
		return;
	}


	/**
	 * Checks if the device support flash light
	 * @return
	 */
	private Boolean doesThisDeviceSupportFlash() {
		return getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
        
        
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
			}
		}
	}
      
      /*
      * Turning On flash
      */
	private void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;
			layout.setBackgroundResource(R.drawable.back);
		}

	}
     
     
	private void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
			layout.setBackgroundResource(R.drawable.back_dark);
		}
	}
	
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	    }
	 
	    @Override
	    protected void onPause() {
	        super.onPause();
	        turnOffFlash();
	    }
	 
	    @Override
	    protected void onRestart() {
	        super.onRestart();
	    }
	 
	    @Override
	    protected void onResume() {
	        super.onResume();
	        if(hasFlash && !btnSwitch.isChecked())
	            turnOnFlash();
	    }
	 
	    @Override
	    protected void onStart() {
	        super.onStart();
	        getCamera();
	    }
	 
	    @Override
	    protected void onStop() {
	        super.onStop();
	        if (camera != null) {
	            camera.release();
	            camera = null;
	        }
	    }

}
