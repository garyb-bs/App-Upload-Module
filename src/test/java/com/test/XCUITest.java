package com.test;

import com.test.utils.AppUtils;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;

public class XCUITest {
    // path to JSON file (does not need to be updated)
    private static String jsonFilePath = "src/test/resources/app/xcuitest.json";

    // Provide unique custom IDs (if IDs exist, it will use the
    private static String appCustomID = "SampleAppModuleFile4";
    private static String testSuiteCustomID = "SampleTestSuiteModuleFile4";

    // Provide the URLs of your app and tests files if doing upload by URL
    private static String appFileURL = "https://www.browserstack.com/app-automate/sample-apps/ios/BrowserStack-SampleApp.ipa";
    private static String testSuiteURL = "https://www.browserstack.com/app-automate/sample-apps/ios/BrowserStack-SampleXCUITest.zip";

    // Set the path to the files on your machine
    private static File appFile = new File("/Users/garybehan/Downloads/BrowserStack-SampleApp.ipa");
    private static File testSuiteFile = new File("/Users/garybehan/Downloads/BrowserStack-SampleXCUITest.zip");

    // Set one to true and one to false depending on the type of upload
    private boolean doFileUpload = true;
    private boolean doUrlUpload = false;

    @BeforeSuite
    public void setup() {
        AppUtils.setupAuthPrep("app-automate/xcuitest/v2");
    }

    @BeforeTest
    public void uploadAppAndTestSuite() {
        AppUtils.uploadApp(appCustomID, doFileUpload, doUrlUpload, appFile, appFileURL);
        AppUtils.uploadTestSuite(testSuiteCustomID, doFileUpload, doUrlUpload, testSuiteFile, testSuiteURL);
    }

    @Test
    public void xcuiTest() throws Exception {
        AppUtils.updateJSON(appCustomID, testSuiteCustomID, jsonFilePath);
        System.out.println("Executing test suite...");
        String message = given()
                .header("Content-Type", "application/json")
                .body(new File("src/test/resources/app/xcuitest.json"))
                .post("build")
                .jsonPath()
                .get("message");
        assertEquals(message, "Success", "Build did not start");
    }
}