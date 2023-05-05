package com.erensekkeli.gradconnect.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erensekkeli.gradconnect.R
import com.erensekkeli.gradconnect.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.button.setOnClickListener { searchUser(view) }
    }

    private fun searchUser(view: View) {
        //go to search result fragment
        val bundle = Bundle()
        val nameSurname: String? = binding.nameOrSurnameSearch.text?.toString()
        if (nameSurname != "") {
            bundle.putString("nameSurname", nameSurname)
        }
        val country: String? = binding.countrySpinner?.selectedItem?.toString()
        bundle.putString("country", country)
        val city: String? = binding.citySearch?.text?.toString()
        if(city != "") {
            bundle.putString("city", city)
        }
        var graduateDate: String? = binding.graduationDateSearch?.text?.toString()
        if(graduateDate != "") {
            bundle.putString("graduateDate", graduateDate)
        }
        val searchResultFragment = SearchResultFragment()
        searchResultFragment.arguments = bundle
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_right_to_left,
            R.anim.exit_right_to_left,
            R.anim.enter_left_to_right,
            R.anim.exit_left_to_right
        )
        transaction.replace(R.id.feedContainerFragment, searchResultFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}