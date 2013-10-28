/*
 * TopStack (c) Copyright 2012-2013 Transcend Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msi.tough.rdsquery;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.amazonaws.util.DateUtils;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.DateHelper;
import com.msi.tough.query.ErrorResponse;
import com.transcend.rds.message.RDSMessage.Event.EventType;

public class QueryUtilV2 {
    public static void addCdata(final XMLNode node, final String tag,
            final String value) {
        if (value != null && value.length() > 0) {
            final XMLNode n = new XMLNode(tag);
            node.addNode(n);
            final XMLNode n0 = new XMLNode();
            n.addNode(n0);
            n0.setPlaintext("<![CDATA[\n" + value + "\n]]>");
        }
    }

    public static XMLNode addNode(final XMLNode node, final String tag) {
        final XMLNode n = new XMLNode(tag);
        node.addNode(n);
        return n;
    }

    public static void addNode(final XMLNode n, final String tag,
            final EventType value) {
        if (value != null) {
            addNode(n, tag, value.toString());
        }
    }

    public static void addNode(final XMLNode node, final String tag,
            final Boolean value) {
        if (value != null) {
            addNode(node, tag, "" + value);
        }
    }

    public static void addNode(final XMLNode n, final String tag,
            final Calendar value) {
        if (value != null) {
            addNode(n, tag, new DateUtils().formatIso8601Date(value.getTime()));
        }
    }

    public static void addNode(final XMLNode n, final String tag,
            final Date value) {
        if (value != null) {
            addNode(n, tag, new DateUtils().formatIso8601Date(value));
        }
    }

    public static void addNode(final XMLNode n, final String tag,
            final Double value) {
        if (value != null) {
            addNode(n, tag, value.toString());
        }
    }

    public static void addNode(final XMLNode n, final String tag,
            final Integer value) {
        if (value != null) {
            addNode(n, tag, value.toString());
        }
    }

    public static void addNode(final XMLNode n, final String tag,
            final Long value) {
        if (value != null) {
            addNode(n, tag, value.toString());
        }
    }

    public static void addNode(final XMLNode node, final String tag,
            final String value) {
        if (value != null && value.length() > 0) {
            final XMLNode n = new XMLNode(tag);
            node.addNode(n);
            final XMLNode n0 = new XMLNode();
            n.addNode(n0);
            n0.setPlaintext(value);
        }
    }

    public static void addResponseMetadata(final XMLNode parent,
            final String requestId) {
        final XMLNode mdNode = QueryUtilV2.addNode(parent, "ResponseMetadata");
        QueryUtilV2.addNode(mdNode, "RequestId", requestId);
    }

    public static boolean getBoolean(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            return false;
        }
        return Boolean.parseBoolean(in.get(key)[0]);
    }

    public static boolean getBoolean(final Map<String, String[]> in,
            final String key, final boolean defaultValue) {
        if (in.get(key) == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(in.get(key)[0]);
    }

    public static double getDouble(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            return 0;
        }
        return Double.parseDouble(in.get(key)[0]);
    }

    public static int getInt(final Map<String, String[]> in, final String key) {
        return getInt(in, key, 0);
    }

    public static int getInt(final Map<String, String[]> in, final String key,
            final int defaultValue) throws NumberFormatException {
        if (in.get(key) == null) {
            return defaultValue;
        }
        return Integer.parseInt(in.get(key)[0]);
    }

    public static Integer getIntObject(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            return null;
        }
        return Integer.parseInt(in.get(key)[0]);
    }

    public static Long getLong(final Map<String, String[]> in, final String key) {
        if (in.get(key) == null) {
            return null;
        }
        return Long.parseLong(in.get(key)[0]);
    }

    public static String getString(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            return "";
        }
        return in.get(key)[0];
    }

    public static Collection<String> getStringArray(
            final Map<String, String[]> in, final String key, final int max) {
        final Collection<String> stringArray = new ArrayList<String>();

        for (int i = 1; i <= max; i++) {
            final String member = key + "." + i;
            if (in.get(member) == null) {
                break;
            }
            stringArray.add(QueryUtilV2.getString(in, member));
        }

        return stringArray;
    }

    public static String padAccId12Digits(final long accIdLong) {
        final String accId = "" + accIdLong;
        final int lim = 12 - accId.length();
        String result = "";
        for (int i = 0; i < lim; ++i) {
            result += '0';
        }
        return result + accId;
    }

    public static String padAccId12Digits(final String accId) {
        final int lim = 12 - accId.length();
        String result = "";
        for (int i = 0; i < lim; ++i) {
            result += '0';
        }
        return result + accId;
    }

    public static boolean requiredBoolean(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            throw ErrorResponse.missingParameter();
        }
        return Boolean.parseBoolean(in.get(key)[0]);
    }

    public static Date requiredDate(final Map<String, String[]> in,
            final String key) {
        final String[] cgi = in.get(key);
        if (cgi == null) {
            throw ErrorResponse.missingParameter();
        }
        return DateHelper.getCalendarFromISO8601String(cgi[0],
                TimeZone.getTimeZone("GMT")).getTime();
    }

    public static int requiredInt(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            throw ErrorResponse.missingParameter();
        }
        return Integer.parseInt(in.get(key)[0]);
    }

    public static String requiredString(final Map<String, String[]> in,
            final String key) {
        if (in.get(key) == null) {
            throw ErrorResponse.missingParameter();
        }
        return in.get(key)[0];
    }

    public static Collection<String> requiredStringArray(
            final Map<String, String[]> in, final String key, final int max) {
        final Collection<String> stringArray = new ArrayList<String>();

        for (int i = 1; i <= max; i++) {
            final String member = key + "." + i;
            if (in.get(member) == null) {
                break;
            }

            stringArray.add(QueryUtilV2.requiredString(in, member));
        }

        if (in.size() == 0) {
            throw ErrorResponse.missingParameter();
        }

        return stringArray;
    }
}
