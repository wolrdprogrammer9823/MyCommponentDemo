package com.heng.home
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseFragment
import com.heng.common.entity.Computer
import com.heng.common.log.PERSON_COMPUTER
import com.heng.common.log.PERSON_NAME
import com.heng.common.log.PERSON_NUMBER
import kotlinx.android.synthetic.main.home_fragment_home.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : BaseFragment() {

    private var param1: String? = null
    private var param2: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initArguments(bundle: Bundle?) {
        bundle?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun firstInit() {
        super.firstInit()
    }

    override fun initWidget() {
        super.initWidget()
    }

    override fun initData() {
        val computer = Computer(1090,"Dell 101",5005.0)
        btn_share.setOnClickListener {
            ARouter.getInstance()
                .build(CommonConstant.TO_SHARE_ACTIVITY)
                .withString(PERSON_NAME, "tom")
                .withInt(PERSON_NUMBER, 1180)
                .withObject(PERSON_COMPUTER, computer)
                .greenChannel()
                .navigation()
        }

        val mAxisTexts = arrayListOf("1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12")
        val dataSet = intArrayOf(12, 24, 45, 56, 89, 70, 49, 22, 23, 10, 12, 3)
        curve_view.mMax = 89
        curve_view.dataSet = dataSet
        curve_view.mAxisTexts = mAxisTexts
    }

    override fun getContentLayoutId(): Int = R.layout.home_fragment_home
}
