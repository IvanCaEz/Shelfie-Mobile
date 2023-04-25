package com.example.shelfie_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.R
import com.example.shelfie_app.databinding.FragmentCompleteRegisterBinding
import com.example.shelfie_app.model.User
import com.example.shelfie_app.model.UserType
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class CompleteRegisterFragment : Fragment() {
  private lateinit var binding: FragmentCompleteRegisterBinding
    lateinit var viewModel: ApiViewModel
    var imageName = "/placeholder.jpg"
    var imagePath = ""
    var imageUri: Uri? = null
    private val REQUEST_PERMISSIONS = 1

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                runBlocking {
                    imageUri = data.data!!
                    binding.addPhotoIV.setImageURI(imageUri)
                    // Get the file name from the URI
                    imageName = getFileName(imageUri!!)
                }
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

        val userName = arguments?.getString("userName")
        val password = arguments?.getString("password")
        binding.usernameTV.text = "@$userName"



        binding.addPhotoIV.setOnClickListener {
            // Check if permissions are given
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the missing permissions
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
            } else {
                selectImage()
            }
        }

        binding.continueIV.setOnClickListener{
            val name = binding.nameET.editText?.text.toString().trim()
            val email = binding.emailET.editText?.text.toString().trim()
            val bio = binding.bioET.editText?.text.toString().trim()

            if (validateEmail(email)){
                binding.progressBar.visibility = View.VISIBLE
                if (imageUri != null){
                    val newUser = User("0",name, userName!!,bio,email, password!!, UserType.NORMAL,
                        0, setOf<Int>(), false, imageName)
                    try {
                        imagePath = getPathFromUri(requireContext(), imageUri!!)!!
                    } catch (e: java.lang.NullPointerException){
                        println("${e.cause} : ${e.message}")
                    }
                    println(imagePath)

                    val imageFile = File(imagePath)
                    viewModel.postUser(newUser,imageFile)
                } else{
                    val newUser = User("0",name, userName!!,bio,email, password!!, UserType.NORMAL,
                        0, setOf<Int>(), false, "user_placeholder.png")

                    val placeholderDrawable = resources.getDrawable(R.drawable.user_placeholder)
                    val file = File(context?.cacheDir, "placeholder.png")
                    val fileOutputStream = FileOutputStream(file)
                    val bitmap = (placeholderDrawable as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.close()
                    val filePath = file.absolutePath
                    val imageFile = File(filePath)
                    viewModel.postUser(newUser,imageFile)
                }
                binding.progressBar.visibility = View.INVISIBLE
                toLogin()
            }
        }
    }



    private fun toLogin(){
        val toLogin = CompleteRegisterFragmentDirections.actionCompleteRegisterFragmentToLoginFragment()
        findNavController().navigate(toLogin)
    }
    private fun validateEmail(email: String): Boolean {
        val emailPattern = Regex(
                    "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$")
        return if (email.isEmpty()){
            binding.emailET.error = "Email can't be empty."
            false
        }else if (!emailPattern.matches(email)){
            binding.emailET.error = "Not a valid email."
            false
        } else {
            binding.emailET.error = null
            binding.emailET.isErrorEnabled = false
            true
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    selectImage()
                } else {
                   Toast.makeText(requireContext(),
                       "Please accept the permissions", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}


// Function graveyard
/*
 fun getPathFromURI(context: Context, uri: Uri): String? {
        var path = uri.path
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val documentId = cursor.getString(0)
            documentId?.let {
                val split = it.split(":")
                if (split.size > 1) {
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                }
            }
            cursor.close()
        }
        return path
    }


    private fun getRealPathFromURI(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri!!, projection, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(projection[0])
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }
    fun getPathFromURI3(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        }
        return null
    }

    fun getImagePathFromImageView(context: Context, imageView: ImageView, uri: Uri): String? {
        val drawable = imageView.drawable ?: return null
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        }
        return null
    }

    fun getMediaFilePath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun getPathFromURI4(context: Context, uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            } else {
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    path = file.absolutePath
                }
            }
        }
        return path
    }
 */