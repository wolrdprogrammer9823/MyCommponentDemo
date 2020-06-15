package com.heng.home
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.entity.Computer
import com.heng.common.log.PERSON_COMPUTER
import com.heng.common.log.PERSON_LOG
import com.heng.common.log.PERSON_NAME
import com.heng.common.log.PERSON_NUMBER
import com.heng.common.tool.JsonServiceImpl
import kotlinx.android.synthetic.main.home_fragment_home.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.home_fragment_home, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
    }

    private var rootView : View? = null
}
