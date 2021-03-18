package com.lefu.healthu.business.mine.binddevice

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import com.lefu.ppscale.wifi.R

object BindDeviceConfigNetHint {

    const val EVENT_STRING_SHOW_CONFIG_WIFI_HINT = "EVENT_STRING_SHOW_CONFIG_WIFI_HINT" // 显示wifi配网选项

}

class BindDeviceConfigNetHintDialog : androidx.fragment.app.DialogFragment() {

    private var onSlectListener: OnSelectListener? = null;
    private var contentView: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.App_Dialog_2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bind_device_config_net_hint_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window?.apply {
            setGravity(Gravity.CENTER)
        }
        if (window != null) {
            val params = window.attributes
            params.width = context?.resources?.displayMetrics?.widthPixels
                    ?: WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    private fun initView(view: View) {

        contentView = view.findViewById<TextView>(R.id.bind_device_config_net_hint_id_content)

        view.findViewById<TextView>(R.id.bind_device_config_net_hint_id_to_config_net).apply {
            setOnClickListener {
                onSlectListener?.onGoToConfigWiFi(this@BindDeviceConfigNetHintDialog)
            }
        }
        view.findViewById<TextView>(R.id.bind_device_select_config_net_id_submit).apply {
            setOnClickListener() {
                onSlectListener?.onCorfirm(this@BindDeviceConfigNetHintDialog)
            }
        }
        view.findViewById<TextView>(R.id.bind_device_select_config_net_id_cancel).apply {
            setOnClickListener() {
                onSlectListener?.onCancle(this@BindDeviceConfigNetHintDialog)
            }
        }

    }

    public fun setOnSelectListener(listener: OnSelectListener) {
        onSlectListener = listener;
    }

    interface OnSelectListener {

        fun onCancle(dialog: androidx.fragment.app.DialogFragment)

        fun onCorfirm(dialog: androidx.fragment.app.DialogFragment)

        fun onGoToConfigWiFi(dialog: androidx.fragment.app.DialogFragment)

    }


}