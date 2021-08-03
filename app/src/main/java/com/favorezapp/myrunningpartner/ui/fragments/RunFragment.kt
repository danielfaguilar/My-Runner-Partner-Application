package com.favorezapp.myrunningpartner.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsChecker
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsCheckerImpl
import com.favorezapp.myrunningpartner.ui.adapters.RunListAdapter
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import com.favorezapp.myrunningpartner.util.RunSortType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

const val LOCATION_PERMISSIONS_REQUEST_CODE = 1;

const val INDEX_OF_DATE = 0
const val INDEX_OF_DISTANCE = 2
const val INDEX_OF_DURATION = 1
const val INDEX_OF_AVG_SPEED = 3
const val INDEX_OF_CALORIES_BURNED = 4

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks{
    // ViewModel provided with Hilt by HiltViewModel
    private val mMainViewModel: MainViewModel by viewModels()

    // Views and view helpers
    lateinit var mRecyclerRuns: RecyclerView
    lateinit var mRunAdapter: RunListAdapter
    lateinit var mSpinnerFilter: Spinner

    // Permissions
    lateinit var mLocationPermissionChecker: GeoLocationPermissionsChecker

    // Listener for the filter spinner
    private val mSpinnerOnItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            when( position ) {
                INDEX_OF_DATE -> mMainViewModel.sortRuns(RunSortType.DATE)
                INDEX_OF_DURATION -> mMainViewModel.sortRuns(RunSortType.DURATION)
                INDEX_OF_DISTANCE -> mMainViewModel.sortRuns(RunSortType.DISTANCE)
                INDEX_OF_AVG_SPEED -> mMainViewModel.sortRuns(RunSortType.AVG_SPEED)
                INDEX_OF_CALORIES_BURNED -> mMainViewModel.sortRuns(RunSortType.CALORIES_BURNED)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }


    /**
     * Initialize the location permission checker with the host component context
     * @param context of the host Activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLocationPermissionChecker =
            GeoLocationPermissionsCheckerImpl(context)
    }

    /**
     * Set listener for the fab button and navigate to another fragment using
     * the nav controller
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        root?.apply {
            findViewById<FloatingActionButton>(R.id.fab_add_run).setOnClickListener {
                findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
            }

            mRecyclerRuns = findViewById(R.id.recycler_view_runs)!!
            mSpinnerFilter.onItemSelectedListener = mSpinnerOnItemSelectedListener
        }

        return root!!
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermissions()
        setupRecyclerView()

        when( mMainViewModel.sortType ) {
            RunSortType.DATE -> mSpinnerFilter.setSelection(INDEX_OF_DATE)
            RunSortType.DURATION -> mSpinnerFilter.setSelection(INDEX_OF_DURATION)
            RunSortType.DISTANCE -> mSpinnerFilter.setSelection(INDEX_OF_DISTANCE)
            RunSortType.AVG_SPEED -> mSpinnerFilter.setSelection(INDEX_OF_AVG_SPEED)
            RunSortType.CALORIES_BURNED -> mSpinnerFilter.setSelection(INDEX_OF_CALORIES_BURNED)
        }

        mMainViewModel.runsMediator.observe(viewLifecycleOwner) {
            mRunAdapter.submitList(it)
        }
    }
    /*
    * Setup the recyclerview
    * */
    private fun setupRecyclerView() = mRecyclerRuns.apply {
        mRunAdapter = RunListAdapter()
        adapter = mRunAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Delegate the result provided by the android framework to Easy Permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * If permissions are not given check if are permanently denied, if so use the easy permissions dialog
     * to navigate to settings of the app
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if( EasyPermissions.somePermissionPermanentlyDenied(this, perms) )
            AppSettingsDialog.Builder(this).build().show()
        else
            requestLocationPermissions()
    }

    // If permissions are given everything is fine so do nothing
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    /**
     * Request the permissions if needed based on the Build Version
     */
    private fun requestLocationPermissions() {
        if( mLocationPermissionChecker.arePermissionsGiven )
            return

        val rationaleMessage = requireContext()
            .getString(R.string.rationale_request_location_permissions)

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q )
            EasyPermissions.requestPermissions(
                this,
                rationaleMessage,
                LOCATION_PERMISSIONS_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        else
            EasyPermissions.requestPermissions(
                this,
                rationaleMessage,
                LOCATION_PERMISSIONS_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
    }
}
