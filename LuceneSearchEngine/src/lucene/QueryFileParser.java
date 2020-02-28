package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;


public class QueryFileParser {

	public String[] flags = new String[]{".I",".T",".A",".B", ".W"};
	private String filename;

	private List<Query> queries;

	private Integer queryCount = 0;

	
	private QueryParser qp;
	
	private MultiFieldQueryParser multiFieldQP;

	public QueryFileParser(String filename, Analyzer analyzer) {
		
		this.qp = new QueryParser("content", analyzer);
		this.qp.setAllowLeadingWildcard(true);
		
		
		HashMap<String,Float> boosts = new HashMap<String,Float>();
		boosts.put("title", 5f);
		boosts.put("author", 2f);
		boosts.put("content", 10f);
		this.multiFieldQP = new MultiFieldQueryParser(new String[] {"title","author","content"}, analyzer, boosts);
		
		
		this.filename = filename+"/cranqry.txt";
		
		this.queries = new ArrayList<>();
	}

	public List<Query> getQueries() {
		return queries;
	}

	public Integer getQueryCount() {
		return queryCount;
	}

	public void loadQueries() {
		String queryId = "";
		StringBuilder queryStr = new StringBuilder();

		String tag = ".I";

		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				
//				line = removeWildCardIfAny(line);
				
				String newTag = getFlags(line);
				if (newTag != null) {
					tag = newTag;
					if (!".I".equals(newTag))
						line = br.readLine();
				}

				if (".I".equals(tag)) {

					queryId = line.split(" ")[1];

					queryCount++;
					if (queryCount > 1) {
						Query query = createQuery(queryId, queryStr.toString());
						queries.add(query);
					}

					queryId = "";
					queryStr = new StringBuilder();

				} else if (".W".equals(tag)) {
					if(line.contains("?")) {
						line = line.replace('?', ' ');
					}
					queryStr.append(line);
				}
			}

			Query query = createQuery(queryId, queryStr.toString());
			queries.add(query);

		} catch (IOException | ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public Query createQuery(String queryStr) throws ParseException {
		Query q = this.multiFieldQP.parse(queryStr);
		return q;
	}

	private Query createQuery(String queryId, String queryStr) throws IOException, ParseException {
		Query q = null;
		
		q = multiFieldQP.parse(queryStr);
		return q;
	}

	
	private String getFlags(String line) {

		for (String tag : flags) {
			if (line.contains(tag))
				return tag;
		}
		return null;
	}
	
}
