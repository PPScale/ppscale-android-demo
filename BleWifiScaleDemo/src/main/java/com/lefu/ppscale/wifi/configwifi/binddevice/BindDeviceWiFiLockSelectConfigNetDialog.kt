package com.lefu.healthu.business.mine.binddevice

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.lefu.ppscale.wifi.R

class BindDeviceWiFiLockSelectConfigNetDialog : DialogFragment() {

    private var onSlectListener: OnSelectListener? = null;
    private var contentView: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.App_Dialog_2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bind_device_wifi_select_config_net_dialog, container, false)
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

        contentView = view.findViewById<TextView>(R.id.content)

        view.findViewById<TextView>(R.id.bind_device_select_config_net_id_to_config_net).apply {
            setOnClickListener {
                onSlectListener?.onGoToConfigWiFi(this@BindDeviceWiFiLockSelectConfigNetDialog)
            }
        }
        view.findViewById<TextView>(R.id.bind_device_select_config_net_id_submit).apply {
            setOnClickListener() {
                onSlectListener?.onCorfirm(this@BindDeviceWiFiLockSelectConfigNetDialog)
            }
        }
        view.findViewById<TextView>(R.id.bind_device_select_config_net_id_cancel).apply {
            setOnClickListener() {
                onSlectListener?.onCancle(this@BindDeviceWiFiLockSelectConfigNetDialog)
            }
        }

        showDialog(arguments?.getString("content", "") ?: "")

    }

    private fun showDialog(weight: String) {
        if (!TextUtils.isEmpty(weight)) {
            var content = getString(R.string.lock_data_received) + weight + getString(R.string.go_to_config_wifi)
            contentView?.setText(content)
        }
    }

    public fun setOnSelectListener(listener: OnSelectListener) {
        onSlectListener = listener;
    }

    interface OnSelectListener {

        fun onCancle(dialog: DialogFragment)

        fun onCorfirm(dialog: DialogFragment)

        fun onGoToConfigWiFi(dialog: DialogFragment)

    }


}