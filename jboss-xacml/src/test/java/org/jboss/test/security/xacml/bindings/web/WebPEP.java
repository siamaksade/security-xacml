/*
  * JBoss, Home of Professional Open Source
  * Copyright 2007, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.test.security.xacml.bindings.web;

import java.net.URI;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.factories.RequestAttributeFactory;
import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;


/**
 *  PEP for the web layer
 *  @author Anil.Saldhana@redhat.com
 *  @since  Jul 10, 2007 
 *  @version $Revision$
 */
public class WebPEP
{

   @SuppressWarnings("unchecked")
   public RequestContext createXACMLRequest(HttpServletRequest request, Principal principal, Group roleGroup)
         throws Exception
   {
      RequestContext requestCtx = RequestResponseContextFactory.createRequestCtx();

      //Create a subject type
      SubjectType subject = new SubjectType();
      subject.getAttribute().add(
            RequestAttributeFactory.createStringAttributeType(XACMLConstants.ATTRIBUTEID_SUBJECT_ID, "jboss.org",
                  principal.getName()));
      Enumeration<Principal> roles = (Enumeration<Principal>) roleGroup.members();
      while (roles.hasMoreElements())
      {
         Principal rolePrincipal = roles.nextElement();
         AttributeType attSubjectID = RequestAttributeFactory.createStringAttributeType(
               XACMLConstants.ATTRIBUTEID_ROLE, "jboss.org", rolePrincipal.getName());
         subject.getAttribute().add(attSubjectID);
      }

      //Create a resource type
      ResourceType resourceType = new ResourceType();
      resourceType.getAttribute().add(
            RequestAttributeFactory.createAnyURIAttributeType(XACMLConstants.ATTRIBUTEID_RESOURCE_ID, null, new URI(
                  request.getRequestURI())));

      //Create an action type
      ActionType actionType = new ActionType();
      actionType.getAttribute().add(
            RequestAttributeFactory
                  .createStringAttributeType(XACMLConstants.ATTRIBUTEID_ACTION_ID, "jboss.org", "read"));

      //Create an Environment Type (Optional)
      EnvironmentType environmentType = new EnvironmentType();
      environmentType.getAttribute().add(
            RequestAttributeFactory.createDateTimeAttributeType(XACMLConstants.ATTRIBUTEID_CURRENT_TIME, null));

      //Create a Request Type
      RequestType requestType = new RequestType();
      requestType.getSubject().add(subject);
      requestType.getResource().add(resourceType);
      requestType.setAction(actionType);
      requestType.setEnvironment(environmentType);

      requestCtx.setRequest(requestType);

      return requestCtx;
   }
}
