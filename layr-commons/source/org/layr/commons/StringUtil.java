/**
 * Copyright 2013 Miere Liniel Teixeira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.layr.commons;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static String stripURLFirstSlash( String url ){
		return url.replaceAll("^/+", "");
	}
	
	public static String match(String target, String needle) {
		HashMap<String, String> accents = 
			new HashMap<String, String>();

		accents.put("a", "[aàáâãäåæ]");
		accents.put("c", "[cç]");
		accents.put("e", "[eèéêëæ]");
		accents.put("i", "[iìíîï]");
		accents.put("n", "[nñ]");
		accents.put("o", "[oòóôõöø]");
		accents.put("s", "[sß]");
		accents.put("u", "[uùúûü]");
		accents.put("y", "[yÿ]");

		for ( String key : accents.keySet() )
			needle = needle.replaceAll(key, accents.get(key));

        Matcher matcher = Pattern.compile(needle,Pattern.CASE_INSENSITIVE).matcher(target);
        if (!matcher.find())
        	return null;
        
        return matcher.group();
	}
	
	public static String join(String[] strings, String separator) {
		StringBuffer buffer = new StringBuffer();
		for (String string : strings)
			buffer.append(String.format("%s%s", string, separator));
		buffer = buffer.replace(buffer.length()-1, buffer.length(), "");
		return buffer.toString();
	}

	public static String escape(String s) {
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
