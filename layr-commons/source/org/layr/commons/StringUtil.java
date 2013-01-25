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
//			case '&':
//				sb.append("&amp;");
//				break;
			case '"':
				sb.append("&quot;");
				break;
			case 'à':
				sb.append("&agrave;");
				break;
			case 'À':
				sb.append("&Agrave;");
				break;
			case 'â':
				sb.append("&acirc;");
				break;
			case 'Â':
				sb.append("&Acirc;");
				break;
			case 'ä':
				sb.append("&auml;");
				break;
			case 'Ä':
				sb.append("&Auml;");
				break;
			case 'å':
				sb.append("&aring;");
				break;
			case 'Å':
				sb.append("&Aring;");
				break;
			case 'æ':
				sb.append("&aelig;");
				break;
			case 'Æ':
				sb.append("&AElig;");
				break;
			case 'ç':
				sb.append("&ccedil;");
				break;
			case 'Ç':
				sb.append("&Ccedil;");
				break;
			case 'é':
				sb.append("&eacute;");
				break;
			case 'É':
				sb.append("&Eacute;");
				break;
			case 'è':
				sb.append("&egrave;");
				break;
			case 'È':
				sb.append("&Egrave;");
				break;
			case 'ê':
				sb.append("&ecirc;");
				break;
			case 'Ê':
				sb.append("&Ecirc;");
				break;
			case 'ë':
				sb.append("&euml;");
				break;
			case 'Ë':
				sb.append("&Euml;");
				break;
			case 'ï':
				sb.append("&iuml;");
				break;
			case 'Ï':
				sb.append("&Iuml;");
				break;
			case 'ô':
				sb.append("&ocirc;");
				break;
			case 'Ô':
				sb.append("&Ocirc;");
				break;
			case 'ö':
				sb.append("&ouml;");
				break;
			case 'Ö':
				sb.append("&Ouml;");
				break;
			case 'ø':
				sb.append("&oslash;");
				break;
			case 'Ø':
				sb.append("&Oslash;");
				break;
			case 'ß':
				sb.append("&szlig;");
				break;
			case 'ù':
				sb.append("&ugrave;");
				break;
			case 'Ù':
				sb.append("&Ugrave;");
				break;
			case 'û':
				sb.append("&ucirc;");
				break;
			case 'Û':
				sb.append("&Ucirc;");
				break;
			case 'ü':
				sb.append("&uuml;");
				break;
			case 'Ü':
				sb.append("&Uuml;");
				break;
			case '®':
				sb.append("&reg;");
				break;
			case '©':
				sb.append("&copy;");
				break;
			case '€':
				sb.append("&euro;");
				break;
			/*case ' ':
				sb.append("&nbsp;");
				break;*/

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
