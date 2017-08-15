package com.test;

public class FilePaths {
    private static int os = 1; //integer to determine os of the test environment (0 windows, 1 mac, 2 linux)
    public static String getPath(String s) {
        char s1[]=s.toCharArray();

        if(os!=0){
            for(int i=0;i<s.length();i++){
                if(s.charAt(i)=='\\')
                    s1[i]='/';
            }
        }
        String r="";
        for(int i=0;i<s1.length;i++){
            r+=s1[i];
        }
        return r;
    }

    public static int checkOS() {
        String n=System.getProperty("os.name");
        n = n.toUpperCase();
        if (n.contains("WINDOWS")) {os = 0;}
        if (n.contains("OSX")) {os = 1;}
        if (n.contains("LINUX")) {os = 2;}
        return os;
    }
}
