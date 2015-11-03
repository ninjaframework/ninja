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

The manual default way
--------------

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

The integrated new way
--------------

The controller at <code>/uploadFinish</code> can automatically handle the upload, and return either a FileItem, InputStream or File.
Using FileItem allows provides access to additional properties, like <code>getFileName()</code> to get the original file name sent by the browser.

<pre class="prettyprint">
public Result uploadFinish(Context context, @Param("upfile") FileItem upfile) throws Exception {
}
</pre>
or
<pre class="prettyprint">
public Result uploadFinish(Context context, @Param("upfile") InputStream upfile) throws Exception {
}
</pre>
or
<pre class="prettyprint">
public Result uploadFinish(Context context, @Param("upfile") File upfile) throws Exception {
}
</pre>
or
<pre class="prettyprint">
public Result uploadFinish(Context context) throws Exception {
    FileItem upfile = context.getParameterAsFileItem("upfile");
}
</pre>

### In-memory or disk based file ?

Ninja comes with two providers to choose between in-memory and disk file for storing uploaded content:
- <code>MemoryFileItemProvider</code>, to stores the file bytes into memory
- <code>DiskFileItemProvider</code>, to stores the file content to disk in a temporary folder, that can be set using the <code>uploads.temp_folder</code> ninja property

In all case, you can limit the size of each file using <code>uploads.max_file_size</code> and the total size of all files using <code>uploads.max_total_size</code> ninja properties.

<div class="alert alert-info">
When using disk base storage, uploaded files are automatically deleted at the end of the request, to prevent file system exhaustion. Because of this, you must copy (or move) the file somewhere else before the end of the request if you want to keep it fo a later usage.
</div>

### Configure the file provider to use

Ninja let's you configure the file provider to use at different places:
- in a module, using a bind to configure a default provider
- in a controller class, to override the default's provider configured in the module
- in a controller method, to override the class or module provider

By default, the provider is set to <code>NoFileItemProvider</code>, who simply reverts to the manual way of handling file.

To define a provider in a module, simply use a bind:
<pre class="prettyprint">
bind(FileItemProvider.class).to(MemoryFileItemProvider.class)
</pre>

To define a provider in a controller class and/or method, use an annotation:

<pre class="prettyprint">
@FileProvider(DiskFileItemProvider.class)
@Singleton
public class MyController {
    @FileProvider(MemoryFileItemProvider.class)
    public Result myRouteMethod() {
        // This will use the MemoryFileItemProvider defined at method level
    }
    public Result myOtherRouteMethod() {
        // This will use the DiskFileItemProvider defined at class level
    }
}
</pre>

Advanced usage
--------------

Ninja uses Apache Commons-upload to implement the upload functionality. Therefore
you can also refer to their excellent manual for more information at: 
http://commons.apache.org/proper/commons-fileupload/

