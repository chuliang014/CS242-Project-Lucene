package com.lucene.index;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class ReaderByIndexerTest {

	public static void search(String indexDir, String q) throws Exception {

		Directory dir = FSDirectory.open(new File(indexDir));

		IndexReader reader = DirectoryReader.open(dir);

		IndexSearcher indexSearch = new IndexSearcher(reader);

		//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		//
		Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
		analyzerPerField.put("header", new KeywordAnalyzer());
		PerFieldAnalyzerWrapper aWrapper =
				new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_47), analyzerPerField);
		
		QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47,
				new String[]{"header","body"}, aWrapper);

		Query query = parser.parse(q);

		long start = System.currentTimeMillis();

		TopDocs hits = indexSearch.search(query, 10);

		long end = System.currentTimeMillis();

		System.out.println("Searching " + q + " totalTime is " + (end - start) + "ms" + " and the items searched are "
				+ hits.totalHits);

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = indexSearch.doc(scoreDoc.doc);
			System.out.println(doc.get("header")+", "+doc.get("url"));
		}

		reader.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexDir = "../Index";

		String query = "joystick";

		try {
			search(indexDir, query);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
