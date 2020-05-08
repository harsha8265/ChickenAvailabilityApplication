package com.mobile.chickenavailabilityapplication.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.LruCache;

public class TypefaceCache {

	public static final int TYPEFACE_REGULAR = 0;
	public static final int TYPEFACE_BOLD = 1;
	
	private static final LruCache<String, Typeface> CACHE = new LruCache<String, Typeface>(3);

	private static final String LATO_REGULAR = "font/Lato-Regular.ttf";
	private static final String LATO_BOLD = "font/Lato-Bold.ttf";
	private static final String VARELA_REGULAR = "font/VarelaRound-Regular.ttf";

	public static Typeface get(AssetManager manager, int typefaceCode) {
		synchronized (CACHE) {

			String typefaceName = getTypefaceName(typefaceCode);

			if (CACHE.get(typefaceName) == null) {
				Typeface t = Typeface.createFromAsset(manager, typefaceName);
				CACHE.put(typefaceName, t);
			}
			return CACHE.get(typefaceName);
		}
	}

	private static String getTypefaceName(int typefaceCode) {
		switch (typefaceCode) {
		case 0:
			return LATO_REGULAR;

		case 1:
			return LATO_BOLD;
		
		case 3:
			return VARELA_REGULAR;

		default:
			return LATO_REGULAR;
		}
	}
	
	public static void clearCache() {
		CACHE.evictAll();
	}
}