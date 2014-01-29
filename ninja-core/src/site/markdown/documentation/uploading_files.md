Uploading files
===============

Handling multipart/form-data uploads
------------------------------------

Ninja offers direct access to file uploads in your controller method. This 
is accomplished via the Context object and two of its methods: 
<code>context.isMultipart()</code> to check if we are dealing with a multipart upload
and <code>context.getFileItemIterator()</code> to iterate through potential
files.


Usually you render a form that links to the controller that will handle the
upload. In the following case we are using a route to <code>/uploadFinish</code>.

<pre class="prettyprint">
&lt;form method=&quot;post&quot; enctype=&quot;multipart/form-data&quot; action=&quot;/uploadFinish&quot;&gt;
    Please specify file to upload: &lt;input type=&quot;file&quot; name=&quot;upfile&quot;&gt;&lt;br /&gt;
    &lt;input type=&quot;submit&quot; value=&quot;submit&quot;&gt;
&lt;/form&gt;
</pre>

The controller at <code>/uploadFinish</code> will then handle the upload:

<pre class="prettyprint">
public Result uploadFinish(Context context) throws Exception {

    // Make sure the context really is a multipart context...
    if (context.isMultipart()) {

        // This is the iterator we can use to iterate over the
        // contents of the request.
        FileItemIterator fileItemIterator = context
                .getFileItemIterator();

        while (fileItemIterator.hasNext()) {

            FileItemStream item = fileItemIterator.next();

            String name = item.getFieldName();
            InputStream stream = item.openStream();

            String contentType = item.getContentType();

            if (item.isFormField()) {

                // do something with the form field

            } else {

                // process file as input stream

            }
        }

    }
    
    // We always return ok. You don't want to do that in production ;)
    return Results.ok();

}
</pre>

Advanced usage
--------------

Ninja uses Apache Commons-upload to implement the upload functionality. Therefore
you can also refer to their excellent manual for more information at: 
http://commons.apache.org/proper/commons-fileupload/

