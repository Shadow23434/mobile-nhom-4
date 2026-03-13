package com.example.roomrentalmanagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.roomrentalmanagement.controller.RoomController
import com.example.roomrentalmanagement.model.Room
import com.google.android.material.textfield.TextInputLayout

class RoomFormActivity : AppCompatActivity() {

    // TextInputLayout containers (để set error)
    private lateinit var tilRoomId: TextInputLayout
    private lateinit var tilRoomName: TextInputLayout
    private lateinit var tilPrice: TextInputLayout
    private lateinit var tilTenantName: TextInputLayout
    private lateinit var tilTenantPhone: TextInputLayout

    // EditText bên trong để lấy text
    private lateinit var etRoomId: EditText
    private lateinit var etRoomName: EditText
    private lateinit var etPrice: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioAvailable: RadioButton
    private lateinit var radioRented: RadioButton
    private lateinit var layoutTenant: View
    private lateinit var etTenantName: EditText
    private lateinit var etTenantPhone: EditText
    private lateinit var btnSave: Button

    private var isEditMode = false
    private var editPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_form)

        val toolbar = findViewById<Toolbar>(R.id.toolbarForm)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Bind TextInputLayout
        tilRoomId = findViewById(R.id.tilRoomId)
        tilRoomName = findViewById(R.id.tilRoomName)
        tilPrice = findViewById(R.id.tilPrice)
        tilTenantName = findViewById(R.id.tilTenantName)
        tilTenantPhone = findViewById(R.id.tilTenantPhone)

        // Bind EditText
        etRoomId = findViewById(R.id.etRoomId)
        etRoomName = findViewById(R.id.etRoomName)
        etPrice = findViewById(R.id.etPrice)
        radioGroup = findViewById(R.id.radioGroupStatus)
        radioAvailable = findViewById(R.id.radioAvailable)
        radioRented = findViewById(R.id.radioRented)
        layoutTenant = findViewById(R.id.layoutTenant)
        etTenantName = findViewById(R.id.etTenantName)
        etTenantPhone = findViewById(R.id.etTenantPhone)
        btnSave = findViewById(R.id.btnSave)

        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)
        supportActionBar?.title = if (isEditMode) "Sửa thông tin phòng" else "Thêm phòng mới"

        if (isEditMode) {
            editPosition = intent.getIntExtra("POSITION", -1)
            etRoomId.setText(intent.getStringExtra("ROOM_ID"))
            etRoomId.isEnabled = false
            etRoomName.setText(intent.getStringExtra("ROOM_NAME"))
            etPrice.setText(intent.getDoubleExtra("ROOM_PRICE", 0.0).toBigDecimal().stripTrailingZeros().toPlainString())
            val isAvailable = intent.getBooleanExtra("ROOM_AVAILABLE", true)
            if (isAvailable) radioAvailable.isChecked = true else radioRented.isChecked = true
            etTenantName.setText(intent.getStringExtra("ROOM_TENANT_NAME"))
            etTenantPhone.setText(intent.getStringExtra("ROOM_TENANT_PHONE"))
            layoutTenant.visibility = if (isAvailable) View.GONE else View.VISIBLE
        } else {
            radioAvailable.isChecked = true
            layoutTenant.visibility = View.GONE
        }

        // Xóa lỗi khi người dùng bắt đầu nhập
        etRoomId.setOnFocusChangeListener { _, _ -> tilRoomId.error = null }
        etRoomName.setOnFocusChangeListener { _, _ -> tilRoomName.error = null }
        etPrice.setOnFocusChangeListener { _, _ -> tilPrice.error = null }
        etTenantName.setOnFocusChangeListener { _, _ -> tilTenantName.error = null }
        etTenantPhone.setOnFocusChangeListener { _, _ -> tilTenantPhone.error = null }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            layoutTenant.visibility = if (checkedId == R.id.radioRented) View.VISIBLE else View.GONE
        }

        btnSave.setOnClickListener { saveRoom() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun saveRoom() {
        // Xóa tất cả lỗi cũ trước
        tilRoomId.error = null
        tilRoomName.error = null
        tilPrice.error = null
        tilTenantName.error = null
        tilTenantPhone.error = null

        val id = etRoomId.text.toString().trim()
        val name = etRoomName.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        val isAvailable = radioAvailable.isChecked
        val tenantName = etTenantName.text.toString().trim()
        val tenantPhone = etTenantPhone.text.toString().trim()

        // Validation – set error trên TextInputLayout để hiển thị đúng
        var hasError = false

        if (id.isEmpty()) {
            tilRoomId.error = "Mã phòng không được để trống"
            hasError = true
        } else if (RoomController.isRoomIdDuplicate(id, if (isEditMode) editPosition else -1)) {
            tilRoomId.error = "Mã phòng đã tồn tại"
            hasError = true
        }

        if (name.isEmpty()) {
            tilRoomName.error = "Tên phòng không được để trống"
            hasError = true
        }

        val price = priceStr.toDoubleOrNull()
        if (priceStr.isEmpty()) {
            tilPrice.error = "Giá thuê không được để trống"
            hasError = true
        } else if (price == null || price <= 0) {
            tilPrice.error = "Giá thuê phải là số dương hợp lệ"
            hasError = true
        }

        if (!isAvailable) {
            if (tenantName.isEmpty()) {
                tilTenantName.error = "Tên người thuê không được để trống"
                hasError = true
            }
            if (tenantPhone.isEmpty()) {
                tilTenantPhone.error = "Số điện thoại không được để trống"
                hasError = true
            } else if (!tenantPhone.matches(Regex("^0[0-9]{9}$"))) {
                tilTenantPhone.error = "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)"
                hasError = true
            }
        }

        if (hasError) {
            Toast.makeText(this, "Vui lòng kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val room = Room(
            id = id,
            name = name,
            price = price!!,
            isAvailable = isAvailable,
            tenantName = if (isAvailable) "" else tenantName,
            tenantPhone = if (isAvailable) "" else tenantPhone
        )

        if (isEditMode) {
            RoomController.updateRoom(editPosition, room)
            Toast.makeText(this, "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show()
            val resultIntent = Intent().apply { putExtra("POSITION", editPosition) }
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            RoomController.addRoom(room)
            Toast.makeText(this, "Thêm phòng \"$name\" thành công!", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
        }
        finish()
    }
}
