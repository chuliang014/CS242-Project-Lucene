package com.lucene.index;

import java.io.File;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "header", analyzer);

		Query query = parser.parse(q);

		long start = System.currentTimeMillis();

		TopDocs hits = indexSearch.search(query, 10);

		long end = System.currentTimeMillis();

		System.out.println("Searching " + q + " ，totalTime is " + (end - start) + "ms" + " and the items searched are "
				+ hits.totalHits);

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = indexSearch.doc(scoreDoc.doc);
			System.out.println(doc.get("header"));
		}

		reader.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexDir = "/Users/vincent/Learning-Data/cs242/project/CS242-Project-Lucene/indexDir";

		String query = "1916";

		try {
			search(indexDir, query);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
