package lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import lucene.CranFileParser;
import lucene.QueryFileParser;


public class MainClass {
	private static Similarity similarity1 = new BM25Similarity();
	private static Similarity similarity1_1 = new BM25Similarity(1.2f, 0.75f);
	private static Similarity similarity2 = new ClassicSimilarity();
	

	public static void main(String[] args) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		
		
		String indexPath = "C:\\Users\\User\\Desktop\\Search Engine\\Data\\index";
		String sourceFolder = "C:\\Users\\User\\Desktop\\Search Engine\\Data";
		Boolean printResults = true;
		
		File out = new File("results.txt");
		int hitsperpage = 1000;
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		
		
		for (int i = 0; i < args.length; i++) {
			if ("-indexPath".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-printResults".equals(args[i])) {
				printResults = Boolean.valueOf(args[i + 1]);
				i++;
			} else if ("-hitsperpage".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					hitsperpage = Integer.parseInt(args[i + 1]);
				// else default = 10
				
				i++;
			} else if ("-source".equals(args[i])) {
				if (args[i + 1] != null || !args[i + 1].equals(""))
					sourceFolder = args[i + 1];
				else
					throw new RuntimeException("Must specify source folder for CRAN.");
				i++;
			} 
		}

		// Load all the documents
		final List<String> stopWords = Arrays.asList(
				"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", 
				"and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been", "before",
				"being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did",
				"didn", "didn't", "do", "does", "doesn", "doesn't", "doing", "don", "don't", "down", "during",
				"each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have",
				"haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how",
				"i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just", "ll", "m", "ma",
				"me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor",
				"not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over",
				"own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't", "so",
				"some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves", "then", "there",
				"these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn",
				"wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will",
				"with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
				"yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd", "i'll", "i'm", "i've", "let's",
				"ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll",
				"we're", "we've", "what's", "when's", "where's", "who's", "why's", "would"
			    );
	    final CharArraySet stopSet = new CharArraySet(
	        stopWords.size(), false);
	    stopSet.addAll(stopWords);  
		CranFileParser cranparser = new CranFileParser(sourceFolder);
		cranparser.loadContentFromFile();
		List<Document> docs = cranparser.getDocuments();

//		Analyzer analyzer = new StandardAnalyzer(stopSet);
		Analyzer analyzer = new
				StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

//		Analyzer analyzer = new EnglishAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		iwc.setSimilarity(similarity2);
		
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		
		IndexWriter indexWriter = new IndexWriter(indexDir, iwc);
		indexWriter.addDocuments(docs);
		indexWriter.close();

		// Instantiate the Search Engine
		SearchEngine searchEngine = new SearchEngine(indexDir);

		QueryFileParser queryparser = new QueryFileParser(sourceFolder, analyzer);
		queryparser.loadQueries();
		List<Query> queries = queryparser.getQueries();


		int queryCount = 1;
		
		for (Query query : queries) {
			ScoreDoc[] hits = searchEngine.searchQuery(query, printResults, hitsperpage);
			sb.setLength(0);
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				double score = hits[i].score;
				sb.append(queryCount+" Q0 "+Integer.toString(docId+1)+ " "+ i+1 +" "+ score+" STANDARD\n");
				
			}
			System.out.println(sb);
			bw.write(sb.toString());
			queryCount++;
		}
		bw.close();

	}





}
