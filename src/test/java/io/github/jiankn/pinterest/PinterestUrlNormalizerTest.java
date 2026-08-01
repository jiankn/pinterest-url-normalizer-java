package io.github.jiankn.pinterest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PinterestUrlNormalizerTest {
    @Test void normalizesRegionalPin() {
        PinterestUrlNormalizer.Result result = PinterestUrlNormalizer.parse(
            "https://www.pinterest.co.uk/pin/example--123456789/?utm_source=test");
        assertEquals(PinterestUrlNormalizer.Kind.PIN, result.getKind());
        assertEquals("123456789", result.getIdentifier());
        assertEquals("https://www.pinterest.com/pin/123456789/", result.getNormalizedUrl());
    }

    @Test void supportsShortProfileBoardAndIdeas() {
        assertEquals(PinterestUrlNormalizer.Kind.SHORT, PinterestUrlNormalizer.parse("https://pin.it/AbC123").getKind());
        assertEquals(PinterestUrlNormalizer.Kind.PROFILE, PinterestUrlNormalizer.parse("https://pinterest.com/savepinner").getKind());
        assertEquals(PinterestUrlNormalizer.Kind.BOARD, PinterestUrlNormalizer.parse("https://pinterest.com/savepinner/recipes").getKind());
        assertEquals(PinterestUrlNormalizer.Kind.IDEAS, PinterestUrlNormalizer.parse("https://pinterest.com/ideas/home-decor/12345").getKind());
    }

    @Test void rejectsUnsafeAndLookalikeUrls() {
        assertFalse(PinterestUrlNormalizer.isPinterestUrl("http://pinterest.com/pin/123"));
        assertFalse(PinterestUrlNormalizer.isPinterestUrl("https://pinterest.com.evil.test/pin/123"));
        assertFalse(PinterestUrlNormalizer.isPinterestUrl("https://user:p@pinterest.com/pin/123"));
        assertFalse(PinterestUrlNormalizer.isPinterestUrl("https://pinterest.com:444/pin/123"));
        assertFalse(PinterestUrlNormalizer.isPinterestUrl("https://pinterest.com/pin/%31%32%33"));
        assertThrows(PinterestUrlNormalizer.PinterestUrlException.class,
            () -> PinterestUrlNormalizer.parse("https://pinterest.com/search/pins"));
    }
}

