
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

public class UserInterface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "UserInterface";

    private static final String PREF_180 = "rotate_180";
    private static final String PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String PREF_USE_ALT_RESOLVER = "use_alt_resolver";
    private static final String PREF_VIBRATE_NOTIF_EXPAND = "vibrate_notif_expand";
    private static final String PREF_RECENT_KILL_ALL = "recent_kill_all";
    private static final String PREF_RAM_USAGE_BAR = "ram_usage_bar";
    private static final String PREF_ENABLE_VOLUME_OPTIONS = "enable_volume_options";
    private static final String PREF_IME_SWITCHER = "ime_switcher";
    private static final String PREF_ALARM_ENABLE = "alarm";
    private static final String PREF_SHOW_OVERFLOW = "show_overflow";
    private static final String PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED = "wakeup_when_plugged_unplugged";
    private static final String PREF_POWER_CRT_SCREEN_ON = "system_power_crt_screen_on";
    private static final String PREF_POWER_CRT_SCREEN_OFF = "system_power_crt_screen_off"; 
    private static final String PREF_LOW_BATTERY_WARNING_POLICY = "pref_low_battery_warning_policy";
    private static final String PREF_FULLSCREEN_KEYBOARD = "fullscreen_keyboard";
    private static final String KEYBOARD_ROTATION_TOGGLE = "keyboard_rotation_toggle";
    private static final String KEYBOARD_ROTATION_TIMEOUT = "keyboard_rotation_timeout";
    private static final String SHOW_ENTER_KEY = "show_enter_key";

    CheckBoxPreference mAllow180Rotation;
    CheckBoxPreference mStatusBarNotifCount;
    Preference mCustomLabel;
    CheckBoxPreference mUseAltResolver;
    CheckBoxPreference mVibrateOnExpand;
    CheckBoxPreference mRecentKillAll;
    CheckBoxPreference mRamBar;
    SharedPreferences prefs;
    CheckBoxPreference mEnableVolumeOptions;
    CheckBoxPreference mDisableBootAnimation;
    CheckBoxPreference mShowImeSwitcher;
    CheckBoxPreference mAlarm;
    CheckBoxPreference mShowActionOverflow;
    CheckBoxPreference mWakeUpWhenPluggedOrUnplugged;
    CheckBoxPreference mCrtOff;
    CheckBoxPreference mCrtOn;
    ListPreference mLowBatteryWarning;
    CheckBoxPreference mFullscreenKeyboard;
    CheckBoxPreference mKeyboardRotationToggle;
    ListPreference mKeyboardRotationTimeout;
    CheckBoxPreference mShowEnterKey;
    

    private static final int KEYBOARD_ROTATION_TIMEOUT_DEFAULT = 5000; // 5s

    Random randomGenerator = new Random();

    String mCustomLabelText = null;

    int newDensityValue;

    DensityChanger densityFragment;

    private boolean isCrtOffChecked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);

        PreferenceScreen prefs = getPreferenceScreen();
        ContentResolver cr = mContext.getContentResolver();

        mLowBatteryWarning = (ListPreference) findPreference(PREF_LOW_BATTERY_WARNING_POLICY);
        int lowBatteryWarning = Settings.System.getInt(getActivity().getContentResolver(),
                                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 3);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());
        mLowBatteryWarning.setOnPreferenceChangeListener(this);


        mEnableVolumeOptions = (CheckBoxPreference) findPreference(PREF_ENABLE_VOLUME_OPTIONS);
        mEnableVolumeOptions.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(),
                Settings.System.ENABLE_VOLUME_OPTIONS, false));

        mAllow180Rotation = (CheckBoxPreference) findPreference(PREF_180);
        mAllow180Rotation.setChecked(Settings.System.getInt(mContext
                 .getContentResolver(), Settings.System.ACCELEROMETER_ROTATION_ANGLES, (1 | 2 | 8)) == (1 | 2 | 4 | 8));

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(mContext
                .getContentResolver(), Settings.System.STATUSBAR_NOTIF_COUNT,
                false));

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mUseAltResolver = (CheckBoxPreference) findPreference(PREF_USE_ALT_RESOLVER);
        mUseAltResolver.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.ACTIVITY_RESOLVER_USE_ALT, false));

        mShowImeSwitcher = (CheckBoxPreference) findPreference(PREF_IME_SWITCHER);
        mShowImeSwitcher.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                         Settings.System.SHOW_STATUSBAR_IME_SWITCHER, true));

        mAlarm = (CheckBoxPreference) findPreference(PREF_ALARM_ENABLE);
        mAlarm.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                         Settings.System.STATUSBAR_SHOW_ALARM, 1) == 1);
         
        mVibrateOnExpand = (CheckBoxPreference) findPreference(PREF_VIBRATE_NOTIF_EXPAND);
        mVibrateOnExpand.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                Settings.System.VIBRATE_NOTIF_EXPAND, true));

        mFullscreenKeyboard = (CheckBoxPreference) findPreference(PREF_FULLSCREEN_KEYBOARD);
        mFullscreenKeyboard.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FULLSCREEN_KEYBOARD, 0) == 1);

        mShowEnterKey = (CheckBoxPreference) findPreference(SHOW_ENTER_KEY);
        mShowEnterKey.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, 0) == 1);

        mKeyboardRotationToggle = (CheckBoxPreference) findPreference(KEYBOARD_ROTATION_TOGGLE);
        mKeyboardRotationToggle.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.KEYBOARD_ROTATION_TIMEOUT, 0) > 0);

        mKeyboardRotationTimeout = (ListPreference) findPreference(KEYBOARD_ROTATION_TIMEOUT);
        mKeyboardRotationTimeout.setOnPreferenceChangeListener(this);
        updateRotationTimeout(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.KEYBOARD_ROTATION_TIMEOUT, KEYBOARD_ROTATION_TIMEOUT_DEFAULT));
        
        mRecentKillAll = (CheckBoxPreference) findPreference(PREF_RECENT_KILL_ALL);
        mRecentKillAll.setChecked(Settings.System.getBoolean(getActivity  ().getContentResolver(),
                Settings.System.RECENT_KILL_ALL_BUTTON, false));

        mRamBar = (CheckBoxPreference) findPreference(PREF_RAM_USAGE_BAR);
        mRamBar.setChecked(Settings.System.getBoolean(getActivity  ().getContentResolver(),
                Settings.System.RAM_USAGE_BAR, false));

        mShowActionOverflow = (CheckBoxPreference) findPreference(PREF_SHOW_OVERFLOW);
        mShowActionOverflow.setChecked(Settings.System.getBoolean(getActivity().
                        getApplicationContext().getContentResolver(),
                        Settings.System.UI_FORCE_OVERFLOW_BUTTON, false));

        mWakeUpWhenPluggedOrUnplugged = (CheckBoxPreference) findPreference(PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED);
        mWakeUpWhenPluggedOrUnplugged.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED, true));

       // respect device default configuration
        // true fades while false animates
        boolean electronBeamFadesConfig = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_animateScreenLights);

        // use this to enable/disable crt on feature
        // crt only works if crt off is enabled
        // total system failure if only crt on is enabled
        isCrtOffChecked = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF,
                electronBeamFadesConfig ? 0 : 1) == 1;

        mCrtOff = (CheckBoxPreference) findPreference(PREF_POWER_CRT_SCREEN_OFF);
        mCrtOff.setChecked(isCrtOffChecked);
        mCrtOff.setOnPreferenceChangeListener(this);

        mCrtOn = (CheckBoxPreference) findPreference(PREF_POWER_CRT_SCREEN_ON);
        mCrtOn.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SYSTEM_POWER_ENABLE_CRT_ON, 0) == 1);
        mCrtOn.setEnabled(isCrtOffChecked);
        mCrtOn.setOnPreferenceChangeListener(this);
	
         
        mDisableBootAnimation = (CheckBoxPreference) findPreference("disable_bootanimation");
        mDisableBootAnimation.setChecked(!new File("/system/media/bootanimation.zip").exists());
                 if (mDisableBootAnimation.isChecked()) {
                     Resources res = mContext.getResources();
                     String[] insults = res.getStringArray(R.array.disable_bootanimation_insults);
                     int randomInt = randomGenerator.nextInt(insults.length);
                     mDisableBootAnimation.setSummary(insults[randomInt]);
                 }
    }

     public void updateRotationTimeout(int timeout) {
        if (timeout == 0)
            timeout = KEYBOARD_ROTATION_TIMEOUT_DEFAULT;
        mKeyboardRotationTimeout.setValue(Integer.toString(timeout));
        mKeyboardRotationTimeout.setSummary(getString(R.string.keyboard_rotation_timeout_summary, mKeyboardRotationTimeout.getEntry()));
    }

     @Override
    public void onPause() {
        super.onPause();
    }

    public void mKeyboardRotationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.keyboard_rotation_dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(com.android.internal.R.string.ok), null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
       if (preference == mEnableVolumeOptions) {

           boolean checked = ((CheckBoxPreference) preference).isChecked();
           Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.ENABLE_VOLUME_OPTIONS, checked);
           return true;
       } else if (preference == mAllow180Rotation) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES, checked ? (1 | 2 | 4 | 8) : (1 | 2 | 8 ));
            return true;
        } else if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mShowImeSwitcher) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.SHOW_STATUSBAR_IME_SWITCHER,
                    isCheckBoxPreferenceChecked(preference));
            return true;
        } else if (preference == mShowEnterKey) {
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.FORMAL_TEXT_INPUT, mShowEnterKey.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mFullscreenKeyboard) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.FULLSCREEN_KEYBOARD,
                    mFullscreenKeyboard.isChecked() ? 1 : 0);
        } else if (preference == mAlarm) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                     Settings.System.STATUSBAR_SHOW_ALARM, checked ? 1 : 0);
        } else if (preference == mKeyboardRotationToggle) {
            boolean isAutoRotate = (Settings.System.getInt(getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
            if (isAutoRotate && mKeyboardRotationToggle.isChecked())
                mKeyboardRotationDialog();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYBOARD_ROTATION_TIMEOUT,
                    mKeyboardRotationToggle.isChecked() ? KEYBOARD_ROTATION_TIMEOUT_DEFAULT : 0);
            updateRotationTimeout(KEYBOARD_ROTATION_TIMEOUT_DEFAULT);
        } else if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);

            alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                    Intent i = new Intent();
                    i.setAction("com.sourcery.magiccontrol.LABEL_CHANGED");
                    mContext.sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        } else if (preference == mUseAltResolver) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.ACTIVITY_RESOLVER_USE_ALT,
                    isCheckBoxPreferenceChecked(preference));
            return true;
        } else if (preference == mVibrateOnExpand) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.VIBRATE_NOTIF_EXPAND,
                    ((CheckBoxPreference) preference).isChecked());
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRecentKillAll) {
            boolean checked = ((CheckBoxPreference)preference).isChecked();
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.RECENT_KILL_ALL_BUTTON, checked ? true : false);
            return true;
        } else if (preference == mRamBar) {
            boolean checked = ((CheckBoxPreference)preference).isChecked();
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.RAM_USAGE_BAR, checked ? true : false);
            return true;
          } else if (preference == mShowActionOverflow) {
            boolean enabled = mShowActionOverflow.isChecked();
            Settings.System.putBoolean(getContentResolver(), Settings.System.UI_FORCE_OVERFLOW_BUTTON,
                    enabled ? true : false);
            // Show toast appropriately
            if (enabled) {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_enable,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_disable,
                        Toast.LENGTH_LONG).show();
            }
            return true;
          } else if (preference == mWakeUpWhenPluggedOrUnplugged) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
         } else if (preference == mDisableBootAnimation) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            if (checked) {
                Helpers.getMount("rw");
                CMDProcessor.runSuCommand("mv /system/media/bootanimation.zip /system/media/bootanimation.sourcery");
                Helpers.getMount("ro");
                Resources res = mContext.getResources();
                String[] insults = res.getStringArray(R.array.disable_bootanimation_insults);
                int randomInt = randomGenerator.nextInt(insults.length);
                preference.setSummary(insults[randomInt]);
            } else {
                Helpers.getMount("rw");
                CMDProcessor.runSuCommand("mv /system/media/bootanimation.sourcery /system/media/bootanimation.zip");
                Helpers.getMount("ro");
                preference.setSummary("");
            }
            return true;
            }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

     @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
         if (mCrtOff.equals(preference)) {
            isCrtOffChecked = ((Boolean) newValue).booleanValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF,
                    (isCrtOffChecked ? 1 : 0));
            // if crt off gets turned off, crt on gets turned off and disabled
            if (!isCrtOffChecked) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SYSTEM_POWER_ENABLE_CRT_ON, 0);
                mCrtOn.setChecked(false);
            }
            mCrtOn.setEnabled(isCrtOffChecked);
            return true;
        } else if (mCrtOn.equals(preference)) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SYSTEM_POWER_ENABLE_CRT_ON,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) newValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        } else if (preference == mKeyboardRotationTimeout) {
            int timeout = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEYBOARD_ROTATION_TIMEOUT, timeout);
            updateRotationTimeout(timeout);
            return true;
        }
        return false;
    }
}
