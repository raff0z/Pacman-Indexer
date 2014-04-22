package it.uniroma3.giw;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.tst.TSTLookup;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.uwyn.jhighlight.tools.FileUtils;

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {

	private static String indexPath;
	private static String imagesIndexPath;
	private static String imagesPath;
	private static String docsPath;
	private static DocumentIO io;

	public IndexFiles() {
		io = new DocumentIO();
		indexPath = io.getIndexPath();
		imagesPath = io.getImagesPath();
		docsPath = io.getDocumentPath();
	}

	public IndexFiles(String indexPath,String docsPath) {
		this.io = new DocumentIO();
		this.indexPath = indexPath;
		this.docsPath = docsPath;
	}

	public void index(boolean update){
		final File docDir = new File(docsPath);

		if (!docDir.exists() || !docDir.canRead()) {
			//TODO
			System.exit(1);
		}

		try {

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);

			if (!update) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			writer.close();

			didYouMeanMaker(analyzer);

		} catch (IOException e) {
			//TODO
		}
	}

	/** Index all text files under a directory. */
	public static void main(String[] args) {
		String usage = "java org.apache.lucene.demo.IndexFiles"
				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";
		boolean create = true;
		io = new DocumentIO();
		docsPath = io.getDocumentPath();
		indexPath = io.getIndexPath();
		imagesPath = io.getImagesPath();
		imagesIndexPath = io.getImagesIndexPath();

		if (docsPath == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			// :Post-Release-Update-Version.LUCENE_XY:
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer.  But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);
			ImageIndexer imgIndexer = new ImageIndexer();
			imgIndexer.startIndex(imagesIndexPath, imagesPath);

//			IndexWriter writer = new IndexWriter(dir, iwc);
//			indexDocs(writer, docDir);
//
//			// NOTE: if you want to maximize search performance,
//			// you can optionally call forceMerge here.  This can be
//			// a terribly costly operation, so generally it's only
//			// worth it when your index is relatively static (ie
//			// you're done adding documents to it):
//			//
//			// writer.forceMerge(1);
//
//			writer.close();
//
//			System.out.println("Creating dictionary...");
//
//			didYouMeanMaker(analyzer);
//
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is given,
	 * recurses over files and directories found under the given directory.
	 * 
	 * NOTE: This method indexes one document per input file.  This is slow.  For good
	 * throughput, put multiple documents into your input file(s).  An example of this is
	 * in the benchmark module, which can create "line doc" files, one document per line,
	 * using the
	 * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 *  
	 * @param writer Writer to the index where the given file/dir info will be stored
	 * @param file The file to index, or the directory to recurse into to find files to index
	 * @param indexPath 
	 * @throws IOException If there is a low-level I/O error
	 */
	static void indexDocs(IndexWriter writer, File file)
			throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {

				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					// at least on windows, some temporary files raise this exception with an "access denied" message
					// checking if the file can be read doesn't help
					return;
				}

				try {

					// make a new, empty document
					Document doc = new Document();

					// Add the path of the file as a field named "path".  Use a
					// field that is indexed (i.e. searchable), but don't tokenize 
					// the field into separate words and don't index term frequency
					// or positional information:
					Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
					doc.add(pathField);

					// Add the last modified date of the file a field named "modified".
					// Use a LongField that is indexed (i.e. efficiently filterable with
					// NumericRangeFilter).  This indexes to milli-second resolution, which
					// is often too fine.  You could instead create a number based on
					// year/month/day/hour/minutes/seconds, down the resolution you require.
					// For example the long value 2011021714 would mean
					// February 17, 2011, 2-3 PM.
					doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

					// Add the contents of the file to a field named "contents".  Specify a Reader,
					// so that the text of the file is tokenized and indexed, but not stored.
					// Note that FileReader expects the file to be in UTF-8 encoding.
					// If that's not the case searching for special characters will fail.
					if (isHtml(doc)){
						setHtmlTextAndTitle(doc,fis);
					}
					else{
						String contents = IOUtils.toString(fis);
						doc.add(new TextField("contents", contents, Field.Store.YES));
						setTitle(doc);
					}
					//Set title and short-path
					setShortPath(doc);

					//Set inverted index
					// setInvertedIndex(doc);

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old document can be there):
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						// Existing index (an old copy of this document may have been indexed) so 
						// we use updateDocument instead to replace the old one matching the exact 
						// path, if present:
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()), doc);
					}
				}
				finally {
					fis.close();
				}
			}
		}
	}

	private static boolean isImage(File file) {
		String path = file.getAbsolutePath();
		String[] pathSplitted = path.split(File.separator);
		String name = pathSplitted[pathSplitted.length-1];

		String nameSplitted[] = name.split("\\.");
		String extension = nameSplitted[nameSplitted.length-1];
		return extension.equals("jpg");
	}

	private static boolean isHtml(Document doc) {
		String path = doc.get("path");
		String[] pathSplitted = path.split(File.separator);
		String name = pathSplitted[pathSplitted.length-1];

		String nameSplitted[] = name.split("\\.");
		String extension = nameSplitted[nameSplitted.length-1];
		return extension.equals("html") || extension.equals("htm");
	}

	private static void setHtmlTextAndTitle(Document doc, FileInputStream fis) {
		InputStream input;
		try {
			input = new FileInputStream(doc.get("path"));
			BodyContentHandler handler = new BodyContentHandler(-1);
			Metadata metadata = new Metadata();
			new HtmlParser().parse(input, handler, metadata, new ParseContext());
			String title = metadata.get("title");
			//String title = metadata.get("og:title");

			//verifico che il title non sia null
			if(title!=null){
				doc.add(new StringField("title", title, Field.Store.YES));
			}

			String keywords = metadata.get("keywords");
			if(keywords!=null){
				doc.add(new TextField("keywords", keywords, Field.Store.YES));
			}


			String plainText = handler.toString();
			InputStream is = new ByteArrayInputStream(plainText.getBytes());


			//			FieldType contentFieldType = new FieldType();
			//	        contentFieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			//	        contentFieldType.setIndexed(true);
			//	        contentFieldType.setStored(true);

			String contents = IOUtils.toString(is);

			// doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(is, "UTF-8")));
			doc.add(new TextField("contents", contents, Field.Store.YES));

		} catch (IOException | SAXException | TikaException e) {
			e.printStackTrace();
		}
	}

	private static void setShortPath(Document doc) {
		String documentsPath = new DocumentIO().getDocumentPath();
		String fullPath = doc.get("path");
		String[] pathSplitted = fullPath.split(documentsPath);
		String shortPath = pathSplitted[1];

		doc.add(new StringField("shortPath", shortPath, Field.Store.YES));
	}

	private static void setTitle(Document doc) {
		String path = doc.get("path");
		String[] pathSplitted = path.split(File.separator);
		String name = pathSplitted[pathSplitted.length-1];

		doc.add(new StringField("title", name, Field.Store.YES));
	}


	private static void didYouMeanMaker(Analyzer analyzer) throws IOException{
		DocumentIO io = new DocumentIO(); 

		Directory spellCheckerDir = FSDirectory.open(new File(io.getSpellCheckerPath()));

		Directory indexPathDir = FSDirectory.open(new File(io.getIndexPath()));

		SpellChecker spellChecker = new SpellChecker(spellCheckerDir);

		IndexReader ir = DirectoryReader.open(indexPathDir);
		Dictionary dic = new LuceneDictionary(ir, "contents");

		spellChecker.indexDictionary(dic,new IndexWriterConfig(Version.LUCENE_47, analyzer),false);
		spellChecker.close();

	}
}