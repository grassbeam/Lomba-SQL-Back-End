package oraclehomeworkautograder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CommitRollbackChecker {
	
	public static boolean commitRollbackChecker (String query)
	{
		
		if (StringUtils.containsIgnoreCase(query, "commit") || StringUtils.containsIgnoreCase(query, "rollback"))
		{
			System.out.println("error karena ada commit atau rollback");
			return true;
		}
		return false;
	}
	
//	public static void main (String[] Args)
//	{
//		String query = "/* Create table Peserta */ create table peserta(id_peserta varchar2(6) not null,nama varchar2(20) not null,jurusan varchar2(30) not null,total_sks  number not null,primary key(id_peserta)";
//		System.out.println(commitRollbackChecker (query));
//	}

}
