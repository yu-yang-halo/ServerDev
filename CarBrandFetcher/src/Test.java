
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
  public static void main(String[] args) {
	Calendar calendar=Calendar.getInstance();
	
	SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	try {
		Date date=dateformat.parse("2016-12-01 13:00:00");
		calendar.setTime(date);

		calendar.set(2016, 11, 1);//0-11

		System.out.println(calendar.get(Calendar.YEAR));
		
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
}
