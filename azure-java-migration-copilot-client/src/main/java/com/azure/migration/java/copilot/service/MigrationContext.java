package com.azure.migration.java.copilot.service;


import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
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
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.migration.java.copilot.command.MigrationCommand.loop;
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
                terminal.println(Ansi.ansi().bold().a("\nCopilot: I‘m your migration assistant. Could you please provide me with the location of your source code?").reset().toString());
                sourcePathString = textIO.
                        newStringInputReader().
                        withDefaultValue(getProperty("user.dir")).
                        read("/>");
            }
            File file = new File(sourcePathString);
            if (!file.exists()) {
                terminal.println("The path does not exist, please check and try again.");
                continue;
            }
            this.sourceCodePath = file.getAbsolutePath();
            String tempDir = getProperty("java.io.tmpdir");
            String basePathPrefix = "migration-pilot/" + generateMD5Hash(this.sourceCodePath);
            File baseFile = new File(tempDir, basePathPrefix);
            this.basePath = baseFile.getAbsolutePath();
            boolean needScan = false;
            findReport();
            if (!Optional.ofNullable(baseFile.list()).map(arr -> arr.length == 0).orElse(true) && !force) {
                terminal.println("Skip rebuild the report because find report and manifest.yml under: " + basePath);
            } else {
                if (baseFile.exists()) {
                    terminal.print("The report already exists, do you want to delete it and rebuild the report?:");
                    String text = textIO.newStringInputReader().withDefaultValue("N").read("/> ");
                    if(text.equalsIgnoreCase("Y")) {
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
                terminal.print("Start to scan the source code with AppCat and Cloud Foundary manifest.yml, it will take some time, continue? (yes/no)");
                String text = textIO.newStringInputReader().withDefaultValue("N").read("/> [y/N]");
                if(text.equalsIgnoreCase("Y")) {
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
        File fromFile = new File(sourceCodePath, "manifest.yml");
        if (fromFile.exists()) {
            terminal.println("Found the Cloud Foundary manifest.yml under: " + fromFile.getAbsolutePath());
            cfManifestPath = fromFile.getAbsolutePath();
        }
        fromFile = new File(basePath, "appcat-report");
        if (fromFile.exists()) {
            terminal.println("Found the AppCat Report under: " + fromFile.getAbsolutePath());
            windupReportPath = fromFile.getAbsolutePath();
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

