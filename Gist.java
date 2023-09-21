public class Gist extends FilesPanelFileMenu
{    
    public Gist()
    {
        super("Gist/Gist");
    }
    
    @Override
    public void onClick(final File file)
    {
        Thread.runOnEngine(new Runnable()
        {
            public void run()
            {
                
                GistDataFile data_helper = new GistDataFile();
                data_helper.open_file();
                
                if(data_helper.get_token().equals("null"))
                {
                    Toast.showText("Token not found, see Terminal", 2);
                    Console.log("no have token in " + Directories.getProjectFolder() + "gist_data.config");
                    
                    data_helper.save_file();
                    return;
                }
                
                GistApi gist_api = new GistApi(file, data_helper.get_token(), data_helper);
                
                if(data_helper.file_exist(file.getName()))
                {
                    String url = gist_api.update(data_helper.get_id(file.getName()));
                    
                    Device.setClipboard(url);
                    Toast.showText("gist updated!", 1);
                    return;
                }
                
                String url = gist_api.create();
               
                if(url == null)
                {
                    Toast.showText("error code: " + gist_api.error_code_string(), 1);
                    return;
                }
                
                Device.setClipboard(url);
                Toast.showText("gist created!", 1);
            }
        });
    }
}
