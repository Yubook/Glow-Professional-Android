package com.youbook.glowpros.ui.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.fade.ui.chat.MessageDataItem
import com.youbook.glowpros.utils.*
import com.youbook.glowpros.utils.Utils.toast
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityChatBinding
import com.youbook.glowpros.utils.FileUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private var isScroll: Boolean? = true
    private var mCurrentPhotoPath: String? = null
    private var chattingAdapter: ChattingAdapter? = null
    private var arrayList: ArrayList<MessageDataItem>? = null
    private lateinit var binding: ActivityChatBinding
    private var titleName: String? = ""
    private var currentPage: Int? = 1
    private val mapHeader: HashMap<String, String> = HashMap()
    var uploadDialog: ProgressDialog? = null
    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_GET_PHOTO = 2
    private val PermissionsRequestCode = 101
    private var uriTemp: Uri? = null
    private lateinit var managePermissions: ManagePermissions

    val list = listOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleName = intent.getStringExtra(Constants.INTENT_KEY_CHAT_TITLE)
        binding.tvToolbarTitle.text = titleName
        uploadDialog = ProgressDialog(this)
        setClickListener()
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        setData()

        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                emitMessages()
                onMessages()
                onCurrentMessage()
            }
        }

        initKeyBoardListener()
    }

    private fun setData() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.itemAnimator = null
        arrayList = ArrayList<MessageDataItem>()
        chattingAdapter = ChattingAdapter(
            this, arrayList!!, Prefrences.getPreferences(
                this,
                Constants.USER_ID
            )!!
        )
        binding.chatRecyclerView.adapter = chattingAdapter
        chattingAdapter!!.notifyDataSetChanged()
    }

    // initialize inbox data
    private fun emitMessages() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("group_id", intent.getStringExtra(Constants.INTENT_KEY_CHAT_GROUP_ID))
            jsonObject.put("token", Prefrences.getPreferences(this, Constants.API_TOKEN))
            //jsonObject.put("page", currentPage)
            SocketConnector.getSocket()!!.emit("getMessages", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // retrieve inbox data
    private fun onMessages() {
        SocketConnector.getSocket()!!.on(
            "setMessages"
        ) { args ->
            val data = args[0] as JSONObject
            /*if (getPrefValue(Keys.USER_TOKEN).equals(data.optString("token"))) {*/
            val chatData: ChatData =
                Gson().fromJson(
                    data.toString(),
                    object : TypeToken<ChatData?>() {}.type
                )
            arrayList!!.clear()
            mapHeader.clear()
            for (i in chatData.messageData!!.indices) {
                val messageDataItem: MessageDataItem = chatData.messageData[i]
                val localTime: Calendar = Utils.getLocalTime(
                    messageDataItem.createdAt,
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                )!!
                @SuppressLint("SimpleDateFormat") val time =
                    SimpleDateFormat("hh:mm a").format(localTime.time)
                if (!mapHeader.containsKey(
                        getFormattedDate(this, localTime.timeInMillis)
                    )
                ) {
                    mapHeader.put(
                        getFormattedDate(this, localTime.timeInMillis)!!,
                        getFormattedDate(this, localTime.timeInMillis)!!
                    )
                    messageDataItem.header = getFormattedDate(this, localTime.timeInMillis)!!
                } else {
                    messageDataItem.header = ""
                }
                arrayList!!.add(messageDataItem)
            }
            notifyList()
            scrollViewToLastPos(false)

        }
    }


    //Get Current Msg
    private fun onCurrentMessage() {
        SocketConnector.getSocket()!!.on(
            "getCurrentMessage"
        ) { args ->
            runOnUiThread {
                if (uploadDialog!!.isShowing) {
                    uploadDialog!!.hide()
                }
            }
            val data = args[0] as JSONObject
            try {
                val result = data.getJSONObject("result")
                if (intent.getStringExtra(Constants.INTENT_KEY_CHAT_GROUP_ID)!!
                        .toInt() === result.optInt(
                        "group_id"
                    )
                ) {
                    val messageData: MessageData =
                        Gson().fromJson(data.toString(), object : TypeToken<MessageData?>() {}.type)
                    val localTime: Calendar = Utils.getLocalTime(
                        messageData.result!!.createdAt,
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                    )!!
                    @SuppressLint("SimpleDateFormat") val time =
                        SimpleDateFormat("hh:mm a").format(localTime.time)
                    if (!mapHeader.containsKey(
                            getFormattedDate(
                                this,
                                localTime.timeInMillis
                            )
                        )
                    ) {
                        mapHeader[getFormattedDate(
                            this,
                            localTime.timeInMillis
                        )!!] = getFormattedDate(this, localTime.timeInMillis)!!

                        messageData.result.header = getFormattedDate(
                            this,
                            localTime.timeInMillis
                        )!!

                    } else {
                        messageData.result.header = ""
                    }
                    Log.d("TAG", "call:  status..." + messageData.result.status)
                    arrayList!!.add(messageData.result)
                    notifyList()
                    scrollViewToLastPos(true)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun initKeyBoardListener() {
        val MIN_KEYBOARD_HEIGHT_PX = 150
        val decorView = window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight = 0
            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        keyboardState(true)
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        keyboardState(false)
                    }
                }
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }

    private fun notifyList() {
        runOnUiThread { chattingAdapter!!.notifyDataSetChanged() }
    }

    private fun scrollViewToLastPos(isSmoothScroll: Boolean) {
        if (arrayList != null && arrayList!!.size > 0) {
            runOnUiThread {
                if (isSmoothScroll) binding.chatRecyclerView.smoothScrollToPosition(arrayList!!.size - 1) else binding.chatRecyclerView.scrollToPosition(
                    arrayList!!.size - 1
                )
//                binding.relNewMsg.setVisibility(View.GONE)
//                count = 0
                isScroll = false
//                binding.relNewMsg.setVisibility(View.GONE)
            }
        }
    }

    private fun getFormattedDate(context: Context?, timeInMillis: Long): String? {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = timeInMillis
        val now = Calendar.getInstance()
        val dateTimeFormatString = "EEEE, MMMM d"
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            "Today "
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            "Yesterday "
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MMMM dd yyyy", smsTime).toString()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    chooseImage()
                } else {
                    this.toast("Permissions denied.")
                }
                return
            }
        }
    }

    fun keyboardState(b: Boolean) {
        if (isScroll!!) scrollViewToLastPos(false)
    }

    private fun setClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.ivGallery.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
            R.id.ivSend -> sendMessage()
            R.id.ivGallery -> permissionGranted()

        }
    }

    private fun permissionGranted() {
        if (managePermissions.checkPermissions()) {
            chooseImage()
        }
    }

    private fun sendMessage() {
        if (Utils.isConnected(this)) {
            if (binding.edtMessage.text.toString() != "") {
                isScroll = false
                emitSendMsg()
            }

            notifyList()
            binding.edtMessage.setText("")
        }
    }

    private fun chooseImage() {
        val btnsheet = layoutInflater.inflate(R.layout.choose_image_dialog, null)
        val dialog = BottomSheetDialog(this)
        val tvCaptureImage: TextView = btnsheet.findViewById(R.id.tvCaptureImage)
        val tvUploadImage: TextView = btnsheet.findViewById(R.id.tvUploadImage)
        dialog.setContentView(btnsheet)
        tvCaptureImage.setOnClickListener {
            takePicture()
            dialog.cancel()
        }
        tvUploadImage.setOnClickListener {
            TedImagePicker.with(this)
                .start { uri -> showSingleImage(uri) }
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showSingleImage(uri: Uri) {
        mCurrentPhotoPath = FileUtil.getPath(ToastUtil.context, uri)
        val f = File(mCurrentPhotoPath!!)
        uriTemp = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            f
        )
        cropImage(uriTemp!!)
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider", photoFile
                    )
                )
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FadeDriver")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val file: File = File(storageDir.getPath().toString() + File.separator + imageFileName)

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = file.absolutePath
        return file
    }

    //Send message
    private fun emitSendMsg() {
        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put(
                        "group_id",
                        intent.getStringExtra(Constants.INTENT_KEY_CHAT_GROUP_ID)
                    )
                    jsonObject.put("token", Prefrences.getPreferences(this, Constants.API_TOKEN))
                    jsonObject.put(
                        "message",
                        Objects.requireNonNull(binding.edtMessage.text).toString()
                            .trim { it <= ' ' })
                    jsonObject.put("type", "1")
                    SocketConnector.getSocket()!!.emit("sendMessage", jsonObject)
                    binding.edtMessage.setText("")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        f
                    )
                    cropImage(uriTemp!!)
                }
                REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    cropImage(uriTemp!!)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                    val file = File(mCurrentPhotoPath!!)
                    val imagePath: String = Utils.compressImage(
                        this@ChatActivity,
                        mCurrentPhotoPath!!
                    ).toString()
                    val bm = BitmapFactory.decodeFile(imagePath)
                    val bOut = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bOut)
                    val base64Image = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
                    binding.progressBar.visibility = View.VISIBLE
                    emitSendFile(base64Image)
                    /*Glide.with(application)
                        .load(file)
                        .placeholder(R.drawable.ic_avtar_placeholder)
                        .error(R.drawable.ic_avtar_placeholder)
                        .into(binding.ivUserProfile)*/
                }
            }
        }
    }


    //Send file
    private fun emitSendFile(base64String: String) {
        if (binding.progressBar.visibility == View.VISIBLE) {
            binding.progressBar.visibility = View.GONE
        }
        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("group_id", intent.getStringExtra(Constants.INTENT_KEY_CHAT_GROUP_ID))
                    jsonObject.put("token", Prefrences.getPreferences(this, Constants.API_TOKEN))
                    jsonObject.put("media", "data:image/png;base64,$base64String")
                    jsonObject.put("type", "2")
                    SocketConnector.getSocket()!!.emit("sendFile", jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun cropImage(uriTemp: Uri) {
        CropImage.activity(uriTemp)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(this)
    }
}