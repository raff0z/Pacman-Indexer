package it.uniroma3.giw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DocumentIO {

	private String indexPath;
	private String documentPath;
	private String spellCheckerPath; 
	
	public DocumentIO(){
		Properties conf = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/pacman_configuration.properties");
			if(inputStream == null)
				System.out.println("qua");
			
			conf.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		this.indexPath = conf.getProperty("index-path");
		this.documentPath = conf.getProperty("documents-path");
		this.spellCheckerPath = conf.getProperty("spell-checker-path");
	}

	public String getIndexPath() {
		return indexPath;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public String getSpellCheckerPath() {
		return spellCheckerPath;
	}

}
