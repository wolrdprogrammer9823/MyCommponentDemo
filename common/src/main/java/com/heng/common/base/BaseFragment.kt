package com.heng.common.base
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    private var rootView :View? = null
    private var mIsFirstInit = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initArguments(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            val root = inflater.inflate(getContentLayoutId(), container, false)
            initWidget()
            rootView = root
        } else {
            rootView?.let {
                (it.parent as ViewGroup).removeView(it)
            }
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mIsFirstInit) {
            /*初始一次之后就不再初始化*/
            firstInit()
            mIsFirstInit = false
        }
        initData()
    }

    open fun initArguments(bundle: Bundle?) {}

    /*初始化控件*/
    open fun initWidget() {}

    /*首次初始化*/
    open fun firstInit() {}

    /*初始化数据*/
    open fun initData() {}

    abstract fun getContentLayoutId() : Int

    /*按返回键时触发调用 false标识activity处理相关的逻辑  true当前fragment已经处理此逻辑*/
    open fun onBackPressed(): Boolean {
        return false
    }
}
