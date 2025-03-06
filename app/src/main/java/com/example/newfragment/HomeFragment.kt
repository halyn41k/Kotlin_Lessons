package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_simple, container, false)
        view.findViewById<TextView>(R.id.tvTitle).text = "Головна"
        val textView = view.findViewById<TextView>(R.id.textView)
            textView.setOnClickListener {
                textView.text = if (textView.text == "Натисни на мене") "Текст змінився" else "Натисни на мене"
            }
        return view
    }



}