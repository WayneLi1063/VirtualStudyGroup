package com.example.virtualstudygroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), OnGroupClickedListener {

    companion object {
        const val TAG = "Printing"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getGroupListFragment() == null) {
            val groupListFragment = GroupListFragment.getInstance()
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragContainer, groupListFragment, GroupListFragment.TAG
                )
                .commit()
        }

    }

    private fun getGroupListFragment() =
        supportFragmentManager.findFragmentByTag(GroupListFragment.TAG) as? GroupListFragment

    override fun onGroupClicked(group: Group) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
