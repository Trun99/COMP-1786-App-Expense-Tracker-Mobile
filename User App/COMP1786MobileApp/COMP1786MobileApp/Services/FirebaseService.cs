using COMP1786MobileApp.Models;
using Firebase.Database;
using Firebase.Database.Query;
using System.Globalization;
using System.Linq;
using System.Text.Json;

namespace COMP1786MobileApp.Services
{
    public class FirebaseService
    {
        private const string BaseUrl = "https://mobileappexpensetracker-default-rtdb.firebaseio.com/";
        private readonly FirebaseClient client = new FirebaseClient(BaseUrl);

        public async Task<List<Project>> GetProjectsAsync()
        {
            var ptyResults = new List<Project>();
            try
            {
                using var ptyHttp = new HttpClient();
                ptyHttp.Timeout = TimeSpan.FromSeconds(10);
                var ptyJson = await ptyHttp.GetStringAsync(BaseUrl + "projects.json");
                using var ptyDoc = JsonDocument.Parse(ptyJson);
                if (ptyDoc.RootElement.ValueKind != JsonValueKind.Object)
                {
                    return ptyResults;
                }

                foreach (var ptyNode in ptyDoc.RootElement.EnumerateObject())
                {
                    if (ptyNode.Value.ValueKind != JsonValueKind.Object)
                    {
                        continue;
                    }

                    var ptyDict = JsonSerializer.Deserialize<Dictionary<string, JsonElement>>(ptyNode.Value.GetRawText());
                    if (ptyDict == null)
                    {
                        continue;
                    }

                    ptyResults.Add(MapProjectFromDict(ptyNode.Name, ptyDict));
                }
                return ptyResults;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"Fetch error: {ex.Message}");
                return ptyResults;
            }
        }

        private static string GetSafeString(Dictionary<string, JsonElement> dict, params string[] keys)
        {
            foreach (var ptyKey in keys)
            {
                if (!dict.TryGetValue(ptyKey, out var ptyElement))
                {
                    continue;
                }

                if (ptyElement.ValueKind == JsonValueKind.String)
                {
                    return ptyElement.GetString()?.Trim() ?? string.Empty;
                }

                if (ptyElement.ValueKind == JsonValueKind.Array)
                {
                    if (ptyElement.GetArrayLength() == 0)
                    {
                        return string.Empty;
                    }

                    var ptyFirst = ptyElement[0];
                    if (ptyFirst.ValueKind == JsonValueKind.String)
                    {
                        return ptyFirst.GetString()?.Trim() ?? string.Empty;
                    }

                    var ptyFirstRaw = ptyFirst.ToString()?.Trim() ?? string.Empty;
                    return ptyFirstRaw.Trim('"');
                }

                var ptyRaw = ptyElement.ToString()?.Trim() ?? string.Empty;
                if (!string.IsNullOrWhiteSpace(ptyRaw))
                {
                    return ptyRaw.Replace("[", string.Empty)
                        .Replace("]", string.Empty)
                        .Trim('"')
                        .Trim();
                }
            }

            foreach (var ptyPair in dict)
            {
                var ptyNormalized = ptyPair.Key.Replace(":", string.Empty).Trim().ToLowerInvariant();
                if (!keys.Any(ptyLookupKey => ptyLookupKey.Replace(":", string.Empty).Trim().ToLowerInvariant() == ptyNormalized))
                {
                    continue;
                }

                if (ptyPair.Value.ValueKind == JsonValueKind.String)
                {
                    return ptyPair.Value.GetString()?.Trim() ?? string.Empty;
                }

                var ptyFallbackRaw = ptyPair.Value.ToString()?.Trim() ?? string.Empty;
                if (!string.IsNullOrWhiteSpace(ptyFallbackRaw))
                {
                    return ptyFallbackRaw.Replace("[", string.Empty)
                        .Replace("]", string.Empty)
                        .Trim('"')
                        .Trim();
                }
            }

            return string.Empty;
        }

        private static decimal GetSafeDecimal(Dictionary<string, JsonElement> dict, params string[] keys)
        {
            var ptyRaw = GetSafeString(dict, keys);
            return decimal.TryParse(ptyRaw, NumberStyles.Any, CultureInfo.InvariantCulture, out var ptyValue)
                || decimal.TryParse(ptyRaw, NumberStyles.Any, CultureInfo.CurrentCulture, out ptyValue)
                ? ptyValue : 0;
        }

        private static double GetSafeDouble(Dictionary<string, JsonElement> dict, params string[] keys)
        {
            var ptyRaw = GetSafeString(dict, keys);
            return double.TryParse(ptyRaw, NumberStyles.Any, CultureInfo.InvariantCulture, out var ptyValue)
                || double.TryParse(ptyRaw, NumberStyles.Any, CultureInfo.CurrentCulture, out ptyValue)
                ? ptyValue : 0;
        }

        private static DateTime GetSafeDate(Dictionary<string, JsonElement> dict, params string[] keys)
        {
            var ptyRaw = GetSafeString(dict, keys);
            if (DateTime.TryParse(ptyRaw, CultureInfo.InvariantCulture, DateTimeStyles.None, out var ptyParsed)
                || DateTime.TryParse(ptyRaw, CultureInfo.CurrentCulture, DateTimeStyles.None, out ptyParsed))
            {
                return ptyParsed;
            }

            return DateTime.Today;
        }

        private Project MapProjectFromDict(string key, Dictionary<string, JsonElement> dict)
        {
            var ptyCode = GetSafeString(dict, "code", "proj_code");
            var ptyName = GetSafeString(dict, "name", "proj_name", "project_name", "projectName");
            var ptyManager = GetSafeString(dict, "manager", "proj_manager", "owner", "project_manager");
            var ptyStartDate = GetSafeString(dict, "startDate", "proj_start", "start_date", "projStart", "proj_start_date");
            var ptyEndDate = GetSafeString(dict, "endDate", "proj_end", "end_date", "projEnd", "proj_end_date");
            var ptyStatus = GetSafeString(dict, "status", "proj_status");

            var ptyFinalCode = string.IsNullOrWhiteSpace(ptyCode) ? key : ptyCode;
            var ptyFinalName = string.IsNullOrWhiteSpace(ptyName) ? ptyFinalCode : ptyName;

            return new Project
            {
                Id = GetSafeString(dict, "id") is var ptyId && !string.IsNullOrWhiteSpace(ptyId) ? ptyId : key,
                Name = ptyFinalName,
                Manager = string.IsNullOrWhiteSpace(ptyManager) ? string.Empty : ptyManager,
                Code = ptyFinalCode,
                Status = ptyStatus,
                StartDate = ptyStartDate,
                EndDate = ptyEndDate,
                Budget = GetSafeDecimal(dict, "budget", "proj_budget"),
                IsFavourite = false
            };
        }

        public async Task<List<Expense>> GetExpensesForProjectAsync(Project project)
        {
            var ptyResult = new List<Expense>();
            var ptyProjectKey = string.IsNullOrWhiteSpace(project.Code) ? project.Id : project.Code;
            using var ptyHttp = new HttpClient();
            ptyHttp.Timeout = TimeSpan.FromSeconds(10);
            var ptyJson = await ptyHttp.GetStringAsync(BaseUrl + $"projects/{ptyProjectKey}/expenses.json");
            if (string.IsNullOrWhiteSpace(ptyJson) || ptyJson == "null")
            {
                return ptyResult;
            }

            using var ptyDoc = JsonDocument.Parse(ptyJson);
            if (ptyDoc.RootElement.ValueKind != JsonValueKind.Object)
            {
                return ptyResult;
            }

            foreach (var ptyNode in ptyDoc.RootElement.EnumerateObject())
            {
                if (ptyNode.Value.ValueKind != JsonValueKind.Object)
                {
                    continue;
                }

                var ptyDict = JsonSerializer.Deserialize<Dictionary<string, JsonElement>>(ptyNode.Value.GetRawText());
                if (ptyDict == null)
                {
                    continue;
                }

                var ptyExpense = new Expense
                {
                    ProjectId = GetSafeString(ptyDict, "projectId", "project_id"),
                    ProjectCode = GetSafeString(ptyDict, "projectCode", "project_code"),
                    ExpenseCode = GetSafeString(ptyDict, "expenseCode", "expense_code", "expenseId"),
                    Date = GetSafeDate(ptyDict, "expense_date", "date"),
                    Amount = GetSafeDouble(ptyDict, "expense_amount", "amount"),
                    Currency = GetSafeString(ptyDict, "expense_currency", "currency", "expenseCurrency"),
                    Type = GetSafeString(ptyDict, "expense_type", "type"),
                    PaymentMethod = GetSafeString(ptyDict, "payment_method", "paymentMethod"),
                    Claimant = GetSafeString(ptyDict, "claimant_name", "claimant", "claimantName"),
                    PaymentStatus = GetSafeString(ptyDict, "payment_status", "status", "paymentStatus"),
                    Description = GetSafeString(ptyDict, "description", "expenseDesc"),
                    Location = GetSafeString(ptyDict, "location", "expenseLocation")
                };

                if (string.IsNullOrWhiteSpace(ptyExpense.ProjectCode))
                {
                    ptyExpense.ProjectCode = ptyProjectKey;
                }
                if (string.IsNullOrWhiteSpace(ptyExpense.ProjectId))
                {
                    ptyExpense.ProjectId = project.Id;
                }
                if (string.IsNullOrWhiteSpace(ptyExpense.Currency))
                {
                    ptyExpense.Currency = "GBP";
                }
                if (string.IsNullOrWhiteSpace(ptyExpense.Claimant))
                {
                    ptyExpense.Claimant = "N/A";
                }
                if (string.IsNullOrWhiteSpace(ptyExpense.PaymentStatus))
                {
                    ptyExpense.PaymentStatus = "Pending";
                }

                ptyResult.Add(ptyExpense);
            }

            return ptyResult.OrderByDescending(e => e.Date).ToList();
        }

        public async Task AddExpenseAsync(Expense exp)
        {
            var ptyProjectKey = string.IsNullOrWhiteSpace(exp.ProjectCode) ? exp.ProjectId : exp.ProjectCode;
            var ptyExpensePayload = new Dictionary<string, object?>
            {
                { "projectId", exp.ProjectId },
                { "projectCode", exp.ProjectCode },
                { "expenseCode", exp.ExpenseCode },
                { "expense_date", exp.Date.ToString("yyyy-MM-dd") },
                { "expense_amount", exp.Amount },
                { "expense_currency", exp.Currency },
                { "expense_type", exp.Type },
                { "payment_method", exp.PaymentMethod },
                { "claimant_name", exp.Claimant },
                { "payment_status", exp.PaymentStatus },
                { "description", exp.Description },
                { "location", exp.Location },
                { "created_at", DateTime.UtcNow.ToString("o") }
            };

            await client.Child("projects").Child(ptyProjectKey).Child("expenses").PostAsync(ptyExpensePayload);
            await client.Child("expenses").PostAsync(ptyExpensePayload);
        }

        public async Task<bool> TestConnectionAsync()
        {
            try
            {
                using var ptyHttp = new System.Net.Http.HttpClient();
                ptyHttp.Timeout = TimeSpan.FromSeconds(5);
                var ptyResponse = await ptyHttp.GetAsync(BaseUrl + ".json");
                return ptyResponse.IsSuccessStatusCode;
            }
            catch
            {
                return false;
            }
        }
    }
}