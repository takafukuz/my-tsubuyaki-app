package common;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	// load()の戻り値はProperties型（key=value)
	public static Properties load() {
	
		// ① JVM オプションから env を取得（指定がなければ dev）
		String env = System.getProperty("env", "dev");
		
		// ② 読み込むファイル名を決定
		String fileName = "db.properties." + env;
		System.out.println(fileName + "を読み込みます：" + java.time.LocalDateTime.now());
		
		// src/main/resources内のファイルに書いてあるkey=valueをpropsに入れて返す
		try (InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
		    Properties props = new Properties();
		    props.load(is);
		    return props;
		} catch (Exception e) {
		    throw new RuntimeException("設定ファイルの読み込みに失敗しました: " + fileName, e);
		}
	}
}
