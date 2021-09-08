package app.ticktasker.task.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import app.ticktasker.databinding.ItemDropLocationBinding
import app.ticktasker.databinding.ItemTaskAndLocationBinding
import app.ticktasker.networking.model.request.TaskDetailJsonTaskRequest
import app.ticktasker.task.model.Job
import app.ticktasker.task.model.Task

abstract class BaseTaskDialogAdapter(
    val job: Job,
    var taskDetailsList: ArrayList<TaskDetailJsonTaskRequest>? = null
) : RecyclerView.Adapter<BaseTaskDialogAdapter.ViewHolder>() {
    val PICK_LOCATION_ITEM = 0
    val DROP_LOCATION_ITEM = 1

    protected var doWhenTaskDetailsClicked: (position: Int) -> Unit = {}
    protected var doWhenPickOrDropLocationClicked: (Boolean, Int) -> Unit = { fromPickLocation, taskPostion -> }
    protected var doWhenPlusClicked: (Int) -> Unit = {}
    protected var doWhenRemoveClicked: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = if (viewType == PICK_LOCATION_ITEM) ItemTaskAndLocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        else ItemDropLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = job.tasks.size + 1

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        if (position == itemCount - 1) {
            return DROP_LOCATION_ITEM
        }
        return PICK_LOCATION_ITEM
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindPickLocation(task: Task, taskDetails: TaskDetailJsonTaskRequest?) {
            binding as ItemTaskAndLocationBinding
            binding.pickupLocationEdittext.setText(task.pickLocation.formatted_address)
            binding.dropLocationIconImageview

            // if (!task.pickJsonLocation?.pickupLocation.isNullOrBlank()) {
            if (!taskDetails?.taskDescription.isNullOrBlank()) {
                binding.taskDetailsChip.visibility = View.VISIBLE
                binding.plusIcon.visibility = View.GONE
            }
            if (layoutPosition == 0) {
                binding.removeTaskImageview.visibility = View.GONE
            } else {
                binding.removeTaskImageview.setOnClickListener {
                    Log.d("TAGls1", "bindPickLocation: ${job.tasks.size}")
                    job.tasks.removeAt(adapterPosition)
                    Log.d("TAGls2", "bindPickLocation: ${job.tasks.size}")
                    doWhenRemoveClicked(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }

        fun bindDropLocation() {
            binding as ItemDropLocationBinding
            binding.dropLocationEdittext.setText(job.dropLocation.formatted_address)
        }
    }

    fun addTask() {
        addOneMoreTask()
        notifyItemInserted(itemCount - 1)
    }

    protected abstract fun addOneMoreTask()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != itemCount - 1) {
//                job.tasks.forEach {
//                    println(it.description + "description")
//                }
            holder.bindPickLocation(job.tasks[position], taskDetailsList?.get(position))//lList[position] )
        } else {
            holder.bindDropLocation()
        }
    }

    fun doWhenTaskDetailsClicked(action: (position: Int) -> Unit) {
        doWhenTaskDetailsClicked = action
    }

    fun doWhenPickOrDropLocationClicked(action: (fromPickLocation: Boolean, taskPosition: Int) -> Unit) {
        doWhenPickOrDropLocationClicked = action
    }

    fun doWhenPlusClicked(action: (taskPosition: Int) -> Unit) {
        doWhenPlusClicked = action
    }

    fun doWhenRemoveClicked(action: (taskPosition: Int) -> Unit) {
        doWhenRemoveClicked = action
    }
}