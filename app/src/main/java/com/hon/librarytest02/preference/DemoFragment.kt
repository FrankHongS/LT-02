package com.hon.librarytest02.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.hon.librarytest02.R
import com.hon.mylogger.MyLogger

/**
 * Created by Frank Hon on 2022/9/19 7:18 下午.
 * E-mail: frank_hon@foxmail.com
 */
class DemoFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        MyLogger.d("onCreate: $this")
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        MyLogger.d("onCreatePreferences: ")
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        initPreferences()
    }

    private fun initPreferences() {
        val preference = findPreference<Preference>("preference")
        val stylized = findPreference<Preference>("stylized")
        preference?.setOnPreferenceClickListener {
            preferenceScreen.run {
                val result = removePreference(stylized!!)
                if (!result) {
                    addPreference(stylized)
                }
            }
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MyLogger.d("onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        MyLogger.d("onDestroyView: ")
        super.onDestroyView()
    }

    override fun onDestroy() {
        MyLogger.d("onDestroy: ")
        super.onDestroy()
    }

}