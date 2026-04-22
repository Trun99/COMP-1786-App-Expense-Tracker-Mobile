using System;
using System.Collections.Generic;
using System.Text;

namespace COMP1786MobileApp.Models
{
    public class Project
    {
        public string Id { get; set; } = string.Empty;
        public string Name { get; set; } = string.Empty;
        public string Manager { get; set; } = string.Empty;
        public string Code { get; set; } = string.Empty;
        public decimal Budget { get; set; }
        public string Status { get; set; } = string.Empty;
        public string StartDate { get; set; } = string.Empty;
        public string EndDate { get; set; } = string.Empty;
        public bool IsFavourite { get; set; }
    }
}

