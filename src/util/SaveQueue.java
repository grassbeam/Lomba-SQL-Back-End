package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SaveQueue {
	
	private LinkedBlockingQueue<String> submissionQueue;
	
	public SaveQueue(LinkedBlockingQueue<String> submissionQueue)
	{
		this.submissionQueue = submissionQueue;
	}
	
	public SaveQueue()
	{
		
	}
	
	public void save()
	{
		try (ObjectOutputStream out = new ObjectOutputStream(
		        new FileOutputStream("submissionQueue.data"))) {
		    out.writeObject(submissionQueue);
		    out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public LinkedBlockingQueue<String> read()
	{
		LinkedBlockingQueue<String> queue = null;
		try (ObjectInputStream in = new ObjectInputStream (
		        new FileInputStream("submissionQueue.data"))) {
		    queue = (LinkedBlockingQueue<String>) in.readObject();
		    System.out.println("read");
		    in.close();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queue;
	}

}
