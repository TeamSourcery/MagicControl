
package com.sourcery.magiccontrol.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.R.xml;

public class PowerMenu extends SettingsPreferenceFragment {

    
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";
    private static final String KEY_POWER_BUTTON_TORCH = "power_button_torch";

    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowTorchToggle;
    CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mShowNavBarHide;
    
    private CheckBoxPreference mPowerButtonTorch;
 
    private boolean torchSupported() {
        return getResources().getBoolean(R.bool.has_led_flash);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);

        mPowerButtonTorch = (CheckBoxPreference) findPreference(KEY_POWER_BUTTON_TORCH);
        if (torchSupported()) {
            mPowerButtonTorch.setChecked((Settings.System.getInt(getActivity().
                    getApplicationContext().getContentResolver(),
                    Settings.System.POWER_BUTTON_TORCH, 0) == 1));
        } else {
            getPreferenceScreen().removePreference(mPowerButtonTorch);
        }
        
        mShowTorchToggle = (CheckBoxPreference) findPreference(PREF_TORCH_TOGGLE);
        mShowTorchToggle.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE, false));

        mShowScreenShot = (CheckBoxPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, false));

        mShowAirplaneToggle = (CheckBoxPreference) findPreference(PREF_AIRPLANE_TOGGLE);
        mShowAirplaneToggle.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE, true));

        mShowNavBarHide = (CheckBoxPreference) findPreference(PREF_NAVBAR_HIDE);
        mShowNavBarHide.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, false));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
           if (preference == mShowScreenShot) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_SCREENSHOT,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowTorchToggle) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowAirplaneToggle) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowNavBarHide) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mPowerButtonTorch) {
 	     boolean enabled = mPowerButtonTorch.isChecked();
             Settings.System.putInt(getContentResolver(), Settings.System.POWER_BUTTON_TORCH,
                    enabled ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
