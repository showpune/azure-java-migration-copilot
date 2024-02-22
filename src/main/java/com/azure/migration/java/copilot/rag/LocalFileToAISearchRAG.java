package com.azure.migration.java.copilot.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Component
public class LocalFileToAISearchRAG {

    @Autowired
    EmbeddingStoreIngestor ingestor;


    public void ingest(String filePath) throws IOException {

        File file =  new File(filePath);;
        if (file.isFile()) {
            Document document = loadDocument(file.getPath(), new TextDocumentParser());
            ingestor.ingest(document);
            System.out.println("Ingest File: "+ filePath);
        } else if (file.isDirectory()) {
            String[] children = getStrings(file);
            for (String child : children) {
                ingest((new File(file,child)).getPath());
            }
        }
    }

    @NotNull
    private static String[] getStrings(File file) {
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.toLowerCase().endsWith(".md")){
                    return true;
                }else if((new File(file,name)).isDirectory()){
                    return true;
                }
                return false;
            }
        };
        String[] children = file.list(filenameFilter);
        assert children != null;
        return children;
    }
}

