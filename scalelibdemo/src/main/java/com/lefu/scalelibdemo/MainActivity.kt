package com.lefu.scalelibdemo

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_calculat.setOnClickListener{
            main()
        }
    }

    private fun main() {
        /**
         * double weightKg;            //体重(kg)
         *@param int heightCm;               //身高(cm)，需在 90 ~ 220cm
         *@param int age;                    //年龄(岁)，需在6 ~ 99岁
         *@param int sex;                    //性别 女0 男1
         *@param int sportsman;  //运动员模式1 非运动员模式0  默认0
         *@param int impedance; //阻抗 200-1200之间为有效脂肪数据
         */
        val height = 170
        val age = 31
        val weight = 50.0
        val sex = 0
        val sportsman = 0
        val impedance = 600

        val lfPeopleGeneral = LFPeopleGeneral(weight, height, age, sex, sportsman, impedance)
        if (lfPeopleGeneral != null) {
            lfPeopleGeneral.bodyfatParameters
            Log.d("test : ", lfPeopleGeneral.toString())

            calculat_value.text = lfPeopleGeneral.toString()
        }

    }
}