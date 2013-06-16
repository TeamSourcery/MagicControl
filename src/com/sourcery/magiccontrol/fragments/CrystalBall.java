/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sourcery.magiccontrol.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.INotificationManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.util.Helpers;
import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.util.CMDProcessor;
import com.sourcery.magiccontrol.util.Utils;
import com.sourcery.magiccontrol.widgets.AlphaSeekBar;
import com.sourcery.magiccontrol.widgets.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import net.margaritov.preference.colorpicker.ColorPickerView;

public class CrystalBall extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "CrystalBall";
   
    
   
    private static final String KEY_HALO_ENABLED = "halo_enabled";
    private static final String KEY_WE_WANT_POPUPS = "show_popup";
    private static final String KEY_HALO_PAUSE = "halo_pause";
    private static final String KEY_HALO_STATE = "halo_state";
    private static final String KEY_HALO_HIDE = "halo_hide";
    private static final String KEY_HALO_REVERSED = "halo_reversed";
    
    
  
    private CheckBoxPreference mHaloEnabled;
    private CheckBoxPreference mWeWantPopups;
    private CheckBoxPreference mHaloPause;
    private ListPreference mHaloState;
    private CheckBoxPreference mHaloHide;
    private CheckBoxPreference mHaloReversed;

   
   
    private Activity mActivity;

    private int seekbarProgress;
 
    private INotificationManager mNotificationManager;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        addPreferencesFromResource(R.xml.crystal_ball);

        PreferenceScreen prefSet = getPreferenceScreen();
        PreferenceScreen prefs = getPreferenceScreen();

       

        mNotificationManager = INotificationManager.Stub.asInterface(
                ServiceManager.getService(Context.NOTIFICATION_SERVICE));

        mHaloEnabled = (CheckBoxPreference) findPreference(KEY_HALO_ENABLED);
        mHaloEnabled.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.HALO_ENABLED, 0) == 1);

       int showPopups = Settings.System.getInt(getContentResolver(), Settings.System.WE_WANT_POPUPS, 1);

        mWeWantPopups = (CheckBoxPreference) findPreference(KEY_WE_WANT_POPUPS);
        mWeWantPopups.setOnPreferenceChangeListener(this);
        mWeWantPopups.setChecked(showPopups > 0);

       int isLowRAM = (ActivityManager.isLargeRAM()) ? 0 : 1;
        mHaloPause = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_PAUSE);
        mHaloPause.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_PAUSE, isLowRAM) == 1);

        mHaloState = (ListPreference) prefSet.findPreference(KEY_HALO_STATE);
        mHaloState.setValue(String.valueOf((isHaloPolicyBlack() ? "1" : "0")));
        mHaloState.setOnPreferenceChangeListener(this);

        mHaloHide = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_HIDE);
        mHaloHide.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_HIDE, 0) == 1);

        mHaloReversed = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_REVERSED);
        mHaloReversed.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_REVERSED, 1) == 1);
                
        }

     
        
     @Override
     public void onResume() {
         super.onResume();
     }

     private boolean isHaloPolicyBlack() {
        try {
            return mNotificationManager.isHaloPolicyBlack();
        } catch (android.os.RemoteException ex) {
                // System dead
        }
        return true;
    }
 	
   
 

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        boolean value;
        if  (preference == mHaloEnabled) {  
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HALO_ENABLED, mHaloEnabled.isChecked()
                    ? 1 : 0);  
         } else if (preference == mHaloHide) {  
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_HIDE, mHaloHide.isChecked()
                    ? 1 : 0); 
         } else if (preference == mHaloPause) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_PAUSE, mHaloPause.isChecked()
                    ? 1 : 0);
        } else if (preference == mHaloReversed) {  
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_REVERSED, mHaloReversed.isChecked()
                     ? 1 : 0);  
    }
         return super.onPreferenceTreeClick(preferenceScreen, preference);

     }

     

          
         @Override
    public boolean onPreferenceChange(Preference preference, Object Value) {
         final String key = preference.getKey();
         if (preference == mHaloState) {
            boolean state = Integer.valueOf((String) Value) == 1;
            try {
                mNotificationManager.setHaloPolicyBlack(state);
            } catch (android.os.RemoteException ex) {
                // System dead
            }          
            return true;
          } else if (preference == mWeWantPopups) {
            boolean checked = (Boolean) Value;
                        Settings.System.putBoolean(getActivity().getContentResolver(),
                                Settings.System.WE_WANT_POPUPS, checked);
            return true;
        }
            return false;
             }
    
}
