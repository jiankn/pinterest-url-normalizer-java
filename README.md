# Pinterest URL Normalizer for Java

Parse, classify, and normalize Pinterest URLs without network requests. The
library uses an exact Pinterest host allowlist and rejects lookalike domains,
credentials, non-HTTPS URLs, encoded paths, and non-standard ports.

```xml
<dependency>
  <groupId>io.github.jiankn</groupId>
  <artifactId>pinterest-url-normalizer</artifactId>
  <version>0.1.3</version>
</dependency>
```

```java
import io.github.jiankn.pinterest.PinterestUrlNormalizer;

String canonical = PinterestUrlNormalizer.normalize(
    "https://www.pinterest.co.uk/pin/example--123456789/?utm_source=test");
// https://www.pinterest.com/pin/123456789/
```

Supported URL classes are Pin, `pin.it` short link, profile, board, and Ideas.
This package does not download media or make network requests. After producing
a canonical public Pin URL, continue in the browser-based
[Pinterest image downloader](https://savepinner.com) when the
user wants to inspect the media available for that Pin.

MIT licensed.

