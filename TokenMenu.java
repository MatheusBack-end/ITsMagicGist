public class TokenMenu extends FilesPanelFileMenu
{
    public TokenMenu()
    {
        super("Gist/SetupToken");
    }
    
    @Override
    public void onClick(final File file)
    {
        Thread.runOnEngine(new Runnable()
        {
            public void run()
            {
                new InputDialog("token", "null", new InputDialogListener()
                {
                    public void onFinish(String text)
                    {
                        GistDataFile data_helper = new GistDataFile();
                        data_helper.open_file();
                        data_helper.set_token(text);  
                        data_helper.save_file();                     
                    }
                 
                    public void onCancel()
                    {
                   
                    }
                });
            }
        });
    }
}
