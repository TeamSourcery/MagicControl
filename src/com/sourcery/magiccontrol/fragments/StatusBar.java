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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.util.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    
    private static final String STATUS_BAR_TRANSPARENCY = "status_bar_transparency";
    private static final String NAV_BAR_TRANSPARENCY = "nav_bar_transparency";

   
    private ListPreference mStatusbarTransparency;
    private ListPreference mNavigationBarTransparency;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_transparent);

        PreferenceScreen prefSet = getPreferenceScreen();

       
        mStatusbarTransparency = (ListPreference) prefSet.findPreference(STATUS_BAR_TRANSPARENCY);
        mNavigationBarTransparency = (ListPreference) prefSet.findPreference(NAV_BAR_TRANSPARENCY);

  

        int statusBarTransparency = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_TRANSPARENCY, 100);
        mStatusbarTransparency.setValue(String.valueOf(statusBarTransparency));
        mStatusbarTransparency.setOnPreferenceChangeListener(this);

       int navBarTransparency = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
 	               Settings.System.NAV_BAR_TRANSPARENCY, 100);
        mNavigationBarTransparency.setValue(String.valueOf(navBarTransparency));
        mNavigationBarTransparency.setOnPreferenceChangeListener(this);

       
       }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if  (preference == mStatusbarTransparency) {
            int statusBarTransparency = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_TRANSPARENCY, statusBarTransparency);
            return true;
    	} else if (preference == mNavigationBarTransparency) {
            int navBarTransparency = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_TRANSPARENCY, navBarTransparency);
            return true;
        }
         return false;
	
     }
          

  }
