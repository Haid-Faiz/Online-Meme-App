package app.ticktasker.task.adapters

import android.annotation.SuppressLint
import android.util.Log
import app.ticktasker.databinding.ItemDropLocationBinding
import app.ticktasker.databinding.ItemTaskAndLocationBinding
import app.ticktasker.networking.model.request.TaskDetailJsonLocationRequest
import app.ticktasker.networking.model.request.TaskDetailJsonTaskRequest
import app.ticktasker.task.model.GeneralTask
import app.ticktasker.task.model.Job
import app.ticktasker.utils.doOnMotionEventUp

class GeneralTaskRecyclerAdapter(
//    private var dropLocationListener: ((position: Int) -> Unit)? = null,
    job: Job,
    val lList: ArrayList<TaskDetailJsonLocationRequest>?,
    var taskDetailList: ArrayList<TaskDetailJsonTaskRequest>?
) : BaseTaskDialogAdapter(job, taskDetailList) {

    private var dropLocation: String? = null

    //    val pos: Int
    override fun addOneMoreTask() {
        job.tasks.add(GeneralTask())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder.binding is ItemTaskAndLocationBinding) {
            Log.d("TAGls5", "bindPickLocation: ${lList?.size}")
//            holder.binding.pickupLocationEdittext.setOnTouchListener { view, event ->
//                view.parent.requestDisallowInterceptTouchEvent(true)
//                if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//                    view.parent.requestDisallowInterceptTouchEvent(false)
//                }
//                return@setOnTouchListener false
//            }

            if (lList?.size != 0) {
                lList?.let {
                    holder.binding.pickupLocationEdittext.setText(it[position].pickupLocation)
                } ?: run {
                    //  holder.binding.pickupLocationEdittext.setText("pick up loc")
                    holder.binding.pickupLocationEdittext.hint = "Pickup Location"
                }
            }

            holder.binding.plusIcon.setOnClickListener {
                doWhenPlusClicked.invoke(holder.adapterPosition)
            }

//            holder.binding.addTaskText.text =
//                (job.tasks[position] as GeneralTask).category ?: "Add Task"
            holder.binding.addTaskText.text =
                taskDetailList?.get(position)?.category ?: "Add Task"
            holder.binding.pickupLocationEdittext.hint = "Pickup Location"

            holder.binding.pickupLocationEdittext.doOnMotionEventUp {
                doWhenPickOrDropLocationClicked.invoke(true, holder.adapterPosition)
            }

            holder.binding.taskDetailsChip.setOnClickListener {
                doWhenTaskDetailsClicked.invoke(holder.adapterPosition)
            }

            Log.d("TAGt1", "onBindViewHolder: ${holder.binding.pickupLocationEdittext.text}")

        } else if (holder.binding is ItemDropLocationBinding) {
            holder.binding.dropLocationEdittext.setText(
                dropLocation ?: "" //PrefManager.getString("location")
            )
            holder.binding.dropLocationEdittext.doOnMotionEventUp {
                doWhenPickOrDropLocationClicked.invoke(false, -1)
            }

            Log.d("TAGt2", "onBindViewHolder: ${holder.binding.dropLocationEdittext.text}")
        }
    }

    fun setDropLocation(location: String) {
        dropLocation = location
        Log.d("TAGdr1", "setDropLocation: $dropLocation")
    }
}