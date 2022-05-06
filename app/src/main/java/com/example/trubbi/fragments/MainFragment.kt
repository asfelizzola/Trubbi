package com.example.trubbi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trubbi.R

class MainFragment : Fragment() {

    lateinit var main_view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        main_view = inflater.inflate(R.layout.fragment_main, container, false)

        return main_view
    }

    override fun onStart() {
        super.onStart()
    }

}