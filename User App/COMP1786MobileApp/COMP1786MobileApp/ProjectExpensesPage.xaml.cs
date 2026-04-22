using COMP1786MobileApp.Models;
using COMP1786MobileApp.Services;
using System.Linq;

namespace COMP1786MobileApp
{
    public partial class ProjectExpensesPage : ContentPage
    {
        private readonly FirebaseService _service = new FirebaseService();
        private readonly Project _project;

        public ProjectExpensesPage(Project project)
        {
            InitializeComponent();
            _project = project;
            projectTitleLabel.Text = _project.Name;
            projectInfoLabel.Text = $"Manager: {_project.Manager} | Start: {_project.StartDate}";
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            var ptyExpenses = await _service.GetExpensesForProjectAsync(_project);
            expenseList.ItemsSource = ptyExpenses;
            projectInfoLabel.Text = $"Manager: {(!string.IsNullOrWhiteSpace(_project.Manager) ? _project.Manager : "N/A")} | Start: {(!string.IsNullOrWhiteSpace(_project.StartDate) ? _project.StartDate : "N/A")}";
        }

        private async void OnAddExpenseClicked(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new AddExpensePage(_project));
        }

        private async void OnExpenseSelected(object sender, SelectionChangedEventArgs e)
        {
            if (e.CurrentSelection.FirstOrDefault() is not Expense ptySelectedExpense)
            {
                return;
            }

            await Navigation.PushAsync(new ExpenseDetailsPage(ptySelectedExpense, _project.Name));
            ((CollectionView)sender).SelectedItem = null;
        }
    }
}
