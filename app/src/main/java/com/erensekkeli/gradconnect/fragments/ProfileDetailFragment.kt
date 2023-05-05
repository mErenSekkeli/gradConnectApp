package com.erensekkeli.gradconnect.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.adapters.ProfileDetailAdapter
import com.erensekkeli.gradconnect.databinding.FragmentProfileDetailBinding
import com.erensekkeli.gradconnect.models.User


class ProfileDetailFragment : Fragment() {

    private lateinit var binding: FragmentProfileDetailBinding
    private lateinit var user: User
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION")
        user = arguments?.getSerializable("user") as User
        binding.backBtn.setOnClickListener {
            getBack()
        }

        if(user.profileImage != null) {
            Glide.with(this).load(user.profileImage).into(binding.profileImage)
        }else {
            Glide.with(this).load(R.drawable.app_icon).into(binding.profileImage)
        }

        binding.profileNameSurname.text = user.name + " " + user.surname

        recyclerView = binding.profileDetailRecyclerView
        binding.profileDetailRecyclerView.layoutManager = LinearLayoutManager(context)
        val dataHashMap = HashMap<String, String?>()
        dataHashMap["Country"] = user.country
        dataHashMap["City"] = user.city
        dataHashMap["Contact Mail"] = user.contactMail
        dataHashMap["Contact Phone"] = user.contactPhone
        dataHashMap["School Entry Date"] = user.entryDate
        dataHashMap["Graduate Date"] = user.graduateDate
        dataHashMap["Current Business"] = user.currentBusiness
        dataHashMap["Department"] = user.department
        dataHashMap["Education Status"] = user.educationStatus
        dataHashMap["Facebook"] = user.facebook
        dataHashMap["Linkedin"] = user.linkedin

        binding.profileDetailRecyclerView.adapter = ProfileDetailAdapter(dataHashMap)

    }
    private fun getBack() {
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.popBackStack()
    }
}