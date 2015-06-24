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

New way of handling file uploads
--------------------------------
Would it be nice if uploaded files of a multipart request could be injected
directly into controller method just like path or query parameters?
Ninja allows you to do so -- to inject uploaded file `@FileParam` annotation
is used. Extracted type of this annotation is `java.io.InputStream` so that
you can read contents of an uploaded file in any way you like.

In the case given above, the controller would look like this:
```java
public Result uploadFinish(Context context, @FileParam("upfile") InputStream stream) {
    if(stream != null) {
	// use stream to read file contents
    }
    return Results.ok();
}
```

Besides injecting uploaded files into your controller methods, you can also
inject simple key/value form fields of a multipart request via annotations
`@Param` and `@Params`.


File upload settings
--------------------
By default files of a multipart request are stored both in memory and on file system.
Commons-upload has a nice implementation that stores small amounts of data first
in memory and only after some threshold value the data is dumped to file system
(More about this read [here](https://commons.apache.org/proper/commons-fileupload/using.html)).

There may be environments where access to file system is restricted or there
is no access at all.
In a former case you can setup a target directory with write access rights
where uploaded files will be temporarily stored.
This is done via `file.uploads.directory` property.

Please note that specifying target directory does not mean that uploaded files
will remain there. All uploaded files are stored in target directory temporarily
and cleaned up as long as the request context is not used anymore. Make sure you
dump uploaded file streams somewhere you need.

In a latter case when there is no access to file system at all, Ninja can be setup
to handle file uploads totally in memory. To do this you have to set property
`file.uploads.in_memory` to true. This property has a default value of false.
When handling file uploads in memory consider setting the following properties:

* `file.uploads.file.size.max`: the maximum allowed size of a single uploaded file
in bytes (defaults to 10 MB)
* `file.uploads.total.size.max`: the maximum allowed size of a complete request, i.e. size of
all uploaded files


Advanced usage
--------------

Ninja uses Apache Commons-upload to implement the upload functionality. Therefore
you can also refer to their excellent manual for more information at: 
http://commons.apache.org/proper/commons-fileupload/

