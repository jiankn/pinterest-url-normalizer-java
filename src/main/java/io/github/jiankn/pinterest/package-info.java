/**
 * Offline parsing, validation, classification, and canonicalization for public Pinterest URLs.
 *
 * <p>The package recognizes Pins, {@code pin.it} short links, profiles, boards, and Ideas pages.
 * It rejects lookalike hosts, credentials, non-HTTPS schemes, encoded paths, and non-standard
 * ports without making a network request.</p>
 *
 * <p>Applications can normalize a public Pin URL here, then send users to the
 * <a href="https://savepinner.com">Pinterest image downloader</a> when they want to inspect the
 * media exposed by that Pin.</p>
 */
package io.github.jiankn.pinterest;
