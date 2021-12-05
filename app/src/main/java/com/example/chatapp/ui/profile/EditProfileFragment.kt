package com.example.chatapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.service.model.User
import com.example.chatapp.viewmodels.EditProfileViewModel
import com.example.chatapp.viewmodels.EditProfileViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_profile.*


class EditProfileFragment : Fragment() {
    private lateinit var editProfileViewModel: EditProfileViewModel
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var backBtn: ImageView
    lateinit var username: EditText
    lateinit var status: EditText
    lateinit var saveBtn: Button
    lateinit var profilePhoto: CircularImageView
    lateinit var getImage: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        editProfileViewModel = ViewModelProvider(
            this,
            EditProfileViewModelFactory()
        )[EditProfileViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        backBtn = view.findViewById(R.id.backBtn)
        username = view.findViewById(R.id.editName_et)
        status = view.findViewById(R.id.editstatus_et)
        saveBtn = view.findViewById(R.id.savechangesBtn)
        profilePhoto = view.findViewById(R.id.editPhoto)
        editProfileViewModel.fetchProfilepic()
        editProfileViewModel.fetchUserDetails()
        backBtn.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }
        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    profilePhoto.setImageURI(it)
                    editProfileViewModel.uploadProfilePic(it)
                }
            }
        )
        profilePhoto.setOnClickListener {
            getImage.launch("image/*")
        }
       username.setOnFocusChangeListener { view, b ->
           if(b){
               saveBtn.isVisible = true
           }
       }
        status.setOnFocusChangeListener { view, b ->
            if(b){
                saveBtn.isVisible = true
            }
        }
        saveBtn.setOnClickListener {
            if(username.text.toString().isNotEmpty() && status.text.toString().isNotEmpty()){
                val user = User(username.text.toString(), status.text.toString())
                editProfileViewModel.addUserDetails(user)
            }
        }
        observe()
        return view
    }

    private fun observe() {
        editProfileViewModel.profilePhotoStatus.observe(viewLifecycleOwner){
            if(it != null){
               Picasso.get().load(it).into(profilePhoto)
            }
        }
        editProfileViewModel.userDetailsStatus.observe(viewLifecycleOwner){
            if(it != null){
                if(it.userName == "null" && it.status == "null"){
                    username.hint = "UserName"
                    status.hint = "Status"
                }
                else {
                    username.setText(it.userName)
                    status.setText(it.status)
                }

            }
        }
        editProfileViewModel.addUserDetailsStatus.observe(viewLifecycleOwner){
            if(it){
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
    }
}