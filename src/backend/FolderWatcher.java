package backend;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FolderWatcher extends Thread {
	
	Path myDir;
	
	public FolderWatcher (String path)
	{
		this.myDir = Paths.get(path);
	}
	
	public void Watch()
	{
		try {
	           WatchService watcher = myDir.getFileSystem().newWatchService();
	           myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
	           StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

	           WatchKey watckKey = watcher.take();

	           List<WatchEvent<?>> events = watckKey.pollEvents();
	           for (WatchEvent event : events) {
	                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
	                    System.out.println("Created: " + event.context().toString()+" on folder "+myDir.toAbsolutePath());
	                    String filePath = myDir.toAbsolutePath().toString()+File.separator+event.context().toString();
	                    System.out.println(filePath);
	                    FileWatcher.submissionQueue.put(filePath);
	                    Watch();
	                }
	                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
	                {
	                	System.out.println("Created: " + event.context().toString()+" on folder "+myDir.toAbsolutePath());
	                	String filePath = myDir.toAbsolutePath().toString()+File.separator+event.context().toString();
	                    System.out.println(filePath);
	                    FileWatcher.submissionQueue.put(filePath);
	                	Watch();
	                }
	            }
	           
	        } catch (Exception e) {
	            System.out.println("Error: " + e.toString());
	        }
	}
	
	public void run() {
		Watch();
			
    }

}
