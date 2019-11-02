package ru.islab.evilcomments.presentation.creators

import android.os.Bundle
import moxy.MvpAppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_creators.*
import ru.islab.evilcomments.R

class CreatorsActivity : MvpAppCompatActivity() {

    private lateinit var adapter: CreatorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.islab.evilcomments.R.layout.activity_creators)

        rvCreators.layoutManager = LinearLayoutManager(baseContext)
        adapter = CreatorsAdapter()
        rvCreators.adapter = adapter

        setNames()
    }

    private fun setNames() {
       adapter.setList(resources.getStringArray(R.array.creators).toList())
    }
}