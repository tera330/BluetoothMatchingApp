package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.BluetoothBK
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentProfileListBinding



class ProfileListFragment : Fragment() {

    private var _binding: FragmentProfileListBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemListAdapter = ItemListAdapter({
            val action = ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment2() // ToDo navgraphで引数を渡す
            this.findNavController().navigate(action)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter

        fireStore.getData(itemListAdapter, this)


        binding.pastButton.setOnClickListener {
            val action = ProfileListFragmentDirections.actionProfileListFragmentToPastProfileListFragment2()
            this.findNavController().navigate(action)
        }

        binding.button.setOnClickListener { //スタート
            val intent = Intent(requireActivity(), BluetoothBK::class.java)
            requireActivity().startForegroundService(intent);
        }

        binding.button2.setOnClickListener {
            val intent = Intent(requireContext() , BluetoothBK::class.java)
            requireActivity(). stopService(intent);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
