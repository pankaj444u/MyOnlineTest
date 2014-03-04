package exam;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import freemarker.template.Configuration;
import freemarker.template.Template;


public class MyFirstPage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(MyFirstPage.class,"/");
		Spark.get(new Route("/") {
			@Override
			public Object handle(Request arg0, Response arg1) {
				// TODO Auto-generated method stub
				StringWriter writer = new StringWriter();
				try {
					Template helloTemplate = configuration.getTemplate("Hello.ftl");
					Map<String,Object> helloMap = new HashMap<String, Object>();
					helloMap.put("name", "Pankaj");
					helloTemplate.process(helloMap, writer);
					//System.out.print(writer);
					
				} catch (Exception e) {
						// TODO Auto-generated catch block
						halt(500);
						e.printStackTrace();
				}
				
				return writer;
			}
		});
	}

}
