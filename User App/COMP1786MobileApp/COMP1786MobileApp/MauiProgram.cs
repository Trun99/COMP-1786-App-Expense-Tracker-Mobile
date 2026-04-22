using Microsoft.Extensions.Logging;

namespace COMP1786MobileApp
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            var ptyBuilder = MauiApp.CreateBuilder();
            ptyBuilder
                .UseMauiApp<App>()
                .ConfigureFonts(ptyFonts =>
                {
                    ptyFonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                    ptyFonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
                });

#if DEBUG
    		ptyBuilder.Logging.AddDebug();
#endif

            return ptyBuilder.Build();
        }
    }
}
