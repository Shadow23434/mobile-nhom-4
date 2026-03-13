package com.example.roomrentalmanagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomrentalmanagement.adapter.RoomAdapter
import com.example.roomrentalmanagement.controller.RoomController
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter
    private val REQUEST_ADD = 1001
    private val REQUEST_EDIT = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quản lý phòng trọ"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RoomAdapter(
            RoomController.getRooms(),
            onEdit = { position -> openEditForm(position) },
            onDelete = { position -> confirmDelete(position) }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddRoom).setOnClickListener {
            val intent = Intent(this, RoomFormActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD)
        }
    }

    private fun openEditForm(position: Int) {
        val room = RoomController.getRooms()[position]
        val intent = Intent(this, RoomFormActivity::class.java).apply {
            putExtra("EDIT_MODE", true)
            putExtra("POSITION", position)
            putExtra("ROOM_ID", room.id)
            putExtra("ROOM_NAME", room.name)
            putExtra("ROOM_PRICE", room.price)
            putExtra("ROOM_AVAILABLE", room.isAvailable)
            putExtra("ROOM_TENANT_NAME", room.tenantName)
            putExtra("ROOM_TENANT_PHONE", room.tenantPhone)
        }
        startActivityForResult(intent, REQUEST_EDIT)
    }

    private fun confirmDelete(position: Int) {
        val room = RoomController.getRooms()[position]
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa phòng \"${room.name}\" không?")
            .setPositiveButton("Xóa") { _, _ ->
                RoomController.deleteRoom(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, RoomController.getRooms().size)
                Toast.makeText(this, "Đã xóa phòng ${room.name}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ADD -> {
                    adapter.notifyItemInserted(RoomController.getRooms().size - 1)
                }
                REQUEST_EDIT -> {
                    val position = data?.getIntExtra("POSITION", -1) ?: -1
                    if (position >= 0) {
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }
}