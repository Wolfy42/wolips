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
package org.objectstyle.woproject.ant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSetCollection;

/**
 * Abstract helper class that defines the algorithm for 
 * autogeneration of standard project files needed for deployment
 * of the applications and frameworks. In a way it "formats"
 * deployed project.
 *
 * @author Andrei Adamchik
 */
public abstract class ProjectFormat {
	protected static final String endLine =
		System.getProperty("line.separator");

	protected WOTask task;

	/** 
	 * Creates new TemplateFilter and initializes it with the name
	 * of the project being built.
	 */
	public ProjectFormat(WOTask task) {
		this.task = task;
	}

	/** 
	 * Returns a name of WebObjects project being built. 
	 */
	public String getName() {
		return task.getName();
	}

	/** 
	 * Creates all needed files based on WOProject templates. 
	 * This is a main worker method. 
	 */
	public void processTemplates() throws IOException {
		Iterator it = fileIterator();

		while (it.hasNext()) {
			String targetName = (String) it.next();
			String templName = templateForTarget(targetName);
			FilterSetCollection filters = filtersForTarget(targetName);

			InputStream template =
				this.getClass().getClassLoader().getResourceAsStream(templName);
			File target = new File(targetName);
			copyFile(template, target, filters);
		}
	}

	/** 
	 * Returns an iterator over String objects that
	 * specify paths of the files to be created during
	 * the build process.
	 */
	public abstract Iterator fileIterator();

	/** 
	 * Returns a path to the template that should be used to
	 * build a target file.
	 */
	public abstract String templateForTarget(String targetName)
		throws BuildException;

	/** 
	 * Returns a FilterSetCollection that should be applied when
	 * generating a target file.
	 */
	public abstract FilterSetCollection filtersForTarget(String targetName)
		throws BuildException;

	/**
	 * Convienence method to copy a file from a source to a
	 * destination specifying if token filtering must be used.
	 *
	 * <p><i>This method is copied from Ant FileUtils with some changes
	 * and simplifications. FileUtils can't be used directly, since its 
	 * API doesn't allow InputStreams for the source file.</i></p>
	 *  
	 * @throws IOException 
	 */
	public void copyFile(
		InputStream src,
		File destFile,
		FilterSetCollection filters)
		throws IOException {

		if (destFile.exists() && destFile.isFile()) {
			destFile.delete();
		}

		// ensure that parent dir of dest file exists!
		// not using getParentFile method to stay 1.1 compat
		File parent = new File(destFile.getParent());
		if (!parent.exists()) {
			parent.mkdirs();
		}

		if (filters != null && filters.hasFilters()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(src));
			BufferedWriter out = new BufferedWriter(new FileWriter(destFile));

			int length;
			String newline = null;
			String line = in.readLine();
			while (line != null) {
				if (line.length() == 0) {
					out.newLine();
				} else {
					newline = filters.replaceTokens(line);
					out.write(newline);
					out.newLine();
				}
				line = in.readLine();
			}

			out.close();
			in.close();
		} else {
			FileOutputStream out = new FileOutputStream(destFile);

			byte[] buffer = new byte[8 * 1024];
			int count = 0;
			do {
				out.write(buffer, 0, count);
				count = src.read(buffer, 0, buffer.length);
			} while (count != -1);

			src.close();
			out.close();
		}
	}

	/** 
	 * Returns an iterator over a String array.
	 */
	public Iterator stringArrayIterator(String[] strs) {
		ArrayList list = new ArrayList(strs.length);
		for (int i = 0; i < strs.length; i++) {
			list.add(strs[i]);
		}

		return list.iterator();
	}

	/** 
	 * Returns an iterator with a single String element.
	 */
	public Iterator stringIterator(String str) {
		ArrayList list = new ArrayList(1);
		list.add(str);
		return list.iterator();
	}

	/** 
	 * Returns a string that can be used in Info.plist
	 * file to indicate JARs required by the project.
	 */
	public String libString(Iterator extLibs) {
		StringBuffer buf = new StringBuffer();

		buf.append("<array>");
		if (task.hasClasses()) {
			buf
				.append(endLine)
				.append("\t\t<string>")
				.append(getName().toLowerCase() + ".jar")
				.append("</string>");
		}

		if (extLibs != null) {
			while (extLibs.hasNext()) {
				String libFile = (String) extLibs.next();
				buf.append(endLine).append("\t\t<string>");
				buf.append(libFile);
				buf.append("</string>");
			}
		}
		buf.append(endLine).append("\t</array>");
		return buf.toString();
	}
}