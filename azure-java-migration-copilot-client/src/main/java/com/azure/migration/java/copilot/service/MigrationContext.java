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
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.System.getProperty;

@Component
public class MigrationContext {

    private final String[] DEFAULT_TARGET_SERVICE = new String[]{
            "azure-spring-apps"
    };
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
    private TemplateContext templateContext = new TemplateContext();

    public void init(ApplicationArguments args) throws IOException {

        boolean force = args.containsOption("force");
        String sourcePathString = null;
        if (args.containsOption("path")) {
            sourcePathString = args.getOptionValues("path").get(0);
        }

        boolean initSuccess = false;
        while (!initSuccess) {
            if (sourcePathString == null) {
                terminal.println(Ansi.ansi().bold().a("\nI‘m your migration assistant. Could you please provide me with the location of your source code?").reset().toString());
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
            String tempDir = System.getProperty("java.io.tmpdir");
            String basePathPrefix = "migration-pilot/" + generateMD5Hash(this.sourceCodePath);
            File baseFile = new File(tempDir, basePathPrefix);
            if (!Optional.ofNullable(baseFile.list()).map(arr -> arr.length == 0).orElse(true) && !force) {
                this.basePath = baseFile.getAbsolutePath();
                this.windupReportPath = (new File(basePath, "appcat-report")).getAbsolutePath();
                this.cfManifestPath = (new File(basePath, "manifest.yml")).getAbsolutePath();
                terminal.println("Skip rebuild the report because find report and manifest.yml under: " + basePath);
                return;
            } else {
                if (baseFile.exists()) {
                    baseFile.delete();
                }
            }
            this.basePath = baseFile.getAbsolutePath();
            scanCodeWithAppCat();
            scanCFManifest();
            initSuccess = true;
        }
    }

    public void scanCFManifest() throws IOException {
        File fromFile = new File(sourceCodePath, "manifest.yml");
        if (!fromFile.exists()) {
            return;
        }
        File toFile = new File(basePath, "manifest.yml");
        terminal.println("Handle the manifest.yml file");
        Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        cfManifestPath = toFile.getAbsolutePath();
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
