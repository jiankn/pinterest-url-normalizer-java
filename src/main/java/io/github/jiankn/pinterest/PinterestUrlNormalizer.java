package io.github.jiankn.pinterest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Offline parser and normalizer for supported Pinterest URLs. */
public final class PinterestUrlNormalizer {
    public enum Kind { PIN, SHORT, PROFILE, BOARD, IDEAS }

    public static final class Result {
        private final Kind kind;
        private final String normalizedUrl;
        private final String identifier;

        private Result(Kind kind, String normalizedUrl, String identifier) {
            this.kind = kind;
            this.normalizedUrl = normalizedUrl;
            this.identifier = identifier;
        }

        public Kind getKind() { return kind; }
        public String getNormalizedUrl() { return normalizedUrl; }
        public String getIdentifier() { return identifier; }
    }

    public static final class PinterestUrlException extends IllegalArgumentException {
        private final String code;

        private PinterestUrlException(String code, String message) {
            super(message);
            this.code = code;
        }

        public String getCode() { return code; }
    }

    private static final String CANONICAL_HOST = "www.pinterest.com";
    private static final Set<String> COUNTRY_HOSTS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
        "pinterest.at", "pinterest.be", "pinterest.ca", "pinterest.ch", "pinterest.cl", "pinterest.co",
        "pinterest.co.kr", "pinterest.co.nz", "pinterest.co.uk", "pinterest.com.au", "pinterest.com.br",
        "pinterest.com.mx", "pinterest.com.pe", "pinterest.com.tr", "pinterest.cz", "pinterest.de",
        "pinterest.dk", "pinterest.es", "pinterest.fi", "pinterest.fr", "pinterest.gr", "pinterest.hu",
        "pinterest.id", "pinterest.ie", "pinterest.it", "pinterest.jp", "pinterest.nl", "pinterest.no",
        "pinterest.ph", "pinterest.pl", "pinterest.pt", "pinterest.ro", "pinterest.se", "pinterest.sk"
    )));
    private static final Set<String> RESERVED = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
        "business", "categories", "explore", "help", "ideas", "login", "logout", "oauth", "pin",
        "resource", "search", "settings", "signup", "today", "topics"
    )));

    private PinterestUrlNormalizer() {}

    public static String normalize(String input) { return parse(input).getNormalizedUrl(); }

    public static boolean isPinterestUrl(String input) {
        try { parse(input); return true; } catch (PinterestUrlException exception) { return false; }
    }

    public static Result parse(String input) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty() || value.length() > 2048 || value.indexOf('\\') >= 0) {
            throw invalid("URL is empty, too long, or contains a backslash");
        }

        final URI uri;
        try { uri = new URI(value); } catch (URISyntaxException exception) { throw invalid("URL could not be parsed"); }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) throw invalid("HTTPS URL required");
        if (uri.getRawUserInfo() != null || (uri.getPort() != -1 && uri.getPort() != 443)) throw invalid("credentials or non-standard port");
        if (uri.getRawPath().indexOf('%') >= 0) throw invalid("encoded paths are not supported");

        String host = uri.getHost().toLowerCase();
        String path = uri.getPath();
        String[] segments = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toArray(String[]::new);

        if ("pin.it".equals(host)) {
            if (segments.length != 1 || !segments[0].matches("[A-Za-z0-9]{2,}")) throw unsupported("unsupported pin.it path");
            return new Result(Kind.SHORT, "https://pin.it/" + segments[0] + "/", segments[0]);
        }
        if (!isPinterestHost(host)) throw invalid("host is not an allowed Pinterest domain");

        if ((segments.length == 2 || segments.length == 3) && "pin".equals(segments[0])) {
            String pin = segments[1];
            String id = pin.matches("[0-9]{1,20}") ? pin : pin.replaceFirst("^.*--([0-9]{1,20})$", "$1");
            if (id.matches("[0-9]{1,20}") && (segments.length == 2 || slug(segments[2]))) {
                return new Result(Kind.PIN, "https://" + CANONICAL_HOST + "/pin/" + id + "/", id);
            }
        }
        if (segments.length == 3 && "ideas".equals(segments[0]) && slug(segments[1]) && segments[2].matches("[0-9]{1,20}")) {
            return new Result(Kind.IDEAS, "https://" + CANONICAL_HOST + "/ideas/" + segments[1] + "/" + segments[2] + "/", segments[2]);
        }
        if (segments.length == 1 && username(segments[0]) && !RESERVED.contains(segments[0].toLowerCase())) {
            return new Result(Kind.PROFILE, "https://" + CANONICAL_HOST + "/" + segments[0] + "/", segments[0]);
        }
        if (segments.length == 2 && username(segments[0]) && slug(segments[1]) && !RESERVED.contains(segments[0].toLowerCase())) {
            return new Result(Kind.BOARD, "https://" + CANONICAL_HOST + "/" + segments[0] + "/" + segments[1] + "/", segments[0]);
        }
        throw unsupported("unsupported Pinterest path");
    }

    public static boolean isPinterestHost(String host) {
        if (host == null) return false;
        String value = host.toLowerCase();
        return value.equals("pinterest.com") || value.equals("www.pinterest.com") || value.equals("m.pinterest.com")
            || COUNTRY_HOSTS.contains(value) || (value.startsWith("www.") && COUNTRY_HOSTS.contains(value.substring(4)));
    }

    private static boolean slug(String value) { return value.matches("[A-Za-z0-9][A-Za-z0-9_-]*"); }
    private static boolean username(String value) { return value.matches("[A-Za-z0-9_][A-Za-z0-9_.-]*"); }
    private static PinterestUrlException invalid(String message) { return new PinterestUrlException("INVALID_URL", message); }
    private static PinterestUrlException unsupported(String message) { return new PinterestUrlException("UNSUPPORTED_URL", message); }
}

