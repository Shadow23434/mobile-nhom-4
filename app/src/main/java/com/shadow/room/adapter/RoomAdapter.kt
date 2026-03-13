package com.example.roomrentalmanagement.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomrentalmanagement.R
import com.example.roomrentalmanagement.model.Room
import java.text.NumberFormat
import java.util.Locale

class RoomAdapter(
    private val rooms: MutableList<Room>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)
        val tvRoomId: TextView = view.findViewById(R.id.tvRoomId)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTenantName: TextView = view.findViewById(R.id.tvTenantName)
        val tvTenantPhone: TextView = view.findViewById(R.id.tvTenantPhone)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))

        holder.tvRoomName.text = room.name
        holder.tvRoomId.text = "Mã: ${room.id}"
        holder.tvPrice.text = "Giá: ${formatter.format(room.price)} đ/tháng"

        if (room.isAvailable) {
            holder.tvStatus.text = "Còn trống"
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available)
            holder.tvTenantName.visibility = View.GONE
            holder.tvTenantPhone.visibility = View.GONE
        } else {
            holder.tvStatus.text = "Đã thuê"
            holder.tvStatus.setTextColor(Color.parseColor("#C62828"))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rented)
            holder.tvTenantName.visibility = View.VISIBLE
            holder.tvTenantPhone.visibility = View.VISIBLE
            holder.tvTenantName.text = "Người thuê: ${room.tenantName}"
            holder.tvTenantPhone.text = "SĐT: ${room.tenantPhone}"
        }

        holder.btnEdit.setOnClickListener { onEdit(position) }
        holder.btnDelete.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = rooms.size
}
