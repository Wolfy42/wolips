/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.ui.support;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.SafeRunnable;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.preferences.PreferencesMessages;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

/**
 * Constructs a tree made of <code>TreeContentProviderNode</code>, representing
 * several details about WOLips.
 * 
 */
public class SupportContentProvider extends AbstractTreeContentProvider {
	/**
		 * Collects resource info. Calls all other <code>extract...</code> methods. 
		 *
		 */
	protected void extractInfo() {
		getRootNode().addChild(
			createNode(SupportMessages.getString("WOLips.support.start")));
		extractWOLipsUtilsInfo();
		extractWOVariablesInfo();
		extractEnvironmentInfo();
		extractPersistentProperties();
		getRootNode().addChild(createNode(SupportMessages.getString("WOLips.support.end"))); //$NON-NLS-1$
	}

	protected void extractWOLipsUtilsInfo() {
		TreeContentProviderNode wolipsutilsNode =
			createNode(SupportMessages.getString("WOLips.support.WOLipsUtils"));
		getRootNode().addChild(wolipsutilsNode);
		wolipsutilsNode.addChild(
			createNode(
				SupportMessages.getString(
					"WOLips.support.WOLipsUtils.woTemplateDirectory"),
				WOLipsPlugin.WO_TEMPLATE_DIRECTORY));
		wolipsutilsNode.addChild(
			createNode(
				SupportMessages.getString(
					"WOLips.support.WOLipsUtils.woTemplateFiles"),
				WOLipsPlugin.WO_TEMPLATE_FILES));
		wolipsutilsNode.addChild(
			createNode(
				SupportMessages.getString(
					"WOLips.support.WOLipsUtils.woTemplateProject"),
				WOLipsPlugin.WO_TEMPLATE_PROJECT));
	}
	/**
	 * Extracts WOVariables info.
	 * 
	*/
	protected void extractWOVariablesInfo() {
		TreeContentProviderNode wovariablesNode =
			createNode(SupportMessages.getString("WOLips.support.WOVariables"));
		getRootNode().addChild(wovariablesNode);
	}

	/**
	 * Extracts Environment info.
	 * 
	*/
	protected void extractEnvironmentInfo() {
		TreeContentProviderNode environmentNode =
			createNode(SupportMessages.getString("WOLips.support.Environment"));
		getRootNode().addChild(environmentNode);
		environmentNode.addChild(
			createNode(
				SupportMessages.getString(
					"WOLips.support.Environment.localRoot"),
					VariablesPlugin.getDefault().getLocalRoot().toOSString()));
		environmentNode.addChild(
			createNode(
				SupportMessages.getString(
					"WOLips.support.Environment.nextRoot"),
					VariablesPlugin.getDefault().getSystemRoot().toOSString()));
	}

	/**
	 * Collects persistent properties information.
	 * 
	 */
	protected void extractPersistentProperties() {
		TreeContentProviderNode preferencesNode =
			createNode(SupportMessages.getString("WOLips.support.Preferences"));
		getRootNode().addChild(preferencesNode);
		preferencesNode.addChild(
			createNode(
				PreferencesMessages.getString(
					"Preferences.RunWOBuilderOnBuild.Label"),
				Preferences.getPREF_RUN_WOBUILDER_ON_BUILD() + ""));
		preferencesNode.addChild(
			createNode(
				PreferencesMessages.getString(
					"Preferences.NSProjectSearchPath.Label"),
				Preferences.getPREF_NS_PROJECT_SEARCH_PATH()));
	}

	/**
	 * Reconstructs this content provider data model upon the provided input object.
	 *  
	 * @param input the new input object - must not be null
	 */
	protected void rebuild(Object input) {
		Platform.run(new SafeRunnable() {
			public void run() throws Exception {
				extractInfo();
			}
		});
	}

	/**
	 * Returns true if the input is a resource.
	 * 
	 * @param input an input object
	 */
	protected boolean acceptInput(Object input) {
		return input instanceof SupportContentProvider;
	}

}