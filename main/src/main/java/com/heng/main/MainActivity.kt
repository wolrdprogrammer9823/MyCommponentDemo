package com.heng.main
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.home.HomeFragment
import com.heng.person.PersonFragment
import com.heng.video.VideoFragment
import kotlinx.android.synthetic.main.main_activity_main.*

@Route(path = CommonConstant.TO_MAIN_ACTIVITY)
class MainActivity : BaseActivity() , BottomNavigationView.OnNavigationItemSelectedListener {


    override fun initWidget() {
        super.initWidget()
        navigation_view.setOnNavigationItemSelectedListener(this)
        navigation_view.itemBackgroundResource = android.R.color.transparent
        initHomeFragment()
    }

    override fun getContentLayoutId(): Int = R.layout.main_activity_main

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home ->{
                if (currentIndex == 0) {
                    return true
                }
                currentIndex = 0
                val transaction = supportFragmentManager.beginTransaction()
                if (fragments[0] == null) {
                    fragments[0] = HomeFragment.newInstance("home","home") as Fragment
                    fragments[0]?.let { transaction.add(R.id.fl_content, it).commit() }
                } else {
                    hideAndShowFragment(currentIndex, transaction)
                }
                return true
            }
            R.id.navigation_video ->{
                if (currentIndex == 1) {
                    return true
                }
                currentIndex = 1
                val transaction = supportFragmentManager.beginTransaction()
                if (fragments[1] == null) {
                    fragments[1] = VideoFragment.newInstance("video","video") as Fragment
                    fragments[1]?.let { transaction.add(R.id.fl_content, it).commit() }
                } else {
                    hideAndShowFragment(currentIndex, transaction)
                }
                return true
            }
            R.id.navigation_personal ->{
                if (currentIndex == 2) {
                    return true
                }
                currentIndex = 2
                val transaction = supportFragmentManager.beginTransaction()
                if (fragments[2] == null) {
                    fragments[2] = PersonFragment.newInstance("person","person") as Fragment
                    fragments[2]?.let { transaction.add(R.id.fl_content, it).commit() }
                } else {
                    hideAndShowFragment(currentIndex, transaction)
                }
                return true
            }
        }

        return false
    }

    private fun initHomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        if (fragments[0] == null) {
            fragments[0] = HomeFragment.newInstance("home","home") as Fragment
            fragments[0]?.let { transaction.add(R.id.fl_content, it).commit() }
        } else {
            hideAndShowFragment(currentIndex, transaction)
        }
    }

    private fun hideAndShowFragment(index: Int, transaction: FragmentTransaction) {
        for (i in fragments.indices) {
            if (i != index && fragments[i] != null) {
                fragments[i]?.let { transaction.hide(it) }
            }
        }
        fragments[index]?.let { transaction.show(it) }
        transaction.commit()
        currentIndex = index
    }

    private var currentIndex = 0
    private var fragments = arrayOfNulls<Fragment>(3)
}