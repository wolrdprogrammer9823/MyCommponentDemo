package com.heng.video
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.entity.Computer
import com.heng.common.log.PERSON_COMPUTER
import com.heng.common.log.PERSON_NAME
import com.heng.common.log.PERSON_NUMBER
import com.heng.common.log.doPersonLog

@Route(path = CommonConstant.TO_CARDS_ACTIVITY)
class CardsActivity : BaseActivity() {

    @JvmField
    @Autowired(name = PERSON_NAME)
    var name: String? = null

    @JvmField
    @Autowired(name = PERSON_NUMBER)
    var number: Int = 0

    @JvmField
    @Autowired(name = PERSON_COMPUTER)
    var computer: Computer? = null

    override fun initWidget() {
        super.initWidget()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        super.initData()
        Toast.makeText(this, computer.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun getContentLayoutId(): Int = R.layout.video_activity_cards

}