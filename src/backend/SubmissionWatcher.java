package backend;

import java.util.Timer;
import java.util.TimerTask;

import oraclehomeworkautograder.AnswerModel;
import oraclehomeworkautograder.MainGrader;

public class SubmissionWatcher extends Thread {
	
	
	public void run()
	{
		
		while (true)
		{
			String top;
			try {
				top = FileWatcher.submissionQueue.take();
				MainGrader grade = new MainGrader (top);
				Thread check = new Thread(){
					public void run()
					{
						AnswerModel answer = grade.grade();
						System.out.println("masuk loh");
						System.out.println(answer.getVerdict());
						grade.updateScoreboard(answer);
					}
				};
				check.start();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
