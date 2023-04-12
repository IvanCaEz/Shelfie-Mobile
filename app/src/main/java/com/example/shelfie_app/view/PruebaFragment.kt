package com.example.shelfie_app.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shelfie_app.databinding.FragmentPruebaBinding
import com.example.shelfie_app.model.Book
import com.example.shelfie_app.viewmodel.ApiViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DecimalFormat


class PruebaFragment : Fragment() {
    private lateinit var binding: FragmentPruebaBinding
    lateinit var viewModel: ApiViewModel


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val imageUri = data.data!!
                val imagePath = getPathFromImageUri(requireContext(), imageUri)
                binding.bookCover.setImageURI(imageUri)

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPruebaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ApiViewModel::class.java)
        binding.bookCover.setOnClickListener {
           selectImage()
        }
        binding.buscar.setOnClickListener {
            viewModel.getBookByID(binding.bookIDET.text.toString())
            viewModel.bookData.observe(viewLifecycleOwner, Observer {
                if (viewModel.bookData.value == null) {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.getAllReviewsFromBook(it.idBook)
                    viewModel.getBookCover(it.idBook)
                    getRating()
                    pasteInfo()
                    getBookCover()


                }
            })
        }


    }

fun getBookCover(){
    viewModel.bookCover.observe(viewLifecycleOwner, Observer {
        binding.bookCover.setImageBitmap(it)
    })

}


fun getRating() {
    viewModel.listOfBookReviews.observe(viewLifecycleOwner, Observer { reviewList ->
        println(reviewList.size)
        if (viewModel.listOfBookReviews.value != null){
            binding.bookScore.text = "Puntuación: " + viewModel.getBookScore(reviewList)
        } else {
            binding.bookScore.text = "Puntuación: 0"
        }

    })
}

    fun displayImageFromExternalStorage(imageName: String, imageView: ImageView) {
        val imageFile = File("src/main/kotlin/com/example/book-covers/$imageName")
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
    }

fun pasteInfo() {
    println(viewModel.bookData.value?.title)

    binding.bookName.text = viewModel.bookData.value?.title
}

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }





    fun getPathFromImageUri(context: Context, imageUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(imageUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun getImagePathFromImageView(context: Context, imageView: ImageView): String? {
        val drawable = imageView.drawable ?: return null
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return null
        val uri = getImageUri(context, bitmap) ?: return null
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

    fun getImageUri(context: Context, image: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
        return Uri.parse(path)
    }


    fun getImageUriFromImageView(context: Context, imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val savedImageUri = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "image",
                "image from ImageView"
            )
            return Uri.parse(savedImageUri)
        }
        return null
    }
}