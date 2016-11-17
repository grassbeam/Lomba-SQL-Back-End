package oraclehomeworkautograder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class EvaluateInsert {
	
	static String tableName;
	static final String JDBC_DRIVER = "com.oracle.jdbc.Driver";  
	  // static final String DB_URL = "jdbc:oracle://localhost:1521";
	  static final String DB_URL = "AutoGrader@//localhost:1521/xe";
	  
	  // Database credentials
	  static final String USER = "Autograder";
	  static final String PASS = "Autograder";
	  
	  private Connection connection;
	  private Connection connCheck;
	  private String idPeserta;
	  private String passwordPeserta;
	  
	  public EvaluateInsert (String idPeserta,String tableName)
	  {
		  this.idPeserta = idPeserta;
		  try {
				Class.forName( util.Constant.className );
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  this.tableName = tableName;
		  Statement stmt = null;
		  try {
				this.connCheck = DriverManager.getConnection( util.Constant.judgeConnectionString, util.Constant.judgeUsername, util.Constant.judgePassword );
				stmt = connCheck.createStatement();
		  }
		  catch (SQLException e)
		  {
			  e.printStackTrace();
		  }
		  String searchPassword = "select password from login where username = '"+idPeserta+"'";
		  System.out.println(searchPassword);
		  try
		  {
			  ResultSet result = stmt.executeQuery(searchPassword);
			  while (result.next())
			  {
				  this.passwordPeserta = result.getString(1);
			  }
		  }
		  catch (SQLException e)
		  {
			  System.out.println("error");
			  e.printStackTrace();
		  }
		  try {
				this.connection = DriverManager.getConnection( util.Constant.judgeConnectionString, this.idPeserta, this.passwordPeserta );
		  }
		  catch (SQLException e)
		  {
			  e.printStackTrace();
		  }
		  
		  
	  }
	  public void cleanTable()
	  {
		  for (int i=0;i<util.Constant.tableList.size();i++)
		  {
			  String createTable = "truncate table "+util.Constant.tableList.get(i)+"insert";
			  Statement stmtPeserta= null;
			  try {
				stmtPeserta = this.connection.createStatement();
				  stmtPeserta.executeUpdate(createTable);
				  System.out.println("truncate successful");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  
		try {
			this.connCheck.close();
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	  }
	  //return 0 = pass
	  //return 1 = sql error pas d execute
	  //return 2 = wrong answer
	  
	  public int evaluate (String query)
	  {
		  Statement stmt = null;
		  try
		  {
			  stmt = connection.createStatement();
		  }
		  catch (SQLException e)
		  {
			  e.printStackTrace();
		  }
		  if (StringUtils.containsIgnoreCase(query, "/*") || StringUtils.containsIgnoreCase(query, "*/") || StringUtils.containsIgnoreCase(query, "--"))
		  {
			  System.out.println("ada comment! ga boleh");
			  return 2;
		  }
		  else if (StringUtils.containsIgnoreCase(query, "update") ||StringUtils.containsIgnoreCase(query, "select") || StringUtils.containsIgnoreCase(query,"delete") || StringUtils.containsIgnoreCase(query,"drop") || StringUtils.containsIgnoreCase(query,"truncate") || StringUtils.containsIgnoreCase(query,"table") || StringUtils.containsIgnoreCase(query,"alter") || StringUtils.containsIgnoreCase(query,"create") || StringUtils.containsIgnoreCase(query,"event") || StringUtils.containsIgnoreCase(query,"trigger"))
		  {
			  System.out.println("harus insert doang!");
			  return 2;
		  }
		  else if (CommitRollbackChecker.commitRollbackChecker(query))
		  {
			  System.out.println("ada commit ga boleh");
			  return 2;
		  }
		  else
		  {
			  String[] queryToken = null;				  
			  //query.replaceAll("\n", " ");
			  query = query.trim();
			  queryToken = query.split(";");
				  for( int i = 0; i < queryToken.length; i++ )
				    { 
					    queryToken[i] = util.PreProcessing.CleanQuery(queryToken[i]);
					    String[] token = queryToken[i].split(" ");
					    String gabung = "";
					    for (int j=0;j<token.length;j++)
					    {
					    	if (token[j].equalsIgnoreCase("into"))
					    	{
					    		token[j+1] = token[j+1] + "insert";
					    	}
					    	if (j != token.length-1)
					    	{
					    		gabung = gabung + token[j] + " ";
					    	}
					    	else
					    	{
					    		gabung = gabung + token[j];
					    	}
					    }
					    queryToken[i] = gabung;
				    }
				  for (int i=0;i<queryToken.length;i++)
				  {
					  System.out.println(queryToken[i]);
				  }
				  for (int i=0;i<queryToken.length;i++)
				  {
					  try {
						stmt.executeUpdate(queryToken[i]);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("query failed to execute");
						e.printStackTrace();
						return 1;
					}
					  
				  }
				  String checkQuery = "select * from "+tableName+" order by 1"; 
				  String submittedQuery = "select * from "+tableName+"insert order by 1";
				  float score = 0.000000f;
				    float meetsLogicCriteriaScore = 0.00f;
				    float queryExecutesScore = 0.00f;
				    float resultSetsMatchScore = 0.00f;
				    String queryExecutes = "Y";
				    String resultSetsMatch = "N";
				    
				  try
				    { // Execute the query
				    	System.out.println("solution = "+checkQuery);
				    	Statement stmtCheck = connCheck.createStatement();
				      ResultSet solutionResultSet = stmtCheck.executeQuery( checkQuery );
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
				      System.out.println(solutionArray.size());
				      for (int i=0;i<solutionArray.size();i++)
				      {
				    	  System.out.println(solutionArray.get(i));
				      }
				      System.out.println();
				      for (int i=0;i<submittedArray.size();i++)
				      {
				    	  System.out.println(submittedArray.get(i));
				      }
				      resultSetsMatch = ( submittedArray.equals( solutionArray ) ) ? "Y" : "N";
	
					  System.out.println(submittedArray.equals(solutionArray));
				      queryExecutesScore = ( queryExecutes == "Y" ) ? 0.0f : 0;
				      resultSetsMatchScore = ( resultSetsMatch == "Y" ) ? 3.000000f : 0;
				      score += ( meetsLogicCriteriaScore + queryExecutesScore + resultSetsMatchScore );
				     
				    }
				      
				    catch( SQLException se )
				    { //Handle errors for JDBC
				      // se.printStackTrace();
				      // System.out.println( "In SQL Exception block" );
				    	se.printStackTrace();
				      queryExecutes = "N";
				    }
				  if (resultSetsMatch.equals("Y"))
				  {
					  return 0;
				  }
				  else
				  {
					  return 2;
				  }
				  
			  }
	  }
	  
//	  public static void main (String[] Args)
//	  {
//		  EvaluateInsert eval = new EvaluateInsert("hobert","peserta");
//////			  Connection connection;
//////			  Statement stmt = null;
//////			try {
//////				connection = DriverManager.getConnection( "jdbc:oracle:thin:Autograder@//localhost:1521/xe", "hobert", "hobert" );
//////				stmt = connection.createStatement();
//////			} catch (SQLException e1) {
//////				// TODO Auto-generated catch block
//////				e1.printStackTrace();
//////			}
//		  BufferedReader bf = null;
//		try {
//			bf = new BufferedReader (new FileReader ("tesQuery.sql"));
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		  StringBuilder sb = new StringBuilder();
//		  String line = "";
//		  try {
//			while ((line = bf.readLine())!=null)
//			  {
//				  sb.append(line);
//				  sb.append("\n");
//			  }
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		  String query = sb.toString();
//		  int verdict = eval.evaluate(query);
//		  if (verdict == 0)
//		  {
//			  System.out.println("ACCEPTED");
//		  }
//		  else if (verdict == 1)
//		  {
//			  System.out.println("COMPILE ERROR");
//		  }
//		  else if (verdict == 2)
//		  {
//			  System.out.println("WRONG ANSWER");
//		  }
//////		  if (StringUtils.contains(query, "/*") || StringUtils.contains(query, "*/") || StringUtils.contains(query, "--"))
//////		  {
//////			  System.out.println("ada comment! ga boleh");
//////		  }
//////		  else
//////		  {
//////			  String[] queryToken = null;
//////			  if (!CommitRollbackChecker.commitRollbackChecker(query))
//////			  {
//////				  query.replaceAll("\n", " ");
//////				  queryToken = sb.toString().split(";");
//////			  }
//////			  for( int i = 0; i < queryToken.length-1; i++ )
//////			    { queryToken[i] = queryToken[i].toUpperCase();
//////			      queryToken[i] = queryToken[i].replace( ";", "" );
//////			      queryToken[i] = queryToken[i].replace( "<", " < " );
//////			      queryToken[i] = queryToken[i].replace( ">", " > " );
//////			      queryToken[i] = queryToken[i].replace( "=", " = " );
//////			      queryToken[i] = queryToken[i].replace( ",", " , " );
//////			      queryToken[i] = queryToken[i].replace( "(", " ( " );
//////			      queryToken[i] = queryToken[i].replace( ")", " ) " );
//////			      // Replace multiple consecutive spaces with 1 consecutive space
//////			      queryToken[i] = queryToken[i].trim().replaceAll(" +", " ");
//////			    }
//////			  for (int i=0;i<queryToken.length-1;i++)
//////			  {
//////				  System.out.println(queryToken[i]);
//////			  }
//////			  /*for (int i=0;i<queryToken.length-1;i++)
//////			  {
//////				  try {
//////					stmt.executeUpdate(queryToken[i]);
//////				} catch (SQLException e) {
//////					// TODO Auto-generated catch block
//////					System.out.println("query failed to execute");
//////					e.printStackTrace();
//////				}
//////				  
//////			  }*/
//////			  String checkQuery = "select * from "+tableName;
//////			  Connection connCheck;
//////			  Statement stmtCheck = null;
//////			try {
//////				connCheck = DriverManager.getConnection( "jdbc:oracle:thin:Autograder@//localhost:1521/xe", "system", "oracle" );
//////				stmtCheck = connCheck.createStatement();
//////			} catch (SQLException e) {
//////				// TODO Auto-generated catch block
//////				e.printStackTrace();
//////			}
//////			  
//////			  float score = 0.000000f;
//////			    float meetsLogicCriteriaScore = 0.00f;
//////			    float queryExecutesScore = 0.00f;
//////			    float resultSetsMatchScore = 0.00f;
//////			    String queryExecutes = "Y";
//////			    String resultSetsMatch = "N";
//////			    
//////			  try
//////			    { // Execute the query
//////			    	System.out.println("solution = "+checkQuery);
//////			      ResultSet solutionResultSet = stmtCheck.executeQuery( checkQuery );
//////			      int solutionColumnCount = solutionResultSet.getMetaData().getColumnCount();
//////			      ArrayList<String> solutionArray = new ArrayList<String>();
//////			      
//////			      while( solutionResultSet.next() )
//////			      { String currentRow = "";
//////			        for( int i = 1; i <= solutionColumnCount; i++ )  
//////			        { if( i > 1 ) { currentRow += " | "; }
//////			          currentRow += solutionResultSet.getString( i );
//////			        }
//////			        solutionArray.add( currentRow );
//////			      }
//////			      
//////			      ResultSet submittedResultSet = stmt.executeQuery( checkQuery );
//////			      int submittedColumnCount = submittedResultSet.getMetaData().getColumnCount();
//////			      ArrayList<String> submittedArray = new ArrayList<String>();
//////			      
//////			      while( submittedResultSet.next() )
//////			      { String currentRow = "";
//////			        for( int i = 1; i <= submittedColumnCount; i++ )  
//////			        { if( i > 1 ) { currentRow += " | "; }
//////			          currentRow += submittedResultSet.getString( i );
//////			        }
//////			        submittedArray.add( currentRow );
//////			      }
//////			      System.out.println(solutionArray.size());
//////			      for (int i=0;i<solutionArray.size();i++)
//////			      {
//////			    	  System.out.println(solutionArray.get(i));
//////			      }
//////			      System.out.println();
//////			      for (int i=0;i<submittedArray.size();i++)
//////			      {
//////			    	  System.out.println(submittedArray.get(i));
//////			      }
//////			      resultSetsMatch = ( submittedArray.equals( solutionArray ) ) ? "Y" : "N";
//////
//////				  System.out.println(submittedArray.equals(solutionArray));
//////			      queryExecutesScore = ( queryExecutes == "Y" ) ? 0.0f : 0;
//////			      resultSetsMatchScore = ( resultSetsMatch == "Y" ) ? 3.000000f : 0;
//////			      score += ( meetsLogicCriteriaScore + queryExecutesScore + resultSetsMatchScore );
//////			     
//////			    }
//////			      
//////			    catch( SQLException se )
//////			    { //Handle errors for JDBC
//////			      // se.printStackTrace();
//////			      // System.out.println( "In SQL Exception block" );
//////			      queryExecutes = "N";
//////			    }
//////			  
//////			  
//////		  }
//////		  
//		  
//	  }

}
