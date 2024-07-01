package com.azure.migration.java.copilot.service;


import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DetectTools {

    public static final String AppCatReportUrl = "report-assessment";
    public static final String CfManifest = "manifest.yml";

    Map<String, String> scanInput(String sourcePath,boolean force) throws Exception {
        String tmpDirsLocation = System.getProperty("java.io.tmpdir");
        File pilotFolder = new File(tmpDirsLocation, "migration-pilot");
        if (!pilotFolder.exists()) {
            pilotFolder.mkdir();
        }

        File metaData = new File(pilotFolder, generateMD5Hash(sourcePath));

        if (metaData.exists()) {
            if (force) {
                System.out.println("The report data exists. Force to rebuild it " + metaData.getAbsolutePath());
                metaData.deleteOnExit();
            } else {
                System.out.println("The report data exists. Skip scanning and use the existing report at " + metaData.getAbsolutePath());
                return scanFolder(metaData);
            }
        }else{
            System.out.println("The report data does not exist. Start scanning the application.");
        }

        //check if cmd "appcat -v" can be successfully executed
        System.out.println("****************************************************************************************");
        System.out.println("Use AppCat the Scan the code");
        System.out.println("****************************************************************************************");

        boolean isAppcatExists;
        try {
            Process process = getCommand("appcat -v").start();
            process.waitFor();
            isAppcatExists = process.exitValue() == 0;
        } catch (Exception e) {
            throw new Exception("Please install appcat first. You can download it from https://aka.ms/appcat");
        }

        String reportUrl = new File(metaData, AppCatReportUrl).getAbsolutePath();
        String command = "appcat --input " + sourcePath
                + " --output " + reportUrl
                + " --target azure-appservice "
                + "--batchMode --overwrite";
        if (isAppcatExists) {
            System.out.println("Scanning the application use AppCat and set the report at " + reportUrl + " ...");
            Process process = getCommand(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new Exception("Failed to scan the application. Please check the source path and try again.");
            }
        } else {
            throw new Exception("Please install appcat first. You can download it from https://aka.ms/appcat");
        }

        //copy the cf manifest to metadata folder
        System.out.println("****************************************************************************************");
        System.out.println("Check the Cloud Foundary config file");
        System.out.println("****************************************************************************************");
        File cfManifest = new File(sourcePath, CfManifest);
        if(cfManifest.exists()){
            System.out.println("Find the Cloud Foundary config and copy it to " + cfManifest.getAbsolutePath() + " ...");
            File cfManifestCopy = new File(metaData, CfManifest);
            Files.copy(cfManifest.toPath(), cfManifestCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

       return scanFolder(metaData);

    }

    private Map<String, String> scanFolder(File folder) {
        Map<String, String> result = new HashMap<>();

        File file = new File(folder, CfManifest);
        if (file.exists()) {
            result.put(CfManifest, file.getAbsolutePath());
        }
        file = new File(folder, AppCatReportUrl);
        if (file.exists()) {
            result.put(AppCatReportUrl, file.getAbsolutePath());
        }
        return result;
    }

    public String generateMD5Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] hashBytes = digest.digest(input.getBytes());

            BigInteger number = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private ProcessBuilder getCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        String mode = "c";
        String os = System.getProperty("os.name").toLowerCase();
        String shell = "sh";
        String parameterStart = "-";
        if (os.contains("win")) {
            shell = "cmd";
            parameterStart = "/";
        }
        processBuilder.command(shell, parameterStart + mode, command);

        return processBuilder;
    }
}
