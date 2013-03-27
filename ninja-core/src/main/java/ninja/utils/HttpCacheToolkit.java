package ninja.utils;

import javax.annotation.Nullable;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

@ImplementedBy(HttpCacheToolkitImpl.class)
public interface HttpCacheToolkit {

    /**
     * Checks if resource has been modified.
     * Checks via etag or lastModified when etag not present.
     * 
     * @param etag (can be null)
     * @param lastModified (can be null)
     * @param context the Context of this request
     * @return true if modified / false if not.
     */
    boolean isModified(@Nullable String etag, @Nullable Long lastModified, Context context);

    /**
     * Adds etag to result.
     * 
     * @param context The context
     * @param result The result to populate with etag
     * @param lastModified Last modified => In that case used to generate etag.
     */
    void addEtag(Context context, Result result, Long lastModified);
    
}
