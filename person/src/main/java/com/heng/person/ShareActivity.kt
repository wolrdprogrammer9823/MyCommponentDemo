package com.heng.person
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import kotlinx.android.synthetic.main.person_activity_share.*

@Route(path = CommonConstant.TO_SHARE_ACTIVITY)
class ShareActivity : BaseActivity() {

    private var tag = ShareActivity::class.java.simpleName

    override fun initBefore() {
        super.initBefore()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {

        super.initData()

        val dataList = floatArrayOf(12F, 24F, 45F, 56F, 89F, 70F, 49F, 22F, 23F, 10F, 12F, 3F)
        val horizontalAxis = arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        bar_chart_view.mMax = 89
        bar_chart_view.mDataList = dataList
        bar_chart_view.mHorizontalAxis = horizontalAxis
        bar_chart_view.mEnableGrowIncrement = false
    }

    override fun getContentLayoutId(): Int = R.layout.person_activity_share
}
