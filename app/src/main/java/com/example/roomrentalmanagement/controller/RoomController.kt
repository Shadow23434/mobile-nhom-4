package com.example.roomrentalmanagement.controller

import com.example.roomrentalmanagement.model.Room

object RoomController {

    private val roomList: MutableList<Room> = mutableListOf()

    init {
        // Seed sample data
        roomList.add(Room("P001", "Phòng 101", 1500000.0, true))
        roomList.add(Room("P002", "Phòng 102", 1800000.0, false, "Nguyễn Văn A", "0901234567"))
        roomList.add(Room("P003", "Phòng 103", 2000000.0, true))
    }

    fun getRooms(): MutableList<Room> = roomList

    fun addRoom(room: Room) {
        roomList.add(room)
    }

    fun updateRoom(index: Int, room: Room) {
        if (index in roomList.indices) {
            roomList[index] = room
        }
    }

    fun deleteRoom(index: Int) {
        if (index in roomList.indices) {
            roomList.removeAt(index)
        }
    }

    fun isRoomIdDuplicate(id: String, excludeIndex: Int = -1): Boolean {
        return roomList.filterIndexed { i, room -> i != excludeIndex && room.id == id }.isNotEmpty()
    }
}
