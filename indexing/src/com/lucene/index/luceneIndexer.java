package com.lucene.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.google.gson.*;

public class luceneIndexer {

	public IndexWriter writer;

	// initialization
	public luceneIndexer(String indexDir) throws Exception {
		super();
		// getting the path to store
		Directory dir = FSDirectory.open(new File(indexDir));
		//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
		analyzerPerField.put("header", new KeywordAnalyzer());
		PerFieldAnalyzerWrapper aWrapper =
				new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_47), analyzerPerField);
		IndexWriterConfig con = new IndexWriterConfig(Version.LUCENE_47, aWrapper);
		writer = new IndexWriter(dir, con);
	}

	// close indexing
	public void close() throws Exception {
		writer.close();
	}

	public int index(String dataDir) throws Exception {

		File[] file = new File(dataDir).listFiles();
		// indexing file
		for (File files : file) {

			indexFile(files);

		}
		// return how many files we index
		return writer.numDocs();
	}

	public JsonObject parseJSONFile(String jsonFilePath) throws FileNotFoundException {

		File file = new File(jsonFilePath);
		FileReader readerJson = new FileReader(file);

		JsonParser parse = new JsonParser();
		JsonObject json = null;
		try {
			json = (JsonObject) parse.parse(readerJson);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return json;
	}

	private void indexFile(File files) throws Exception {

		Document doc = getDocument(files);

		// writing document into index file
		writer.addDocument(doc);
	}

	private Document getDocument(File files) throws Exception {
		// TODO Auto-generated method stub

		Document doc = new Document();
		// parsing jsonFile
		JsonObject jso = parseJSONFile(files.getAbsolutePath());
		// storing header, url, body into indexing file. If NO, not store
		// set different weights
		TextField HEADER = new TextField("header", jso.get("header").toString(), Field.Store.YES);
		HEADER.setBoost(1.5F);
		doc.add(HEADER);
		doc.add(new StringField("url", jso.get("url").toString(), Field.Store.YES));
		doc.add(new TextField("body", jso.get("body").toString(), Field.Store.NO));

		return doc;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexDir = "../Index";

		String dataDir = "../data";

		luceneIndexer indexer = null;
		int numIndex = 0;

		long start = System.currentTimeMillis();

		try {

			indexer = new luceneIndexer(indexDir);

			numIndex = indexer.index(dataDir);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				indexer.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Indexing  " + numIndex + "  files, and took  " + (end - start) + "  ms");
	}

}
