package com.bagus.finbud.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bagus.finbud.R
import com.bagus.finbud.activity.LoginActivity
import com.bagus.finbud.databinding.FragmentProfileBinding
import com.bagus.finbud.preferences.PreferenceManager
import com.bagus.finbud.util.PrefUtil

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        binding.textBalance.text = requireActivity().intent.getStringExtra("balance")
    }

    override fun onStart() {
        super.onStart()
        getAvatar()
    }

    private fun getAvatar(){
        binding.imageAvatar.setImageResource(pref.getInt(PrefUtil.pref_avatar)!! )
        binding.textName.text = pref.getString(PrefUtil.pref_name)
        binding.textUsername.text = pref.getString(PrefUtil.pref_username)
        binding.textDate.text = pref.getString(PrefUtil.pref_date)


    }

    private fun setupListener(){
        binding.imageAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_avatarFragment)
        }
        binding.cardLogout.setOnClickListener {
            pref.clear()
            Toast.makeText(requireContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(requireActivity(), LoginActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )
            requireActivity().finish()
        }
    }
}