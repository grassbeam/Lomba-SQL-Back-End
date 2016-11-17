package util;

public class PreProcessing {
	
	public static String CleanQuery (String query)
	{
		query = query.toUpperCase();
	      query = query.replace( ";", "" );
	      query = query.replace( "<", " < " );
	      query = query.replace( ">", " > " );
	      query = query.replace( "=", " = " );
	      query = query.replace( ",", " , " );
	      query = query.replace( "(", " ( " );
	      query = query.replace( ")", " ) " );
	      query = query.replace("*", " * ");
	      query = query.replace("\t", " ");
	      // Replace multiple consecutive spaces with 1 consecutive space
	      query = query.trim().replaceAll(" +", " ");
	      return query;
	}

}
