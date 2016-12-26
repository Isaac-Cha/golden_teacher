# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\yiban\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-ignorewarning
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-dontwarn com.yl.teacher.model.**
-keep class com.yl.teacher.model.** { *; } #Bean类不参与混淆
-keep class com.yl.teacher.db.** { *; }
-keep class com.yl.teacher.widget.** { *; } #自定义控件不参与混淆
-keep class com.yl.teacher.xalertdialog.** { *; } #自定义控件不参与混淆

-dontwarn com.squareup.okhttp.**
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
  public *;
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
  public *;
}
-keepclassmembers class com.yl.teacher.view.VoteDetaileActivity {
  public *;
}
-keepclassmembers class com.yl.teacher.view.BulletinDetailActivity {
  public *;
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepattributes Signature
-keepclassmembers class ** {
    public void onEvent*(***);
}
-keepattributes SourceFile,LineNumberTable
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }
-keep public class * extends com.umeng.**
-keep public class com.umeng.fb.ui.ThreadView {
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.yl.teacher.R$*{
    public static final int *;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn com.facebook.**
-keep class com.facebook.** { *; }
-dontwarn com.lidroid.**
-keep class com.lidroid.** { *; }
-dontwarn de.greenrobot.event.**
-keep class de.greenrobot.event.** { *; }
-dontwarn com.handmark.**
-keep class com.handmark.** { *; }
-dontwarn com.handmark.**
-keep class com.handmark.** { *; }
-dontwarn uk.co.**
-keep class uk.co.** { *; }
-dontwarn com.google.**
-keep class com.google.** { *; }
-dontwarn org.json.**
-keep class org.json.** { *; }
-dontwarn org.jsoup.**
-keep class org.jsoup.** { *; }
-keep class sun.misc.Unsafe { *; }

-dontwarn com.zhy.autolayout.**
-keep class com.zhy.autolayout.** { *; }
-dontwarn cn.finalteam.galleryfinal.**
-keep class cn.finalteam.galleryfinal.widget.*{*;}
-keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
-keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}
-dontwarn cn.aigestudio.datepicker.**
-keep class cn.aigestudio.datepicker.** { *; }

# 极光推送混淆
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

# 环信混淆开始
-dontwarn  com.hyphenate.**
-keep class com.hyphenate.** {*;}
-keep class com.yl.teacher.chat.db.** { *; }
-keep class com.yl.teacher.chat.domain.** { *; }
-keep class com.yl.teacher.chat.parse.** { *; }
-keep class com.yl.teacher.chat.util.** { *; }
-keep class com.yl.teacher.chat.** { *; }

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class android.support.v4.** {*;}
-keep class org.xmlpull.** {*;}
-keep class com.baidu.** {*;}
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }
-keep class com.squareup.picasso.* {*;}
-keep class com.hyphenate.* {*;}
-keep class com.hyphenate.chat.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-keep class com.hyphenate.chatuidemo.utils.SmileUtils {*;}
#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}
#华为推送
-keep class com.huawei.android.pushagent.**{*;}
-keep class com.huawei.android.pushselfshow.**{*;}
-keep class com.huawei.android.microkernel.**{*;}
#环信混淆完毕
