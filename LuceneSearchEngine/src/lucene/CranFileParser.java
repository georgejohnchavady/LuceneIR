package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class CranFileParser {

	private String filename;
	public String[] flags = new String[]{".I",".T",".A",".B", ".W"};
	private List<Document> documents;

	private Integer corpusCount = 0;

	public CranFileParser(String filename) {
		this.filename = filename+"/cran.txt";
		this.documents = new ArrayList<>();
	}

	public String getFilename() {
		return filename;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public Integer getCorpusCount() {
		return corpusCount;
	}

	public void loadContentFromFile() {
		String docId = "";
		StringBuilder textAbstract = new StringBuilder();
		String authors = "";
		String bibliography = "";
		StringBuilder content = new StringBuilder();

		String tag = ".I";

		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				String newTag = getFlags(line);
				if (newTag != null) {
					tag = newTag;
					if(!".I".equals(newTag))
						line = br.readLine();
				}

				if (".I".equals(tag)) {

					// Extract the docId
					docId = line.split(" ")[1];

					corpusCount++;
					if (corpusCount > 1) {
						Document doc = createDocument(docId, textAbstract.toString(), authors, bibliography, 
								content.toString());
						documents.add(doc);
					}

					docId = "";
					textAbstract = new StringBuilder();
					authors = "";
					bibliography = "";
					content = new StringBuilder();

				} else if (".T".equals(tag)) {
					textAbstract.append(line);
				} else if (".A".equals(tag)) {
					authors += line;
				} else if (".B".equals(tag)) {
					bibliography += line;
				} else if (".W".equals(tag)) {
					content.append(line);
				}
			}
			
			Document doc = createDocument(docId, textAbstract.toString(), authors, bibliography, 
					content.toString());
			documents.add(doc);
			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private Document createDocument(String docId, String title, String authors, String bibliography,
			String content) throws IOException {
		Document doc = new Document();
		
//		TextField titleField=new TextField("title", title, Field.Store.YES);
//		doc.add(titleField);
		
		
		
		doc.add(new StringField("docId", docId, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("author", authors, Field.Store.YES));
//		doc.add(new StringField("bibliography", bibliography, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		return doc;
	}

	
	private String getFlags(String line) {

		for (String tag : flags) {
			if(line.contains(tag))
				return tag;
		}
		return null;
	}

}
