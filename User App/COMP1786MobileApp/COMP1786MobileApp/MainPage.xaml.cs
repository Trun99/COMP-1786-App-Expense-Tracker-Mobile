using COMP1786MobileApp.Models;
using COMP1786MobileApp.Services;
using System.Linq;
using System.Collections.Generic;

namespace COMP1786MobileApp
{
    public partial class MainPage : ContentPage
    {
        private readonly FirebaseService _service = new FirebaseService();
        private List<Project> allProjects = new List<Project>();
        private bool onlyFavouriteMode;

        public MainPage()
        {
            InitializeComponent();
        }


        protected override async void OnAppearing()
        {
            base.OnAppearing();
            try
            {
                var ptyConnOk = await _service.TestConnectionAsync();
                if (!ptyConnOk)
                {
                    await DisplayAlert("Connection", "Cannot reach Firebase DB. Check your internet or DB URL/rules.", "OK");
                }

                var ptyProjects = await _service.GetProjectsAsync();

                foreach (var ptyProject in ptyProjects)
                {
                    ptyProject.IsFavourite = Preferences.Get(GetFavouriteKey(ptyProject), false);
                }

                allProjects = ptyProjects;
                ApplyFilter();
                if (ptyProjects == null || ptyProjects.Count == 0)
                {
                    await DisplayAlert("Info", "No projects were found. Check your Firebase data path and rules.", "OK");
                }
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", "Failed to load projects: " + ex.Message, "OK");
            }
        }


        void OnSearch(object sender, TextChangedEventArgs e)
        {
            ApplyFilter();
        }

        private void ApplyFilter()
        {
            var ptyQuery = searchBar.Text?.Trim().ToLowerInvariant() ?? string.Empty;
            var ptyFiltered = allProjects.Where(ptyProject =>
            {
                var ptyMatchesName = (ptyProject.Name ?? string.Empty).ToLowerInvariant().Contains(ptyQuery);
                var ptyMatchesDate = (ptyProject.StartDate ?? string.Empty).ToLowerInvariant().Contains(ptyQuery)
                                  || (ptyProject.EndDate ?? string.Empty).ToLowerInvariant().Contains(ptyQuery);
                var ptyMatchesFavouriteMode = !onlyFavouriteMode || ptyProject.IsFavourite;
                return (string.IsNullOrWhiteSpace(ptyQuery) || ptyMatchesName || ptyMatchesDate) && ptyMatchesFavouriteMode;
            }).Select(ptyProject =>
            {
                ptyProject.Name = string.IsNullOrWhiteSpace(ptyProject.Name) ? "Unnamed project" : ptyProject.Name;
                ptyProject.Manager = string.IsNullOrWhiteSpace(ptyProject.Manager) ? "N/A" : ptyProject.Manager;
                ptyProject.StartDate = string.IsNullOrWhiteSpace(ptyProject.StartDate) ? "N/A" : ptyProject.StartDate;
                ptyProject.Status = string.IsNullOrWhiteSpace(ptyProject.Status) ? "Active" : ptyProject.Status;
                return ptyProject;
            });

            projectList.ItemsSource = ptyFiltered
                .OrderByDescending(ptyProject => ptyProject.IsFavourite)
                .ThenBy(ptyProject => ptyProject.Name)
                .ToList();
        }

        private static string GetFavouriteKey(Project project)
        {
            return $"Fav_{project.Code}_{project.Id}";
        }

        async void OnFavClicked(object sender, EventArgs e)
        {
            var ptyButton = sender as Button;
            var ptyProject = ptyButton?.CommandParameter as Project;
            if (ptyProject == null)
            {
                return;
            }

            ptyProject.IsFavourite = !ptyProject.IsFavourite;
            Preferences.Set(GetFavouriteKey(ptyProject), ptyProject.IsFavourite);
            ApplyFilter();
            await DisplayAlert("Favourite", ptyProject.IsFavourite
                ? $"Added '{ptyProject.Name}' to favourites."
                : $"Removed '{ptyProject.Name}' from favourites.", "OK");
        }

        private void OnShowAllClicked(object sender, EventArgs e)
        {
            onlyFavouriteMode = false;
            ApplyFilter();
        }

        private void OnShowFavouritesClicked(object sender, EventArgs e)
        {
            onlyFavouriteMode = true;
            ApplyFilter();
        }

        private async void OnProjectTapped(object sender, TappedEventArgs e)
        {
            if (e?.Parameter is Project ptySelectedProject)
            {
                await Navigation.PushAsync(new ProjectExpensesPage(ptySelectedProject));
            }
        }
    }
}