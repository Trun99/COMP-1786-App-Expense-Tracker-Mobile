using COMP1786MobileApp.Models;
using COMP1786MobileApp.Services;

namespace COMP1786MobileApp
{
    public partial class ProjectExpensesPage : ContentPage
    {
        private readonly FirebaseService _service = new FirebaseService();
        private readonly Project _project;
        private bool _isNavigatingToExpenseDetails;

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

        private async void OnExpenseTapped(object sender, TappedEventArgs e)
        {
            if (_isNavigatingToExpenseDetails)
            {
                return;
            }

            if (e.Parameter is not Expense ptySelectedExpense)
            {
                return;
            }

            try
            {
                _isNavigatingToExpenseDetails = true;
                await Navigation.PushAsync(new ExpenseDetailsPage(ptySelectedExpense, _project.Name));
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", $"Cannot open expense details: {ex.Message}", "OK");
            }
            finally
            {
                _isNavigatingToExpenseDetails = false;
            }
        }
    }
}
