package com.example.todothree.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todothree.databinding.EachtodoBinding

class todoAdapter(private val list:MutableList<todoData>):RecyclerView.Adapter<todoAdapter.todoViewholder>() {

    private var listener:TodoAdapterClicksInterface?=null
    fun setListener(listener:TodoAdapterClicksInterface){
        this.listener = listener
    }
    inner class todoViewholder(val binding:EachtodoBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoViewholder {
        val binding=EachtodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return todoViewholder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: todoViewholder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text= this.task
                binding.dltBtn.setOnClickListener{
                    listener?.onDltBtnClicked(this)
                }
                binding.edtBtn.setOnClickListener{
                    listener?.onEdtBtnClicked(this)
                }
            }
        }
    }

    interface TodoAdapterClicksInterface{
        fun onDltBtnClicked(todoData: todoData)
        fun onEdtBtnClicked(todoData: todoData)


    }
}