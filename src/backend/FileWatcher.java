package backend;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.SaveQueue;
public class FileWatcher {
	
	public static LinkedBlockingQueue<String> submissionQueue;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrintStream printStream=null;
		try {
			printStream = new PrintStream(new FileOutputStream("ConsoleLog.log"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.setOut(printStream);
		File backupQueue = new File ("submissionQueue.data");
		if (backupQueue.exists())
		{
			SaveQueue back = new SaveQueue();
			submissionQueue = back.read();
		}
		else
		{
			submissionQueue = new LinkedBlockingQueue<String>();
		}
		SubmissionWatcher submit = new SubmissionWatcher();
		submit.start();
		File directory = new File ("jawaban");
		File[] fList = directory.listFiles();
		for (File file : fList)
		{
			if (file.isDirectory())
			{
				FolderWatcher watch = new FolderWatcher (file.getAbsolutePath());
				watch.start();
			}
			
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run()
			{
				SaveQueue save = new SaveQueue(submissionQueue);
				save.save();
			}
		}, 0,2000);
		
	}
	

}
