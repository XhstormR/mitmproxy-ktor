-optimizationpasses 5

-adaptclassstrings
-adaptresourcefilenames
-allowaccessmodification

-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-repackageclasses com.xhstormr.app

-keepattributes *Annotation*

-keepclassmembers enum * {
    public static **[] values();
}

-keepclasseswithmembers class * {
    public static void main(java.lang.String[]);
}

-dontwarn org.codehaus.**

-printusage ../build/usage.txt
-printmapping ../build/mapping.txt
