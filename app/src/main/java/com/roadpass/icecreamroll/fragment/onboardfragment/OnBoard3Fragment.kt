package com.roadpass.icecreamroll.fragment.onboardfragment


import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.roadpass.avid.util.postOpenLauncherPixel
import com.roadpass.avid.util.postPopUpLauncherPixel
import com.roadpass.icecreamroll.AppObject
import com.roadpass.icecreamroll.R
import com.roadpass.icecreamroll.activity.FakeLauncherActivity
import com.roadpass.icecreamroll.activity.HomeActivity


class OnBoard3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.on_board_fragment3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val v = view.findViewById<TextView>(R.idtext_view)
//        val noButton = view.findViewById<Button>(R.id.button)
        val makeDefaultButton = view.findViewById<Button>(R.id.button2);
        val terms = view.findViewById<TextView>(R.id.TnC)

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            trackOpen()
        }

        makeDefaultButton.setOnClickListener {
            val analytics: FirebaseAnalytics? = null
            val params = Bundle()
            params.putString("default_button_click", "onboarding_screen")
            analytics?.logEvent("default_button_click", params)
            trackPopup()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showLauncherSelection()
            } else {
                resetPreferredLauncherAndOpenChooser(requireContext());
            }

        }
//        noButton.setOnClickListener {
//            setState();
//        }
    }

    private fun guid(): String? {
        return getDefaultSharedPreferences(context).getString("uuid","error")
    }

    private fun trackOpen() {
        postOpenLauncherPixel("IceCreamRoll", "idfv", guid())
    }

    private fun trackPopup() {
        postPopUpLauncherPixel("IceCreamRoll", "idfv", guid())
    }

    private fun setState() {

        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        (requireContext().applicationContext as AppObject).showAd()
    }

    private fun resetPreferredLauncherAndOpenChooser(context: Context) {
        Log.d("[defaultCheck123]", "show selector2")
        val packageManager: PackageManager = context.packageManager
        val componentName =
            ComponentName(context, FakeLauncherActivity::class.java)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        selector.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(selector)
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
            PackageManager.DONT_KILL_APP
        )
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            print(activityResult.data)
            print("launcher")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showLauncherSelection() {
        Log.d("[defaultCheck123]", "show selector")
        val roleManager = requireActivity().getSystemService(Context.ROLE_SERVICE)
                as RoleManager
        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
            startForResult.launch(intent)
        }
    }


}