package com.dimasfs.kpu.fragment

//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.navigation.fragment.findNavController
//import com.dimasfs.kpu.R
//import com.dimasfs.kpu.databinding.FragmentHomeBinding
//
//class HomeFragment : Fragment() {
//    lateinit var binding : FragmentHomeBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.informasi.setOnClickListener{
//            findNavController().navigate(R.id.action_HomeFragment_to_InformasiFragment)
//        }
//
//        binding.inputData.setOnClickListener{
//            findNavController().navigate(R.id.action_HomeFragment_to_InputDataFragment)
//        }
//
//        binding.lihatData.setOnClickListener{
//            findNavController().navigate(R.id.action_HomeFragment_to_LihatDataFragment)
//        }
//
//        binding.keluar.setOnClickListener{
//            activity?.finish()
//        }
//    }
//}