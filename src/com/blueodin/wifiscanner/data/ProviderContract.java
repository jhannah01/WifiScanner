package com.blueodin.wifiscanner.data;

import android.net.Uri;

public class ProviderContract {
	public static final String AUTHORITY = "com.blueodin.wifiscanner.provider";
	public static final String BASE_URI = "content://" + AUTHORITY + "/";
	
	public static final class Runs {
		public static final String PATH = "runs";
		public static final String PATH_BY_ID = "run/";
				
		public static final Uri CONTENT_URI = Uri.parse(BASE_URI + PATH);
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(BASE_URI + PATH_BY_ID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(BASE_URI + PATH_BY_ID + "#");

        public static Uri uriById(long id) {
        	return Uri.parse(BASE_URI + PATH_BY_ID + id);
        }
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".runs";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + ".runs";
        
        public static final String DEFAULT_ORDER_BY = DBHelper.RunsColumns.END_TIMESTAMP + " DESC";
	}
	
	public static final class Results {
		public static final String PATH = "results";
		public static final String PATH_BY_ID = "result/";
		
		public static final Uri CONTENT_URI = Uri.parse(BASE_URI + PATH);
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(BASE_URI + PATH_BY_ID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(BASE_URI + PATH_BY_ID + "#");
        
        public static Uri uriById(long id) {
        	return Uri.parse(BASE_URI + PATH_BY_ID + id);
        }
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".results";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + ".results";
        
        public static final String DEFAULT_ORDER_BY = DBHelper.ResultsColumns.TIMESTAMP + " DESC";
	}
} 
