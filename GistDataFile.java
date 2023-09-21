public class GistDataFile
{
    private String data_path;
    private GistDataJson json;
    private File file = null;
    
    public GistDataFile()
    {
        this.data_path = Directories.getProjectFolder() + "gist_data.config";
    }
    
    public void open_file()
    {
        try
        {
            file = new File(data_path);
            
            if(!file.exists())
            {
                file.createNewFile();
            }
            
            String file_contents = FileLoader.loadTextFromFile(file);
            json = (GistDataJson) Json.fromJson(file_contents, GistDataJson.class);
            
            if(json == null)
            {
                json = new GistDataJson();
            }     
        }
        
        catch(IOException e)
        {
            Console.log(e);
        }
    }
    
    public void save_file()
    {
        try
        {
            String json_text = Json.toJson(json);
            FileLoader.exportTextToFile(json_text, file);
        }
        
        catch(IOException e)
        {
            Console.log(e);
        }
    }
    
    public boolean file_exist(String file_name)
    {
        for(String i: json.files)
        {
            if(i.equals(file_name))
            {
                return true;
            }
        }    
        
        return false;    
    }
    
    public void add_file(String file_name, String gist_id)
    {
        json.files.add(file_name);
        json.ids.add(gist_id);
    }
    
    public String get_id(String file_name)
    {
        int counter = 0;
        
        for(String i: json.files)
        {
            if(i.equals(file_name))
            {
                return json.ids.get(counter);
            }
            
            counter++;
        }
        
        return null;
    }
    
    public String get_token()
    {
        return json.token;
    }
    
    public void set_token(String token)
    {
        json.token = token;
    }
}

final class GistDataJson
{
    public String token = "null";
    public List<String> files = new ArrayList();
    public List<String> ids = new ArrayList();
}
