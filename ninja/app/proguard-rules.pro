# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



 -dontwarn org.jetbrains.annotations.**
 -keep class kotlin.Metadata { *; }
 -keepclassmembers class com.myapp.packagnename.** { <init>(...); <fields>;}
 -keep @android.support.annotation.Keep class * {*;}


 #EventBus
 -keepattributes *Annotation*
 -keepclassmembers class * {
     @org.greenrobot.eventbus.Subscribe <methods>;
 }
 -keep enum org.greenrobot.eventbus.ThreadMode { *; }


-keep class com.umeng.** {*;}
-keep class com.ninjahome.ninja.model.bean.** {*;}

-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.** {*;}
-keep class com.ut.** {*;}
-keep class com.uc.** {*;}
-keep class com.ta.** {*;}


-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#SDK 9.2.4?????????????????????oaid???????????????????????????????????????????????????oaid???????????????????????????????????????
-keep class com.zui.**{*;}
-keep class com.miui.**{*;}
-keep class com.heytap.**{*;}
-keep class a.**{*;}
-keep class com.vivo.**{*;}


#????????????
-keep class org.android.agoo.xiaomi.MiPushBroadcastReceiver {*;}
-dontwarn com.xiaomi.push.**

#????????????
-ignorewarnings
-keepattributes *Annotation*, Exceptions, InnerClasses, Signature, SourceFile, LineNumberTable
-keep class com.hianalytics.android.** {*;}
-keep class com.huawei.updatesdk.** {*;}
-keep class com.huawei.hms.** {*;}
#oppo
-keep public class * extends android.app.Service


-keepclassmembers class ** {
    public void on*Event(...);
}

-keep class com.tencent.tencentmap.**{*;}
-keep class com.tencent.map.**{*;}
-keep class com.tencent.beacontmap.**{*;}
-keep class navsns.**{*;}
-dontwarn com.qq.**
-dontwarn com.tencent.**

-keepclassmembers class ** {
    public void on*Event(...);
}

-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}

-keep public class com.tencent.location.**{
    public protected *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

