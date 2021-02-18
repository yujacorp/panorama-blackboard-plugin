package com.yuja.panorama;

import blackboard.platform.session.BbSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership.Role;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.gradebook2.SubmissionAttemptFileLocationUtil;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;
import blackboard.servlet.renderinghook.RenderingHook;

public class RenderInjectionScript implements RenderingHook{
	
	private static HashMap<String,String> configValues = null;
	
	static
	{
		updateConfig();
	}
	
	public static void updateConfig(){

    	File configDir;
        try
        {
            configDir = PlugInUtil.getConfigDirectory("yuja", "panorama");
        }
        catch(PlugInException ex)
        {
            throw new RuntimeException("Problem while trying to get Panorama Building Block Config Directory", ex);
        }
        File pano = new File(configDir, "panoconfig");
        if(!pano.exists())
        	pano.mkdir();
        File openFile = new File(pano.getAbsoluteFile() + "/panoconfig.properties");
        try {
        	if(openFile.exists())
        	{
        		BufferedReader fr = new BufferedReader(new FileReader(openFile));
				String value = fr.readLine();
				StringBuilder sb = new StringBuilder();
        		while(value != null && !value.trim().equals(""))
        		{
					sb.append(value);
        			value = fr.readLine();
				}

				try {
					configValues = new Gson().fromJson(sb.toString(), HashMap.class);
				} catch (Exception | Error e) {

				}
				

        	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getContent() {
		String identifierKey;
		Context context = ContextManagerFactory.getInstance().getContext();
        if(context == null) {
            return missingContextScript;
		}

		Course cc = context.getCourse();

		CourseMembership cm = context.getCourseMembership();
		if (cm == null) {
			return missingCourseMembershipScript;
		}

		Role rr = cm.getRole();
		if (rr == null) {
			return missingRoleScript;
		}
        if(rr.equals(Role.INSTRUCTOR) || rr.equals(Role.COURSE_BUILDER) || rr.equals(Role.TEACHING_ASSISTANT) || rr.equals(Role.GRADER)) {
			identifierKey = configValues.get("instructorIdentifierKey");
		} else {
			identifierKey = configValues.get("studentIdentifierKey");
		}

		String environment = configValues.getOrDefault("environment", "");
		String url = "https://localhost:443";
		String cdnUrl = "https://localhost:443";
		if(environment.equals("Staging")) {
			url = "https://staging-panorama-api.yuja.com";
			cdnUrl = "https://staging-cdn-panorama.yuja.com";
		} else if (environment.equals("Production - US")) {
			url = "https://panorama-api.yuja.com";
			cdnUrl = "https://cdn-panorama.yuja.com";
		} else if (environment.equals("Production - CA")) {
			url = "https://panorama-api-cz.yuja.com";
			cdnUrl = "https://cdn-panorama.yuja.com";
		} else if (environment.equals("Production - EU")) {
			url = "https://panorama-api-ez.yuja.com";
			cdnUrl = "https://cdn-panorama.yuja.com";
		}

		String scriptToUse;

		String visualizerVersion = configValues.get("version");
		if (visualizerVersion == null || visualizerVersion.equals("null") || visualizerVersion.length() == 0) {
			scriptToUse = autoUpdatedPanoramaScript;
		} else {
			scriptToUse = versionLockedPanoramaScript;
		}

		try {
			scriptToUse = scriptToUse.replace("#UUID#", cc.getUuid());
			scriptToUse = scriptToUse.replace("#ROLE#", rr.toString());
			scriptToUse = scriptToUse.replace("#URL#", url);
			scriptToUse = scriptToUse.replace("#CDNURL#", cdnUrl);
			scriptToUse = scriptToUse.replace("#COURSECONTEXT#", cc.toString());
			scriptToUse = scriptToUse.replace("#IDKEY#", identifierKey);
			scriptToUse = scriptToUse.replace("#VERSION#", configValues.getOrDefault("version", ""));
			scriptToUse = scriptToUse.replace("#INTEGRITY#", configValues.getOrDefault("integrityHash", ""));
		} catch (Exception | Error e) {
			return 
			"<script>\r\n" + 
			"    console.error('Error generating panorama html: " + e.toString() + " ') \r\n" + 
			"</script>\r\n";
		}
		
        return scriptToUse;
	}

	@Override
	public String getKey() {
        return "jsp.topFrame.start";
	}

	private static String missingContextScript =
	"<script>\r\n" + 
	"    console.warn('Context is null!') \r\n" + 
	"</script>\r\n";

	private static String missingCourseMembershipScript =
	"<script>\r\n" + 
	"    console.warn('Missing course membership') \r\n" + 
	"</script>\r\n";

	private static String missingRoleScript =
	"<script>\r\n" + 
	"    console.warn('Missing role!') \r\n" + 
	"</script>\r\n";
	
	private static String autoUpdatedPanoramaScript = "<!-- This snippet binds a function to the \"dom:loaded\" event.\r\n" + 
			"You can put your own javascript into the function, or replace\r\n" + 
			"the entire snippet. -->\r\n" + 
			"<script>\r\n" + 
			"    window.SERVER_URL = `#URL#`; \r\n" + 
			"    window.CDN_URL = `#CDNURL#`; \r\n" + 
			"    window.identifierKey = `#IDKEY#`; \r\n" + 
			"    window.PAN_ENV = {\r\n" + 
			"	    \"courseId\": `#UUID#`,\r\n" + 
			"	    \"userRole\": `#ROLE#`,\r\n" + 
			"	    \"courseContext\": `#COURSECONTEXT#`,\r\n" + 
			"    };\r\n" + 
			"</script>\r\n" +
			"<script type=\"text/javascript\" src=\"#URL#/visualizers/blackboard-rest/#IDKEY#\"></script>\r\n" ;

	private static String versionLockedPanoramaScript = "<!-- This snippet binds a function to the \"dom:loaded\" event.\r\n" + 
			"You can put your own javascript into the function, or replace\r\n" + 
			"the entire snippet. -->\r\n" + 
			"<script>\r\n" + 
			"    window.SERVER_URL = `#URL#`; \r\n" + 
			"    window.CDN_URL = `#CDNURL#`; \r\n" + 
			"    window.identifierKey = `#IDKEY#`; \r\n" + 
			"    window.PAN_ENV = {\r\n" + 
			"    \"courseId\": `#UUID#`,\r\n" + 
			"    \"userRole\": `#ROLE#`,\r\n" + 
			"    \"courseContext\": `#COURSECONTEXT#`,\r\n" + 
			"    };\r\n" + 
			"</script>\r\n" + 
			"<script type=\"text/javascript\" src=\"#CDNURL#/resources/build/blackboard-rest-visualizer.#VERSION#.js\" integrity=\"#INTEGRITY#\" crossorigin=\"anonymous\"></script>\r\n" ;
}

