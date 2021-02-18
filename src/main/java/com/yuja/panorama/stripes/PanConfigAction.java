package com.yuja.panorama.stripes;

import com.alltheducks.bb.stripes.BlackboardActionBeanContext;

import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;
import blackboard.base.BbList;
import blackboard.data.course.Course;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.platform.BbServiceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.lang.StringBuilder;
import net.sourceforge.stripes.action.*;


public class PanConfigAction
    implements ActionBean
{
	
	private String studentIdentifierKey;
	private String instructorIdentifierKey;
	private String environment = "Local";
	private String version = "";
	private String integrityHash = "";

	private static HashMap<String,String> configValues = new HashMap<String,String>();
	
	BlackboardActionBeanContext context;
	private HashMap<String, String> courses = new HashMap<String,String>();

	public void setContext(ActionBeanContext context)
    {
        this.context = (BlackboardActionBeanContext)context;
    }

    public ActionBeanContext getContext()
    {
        return this.context;
    }

    public PanConfigAction()
    {
    }

    public Resolution displayPanConfigPage()
        throws IOException
    {
    	try {
			BbList<Course> allCourses = CourseDbLoader.Default.getInstance().loadAllCourses();
			Iterator it = allCourses.iterator();
			while(it.hasNext())
			{
				Course course = (Course)it.next();
				courses.put(course.getUuid(), course.getDisplayTitle());
				getValuesFromConfig(courses);
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ForwardResolution("/WEB-INF/jsp/config.jsp");
    }

    private void getValuesFromConfig(HashMap<String, String> courses) {
    	File configDir;
        try
        {
            configDir = PlugInUtil.getConfigDirectory("YuJa", "Panorama");
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
					studentIdentifierKey = configValues.getOrDefault("studentIdentifierKey", "");
					instructorIdentifierKey = configValues.getOrDefault("instructorIdentifierKey", "");
					environment = configValues.getOrDefault("environment", "Local");
					version = configValues.getOrDefault("version", "");
					integrityHash = configValues.getOrDefault("integrityHash", "");
				} catch (Exception e) {
					studentIdentifierKey = "";
					instructorIdentifierKey = "";
					version = "";
					integrityHash = "";
				}
        	} else {
        		studentIdentifierKey = "";
				instructorIdentifierKey = "";
				version = "";
				integrityHash = "";
        	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getStudentIdentifierKey() {
		if (studentIdentifierKey == null || studentIdentifierKey.equals("null")) {
			return "";
		}
		return studentIdentifierKey;
	}

	public void setStudentIdentifierKey(String studentIdentifierKey) {
		this.studentIdentifierKey = studentIdentifierKey;
	}

	public String getInstructorIdentifierKey() {
		if (instructorIdentifierKey == null || instructorIdentifierKey.equals("null")) {
			return "";
		}
		return instructorIdentifierKey;
	}

	public void setInstructorIdentifierKey(String instructorIdentifierKey) {
		this.instructorIdentifierKey = instructorIdentifierKey;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		if (version == null || version.equals("null")) {
			return "";
		}
		return version;
	}

	public void setIntegrityHash(String integrityHash) {
		this.integrityHash = integrityHash;
	}

	public String getIntegrityHash() {
		if (integrityHash == null || integrityHash.equals("null")) {
			return "";
		}
		return integrityHash;
	}
}
