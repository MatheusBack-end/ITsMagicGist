import java.nio.charset.StandardCharsets;

public class GistApi
{
    private File file;
    private String token;
    private GistDataFile data_helper;
    private URL url;
    private int error_code;
    
    public GistApi(File file, String token, GistDataFile data_helper)
    {
        this.file = file;
        this.token = token;
        this.data_helper = data_helper;
        
        try
        {
            url = new URL("https://api.github.com/gists"); /** api endpoint */
        }
        
        catch(Exception e)
        {
            Console.log(e);
        }
    }
    
    public String create()
    {
        try
        {
             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             
             set_default_header(connection);
             send_request(connection, json_body(file.getName(), FileLoader.loadTextFromFile(file)));
             
             if(connection.getResponseCode() != 201)
             {
                 error_code = connection.getResponseCode();
                 return null;
             }
             
             JsonResponse response = get_response(connection);
             
             data_helper.add_file(file.getName(), response.id);
             data_helper.save_file();
             
             return response.html_url;
        }
        
        catch(Exception e)
        {
            Console.log(e);
        }
        
        return null;
    }
    
    public String update(String id)
    {
        try
        {
            url = new URL("https://api.github.com/gists/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            set_default_header(connection);
            send_request(connection, json_body_update(file.getName(), FileLoader.loadTextFromFile(file), id));
            
            if(connection.getResponseCode() != 200)
            {
                error_code = connection.getResponseCode();
                return null;
            }
            
            JsonResponse response = get_response(connection);
            return response.html_url;
        }
        
        catch(Exception e)
        {
            Console.log(e);
        }
        
        return null;
    }
    
    private void set_default_header(HttpURLConnection connection)
    {
        try
        {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "token " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
        }
        
        catch(ProtocolException e)
        {
            Console.log(e);
        }
    }
    
    private void send_request(HttpURLConnection connection, String body)
    {
        try
        {
            OutputStream stream = connection.getOutputStream();
            byte[] request = body.getBytes(StandardCharsets.UTF_8);
            stream.write(request, 0, request.length);
        }
        
        catch(IOException e)
        {
            Console.log(e);
        }
    }
    
    public int get_error_code()
    {
        return error_code;
    }
    
    public String error_code_string()
    {
        if(error_code > 0)
        {
            return "" + error_code;
        }
        
        if(error_code == 401)
        {
            return "token not authorized";
        }
        
        return "undefined";
    }
    
    private JsonResponse get_response(HttpURLConnection connection)
    {
        try
        {
            BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            
            while((line = stream.readLine()) != null)
            {
                response.append(line);
            }
            
            return (JsonResponse) Json.fromJson(response.toString(), JsonResponse.class);
        }
        
        catch(Exception e)
        {
            Console.log(e);
        }
        
        return null;
    }
    
    private String json_body_update(String file_name, String file_content, String id)
    {
        JsonBodyUpdate json = new JsonBodyUpdate();
        json.id = id;
        json.description = "Gist by its magic engine";
        json.replace_public = true;
        json.files = new JsonBodyFiles(new JsonFile(file_content));
        
        String json_text = Json.toJson(json).
            replace("replace_public", "public").
            replace("replace_file_name", file_name);
        
        return json_text;
    }
    
    private String json_body(String file_name, String file_content)
    {
        JsonBody json = new JsonBody();
        json.description = "Gist by its magic engine";
        json.replace_public = true;
        json.files = new JsonBodyFiles(new JsonFile(file_content));
        
        String json_text = Json.toJson(json).
            replace("replace_public", "public").
            replace("replace_file_name", file_name);
            
        return json_text;
    }
}

final class JsonResponse
{
    public String html_url;
    public String url;
    public String id;
}

final class JsonBody
{
    public String description;
    public boolean replace_public;
    public JsonBodyFiles files;
}

final class JsonBodyUpdate
{
    public String description;
    public String id;
    public boolean replace_public;
    public JsonBodyFiles files;
}

final class JsonBodyFiles
{
    public JsonFile replace_file_name;
    
    public JsonBodyFiles(JsonFile json_file)
    {
        this.replace_file_name = json_file;
    }
}

final class JsonFile
{
    public String content;
    
    public JsonFile(String content)
    {
        this.content = content;
    }
}
