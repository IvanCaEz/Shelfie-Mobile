package com.example.shelfie_app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shelfie_app.databinding.FragmentEditProfileBinding
import com.example.shelfie_app.model.User
import com.example.shelfie_app.model.UserType
import com.example.shelfie_app.viewmodel.ApiViewModel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream


class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    val viewModel: ApiViewModel by activityViewModels()
    var imagePath = ""
    var imageUri: Uri? = null
    private val REQUEST_PERMISSIONS = 1
    var newImageName = ""

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                runBlocking {
                    imageUri = data.data!!
                    binding.profilepic.setImageURI(imageUri)
                    // Get the file name from the URI
                    newImageName = getFileName(imageUri!!)

                    println(imageUri.toString())

                    println(newImageName)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userID = viewModel.userData.value!!.idUser
        setCurrentUserInfo()
        val userImageName = viewModel.userData.value!!.userImage

        binding.profilepic.setOnClickListener{
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

        binding.saveButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val name = binding.usernameText.text.toString()
            val userName = binding.usernameArroba.text.toString()
            val bio = binding.userBio.text.toString()
            val email = binding.emailET.text.toString()
            val password = viewModel.userData.value!!.password


            if (validateEmail(email)){
                // Si el nombre de la imagen es diferente, subimos la nueva
                if (userImageName != newImageName){
                    val userToUpdate = User(userID, name, userName, bio, email, password,
                        UserType.NORMAL, 0, setOf<Int>(), false, newImageName)
                    try {
                        imagePath = getPathFromUri(requireContext(), imageUri!!)!!
                    } catch (e: java.lang.NullPointerException){
                        println("${e.cause} : ${e.message}")
                    }
                    val imageFile = File(imagePath)
                    viewModel.putUser(userToUpdate,imageFile)
                // Si no es diferente, subimos la misma
                } else {
                    val userToUpdate = User(userID, name, userName, bio, email, password,
                        UserType.NORMAL, 0, setOf<Int>(), false, userImageName)

                    val path = pathFromBitmap(viewModel.userImage.value!!, userImageName)

                    val imageFile = File(path!!)

                    println("ahora no se sube la misma imagen :(")
                    println(imageFile)

                    viewModel.putUser(userToUpdate,imageFile)


                }
                binding.progressBar.visibility = View.INVISIBLE
                toProfile()
            }
        }

        // Delete
        binding.deleteButton.setOnClickListener {
            viewModel.deleteUser(userID)
            val toLogin = EditProfileFragmentDirections.actionEditProfileFragmentToLoginFragment()
            findNavController().navigate(toLogin)
        }
        // Cancel
        binding.backButton.setOnClickListener {
           toProfile()
        }
    }

    private fun pathFromBitmap(bitmap: Bitmap, imageName: String): String? {
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),imageName )
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun toProfile(){
        val toProfile =
            EditProfileFragmentDirections.actionEditProfileFragmentToUserProfileFragment()
        findNavController().navigate(toProfile)
    }
    private fun setCurrentUserInfo() {
        binding.profilepic.setImageBitmap(viewModel.userImage.value)
        binding.usernameText.setText(viewModel.userData.value!!.name)
        binding.usernameArroba.setText(viewModel.userData.value!!.userName)
        binding.userBio.setText(viewModel.userData.value!!.description)
        binding.emailET.setText(viewModel.userData.value!!.email)
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

    private fun validateEmail(email: String): Boolean {
        val emailPattern = Regex(
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$")
        return if (!emailPattern.matches(email)){
             Toast.makeText(requireContext(),"Email can't be empty.",Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
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