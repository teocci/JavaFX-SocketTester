package com.github.teocci.socket.test;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jun-14
 */
public class ContainsAnalysis
{
    private static final String SRC_STRING = "OMX.google.h264.encoder";
    private static final String SUB_STRING = "omx.google";

    // The cached pattern for case 5
    private static final Pattern P = Pattern.compile(Pattern.quote("omx.google"), Pattern.CASE_INSENSITIVE);

    // The cached substring for case 3
    private static final String S = "omx.google".toLowerCase();

    /**
     * Case 1 utilizing String.regionMatches()
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsIgnoreCaseManual(String src, String sub)
    {
        final int length = sub.length();
        // Empty string is contained
        if (length == 0) return true;

        final char firstLo = Character.toLowerCase(sub.charAt(0));
        final char firstUp = Character.toUpperCase(sub.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp) continue;

            if (src.regionMatches(true, i, sub, 0, length)) return true;
        }

        return false;
    }

    /**
     * Case 2 with 2x toLowerCase() and contains()
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsConverting(String src, String sub)
    {
        return src.toLowerCase().contains(sub.toLowerCase());
    }

    /**
     * Case 3 with pre-cached substring and 1x toLowerCase() and contains()
     *
     * @param src string
     * @return true is substring exits
     */
    public static boolean containsConverting(String src)
    {
        return src.toLowerCase().contains(S);
    }

    /**
     * Case 4 with regexp
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsIgnoreCaseRegexp(String src, String sub)
    {
        return Pattern.compile(Pattern.quote(sub), Pattern.CASE_INSENSITIVE)
                .matcher(src)
                .find();
    }

    /**
     * Case 5 with pre-cached Pattern
     *
     * @param src string
     * @return true is substring exits
     */
    public static boolean containsIgnoreCaseRegexp(String src)
    {
        return P.matcher(src).find();
    }

    /**
     * Case 6 with pre-cached Pattern
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsIgnoreCasePureRegexp(String src, String sub)
    {
        return src.matches(".*(?i)" + sub + ".*");
    }


    /**
     * Case 7 with String Utils from Apache Commons
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsIgnoreCaseApacheCommons(String src, String sub)
    {
        return StringUtils.containsIgnoreCase(src, sub);
    }

    /**
     * Case 8 utilizing String.regionMatches()
     *
     * @param src string
     * @param sub substring
     * @return true is substring exits
     */
    public static boolean containsIgnoreCaseRegionMatches(String src, String sub)
    {
        if (src == null || sub == null) {
            return false;
        }

        final int length = sub.length();
        final int max = src.length() - length;
        for (int i = 0; i <= max; i++) {
            if (src.regionMatches(true, i, sub, 0, length)) return true;
        }

        return false;
    }

    /**
     * Case 9 with CharSequence
     *
     * @param src string
     * @param sub substring
     * @return true if the CharSequence contains the search CharSequence irrespective of
     * case or false if not or {@code null} string input
     */
    public static boolean containsIgnoreCaseCharSequence(final CharSequence src, final CharSequence sub)
    {
        if (src == null || sub == null) return false;

        final int len = sub.length();
        final int max = src.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(src, true, i, sub, 0, len)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Green implementation of regionMatches.
     *
     * @param cs         the {@code CharSequence} to be processed
     * @param ignoreCase whether or not to be case insensitive
     * @param thisStart  the index to start on the {@code cs} CharSequence
     * @param substring  the {@code CharSequence} to be looked for
     * @param start      the index to start on the {@code substring} CharSequence
     * @param length     character length of the region
     * @return whether the region matched
     */
    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                                        final CharSequence substring, final int start, final int length)
    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    private static void evaluateCode(int code)
    {
        long start, end;
        final int N = 10_000_000;
        start = System.nanoTime();
        switch (code) {
            case 1:
                for (int i = 0; i < N; i++) Assert.isTrue(containsIgnoreCaseManual(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 2:
                for (int i = 0; i < N; i++) Assert.isTrue(containsConverting(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 3:
                for (int i = 0; i < N; i++) Assert.isTrue(containsConverting(SRC_STRING), "Fail");
                break;
            case 4:
                for (int i = 0; i < N; i++) Assert.isTrue(containsIgnoreCaseRegexp(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 5:
                for (int i = 0; i < N; i++) Assert.isTrue(containsIgnoreCaseRegexp(SRC_STRING), "Fail");
                break;
            case 6:
                for (int i = 0; i < N; i++) Assert.isTrue(containsIgnoreCasePureRegexp(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 7:
                for (int i = 0; i < N; i++)
                    Assert.isTrue(containsIgnoreCaseApacheCommons(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 8:
                for (int i = 0; i < N; i++)
                    Assert.isTrue(containsIgnoreCaseRegionMatches(SRC_STRING, SUB_STRING), "Fail");
                break;
            case 9:
                for (int i = 0; i < N; i++)
                    Assert.isTrue(containsIgnoreCaseCharSequence(SRC_STRING, SUB_STRING), "Fail");
                break;

            default:
        }
        end = System.nanoTime();
        System.out.println("Case " + code + " took " + ((end - start) / 1000000) + "ms");
    }

    // Main method: performs speed analysis on different contains methods
    // (case ignored)
    public static void main(String[] args)
    {
        for (int i = 0; i < 9; i++) evaluateCode(i + 1);
    }
}
