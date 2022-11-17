package com.test;

import com.test.utils.AppUtils;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;

public class AppiumTest {

    // Provide unique custom IDs (if IDs exist, it will use the
    private static String appCustomID = "TheCalculatorAppAndroid";

    // Provide the URLs of your app and tests files if doing upload by URL
    private static String appFileURL = "https://www.browserstack.com/app-automate/sample-apps/android/Calculator.apk";

    // Set the path to the files on your machine
    private static File appFile = new File("/Users/garybehan/Downloads/Calculator.apk");

    // Set one to true and one to false depending on the type of upload
    private boolean doFileUpload = true;
    private boolean doUrlUpload = false;

    @BeforeSuite
    public void setup() {
        AppUtils.setupAuthPrep("app-automate");
    }

    @Test
    public void uploadApp() {
        AppUtils.uploadAppiumApp(appCustomID, doFileUpload, doUrlUpload, appFile, appFileURL);
    }
}
