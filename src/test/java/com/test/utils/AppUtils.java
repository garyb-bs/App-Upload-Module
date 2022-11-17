package com.test.utils;

import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.restassured.RestAssured.*;

public final class AppUtils {

    private static Response response;

    private AppUtils() {}

    public static void setupAuthPrep(String basePath) {
        PreemptiveBasicAuthScheme authenticationScheme = new PreemptiveBasicAuthScheme();
        authenticationScheme.setUserName(System.getenv("BROWSERSTACK_USERNAME"));
        authenticationScheme.setPassword(System.getenv("BROWSERSTACK_ACCESS_KEY"));
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://api-cloud.browserstack.com")
                .setBasePath(basePath)
                .setAuth(authenticationScheme)
                .build();
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    public static void updateJSON(String appCustomID, String testSuiteCustomID, String filePath) throws Exception {
        File f = new File(filePath);
        if (f.exists()){
            InputStream is = new FileInputStream(filePath);
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            //System.out.println(jsonTxt);
            JSONObject json = new JSONObject(jsonTxt);
            String a = json.getString("app");
            System.out.println(a);
            json.put("app", appCustomID);
            json.put("testSuite", testSuiteCustomID);
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(json.toString());
            writer.close();
        }
    }

    public static void uploadAppiumApp(String appCustomID, boolean fileUpload, boolean urlUpload, File appFile, String appFileURL) {
        List<String> customIds = get("recent_apps/" + appCustomID).jsonPath().getList("custom_id");
        if (customIds == null) {
            System.out.println("Uploading app ...");
            if (fileUpload && !urlUpload) {
                doFileUpload(appFile, appCustomID, "upload");
            } else if (!fileUpload && urlUpload) {
                doURLUpload(appFileURL, appCustomID, "upload");
            }
        } else {
            System.out.println("Using previously uploaded app...");
        }
    }

    public static void uploadApp(String appCustomID, boolean fileUpload, boolean urlUpload, File appFile, String appFileURL) {
        List<String> appCustomIds = get("apps").jsonPath().getList("apps.custom_id");
        if (appCustomIds.isEmpty() || !appCustomIds.contains(appCustomID)) {
            System.out.println("Uploading app ...");
            if (fileUpload && !urlUpload) {
                doFileUpload(appFile, appCustomID, "app");
            } else if (!fileUpload && urlUpload) {
                doURLUpload(appFileURL, appCustomID, "app");
            }
        } else {
            System.out.println("Using previously uploaded app...");
        }
    }

    public static void uploadTestSuite(String testSuiteCustomID, boolean fileUpload, boolean urlUpload,  File testSuiteFile, String testSuiteURL) {
        List<String> testSuiteCustomIds = get("test-suites").jsonPath().getList("test_suites.custom_id");
        if (testSuiteCustomIds.isEmpty() || !testSuiteCustomIds.contains(testSuiteCustomID)) {
            System.out.println("Uploading test suite ...");
            if (fileUpload && !urlUpload) {
                doFileUpload(testSuiteFile, testSuiteCustomID, "test-suite");
            } else if (!fileUpload && urlUpload) {
                doURLUpload(testSuiteURL, testSuiteCustomID, "test-suite");
            }

        } else {
            System.out.println("Using previously uploaded test suite...");
        }
    }

    private static String getAppUrl() {
        JsonPath json = response.getBody().jsonPath();
        System.out.println("App Url: " + json.get("app_url"));
        return json.get("app_url");
    }

    private static void doURLUpload(String url, String customID, String app) {
        response = given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("url", url, "text")
                .param("custom_id", customID)
                .post(app);

        getAppUrl();
    }

    private static void doFileUpload(File fileToUpload, String customID, String app) {
        response = given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", fileToUpload)
                .param("custom_id", customID)
                .post(app);

        getAppUrl();
    }

}
