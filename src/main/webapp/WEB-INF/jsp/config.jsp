<!DOCTYPE html>
<%-- 
    Document   : config.jsp
    Created on : 21/07/2011, 12:33:41 PM
    Author     : Wiley Fuller <wiley@alltheducks.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<fmt:message var="pluginPageStr" key="admin_plugin_manage.label" bundle="${bundles.navigation_item}"/>
<fmt:message var="title" key="com.yuja.title" />
<fmt:message var="pageHelp" key="com.yuja.panorama.pageHelp" />
<%String message = request.getParameter("message"); %>
<jsp:useBean id="actionBean" class="com.yuja.panorama.stripes.PanConfigAction" scope="request" />

<bbNG:genericPage title="${title}" ctxId="ctx" >
 <bbNG:cssFile href="css/default.css"/>
 <bbNG:jsFile href="js/panorama.js"/>
 <bbNG:pageHeader instructions="${pageHelp}">
    <bbNG:breadcrumbBar environment="SYS_ADMIN" >
      <bbNG:breadcrumb href="../blackboard/admin/manage_plugins.jsp" title="${pluginPageStr}" />
      <bbNG:breadcrumb title="${title}"/>
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar title="${title}" />
  </bbNG:pageHeader>
  
  <stripes:form id="panForm" beanclass="com.yuja.panorama.stripes.PanConfigSaveAction" enctype="multipart/form-data" method="POST" onsubmit="return getTags(this)">
  <stripes:hidden name="savePanSettings" />
  <stripes:hidden name="courseTags" id="courseTags"/>
   <bbNG:dataCollection>
   <bbNG:step title="Panorama Settings">
   		<bbNG:dataElement label="Choose environment">
          <stripes:select name="environment" id="environment" value = "${actionBean.environment}">
          	<stripes:option value="Local">Local</stripes:option>
            <stripes:option value="Staging">Staging</stripes:option>
            <stripes:option value="Production - US">Production - US</stripes:option>
            <stripes:option value="Production - CA">Production - CA</stripes:option>
            <stripes:option value="Production - EU">Production - EU</stripes:option>
          </stripes:select>
        </bbNG:dataElement> 
        <bbNG:dataElement label="Student Identifier Key" isRequired="true">
          <stripes:text name="studentIdentifierKey" id="studentIdentifierKey" value = "${actionBean.studentIdentifierKey}" style="width:35em;" />
        </bbNG:dataElement>
        <bbNG:dataElement label="Instructor Identifier Key" isRequired="true">
          <stripes:text name="instructorIdentifierKey" id="instructorIdentifierKey" value = "${actionBean.instructorIdentifierKey}" style="width:35em;" />
        </bbNG:dataElement>
        <bbNG:dataElement label="(Optional) Visualizer Version" isRequired="false">
          <stripes:text name="version" id="version" value = "${actionBean.version}" style="width:35em;" />
        </bbNG:dataElement>
        <bbNG:dataElement label="Integrity Hash of Selected Version" isRequired="false">
          <stripes:text name="integrityHash" id="integrityHash" value = "${actionBean.integrityHash}" style="width:35em;" />
        </bbNG:dataElement>
      </bbNG:step>
      <bbNG:stepSubmit cancelUrl="PanConfig.action">
        <bbNG:stepSubmitButton label="Save" id="submitPanoramaButton"/>
      </bbNG:stepSubmit>
    </bbNG:dataCollection>
   </stripes:form>
  <p><font color="red"><%=(message != null ? message : "") %></font></p>
</bbNG:genericPage>