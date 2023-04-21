package com.example.shelfie_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentCompleteRegisterBinding
import com.example.shelfie_app.model.User
import com.example.shelfie_app.model.UserType
import com.example.shelfie_app.viewmodel.ApiViewModel
import java.io.File

class CompleteRegisterFragment : Fragment() {
  private lateinit var binding: FragmentCompleteRegisterBinding
    lateinit var viewModel: ApiViewModel
    var imageName = "/placeholder.jpg"
    var imagePath = ""
    lateinit var imageUri: Uri
    val REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 100

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                imageUri = data.data!!

                binding.addPhotoIV.setImageURI(imageUri)
                // Get the file name from the URI
                imageName = getFileName(imageUri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentCompleteRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ApiViewModel::class.java)

        //val userName = arguments?.getString("userName")
        //val password = arguments?.getString("password")
        val userName = arguments?.getString("userName")
        val password = arguments?.getString("password")
        binding.usernameTV.text = userName






        binding.addPhotoIV.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE_PERMISSION)
            } else{
                selectImage()
            }

        }

        binding.continueIV.setOnClickListener{
            val name = binding.nameET.editText?.text.toString()
            val email = binding.emailET.editText?.text.toString()
            val bio = binding.bioET.editText?.text.toString()



            val newUser = User("0",name, userName!!,bio,email, password!!, UserType.NORMAL,
                0, setOf<Int>(), false, imageName)


            println(imageUri)

            imagePath = getPathFromUri(requireContext(),imageUri)!!
            println(imagePath)
            val imageFile = File(imagePath)
            //println(getPathFromImageUri(requireContext(),imageUri)!!)
            viewModel.postUser(newUser,imageFile)





        }



    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context?.contentResolver?.query(uri,
                null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    result = cursor.getString(column_index)
                }
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result!!
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split the ID into the type and the actual ID
        val id = wholeID.split(":")[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // Get the cursor for the selected image
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null)

        cursor?.let {
            val columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }

        return filePath
    }

    private fun getPathFromUri2(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val imagePath = it.getString(columnIndex)
            cursor.close()
            return imagePath
        }
        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    selectImage()
                } else {
                    // permission denied, handle the error
                }
                return
            }
            // add cases for other permissions you may request
            else -> {
                // handle other permissions
            }
        }
    }
}

/*
          val drawable = binding.addPhotoIV.drawable
           if (drawable is BitmapDrawable) {
              val bitmap = drawable.bitmap
              val file = File(context?.externalCacheDir, "image.jpg")
              val outputStream = FileOutputStream(file)
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
              outputStream.flush()
              outputStream.close()

              val path = file.path
              // use the file path here
          viewModel.postUser(newUser, path)
          }
           */