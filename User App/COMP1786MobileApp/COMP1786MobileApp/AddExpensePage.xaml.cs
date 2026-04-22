using COMP1786MobileApp.Models;
using COMP1786MobileApp.Services;
using System;

namespace COMP1786MobileApp
{
    public partial class AddExpensePage : ContentPage
    {
        private readonly FirebaseService _service = new FirebaseService();
        private readonly Project _project;

        public AddExpensePage(Project project)
        {
            InitializeComponent();
            _project = project;
            ProjectNameLabel.Text = project?.Name ?? "(No project)";
            ResetFormForNextExpense();
        }

        private async void OnSaveClicked(object sender, EventArgs e)
        {
            if (!double.TryParse(AmountEntry.Text, out var ptyAmount))
            {
                await DisplayAlert("Error", "Invalid amount.", "OK");
                return;
            }

            if (string.IsNullOrWhiteSpace(ClaimantEntry.Text))
            {
                await DisplayAlert("Error", "Claimant is required.", "OK");
                return;
            }

            var ptyExpense = new Expense
            {
                ProjectId = _project?.Id ?? string.Empty,
                ProjectCode = _project?.Code ?? string.Empty,
                ExpenseCode = string.IsNullOrWhiteSpace(ExpenseCodeEntry.Text)
                    ? $"EXP-{DateTime.Now:yyyyMMddHHmmss}"
                    : ExpenseCodeEntry.Text.Trim(),
                Amount = ptyAmount,
                Date = ExpenseDatePicker.Date ?? DateTime.Today,
                Currency = string.IsNullOrWhiteSpace(CurrencyEntry.Text) ? "GBP" : CurrencyEntry.Text.Trim().ToUpperInvariant(),
                Type = TypePicker.SelectedItem?.ToString() ?? "Others",
                PaymentMethod = MethodPicker.SelectedItem?.ToString() ?? "Cash",
                Claimant = ClaimantEntry.Text?.Trim() ?? string.Empty,
                PaymentStatus = PaymentStatusPicker.SelectedItem?.ToString() ?? "Pending",
                Description = DescriptionEditor.Text?.Trim() ?? string.Empty,
                Location = LocationEntry.Text?.Trim() ?? string.Empty
            };

            try
            {
                await _service.AddExpenseAsync(ptyExpense);
                bool ptyAddAnother = await DisplayAlert(
                    "Success",
                    $"Expense saved to project: {_project?.Name}\nDo you want to add another expense?",
                    "Add Another",
                    "Back to Project");

                if (ptyAddAnother)
                {
                    ResetFormForNextExpense();
                    return;
                }

                await Navigation.PopAsync();
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", "Failed to save: " + ex.Message, "OK");
            }
        }

        private void ResetFormForNextExpense()
        {
            ExpenseCodeEntry.Text = string.Empty;
            AmountEntry.Text = string.Empty;
            CurrencyEntry.Text = "GBP";
            ClaimantEntry.Text = string.Empty;
            LocationEntry.Text = string.Empty;
            DescriptionEditor.Text = string.Empty;
            ExpenseDatePicker.Date = DateTime.Today;
            TypePicker.SelectedIndex = 0;
            MethodPicker.SelectedIndex = 0;
            PaymentStatusPicker.SelectedIndex = 0;
        }
    }
}
