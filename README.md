[English Docs](doc/README-en.md)  |  [中文文档](README.md)

#  乐福蓝牙秤SDK

注意：ppscale是蓝牙连接逻辑以及数据解析逻辑。另一个是Android层蓝牙封装库。集成SDK的开发者无需关心里面的逻辑实现。在开发者集成的时候，请采用从maven下载的库的集成方式集成。建议开发者查看README.md文档，完成集成。

##  Ⅰ . 集成方式 -两种方式

#####  gradle自动导入方式
    
1、在project项目目录下的build.gradle中加入
    
        allprojects {
            repositories {
                、、、
                 maven { url 'http://nexus.lefuenergy.com/repository/maven-public' }
            }
        }
      
2、在需要引入sdk的module下的build.gradle中加入
        //根据不同的分支请采用不同的artifactId，格式是：ppscale-分支名
        //下面是master分支的集成方式，已集成相应的so文件
       
        dependencies {
            、、、
                implementation 'com.peng.ppscale:ppscale-new-master:0.0.3.6'     
        }
        
        
## Ⅱ .使用说明

* 由于需要蓝牙连接，Demo需要真机运行。

* 在Android 6.0及以上系统版本，启动扫描前，需确保开启 
    
    1、定位权限  
    2、定位开关  
    3、蓝牙开关 
    
* 如果需要体重值以外的信息需要输入身高、年龄、性别并且光脚上秤。
    
* 身高的取值范围：30-220厘米；年龄的取值范围：10-99岁；单位0代表千克，1代表斤，2代表镑；性别1代表男，0代表女；用户组取值范围0-9（特定的秤需要这个值）
    
* 使用Demo过程中需要您打开蓝牙，同时给予Demo定位权限

    1. 绑定设备 - 在这个控制器在被实例化后会开始扫描附近的外设，并将您的外设做一个记录。

    2. 上秤称重 - 这个控制器在被实例化后也会开始扫描附近的外设，通过过滤去连接已绑定过的设备。所以只有被绑定过后才能去进行上秤称重，否则无法接收到数据。

    3. 设备管理 - 这个控制器会用列表的方式展示你在“绑定设备”页面绑定的外设。你可以通过长按的方式去删除已绑定设备。

    4. 在“绑定设备”和“上秤称重”页面接收到外设返回的数据后，会自动停止扫描并断开与外设的连接，然后把数据通过回调的方式传回“主页信息”更新体重一栏，具体的数据可以去“ 数据详情”页查看。
    
       
## Ⅲ .ppscalelib在蓝牙设备的使用
    
###### 1.1 绑定或扫描指定蓝牙设备

        //绑定设备和扫描设备的区别在于  searchType  0绑定设备 1扫描指定设备
        //setProtocalFilterImpl() 接收数据回调接口，过程数据、锁定数据、历史数据，
        //setDeviceList()函数的参数为不为空，绑定需传null(或不调用)，扫描指定设备请传指定设备的List<DeviceModel>。绑定
        //setBleOptions()蓝牙参数配置
        //setBleStateInterface() 需要参数PPBleStateInterface，蓝牙状态监控回调和系统蓝牙状态回调
        //startSearchBluetoothScaleWithMacAddressList（）启动扫描设备
        
       /**
        * sdk入口,实例对象 
        */
       private void bindingDevice() {
               if (searchType == 0) {
                   ppScale = new PPScale.Builder(this)
                           .setProtocalFilterImpl(getProtocalFilter())
                           .setBleOptions(getBleOptions())
       //                    .setDeviceList(null)
                           .setBleStateInterface(bleStateInterface)
                           .build();
                   ppScale.startSearchBluetoothScaleWithMacAddressList();
               } else {
                   List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
                   List<String> addressList = new ArrayList<>();
                   for (DeviceModel deviceModel : deviceList) {
                       addressList.add(deviceModel.getDeviceMac());
                   }
                   ppScale = new PPScale.Builder(this)
                           .setProtocalFilterImpl(getProtocalFilter())
                           .setBleOptions(getBleOptions())
                           .setDeviceList(addressList)
                           .setBleStateInterface(bleStateInterface)
                           .build();
                   ppScale.startSearchBluetoothScaleWithMacAddressList();
               }

注意：如果需要自动循环扫描，需要在lockedData()后重新调用 ppScale.startSearchBluetoothScaleWithMacAddressList()
    
###### 1.2  BleOptions 蓝牙参数配置

    //配置需要扫描的秤类型 默认全部，可选FEATURES_WEIGHT, //体重秤
                                FEATURES_FAT, //脂肪秤
                                FEATURES_HEART_RATE, //心率秤
                                FEATURES_HISTORY, //离线+脂肪秤
                                FEATURES_BMDJ, //闭目单脚+脂肪秤
                                FEATURES_ALL,  //全类型
                                FEATURES_CUSTORM //自定义 选则自定义需要设置PPScale的setDeviceList()
    setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_ALL)
    
###### 1.3  PPBleStateInterface，蓝牙状态监控回调和系统蓝牙状态回调

    //包含两个回调方法，一个是蓝牙状态监控，一个是系统蓝牙回调
   
     PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
            //蓝牙状态监控
            @Override
            public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState) {
                if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                    Logger.d("设备已连接");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                    Logger.d("设备连接中");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                    Logger.d("设备已断开");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
                    Logger.d("停止扫描");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                    Logger.d("扫描中");
                } else {
                    Logger.e("蓝牙状态异常");
                }
            }
    
            //系统蓝牙回调
            @Override
            public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
                if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                    Logger.e("系统蓝牙断开");
                    Toast.makeText(BindingDeviceActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
                } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                    Logger.d("系统蓝牙打开");
                    Toast.makeText(BindingDeviceActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
                } else {
                    Logger.e("系统蓝牙异常");
                }
            }
        };
    
###### 1.4  ProtocalFilterImpl

    //根据需求实现接口
    //监听过程数据setPPProcessDateInterface()
    //监听锁定数据setPPLockDataInterface()
    //监听历史数据setPPHistoryDataInterface()
    //setUserModel() 参数PPUserModel 正常情况， setUserModel()是必须的，
   
     ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
            protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            //过程数据
                @Override
                public void monitorProcessData(PPBodyBaseModel bodyBaseModel) {
                    Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
                    //体重
                    String weightStr = PPUtil.getWeight(unitType, bodyBaseModel.getPpWeightKg());
                    
                }
            });
            protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
                //监听锁定数据
                @Override
                public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {  
                    if (bodyFatModel.isHeartRateEnd()) {
                        if (bodyFatModel != null) {
                            Logger.d("monitorLockData  bodyFatModel weightKg = " + bodyFatModel.getPpWeightKg());
                        } else {
                            Logger.d("monitorLockData  bodyFatModel heartRate = " + bodyFatModel.getPpHeartRate());
                        }
                        String weightStr = PPUtil.getWeight(unitType, bodyFatModel.getPpWeightKg());
                        if (weightTextView != null) {
                            weightTextView.setText(weightStr);
                            showDialog(deviceModel, bodyFatModel);
                        }
                    } else {
                        Logger.d("正在测量心率");
                    }
                }
            });
    
            if (searchType != 0) {
                //Do not receive offline data when binding the device， If you need to receive offline data, please implement this interface
                protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
                    @Override
                    public void monitorHistoryData(PPBodyFatModel bodyBaseModel, boolean isEnd, String dateTime) {
                        if (bodyBaseModel != null) {
                            Logger.d("ppScale_ isEnd = " + isEnd + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyBaseModel.getPpWeightKg());
                        } else {
                            Logger.d("ppScale_ isEnd = " + isEnd);
                        }
                    }
                });
            }
            protocalFilter.setUserModel(userModel);
    
###### 1.5  PPUserModel   用户基础信息 
        
        userHeight、age、sex必须是真实的 
        userHeight范围是100-220cm
        age范围是10-99
        sex 0为女 1为男   
             
            
            
###### 1.6 蓝牙操作相关

预留蓝牙操作对象

     BluetoothClient client = ppScale.getBleClient();

停止扫描

       ppScale.stopSearch();
      
断开设备连接

        ppScale.disConnect();
          
最后你需要在离开页面的之前调用stopSearch方法。
具体的实现请参考Demo中BindingDeviceActivity和ScaleWeightActivity中的代码。
  
###### 1.7  PPBodyFatModel 参数说明

    protected int impedance;                                                  //阻抗值（加密）
    //    protected float ppZTwoLegs;                                         //脚对脚阻抗值(Ω), 范围200.0 ~ 1200.0
    protected double ppWeightKg;                                              //体重
    protected int ppHeartRate;                                                //心率
    protected int scaleType;                                                  //称类型
    protected boolean isHeartRateEnd = true;                                  //心率结束符
    protected String scaleName;                                               //称名称

    protected PPUserModel userModel;
    protected PPUserSex ppSex;                                                //性别
    protected double ppHeightCm;                                              //身高(cm)，需在 90 ~ 220cm
    protected int ppAge;                                                      //年龄(岁)，需在6 ~ 99岁
    protected double ppProteinPercentage;                                     //蛋白质,分辨率0.1, 范围2.0% ~ 30.0%
    protected int ppBodyAge;                                                  //身体年龄,6~99岁
    protected double ppIdealWeightKg;                                         //理想体重(kg)
    protected double ppBMI;                                                   //BMI 人体质量指数, 分辨率0.1, 范围10.0 ~ 90.0
    protected int ppBMR;                                                      //Basal Metabolic Rate基础代谢, 分辨率1, 范围500 ~ 10000
    protected int ppVFAL;                                                     //Visceral fat area leverl内脏脂肪, 分辨率1, 范围1 ~ 60
    protected double ppBoneKg;                                                //骨量(kg), 分辨率0.1, 范围0.5 ~ 8.0
    protected double ppBodyfatPercentage;                                     //脂肪率(%), 分辨率0.1, 范围5.0% ~ 75.0%
    protected double ppWaterPercentage;                                       //水分率(%), 分辨率0.1, 范围35.0% ~ 75.0%
    protected double ppMuscleKg;                                              //肌肉量(kg), 分辨率0.1, 范围10.0 ~ 120.0
    protected int ppBodyType;                                                 //身体类型
    protected int ppBodyScore;                                                //身体得分 50 ~ 100分
    protected double ppMusclePercentage;                                      //肌肉率(%),分辨率0.1，范围5%~90%
    protected double ppBodyfatKg;                                             //脂肪量(kg)
    protected double ppBodystandard;                                          //标准体重(kg)
    protected double ppLoseFatWeightKg;                                       //去脂体重(kg)
    protected double ppControlWeightKg;                                       //体重控制(kg)
    protected double ppFatControlKg;                                          //脂肪控制量(kg)
    protected double ppBonePercentage;                                        //骨骼肌率(%)
    protected double ppBodyMuscleControlKg;                                   //肌肉控制量(kg)
    protected double ppVFPercentage;                                          //皮下脂肪(%)
    protected PPBodyEnum.PPBodyGrade ppBodyHealth;                            //健康评估
    protected PPBodyEnum.PPBodyFatGrade ppFatGrade;                           //肥胖等级
    protected PPBodyEnum.PPBodyHealthAssessment ppBodyHealthGrade;            //健康等级
    protected PPBodyEnum.PPBodyfatErrorType ppBodyfatErrorType;                //错误类型

   
  注意：在使用时拿到对象，请调用对应的get方法来获取对应的值
  
###### 1.8  PPBodyEnum 参数说明 
  
  1.错误类型 PPBodyEnum.PPBodyfatErrorType
  
      PPBodyfatErrorTypeNone(0),         //!< 无错误(可读取所有参数)
      PPBodyfatErrorTypeImpedance(-1),    //!< 阻抗有误,阻抗有误时, 不计算除BMI/idealWeightKg以外参数(写0)
      PPBodyfatErrorTypeAge(-1),          //!< 年龄参数有误，需在 6   ~ 99岁(不计算除BMI/idealWeightKg以外参数)
      PPBodyfatErrorTypeWeight(-2),       //!< 体重参数有误，需在 10  ~ 200kg(有误不计算所有参数)
      PPBodyfatErrorTypeHeight(-3);       //!< 身高参数有误，需在 90 ~ 220cm(不计算所有参数)

  2.健康评估 PPBodyEnum.PPBodyfatErrorType
  
      PPBodyGradeThin(0),             //!< 偏瘦型
      PPBodyGradeLThinMuscle(1),      //!< 标准型
      PPBodyGradeMuscular(2),         //!< 超重型
      PPBodyGradeLackofexercise(3);   //!< 肥胖型

  3.肥胖等级 PPBodyEnum.PPBodyfatErrorType
  
      PPBodyGradeFatOne(0),             //!< 肥胖1级
      PPBodyGradeLFatTwo(1),            //!< 肥胖2级
      PPBodyGradeFatThree(2),           //!< 肥胖3级
      PPBodyGradeFatFour(-1);           //!< 参数错误
  
  4.健康等级 PPBodyEnum.PPBodyfatErrorType
  
      PPBodyAssessment1(0),          //!< 健康存在隐患
      PPBodyAssessment2(1),          //!< 亚健康
      PPBodyAssessment3(2),          //!< 一般
      PPBodyAssessment4(3),          //!< 良好
      PPBodyAssessment5(4),          //!< 非常好
      PPBodyAssessmentError(-1);          //!< 参数错误
  

## IV .ppscalelib在WIFI设备的使用

###### 2.0 蓝牙配网

具体可参考：{@link BleConfigWifiActivity}
        
       
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        //监听配网结果 setConfigWifiInterface()
        protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface() {
            
            /**
             * wifi设备配网成功并获取到SN
             *
             * @param sn 设备识别码
             */
             @Override
             public void monitorConfigState(String sn) {
                 //拿到sn 处理业务逻辑
                 Logger.e("xxxxxxxxxxxx-" + sn);
             }
         });
        
        ppScale = new PPScale.Builder(this)
                   .setProtocalFilterImpl(protocalFilter)
                   .setBleOptions(getBleOptions())
                   .setBleStateInterface(bleStateInterface)
                   .build();
        ppScale.startSearchBluetoothScaleWithMacAddressList();

WIFI参数配置

        /**
            * 参数配置 绑定时请确保WIFI是2.4G，并且账号密码正确
            *
            * @param password     WIFI密码
            * @param featuresFlag 具备的能力，WIFI秤{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI}                      
            * @parm ssid          WIFI账号  不可为空
            * @return
            */
           private BleOptions getBleOptions() {
               return new BleOptions.Builder()
                       .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                       .setPassword("12345678")
                       .setSsid("IT05-2.4G")
                       .build();
           }

蓝牙状态监听，请参考本文档   1.3  PPBleStateInterface       


## V .版本更新说明
   
    ----0.0.1-----
    1、增加maven配置  2、增加兼容BodyFat Scale1
    ----0.0.2-----
    1、增加蓝牙WIFI配网功能
    ----0.0.3-----
    1、优化蓝牙配网功能  2、提高广播数据兼容性
    ----0.0.3.4-----
    1、增加Health Scale5
    ----0.0.3.6-----
    1、蓝牙配网增加蓝牙设备信息返回
    ----0.0.3.7-----
    1、增加LF_SC脂肪广播秤
    ----0.0.3.9-----
    1、增加PPBodyEnum.kt 增加错误类型输出、修改身体类型、肥胖等级、健康等级回调方式
    ----0.0.4-----
    1、增加婴儿模式
    
Contact Developer：
Email: yanfabu-5@lefu.cc

   
   
   
