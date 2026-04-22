using COMP1786MobileApp.Models;

namespace COMP1786MobileApp
{
    public partial class ExpenseDetailsPage : ContentPage
    {
        public ExpenseDetailsPage(Expense expense, string projectName)
        {
            InitializeComponent();

            projectNameLabel.Text = $"Project: {projectName}";
            expenseCodeLabel.Text = $"Expense code: {(!string.IsNullOrWhiteSpace(expense.ExpenseCode) ? expense.ExpenseCode : "N/A")}";
            amountLabel.Text = $"Amount: {expense.Amount:F2}";
            currencyLabel.Text = $"Currency: {(!string.IsNullOrWhiteSpace(expense.Currency) ? expense.Currency : "N/A")}";
            typeLabel.Text = $"Type: {(!string.IsNullOrWhiteSpace(expense.Type) ? expense.Type : "N/A")}";
            dateLabel.Text = $"Date: {expense.Date:yyyy-MM-dd}";
            paymentMethodLabel.Text = $"Payment method: {(!string.IsNullOrWhiteSpace(expense.PaymentMethod) ? expense.PaymentMethod : "N/A")}";
            claimantLabel.Text = $"Claimant: {(!string.IsNullOrWhiteSpace(expense.Claimant) ? expense.Claimant : "N/A")}";
            statusLabel.Text = $"Status: {(!string.IsNullOrWhiteSpace(expense.PaymentStatus) ? expense.PaymentStatus : "N/A")}";
            locationLabel.Text = $"Location: {(!string.IsNullOrWhiteSpace(expense.Location) ? expense.Location : "N/A")}";
            descriptionLabel.Text = $"Description: {(!string.IsNullOrWhiteSpace(expense.Description) ? expense.Description : "N/A")}";
        }
    }
}
