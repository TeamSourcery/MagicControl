
package com.sourcery.magiccontrol.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.util.CMDProcessor;
import com.sourcery.magiccontrol.util.Helpers;
import com.sourcery.magiccontrol.MagicControlActivity;
import com.android.internal.util.sourcery.NavBarHelpers;
import com.sourcery.magiccontrol.widgets.SeekBarPreference;

public class TabletMode extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "TabletMode";

   
    private static final String PREF_FORCE_DUAL_PANEL = "force_dualpanel";
    private static final String PREF_USER_MODE_UI = "user_mode_ui";
    private static final String PREF_HIDE_EXTRAS = "hide_extras";
    
   
    CheckBoxPreference mDualpane;
    ListPreference mUserModeUI;
    CheckBoxPreference mHideExtras;
    Preference mLcdDensity;
    Preference mWidthHelp;
    SeekBarPreference mWidthPort;
    SeekBarPreference mWidthLand;
   

         
    int newDensityValue;

    DensityChanger densityFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_tabletmode);

        PreferenceScreen prefs = getPreferenceScreen();
        ContentResolver cr = mContext.getContentResolver();
               
        mHideExtras = (CheckBoxPreference) findPreference(PREF_HIDE_EXTRAS);
        mHideExtras.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.HIDE_EXTRAS_SYSTEM_BAR, false));

        mWidthHelp = (Preference) findPreference("width_help");

        float defaultPort = Settings.System.getFloat(getActivity()
                .getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH_PORT,
                0f);
        mWidthPort = (SeekBarPreference) findPreference("width_port");
        mWidthPort.setInitValue((int) (defaultPort * 2.5f));
        mWidthPort.setOnPreferenceChangeListener(this);

        float defaultLand = Settings.System.getFloat(getActivity()
                .getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH_LAND,
                0f);
        mWidthLand = (SeekBarPreference) findPreference("width_land");
        mWidthLand.setInitValue((int) (defaultLand * 2.5f));
        mWidthLand.setOnPreferenceChangeListener(this);
       
        mDualpane = (CheckBoxPreference) findPreference(PREF_FORCE_DUAL_PANEL);
        mDualpane.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.FORCE_DUAL_PANEL, getResources().getBoolean(
                        com.android.internal.R.bool.preferences_prefer_dual_pane)));
    
        mUserModeUI = (ListPreference) findPreference(PREF_USER_MODE_UI);
        int uiMode = Settings.System.getInt(cr,
		     Settings.System.CURRENT_UI_MODE, 0);
        mUserModeUI.setValue(Integer.toString(Settings.System.getInt(cr,
                Settings.System.USER_UI_MODE, uiMode)));
        mUserModeUI.setOnPreferenceChangeListener(this);
           
    mLcdDensity = findPreference("lcd_density_setup");
    String currentProperty = SystemProperties.get("persist.lcd_density");
    if (currentProperty.length() == 0) currentProperty = SystemProperties.get("ro.sf.lcd_density");
    try {
         newDensityValue = Integer.parseInt(currentProperty);
    } catch (Exception e) {
         getPreferenceScreen().removePreference(mLcdDensity);
    }

    mLcdDensity.setSummary(getResources().getString(R.string.current_lcd_density) + currentProperty);
 
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
             if (preference == mHideExtras) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                   Settings.System.HIDE_EXTRAS_SYSTEM_BAR,
                   ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mDualpane) {
             Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.FORCE_DUAL_PANEL,
                    ((CheckBoxPreference) preference).isChecked());
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mLcdDensity) {
              ((PreferenceActivity) getActivity())
                     .startPreferenceFragment(new DensityChanger(), true);
             return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

   @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
         if (preference == mUserModeUI) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USER_UI_MODE, Integer.parseInt((String) newValue));
            Helpers.restartSystemUI();
            return true;
       } else if (preference == mWidthPort) {
            float val = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH_PORT,
                    val * 0.4f);
            return true;
        } else if (preference == mWidthLand) {
            float val = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH_LAND,
                    val * 0.4f);
            return true;
        }
        return false;
    }
}
