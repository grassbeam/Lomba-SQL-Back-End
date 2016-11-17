package oraclehomeworkautograder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainGrader {
	
	private AnswerModel answer;
	private BufferedReader bf;
	private String path;
	
	public MainGrader (String path)
	{
		this.path = path;
	}
	
	public AnswerModel grade()
	{
		File file = new File (path);
		try {
			bf = new BufferedReader (new FileReader (path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fileName = file.getName();
		fileName.replace(".sql", "");
		String[] data = fileName.split("_");
		data[4] = data[4].replace(".sql", "");
		StringBuilder sb = new StringBuilder();
		  String line = "";
		  try {
			while ((line = bf.readLine())!=null)
			  {
				  sb.append(line);
				  sb.append(" ");
			  }
			bf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			answer = new AnswerModel (data[0],data[1],data[2],Integer.parseInt(data[3]),data[4],"");
			answer.setVerdict(1);
		}
		
		answer = new AnswerModel (data[0],data[1],data[2],Integer.parseInt(data[3]),data[4],sb.toString());
		int noSoal = answer.getNomorSoal();
		if (noSoal >=8 && noSoal <=17)
		{
			EvaluateSelect eval = new EvaluateSelect (Integer.toString(answer.getNomorSoal()));
			int verdict = eval.evaluate(answer.getQuery());
			answer.setVerdict(verdict);
			eval.close();
		}
		else if (noSoal >= 2 && noSoal <= 7)
		{
			String tableName = util.Constant.tableList.get(noSoal-2);
			EvaluateInsert eval = new EvaluateInsert (answer.getUsername(),tableName);
			int verdict = eval.evaluate(answer.getQuery());
			eval.cleanTable();
			answer.setVerdict(verdict);
		}
		else if (noSoal ==1)
		{
			EvaluateCreate eval = new EvaluateCreate(answer.getUsername());
			int verdict = eval.evaluate(answer.getQuery());
			answer.setVerdict(verdict);
			eval.cleanDatabase();
		}
		return answer;
		
	}
	
	public void updateScoreboard (AnswerModel answer)
	{
		ScoreBoardUpdater update = new ScoreBoardUpdater(answer);
		update.updateSubmission();
		update.updateScoreboard();
	}
	
	
	
	
	
	

}
