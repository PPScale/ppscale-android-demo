[English Docs](README-en.md)  | [中文文档](../README.md)

#  乐福蓝牙秤SDK

##  Ⅰ . 集成方式 -两种方式

######  一、gradle自动导入方式（推荐）
    
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
            implementation 'com.peng.ppscale:ppscale-master:1.0.0'     
        }
        
######  二、直接引用aar文件

1、下载aar文件引入到module目录下的libs目录
    
2、在自己需要引入sdk的module下的build.gradle中加入引入sdk
    name与aar文件名称保持一致
    
    dependencies {
          、、、
          implementation(name: 'ppscalelibrary_master_v1.0.0_20200728', ext: 'aar')     
    }
      
## Ⅱ . 混淆

    -keepattributes Exceptions,InnerClasses,...
    -keep class chipsea.bias.v236.CSBiasAPI{
        *;
    }
    -keep class chipsea.bias.v236.CSBiasAPI$* {
        *;
    }

## Ⅲ .使用说明

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
    
       
#### ppscalelibrary的使用
    
ppscalelibrary提供了一个工具类入口BleManager，该对象为一个单例，保证你在不同控制器中调用的时候蓝牙的状态是一致的。
 
BleManager使用

       /**
        * sdk入口,实例对象 
        */
        public static BleManager shareInstance(Context context){
            // ...
        }
    
    
扫描示例：
  
             /**
             *  开始扫描蓝牙
             *
             * @param isBinding 是否是绑定设备，第一次扫描或不指定扫描设备时设置false, 扫描指定设备设置为true
             * @param deviceList 指定扫描设备列表 isBinging设置为true该方法生效
             * @param userModel 用户基本信息，包括身高、年龄、性别、重量单位（需要秤支持）
             * @param protocoInterface 扫描数据回调类
             */
           bleManager.searchDevice(false, deviceList, userModel, new BleDataProtocoInterface() {

               /**
                * 过程数据回调
                *
                * @param bodyDataModel
                */
                @Override
                public void progressData(final LFPeopleGeneral bodyDataModel) {
                    
                }
    
                /**
                 * 锁定数据回调 （由于用户可能没有传入正缺范围内的身高性别等信息或者秤重过程中的一些别的原因（穿鞋，没踩到电极片等），锁定数据中只有体重和BMI的数据。）
                 * @param bodyDataModel 测量结果
                 * @param deviceModel   设备信息
                 * @param isHeartRating 是否开始测量心率，true开始测量 false不测量或测量结束
                 */
                @Override
                public void lockedData(LFPeopleGeneral bodyDataModel, BleDeviceModel deviceModel, boolean isHeartRating) {
                    if (!isHeartRating) {
                        String weightStr = PPUtil.getWeight(userModel.unit, bodyDataModel.lfWeightKg);
                        weightTextView.setText(weightStr);
                        DataUtil.util().setBodyDataModel(bodyDataModel);
                        dismissSelf();
                    } else {
                        //等待测量心率,非心率秤直接返回false
                    }
    
                }
    
                /**
                 * 历史数据回调， 在测量完成后，支持离线数据的秤会回调，不支持不回调
                 *
                 * @param isEnd 结束标志
                 * @param bodyDataModel 存储数据的对象
                 * @param date 时间 yyyy-MM-dd HH:mm:ss
                 */
                @Override
                public void historyData(boolean isEnd, LFPeopleGeneral bodyDataModel, String date) {
                    if (!isEnd) {
                        Log.d("history", "historyData: " + bodyDataModel.toString());
                    }
                }
    
                /**
                 * 蓝牙设备信息回调,在lockedData（）执行时回调
                 * @param deviceModel  秤的设备信息
                 */
                @Override
                public void deviceInfo(BleDeviceModel deviceModel) {
    
                }
            });
            
   注意：如果需要自动循环扫描，需要在lockedData()后重新调用 bleManager.reSearchDevice()
            
            
停止扫描
    
      bleManager.stopSearch();
      
断开设备连接

     bleManager.disconnectDevice();
                 
            
    
最后你需要在离开页面的之前调用stopSearch方法。
具体的实现请参考Demo中BindingDeviceActivity和ScaleWeightActivity中的代码。
  
### LFPeopleGeneral 参数说明

      public double lfWeightKg;                                               //体重
      public double lfHeightCm;                                               //身高
      public int lfAge;                                                       //年龄
      public int lfSex;                                                       //性别
      public double lfZTwoLegs;
      public int lfBodyAge;                                                   //身体年龄
      public double lfIdealWeightKg;                                          //理想体重
      public double lfBMI;                                                    //BMI
      public int lfBMR;                                                       //BMR
      public int lfVFAL;                                                      //内脏脂肪等级
      public double lfBoneKg;                                                 //骨量含量 // 骨盐量
      public double lfBodyfatPercentage;                                      //脂肪率
      public double lfWaterPercentage;                                        //水分率
      public double lfMuscleKg;                                               //肌肉含量
      public double lfProteinPercentage;                                      //蛋白质率
      public int lfBodyType;                                                  //身体类型
      public int lfBodyScore;                                                 //身体得分
      public double lfMusclePercentage;                                       //肌肉率
      public double lfBodyfatKg;                                              //体脂量
      public Hashtable<String, String> lfBMIRatingList = new Hashtable();     //BMI健康标准字典
      public Hashtable<String, String> lfBMRRatingList = new Hashtable();     //基础代谢健康标准字典
      public Hashtable<String, String> lfVFALRatingList = new Hashtable();    //内脏脂肪等级标准字典
      public Hashtable<String, String> lfBoneRatingList = new Hashtable();    //骨量等级标准字典
      public Hashtable<String, String> lfBodyfatRatingList = new Hashtable(); //脂肪率健康标准字典
      public Hashtable<String, String> lfWaterRatingList = new Hashtable();   //水分率健康标准
      public Hashtable<String, String> lfMuscleRatingList = new Hashtable();  //肌肉量健康标准
      public Hashtable<String, String> lfProteinRatingList = new Hashtable(); //蛋白健康标准字典
      public double lfStWeightKg;
      public double lfLoseFatWeightKg;                                        //去脂体重
      public double lfControlWeightKg;                                        //体重控制
      public double lfFatControlKg;                                           //脂肪控制量
      public double lfBonePercentage;                                         //骨量率
      public double lfBodyMuscleControlKg;                                    //骨骼肌率
      public double lfVFPercentage;                                           //皮下脂肪
      public String lfHealthLevel;                                            //健康水平
      public String lfFatLevel;                                               //肥胖等级
      public double lfWHR;                                                    //腰臀比
      public String lfHealthReport;                                           //健康评估
      public String scaleType;                                                //称类型
      public String scaleName;                                                //称名称
      private int impedance;                                                  //阻抗值（加密）
      private double lfWaist;                                                 //腰围
      private double lfHipLine;                                               //臀围
      private int lfHeartRate;                                                //心率

      LFPeopleGeneral 使用方法：
    
            /**
             *
             * @param lfWeightKg  体重
             * @param lfHeightCm  身高
             * @param lfSex       性别
             * @param lfAge       年龄
             * @param impedance   阻抗 （称协议解析后会返回）非测脂数据 默认传0
             * @param lfWaist     腰围  默认传0
             * @param lfHipLine   腰臀比 默认传0
             * @param scaleType   称类型 根据称协议  cf体脂称  ce人体秤
             * @param scaleName   称名称 称蓝牙名称
             */
            LFPeopleGeneral dataModel = new LFPeopleGeneral(weight, height, sex, age, 0, 0, 0, "", "");
             
          拿到对象直接使用get方法即可。
  

## IV .版本更新说明
   
    ----1.0.0-----
    1、增加maven配置  2、增加兼容BodyFat Scale1
    
Contact Developer：
Email: yanfabu-5@lefu.cc

   
   
   
