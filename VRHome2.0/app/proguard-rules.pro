# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码压缩级别
-optimizationpasses 5

#报名不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#优化  不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

#保证wasu_sdk不被混淆
-keep class com.wasu.decode.** { *; }
-dontwarn com.wasu.decode.**

#忽略警告
-ignorewarnings

##记录生成的日志数据,gradle build时在本项目根目录输出##

#apk 包内所有 class 的内部结构
-dump class_files.txt

#未混淆的类和成员
-printseeds seeds.txt

#列出从 apk 中删除的代码
-printusage unused.txt

#混淆前后的映射
-printmapping mapping.txt

########记录生成的日志数据，gradle build时 在本项目根目录输出-end######


#####混淆保护自己项目的部分代码以及引用的第三方jar包library#######
-libraryjars libs/armeabi/libandroidnet.so
-libraryjars libs/armeabi/libbspatch.so
-libraryjars libs/armeabi/libSensor.so
-libraryjars libs/armeabi/libWasuDecrypt.so
-libraryjars libs/armeabi/libwebpbackport.so

-libraryjars libs/armeabi-v7a/libandroidnet.so
-libraryjars libs/armeabi-v7a/libaudioplugingvrunity.so
-libraryjars libs/armeabi-v7a/libBlueDoveMediaRender.so
-libraryjars libs/armeabi-v7a/libbspatch.so
-libraryjars libs/armeabi-v7a/libgvrunity.so
-libraryjars libs/armeabi-v7a/libmain.so
-libraryjars libs/armeabi-v7a/libmono.so
-libraryjars libs/armeabi-v7a/libSensor.so
-libraryjars libs/armeabi-v7a/libunity.so
-libraryjars libs/armeabi-v7a/libWasuDecrypt.so
-libraryjars libs/armeabi-v7a/libwebpbackport.so
-libraryjars libs/armeabi-v7a/libzipw.so

-libraryjars libs/x86/libandroidnet.so
-libraryjars libs/x86/libaudioplugingvrunity.so
-libraryjars libs/x86/libBlueDoveMediaRender.so
-libraryjars libs/x86/libbspatch.so
-libraryjars libs/x86/libgvrunity.so
-libraryjars libs/x86/libmain.so
-libraryjars libs/x86/libmono.so
-libraryjars libs/x86/libSensor.so
-libraryjars libs/x86/libunity.so
-libraryjars libs/x86/libWasuDecrypt.so
-libraryjars libs/x86/libwebpbackport.so
-libraryjars libs/x86/libzipw.so

-keep class com.loopj.android.http.** { *; }
-keep class android.support.** { *; }
-keep class android.support.v7.widget.** { *; }
-keep class in.srain.cube.util.** { *; }

-dontwarn com.EasyMovieTexture.**
-keep class com.EasyMovieTexture.** {
*;
}

-keep class com.google.gson.** { *; }
-keep class com.nineoldadnroids.** { *; }

-keep class bitter.jnibridge.** { *; }
-keep class com.uniry3d.player.** { *; }
-keep class org.fmod.** { *; }
-keep class com.google.** { *; }

-keep class com.vrseen.utilforunity.** { *; }
#-keep class com.VRSeen.sensor.** { *; }
-keep class apache.commons.math3.** { *; }
-keep class android.taobao.windvane.** { *; }

-keep class com.** { *; }
-keep class de.greenrobot.event.** { *; }
#-keep class net.wasu.tools.** { *; }
#-keep class wasu.** { *; }

-keep class com.android.vending.expansion.zipfile.** { *; }


#友盟
#-keep class com.umeng.**{*;}
#项目特殊处理代码

#忽略警告
-dontwarn com.vrseen.vrstore**

-keep class android.** {*;}

#保留完整的包
#-keep class com.vrseen.vrstore**{
#    *;
#}
#
#-keep class com.lidroid.xutils**{
#    *;
#}
#
#-keep class in.srain.cube**{
#    *;
#}

#保持sharsdk不被混淆
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class m.framework.**{*;}

#如果引用了v4或者v7包
-dontwarn android.**

####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

-keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

#保持自定义控件类不被混淆
-keepclasseswithmembers class *{

    public <init>(android.content.Context,android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity{

    public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

 #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
 #  public static **[] values();
 #  public static ** valueOf(java.lang.String);
 #}

 -keepclassmembers class * {
     public void *ButtonClicked(android.view.View);
 }

#不混淆资源类
 -keepclassmembers class **.R$* {
     public static <fields>;
 }

#避免混淆泛型 如果混淆报错建议关掉
 -keepattributes Signature

 -keepattributes EnclosingMethod

-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#Umeng SDK混淆配置
#-keepclassmembers class * {
#   public <init>(org.json.JSONObject);
#}

#-keep class com.umeng.**

#-keep public class com.idea.fifaalarmclock.app.R$*{
#    public static final int *;
#}

#-keep public class com.umeng.fb.ui.ThreadView {
#}

#-dontwarn com.umeng.**

#-dontwarn org.apache.commons.**

#-keep public class * extends com.umeng.**

#-keep class com.umeng.** {*; }

#-libraryjars libs/tncrash.jar
-dontwarn com.testin.agent.**
-keep class com.testin.agent.** {*;}

-keepattributes SourceFile, LineNumberTable