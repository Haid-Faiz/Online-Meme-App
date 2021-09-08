package app.ticktasker.main.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.ticktasker.BaseDialogFragment
import app.ticktasker.MainActivity
import app.ticktasker.MainActivity.Companion.navController
import app.ticktasker.R
import app.ticktasker.databinding.DialogPickLocationBinding
import app.ticktasker.networking.model.request.TaskDetailJsonLocationRequest
import app.ticktasker.networking.model.request.TaskDetailJsonTaskRequest
import app.ticktasker.task.adapters.BaseTaskDialogAdapter
import app.ticktasker.task.adapters.GeneralTaskRecyclerAdapter
import app.ticktasker.task.model.GeneralTask
import app.ticktasker.task.view.PickupAndDropTaskFragmentDirections
import app.ticktasker.utils.applyInvalidAttributes
import app.ticktasker.utils.applyValidAttributes
import app.ticktasker.viewmodels.GeneralTaskViewModel
import app.ticktasker.viewmodels.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility")
class PickLocationDialog : BaseDialogFragment(R.layout.dialog_pick_location) {
    val viewModel: GeneralTaskViewModel by navGraphViewModels(R.id.new_general_task_nav_graph)
    var doOnViewDetailsClick: () -> Unit = {}
    var doOnCancelClick: () -> Unit = {}
    var pos: Int = 0
    var mLocation: String? = null
    var mLat: String? = null
    var mLang: String? = null
    var taskPos: Int = 0
    private var taskDetailPosition: Int = 0
    lateinit var binding: DialogPickLocationBinding

    var list: ArrayList<TaskDetailJsonLocationRequest>? = null
//    var taskDetailsList: ArrayList<TaskDetailJsonTaskRequest> = ArrayList()
    private var dropLocation: String? = null
    private var dropLat: String? = null
    private var dropLang: String? = null
    private val vmLogin: LoginViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val bottom_dialog = dialog as BottomSheetDialog
            val bottomSheet =
                (bottom_dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)!!

            val behaviour = BottomSheetBehavior.from(bottomSheet)
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            behaviour.skipCollapsed = true
            behaviour.isHideable = false

//            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
//            dialog.window?.attributes?.gravity = Gravity.BOTTOM

            val displayMetrics = requireActivity().resources.displayMetrics
            val height = displayMetrics.heightPixels
            val maxHeight = (height / 15).toInt()
            BottomSheetBehavior.from(bottomSheet).peekHeight = maxHeight // 136
            dialog.window?.setDimAmount(0.0f)
            Log.d("TAGbot", "onCreateDialog: ${height}//${maxHeight}//${displayMetrics}")
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        taskDetailsList.add(TaskDetailJsonTaskRequest(null, null , null, null))

        if(!vmLogin.isFirstTimeOpened) {
            vmLogin.isFirstTimeOpened = true
            vmLogin.taskDetailsList.add(TaskDetailJsonTaskRequest(null, null , null, null))
        }


        Log.d("TAGcycle", "onViewCreated: cycle")
        binding = DialogPickLocationBinding.bind(view)
        binding.dialog = this
        isCancelable = false

        //  PickLocationDialog()
        viewModel.job.dropLocation.formatted_address?.let {
            // here we will get DROP-LOCATION if it was initialised
            this.dropLocation = it
            vmLogin.dropLocation = it
        }

        viewModel.job.dropLat?.let {
            // here we will get DROP-LAT if it was initialised
            this.dropLat = it
            vmLogin.dropLat = it
        }

        viewModel.job.dropLang?.let {
            // here we will get DROP-LANG if it was initialised
            this.dropLang = it
            vmLogin.dropLang = it
        }

        Log.d("TAGposi2", "setupRecyclerView: $pos")
        setupRecyclerView()

        parentFragmentManager.setFragmentResultListener(
            "location_info",
            viewLifecycleOwner
        ) { key, bundle ->
            list = bundle.getParcelableArrayList<TaskDetailJsonLocationRequest>("list")
            taskPos = bundle.getInt("mPosition")
            pos = taskPos
            //   val mPos = bundle.getInt("mPosition")
            Log.d("TAGls6", "bindPickLocation: ${list?.size}")
            list?.let {
                setupRecyclerView()
                /** Pick Loc */
                if (pos != -1) {
                    viewModel.job.tasks[pos].pickLocation.formatted_address =
                        list?.get(pos)?.pickupLocation.toString()
                    viewModel.job.tasks[pos].pickLat = list?.get(pos)?.lat.toString()
                    viewModel.job.tasks[pos].pickLang = list?.get(pos)?.lang.toString()

                    Log.d(
                        "TAG81",
                        "pickup andr: ${viewModel.job.tasks[pos].pickLocation.formatted_address}//${
                            list?.get(pos)?.lat
                        }"
                    )

                    // sending data to pick and drop frag for pickup marker
                    parentFragmentManager.setFragmentResult(
                        "pick&drop_task_frag",
                        bundleOf(
                            "lat" to list?.get(pos)?.lat.toString(),
                            "lang" to list?.get(pos)?.lang.toString(),
                            "pickLoc" to list?.get(pos)?.pickupLocation.toString(),
                            "pickLocList" to list,
                            "pos" to pos
                        )
                    )
                }
            }
        }
//        parentFragmentManager.setFragmentResult()

        ensureJobComplete()
    }

    override fun onResume() {
        super.onResume()

//        vmLogin.taskDetailMap?.let {
//            taskDetailsList.add(
//                it["position"] as Int,
//                it["task_detail_value"] as TaskDetailJsonTaskRequest
//            )
//        } ?: run {
//            vmLogin.taskDetailsList?.let {
//                taskDetailsList = it
//            }
//        }
//
        setupRecyclerView()




        Log.d("TAGposi3", "setupRecyclerView: $pos")
        viewModel.job.dropLocation.formatted_address?.let {
            // here we will get drop location if it was initialised
            this.dropLocation = it
            // viewModel.job.dropLocation.formatted_address = it
            vmLogin.dropLocation = it
        }
        vmLogin.list?.let {
            list = it
            setupRecyclerView()
        }
        /** Drop Loc *"pos = -1" */
        if (pos == -1) {
            parentFragmentManager.setFragmentResult(
                "pick&drop_task_Drop",
                bundleOf(
                    "dropLoc" to dropLocation,
                    "dropLat" to dropLat,
                    "dropLang" to dropLang,
                    "pos-1" to pos
                )
            )
            Log.d(
                "TAGcl",
                "onViewCreated: ${viewModel.job.dropLocation.formatted_address}//${viewModel.job.dropLat}//${viewModel.job.dropLang}"
            )
            Log.d("TAG83b", "drop baher: $dropLocation// $dropLat // $dropLang")
        }
    }

    private fun ensureJobComplete() {
        if (viewModel.job.hasCompleteTask()) {
            binding.viewTaskDetailsButton.visibility = View.VISIBLE
            applyValidAttributes(binding.confirmButton)
        } else {
            applyInvalidAttributes(binding.confirmButton)
        }
    }

    private fun setupRecyclerView() {
        binding.tasksRecyclerview.apply {
            // Data.taskPosition, Data.tryList
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = GeneralTaskRecyclerAdapter(viewModel.job, list, vmLogin.taskDetailsList).apply {
                Log.d("TAGls4", "bindPickLocation: ${list?.size}")

                dropLocation?.let {
                    setDropLocation(it)
                }

                // + Plus click
                doWhenPlusClicked { taskPosition ->

                    vmLogin.isUpdateTaskClicked = false

                    val action =
                        PickLocationDialogDirections.actionPickLocationDialogToTaskCategoriesDialog(
                            taskPosition
                        )

                    navController.navigate(action)
                }

                doWhenRemoveClicked { removedPosition ->
                    Log.d("TAGls6", "bindPickLocation: ${list?.size}")
                    list?.removeAt(removedPosition)
                    notifyDataSetChanged()
                    notifyItemRemoved(removedPosition)
                    onResume()
                }

                // Search Location Click
                doWhenPickOrDropLocationClicked { fromPickLocation, taskPosition ->
                    val action =
                        PickLocationDialogDirections.actionPickLocationDialogToSearchLocationFragment(
                            fromPickLocation,
                            taskPosition
                        )
//                    this@PickLocationDialog.pos = taskPosition
                    pos = taskPosition
                    Log.d("TAGposi", "setupRecyclerView: ${pos}//${taskPosition}")

                    if (taskPosition == -1) {
                        // this is drop location click callback
                    }

                    vmLogin.list = null

                    if (list == null) {
                        list = ArrayList()
//                        for (i in 0 until taskPosition) {
//                            list!!.add(
//                                TaskDetailJsonLocationRequest(
//                                    pickupLocation = "Please select pickup location",
//                                    null, null
//                                )
//                            )
//                        }
                    }

                    parentFragmentManager.setFragmentResult(
                        "req_key_1_2",
                        bundleOf(
                            "list_key_1_2" to list,
                            "loc_position" to taskPosition
                        )
                    )

                    MainActivity.navController.navigate(action)
                }

                // Task Details Click
                doWhenTaskDetailsClicked { taskPosition ->
                    Log.d("ClickCheck", "Called: doWhenTaskDetailsClicked")


                    val task = (viewModel.getTask(taskPosition) as GeneralTask)
                    val action =
                        PickLocationDialogDirections.actionPickLocationDialogToAddTaskFragment(
                            task.category!!,
                            task.categoryDrawableRes!!,
                            taskPosition
                        )

                    vmLogin.isUpdateTaskClicked = true

                    navController.navigate(action)
                }
            }
        }
    }

    // When confirm is clicked
    fun showReviewAndAgreeDialog() {
        navController.navigateUp() // go back
        val action =
            PickupAndDropTaskFragmentDirections.actionNewTaskFragmentToReviewAndAgreeDialog()
        navController.navigate(action)
    }

    // When plus icon is clicked
    fun addOneMoreTask() {
        Log.d("ClickCheck", "Called: addOneMoreTask")
        viewModel.addedDetailsForOneTaskMinimum.value = false
        val adapter = binding.tasksRecyclerview.adapter as BaseTaskDialogAdapter
        adapter.addTask()
        ensureJobComplete()

        list?.add(
            TaskDetailJsonLocationRequest(
                pickupLocation = null, null, null
            )
        )
    }

    // when view task details is clicked
    fun showTaskDetailsScreen() {
        val action =
            PickLocationDialogDirections.actionPickLocationDialogToAllTasksDetailsFragment()
        findNavController().navigate(action)
    }

    // when cancel is clicked
    fun goToHomeFragment() {
        findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onStop() {
        super.onStop()
        //  PrefManager.putString("location", "BookTaskerLocation")
        vmLogin.dropLocation = ""
        viewModel.job.tasks.forEach {
            it.pickLocation.formatted_address = null
            it.description = null
        }
    }
}