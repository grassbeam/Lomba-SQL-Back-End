package oraclehomeworkautograder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

public class EvaluateSelect {
	
	static final String JDBC_DRIVER = "com.oracle.jdbc.Driver";  
	  // static final String DB_URL = "jdbc:oracle://localhost:1521";
	  static final String DB_URL = "AutoGrader@//localhost:1521/xe";
	  
	  // Database credentials
	  static final String USER = "Autograder";
	  static final String PASS = "Autograder";
	  
	  
	  private Connection connection;
	  private String idPeserta;
	  private String problemID;
	  private String querySolutions;
	  
	  
	  public EvaluateSelect (String problemID)
	  {
		  this.problemID = problemID;
		  try {
			Class.forName( util.Constant.className );
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Statement stmt;
		  try {
			connection = DriverManager.getConnection( util.Constant.judgeConnectionString, util.Constant.judgeUsername, util.Constant.judgePassword );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  org.w3c.dom.Document doc = null;
		try {
			doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
			  		    .newDocumentBuilder()
			  		    .parse(util.Constant.answerPath);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final org.w3c.dom.NodeList answerList = doc.getElementsByTagName("answer");
		final org.w3c.dom.NodeList problemList = doc.getElementsByTagName("problemNumber");
		int index = -1;
		for (int i=0;i<problemList.getLength();i++)
		{
			if (problemID.equals(problemList.item(i).getTextContent()))
			{
				index = i;
				break;
			}
		}
		this.querySolutions = answerList.item(index).getTextContent();
		System.out.println(querySolutions);
		  
	  }
	  
	  public void close()
	  {
		  try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  
	  public int evaluate (String query)
	  {
		  if (StringUtils.containsIgnoreCase(query, "/*") || StringUtils.containsIgnoreCase(query, "*/") || StringUtils.containsIgnoreCase(query, "--"))
		  {
			  System.out.println("ada comment! ga boleh");
			  return 2;
		  }
		  else if (StringUtils.containsIgnoreCase(query, "update") || StringUtils.containsIgnoreCase(query, "insert") || StringUtils.containsIgnoreCase(query,"delete") || StringUtils.containsIgnoreCase(query,"drop") || StringUtils.containsIgnoreCase(query,"truncate") || StringUtils.containsIgnoreCase(query,"table") || StringUtils.containsIgnoreCase(query,"alter") || StringUtils.containsIgnoreCase(query,"create") || StringUtils.containsIgnoreCase(query,"event") || StringUtils.containsIgnoreCase(query,"trigger"))
		  {
			  System.out.println("harus select doang!");
			  return 2;
		  }
		  else if (CommitRollbackChecker.commitRollbackChecker(query))
		  {
			  System.out.println("ada commit ga boleh");
			  return 2;
		  }
		  else
		  {
			  String solutionQuery = util.PreProcessing.CleanQuery(this.querySolutions);
			  String submittedQuery = util.PreProcessing.CleanQuery(query);
			  String queryExecutes = "Y";
			  String resultSetsMatch = "N";
			  System.out.println(solutionQuery);
			  System.out.println(submittedQuery);
			  Statement stmt;
			  try
			  {
				  stmt = connection.createStatement();
				  ResultSet solutionResultSet = stmt.executeQuery( solutionQuery );
			      int solutionColumnCount = solutionResultSet.getMetaData().getColumnCount();
			      ArrayList<String> solutionArray = new ArrayList<String>();
			      
			      while( solutionResultSet.next() )
			      { String currentRow = "";
			        for( int i = 1; i <= solutionColumnCount; i++ )  
			        { if( i > 1 ) { currentRow += " | "; }
			          currentRow += solutionResultSet.getString( i );
			        }
			        solutionArray.add( currentRow );
			      }
			      
			      ResultSet submittedResultSet = stmt.executeQuery( submittedQuery );
			      int submittedColumnCount = submittedResultSet.getMetaData().getColumnCount();
			      ArrayList<String> submittedArray = new ArrayList<String>();
			      
			      while( submittedResultSet.next() )
			      { String currentRow = "";
			        for( int i = 1; i <= submittedColumnCount; i++ )  
			        { if( i > 1 ) { currentRow += " | "; }
			          currentRow += submittedResultSet.getString( i );
			        }
			        submittedArray.add( currentRow );
			      }
			      resultSetsMatch = ( submittedArray.equals( solutionArray ) ) ? "Y" : "N";
			  }
		      
			  catch( SQLException se )
			  { //Handle errors for JDBC
			      // se.printStackTrace();
			      // System.out.println( "In SQL Exception block" );
			      queryExecutes = "N";
			      se.printStackTrace();
			  }
			      
			  catch( Exception e )
			  { //Handle errors for Class.forName
			    e.printStackTrace();
			  }
			  if (queryExecutes.equals("N"))
			  {
				  return 1;
			  }
			  else if (resultSetsMatch.equals("N"))
			  {
				  return 2;
			  }
			  else if (resultSetsMatch.equals("Y"))
			  {
				  return 0;
			  }
			  
			  
			  
		  }
		  return 0;
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query = "select jurusan as \"Jurusan\", min(honor) as \"Honor terkecil\", avg(honor) as \"Honor rata-rata\", max(honor) as \"Honor terbesar\" from Pengajar group by jurusan";
		EvaluateSelect eval = new EvaluateSelect ("5");
		int verdict = eval.evaluate(query);
		if (verdict == 0)
		  {
			  System.out.println("ACCEPTED");
		  }
		  else if (verdict == 1)
		  {
			  System.out.println("COMPILE ERROR");
		  }
		  else if (verdict == 2)
		  {
			  System.out.println("WRONG ANSWER");
		  }

	}

}
