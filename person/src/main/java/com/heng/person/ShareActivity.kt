package com.heng.person
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

@Route(path = CommonConstant.TO_SHARE_ACTIVITY)
class ShareActivity : BaseActivity() {

    private var tag = ShareActivity::class.java.simpleName

    @JvmField
    @Autowired(name = PERSON_NAME)
    var name: String? = null

    @JvmField
    @Autowired(name = PERSON_NUMBER)
    var number: Int = 0

    @JvmField
    @Autowired(name = PERSON_COMPUTER)
    var computer: Computer? = null

    override fun initBefore() {
        super.initBefore()
        ARouter.getInstance().inject(this)
    }

    override fun initData() {
        super.initData()
        doPersonLog(tag, computer.toString())
        Toast.makeText(this, computer.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun getContentLayoutId(): Int = R.layout.person_activity_share
}
