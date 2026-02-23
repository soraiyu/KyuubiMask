# KyuubiMask ProGuard Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep only Android framework entry points declared in AndroidManifest.xml.
# Internal implementation classes (strategies, repositories, etc.) are intentionally
# left unspecified so R8 can obfuscate them for security.
-keep public class com.rtneg.kyuubimask.KyuubiMaskApp { *; }
-keep public class com.rtneg.kyuubimask.SettingsActivity { *; }
-keep public class com.rtneg.kyuubimask.NotificationMaskService { *; }
