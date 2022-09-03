//package com.crio.xurl;
package com.crio.shorturl;
import java.util.HashMap;


class XUrlImpl implements XUrl{

    private  Long registeredUrlCount = 0L;
    private final HashMap<String, Long> UrlToIndex = new HashMap<String, Long>();
    private final HashMap<Long, String> IndexToUrl = new HashMap<Long, String>();
    private final HashMap<String, String> LongToShort = new HashMap<String, String>();
    private final HashMap<String, String> ShortToLong = new HashMap<String, String>();
    private final HashMap<String, Integer> CountFreq = new HashMap<String, Integer>();

    private final String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String encode(Long value) {
        final StringBuilder result = new StringBuilder();
        while (value != 0) {
            result.append(base62.charAt((int) (value % 62)));
            value /= 62;
        }
        while (result.length() < 9)
            result.append(0);
        return result.reverse().toString();
    }


    private Long getValue(final String shortUrl) {
        final String base62String = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);
        Long value = 0L;
        for (int i = 0; i < base62String.length(); i++) {
            value = value * 62 + base62.indexOf("" + base62String.charAt(i));
        }
        return value;
    }

    public String registerNewUrl(final String longUrl) {
        if (UrlToIndex.containsKey(longUrl)) {
            return "http://short.url/" + encode(UrlToIndex.get(longUrl));
        } else {
            IndexToUrl.put(registeredUrlCount, longUrl);
            UrlToIndex.put(longUrl, registeredUrlCount);
            while (IndexToUrl.containsKey(registeredUrlCount)) {
                registeredUrlCount++;
            }
            final String url = "http://short.url/" + encode(UrlToIndex.get(longUrl));
            LongToShort.put(longUrl, url);
            ShortToLong.put(url, longUrl);
            return url;
        }
    }

    public String registerNewUrl(final String longUrl, final String shortUrl) {
        if (ShortToLong.containsKey(shortUrl))
            return null;
        else {
            final Long value = getValue(shortUrl);
            LongToShort.put(longUrl, shortUrl);
            ShortToLong.put(shortUrl, longUrl);
            IndexToUrl.put(value, longUrl);
            UrlToIndex.put(longUrl, value);
            return shortUrl;
        }
    }

    public String getUrl(final String shortUrl) {
        if (ShortToLong.containsKey(shortUrl)) {
            final String longUrl = ShortToLong.get(shortUrl);
            if (CountFreq.get(longUrl) == null) {
                CountFreq.put(longUrl, 1);
            } else {
                final int freq = CountFreq.get(longUrl);
                CountFreq.remove(longUrl);
                CountFreq.put(longUrl, freq + 1);
            }
            return longUrl;
        } else {
            return null;
        }
    }

    public String delete(final String longUrl) {
        final Long value = UrlToIndex.get(longUrl);
        final String shortUrl = LongToShort.get(longUrl);
        LongToShort.remove(longUrl);
        ShortToLong.remove(shortUrl);
        UrlToIndex.remove(longUrl);
        IndexToUrl.remove(value);
        return shortUrl;
    }

    public Integer getHitCount(final String longUrl) {
        if(CountFreq.get(longUrl)==null){
            return 0;
        }
        return CountFreq.get(longUrl);
    }
}