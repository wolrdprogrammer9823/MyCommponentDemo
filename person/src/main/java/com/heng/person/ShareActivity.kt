package com.heng.person
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.person_activity_share)
        ARouter.getInstance().inject(this)
        doPersonLog(tag, computer.toString())
        //Toast.makeText(this, "name:$name,number:$number", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, computer.toString(), Toast.LENGTH_SHORT).show()
    }
}
