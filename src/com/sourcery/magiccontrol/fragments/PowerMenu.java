
package com.sourcery.magiccontrol.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.R.xml;

public class PowerMenu extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_EXPANDED_DESKTOP = "power_menu_expanded_desktop";
    
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";
    ListPreference mExpandedDesktopPref;
    private static final String PREF_REBOOT_KEYGUARD = "show_reboot_keyguard";

    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowTorchToggle;
    CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mShowNavBarHide;
    CheckBoxPreference mShowExpandedDesktopToggle;
    CheckBoxPreference mShowRebootKeyguard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);
        
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

        mShowRebootKeyguard = (CheckBoxPreference) findPreference(PREF_REBOOT_KEYGUARD);
        mShowRebootKeyguard.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD, true));

        PreferenceScreen prefSet = getPreferenceScreen();
        mExpandedDesktopPref = (ListPreference) prefSet.findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(), Settings.System.EXPANDED_DESKTOP_STYLE, 0);
        mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
        updateExpandedDesktopSummary(expandedDesktopValue);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mExpandedDesktopPref) {
            int expandedDesktopValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_STYLE, expandedDesktopValue);
            updateExpandedDesktopSummary(expandedDesktopValue);
            return true;
        }
        return false;
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
         } else if (preference == mShowRebootKeyguard) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    private void updateExpandedDesktopSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            /* expanded desktop deactivated */
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
            mExpandedDesktopPref.setSummary(res.getString(R.string.expanded_desktop_disabled));
        } else if (value == 1) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            String statusBarPresent = res.getString(R.string.expanded_desktop_summary_status_bar);
            mExpandedDesktopPref.setSummary(res.getString(R.string.summary_expanded_desktop, statusBarPresent));
        } else if (value == 2) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            String statusBarPresent = res.getString(R.string.expanded_desktop_summary_no_status_bar);
            mExpandedDesktopPref.setSummary(res.getString(R.string.summary_expanded_desktop, statusBarPresent));
        }
    }
}
