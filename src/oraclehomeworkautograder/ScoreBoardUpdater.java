package oraclehomeworkautograder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ScoreBoardUpdater {
	
	Connection connCheck;
	AnswerModel answer;
	
	public ScoreBoardUpdater(AnswerModel answer)
	{
		this.answer = answer;
		try {
			Class.forName( util.Constant.className );
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.connCheck = DriverManager.getConnection( util.Constant.judgeConnectionString, util.Constant.judgeUsername, util.Constant.judgePassword );
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateSubmission ()
	{
		Statement stmt = null;
		try
		{
			stmt = connCheck.createStatement();
			String updateSubmissionTable = "update submission set status = "+answer.getVerdict()+" where sub_id = '"+answer.getSubmissionID()+"'";
			stmt.executeUpdate(updateSubmissionTable);
			/*if (Integer.parseInt(answer.getNomorSoal())==1)
			{
				if (answer.getVerdict()==0)
				{
					String updateSubmissionTable = "update submission set status = 999 where sub_id = '"+answer.getSubmissionID()+"'";
					stmt.executeUpdate(updateSubmissionTable);
				}
				else
				{
					String updateSubmissionTable = "update submission set status = "+answer.getVerdict()+" where sub_id = '"+answer.getSubmissionID()+"'";
					stmt.executeUpdate(updateSubmissionTable);
				}
			}
			else
			{
				String updateSubmissionTable = "update submission set status = "+answer.getVerdict()+" where sub_id = '"+answer.getSubmissionID()+"'";
				stmt.executeUpdate(updateSubmissionTable);
			}*/
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateScoreboard ()
	{
		Statement stmt = null;
		try
		{
			stmt = connCheck.createStatement();
			boolean flag = false; // accepted or not
			String checkScoreboard ="select verdict from scoreboard where name_code = '"+answer.getNameCode()+"' and prob_num = "+answer.getNomorSoal();
			ResultSet result = stmt.executeQuery(checkScoreboard);
			while (result.next())
			{
				int verdict = result.getInt(1);
				if (verdict == 0)
				{
					flag = true;
					break;
				}
			}
			if (!flag)//not accepted
			{
				System.out.println(answer.getTimeSubmitted());
				int submitTime = Integer.parseInt(answer.getTimeSubmitted())/60;
				String updateScoreboard = "update scoreboard set submit_count = submit_count + 1 , submit_time = "+submitTime+" , verdict = "+answer.getVerdict()+" where name_code = '"+answer.getNameCode()+"' and prob_num = "+answer.getNomorSoal();
				stmt.executeUpdate(updateScoreboard);
				/*if (Integer.parseInt(answer.getNomorSoal()) == 1)
				{
					if (answer.getVerdict() != 0)
					{
						int submitTime = Integer.parseInt(answer.getTimeSubmitted())/60;
						String updateScoreboard = "update scoreboard set submit_count = submit_count + 1 , submit_time = "+submitTime+" , verdict = "+answer.getVerdict()+" where name_code = '"+answer.getNameCode()+"' and prob_num = '"+answer.getNomorSoal()+"'";
						stmt.executeUpdate(updateScoreboard);
					}
				}
				else
				{
					int submitTime = Integer.parseInt(answer.getTimeSubmitted())/60;
					String updateScoreboard = "update scoreboard set submit_count = submit_count + 1 , submit_time = "+submitTime+" , verdict = "+answer.getVerdict()+" where name_code = '"+answer.getNameCode()+"' and prob_num = '"+answer.getNomorSoal()+"'";
					stmt.executeUpdate(updateScoreboard);
				}*/
				
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
