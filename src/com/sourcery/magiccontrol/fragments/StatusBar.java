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

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import net.margaritov.preference.colorpicker.ColorPickerView;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    
   
    private static final String PREF_STATUSBAR_BACKGROUND_COLOR = "statusbar_background_color";
   
   
    ColorPickerPreference mStatusbarBgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_transparent);

        PreferenceScreen prefSet = getPreferenceScreen();

       
       
        mStatusbarBgColor = (ColorPickerPreference) findPreference(PREF_STATUSBAR_BACKGROUND_COLOR);
        mStatusbarBgColor.setOnPreferenceChangeListener(this);

       
       
     
}
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusbarBgColor) {
 	           String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                   .valueOf(newValue)));
            preference.setSummary(hex);
 
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BACKGROUND_COLOR, intHex);
           Log.e("SOURCERY", intHex + "");
            return true;
        }
         return false;
	
     }
          

  }
