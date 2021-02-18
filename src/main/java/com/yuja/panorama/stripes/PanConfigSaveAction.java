package com.yuja.panorama.stripes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.Base64;

import java.util.HashMap;
import com.google.gson.Gson;

import com.alltheducks.bb.stripes.BlackboardActionBeanContext;
import com.yuja.panorama.RenderInjectionScript;

import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class PanConfigSaveAction implements ActionBean{

	BlackboardActionBeanContext context;
	private String studentIdentifierKey;
	private String instructorIdentifierKey;
	private String environment;
	private String version;
	private String integrityHash;

	@Override
	public ActionBeanContext getContext() {
		return context;
	}

	@Override
	public void setContext(ActionBeanContext context) {
		this.context = (BlackboardActionBeanContext)context;
		
	}
	
	public Resolution savePanSettings() throws IOException
	{
		RedirectResolution redirect = new RedirectResolution("PanConfig.action", false);

		saveSettings();
		redirect.addParameter("message", new Object[] {"Configuration saved successfully !"});
		
		return redirect;
	}

	private void saveSettings() throws IOException {
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
		BufferedWriter fw = new BufferedWriter(new FileWriter(openFile));
		HashMap<String, String> store = new HashMap();

		store.put("environment", environment);
		store.put("studentIdentifierKey", studentIdentifierKey);
		store.put("instructorIdentifierKey", instructorIdentifierKey);
		store.put("version", version);
		store.put("integrityHash", integrityHash);

		Gson gson = new Gson();
		String json = gson.toJson(store);

		fw.write(json);
		fw.flush();
		fw.close();

        // Configurations changed - so reload it in the renderer
        RenderInjectionScript.updateConfig();
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
