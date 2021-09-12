package com.myanmaritc.firebasetodo

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.core.Constants
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),UpdateAndDelete {

    private lateinit var database: FirebaseDatabase

    lateinit var databaseone: DatabaseReference
    var toDoList: MutableList<ToDoItem>? = null
    lateinit var adapter: ToDoAdapter
    private var listViewItem: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        listViewItem = findViewById<ListView>(R.id.items_list)

        databaseone = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setMessage("Add ToDo item")
            alertDialog.setTitle("Enter ToDo item")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Add") { dialog, i ->
                val todoItemData = ToDoItem.create()
                todoItemData.itemText = textEditText.text.toString()
                todoItemData.done = false

                val newItemData = databaseone.child("todo").push()
                todoItemData.objectId = newItemData.key

                newItemData.setValue(todoItemData)

                dialog.dismiss()
                Toast.makeText(
                    this,
                    "Item saved with ID " + todoItemData.objectId,
                    Toast.LENGTH_SHORT
                ).show()

            }
            alertDialog.show()
        }

        toDoList =  mutableListOf<ToDoItem>()
        adapter = ToDoAdapter(this, toDoList!!)
        listViewItem!!.adapter = adapter
        databaseone.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "No item added", Toast.LENGTH_LONG).show()
            }

        })

//        initFirebase()

    }

    private fun <T> mutableListOf(): MutableList<T>? {
        return null
    }

    private fun addItemToList(snapshot: DataSnapshot) {

        val items = snapshot.children.iterator()

        if (items.hasNext()) {
            val toDoIndextValue = items.next()
            val itemsIterator = toDoIndextValue.children.iterator()

            while (itemsIterator.hasNext()) {

                val currentItem = itemsIterator.next()
                val todoItem = ToDoItem.create()
                val map = currentItem.getValue() as HashMap<String, Any>

                todoItem.objectId = currentItem.key
                todoItem.done = map.get("done") as Boolean?
                todoItem.itemText = map.get("itemDataText") as String?
                toDoList!!.add(todoItem);

            }
        }
        adapter.notifyDataSetChanged()

    }

    override fun modifyItem(itemObjectId: String, isDone: Boolean){
        val itemReference = databaseone.child("todo").child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }
    override fun onItemDelete(itemObjectId: String){
        val itemReference = databaseone.child("todo").child(itemObjectId)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()

    }
}

//    private fun initFirebase(){
//
//        database = Firebase.database
//
//        btnInsert.setOnClickListener {
//            var name: String = edtName.text.toString()
////            var password: String = edtPassword.text.toString()
//
//            insertData(name)
//
//        }
//
//    }

//    private fun insertData(name: String){
//
////        val myRef = database.getReference("message")
////        myRef.setValue("Hello, World!")
//        val insertName = database.getReference(name)
////        val insertPassword = database.getReference("password")
//        insertName.setValue(name)
////        insertPassword.setValue(password)
//    }

