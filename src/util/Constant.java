package util;

import java.util.ArrayList;

public class Constant {
	
	public static String answerPath = "forproblemread.xml";
	public static String judgeConnectionString = "jdbc:oracle:thin:@//localhost:1521/xe";
	public static String judgeUsername = "system";
	public static String judgePassword = "SQLc2016untar";
	public static String className = "oracle.jdbc.driver.OracleDriver";
	public static ArrayList<String> tableList = new ArrayList<String>() {{add("peserta");add("pelajaran");add("jadwal");add("pengajar");add("kelas");add("nilai");}};
}
