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

public class EvaluateCreate {
	
	private String idPeserta;
	private Connection connection;
	private Connection connCheck;
	private String passwordPeserta;
	private String[] tableQueryToken;
	
	public EvaluateCreate (String idPeserta)
	{
		this.idPeserta = idPeserta;
		try {
			Class.forName( util.Constant.className );
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		//String searchPassword = "select pass from daftarpeserta where idpeserta = '"+idPeserta+"'";
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
	
	public void cleanDatabase()
	{
		if (tableQueryToken != null)
		{
			for (int i=0;i<tableQueryToken.length;i++)
			{
				String dropTable = "drop table "+tableQueryToken[i]+" cascade constraints purge";
				  Statement stmtPeserta= null;
				  try {
					stmtPeserta = this.connection.createStatement();
					  stmtPeserta.executeUpdate(dropTable);
					  System.out.println("drop successful");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
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
		
		
	}
	
	public int evaluate(String query)
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
		else if (StringUtils.containsIgnoreCase(query, "select") || StringUtils.containsIgnoreCase(query,"delete") || StringUtils.containsIgnoreCase(query,"drop") || StringUtils.containsIgnoreCase(query,"truncate")  || StringUtils.containsIgnoreCase(query,"alter") || StringUtils.containsIgnoreCase(query,"event") || StringUtils.containsIgnoreCase(query,"trigger") || StringUtils.containsIgnoreCase(query,"update"))
		{
			  System.out.println("harus create doang!");
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
			System.out.println(query);
			query = query.trim();
			queryToken = query.split(";");
			tableQueryToken = new String[queryToken.length];
			for (int i=0; i<queryToken.length;i++)
			{
				queryToken[i] = util.PreProcessing.CleanQuery(queryToken[i]);
				String[] token = queryToken[i].split(" ");
				for (int j=0;j<token.length;j++)
				{
					if (token[j].equalsIgnoreCase("table"))
					{
						tableQueryToken[i] = token[j+1];
						break;
					}
				}
			}
			for (int i=0;i<queryToken.length;i++)
			{
				System.out.println(queryToken[i]);
				System.out.println();
			}
			
			for (int i=0;i<queryToken.length;i++)
			{
				try {
					stmt = this.connection.createStatement();
					stmt.executeUpdate(queryToken[i]);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
			}
			//check for fieldList and data type
			int grandVerdict = 0;
			boolean exist = true;
			if (tableQueryToken.length == util.Constant.tableList.size())
			{
				for (int i=0;i<tableQueryToken.length;i++)
				{
					if (!util.Constant.tableList.contains(tableQueryToken[i].toLowerCase()))
					{
						exist = false;
						break;
					}
				}
			}
			String tableAll="";
			if (!exist)
			{
				System.out.println("masuk sini");
				return 2;
			}
			else
			{
				for (int i=0;i<tableQueryToken.length;i++)
				{
					tableAll = tableAll + "'"+tableQueryToken[i].toUpperCase()+"'";
					if (i != tableQueryToken.length-1)
					{
						tableAll = tableAll + ",";
					}
				}
			}
			System.out.println(tableAll);
			String checkDescribe = "select COLUMN_NAME, DATA_TYPE from USER_TAB_COLUMNS where TABLE_NAME in ("+tableAll+") order by column_id";
			int verdict = evaluateSelect (checkDescribe);
			System.out.println();
			System.out.println();
			if (verdict != 0)
			{
				System.out.println("stop di fieldlist");
				return verdict;
			}
			else
			{
				grandVerdict = verdict;
			}
			//check for primary key constraint
			String checkPrimaryKeyPeserta = "SELECT cols.table_name, cols.column_name, cols.position, cons.status FROM all_constraints cons, all_cons_columns cols WHERE cols.table_name in ("+tableAll+") AND cons.constraint_type = 'P' AND cons.constraint_name = cols.constraint_name AND cons.owner = '"+this.idPeserta.toUpperCase()+"' ORDER BY cols.table_name, cols.position";
			String checkPrimaryKeySystem = "SELECT cols.table_name, cols.column_name, cols.position, cons.status FROM all_constraints cons, all_cons_columns cols WHERE cols.table_name in ("+tableAll+") AND cons.constraint_type = 'P' AND cons.constraint_name = cols.constraint_name AND cons.owner = 'SYSTEM' ORDER BY cols.table_name, cols.position";
			String checkForeignKeyPeserta = "SELECT a.table_name, a.column_name, c_pk.table_name r_table_name FROM all_cons_columns a JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name WHERE c.constraint_type = 'R' AND a.table_name in ("+tableAll+") and a.owner = '"+this.idPeserta.toUpperCase()+"'";
			String checkForeignKeySystem = "SELECT a.table_name, a.column_name, c_pk.table_name r_table_name FROM all_cons_columns a JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name JOIN all_constraints c_pk ON c.r_owner = c_pk.owner AND c.r_constraint_name = c_pk.constraint_name WHERE c.constraint_type = 'R' AND a.table_name in ("+tableAll+") and a.owner = 'SYSTEM'";
			int verdictPrimaryKey = evaluate2Select (checkPrimaryKeyPeserta, checkPrimaryKeySystem);
			int verdictForeignKey = evaluate2Select (checkForeignKeyPeserta, checkForeignKeySystem);
			if (verdictPrimaryKey == 0 && verdictForeignKey==0)
			{
				grandVerdict = 0;
			}
			else
			{
				int showVerdict = Math.max(verdictPrimaryKey, verdictForeignKey);
				return showVerdict;
			}
		}
		return 0;
	}
	
	public int evaluate2Select (String queryPeserta, String queryCheckker)
	{
		String resultSetsMatch = "N";
		String queryExecutes = "Y";
		Statement stmt=null;
		try
		  {
			  stmt = this.connCheck.createStatement();
			  ResultSet solutionResultSet = stmt.executeQuery( queryCheckker );
		      int solutionColumnCount = solutionResultSet.getMetaData().getColumnCount();
		      ArrayList<String> solutionArray = new ArrayList<String>();
		      
		      while( solutionResultSet.next() )
		      { String currentRow = "";
		        for( int i = 1; i <= solutionColumnCount; i++ )  
		        { if( i > 1 ) { currentRow += " | "; }
		        	String sementara = solutionResultSet.getString(i);
		        	if (sementara.equalsIgnoreCase("varchar") || sementara.equalsIgnoreCase("char"))
		        	{
		        		sementara = "VARCHAR2";
		        	}
		        	else if (StringUtils.containsIgnoreCase(sementara, "int"))
		        	{
		        		sementara = "NUMBER";
		        	}
		          currentRow += sementara;
		        }
		        solutionArray.add( currentRow );
		      }
		      ResultSet submittedResultSet = stmt.executeQuery( queryPeserta );
		      int submittedColumnCount = submittedResultSet.getMetaData().getColumnCount();
		      ArrayList<String> submittedArray = new ArrayList<String>();
		      
		      while( submittedResultSet.next() )
		      { String currentRow = "";
		        for( int i = 1; i <= submittedColumnCount; i++ )  
		        { if( i > 1 ) { currentRow += " | "; }
		          String sementara = submittedResultSet.getString(i);
		          if (sementara.equalsIgnoreCase("varchar") || sementara.equalsIgnoreCase("char"))
		          {
		        	  sementara = "VARCHAR";
		          }
		          else if (StringUtils.containsIgnoreCase(sementara, "int"))
		          {
		        		sementara = "NUMBER";
		          }
		          currentRow += sementara;
		        }
		        submittedArray.add( currentRow );
		      }
		      for (int i=0;i<solutionArray.size();i++)
		      {
		    	  System.out.println(solutionArray.get(i));
		      }
		      System.out.println("next");
		      for (int i=0;i<submittedArray.size();i++)
		      {
		    	  System.out.println(submittedArray.get(i));
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
		return 0;
		
		
	}
	
	public int evaluateSelect(String describe)
	{
		String resultSetsMatch = "N";
		String queryExecutes = "Y";
		Statement stmt=null;
		try
		  {
			  stmt = this.connCheck.createStatement();
			  ResultSet solutionResultSet = stmt.executeQuery( describe );
		      int solutionColumnCount = solutionResultSet.getMetaData().getColumnCount();
		      ArrayList<String> solutionArray = new ArrayList<String>();
		      
		      while( solutionResultSet.next() )
		      { String currentRow = "";
		        for( int i = 1; i <= solutionColumnCount; i++ )  
		        { if( i > 1 ) { currentRow += " | "; }
		        String sementara = solutionResultSet.getString(i);
	        	if (sementara.equalsIgnoreCase("varchar") || sementara.equalsIgnoreCase("char"))
	        	{
	        		sementara = "VARCHAR2";
	        	}
	          currentRow += sementara;
		        }
		        solutionArray.add( currentRow );
		      }
		      Statement stmtCheck = this.connection.createStatement();
		      ResultSet submittedResultSet = stmtCheck.executeQuery( describe );
		      int submittedColumnCount = submittedResultSet.getMetaData().getColumnCount();
		      ArrayList<String> submittedArray = new ArrayList<String>();
		      
		      while( submittedResultSet.next() )
		      { String currentRow = "";
		        for( int i = 1; i <= submittedColumnCount; i++ )  
		        { if( i > 1 ) { currentRow += " | "; }
		        String sementara = submittedResultSet.getString(i);
		          if (sementara.equalsIgnoreCase("varchar") || sementara.equalsIgnoreCase("char"))
		          {
		        	  sementara = "VARCHAR2";
		          }
		          currentRow += sementara;
		        }
		        submittedArray.add( currentRow );
		      }
		      for (int i=0;i<solutionArray.size();i++)
		      {
		    	  System.out.println(solutionArray.get(i));
		      }
		      System.out.println("next");
		      for (int i=0;i<submittedArray.size();i++)
		      {
		    	  System.out.println(submittedArray.get(i));
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
		return 0;
		
		
	}
	
	public static void main (String[] Args)
	{
		BufferedReader bf = null;
		try {
			bf = new BufferedReader (new FileReader ("tesQuery.sql"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  StringBuilder sb = new StringBuilder();
		  String line = "";
		  try {
			while ((line = bf.readLine())!=null)
			  {
				  sb.append(line);
				  sb.append(" ");
			  }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String query = sb.toString();
		EvaluateCreate eval = new EvaluateCreate ("hobert");
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
