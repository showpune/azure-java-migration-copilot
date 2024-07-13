package com.azure.migration.java.copilot.service;


import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import lombok.Getter;
import lombok.Setter;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.migration.java.copilot.service.ConsoleContext.*;
import static java.lang.System.getProperty;

@Component
public class MigrationContext {

    private final static String[] DEFAULT_TARGET_SERVICE = new String[]{
            "azure-container-apps"
    };

    public final static TemplateContext DEFAULT_TEMPLATE_CONTEXT = new TemplateContext();

    @Autowired
    TextIO textIO;

    @Autowired
    TextTerminal<?> terminal;

    @Value("${copilot.appcat-home}")
    private String appCatHome;

    @Getter
    private String windupReportPath;

    @Getter
    private String sourceCodePath;

    @Getter
    private String basePath;

    @Getter
    private String cfManifestPath;

    @Getter
    @Setter
    private String service;

    @Getter
    @Setter
    private String appName;

    @Autowired
    private LocalCommandTools localCommandTools;

    @Getter
    @Setter
    private TemplateContext templateContext;

    public void init(ApplicationArguments args) throws IOException {

        boolean force = args.containsOption("force");
        String sourcePathString = null;
        if (args.containsOption("path")) {
            sourcePathString = args.getOptionValues("path").get(0);
        }

        AtomicBoolean initSuccess = new AtomicBoolean(false);
        while (!initSuccess.get()) {
            if (sourcePathString == null) {
                terminal.println(ask("Copilot: I‘m your migration assistant. Could you please provide me with the location of your source code?"));
                sourcePathString = textIO.
                        newStringInputReader().
                        withDefaultValue(getProperty("user.dir")).
                        read("/>");
            }
            File file = new File(sourcePathString);
            if (!file.exists()) {
                throw new IOException(error("The path does not exist, please check and try again. " + sourcePathString));
            }
            this.sourceCodePath = file.getCanonicalPath();
            String tempDir = getProperty("java.io.tmpdir");
            String basePathPrefix = "migration-pilot/" + generateMD5Hash(this.sourceCodePath);
            File baseFile = new File(tempDir, basePathPrefix);
            this.basePath = baseFile.getAbsolutePath();
            boolean needScan = false;
            findReport();
            if (!Optional.ofNullable(baseFile.list()).map(arr -> arr.length == 0).orElse(true) && !force) {
                terminal.println(warn("Skip rebuild the report because find report under: " + basePath));
            } else {
                if (baseFile.exists()) {
                    terminal.print("The report already exists, do you want to delete it and rebuild the report?:");
                    boolean confirm = textIO.newBooleanInputReader().withDefaultValue(false).read("/> ");
                    if(confirm) {
                        baseFile.delete();
                        needScan=true;
                    }else {
                        needScan=false;
                    }
                }else{
                    needScan=true;
                }
            }

            if(needScan) {
                terminal.print(ask("Start to scan the source code with AppCat and Cloud Foundry manifest.yml, it will take some time, continue?"));
                boolean confirm = textIO.newBooleanInputReader().withDefaultValue(false).read("/> ");
                if(confirm) {
                    scanCodeWithAppCat();
                    scanCFManifest();
                }else{
                    break;
                }
            }
            initSuccess.set(true);
        }
    }

    public void findReport(){
        Path fromFile = Path.of(sourceCodePath, "manifest.yml");
        cfManifestPath = fromFile.toAbsolutePath().toString();
        if (Files.exists(fromFile)) {
            terminal.println("Found the Cloud Foundry manifest.yml under: " + fromFile);
        }
        fromFile = Path.of(basePath, "appcat-report");
        if (Files.exists(fromFile)) {
            terminal.println("Found the AppCat Report under: " + fromFile);
            windupReportPath = fromFile.toAbsolutePath().toString();
        }
    }

    public void scanCFManifest() throws IOException {
        File fromFile = new File(sourceCodePath, "manifest.yml");
        if (!fromFile.exists()) {
            return;
        }
        cfManifestPath = fromFile.getAbsolutePath();
    }

    public void scanCodeWithAppCat() throws IOException {
        File file = new File(basePath, "appcat-report");
        String appcatReportPath = file.getAbsolutePath();
        terminal.println("Start to generate AppCat report, please wait for a few minutes and DO NOT close this window");
        Path cmdPath = Path.of(appCatHome, "/bin", "/appcat");
        List<String> commands = new ArrayList<>(Arrays.asList(
                cmdPath.toString(),
                "--input", sourceCodePath,
                "--output", appcatReportPath,
                "--batchMode",
                "--overwrite"
        ));
        for (String target : DEFAULT_TARGET_SERVICE) {
            commands.add("--target");
            commands.add(target);
        }
        if (localCommandTools.executeCommand(commands)) {
            this.windupReportPath = appcatReportPath;
            terminal.println("Generated AppCat report under: " + windupReportPath);
        }
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
}

