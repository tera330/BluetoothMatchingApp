package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.MainActivity
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.database.FireBaseStorage
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.database.imageRef
import com.example.bluettoothmatching.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()
    private val storage = FireBaseStorage()
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
        // navigationDrawerの非表示
        val mainActivity = requireActivity() as MainActivity
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val action = mainActivity.getActionBarDrawerToggle()
        action.isDrawerIndicatorEnabled = false
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //トップメニューの非表示
        val toolbar = mainActivity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        binding.postButton.setOnClickListener {
            val body = binding.createBody.text.toString()
            fireStore.post(body)

            if (imageUri != null) {
                storage.uploadImageToFirebaseStorage(imageUri!!, imageRef.toString())
                imageUri = null
            }
        }

        binding.advertiseButton.setOnClickListener {
            val body = binding.createBody.text.toString()
            fireStore.advertise(body)

            if (imageUri != null) {
                storage.uploadImageToFirebaseStorage(imageUri!!, imageRef.toString())
                imageUri = null
            }
        }

        binding.insertImage.setOnClickListener {
            selectPhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val READ_REQUEST_CODE: Int = 42
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        imageUri = uri // 画像のURI取得
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
}
