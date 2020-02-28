package lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;


public class SearchEngine {

	private Directory indexDir;

	public SearchEngine(Directory indexDir) {
		this.indexDir = indexDir;

	}

	public ScoreDoc[] searchQuery(Query query, Boolean printResults, int hitsPerPage) throws IOException {
		try {
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);

//			searcher.setSimilarity(new BM25Similarity(1.2f, 0.75f));
//			searcher.setSimilarity(new BM25Similarity());
//			searcher.setSimilarity(new ClassicSimilarity());
			TopDocs docs = searcher.search(query, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;

			if (printResults) {
				System.out.println("Found " + hits.length + " hits.");
				System.err.print("\nNumber | Document ID | Title | Score\n");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					double score = hits[i].score;
					Document d = searcher.doc(docId);
					System.out.println((i + 1) + ". " + d.get("docId") + "\t" + d.get("title") + "\t" + score);
				}
			}
			reader.close();

			return hits;
		} catch (IOException e) {
			
		}

		return null;
	}
}

