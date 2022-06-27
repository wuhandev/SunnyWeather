package com.wh.sunnyweather.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.wh.sunnyweather.databinding.FragmentPlaceBinding


class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    private var _binding: FragmentPlaceBinding? = null

    private val binding get() = _binding!!

    private val mContext = this

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //requireActivity() 返回的是宿主activity
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.CREATED) {
                    //处理逻辑
                    val layoutManager = LinearLayoutManager(activity)
                    binding.recyclerView.layoutManager = layoutManager
                    adapter = PlaceAdapter(mContext, viewModel.placeList)
                    binding.recyclerView.adapter = adapter
                    binding.searchPlaceEdit.addTextChangedListener { editable ->
                        val content = editable.toString()
                        if (content.isNotEmpty()) {
                            viewModel.searchPlaces(content)
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.bgImageView.visibility = View.VISIBLE
                            viewModel.placeList.clear()
                            adapter.notifyDataSetChanged()
                        }
                    }
                    viewModel.placeLiveData.observe(mContext, Observer { result ->
                        val places = result.getOrNull()
                        if (places != null) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.bgImageView.visibility = View.GONE
                            viewModel.placeList.clear()
                            viewModel.placeList.addAll(places)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                            result.exceptionOrNull()?.printStackTrace()
                        }
                    })

                    //删除观察者
                    lifecycle.removeObserver(this)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}