package app.ticktasker.task.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import app.ticktasker.BaseDialogFragment
import app.ticktasker.R

import app.ticktasker.databinding.FragmentAddTaskBinding
import app.ticktasker.networking.model.request.TaskDetailJsonTaskRequest
import app.ticktasker.task.dialogs.AddPhoneNumberDialog
import app.ticktasker.task.dialogs.AddPictureDialog
import app.ticktasker.task.model.GeneralTask
import app.ticktasker.viewmodels.GeneralTaskViewModel
import app.ticktasker.utils.applyInvalidAttributes
import app.ticktasker.utils.applyValidAttributes
import app.ticktasker.viewmodels.LoginViewModel
import com.abhinav.chouhan.validationnotifieredittext.ValidationNotifierEditText

class AddTaskFragment : BaseDialogFragment(R.layout.fragment_add_task) {

    lateinit var binding: FragmentAddTaskBinding
    val viewModel: GeneralTaskViewModel by navGraphViewModels(R.id.new_general_task_nav_graph)
    private val args: AddTaskFragmentArgs by navArgs()
    val vmLogin: LoginViewModel by activityViewModels()
    lateinit var task: GeneralTask
    private var position: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddTaskBinding.bind(view)
        setupValidation()
        binding.fragment = this
        task = viewModel.getTask(args.taskPosition) as GeneralTask
        task.category = args.name
        task.categoryDrawableRes = args.drawableres
        binding.task = task
        position = args.taskPosition

        if (vmLogin.isUpdateTaskClicked) {
            // Plus icon click case
            val task = vmLogin.taskDetailsList[args.taskPosition]
            Log.d("AddTaskFragment", "onViewCreated: $task ${vmLogin.taskDetailsList}  ${args.taskPosition}")
            binding.itemTypeChip.text = task.category
            binding.taskDetailsEditText.setText(task.taskDescription)
            binding.addPhoneNumberChip.text = task.mobileNumber
        }
    }

    fun setupValidation() {
        binding.taskDetailsEditText.addValidationChangeListener(object :
            ValidationNotifierEditText.ValidationChangeListener {
            override fun onBecomeInvalid(validationNotifierEditText: ValidationNotifierEditText) {
                applyInvalidAttributes(binding.saveTaskDetailsButton)
            }

            override fun onBecomeValid(validationNotifierEditText: ValidationNotifierEditText) {
                applyValidAttributes(binding.saveTaskDetailsButton)
            }
        })
    }

    fun createAddShowPhotosDialog() {
        val addPictureDialog = AddPictureDialog(requireActivity())
        val addButton =
            LayoutInflater.from(requireActivity()).inflate(R.layout.item_add_round, null)
        binding.images.addView(addButton)
        val addDummyPicAction = { // remove later
            val image =
                LayoutInflater.from(requireActivity()).inflate(R.layout.item_task_image, null)
            binding.images.addView(image, 0)
        }
        addPictureDialog.doOnGalleryClick = addDummyPicAction // remove later
        addPictureDialog.doOnCameraClick = addDummyPicAction
        addPictureDialog.show()
    }

    fun createAndShowPhoneNumberDialog() {
        val addPhoneNumberDialog = AddPhoneNumberDialog(requireActivity())
        addPhoneNumberDialog.doOnAddContactClick = {
            binding.addPhoneNumberChip.text = it
            addPhoneNumberDialog.dismiss()
        }
        addPhoneNumberDialog.show()
    }

    fun saveTaskDetails() {
        if (
            !task.pickLocation.formatted_address.isNullOrBlank() &&
            !viewModel.job.dropLocation.formatted_address.isNullOrBlank()
        ) {
            viewModel.addedDetailsForOneTaskMinimum.value = true
        }

        val taskDetails = TaskDetailJsonTaskRequest(
            args.name,
            binding.taskDetailsEditText.text.toString(),
            binding.addPhoneNumberChip.text.toString(),
            null
        )

        if (!vmLogin.isUpdateTaskClicked) {
            // Plus clicked
//            val map = hashMapOf(
//                "position" to position,
//                "task_detail_value" to taskDetails,
//                "isPlusClicked" to true
//            )
//            vmLogin.taskDetailMap = map
            vmLogin.taskDetailsList.add(position, taskDetails)
        } else {
            // Task Detail clicked
//            vmLogin.taskDetailMap = null

            vmLogin.taskDetailsList.set(position, taskDetails)
        }


//        viewModel.job.taskDetailsLists.add(args.taskPosition, o)

        findNavController().navigateUp()
    }
}