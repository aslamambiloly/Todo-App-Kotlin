package com.example.todothree.fragments

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todothree.databinding.FragmentPopupBinding
import com.example.todothree.utils.todoData
import com.google.android.material.textfield.TextInputEditText

class popupFragment : DialogFragment() {

    private lateinit var binding: FragmentPopupBinding
    private lateinit var listener: dialogNextBtn
    private var dataTodo: todoData? = null

    fun setListener(listener: dialogNextBtn) {
        this.listener = listener
    }

    companion object {
        const val TAG = "popupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String): popupFragment {
            return popupFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            dataTodo =
                todoData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString())
            binding.todoEt.setText(dataTodo?.task)
        }

        registerEvents()
    }

    private fun registerEvents() {
        binding.nextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()) {
                if (dataTodo == null) {
                    listener.onSaveTask(todoTask, binding.todoEt)
                } else {
                    dataTodo?.task = todoTask
                    listener.onUpdateTask(dataTodo!!, binding.todoEt)
                }
                listener.onSaveTask(todoTask, binding.todoEt)
            } else {
                Toast.makeText(context, "Please add some tasks to continue", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backimg.setOnClickListener {
            dismiss()
        }
    }

    interface dialogNextBtn {
        fun onSaveTask(todo: String, todoEt: TextInputEditText)
        fun onUpdateTask(datatodoData: todoData, todoEt: TextInputEditText)
    }
}
