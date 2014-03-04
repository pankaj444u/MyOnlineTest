package exam;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;


import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;

public class Practice_Questioner {
	private final Configuration cfg;
	private final UserDAO userDAO;
	private final SessionDAO sessionDAO;
	private int queCount =0;
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		if (args.length == 0) {
			new Practice_Questioner("mongodb://localhost");
		} else {
			new Practice_Questioner(args[0]);
		}

	}

	private void initializeRoutes() throws IOException {

		get(new FreemarkerBasedRoute("/login", "login.ftl") {

			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				SimpleHash root = new SimpleHash();
				root.put("username", "");
				root.put("login_error", "");
				template.process(root, writer);
			}
		});

		post(new FreemarkerBasedRoute("/login", "login.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				System.out.println("Pankaj is here...");
				String username = request.queryParams("username");
				String password = request.queryParams("password");
				System.out.println("Login: User submitted: " + username + "  "
						+ password);
				DBObject user = userDAO.validateLogin(username, password);
				
				if (user != null) {

                    // valid user, let's log them in
                    String sessionID = sessionDAO.startSession(user.get("_id").toString());

                    if (sessionID == null) {
                        response.redirect("/internal_error");
                    }
                    else {
                        // set the cookie for the user's browser
                        response.raw().addCookie(new Cookie("session", sessionID));

                        response.redirect("/welcome");
                    }
                }
                else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", StringEscapeUtils.escapeHtml4(username));
                    root.put("password", "");
                    root.put("login_error", "Invalid Login");
                    template.process(root, writer);
                }
			}
		});

		get(new FreemarkerBasedRoute("/signup", "signup.ftl") {
			@Override
			protected void doHandle(Request request, Response response,
					Writer writer) throws IOException, TemplateException {
				SimpleHash root = new SimpleHash();
				root.put("username", "");
				root.put("password", "");
				root.put("email", "");
				root.put("password_error", "");
				root.put("username_error", "");
				root.put("email_error", "");
				root.put("verify_error", "");
				template.process(root, writer);
			}
		});
		
		
		post(new FreemarkerBasedRoute("/signup","signup.ftl") {
			
			@Override
			protected void doHandle(Request request, Response response, Writer writer)
					throws IOException, TemplateException {
				String email = request.queryParams("email");
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String verify = request.queryParams("verify");

                HashMap<String, String> root = new HashMap<String, String>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                root.put("email", StringEscapeUtils.escapeHtml4(email));

                if (validateSignup(username, password, verify, email, root)) {
                    // good user
                    System.out.println("Signup: Creating user with: " + username + " " + password);
                    if (!userDAO.addUser(username, password, email)) {
                        // duplicate user
                        root.put("username_error", "Username already in use, Please choose another");
                        template.process(root, writer);
                    }
                    else {
                        // good user, let's start a session
                        String sessionID = sessionDAO.startSession(username);
                        System.out.println("Session ID is" + sessionID);

                        response.raw().addCookie(new Cookie("session", sessionID));
                        System.out.println("Paap");
                        response.redirect("/welcome");
                    }
                }
                else {
                    // bad signup
                    System.out.println("User Registration did not validate");
                    template.process(root, writer);
                }
				
			}
		});
		
		
		
		get(new FreemarkerBasedRoute("/welcome", "welcome.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String cookie = getSessionCookie(request);
                String username = sessionDAO.findUserNameBySessionId(cookie);

                if (username == null) {
                    System.out.println("welcome() can't identify the user, redirecting to signup");
                    response.redirect("/signup");
                }
                else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", username);
                    template.process(root, writer);
                }
            }
        });
		
		
		
		get(new FreemarkerBasedRoute("/MyTest", "Test.ftl") {
			 public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
				 try {
					 	
					 	String cookie = getSessionCookie(request);
		                String username = sessionDAO.findUserNameBySessionId(cookie);

		                if (username == null) {
		                    System.out.println("welcome() can't identify the user, redirecting to signup");
		                    response.redirect("/signup");

		                }
		                {
					 
							MongoClient client = new MongoClient(new ServerAddress("192.168.5.221",27017));
							DB database = client.getDB("exam");
							DBCollection collection = database.getCollection("test");
							Map<String,Object> queMap = new HashMap<String, Object>();
							List<Practice_QuestionerObject> qList = new ArrayList<Practice_QuestionerObject>();
							DBCursor cursor = collection.find();
							queCount=0;
							while(cursor.hasNext())
				        	 {
								 DBObject cur = cursor.next();
								 Practice_QuestionerObject quesObj = new Practice_QuestionerObject();
								 List<String> options = new ArrayList<String>();
								 options = (List<String>) cur.get("options");
								 quesObj.setQdescription((String) cur.get("question"));
								 quesObj.setQoptions(options);
								 quesObj.setAanswer((String) cur.get("answer"));
								 qList.add(quesObj);
								 queCount = queCount+1;
				        		 System.out.print("\n"+cur +"\n"+options+"\n Que Count: "+queCount);
				        	 }
							queMap.put("questionList", qList);
							
							template.process(queMap, writer);
		                }
						
					}
					catch (Exception e) {
					// TODO Auto-generated catch block
					halt(500);
					e.printStackTrace();
					
					}
		 }
		 });
		
		post(new FreemarkerBasedRoute("/GK", "result.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
            	
            	
				
				String cookie = getSessionCookie(request);
                String username = sessionDAO.findUserNameBySessionId(cookie);

                if (username == null) {
                    System.out.println("welcome() can't identify the user, redirecting to signup");
                    response.redirect("/signup");
                }
                else {
                	int x = 1;
    				String strreturn ="";
    				String ans = "";
                    SimpleHash root = new SimpleHash();
                    root.put("username", username);
                    List<String> strsubmit = new ArrayList<String>();
                    int score = 0;
                    int correctAns = 0;
                    while( x < queCount+1) {
  			    	  	String subAnswer = request.queryParams(Integer.toString(x));
                    	String ActualAnswer =request.queryParams("answer_"+Integer.toString(x));
                    	if(subAnswer != null)
                    	{
	                    	if(subAnswer.equals(ActualAnswer))
	                    	{
	                    		correctAns = correctAns+1;
	                    	}
                    	}
  			    	  	ans ="\n"+"Question: "+x+"\n Submitted Answer: "+subAnswer+"\n Actual Answer: "+ActualAnswer;
  			    	  	
  			    	  	
  			    	  	 strsubmit.add(ans);
  			    	  	 x++;
    				}
                    System.out.println("Que Count: "+queCount+"\n Correct Ans"+correctAns);
                    score = (100/queCount)*correctAns;
                    root.put("result", strsubmit);
                    root.put("totalscore", score);
                    MongoClient client = new MongoClient(new ServerAddress("192.168.5.221",27017));
					DB database = client.getDB("exam");
					DBCollection collection = database.getCollection("userscore");
					
					DBObject doc = new BasicDBObject("username",username).append("score", score).append("testtype", "JAVA");
					
					collection.insert(doc);
                    
                    
                    template.process(root, writer);
                }
            }
	 });
	
		// allows the user to logout of the blog
        get(new FreemarkerBasedRoute("/logout", "signup.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String sessionID = getSessionCookie(request);

                if (sessionID == null) {
                    // no session to end
                    response.redirect("/login");
                }
                else {
                    // deletes from session table
                    sessionDAO.endSession(sessionID);

                    // this should delete the cookie
                    Cookie c = getSessionCookieActual(request);
                    c.setMaxAge(0);

                    response.raw().addCookie(c);

                    response.redirect("/login");
                }
            }
        });	
		

	}
	
	private Cookie getSessionCookieActual(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie;
            }
        }
        return null;
    }
	
	private String getSessionCookie(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie.getValue();
            }
        }
        return null;
    }

	private Configuration createFreemarkerConfiguration() {
		Configuration retVal = new Configuration();
		retVal.setClassForTemplateLoading(Practice_Questioner.class, "/");
		return retVal;
	}

	public boolean validateSignup(String username, String password,
			String verify, String email, HashMap<String, String> errors) {
		String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
		String PASS_RE = "^.{3,20}$";
		String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

		errors.put("username_error", "");
		errors.put("password_error", "");
		errors.put("verify_error", "");
		errors.put("email_error", "");

		if (!username.matches(USER_RE)) {
			errors.put("username_error",
					"invalid username. try just letters and numbers");
			return false;
		}

		if (!password.matches(PASS_RE)) {
			errors.put("password_error", "invalid password.");
			return false;
		}

		if (!password.equals(verify)) {
			errors.put("verify_error", "password must match");
			return false;
		}

		if (!email.equals("")) {
			if (!email.matches(EMAIL_RE)) {
				errors.put("email_error", "Invalid Email Address");
				return false;
			}
		}

		return true;
	}

	abstract class FreemarkerBasedRoute extends Route {
		final Template template;

		/**
		 * Constructor
		 * 
		 * @param path
		 *            The route path which is used for matching. (e.g. /hello,
		 *            users/:name)
		 */
		protected FreemarkerBasedRoute(final String path,
				final String templateName) throws IOException {
			super(path);
			template = cfg.getTemplate(templateName);
		}

		@Override
		public Object handle(Request request, Response response) {
			StringWriter writer = new StringWriter();
			try {
				doHandle(request, response, writer);
			} catch (Exception e) {
				e.printStackTrace();
				response.redirect("/internal_error");
			}
			return writer;
		}

		protected abstract void doHandle(final Request request,
				final Response response, final Writer writer)
				throws IOException, TemplateException;

	}

	public Practice_Questioner(String mongoURIString) throws IOException {
		final MongoClient mongoClient = new MongoClient(new MongoClientURI(
				mongoURIString));
		final DB examDatabase = mongoClient.getDB("exam");

		userDAO = new UserDAO(examDatabase);
		sessionDAO = new SessionDAO(examDatabase);
		cfg = createFreemarkerConfiguration();
		setPort(4004);
		initializeRoutes();
	}

}

/*
 * // setPort(8011); // Map<String,Object> queMap = new HashMap<String,
 * Object>(); // // Practice_QuestionerObject q1 = new
 * Practice_QuestionerObject(); // q1.setQdescription("What is 2+2?"); //
 * List<String> map1 = new ArrayList<String>(); // map1.add("1"); //
 * map1.add("2"); // map1.add("3"); // map1.add("4"); // q1.setQoptions(map1);
 * // q1.setAanswer("4"); // // Practice_QuestionerObject q2 = new
 * Practice_QuestionerObject(); //
 * q2.setQdescription("How many states in India?"); // List<String> map2 = new
 * ArrayList<String>(); // map2.add("26"); // map2.add("27"); // map2.add("28");
 * // map2.add("32"); // q2.setQoptions(map2); // q2.setAanswer("28"); // //
 * Practice_QuestionerObject q3 = new Practice_QuestionerObject(); //
 * q3.setQdescription("Who is CM of Assam?"); // List<String> map3 = new
 * ArrayList<String>(); // map3.add("Tarun Gogoi"); // map3.add("Nabam Tuki");
 * // map3.add("Neiphiu Rio"); // map3.add("Harish Rawat"); //
 * q3.setQoptions(map3); // q3.setAanswer("Tarun Gogoi"); // // ////
 * queMap.put("question", q.getQdescription()); //// queMap.put("options",
 * q.getQoptions()); // // //228 // List<Practice_QuestionerObject> qList = new
 * ArrayList<Practice_QuestionerObject>(); // qList.add(q1); // qList.add(q2);
 * // qList.add(q3); // queMap.put("questionList", qList); // // // ////
 * Map<String,List<Map<String, Object>> > queMapFinal = new HashMap<String,
 * List<Map<String, Object>> >(); //// d.add(queMap); ////
 * queMapFinal.put("Finals", d); // // try { // Template QuestionerTemplate =
 * configuration.getTemplate("Test.ftl"); // StringWriter writer = new
 * StringWriter(); // QuestionerTemplate.process(queMap, writer); //
 * System.out.print(queMap); // return writer; // } // catch (Exception e) { //
 * // TODO Auto-generated catch block // halt(500); // e.printStackTrace(); //
 * return null; // }
 */