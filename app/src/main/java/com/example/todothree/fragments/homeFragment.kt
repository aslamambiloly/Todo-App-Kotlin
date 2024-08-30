package com.example.todothree.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todothree.databinding.FragmentHomeBinding
import com.example.todothree.utils.todoAdapter
import com.example.todothree.utils.todoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class homeFragment : Fragment(), popupFragment.dialogNextBtn,
    todoAdapter.TodoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var frag : popupFragment?=null
    private lateinit var adapter: todoAdapter
    private lateinit var list: MutableList<todoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFirebase()
        registerEvents()
    }

    private fun registerEvents() {
        binding.addBtn.setOnClickListener {
            if (frag!=null){
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            }
            frag = popupFragment()
            frag!!.setListener(this)
            frag!!.show(childFragmentManager, popupFragment.TAG)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())

        binding.rview.setHasFixedSize(true)
        binding.rview.layoutManager = LinearLayoutManager(context)
        list = mutableListOf()
        adapter = todoAdapter(list)
        adapter.setListener(this)
        binding.rview.adapter = adapter
    }

    private fun getDataFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask = taskSnapshot.key?.let {
                        todoData(it, taskSnapshot.value.toString())
                    }
                    if (todoTask != null) {
                        list.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the onCancelled event appropriately, e.g., show an error message
                Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
            databaseRef.push().setValue(todo).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Your task is saved successfully", Toast.LENGTH_SHORT).show()

                } else
                {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
                todoEt.text=null
                frag!!.dismiss()
            }

    }

    override fun onUpdateTask(datatodoData: todoData, todoEt: TextInputEditText) {
        val map = HashMap<String,Any>()
        map[datatodoData.taskId]=datatodoData.task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(context, it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            todoEt.text=null
            frag!!.dismiss()
        }
    }

    override fun onDltBtnClicked(todoData: todoData) {
            databaseRef.child(todoData.taskId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onEdtBtnClicked(todoData: todoData) {
            if (frag!=null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
        frag=popupFragment.newInstance(todoData.taskId,todoData.task)
        frag!!.setListener(this)
        frag!!.show(childFragmentManager,popupFragment!!.TAG)
    }
}
