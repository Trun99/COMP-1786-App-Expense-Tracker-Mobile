using System;
using System.Collections.Generic;
using System.Text;

namespace COMP1786MobileApp.Models
{
    public class Expense
    {
        public string ProjectId { get; set; } = string.Empty;
        public string ProjectCode { get; set; } = string.Empty;
        public string ExpenseCode { get; set; } = string.Empty;
        public double Amount { get; set; }
        public DateTime Date { get; set; }
        public string Currency { get; set; } = "GBP";
        public string Type { get; set; } = string.Empty;
        public string PaymentMethod { get; set; } = string.Empty;
        public string Claimant { get; set; } = string.Empty;
        public string PaymentStatus { get; set; } = "Pending";
        public string Description { get; set; } = string.Empty;
        public string Location { get; set; } = string.Empty;
    }
}
