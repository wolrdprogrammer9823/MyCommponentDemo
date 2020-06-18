package com.heng.person
import android.content.Intent
import android.os.Bundle
import com.heng.common.base.BaseFragment
import kotlinx.android.synthetic.main.person_fragment_person.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PersonFragment : BaseFragment() {

    private var param1: String? = null
    private var param2: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonFragment().apply {
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

    override fun getContentLayoutId(): Int = R.layout.person_fragment_person

    override fun firstInit() {
        super.firstInit()
        btn_next.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), ShareActivity::class.java))

        }
    }

    override fun initData() {
        super.initData()
        val mAxis = arrayListOf("1", "2", "3", "4", "5", "6",
                                                  "7", "8", "9", "10", "11", "12")
        val dataSet = intArrayOf(12, 24, 45, 56, 89, 70, 49, 22, 23, 10, 12, 3)
        line_chart_view.mAxis = mAxis
        line_chart_view.dataSet = dataSet
        line_chart_view.mMax = 89
    }
}
