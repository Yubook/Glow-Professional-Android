package com.youbook.glowpros.ui.add_portfolio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.compressImage
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityAddPortfolioBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.utils.Utils.hide
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPortfolioActivity : AppCompatActivity(), OnPortfolioClick, View.OnClickListener {
    private lateinit var binding: ActivityAddPortfolioBinding
    private lateinit var viewModel: AddPortfolioViewModel
    private lateinit var adapter: AddPortfolioAdapter
    private val imageList: ArrayList<String> = ArrayList()
    private val requestTakePhoto = 1
    private val requestGetPhoto = 2
    private lateinit var mCurrentPhotoPath: String
    private var uriTemp: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            AddPortfolioViewModelFactory(
                AddPortfolioRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(AddPortfolioViewModel::class.java)

        setUpImageRecyclerview()
        setOnClickListener()
        imageList.add("add portfolio") // because we are showing add portfolio view on 0 position

        viewModel.addPortfolioResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            binding.relSave.visible(false)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    binding.relSave.visible(true)
                    if (it.value.success!!) {
                        ToastUtil.showToast(it.value.message!!)
                        finish()
                        adapter.notifyDataSetChanged()
                    } else {
                        Utils.showErrorDialog(this, it.value.message!!)
                    }

                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                    binding.relSave.visible(true)
                }
            }
        }
    }

    private fun setOnClickListener() {
        binding.relSave.setOnClickListener(this)
        binding.ivBackButton.setOnClickListener(this)
    }

    private fun setUpImageRecyclerview() {
        binding.portfolioRecyclerview.layoutManager = GridLayoutManager(this, 2)
        adapter = AddPortfolioAdapter(this, imageList, this)
        binding.portfolioRecyclerview.adapter = adapter
    }

    override fun onClickDelete(portfolioId: String?, position: Int) {
    }

    override fun addPortFolio() {
        if (imageList.size <= 5) {
            chooseImage()
        } else {
            ToastUtil.showToast("You can add only 5 images at a time")
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

        cropImage(uri)
        /*mCurrentPhotoPath = FileUtil.getPath(ToastUtil.context, uri)
        imageList.add(mCurrentPhotoPath)
        adapter.notifyDataSetChanged()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestTakePhoto -> {
                    val f = File(mCurrentPhotoPath)
                    uriTemp = FileProvider.getUriForFile(
                        this,
                        applicationContext.packageName + ".provider",
                        f
                    )

                    cropImage(uriTemp!!)
                }

                requestGetPhoto -> {
                    uriTemp = data?.data

                    cropImage(uriTemp!!)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                    val file = File(mCurrentPhotoPath)
                    imageList.add(mCurrentPhotoPath)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun cropImage(uriTemp: Uri) {
        CropImage.activity(uriTemp)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
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
                startActivityForResult(takePictureIntent, requestTakePhoto)
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FADE")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        if (!storageDir.exists()) {
            val mkdir = storageDir.mkdirs()
            if (!mkdir) {
                Log.e("TAG", "Directory creation failed.")
            } else {
                Log.e("TAG", "Directory created.")
            }

        }

        val file = File(storageDir.getPath().toString() + File.separator + imageFileName)
        mCurrentPhotoPath = file.absolutePath

        return file
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.relSave -> {
                if (Utils.isConnected(this)) {
                    serverCallForAddPortfolio(imageList)
                } else {
                    ToastUtil.showToast(Constants.NO_INTERNET_CONNECTION_MSG)
                }
            }
            R.id.ivBackButton -> {
                finish()
            }
        }
    }

    private fun serverCallForAddPortfolio(imageList: java.util.ArrayList<String>) {
        val partList: MutableList<MultipartBody.Part> = java.util.ArrayList<MultipartBody.Part>()

        imageList.removeAt(0) // remove add portfolio text that we added on start for that 'Add Photo view'
        for (i in imageList.indices) {
            val imagePath: String? = compressImage(this, imageList[i])
            partList.add(prepareFilePart("images[]", imagePath!!))
        }

        viewModel.viewModelScope.launch {
            viewModel.addPortfolioImage(partList)
        }
    }

    private fun prepareFilePart(partName: String, fileUri: String): MultipartBody.Part {
        val file: File = File(fileUri)
        // create RequestBody instance from file
        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

}